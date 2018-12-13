package com.snail.login3rdparty.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.*;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.snail.login3rdparty.BaseLogin;
import com.snail.login3rdparty.LoginCallback;
import com.snail.login3rdparty.UserInfo;
import com.snail.login3rdparty.Utils;
import org.json.JSONObject;

/**
 * 时间: 2017/9/29 17:11
 * 功能: 新浪微博登录
 */

public class WeiboLogin extends BaseLogin {
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    private static final String SCOPE = "email";
    private static final String GET_USER_INFO_URL_PATTERN = "https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s";
    private SsoHandler ssoHandler;
    
    public WeiboLogin(Context context) {
        this(context, REDIRECT_URL);
    }
    
    public WeiboLogin(Context context, String redirectUrl) {
        super(context);
        String appkey = Utils.getString(context, "weibo_appkey");
        if (appkey.isEmpty()) {
            appkey = Utils.getApplicationMetaValue(context, "WEIBO_APPKEY");
        }
        WbSdk.install(context, new AuthInfo(this.context, appkey == null ? "" : appkey, redirectUrl, SCOPE));
    }
    
    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);
        ssoHandler = new SsoHandler(activity);
        ssoHandler.authorize(new SelfWbAuthListener());
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    
    private boolean isError(JSONObject json) {
        int code = -1;
        try {
            code = Integer.valueOf(json.get("error_code").toString());
        } catch (Exception ignored) {}
        if (code != -1) {
            onError(code, json.optString("error", ""));
            return true;
        }
        return false;
    }

    @Override
    public int loginType() {
        return WEI_BO;
    }

    private class SelfWbAuthListener implements WbAuthListener {

        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            if (token.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(context, token);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //获取用户信息
                            String infoJsonStr = Utils.request(String.format(GET_USER_INFO_URL_PATTERN, token.getToken(), token.getUid()));
                            JSONObject infoJson = new JSONObject(infoJsonStr);
                            if (!isError(infoJson)) {
                                UserInfo info = new UserInfo();
                                info.id = infoJson.getString("idstr");
                                info.nickname = infoJson.optString("screen_name");
                                info.gender = infoJson.optString("gender", "M").toUpperCase().equals("M") ? "M" : "F";
                                info.location = infoJson.optString("location");
                                String figureUrl = infoJson.optString("avatar_hd");
                                if (figureUrl.isEmpty()) {
                                    figureUrl = infoJson.optString("avatar_large");
                                    if (figureUrl.isEmpty()) {
                                        figureUrl = infoJson.optString("profile_image_url");
                                    }
                                }
                                info.figureUrl = figureUrl;
                                WeiboLogin.this.onSuccess(info, infoJson);
                            }
                            onError(8888, Utils.getString(context, "tpl_login_fail"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            onError(8888, e.toString());
                        }
                    }
                }).start();
            }
        }

        @Override
        public void cancel() {
            onCancel();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMsg) {
            int code = -1;
            try {
                code = Integer.valueOf(errorMsg.getErrorCode());
            } catch (Exception ignored) {}
            onError(code, errorMsg.getErrorMessage());
        }
    }
}
