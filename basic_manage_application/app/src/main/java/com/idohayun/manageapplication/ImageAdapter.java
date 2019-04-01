//package com.idohayun.uploadtogallery;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ImageAdapter extends ArrayAdapter {
//    private static final String TAG = "ImageAdapter";
//    private final int layoutResource;
//    private final LayoutInflater layoutInflater;
//    private ArrayList<String> images;
//
//    public ImageAdapter(Context context, int resource, ArrayList<String> images) {
//        super(context, resource);
//        this.layoutResource = resource;
//        this.layoutInflater = LayoutInflater.from(context);
//        this.images = images;
//    }
//
//    @Override
//    public int getCount() { return images.size(); }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(layoutResource, parent, false);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        String currentImageURL = images.get(position);
//        Picasso.get().load(currentImageURL).into(viewHolder.imageURL);
//
//        return convertView;
//    }
//
//    private class ViewHolder {
//        public ImageView imageURL;
//
////        ViewHolder(View v) {
////            this.imageURL = v.findViewById(R.id.picture_small);
////        }
//    }
//}