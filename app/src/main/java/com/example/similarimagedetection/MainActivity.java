package com.example.similarimagedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
    private GridView gridView;
    private GridAdapter gridAdapter;
    private boolean isShowDelete;
    private List<Similar> datas=new ArrayList<>();

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
        picPaths.clear();
        similarPics.clear();
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
            Log.d("TAG","Source code: "+source.hashCode());
            List<String> similarList = new ArrayList<>();
            similarList.add(picPaths.get(i));
            int n=picPaths.size();
            for (int j = i + 1; j < n; ++j) {
                int[] cmp = getPicCode(picPaths.get(j));
                Log.d("TAG","Dest code: "+cmp.hashCode());
                if (comparePicture(source, cmp)) {
                    similarList.add(picPaths.get(j));
                    picPaths.remove(j);
                    n=picPaths.size();
                }
            }
            if (similarList.size() > 0)
                similarPics.add(similarList);
        }

        datas.clear();
        if(similarPics.size()>0) {
            for(int i=0;i<similarPics.size();++i) {
                for(int j=0;j<similarPics.get(i).size();++j) {
                    Similar pic=new Similar(getPicThumb(similarPics.get(i).get(j)),similarPics.get(i).get(j));
                    datas.add(pic);
                }
            }
        }
        Log.d("TAG","We have "+datas.size()+" data");

        gridView=(GridView)findViewById(R.id.photoView);
        gridAdapter=new GridAdapter(this,datas);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isShowDelete) {
                    isShowDelete=false;
                } else {
                    isShowDelete=true;
                }
                gridAdapter.setIsShowDelete(isShowDelete);
                return false;
            }
        });
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

    public static Bitmap getPicThumb(String imagePath) {
        int width=150;
        int height=150;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public int[] getPicCode(String picPath) {
        Bitmap bitmap = null;
        Bitmap scaled = null;
        int width=8;
        int height=8;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(picPath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(picPath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        float scale_width = 8.0f / bitmap.getWidth();
        float scale_height = 8.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale_width, scale_height);
        scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
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
        return different < 11;
    }
}