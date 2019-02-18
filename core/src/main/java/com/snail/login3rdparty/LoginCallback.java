package com.snail.login3rdparty;

import androidx.annotation.NonNull;

import org.json.JSONObject;

/**
 * 时间: 2017/9/20 13:07
 * 功能: 登录过程的回调接口
 */

public interface LoginCallback {
    /**
     * 成功回调
     * @param loginType {@link BaseLogin#QQ}, {@link BaseLogin#WEI_XIN}, {@link BaseLogin#WEI_BO}, {@link BaseLogin#BAI_DU}, 
     * {@link BaseLogin#TWITTER}, {@link BaseLogin#FACEBOOK}
     */
    void onSuccess(int loginType, @NonNull UserInfo info, @NonNull JSONObject origin);
    void onCancel();
    void onError(int errorCode, String errorDetail);
}
