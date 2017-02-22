package com.huylv.gradientblurandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.opencv.core.CvType.CV_8UC1;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    static {
        System.loadLibrary("blurring");
        OpenCVLoader.initDebug();
    }

    @BindView(R.id.tvRotation)
    TextView tvRotation;
    @BindView(R.id.tvCenterX)
    TextView tvCenterX;
    @BindView(R.id.tvCenterY)
    TextView tvCenterY;
    @BindView(R.id.tvRadius)
    TextView tvRadius;
    @BindView(R.id.tvDistance1)
    TextView tvDistance1;
    @BindView(R.id.tvDistance2)
    TextView tvDistance2;


    public static native int getint();

    public static native void gradientBlur(long srcAddress, long dstAddress, long gradientAddress, int radius);

    public static native void createLinearGradientMat(long matAddress, int centerx, int centery, int distance1, int distance2, int angle);

    @BindView(R.id.ivGradientImage)
    ImageView ivGradientImage;
    @BindView(R.id.srcImage)
    ImageView ivSrcImage;
    @BindView(R.id.sbRotation)
    SeekBar sbRotation;
    @BindView(R.id.sbCenterX)
    SeekBar sbCenterX;
    @BindView(R.id.sbCenterY)
    SeekBar sbCenterY;
    @BindView(R.id.sbRadius)
    SeekBar sbRadius;
    @BindView(R.id.sbDistance1)
    SeekBar sbDistance1;
    @BindView(R.id.sbDistance2)
    SeekBar sbDistance2;

    boolean showingFinalImage = true;
    Mat srcMat, dstMat, gradientMat;
    Bitmap srcBm, dstBm, graBm;
    int centerx, centery, distance1, distance2, angle, blurRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        centerx = 0;
        centery = 0;
        distance1 = 100;
        distance2 = 50;
        angle = 135;
        blurRadius = 40;

        srcBm = BitmapFactory.decodeResource(getResources(), R.mipmap.s1);
        int width = srcBm.getWidth();
        int height = srcBm.getHeight();
        graBm = srcBm.copy(srcBm.getConfig(), true);
        dstBm = srcBm.copy(srcBm.getConfig(), true);

        gradientMat = new Mat(height, width, CV_8UC1);
        createLinearGradientMat(gradientMat.getNativeObjAddr(), centerx, centery, distance1, distance2, angle);
        Utils.matToBitmap(gradientMat, graBm);

        ivGradientImage.setImageBitmap(graBm);

        srcMat = new Mat();
        dstMat = new Mat();
        Utils.bitmapToMat(srcBm, srcMat);
        srcMat.copyTo(dstMat);

        gradientBlur(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr(), gradientMat.getNativeObjAddr(), blurRadius);
        Utils.matToBitmap(dstMat, dstBm);
        ivSrcImage.setImageBitmap(dstBm);
        sbCenterX.setOnSeekBarChangeListener(this);
        sbCenterY.setOnSeekBarChangeListener(this);
        sbDistance1.setOnSeekBarChangeListener(this);
        sbDistance2.setOnSeekBarChangeListener(this);
        sbRadius.setOnSeekBarChangeListener(this);
        sbRotation.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sbRotation:
                angle = sbRotation.getProgress();
                reDrawGradient();
                break;
            case R.id.sbCenterX:
                centerx = sbCenterX.getProgress();
                reDrawGradient();
                break;
            case R.id.sbCenterY:
                centery = sbCenterY.getProgress();
                reDrawGradient();
                break;
            case R.id.sbDistance1:
                distance1 = sbDistance1.getProgress();
                reDrawGradient();
                break;
            case R.id.sbDistance2:
                distance2 = sbDistance2.getProgress();
                reDrawGradient();
                break;
            case R.id.sbRadius:
                blurRadius = sbRadius.getProgress();
                gradientBlur(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr(), gradientMat.getNativeObjAddr(), blurRadius);
                Utils.matToBitmap(dstMat, dstBm);
                ivSrcImage.setImageBitmap(dstBm);
                break;
        }
    }

    void reDrawGradient() {
        createLinearGradientMat(gradientMat.getNativeObjAddr(), centerx, centery, distance1, distance2, angle);
        gradientBlur(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr(), gradientMat.getNativeObjAddr(), blurRadius);
        Utils.matToBitmap(dstMat, dstBm);
        ivSrcImage.setImageBitmap(dstBm);
        Utils.matToBitmap(gradientMat, graBm);
        ivGradientImage.setImageBitmap(graBm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (showingFinalImage) {
            ivSrcImage.setVisibility(View.INVISIBLE);
            showingFinalImage = false;
            item.setTitle("Show");
        } else {
            ivSrcImage.setVisibility(View.VISIBLE);
            showingFinalImage = true;
            item.setTitle("Hide");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbRotation:
                tvRotation.setText(getString(R.string.rotation)+progress);
                break;
            case R.id.sbCenterX:
                tvCenterX.setText(getString(R.string.centerx)+progress);
                break;
            case R.id.sbCenterY:
                tvCenterY.setText(getString(R.string.centery)+progress);
                break;
            case R.id.sbDistance1:
                tvDistance1.setText(getString(R.string.distance1)+progress);
                break;
            case R.id.sbDistance2:
                tvDistance2.setText(getString(R.string.distance2)+progress);
                break;
            case R.id.sbRadius:
                tvRadius.setText(getString(R.string.radius)+progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


}
