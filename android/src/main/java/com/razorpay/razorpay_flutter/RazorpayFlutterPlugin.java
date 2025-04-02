package com.razorpay.razorpay_flutter;

import android.util.Log;
import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RazorpayFlutterPlugin migrated to Flutter v2 Android embedding.
 */
public class RazorpayFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private MethodChannel channel;
    private RazorpayDelegate razorpayDelegate;
    private ActivityPluginBinding pluginBinding;
    private static final String CHANNEL_NAME = "razorpay_flutter";
    Map<String, Object> _arguments;
    String customerMobile ;
    String color;


    public RazorpayFlutterPlugin() {

    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(MethodCall call, Result result) {


        switch (call.method) {

            case "open":
                if (razorpayDelegate != null) {
                    razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                } else {
                    result.error("NO_ACTIVITY", "Activity is not attached", null);
                }
                break;

            case "resync":
                if (razorpayDelegate != null) {
                    razorpayDelegate.resync(result);
                } else {
                    result.error("NO_ACTIVITY", "Activity is not attached", null);
                }
                break;

            case "setKeyID":
                String key = call.arguments().toString();
                razorpayDelegate.setKeyID(key,  result);
                break;
            case "linkNewUpiAccount":
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.linkNewUpiAccount(customerMobile, color , result);
                break;

            case "manageUpiAccounts":
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.manageUpiAccounts(customerMobile, color , result);
                break;
            case "isTurboPluginAvailable":
                razorpayDelegate.isTurboPluginAvailable(result);
                break;
            default:
                result.notImplemented();

        }

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        pluginBinding = binding;
        razorpayDelegate = new RazorpayDelegate(binding.getActivity());
        razorpayDelegate.setPackageName(binding.getActivity().getPackageName());
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (pluginBinding != null && razorpayDelegate != null) {
            pluginBinding.removeActivityResultListener(razorpayDelegate);
        }
        pluginBinding = null;
        razorpayDelegate = null;
    }
}
