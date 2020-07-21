package com.example.similarimagedetection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Similar> datas;
    final int position = 0;
    private boolean isShowDelete;

    public GridAdapter(Context context, List<Similar> datas) {
        this.context = context;
        this.datas = datas;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Similar similar = (Similar) getItem(i);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            //view= LayoutInflater.from(context).inflate(R.layout.similar_item,null);
            view = View.inflate(context, R.layout.similar_item, null);
            viewHolder = new ViewHolder();
            viewHolder.photo = (ImageView) view.findViewById(R.id.img);
            viewHolder.deleteImage = (ImageView) view.findViewById(R.id.deleteButton);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.photo.setImageBitmap(similar.getImg());
        viewHolder.deleteImage.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
        if (isShowDelete) {
            viewHolder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String filePath=datas.get(position).getFilePath();
                    datas.remove(position);
                    delete(filePath);
                    setIsShowDelete(false);
                }
            });
        }
        return view;
    }

    public boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            Log.e("--Method--", "Fail to delete file:" + delFile + " not exist!");
            return false;
        } else {
            return deleteSingleFile(delFile);
        }
    }

    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: " + filePath$Name + "succeed!");
                return true;
            } else {
                Log.e("--Method--", "Delete file " + filePath$Name + "failed!");
                return false;
            }
        } else {
            Log.e("--Method--", "Fail to delete folder " + filePath$Name + "not existed!");
            return false;
        }
    }

    public class ViewHolder {
        ImageView photo, deleteImage;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }
}
