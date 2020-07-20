package com.example.similarimagedetection;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Similar> datas;
    final int position=0;
    private boolean isShowDelete;

    public GridAdapter(Context context,List<Similar> datas) {
        this.context=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Similar similar=(Similar) getItem(i);
        View thisView;
        ViewHolder viewHolder;
        if(view==null) {
            thisView= LayoutInflater.from(context).inflate(R.layout.photolayout,null);
            viewHolder=new ViewHolder();
            viewHolder.photo=(ImageView)view.findViewById(R.id.img);
            viewHolder.deleteImage=(ImageView)view.findViewById(R.id.deleteButton);
            thisView.setTag(viewHolder);
        } else {
            thisView=view;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.photo.setImageBitmap(similar.getImg());
        viewHolder.deleteImage.setVisibility(isShowDelete ? View.VISIBLE: View.GONE);
        if(isShowDelete) {
            viewHolder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datas.remove(position);
                    setIsShowDelete(false);
                }
            });
        }
        return thisView;
    }
    class ViewHolder {
        ImageView photo,deleteImage;
    }
    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }
}
