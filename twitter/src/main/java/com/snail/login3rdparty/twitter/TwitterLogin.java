package com.snail.login3rdparty.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.snail.login3rdparty.BaseLogin;
import com.snail.login3rdparty.LoginCallback;
import com.snail.login3rdparty.UserInfo;
import com.snail.login3rdparty.LoginUtils;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import org.json.JSONObject;
import retrofit2.Call;

import java.lang.reflect.Field;

/**
 * 时间: 2017/10/14 21:05
 * 作者: zengfansheng
 * 功能: Twitter登录。
 */

public class TwitterLogin extends BaseLogin {
    private TwitterLoginButton btnLogin;
    
    public TwitterLogin(@NonNull Context context) {
        super(context);
        String apikey = LoginUtils.getApplicationMetaValue(context, "TWITTER_APIKEY");
        String secret = LoginUtils.getApplicationMetaValue(context, "TWITTER_SECRET");
        TwitterConfig config = new TwitterConfig.Builder(context.getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(apikey == null ? "" : apikey, secret == null ? "" : secret))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);
        btnLogin = new TwitterLoginButton(activity);
        btnLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterApiClient client = TwitterCore.getInstance().getApiClient(result.data);
                Call<User> verifyRequest = client.getAccountService().verifyCredentials(true, false, true);
                verifyRequest.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        UserInfo info = new UserInfo();
                        info.id = result.data.idStr;
                        info.nickname = result.data.screenName;
                        info.location = result.data.location;
                        info.figureUrl = result.data.profileImageUrl;
                        info.figureUrl = info.figureUrl.replace("_normal.", "_400x400.");
                        JSONObject json = new JSONObject();
                        try {
                            getUserJson(result.data, json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onSuccess(info, json);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        onError(8888, e.toString());
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                String error = e.toString();
                if (error.contains("request was canceled")) {
                    onCancel();
                } else if (error.contains("bundle incomplete")) {
                    onError(8888, LoginUtils.getString(context, "tpl_author_denied"));
                } else {
                    onError(8888, error);
                }
            }
        });
        btnLogin.callOnClick();
    }

    //将user的值封装成json对象
    private void getUserJson(User user, JSONObject json) throws Exception {
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            SerializedName serializedName = field.getAnnotation(SerializedName.class);
            //如果有注解
            if (serializedName != null) {
                String name = serializedName.value();
                //获取字段值
                field.setAccessible(true);
                Object value = field.get(user);
                if (value instanceof Integer || value instanceof Long || value instanceof Double ||
                        value instanceof Boolean || value instanceof String) {
                    json.put(name, value);
                }
            }
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (btnLogin != null) {
            btnLogin.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    @Override
    public int loginType() {
        return TWITTER;
    }
}
