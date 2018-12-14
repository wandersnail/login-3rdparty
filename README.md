# login-3rdparty
第三方登录库，集合微信、QQ、微博、百度、Twitter、Facebook

## 代码托管
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-core/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-core/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-qq/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-qq/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-weixin/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-weixin/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-weibo/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-weibo/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-baidu/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-baidu/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-twitter/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-twitter/_latestVersion)
[![Download](https://api.bintray.com/packages/wandersnail/android/login-3rdparty-facebook/images/download.svg) ](https://bintray.com/wandersnail/android/login-3rdparty-facebook/_latestVersion)

## 使用

1. app的build.gradle里添加配置如下，各平台的SDK密钥，按需添加，其中com.example.test修改为你应用的包名。
```
buildTypes {
    debug {
        addManifestPlaceholders([packname: "com.example.test"])
        addManifestPlaceholders([tencent_appid: "1106841690"])
        addManifestPlaceholders([baidu_apikey: "u10rwmxIpFG8YTXYaGXSI6lV"])
        addManifestPlaceholders([weibo_appkey: "85283146", weibo_channel: "weibo"])
        addManifestPlaceholders([weixin_appid: "wxb174e4a70dc1779a", weixin_secret: "6e85eb9e5aabc5bd46d695b7cda66d08"])
        addManifestPlaceholders([twitter_apikey: "J20TVzE1VihlIt5Vr3XufK6pN", twitter_secret: "aiaym3F07GpRMsecMrjZ2ocmrI7T0TGXfmEqXebpWrUAciKG0v"])
        addManifestPlaceholders([facebook_appid: "1852628958197307"])
    }
    release {
        addManifestPlaceholders([packname: "com.example.test"])
        addManifestPlaceholders([tencent_appid: "1106841690"])
        addManifestPlaceholders([baidu_apikey: "u10rwmxIpFG8YTXYaGXSI6lV"])
        addManifestPlaceholders([weibo_appkey: "85283146", weibo_channel: "weibo"])
        addManifestPlaceholders([weixin_appid: "wxb174e4a70dc1779a", weixin_secret: "6e85eb9e5aabc5bd46d695b7cda66d08"])
        addManifestPlaceholders([twitter_apikey: "J20TVzE1VihlIt5Vr3XufK6pN", twitter_secret: "aiaym3F07GpRMsecMrjZ2ocmrI7T0TGXfmEqXebpWrUAciKG0v"])
        addManifestPlaceholders([facebook_appid: "1852628958197307"])
        ...
    }
}
```

2. app的build.gradle中的添加依赖，自行修改为最新版本，同步后通常就可以用了，**login-3rdparty-core**是必须的，其他的按需添加：
```
dependencies {
	...
	implementation 'com.github.wandersnail:login-3rdparty-core:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-qq:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-weixin:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-weibo:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-baidu:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-twitter:1.0.0'
	implementation 'com.github.wandersnail:login-3rdparty-facebook:1.0.0'
}
```

3. 如果从jcenter下载失败。在project的build.gradle里的repositories添加内容。
```
allprojects {
	repositories {
		...
		maven { url 'https://dl.bintray.com/wandersnail/android/' }
	}
}
``` 

4. AndroidManifest.xml添加权限
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!-- 用于调用 JNI -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

5. 其他平台以上就可以了，微信还需再添加一个文件：在你的包名相应目录下新建一个wxapi目录，并在该wxapi目录下新增一个WXEntryActivity类，该类继承自WXEventActivity，内容不需要写。如下图：
![image](https://github.com/wandersnail/login-3rdparty/blob/master/screenshot/0d12b411b69c21f97460983f0e22280e5ec424032.jpg)

## 示例
```
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
```

## 关于平台密钥获取
### 微信
到[https://open.weixin.qq.com](https://open.weixin.qq.com)创建移动应用，需要认证通过的开发者才能开通登录权限。没有开通登录权限是无法调起授权页面的。

### QQ
到[http://open.qq.com/](http://open.qq.com/)的应用管理里创建应用即可，不需要提交审核即可登录授权。

### 微博
到[http://open.weibo.com/development/mobile](http://open.weibo.com/development/mobile)接入应用，回调页填写：https://api.weibo.com/oauth2/default.html，也可以填写自定义的，如果填写了自定义的，在实例化WeiboLogin时需要使用传入回调页链接的构造方法。填写完资料即可完成接入，不需要提交审核即可测试登录。

### 百度
到[http://developer.baidu.com/](http://developer.baidu.com/)注册帐号，右上角用户名下拉菜单“应用管理”里创建工程，创建后就有apikey，安全设置里如果启用了“Implicit Grant授权方式”，授权回调页：必须填写，内容填写任意符合uri规则字符串即可。

### Twitter
到[https://apps.twitter.com/](https://apps.twitter.com/)注册帐号并创建应用即可获取。

### Facebook
到[https://developers.facebook.com/](https://developers.facebook.com/)注册帐号并添加应用即可获取。注意下：Facebook添加开发和发布密钥散列时，生成的不是28字符时，使用命令提示符，当时我用PowerShell死活没生成成功，都是不到28字符的。