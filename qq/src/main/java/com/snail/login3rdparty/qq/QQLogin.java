package com.snail.login3rdparty.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.snail.login3rdparty.BaseLogin;
import com.snail.login3rdparty.LoginCallback;
import com.snail.login3rdparty.UserInfo;
import com.snail.login3rdparty.Utils;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * 时间: 2017/9/20 12:58
 * 功能: QQ登录
 */
public class QQLogin extends BaseLogin {
    private Tencent tencent;
    private String scope;

    public QQLogin(Context context) {
        super(context);
        String appid = Utils.getString(context, "tencent_appid");
        tencent = Tencent.createInstance(TextUtils.isEmpty(appid) ? "222222" : appid, context.getApplicationContext());
    }
    
    public void logout() {        
        tencent.logout(context);
    }

    /**
     * 设置登录成功后，需要获取哪些权限
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);
        tencent.login(activity, scope == null ? "get_user_info" : scope, loginListener);
    }
        
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }
    
    private IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                QQLogin.this.onError(8888, Utils.getString(context, "tpl_login_fail"));
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (jsonResponse.length() == 0) {
                QQLogin.this.onError(8888, Utils.getString(context, "tpl_login_fail"));
                return;
            }
            doComplete(jsonResponse);            
        }

        private void doComplete(final JSONObject values) {
            String openid = values.optString("openid");
            if (TextUtils.isEmpty(openid)) {
                QQLogin.this.onError(8888, Utils.getString(context, "tpl_login_fail"));
            } else {
                final UserInfo info = new UserInfo();
                info.id = openid;
                initOpenidAndToken(values);
                if (tencent != null && tencent.isSessionValid()) {
                    IUiListener listener = new IUiListener() {
                        @Override
                        public void onComplete(Object response) {
                            JSONObject json = (JSONObject) response;
                            String gender = json.optString("gender", "M");
                            if (gender.toUpperCase().startsWith("M") || "男".equals(gender)) {
                                info.gender = "M";
                            } else {
                                info.gender = "F";
                            }
                            info.nickname = json.optString("nickname");
                            String city = json.optString("city");
                            info.location = json.optString("province") + (city.isEmpty() ? "" : " " + city);
                            info.figureUrl = json.optString("figureurl_qq_2");
                            Iterator<String> iterator = values.keys();
                            try {   
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    json.put(key, values.get(key));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (callback instanceof QQLoginCallback) {
                                ((QQLoginCallback) callback).onOtherInfo(json.optString(Constants.PARAM_ACCESS_TOKEN), json.optString("pay_token"),
                                        json.optString(Constants.PARAM_EXPIRES_IN), json.optLong("expires_time"));
                            }
                            onSuccess(info, json);
                        }

                        @Override
                        public void onError(UiError error) {
                            QQLogin.this.onError(error.errorCode, error.errorDetail);
                        }

                        @Override
                        public void onCancel() {
                            QQLogin.this.onCancel();
                        }
                    };
                    new com.tencent.connect.UserInfo(context, tencent.getQQToken()).getUserInfo(listener);
                } else {
                    QQLogin.this.onError(8888, Utils.getString(context, "tpl_login_fail"));
                }
            }
        }

        @Override
        public void onError(UiError error) {
            QQLogin.this.onError(error.errorCode, error.errorDetail);
        }

        @Override
        public void onCancel() {
            QQLogin.this.onCancel();
        }
    };

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
            }
        } catch(Exception ignored) {
            onError(8888, Utils.getString(context, "tpl_login_fail"));
        }
    }

    @Override
    public int loginType() {
        return QQ;
    }
}
