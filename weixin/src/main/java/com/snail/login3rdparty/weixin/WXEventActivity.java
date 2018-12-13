package com.snail.login3rdparty.weixin;

import android.app.Activity;
import android.os.Bundle;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEventActivity extends Activity implements IWXAPIEventHandler {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, null);
        iwxapi.handleIntent(getIntent(), this);
    }
    
    @Override
    public void onReq(BaseReq baseReq) {}

    @Override
    public void onResp(BaseResp baseResp) {
        if (WeixinLogin.respCallback != null) {
            WeixinLogin.respCallback.onCallback(baseResp);
        }        
        finish();
    }

    @Override
    protected void onDestroy() {
        WeixinLogin.respCallback = null;
        super.onDestroy();
    }
}