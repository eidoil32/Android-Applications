package com.idohayun.mybusiness;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageAdapter extends BaseAdapter {
    private static boolean state;
    private static final String TAG = "ImageAdapter";
    private static List<ImageURL> images;
    private static Context context;
    private SquareImageView view;

    @Override
    public int getCount() {
        return images.size();
    }

    public ImageAdapter(Context context, List<ImageURL> images) {
        this.context = context;
        this.images = images;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final String url = getItem(position).getUrl();

        view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        String thumbnailUrl = "http://eidoil32.myhf.in/uploadedFiles/" + createThumbnailURL(url) + "_resized.jpg";

        Picasso.get() //
                .load(thumbnailUrl) //
                .placeholder(R.drawable.wait) //
                .error(R.drawable.image_small_not_found) //
                .fit()
                .transform(new RoundedCornersTransform())
                .tag(context) //
                .centerCrop()
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = View.inflate(context,R.layout.dialog_enlarge_image,null);
                PhotoView photoView = mView.findViewById(R.id.big_image);
                Picasso.get()
                        .load(url)
                        .into(photoView);
                mBuilder.setView(mView);
                final AlertDialog  dialog = mBuilder.create();
                ImageView close = (ImageView) mView.findViewById(R.id.dialog_enlarge_image_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics metrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(metrics);
                int height = metrics.heightPixels - (int)(50*metrics.density);
                int width = metrics.widthPixels;

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = MinOfTwo(width,getItem(position).getWidth());
                layoutParams.height = MinOfTwo(height,getItem(position).getHeight());
                layoutParams.dimAmount = 0.5f;
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });

        return view;
    }

    private int MinOfTwo(int one, int two) {
        if(one < two) return one;
        else return two;
    }

    private String createThumbnailURL(String url) {
        String temp;
        String[] tempArray, getName = null;
        int index = -1;

        tempArray = url.split("/");

        for (int i = 0; i < tempArray.length; i++) {
            if(tempArray[i].contains(".png")) {
                getName = tempArray[i].split(".png");
            }
        }

        return getName[0];
    }

    @Override
    public ImageURL getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
