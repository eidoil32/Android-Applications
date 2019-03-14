package com.idohayun.manageapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class SampleGridViewAdapter extends BaseAdapter {
    private static final String TAG = "SampleGridViewAdapter";
    private final Context context;
    private ArrayList<String> urls;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private String updateUrl = "http://eidoil32.myhf.in/deleteFileFromServer.php";

    public SampleGridViewAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.urls = images;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        final String url = getItem(position);

        Picasso.get() //
                .load(url) //
                .placeholder(R.drawable.wait) //
                .error(R.drawable.placeholder) //
                .fit()
                .transform(new RoundedCornersTransform())
                .tag(context) //
                .centerCrop()
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "click on image no " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BigImageGallery.class);
                Bundle b = new Bundle();
                b.putString("IMAGEURL", url);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(context, "Long press !", Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.popup_delete_image);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int displayWidth = displayMetrics.widthPixels;
                int displayHeight = displayMetrics.heightPixels;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                int orientation = v.getContext().getResources().getConfiguration().orientation;
                float multiple_Width = 0.7f, multiple_Height = 0.3f;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.d(TAG, "Portrait");
                    multiple_Width = 0.7f;
                    multiple_Height = 0.3f;
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d(TAG, "Landscape");
                    multiple_Width = 0.7f;
                    multiple_Height = 0.6f;
                }
                int dialogWindowWidth = (int) (displayWidth * multiple_Width);
                int dialogWindowHeight = (int) (displayHeight * multiple_Height);
                layoutParams.width = dialogWindowWidth;
                layoutParams.height = dialogWindowHeight;

                Button btnYes = (Button) dialog.findViewById(R.id.button_yes_image);
                Button btnNo = (Button) dialog.findViewById(R.id.button_no_image);

                btnNo.setEnabled(true);
                btnYes.setEnabled(true);

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        urls.remove(position);
                        queue = Volley.newRequestQueue(v.getContext());
                        String filename;
                        Map<String, String> map = new HashMap<>();
                        if (url.contains("/")) {
                            String[] parts = url.split("/");
                            filename = parts[4];
                        } else {
                            throw new IllegalArgumentException("String " + url + " does not contain '/'");
                        }
                        map.put("FileTarget", filename);
                        Log.d(TAG, "onClick: " + filename);
                        final JSONObject jsonObject = new JSONObject(map);
                        request = new JsonObjectRequest(
                                Request.Method.POST, // the request method
                                updateUrl, jsonObject,
                                new Response.Listener<JSONObject>() { // the response listener
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.getString("status").equals("true")) {
                                                Toast.makeText(context, context.getString(R.string.file_deleted_successfully), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() { // the error listener
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "onErrorResponse: " + error.toString());;
                                    }
                                });

                        queue.add(request);
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: here");

                                notifyDataSetChanged();
                            }
                        },1000);
                        dialog.cancel();
                    }
                });

                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
                return true;

            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}