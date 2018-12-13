package com.snail.login3rdparty;

import org.json.JSONObject;

/**
 * 时间: 2017/9/20 13:07
 * 功能: 登录过程的回调接口
 */

public interface LoginCallback {
    void onSuccess(int loginType, UserInfo info, JSONObject origin);
    void onCancel();
    void onError(int errorCode, String errorDetail);
}
