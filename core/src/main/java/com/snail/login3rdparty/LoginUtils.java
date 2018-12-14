package com.snail.login3rdparty;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * 时间: 2017/9/20 14:22
 * 功能: 工具类
 */

public class LoginUtils {
    public static void updateResourcesLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale locale = Locale.getDefault();
        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            Locale newLocale;
            if ("TW".equals(locale.getCountry()) || "MO".equals(locale.getCountry()) || "HK".equals(locale.getCountry())) {
                newLocale = Locale.TRADITIONAL_CHINESE;
            } else {
                newLocale = Locale.CHINA;
            }
            config.setLocale(newLocale);
        }
    }

    /**
     * 获取重定向后的地址
     * @param url 原地址
     * @return 重定向的地址
     */
    public static String getRedirectUrl(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.getResponseCode();
        return connection.getURL().toString();
    }
    
    public static String request(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String result;
        while ((result = reader.readLine()) != null) {
            builder.append(result);
        }
        connection.disconnect();
        reader.close();
        return builder.toString();
    }
    
    public static String getString(Context context, String name) {
        int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
        if (resId > 0) {
            return context.getString(resId);
        }
        return "";
    }

    /**
     * 获取Application的Meta值
     * @param context 上下文
     * @param name meta名
     * @return 没有返回null
     */
    public static String getApplicationMetaValue(Context context, String name) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Activity的Meta值
     * @param context 上下文
     * @param cls Activity的class
     * @param name meta名
     * @return 没有返回null
     */
    public static String getActivityMetaValue(Context context, Class<?> cls, String name) {
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Receiver的Meta值
     * @param context 上下文
     * @param cls Receiver的class
     * @param name meta名
     * @return 没有返回null
     */
    public static String getReceiverMetaValue(Context context, Class<?> cls, String name) {
        try {
            ActivityInfo info = context.getPackageManager().getReceiverInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Service的Meta值
     * @param context 上下文
     * @param cls Service的class
     * @param name meta名
     * @return 没有返回null
     */
    public static String getServiceMetaValue(Context context, Class<?> cls, String name) {
        try {
            ServiceInfo info = context.getPackageManager().getServiceInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
