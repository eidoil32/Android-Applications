package com.idohayun.manageapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.location.Location;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private final int hourInMilli = 60*60*1000;
    private ListView listView;
    private List<DateListArray> application;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private Map<String, String> map = new HashMap<String, String>();
    private String updateUrl = "http://example.com/file", downloadURL = "http://example.com/file";

    public FeedAdapter(Context context, int resource, List<DateListArray> application, ListView listView) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.application = application;
        this.listView = listView;
    }


    public FeedAdapter(Context context, int resource, List<DateListArray> application) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.application = application;
    }

    @Override
    public int getCount() {
        return application.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final DateListArray currentDate = application.get(position);
        viewHolder.textDate.setText(currentDate.getDay() + "-" + currentDate.getMonth() + "-" + currentDate.getYear());
        String minutes;
        if (currentDate.getMin() == 0) minutes = "00";
        else minutes = Integer.toString(currentDate.getMin());
        viewHolder.textTime.setText(currentDate.getHour() + "-" + minutes);

        viewHolder.exportEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = new Location("MyAddress");
                location.setLatitude(31.941525);
                location.setLongitude(34.841592);
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(currentDate.getYear(), currentDate.getMonth(), currentDate.getDay(), currentDate.getHour(), currentDate.getMin());
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,beginTime.getTimeInMillis() - hourInMilli);
                intent.putExtra("beginTime", beginTime.getTimeInMillis());
                intent.putExtra("endTime", cal.getTimeInMillis() + hourInMilli);
                intent.putExtra("title", currentDate.getType());
                intent.putExtra(CalendarContract.Reminders.MINUTES, 60);
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION,"address");
                layoutInflater.getContext().startActivity(intent);
            }
        });

        if (!currentDate.isAvailable()) {
            viewHolder.optionDel.setVisibility(View.VISIBLE);
            viewHolder.textName.setVisibility(View.VISIBLE);
            viewHolder.textType.setVisibility(View.VISIBLE);
            if(currentDate.getPhone() != 0 )
            {
                viewHolder.textPhone.setVisibility(View.VISIBLE);
                viewHolder.textPhone.setText(Integer.toString(currentDate.getPhone()));
                viewHolder.textPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:0" + currentDate.getPhone()));
                        v.getContext().startActivity(intent);
                    }
                });
            }

            viewHolder.textName.setText(currentDate.getName());
            viewHolder.textType.setText(currentDate.getType());

            viewHolder.optionDel.setVisibility(View.VISIBLE);
            viewHolder.optionDel.setText(getContext().getString(R.string.btn_date_delete));

            viewHolder.optionDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queue = Volley.newRequestQueue(getContext());
                    Log.d(TAG, "onClick: DEL button clicked!");
                    currentDate.resetDate();
                    map.put("TypeOfJSON","Delete");
                    map.put("PersonID", Integer.toString(currentDate.getPersonID()));
                    map.put("Day", Integer.toString(currentDate.getDay()));
                    map.put("Month", Integer.toString(currentDate.getMonth()));
                    map.put("Year", Integer.toString(currentDate.getYear()));
                    map.put("Hour", Integer.toString(currentDate.getHour()));
                    map.put("Min", Integer.toString(currentDate.getMin()));
                    map.put("FullName", currentDate.getName());
                    map.put("Type", currentDate.getType());
                    map.put("Available", "TRUE");
                    map.put("Phone", Integer.toString(currentDate.getPhone()));
                    Log.d(TAG, "onClick: " + map.toString());
                    final JSONObject jsonObject = new JSONObject(map);

                    request = new JsonObjectRequest(
                            Request.Method.POST, // the request method
                            updateUrl, jsonObject,
                            new Response.Listener<JSONObject>() { // the response listener
                                @Override
                                public void onResponse(JSONObject response){
                                    try {
                                        if(response.getString("status").equals("true")) {
                                            Toast.makeText(getContext(),getContext().getString(R.string.appointment_deleted),Toast.LENGTH_SHORT).show();
                                            getInformationToListview getInformationToListview = new getInformationToListview(
                                                    downloadURL,listView,
                                                    currentDate.getDay(),currentDate.getMonth(),currentDate.getYear(),getContext());
                                            getInformationToListview.getJSON();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() { // the error listener
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(),"Oops! Got error from server!",Toast.LENGTH_SHORT).show();
                                }
                            });

                    queue.add(request);
                }
            });
        } else { //is available
            viewHolder.optionCatch.setVisibility(View.VISIBLE);
            viewHolder.optionCatch.setText(getContext().getString(R.string.btn_date_catch));

            viewHolder.optionCatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CatchPopupMenu(currentDate);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    private void CatchPopupMenu(final DateListArray catchDate) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_catch_menu);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int orientation = getContext().getResources().getConfiguration().orientation;
        float multiple_Width = 0.7f, multiple_Height = 0.3f;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "Portrait");
            multiple_Width = 0.7f;
            multiple_Height = 0.3f;
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "Landscape");
            multiple_Width = 0.7f;
            multiple_Height = 0.6f;
        }
        int dialogWindowWidth = (int)(displayWidth * multiple_Width);
        int dialogWindowHeight = (int)(displayHeight * multiple_Height);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        Button btnYes = (Button)dialog.findViewById(R.id.button_yes);
        Button btnNo = (Button)dialog.findViewById(R.id.button_no);

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
                queue = Volley.newRequestQueue(getContext());
                Log.d(TAG, "onClick: Catch button clicked!");
                Map<String,String> tempMap = new HashMap<String, String>();
                tempMap.put("TypeOfJSON","Catch");
                tempMap.put("PersonID", Integer.toString(catchDate.getPersonID()));
                tempMap.put("Day", Integer.toString(catchDate.getDay()));
                tempMap.put("Month", Integer.toString(catchDate.getMonth()));
                tempMap.put("Year", Integer.toString(catchDate.getYear()));
                tempMap.put("Hour", Integer.toString(catchDate.getHour()));
                tempMap.put("Min", Integer.toString(catchDate.getMin()));
                tempMap.put("Available","FALSE");
                final JSONObject jsonObject = new JSONObject(tempMap);
                request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        updateUrl, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    if(response.getString("status").equals("true")) {
                                        Toast.makeText(getContext(),getContext().getString(R.string.appointment_catch),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() { // the error listener
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(),"Oops! Got error from server!",Toast.LENGTH_SHORT).show();
                            }
                        });

                queue.add(request);
                dialog.cancel();
                getInformationToListview getInformationToListview = new getInformationToListview(
                        downloadURL,listView,
                        catchDate.getDay(),catchDate.getMonth(),catchDate.getYear(),getContext());
                getInformationToListview.getJSON();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private class ViewHolder {
        final TextView textDate, textTime, textName, textType, textPhone;
        final Button optionDel, optionCatch, exportEvent;


        ViewHolder(View v) {
            this.textDate = (TextView) v.findViewById(R.id.text_date);
            this.textName = (TextView) v.findViewById(R.id.text_name);
            this.textTime = (TextView) v.findViewById(R.id.text_time);
            this.textType = (TextView) v.findViewById(R.id.text_type);
            this.textPhone = (TextView) v.findViewById(R.id.text_phone);
            this.optionDel = (Button) v.findViewById(R.id.btn_op1);
            this.optionCatch = (Button) v.findViewById(R.id.btn_catch);
            this.exportEvent = (Button) v.findViewById(R.id.export_to_calendar);

        }
    }
}
