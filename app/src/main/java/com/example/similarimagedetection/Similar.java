package com.example.similarimagedetection;

import android.graphics.Bitmap;

public class Similar {
    private Bitmap img;
    private String filePath;

    public Similar(Bitmap img,String filePath) {
        this.img=img;
        this.filePath=filePath;
    }

    public Bitmap getImg() {
        return img;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setImg(Bitmap img) {
        this.img=img;
    }

    public void setFilePath(String filePath) {
        this.filePath=filePath;
    }
}
