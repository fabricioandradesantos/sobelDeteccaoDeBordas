package com.example.sobeldeteccaodebordas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class Main2Activity extends AppCompatActivity {

    private ImageView imagemOriginal;
    private ImageView imagemResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        OpenCVLoader.initDebug();
        imagemOriginal = (ImageView) findViewById(R.id.imageID);
        imagemResult   = (ImageView) findViewById(R.id.image1ID);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("key");
            Uri uri = Uri.parse(value);

            Bitmap bitmapOriginal = null;

            try {
                bitmapOriginal = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imagemOriginal.setImageBitmap(resizeImage(Main2Activity.this, bitmapOriginal, 400,400));

            Bitmap bitmap = ((BitmapDrawable)imagemOriginal.getDrawable()).getBitmap();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            Size size = new Size(7,7);

            Mat blurredImage = new Mat();
            Imgproc.GaussianBlur(mat, blurredImage, size, 0,0);

            Mat gray = new Mat();
            Imgproc.cvtColor(blurredImage, gray,Imgproc.COLOR_RGB2GRAY);

            
            Mat grad_x = new Mat();
            Imgproc.Sobel(gray, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);

            
            Mat grad_y = new Mat();
            Imgproc.Sobel(gray, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);

            
            Mat abs_grad_x = new Mat();
            Mat abs_grad_y = new Mat();
            Core.convertScaleAbs(grad_x, abs_grad_x);
            Core.convertScaleAbs(grad_y, abs_grad_y);

            
            Mat sobel = new Mat();
            Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

            Utils.matToBitmap(sobel, bitmapResult);

            imagemResult.setImageBitmap(resizeImage(Main2Activity.this,bitmapResult,400,400));

        }

    }

    public Bitmap resizeImage(Context context, Bitmap bmpOriginal,
                              float newWidth, float newHeight) {

        Bitmap novoBmp = null;

        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();

        float densityFactor = context.getResources().getDisplayMetrics().density;
        float novoW = newWidth * densityFactor;
        float novoH = newHeight * densityFactor;

        float scalaW = novoW / w;
        float scalaH = novoH / h;

        Matrix matrix = new Matrix();
        matrix.postScale(scalaW, scalaH);
        novoBmp = Bitmap.createBitmap(bmpOriginal, 0, 0, w, h, matrix, true);

        return novoBmp;
    }


    public  void onBackPressed(){
        finish();
        super.onBackPressed();
    }
}
