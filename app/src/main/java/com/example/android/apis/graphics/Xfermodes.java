/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.View;

import com.example.android.apis.utils.AppUtil;

public class Xfermodes extends GraphicsActivity {

    // create a bitmap with a circle, used for the "dst" image
    static Bitmap makeDst(int w, int h) {
        w = w * 3 / 4;
        h = h * 3 / 4;
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(0, 0, w, h), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc(int w, int h) {
        w = w * 5 / 8;
        h = h * 5 / 8;
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF66AAFF);
        c.drawRect(0, 0, w, h, p);
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    private static class SampleView extends View {
        private int W = 0;
        private int H = 0;
        private static final int ROW_MAX = 4;   // number of samples per row

        private Bitmap mSrcB;
        private Bitmap mDstB;
        private Shader mBG;     // background checker-board pattern

        private static final Xfermode[] sModes = {
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                new PorterDuffXfermode(PorterDuff.Mode.SRC),
                new PorterDuffXfermode(PorterDuff.Mode.DST),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.XOR),
                new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
                new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
                new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
                new PorterDuffXfermode(PorterDuff.Mode.SCREEN),
                new PorterDuffXfermode(PorterDuff.Mode.ADD),
                new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        };

        private static final String[] sLabels = {
                "Clear", "Src", "Dst", "SrcOver",
                "DstOver", "SrcIn", "DstIn", "SrcOut",
                "DstOut", "SrcATop", "DstATop", "Xor",
                "Darken", "Lighten", "Multiply", "Screen",
                "Add", "Overlay"
        };


        private final int DX_TRANSLATE = 15;
        private final int DY_TRANSLATE = 25;

        private final int X_INTERVAL = 10;
        private final int Y_INTERVAL = 25;

        private final int TEXT_SIZE = 40;


        private int x_translate = 0;
        private int y_translate = 0;
        private int x_interval = 0;
        private int y_interval = 0;

        private Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint paint = new Paint();

        public SampleView(Context context) {
            super(context);

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            setLayerType(View.LAYER_TYPE_HARDWARE, null);

            //
            x_translate = AppUtil.dp2px(context, DX_TRANSLATE);
            y_translate = AppUtil.dp2px(context, DY_TRANSLATE);
            x_interval = AppUtil.dp2px(context, X_INTERVAL);
            y_interval = AppUtil.dp2px(context, Y_INTERVAL);

            //
            W = (AppUtil.getScreenWidth(context) - x_translate * 2 - x_interval * 3) / 4;
            H = (AppUtil.getScreenHeight(context) - y_translate * 2 - y_interval * 3) / 4;
            if (W < H) {
                H = W;
            }


            //
            mSrcB = makeSrc(W, H);
            mDstB = makeDst(W, H);

            // make a ckeckerboard pattern
            Bitmap bm = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC,
                            0xFFCCCCCC, 0xFFFFFFFF}, 2, 2,
                    Bitmap.Config.RGB_565);
            mBG = new BitmapShader(bm,
                    Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
            Matrix m = new Matrix();
            m.setScale(6, 6);
            mBG.setLocalMatrix(m);


            //
            labelP.setTextAlign(Paint.Align.CENTER);
            labelP.setTextSize(TEXT_SIZE);
            paint.setFilterBitmap(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            canvas.translate(x_translate, y_translate);

            int x = 0;
            int y = 0;
            for (int i = 0; i < sModes.length; i++) {
                // draw the border
                paint.setStyle(Paint.Style.STROKE);
                paint.setShader(null);
                canvas.drawRect(x - 0.5f, y - 0.5f,
                        x + W + 0.5f, y + H + 0.5f, paint);

                // draw the checker-board pattern
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(mBG);
                canvas.drawRect(x, y, x + W, y + H, paint);

                // draw the src/dst example into our offscreen bitmap
                int sc = canvas.saveLayer(x, y, x + W, y + H, null,
                        Canvas.ALL_SAVE_FLAG);

                canvas.translate(x, y);
                canvas.drawBitmap(mDstB, 0, 0, paint);
                paint.setXfermode(sModes[i]);
                canvas.drawBitmap(mSrcB, W * 3 / 8, H * 3 / 8, paint);
                paint.setXfermode(null);
                canvas.restoreToCount(sc);

                // draw the label
                canvas.drawText(sLabels[i],
                        x + W / 2, y - labelP.getTextSize() / 2, labelP);

                x += W + x_interval;

                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0;
                    y += H + y_interval;
                }
            }
        }
    }
}

