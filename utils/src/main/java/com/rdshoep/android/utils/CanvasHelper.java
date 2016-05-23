package com.rdshoep.android.utils;
/*
 * @description
 *   Please write the CanvasHelper module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/23/2016)
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CanvasHelper {

    /**
     * 居中画Text文本
     * http://stackoverflow.com/questions/11120392/android-center-text-on-canvas
     * @param canvas canvas
     * @param paint  paint
     * @param text   需要画的文字内容
     * @param left   左边的偏移量
     * @param top    上边的偏移量
     */
    public static void drawCenterText(Canvas canvas, Paint paint, String text, float left, float top) {
        Rect r = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);

        float x = -r.width() / 2f - r.left;
        float y = r.height() / 2f - r.bottom;

        canvas.drawText(text, left + x, top + y, paint);
    }
}
