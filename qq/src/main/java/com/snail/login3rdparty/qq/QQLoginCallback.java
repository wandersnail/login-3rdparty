package com.snail.login3rdparty.qq;

import android.support.annotation.NonNull;
import com.snail.login3rdparty.LoginCallback;

/**
 * 描述:
 * 时间: 2018/8/9 22:44
 * 作者: zengfansheng
 */
public interface QQLoginCallback extends LoginCallback {
    void onOtherInfo(@NonNull String accessToken, @NonNull String payToken, @NonNull String expiresIn, long expiresTime);
}
