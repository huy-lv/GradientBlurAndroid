package com.huylv.gradientblurandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.opencv.core.CvType.CV_8UC1;

public class TestActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    Bitmap srcBm,dstBm,gradientBm;
    int imagePixels[];
    int width,height;

    @BindView(R.id.ivMain)
    ImageView ivMain;
    @BindView(R.id.btStart)
    Button btStart;
    @BindView(R.id.sbRotation)
    SeekBar sbRotation;
    @BindView(R.id.sbCenterY)
    SeekBar sbCenterY;
    int mul_table[] = {
            512, 512, 456, 512, 328, 456, 335, 512, 405, 328, 271, 456, 388, 335, 292, 512,
            454, 405, 364, 328, 298, 271, 496, 456, 420, 388, 360, 335, 312, 292, 273, 512,
            482, 454, 428, 405, 383, 364, 345, 328, 312, 298, 284, 271, 259, 496, 475, 456,
            437, 420, 404, 388, 374, 360, 347, 335, 323, 312, 302, 292, 282, 273, 265, 512,
            497, 482, 468, 454, 441, 428, 417, 405, 394, 383, 373, 364, 354, 345, 337, 328,
            320, 312, 305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475, 465, 456,
            446, 437, 428, 420, 412, 404, 396, 388, 381, 374, 367, 360, 354, 347, 341, 335,
            329, 323, 318, 312, 307, 302, 297, 292, 287, 282, 278, 273, 269, 265, 261, 512,
            505, 497, 489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417, 411, 405,
            399, 394, 389, 383, 378, 373, 368, 364, 359, 354, 350, 345, 341, 337, 332, 328,
            324, 320, 316, 312, 309, 305, 301, 298, 294, 291, 287, 284, 281, 278, 274, 271,
            268, 265, 262, 259, 257, 507, 501, 496, 491, 485, 480, 475, 470, 465, 460, 456,
            451, 446, 442, 437, 433, 428, 424, 420, 416, 412, 408, 404, 400, 396, 392, 388,
            385, 381, 377, 374, 370, 367, 363, 360, 357, 354, 350, 347, 344, 341, 338, 335,
            332, 329, 326, 323, 320, 318, 315, 312, 310, 307, 304, 302, 299, 297, 294, 292,
            289, 287, 285, 282, 280, 278, 275, 273, 271, 269, 267, 265, 263, 261, 259 };
    int shg_table[] = {
            9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 17,
            17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19,
            19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21,
            21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
            21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22,
            22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
            22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23,
            23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24 };
    private Mat graMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        srcBm = BitmapFactory.decodeResource(getResources(),R.mipmap.s1);
        width = srcBm.getWidth();
        height = srcBm.getHeight();
        dstBm = srcBm.copy(srcBm.getConfig(),true);

        //start
        int wh = width*height;
        int wh4 = wh*4;

        sbRotation.setOnSeekBarChangeListener(this);
        sbCenterY.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.sbRotation:
                gradientBlur(srcBm,dstBm,gradientBm,progress);
                break;
            case R.id.sbCenterY:
                MainActivity.createLinearGradientMat(graMat.getNativeObjAddr(),0,progress,20,100,135);
                Utils.matToBitmap(graMat,gradientBm);
                gradientBlur(srcBm,dstBm,gradientBm,30);
        }
        ivMain.setImageBitmap(dstBm);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @OnClick(R.id.btStart)
    void onclick(){
        //create gradient
        gradientBm = srcBm.copy(srcBm.getConfig(),true);
        graMat = new Mat(height,width,CV_8UC1);
        MainActivity.createLinearGradientMat(graMat.getNativeObjAddr(),0,0,40,100,135);
        Utils.matToBitmap(graMat,gradientBm);

        //blur
        gradientBlur(srcBm,dstBm,gradientBm,30);
        ivMain.setImageBitmap(dstBm);
    }

    void gradientBlur(Bitmap srcMat, Bitmap dstMat,Bitmap radiusData,int radius){
        int blurLevels = 2;
        float increaseFactor = 1.5f;
        float divider = increaseFactor;
        for(int i=1;i<blurLevels;i++){
            divider += Math.pow(increaseFactor,i+1);
        }
        float startRadius = radius/divider;

        int x, y, i, p, yp, yi, yw, r_sum, g_sum, b_sum,
                r_out_sum, g_out_sum, b_out_sum,
                r_in_sum, g_in_sum, b_in_sum,
                pr, pg, pb, rbs;
        int wh = width * height;
        int wh4 = wh << 2;
        int pixels[] = new int[wh4];

        int imagePixels[] = new int[wh4];
        int radiusPixels[] = new int[wh4];
        bitmapToArray(srcMat, imagePixels);
        bitmapToArray(radiusData, radiusPixels);

        System.arraycopy(imagePixels,0,pixels,0,wh4);

        int currentIndex = 0;
        int steps = blurLevels;
        blurLevels -= 1;


        while (steps-- >= 0)
        {
            int iradius = (int) (radius + 0.5);
            if (iradius == 0) continue;
            if (iradius > 256) iradius = 256;
            int div = iradius + iradius + 1;
            int w4 = width << 2;
            int widthMinus1 = width - 1;
            int heightMinus1 = height - 1;
            int radiusPlus1 = iradius + 1;
            int sumFactor = radiusPlus1 * (radiusPlus1 + 1) / 2;

            BlurStack stackStart = new BlurStack();
            BlurStack stack = stackStart;
            BlurStack stackEnd = null;
            for (i = 1; i < div; i++)
            {
                stack = stack.next = new BlurStack();
                if (i == radiusPlus1) stackEnd = stack;
            }
            stack.next = stackStart;
            BlurStack stackIn = null;
            BlurStack stackOut = null;

            yw = yi = 0;

            int mul_sum = mul_table[iradius];
            int shg_sum = shg_table[iradius];

            for (y = 0; y < height; y++)
            {
                r_in_sum = g_in_sum = b_in_sum = r_sum = g_sum = b_sum = 0;

                r_out_sum = radiusPlus1 * (pr = pixels[yi]);
                g_out_sum = radiusPlus1 * (pg = pixels[yi + 1]);
                b_out_sum = radiusPlus1 * (pb = pixels[yi + 2]);

                r_sum += sumFactor * pr;
                g_sum += sumFactor * pg;
                b_sum += sumFactor * pb;

                stack = stackStart;

                for (i = 0; i < radiusPlus1; i++)
                {
                    stack.r = pr;
                    stack.g = pg;
                    stack.b = pb;
                    stack = stack.next;
                }

                for (i = 1; i < radiusPlus1; i++)
                {
                    p = yi + ((widthMinus1 < i ? widthMinus1 : i) << 2);
                    r_sum += (stack.r = (pr = pixels[p])) * (rbs = radiusPlus1 - i);
                    g_sum += (stack.g = (pg = pixels[p + 1])) * rbs;
                    b_sum += (stack.b = (pb = pixels[p + 2])) * rbs;

                    r_in_sum += pr;
                    g_in_sum += pg;
                    b_in_sum += pb;

                    stack = stack.next;
                }


                stackIn = stackStart;
                stackOut = stackEnd;
                for (x = 0; x < width; x++)
                {
                    pixels[yi] = (r_sum * mul_sum) >> shg_sum;
                    pixels[yi + 1] = (g_sum * mul_sum) >> shg_sum;
                    pixels[yi + 2] = (b_sum * mul_sum) >> shg_sum;

                    r_sum -= r_out_sum;
                    g_sum -= g_out_sum;
                    b_sum -= b_out_sum;

                    r_out_sum -= stackIn.r;
                    g_out_sum -= stackIn.g;
                    b_out_sum -= stackIn.b;

                    p = (yw + ((p = x + radiusPlus1) < widthMinus1 ? p : widthMinus1)) << 2;

                    r_in_sum += (stackIn.r = pixels[p]);
                    g_in_sum += (stackIn.g = pixels[p + 1]);
                    b_in_sum += (stackIn.b = pixels[p + 2]);

                    r_sum += r_in_sum;
                    g_sum += g_in_sum;
                    b_sum += b_in_sum;

                    stackIn = stackIn.next;

                    r_out_sum += (pr = stackOut.r);
                    g_out_sum += (pg = stackOut.g);
                    b_out_sum += (pb = stackOut.b);

                    r_in_sum -= pr;
                    g_in_sum -= pg;
                    b_in_sum -= pb;

                    stackOut = stackOut.next;

                    yi += 4;
                }
                yw += width;
            }


            for (x = 0; x < width; x++)
            {
                g_in_sum = b_in_sum = r_in_sum = g_sum = b_sum = r_sum = 0;

                yi = x << 2;
                r_out_sum = radiusPlus1 * (pr = pixels[yi]);
                g_out_sum = radiusPlus1 * (pg = pixels[yi + 1]);
                b_out_sum = radiusPlus1 * (pb = pixels[yi + 2]);

                r_sum += sumFactor * pr;
                g_sum += sumFactor * pg;
                b_sum += sumFactor * pb;

                stack = stackStart;

                for (i = 0; i < radiusPlus1; i++)
                {
                    stack.r = pr;
                    stack.g = pg;
                    stack.b = pb;
                    stack = stack.next;
                }

                yp = width;

                for (i = 1; i < radiusPlus1; i++)
                {
                    yi = (yp + x) << 2;

                    r_sum += (stack.r = (pr = pixels[yi])) * (rbs = radiusPlus1 - i);
                    g_sum += (stack.g = (pg = pixels[yi + 1])) * rbs;
                    b_sum += (stack.b = (pb = pixels[yi + 2])) * rbs;

                    r_in_sum += pr;
                    g_in_sum += pg;
                    b_in_sum += pb;

                    stack = stack.next;

                    if (i < heightMinus1)
                    {
                        yp += width;
                    }
                }

                yi = x;
                stackIn = stackStart;
                stackOut = stackEnd;
                for (y = 0; y < height; y++)
                {
                    p = yi << 2;
                    pixels[p] = (r_sum * mul_sum) >> shg_sum;
                    pixels[p + 1] = (g_sum * mul_sum) >> shg_sum;
                    pixels[p + 2] = (b_sum * mul_sum) >> shg_sum;

                    r_sum -= r_out_sum;
                    g_sum -= g_out_sum;
                    b_sum -= b_out_sum;

                    r_out_sum -= stackIn.r;
                    g_out_sum -= stackIn.g;
                    b_out_sum -= stackIn.b;

                    p = (x + (((p = y + radiusPlus1) < heightMinus1 ? p : heightMinus1) * width)) << 2;

                    r_sum += (r_in_sum += (stackIn.r = pixels[p]));
                    g_sum += (g_in_sum += (stackIn.g = pixels[p + 1]));
                    b_sum += (b_in_sum += (stackIn.b = pixels[p + 2]));

                    stackIn = stackIn.next;

                    r_out_sum += (pr = stackOut.r);
                    g_out_sum += (pg = stackOut.g);
                    b_out_sum += (pb = stackOut.b);

                    r_in_sum -= pr;
                    g_in_sum -= pg;
                    b_in_sum -= pb;

                    stackOut = stackOut.next;

                    yi += width;
                }
            }

            radius *= increaseFactor;
            for (i = wh; --i > -1; )
            {
                int idx = i << 2;
                float lookupValue = (float) ((radiusPixels[idx + 2] & 0xff) / 255.0 * blurLevels);
                int index = (int) lookupValue /*| 0*/;
                if (index == currentIndex)
                {
                    float blend = (float) (256.0 * (lookupValue - (int)lookupValue));
                    float iblend = 256 - blend;

                    imagePixels[idx] = (int)(imagePixels[idx] * iblend + pixels[idx] * blend) >> 8;
                    imagePixels[idx + 1] = (int)(imagePixels[idx + 1] * iblend + pixels[idx + 1] * blend) >> 8;
                    imagePixels[idx + 2] = (int)(imagePixels[idx + 2] * iblend + pixels[idx + 2] * blend) >> 8;
                }
                else if (index == currentIndex + 1)
                {
                    imagePixels[idx] = pixels[idx];
                    imagePixels[idx + 1] = pixels[idx + 1];
                    imagePixels[idx + 2] = pixels[idx + 2];
                }
            }
            currentIndex++;
        }
        ////////////////////
        arrayToBitmap(imagePixels, dstMat);
    }

    private void arrayToBitmap(int[] imagePixels, Bitmap dstMat) {
        int ii = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                dstMat.setPixel(x,y,Color.rgb(imagePixels[ii],imagePixels[ii+1],imagePixels[ii+2]));
                ii += 4;
            }
        }
    }

    void bitmapToArray(Bitmap b,int[] a){
        int ii = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = b.getPixel(x,y);
                a[ii] = Color.red(color);
                a[ii + 1] = Color.green(color);
                a[ii + 2] = Color.blue(color);
                ii += 4;
            }
        }
    }

    class BlurStack {
        public int r, g, b, a;
        BlurStack next;
        BlurStack() {
            r = g = b = a = 0;
            next = null;
        }
    }
}
