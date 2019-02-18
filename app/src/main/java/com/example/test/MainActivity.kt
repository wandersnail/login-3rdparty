package com.example.test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import com.snail.commons.entity.PermissionsRequester
import com.snail.commons.utils.ImageUtils
import com.snail.commons.utils.ToastUtils
import com.snail.login3rdparty.BaseLogin
import com.snail.login3rdparty.LoginCallback
import com.snail.login3rdparty.UserInfo
import com.snail.login3rdparty.baidu.BaiduLogin
import com.snail.login3rdparty.facebook.FacebookLogin
import com.snail.login3rdparty.qq.QQLogin
import com.snail.login3rdparty.twitter.TwitterLogin
import com.snail.login3rdparty.weibo.WeiboLogin
import com.snail.login3rdparty.weixin.WeixinLogin
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var requester: PermissionsRequester? = null
    private var weixinLogin: WeixinLogin? = null
    private var qqLogin: QQLogin? = null
    private var weiboLogin: WeiboLogin? = null
    private var baiduLogin: BaiduLogin? = null
    private var twitterLogin: TwitterLogin? = null
    private var facebookLogin: FacebookLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requester = PermissionsRequester(this)
        val list = ArrayList<String>()
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        list.add(Manifest.permission.ACCESS_NETWORK_STATE)
        list.add(Manifest.permission.READ_PHONE_STATE)
        requester?.check(list)
        weixinLogin = WeixinLogin(this)
        qqLogin = QQLogin(this)
        weiboLogin = WeiboLogin(this)
        baiduLogin = BaiduLogin(this)
        twitterLogin = TwitterLogin(this)
        facebookLogin = FacebookLogin(this)
        btnWeixin.setOnClickListener {
            clearText()
            weixinLogin?.login(this, loginCallback)
        }
        btnQQ.setOnClickListener {
            clearText()
            qqLogin?.login(this, loginCallback)
        }
        btnWeibo.setOnClickListener {
            clearText()
            weiboLogin?.login(this, loginCallback)
        }
        btnBaidu.setOnClickListener {
            clearText()
            baiduLogin?.login(this, loginCallback)
        }
        btnTwitter.setOnClickListener {
            clearText()
            twitterLogin?.login(this, loginCallback)
        }
        btnFacebook.setOnClickListener {
            clearText()
            facebookLogin?.login(this, loginCallback)
        }
    }

    private fun clearText() {
        tv.text = ""
    }

    private val loginCallback = object : LoginCallback {
        override fun onSuccess(loginType: Int, info: UserInfo, origin: JSONObject) {
            val type = when (loginType) {
                BaseLogin.QQ -> "QQ"
                BaseLogin.WEI_XIN -> "微信"
                BaseLogin.WEI_BO -> "微博"
                BaseLogin.BAI_DU -> "百度"
                BaseLogin.TWITTER -> "Twitter"
                BaseLogin.FACEBOOK -> "Facebook"
                else -> ""
            }
            tv.append("类型: $type\n")
            tv.append("id: ${info.id}\n")
            tv.append("性别: ${info.gender}\n")
            tv.append("昵称: ${info.nickname}\n")
            tv.append("所在地: ${info.location}\n")
            tv.append("头像: ${info.figureUrl}\n\n")
            tv.append("原始数据\n")
            tv.append(origin.toString().replace(",", "\n"))
            if (!TextUtils.isEmpty(info.figureUrl)) {
                object : Thread() {
                    override fun run() {
                        val bitmap = ImageUtils.getNetBitmap(info.figureUrl)
                        runOnUiThread { iv.setImageBitmap(bitmap) }
                    }
                }.start()
            }
        }

        override fun onCancel() {
            tv.text = ""
            iv.setImageBitmap(null)
            ToastUtils.showShort("用户取消")
        }

        override fun onError(errorCode: Int, errorDetail: String?) {
            tv.text = ""
            iv.setImageBitmap(null)
            ToastUtils.showShort(errorDetail)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requester?.onActivityResult(requestCode, resultCode, data)
        qqLogin?.onActivityResult(requestCode, resultCode, data)
        weiboLogin?.onActivityResult(requestCode, resultCode, data)
        twitterLogin?.onActivityResult(requestCode, resultCode, data)
        facebookLogin?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requester?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        qqLogin?.logout()
    }
}
