package com.snail.login3rdparty.weixin;

import androidx.annotation.NonNull;
import com.snail.login3rdparty.LoginCallback;

/**
 * 描述:
 * 时间: 2018/8/9 23:22
 * 作者: zengfansheng
 */
public interface WxLoginCallback extends LoginCallback {
    void onOtherInfo(@NonNull String openId, @NonNull String accessToken);
}
