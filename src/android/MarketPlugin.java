package marketplugin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MarketPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) {
        try {
            if ("open".equals(action)) {
                String appId = args.getString(0);
                open(appId, callbackContext);
                return true;
            } else if ("search".equals(action)) {
                String query = args.getString(0);
                search(query, callbackContext);
                return true;
            } else {
                callbackContext.error("Invalid action: " + action);
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("Exception: " + e.getMessage());
            return false;
        }
    }

    private void open(String appId, CallbackContext callbackContext) {
        if (appId == null || appId.trim().isEmpty()) {
            callbackContext.error("Invalid app ID");
            return;
        }

        try {
            Uri uri = Uri.parse("market://details?id=" + appId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                cordova.getActivity().startActivity(intent);
                callbackContext.success();
            } else {
                callbackContext.error("Play Store not found on device");
            }
        } catch (Exception e) {
            callbackContext.error("Error launching market: " + e.getMessage());
        }
    }

    private void search(String query, CallbackContext callbackContext) {
        if (query == null || query.trim().isEmpty()) {
            callbackContext.error("Invalid search query");
            return;
        }

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            Uri uri = Uri.parse("market://search?q=" + encodedQuery);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                cordova.getActivity().startActivity(intent);
                callbackContext.success();
            } else {
                callbackContext.error("Play Store not found on device");
            }
        } catch (UnsupportedEncodingException e) {
            callbackContext.error("Encoding error: " + e.getMessage());
        }
    }
}
