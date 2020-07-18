package com.example.similarimagedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<String> picPaths= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getPicList(View v) {
        List<String> folders= new ArrayList<>();
        folders.add(Environment.DIRECTORY_DCIM);
        folders.add(Environment.DIRECTORY_DOWNLOADS);
        folders.add(Environment.DIRECTORY_PICTURES);
        String[] folderArray= (String[]) folders.toArray();
        for (int i=0;i<folders.size();++i) {
            File file=new File(folderArray[i]);
            File[] files=file.listFiles();
            if (files == null) {
                Log.e("error", "空目录");
                break;
            }
            for(int j=0;j<files.length;++j) {
                if(files[i].getAbsolutePath().endsWith(".jpg") ||
                        files[i].getAbsolutePath().endsWith(".png") ||
                        files[i].getAbsolutePath().endsWith(".bmp") ||
                        files[i].getAbsolutePath().endsWith(".jpeg"))
                    picPaths.add(files[i].getAbsolutePath());
            }
        }
    }
}