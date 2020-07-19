package com.example.similarimagedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<String> picPaths = new ArrayList<>();
    public List<List<String>> similarPics= new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getPicList(View v) {
        List<String> folders = new ArrayList<>();
        folders.add(Environment.DIRECTORY_DCIM);
        folders.add(Environment.DIRECTORY_DOWNLOADS);
        folders.add(Environment.DIRECTORY_PICTURES);
        for (int i = 0; i < folders.size(); ++i) {
            File file = new File(folders.get(i));
            File[] files = file.listFiles();
            if (files == null) {
                Log.e("error", "空目录");
            } else {
                for (int j = 0; j < files.length; ++j) {
                    if (files[i].getAbsolutePath().endsWith(".jpg") ||
                            files[i].getAbsolutePath().endsWith(".png") ||
                            files[i].getAbsolutePath().endsWith(".bmp") ||
                            files[i].getAbsolutePath().endsWith(".jpeg"))
                        picPaths.add(files[i].getAbsolutePath());
                }
            }
        }
        String msg = "found " + picPaths.size() + " pictures";
        Log.d("TAG", msg);

        for (int i = 0; i < picPaths.size(); ++i) {
            int[] source = getPicThumb(picPaths.get(i));
            List<String> similarList = new ArrayList<>();
            similarList.add(picPaths.get(i));
            for (int j = i + 1; j < picPaths.size(); ++j) {
                int[] cmp = getPicThumb(picPaths.get(j));
                if (comparePicture(source, cmp)) {
                    similarList.add(picPaths.get(j));
                    picPaths.remove(j);
                }
            }
            if (similarList.size() > 1)
                similarPics.add(similarList);
        }
        for(int i=0;i<similarPics.size();++i) {

        }
    }

    public int[] getPicThumb(String picPath) {
        Bitmap bitmap = null;
        Bitmap scaled = null;
        File photo = new File(picPath);
        Uri photoUri = Uri.fromFile(photo);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        float scale_width = 8.0f / bitmap.getWidth();
        float scale_height = 8.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale_width, scale_height);
        scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        int width = 8, height = 8;
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int red = (scaled.getPixel(j, i) >> 16) & 0xFF;
                int green = (scaled.getPixel(j, i) >> 8) & 0xFF;
                int blue = (scaled.getPixel(j, i)) & 0xFF;
                pixels[i][j] = 0.3 * red + 0.59 * green + 0.11 * blue;
            }
        }
        double mean = 0.0;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                mean += pixels[i][j];
            }
        }
        mean /= 64;
        int[] hash = new int[width * height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (pixels[i][j] >= mean)
                    hash[i * width + j] = 1;
                else
                    hash[i * width + j] = 0;
            }
        }
        return hash;
    }

    public boolean comparePicture(int[] source, int[] dest) {
        int different = 0;
        for (int i = 0; i < source.length; ++i) {
           if(source[i]!=dest[i]) {
               ++different;
           }
        }
        return different < 5;
    }
}