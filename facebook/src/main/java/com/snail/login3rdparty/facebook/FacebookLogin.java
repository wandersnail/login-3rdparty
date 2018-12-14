package com.snail.login3rdparty.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.snail.login3rdparty.BaseLogin;
import com.snail.login3rdparty.LoginCallback;
import com.snail.login3rdparty.LoginUtils;
import com.snail.login3rdparty.UserInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

/**
 * 时间: 2017/10/13 13:52
 * 功能: Facebook登录
 */

public class FacebookLogin extends BaseLogin {
    private static final String FIGURE_URL_PATTERN = "http://graph.facebook.com/%s/picture?type=large";
    private CallbackManager callbackManager;
    
    public FacebookLogin(@NonNull Context context) {
        super(context);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FbCallback());
    }

    @Override
    public int loginType() {
        return FACEBOOK;
    }
    
    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);
        LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("public_profile"));
    }
    
    private class FbCallback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            //获取用户信息
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(final JSONObject json, GraphResponse response) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (json != null) {
                                try {
                                    UserInfo info = new UserInfo();
                                    info.id = json.getString("id");
                                    info.nickname = json.optString("name");
                                    info.gender = json.optString("gender", "male").equals("male") ? "M" : "F";
                                    info.location = json.optString("locale");
                                    try {
                                        String figureUrl = String.format(FIGURE_URL_PATTERN, info.id);
                                        //获取重定向后的地址
                                        HttpURLConnection connection = (HttpURLConnection) new URL(figureUrl).openConnection();
                                        connection.setInstanceFollowRedirects(true);
                                        connection.getResponseCode();
                                        info.figureUrl = connection.getURL().toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    FacebookLogin.this.onSuccess(info, json);
                                } catch (JSONException e) {
                                    FacebookLogin.this.onError(8888, LoginUtils.getString(context, "tpl_login_fail"));
                                }
                            } else {
                                FacebookLogin.this.onError(8888, LoginUtils.getString(context, "tpl_login_fail"));
                            }
                        }
                    }).start();                    
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,gender,locale");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            FacebookLogin.this.onCancel();
        }

        @Override
        public void onError(FacebookException e) {
            FacebookLogin.this.onError(8888, e.getMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
