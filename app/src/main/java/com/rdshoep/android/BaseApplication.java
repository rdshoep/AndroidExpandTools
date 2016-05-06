package com.rdshoep.android;
/*
 * @description
 *   Please write the BaseApplication module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/4/2016)
 */

import android.app.Application;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        //可以通过此方法设置字体存放的目录
//        FontManager.initFontFolder(getFilesDir().getParentFile());
    }
}
