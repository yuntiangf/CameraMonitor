/* 
 * @ProjectName VideoGoJar
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName EzvizApplication.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-7-12
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.example.cameramonitor;

import android.app.Application;
import android.content.res.Configuration;
import android.provider.Settings.System;

import com.baidu.frontia.FrontiaApplication;
import com.videogo.constant.Config;
import com.videogo.openapi.EzvizAPI;

/**
 * 自定义应用
 * @author chenxingyf1
 * @data 2014-7-12
 */
public class EzvizApplication extends FrontiaApplication{
    //开放平台申请的APP key & secret key
    //open
    public static String APP_KEY = "c97811a420b6456b9bfa05ae31c42e84";
    
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
            
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Config.LOGGING = true;
        EzvizAPI.getInstance();
        EzvizAPI.init(this, APP_KEY); 
        //EzvizAPI.init(this, APP_KEY, "/mnt/sdcard/VideoGo/libs/"); 
        EzvizAPI.getInstance().setServerUrl(API_URL, WEB_URL);     
        //EzvizAPI.getInstance().setAccessToken("at.dmtlxyp47nejsckiai1pdwzsdvxmo7jp-8ofxo9vacz-1s48ov1-p3r36v0vj");
        //EzvizAPI.getInstance().setUserCode("71cd711da693b315");
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        //EzvizAPI.getInstance().gotoLoginPage(false);
    }

}
