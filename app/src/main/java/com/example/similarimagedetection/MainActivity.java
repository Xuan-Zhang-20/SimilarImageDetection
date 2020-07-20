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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        folders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        folders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        folders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        for (int i = 0; i < folders.size(); ++i) {
            File file = new File(folders.get(i));
            File[] files = getFiles(file);
            if (files == null) {
                Log.e("error", "空目录");
            } else {

                for (int j = 0; j < files.length; ++j) {
                    if (files[j].getAbsolutePath().endsWith(".jpg") ||
                            files[j].getAbsolutePath().endsWith(".png") ||
                            files[j].getAbsolutePath().endsWith(".bmp") ||
                            files[j].getAbsolutePath().endsWith(".jpeg"))
                        picPaths.add(files[j].getAbsolutePath());
                }
            }
        }
        String msg = "found " + picPaths.size() + " pictures";
        Log.d("TAG", msg);

        for (int i = 0; i < picPaths.size(); ++i) {
            int[] source = getPicCode(picPaths.get(i));
            List<String> similarList = new ArrayList<>();
            similarList.add(picPaths.get(i));
            for (int j = i + 1; j < picPaths.size(); ++j) {
                int[] cmp = getPicCode(picPaths.get(j));
                if (comparePicture(source, cmp)) {
                    similarList.add(picPaths.get(j));
                    picPaths.remove(j);
                }
            }
            if (similarList.size() > 1)
                similarPics.add(similarList);
        }
        LinearLayout linear = super.findViewById(R.id.photoLinear);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < similarPics.size(); ++i) {
            TextView text = new TextView(this);
            File f = new File(similarPics.get(i).get(0));
            String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(f.lastModified()));
            text.setText(time);
            linear.addView(text);
            MyGridView gridView=new MyGridView(this);
            for(int j=0;j<similarPics.get(i).size();++j) {
                ImageView image=new ImageView(this);
                image.setImageBitmap(getPicThumb(similarPics.get(i).get(j)));
                gridView.addView(image);
            }
            linear.addView(gridView);
        }
    }

    public File[] getFiles(File directory){
        List<File> files=new ArrayList<>();
        File[] temp=directory.listFiles();
        if(temp != null) {
            for(int i=0;i<temp.length;++i) {
                if(temp[i].isFile()) {
                    files.add(temp[i]);
                }
                else  {
                    File[] child=getFiles(temp[i]);
                    if(child != null) {
                        for(int j=0;j<child.length;++j) {
                            files.add(child[j]);
                        }
                    }
                }
            }
            return files.toArray(new File[files.size()]);
        }
        else return null;
    }

    public Bitmap getPicThumb(String picPath) {
        Bitmap bitmap = null;
        File photo = new File(picPath);
        Uri photoUri = Uri.fromFile(photo);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    public int[] getPicCode(String picPath) {
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