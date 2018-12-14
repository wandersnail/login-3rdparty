package com.snail.login3rdparty;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 * 时间: 2017/9/30 13:04
 * 功能: 登录基类
 */

public abstract class BaseLogin {
    public static final int QQ = 1;
    public static final int WEI_XIN = 2;
    public static final int WEI_BO = 3;
    public static final int BAI_DU = 4;
    public static final int FACEBOOK = 5;
    public static final int TWITTER = 6;
    protected LoginCallback callback;
    protected Context context;
    protected Handler mainHandler;

    protected BaseLogin(@NonNull Context context) {
        this.context = context.getApplicationContext();
        LoginUtils.updateResourcesLocale(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void login(@NonNull Activity activity, LoginCallback callback) {
        this.callback = callback;
    }
    
    protected void onSuccess(final UserInfo info, final JSONObject origin) {
        if (callback != null)
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(loginType(), info, origin);
                }
            });
    }

    protected void onCancel() {
        if (callback != null)
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCancel();
                }
            });            
    }

    protected void onError(final int errorCode, final String errorDetail) {
        if (callback != null)
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(errorCode, errorDetail);
                }
            });            
    }

    public abstract int loginType();
}
