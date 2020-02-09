package com.example.lenovo.recognition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private Context mContext;
    private List<Picture> pictureList = new ArrayList<>();

    public PictureAdapter(List<Picture> pictureList){
        this.pictureList = pictureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picture picture= pictureList.get(i);
        Log.e("TAG", "p" + picture.getBitmap());
        viewHolder.pictureimage.setImageBitmap(picture.getBitmap());
        viewHolder.identity_title.setText(picture.getTitle());
        viewHolder.identity_time.setText(picture.getTime());
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pictureimage;
        TextView identity_title,identity_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureimage = itemView.findViewById(R.id.identity_image);
            identity_title = itemView.findViewById(R.id.identity_title);
            identity_time = itemView.findViewById(R.id.identity_time);
        }
    }
}
