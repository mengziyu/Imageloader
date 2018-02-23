package com.ziyu.imageloader;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private String[] data;
    private Context mContext;
    public ImageAdapter(String[] data, Context context){
        this.data=data;
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Imageload.getInstance(mContext).load(data[position],holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.iv);
        }
    }
}
