package marketplugin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import org.apache.cordova.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarketPlugin extends CordovaPlugin {

    private static final String TAG = "MarketPlugin";

    /** requestReview() results (the JS promise's value / rejection). */
    static final String REVIEW_LAUNCHED = "launched";
    static final String REVIEW_STORE_FALLBACK = "store_fallback";
    static final String REVIEW_NOT_RESUMED = "activity_not_resumed";
    static final String REVIEW_NO_PACKAGE = "no_package";
    static final String REVIEW_REQUEST_FAILED = "request_failed";
    static final String REVIEW_LAUNCH_FAILED = "launch_failed";

    /**
     * Whether the host activity is in the foreground. The plugin loads at startup (onload=true), so
     * it sees every pause/resume. The Play review sheet is only launched while this is true: launching
     * it from a paused / finishing activity is what lets Play's shim activities start without a
     * foreground task.
     */
    private volatile boolean resumed = true;

    @Override
    public void onResume(boolean multitasking) {
        resumed = true;
    }

    @Override
    public void onPause(boolean multitasking) {
        resumed = false;
    }

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) {
        try {
            if ("open".equals(action)) {
                open(args.isNull(0) ? null : args.getString(0), callbackContext);
                return true;
            } else if ("search".equals(action)) {
                search(args.isNull(0) ? null : args.getString(0), callbackContext);
                return true;
            } else if ("requestReview".equals(action)) {
                requestReview(callbackContext);
                return true;
            } else {
                callbackContext.error("Invalid action: " + action);
                return false;
            }
        } catch (Exception e) {
            // Answered here, so report the action as handled (false would add a second "invalid action" error).
            callbackContext.error("Exception: " + e.getMessage());
            return true;
        }
    }

    private void open(String appId, CallbackContext callbackContext) {
        if (appId == null || appId.trim().isEmpty() || "null".equals(appId)) {
            callbackContext.error("Invalid app ID");
            return;
        }
        String error = startFirst(cordova.getActivity(),
                "market://details?id=" + appId,
                "https://play.google.com/store/apps/details?id=" + appId);
        if (error == null) {
            callbackContext.success();
        } else {
            callbackContext.error(error);
        }
    }

    private void search(String query, CallbackContext callbackContext) {
        if (query == null || query.trim().isEmpty()) {
            callbackContext.error("Invalid search query");
            return;
        }

        final String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            callbackContext.error("Encoding error: " + e.getMessage());
            return;
        }
        String error = startFirst(cordova.getActivity(),
                "market://search?q=" + encodedQuery + "&c=apps",
                "https://play.google.com/store/search?q=" + encodedQuery + "&c=apps");
        if (error == null) {
            callbackContext.success();
        } else {
            callbackContext.error(error);
        }
    }

    /**
     * Starts an ACTION_VIEW for the first target some app can handle: market:// (the Play Store),
     * then https:// (a browser, when the Play Store is missing or disabled). Deliberately no
     * resolveActivity() pre-check: under Android 11+ package visibility it returns null unless the
     * host manifest declares a matching <queries>, which would falsely report "no store".
     * Returns null on success, or the error message.
     */
    static String startFirst(Activity activity, String... targets) {
        if (activity == null) {
            return "No activity";
        }
        for (String target : targets) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return null;
            } catch (ActivityNotFoundException e) {
                // try the next target
            } catch (RuntimeException e) {
                Log.w(TAG, "could not open " + target, e);
                return "Error launching market: " + e.getMessage();
            }
        }
        return "Play Store not found on device";
    }

    /**
     * Play In-App Review. Requests a ReviewInfo, then launches the sheet on the UI thread ONLY while
     * the activity is resumed and not finishing / destroyed. An IllegalStateException from the Play
     * library falls back to the store listing of THIS package (never a null package). Every path
     * answers the callback exactly once. Play decides whether the sheet actually shows (quota), and
     * reports nothing about what the user did.
     */
    private void requestReview(final CallbackContext callbackContext) {
        final Once once = new Once(callbackContext);
        final Activity activity = cordova.getActivity();
        if (!isUsable(activity)) {
            once.error(REVIEW_NOT_RESUMED);
            return;
        }
        final String packageName = activity.getPackageName();
        if (packageName == null || packageName.isEmpty()) {
            once.error(REVIEW_NO_PACKAGE);
            return;
        }

        final ReviewManager manager;
        final Task<ReviewInfo> request;
        try {
            manager = ReviewManagerFactory.create(activity.getApplicationContext());
            request = manager.requestReviewFlow();
        } catch (RuntimeException e) {
            Log.w(TAG, "requestReviewFlow failed", e);
            once.error(REVIEW_REQUEST_FAILED + ": " + e.getMessage());
            return;
        }

        request.addOnCompleteListener(task -> {
            ReviewInfo reviewInfo = null;
            Exception failure = null;
            try {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                } else {
                    failure = task.getException();
                }
            } catch (RuntimeException e) {
                failure = e;
            }
            if (reviewInfo == null) {
                once.error(REVIEW_REQUEST_FAILED + (failure != null ? ": " + failure.getMessage() : ""));
                return;
            }
            final ReviewInfo info = reviewInfo;
            try {
                activity.runOnUiThread(() -> launchReview(manager, info, activity, packageName, once));
            } catch (RuntimeException e) {
                once.error(REVIEW_LAUNCH_FAILED + ": " + e.getMessage());
            }
        });
    }

    /** UI thread. Launches the review sheet only on a resumed, live activity. */
    void launchReview(ReviewManager manager, ReviewInfo info, Activity activity, String packageName, Once once) {
        if (!isUsable(activity) || activity != cordova.getActivity()) {
            once.error(REVIEW_NOT_RESUMED);
            return;
        }
        final Task<Void> flow;
        try {
            flow = manager.launchReviewFlow(activity, info);
        } catch (IllegalStateException e) {
            Log.w(TAG, "launchReviewFlow threw, opening the store listing instead", e);
            openStoreListing(activity, packageName, once);
            return;
        } catch (RuntimeException e) {
            Log.w(TAG, "launchReviewFlow failed", e);
            once.error(REVIEW_LAUNCH_FAILED + ": " + e.getMessage());
            return;
        }
        flow.addOnCompleteListener(done -> {
            Exception failure = null;
            try {
                failure = done.isSuccessful() ? null : done.getException();
            } catch (RuntimeException e) {
                failure = e;
            }
            if (failure instanceof IllegalStateException) {
                openStoreListing(activity, packageName, once);
            } else if (failure != null) {
                once.error(REVIEW_LAUNCH_FAILED + ": " + failure.getMessage());
            } else {
                once.success(REVIEW_LAUNCHED);
            }
        });
    }

    /** The fallback: this app's own Play listing (market://, then https://). */
    void openStoreListing(Activity activity, String packageName, Once once) {
        if (packageName == null || packageName.isEmpty()) {
            once.error(REVIEW_NO_PACKAGE);
            return;
        }
        if (!isUsable(activity)) {
            once.error(REVIEW_NOT_RESUMED);
            return;
        }
        String error = startFirst(activity,
                "market://details?id=" + packageName,
                "https://play.google.com/store/apps/details?id=" + packageName);
        if (error == null) {
            once.success(REVIEW_STORE_FALLBACK);
        } else {
            once.error(REVIEW_LAUNCH_FAILED + ": " + error);
        }
    }

    boolean isUsable(Activity activity) {
        return activity != null && resumed && !activity.isFinishing() && !activity.isDestroyed();
    }

    /** Answers a Cordova callback at most once, whichever async path gets there first. */
    static final class Once {
        private final CallbackContext callbackContext;
        private final AtomicBoolean answered = new AtomicBoolean(false);

        Once(CallbackContext callbackContext) {
            this.callbackContext = callbackContext;
        }

        void success(String message) {
            if (answered.compareAndSet(false, true)) {
                callbackContext.success(message);
            }
        }

        void error(String message) {
            if (answered.compareAndSet(false, true)) {
                callbackContext.error(message);
            }
        }
    }
}
