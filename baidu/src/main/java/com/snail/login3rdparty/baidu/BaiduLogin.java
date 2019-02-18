package com.snail.login3rdparty.baidu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.baidu.api.*;
import com.snail.login3rdparty.BaseLogin;
import com.snail.login3rdparty.LoginCallback;
import com.snail.login3rdparty.UserInfo;
import com.snail.login3rdparty.LoginUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * 时间: 2017/10/11 09:02
 * 功能: 百度登录
 */

public class BaiduLogin extends BaseLogin {
    private String apikey;
    private Baidu baidu;

    public BaiduLogin(@NonNull Context context) {
        super(context);
        apikey = LoginUtils.getApplicationMetaValue(context, "BAIDU_APIKEY");
    }

    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);
        baidu = new Baidu(apikey == null ? "" : apikey, activity);
        baidu.authorize(activity, false, true, listener);
    }
    
    private BaiduDialog.BaiduDialogListener listener = new BaiduDialog.BaiduDialogListener() {
        @Override
        public void onComplete(Bundle bundle) {
            //获取用户信息
            new AsyncBaiduRunner(baidu).request("https://openapi.baidu.com/rest/2.0/passport/users/getInfo", null, "POST", new BdRequestListener());
        }

        @Override
        public void onBaiduException(BaiduException e) {
            BaiduLogin.this.onError(8888, LoginUtils.getString(context, "tpl_login_fail"));
        }

        @Override
        public void onError(BaiduDialogError error) {
            BaiduLogin.this.onError(error.getErrorCode(), error.getFailingUrl());
        }

        @Override
        public void onCancel() {
            BaiduLogin.this.onCancel();
        }
    };
    
    private class BdRequestListener implements AsyncBaiduRunner.RequestListener {
        @Override
        public void onComplete(String s) {
            try {
                JSONObject json = new JSONObject(s);
                UserInfo info = new UserInfo();
                info.id = json.get("userid").toString();
                info.nickname = json.optString("realname");
                if (info.nickname.isEmpty()) {
                    info.nickname = json.optString("username");
                }
                info.gender = json.optString("sex", "1").equals("1") ? "M" : "F";
                info.figureUrl = json.optString("portrait");
                if (!info.figureUrl.isEmpty()) {
                    info.figureUrl = "http://tb.himg.baidu.com/sys/portrait/item/" + info.figureUrl;
                }
                onSuccess(info, json);
            } catch (JSONException e) {
                onError(8888, e.getMessage());
            }            
        }

        @Override
        public void onIOException(IOException e) {
            onError(8888, LoginUtils.getString(context, "tpl_login_fail"));
        }

        @Override
        public void onBaiduException(BaiduException error) {
            int code = 8888;
            try {
                code = Integer.valueOf(error.getErrorCode());
            } catch (Exception ignored) {}
            onError(code, error.getErrorDesp());
        }
    }
    
    @Override
    public int loginType() {
        return BAI_DU;
    }
}
