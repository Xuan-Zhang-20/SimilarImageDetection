package com.example.similarimagedetection;

import android.graphics.Bitmap;

public class Similar {
    private Bitmap img;

    public Similar(Bitmap img) {
        this.img=img;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img=img;
    }
}
