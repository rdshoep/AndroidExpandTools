package com.rdshoep.android.view;
/*
 * @description
 *   Please write the FontManager module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/4/2016)
 */

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.File;

public class FontManager {

    private static final int FOLDER_TYPE_ASSETS = 1;
    private static final int FOLDER_TYPE_CUSTOM = 2;

    private static int folderType = FOLDER_TYPE_ASSETS;
    private static String customPathFolder = null;

    /**
     * 设置字体根目录
     *
     * @param folder 字体目录文件对象
     */
    public static void initFontFolder(File folder) {
        if (folder != null) {
            initFontFolder(folder.getAbsolutePath() + File.separator);
        }
    }

    /**
     * 设置字体目录、默认为assets\fonts
     *
     * @param folderPath 字体的根目录
     */
    public static void initFontFolder(String folderPath) {
        if (folderPath != null) {
            folderType = FOLDER_TYPE_CUSTOM;
            customPathFolder = folderPath;
        }
    }

    /**
     * 根据字体名称获取对应的字体的Typeface
     *
     * @param context  上下文
     * @param fontName 字体的名称（包括根目录下的子集目录）
     * @return 获取的typeface
     */
    public static Typeface getFontFolder(Context context, String fontName) {
        int type = folderType;
        Typeface typeface = null;

        switch (type) {
            case FOLDER_TYPE_ASSETS:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts" + File.separator + fontName);
                break;
            case FOLDER_TYPE_CUSTOM:
                typeface = Typeface.createFromFile(customPathFolder + fontName);
                break;
        }

        return typeface;
    }

    /**
     * 设置TextView的字体
     *
     * @param context  当前上下文
     * @param tv       需要设置字体的TextView
     * @param fontName 字体名称
     * @return 是否成功设置字体
     */
    public static boolean setTextTypeface(Context context, TextView tv, String fontName) {
        boolean apply = false;
        if (!TextUtils.isEmpty(fontName)) {
            Typeface typeface = FontManager.getFontFolder(context, fontName);
            if (typeface != null) {
                tv.setTypeface(typeface);
                apply = true;
            }
        }
        return apply;
    }
}
