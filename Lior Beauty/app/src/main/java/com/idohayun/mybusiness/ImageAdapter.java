package com.idohayun.mybusiness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.constraint.Guideline;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageAdapter extends BaseAdapter {
    private static final String TAG = "ImageAdapter";
    private List<ImageURL> images;
    private Context context;
    private RequestQueue queue;
    private JsonObjectRequest request;

    @Override
    public int getCount() {
        return images.size();
    }

    ImageAdapter(Context context, List<ImageURL> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final String url = getItem(position).getUrl();

        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        switch (url) {
            case "add_new":
                Log.d(TAG, "getView: add new");
                Picasso.get()
                        .load(R.drawable.add_new)
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
                        Log.d(TAG, "onClick: add new picture");
                        Intent intent = new Intent(context, ManageGalleryAddNew.class);
                        context.startActivity(intent);
                    }
                });
                break;
            case "empty":
                Log.d(TAG, "getView: empty");
                break;
            default:
                String thumbnailUrl = ServerURLSManager.Images_based_uploaded_folder + createThumbnailURL(url) + "_resized.jpg";

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
                        View mView = View.inflate(context, R.layout.dialog_enlarge_image, null);
                        PhotoView photoView = mView.findViewById(R.id.big_image);
                        Picasso.get()
                                .load(url)
                                .into(photoView);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        ImageView close = mView.findViewById(R.id.dialog_enlarge_image_close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics metrics = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(metrics);
                        int height = metrics.heightPixels - (int) (50 * metrics.density);
                        int width = metrics.widthPixels;

                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                        layoutParams.width = MinOfTwo(width, getItem(position).getWidth());
                        layoutParams.height = MinOfTwo(height, getItem(position).getHeight());
                        layoutParams.dimAmount = 0.5f;
                        dialog.show();
                        dialog.getWindow().setAttributes(layoutParams);
                        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    }
                });

                baseUSER user = new baseUSER();
                user.getUserDetails(view);

                if (user.getId() == 1) {
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, "Long press !", Toast.LENGTH_SHORT).show();
                            final Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.popup_simple_yes_or_no);

                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int displayWidth = displayMetrics.widthPixels;
                            int displayHeight = displayMetrics.heightPixels;
                            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                            layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                            int orientation = v.getContext().getResources().getConfiguration().orientation;
                            float multiple_Width = 0.7f, multiple_Height = 0.3f;
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                Log.d(TAG, "Portrait");
                                multiple_Width = 0.8f;
                                multiple_Height = 0.3f;
                            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                Log.d(TAG, "Landscape");
                                multiple_Width = 0.7f;
                                multiple_Height = 0.6f;
                            }

                            int dialogWindowWidth = (int) (displayWidth * multiple_Width);
                            int dialogWindowHeight = (int) (displayHeight * multiple_Height);

                            Guideline guideline_left, guideline_right, guideline_bottom;
                            guideline_left = dialog.findViewById(R.id.popup_guideline_left);
                            guideline_bottom = dialog.findViewById(R.id.popup_guideline_bottom);
                            guideline_right = dialog.findViewById(R.id.popup_guideline_right);

                            guideline_left.setGuidelineBegin(0);
                            guideline_right.setGuidelineBegin(dialogWindowWidth - (int) (30 * displayMetrics.density));
                            guideline_bottom.setGuidelineBegin(dialogWindowHeight - (int) (30 * displayMetrics.density));

                            layoutParams.width = dialogWindowWidth;
                            layoutParams.height = dialogWindowHeight;

                            TextView alertText = dialog.findViewById(R.id.popup_alert_text);
                            alertText.setText(R.string.popup_delete_image);

                            Button btnYes = dialog.findViewById(R.id.btn_popup_yes);
                            Button btnNo = dialog.findViewById(R.id.btn_popup_no);

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
                                    images.remove(position);
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
                                            ServerURLSManager.Images_delete_image, jsonObject,
                                            new Response.Listener<JSONObject>() { // the response listener
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        if (response.getString("status").equals("true")) {
                                                            CustomToast.showToast(context, context.getString(R.string.file_deleted_successfully), 1);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() { // the error listener
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d(TAG, "onErrorResponse: " + error.toString());
                                                }
                                            });

                                    queue.add(request);
                                    v.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(TAG, "run: here");

                                            notifyDataSetChanged();
                                        }
                                    }, 1000);
                                    dialog.cancel();
                                }
                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(layoutParams);
                            return true;
                        }
                    });
                }
                break;
        }

        return view;
    }

    private int MinOfTwo(int one, int two) {
        if(one < two) return one;
        else return two;
    }

    private String createThumbnailURL(String url) {
        String[] tempArray, getName = null;

        tempArray = url.split("/");

        for (String aTempArray : tempArray) {
            if (aTempArray.contains(".png")) {
                getName = aTempArray.split(".png");
            }
        }

        assert getName != null;
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
