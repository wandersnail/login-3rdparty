package com.snail.login3rdparty.weixin;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.snail.login3rdparty.*;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONObject;

/**
 * 时间: 2017/9/28 15:41
 * 功能: 微信登录
 * 需要在app包名目录下建立wxapi包，并在其中新建WXEntryActivity，继承自WXEventActivity，内容不需要写。
 */
public class WeixinLogin extends BaseLogin {
    private String appid;
    private String secret;
    static RespCallback respCallback;

    public WeixinLogin(@NonNull Context context) {
        super(context);
        appid = LoginUtils.getApplicationMetaValue(context, "WEIXIN_APPID");
        secret = LoginUtils.getApplicationMetaValue(context, "WEIXIN_SECRET");
        if (appid == null) {
            appid = "";
        }
        if (secret == null) {
            secret = "";
        }
    }
    
    @Override
    public void login(@NonNull Activity activity, LoginCallback callback) {
        super.login(activity, callback);     
        IWXAPI api = WXAPIFactory.createWXAPI(activity, appid);
        if (!api.isWXAppInstalled()) {
            onError(8888, LoginUtils.getString(context, "tpl_wx_not_install"));
        } else {
            respCallback = new RespCallback();
            api.registerApp(appid);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "third_login_state";
            api.sendReq(req);
        }
    }

    class RespCallback implements Callback<BaseResp> {
        @Override
        public void onCallback(BaseResp baseResp) {
            if (BaseResp.ErrCode.ERR_OK == baseResp.errCode) {
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                if ("third_login_state".equals(resp.state)) {
                    requestUserInfo(resp.code);
                } else {
                    onError(baseResp.errCode, LoginUtils.getString(context, "tpl_login_fail"));
                }
            } else {
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        onError(baseResp.errCode, LoginUtils.getString(context, "tpl_not_support"));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        onCancel();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        onError(baseResp.errCode, LoginUtils.getString(context, "tpl_author_denied"));
                        break;
                    case BaseResp.ErrCode.ERR_BAN:
                        onError(baseResp.errCode, LoginUtils.getString(context, "tpl_packname_or_sign_discrepancy"));
                        break;
                    default:
                        onError(baseResp.errCode, LoginUtils.getString(context, "tpl_login_fail"));
                        break;
                }
            }
        }
    }
            
    private void requestUserInfo(String code) {
        //获取token
        String toekenUrlPattern = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        final String url = String.format(toekenUrlPattern, appid, secret, code);
        new Thread() {
            @Override
            public void run() {
                try {
                    String tokenResp = LoginUtils.request(url);
                    JSONObject tokenJson = new JSONObject(tokenResp);
                    //获取userInfo
                    String userInfoUrlPattern = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
                    final String accessToken = tokenJson.getString("access_token");
                    final String openid = tokenJson.getString("openid");
                    if (respCallback instanceof WxLoginCallback) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((WxLoginCallback) respCallback).onOtherInfo(openid, accessToken);
                            }
                        });
                    }
                    String infoResp = LoginUtils.request(String.format(userInfoUrlPattern, accessToken, openid));
                    JSONObject infoJson = new JSONObject(infoResp);
                    UserInfo info = new UserInfo();
                    info.id = infoJson.getString("unionid");
                    info.nickname = infoJson.optString("nickname");
                    info.gender = infoJson.optInt("sex", 1) == 1 ? "M" : "F";
                    String province = infoJson.optString("province");
                    String city = infoJson.optString("city");
                    info.location = infoJson.optString("country") + (province.isEmpty() ? "" : " " + province) + (city.isEmpty() ? "" : " " + city);
                    info.figureUrl = infoJson.optString("headimgurl");
                    //如果头像链接不为空，将链接将成大图
                    if (!info.figureUrl.isEmpty()) {
                        int lastIndex = info.figureUrl.lastIndexOf("/");
                        if (lastIndex != -1 && lastIndex < info.figureUrl.length() - 1) {
                            String lastNum = info.figureUrl.substring(lastIndex + 1);
                            try {
                                int a = Integer.parseInt(lastNum);
                                if (a != 0) {
                                    info.figureUrl = info.figureUrl.substring(0, lastIndex) + "/" + 0;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    onSuccess(info, infoJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(8888, LoginUtils.getString(context, "tpl_login_fail"));
                }
            }
        }.start();
    }

    @Override
    public int loginType() {
        return WEI_XIN;
    }
}
