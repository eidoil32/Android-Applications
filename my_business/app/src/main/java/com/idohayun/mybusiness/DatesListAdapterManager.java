package com.idohayun.mybusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.Guideline;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DatesListAdapterManager extends ArrayAdapter {
    private static final String TAG = "DatesListAdapterManager";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<DateArray> dateArrayList;
    private static String fullDate, fullTime;
    private final Context context;
    private JsonObjectRequest request;
    private static DateArray currentDate;
    private static RequestQueue queue;
    private static Map<String,String> map = new HashMap<>();
    private final SwipeMenuListView listView;
    private final ProgressBar progressBar;
    private ViewHolder viewHolder = null;

    DatesListAdapterManager(Context context, int resource, List<DateArray> dateArrayList, SwipeMenuListView listView, ProgressBar progressBar) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.dateArrayList = dateArrayList;
        this.listView = listView;
        this.progressBar = progressBar;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dateArrayList.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        viewHolder.textDate.setText(fullDate);
        viewHolder.textTime.setText(fullTime);
        if(currentDate.getApproved() == 1) {
            viewHolder.confirm.setText(context.getString(R.string.btn_cancel));
            viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteButton(position,viewHolder,viewHolder.getView());
                }
            });
        } else {
            viewHolder.confirm.setText(context.getString(R.string.btn_confirm));
            viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmAppointment(position,viewHolder.getView());
                }
            });
        }
        viewHolder.export.setText(convertView.getResources().getString(R.string.order_adapter_btn_export));
        viewHolder.export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selecting on export button");
                currentDate = dateArrayList.get(position);
                Log.d(TAG, "onClick: type = " + currentDate.getType() + " position: " + position);
                if (currentDate.getType() == -1) {
                    CustomToast.showToast(context, context.getString(R.string.error_export_to_calendar), 0);
                } else {
                    CalendarEvent calendarEvent = new CalendarEvent();
                    calendarEvent.exportEvent(context, currentDate.getType(), currentDate);
                }
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRowPopup(dateArrayList.get(position),position);
            }
        });

        viewHolder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreDetails(position,viewHolder.getView());
            }
        });

        return convertView;
    }

    private void confirmAppointment(final int position,final View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(viewHolder.dialogConfirmAppointment.getWindow()).getAttributes());
        float multiple_Width = 0.8f, multiple_Height = 0.3f;
        int dialogWindowWidth = (int) (displayWidth * multiple_Width);
        int dialogWindowHeight = (int) (displayHeight * multiple_Height);

        Guideline guideline_left, guideline_right, guideline_bottom;
        guideline_left = viewHolder.dialogConfirmAppointment.findViewById(R.id.popup_guideline_left);
        guideline_bottom = viewHolder.dialogConfirmAppointment.findViewById(R.id.popup_guideline_bottom);
        guideline_right = viewHolder.dialogConfirmAppointment.findViewById(R.id.popup_guideline_right);

        guideline_left.setGuidelineBegin(0);
        guideline_right.setGuidelineBegin(dialogWindowWidth - (int) (30 * displayMetrics.density));
        guideline_bottom.setGuidelineBegin(dialogWindowHeight - (int) (30 * displayMetrics.density));

        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        TextView alertText = viewHolder.dialogConfirmAppointment.findViewById(R.id.popup_alert_text);
        Button btnYes = viewHolder.dialogConfirmAppointment.findViewById(R.id.btn_popup_yes), btnNo = viewHolder.dialogConfirmAppointment.findViewById(R.id.btn_popup_no);

        alertText.setText(view.getResources().getString(R.string.alert_confirm_appointment_text));

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.dialogConfirmAppointment.cancel();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate = dateArrayList.get(position);
                queue = Volley.newRequestQueue(v.getContext());
                map.put("Day", Integer.toString(currentDate.getDay()));
                map.put("Month", Integer.toString(currentDate.getMonth()));
                map.put("Year", Integer.toString(currentDate.getYear()));
                map.put("Hour", Integer.toString(currentDate.getHour()));
                map.put("Min", Integer.toString(currentDate.getMin()));
                map.put("UserID", Integer.toString(currentDate.getUserID()));
                map.put("Type", Integer.toString(currentDate.getType()));
                Log.d(TAG, "onClick: " + map.toString());
                final JSONObject jsonObject = new JSONObject(map);
                request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        ServerURLSManager.User_manager_approve_appointment, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("true")) {
                                        CustomToast.showToast(view.getContext(),
                                                view.getResources().getString(R.string.appointment_confirm_successfully),1);
                                        viewHolder.dialogConfirmAppointment.cancel();
                                    } else {
                                        CustomToast.showToast(view.getContext(),
                                                view.getResources().getString(R.string.appointment_confirm_failed),0);
                                        viewHolder.dialogConfirmAppointment.cancel();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() { // the error listener
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                                viewHolder.dialogConfirmAppointment.cancel();
                            }
                        });

                queue.add(request);
            }
        });

        viewHolder.dialogConfirmAppointment.show();
        viewHolder.dialogConfirmAppointment.getWindow().setAttributes(layoutParams);
    }

    private void moreDetails(int position, View v) {
        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(viewHolder.popupMoreDetails.getWindow()).getAttributes());
        layoutParams.width = displayWidth;
        layoutParams.height = displayHeight/2;

        TextView user_phone = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_phone),
                user_name = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_name),
                type_of_treatment = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_treatment_type),
                date = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_date);

        getUserDetailsFromID(user_name, user_phone, currentDate.getUserID());
        getTreatmentTypeFromID(type_of_treatment,currentDate.getType());

        String showDate = fullDate + " " +
                getContext().getString(R.string.popup_more_details_time) + " " +
                fullTime;
        date.setText(showDate);

        ImageView closeImage = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_close);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.popupMoreDetails.cancel();
            }
        });
        viewHolder.popupMoreDetails.getWindow().setAttributes(layoutParams);
    }

    private void getTreatmentTypeFromID(final TextView type_of_treatment, int type) {
        queue = Volley.newRequestQueue(getContext());
        map.put("ID", Integer.toString(type));
        Log.d(TAG, "getTreatmentTypeFromID: " + type);
        Log.d(TAG, "onClick: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);

        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.Appointment_get_description_from_id, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            if(response.getString("status").equals("true")) {
                                StringBuilder sb = new StringBuilder();
                                String s = response.getString("data");
                                sb.append(s);
                                JSONArray jsonArray = new JSONArray(s);
                                String deviceLocale = Locale.getDefault().getLanguage();
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                if (jsonArray.length() > 0) {
                                    if(deviceLocale.equals("iw")) {
                                        JSONObject obj = jsonArray.getJSONObject(0);
                                        type_of_treatment.setText(obj.getString("Description"));
                                    } else {
                                        JSONObject obj = jsonArray.getJSONObject(1);
                                        type_of_treatment.setText(obj.getString("Description"));
                                    }
                                    viewHolder.popupMoreDetails.show();
                                }
                            } else {
                                Log.d(TAG, "onResponse: error " + response.getString("data"));
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

    private void getUserDetailsFromID(final TextView user_name, final TextView user_phone, int personID) {
        queue = Volley.newRequestQueue(getContext());
        map.put("PersonID", Integer.toString(personID));
        Log.d(TAG, "onClick: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);

        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_get_user_details_from_id, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            if(response.getString("status").equals("true")) {
                                StringBuilder sb = new StringBuilder();
                                String s = response.getString("data");
                                sb.append(s);
                                JSONArray jsonArray = new JSONArray(s);
                                if (jsonArray.length() > 0) {
                                    JSONObject obj = jsonArray.getJSONObject(0);
                                    user_name.setText(obj.getString("UserName"));
                                    final String phone = obj.getString("Phone");
                                    user_phone.setText(phone);
                                    user_phone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:0" + phone));
                                            v.getContext().startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "onResponse: error" + response.getString("data"));
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

    private void deleteButton(int position, final ViewHolder viewHolder, View v) {
        Log.d(TAG, "onClick: delete button!");
        Log.d(TAG, "onClick: selecting on order button, position = " + position);
        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(viewHolder.dialogConfirmDelete.getWindow()).getAttributes());
        layoutParams.width = displayWidth;
        layoutParams.height = displayHeight/2;
        Guideline guideline = viewHolder.dialogConfirmDelete.findViewById(R.id.guide_center_line);
        guideline.setGuidelineBegin((displayWidth - (int)(20*displayMetrics.density))/2);
        TextView dialog_date = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_date);
        TextView dialog_time = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_time);
        dialog_date.setText(fullDate);
        dialog_time.setText(fullTime);
        Button btnCancel = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_cancel);
        Button btnOK = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
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
                map.put("FullName", " ");
                map.put("Type", "-1");
                map.put("Available", "TRUE");
                map.put("Phone", "0");
                Log.d(TAG, "onClick: " + map.toString());
                final JSONObject jsonObject = new JSONObject(map);

                request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        ServerURLSManager.Appointment_delete_appointment, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    if(response.getString("status").equals("true")) {
                                        CustomToast.showToast(context,viewHolder.getView().
                                                        getResources().getString(R.string.dialog_appointment_cancled_successfully),
                                                1);
                                        viewHolder.dialogConfirmDelete.cancel();
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.dialogConfirmDelete.cancel();
            }
        });

        viewHolder.dialogConfirmDelete.show();
        viewHolder.dialogConfirmDelete.getWindow().setAttributes(layoutParams);
    }

    private void DeleteRowPopup(final DateArray catchDate,final int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_simple_yes_or_no);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        int orientation = getContext().getResources().getConfiguration().orientation;
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

        TextView alertText = dialog.findViewById(R.id.popup_alert_text);
        alertText.setText(dialog.getContext().getString(R.string.alert_text_window_delete_forever));

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
                queue = Volley.newRequestQueue(getContext());
                Log.d(TAG, "onClick: Delete for ever button clicked!");
                Map<String,String> tempMap = new HashMap<>();
                tempMap.put("TypeOfJSON","DeleteForEver");
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
                        ServerURLSManager.Appointment_delete_appointment, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    if(response.getString("status").equals("true")) {
                                        CustomToast.showToast(getContext(),getContext().getString(R.string.appointment_deleted_successfully),1);
                                    } else {
                                        Log.d(TAG, "onResponse: error from sql");
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
                dateArrayList.remove(position);
                notifyDataSetChanged();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private class ViewHolder {
        final TextView textDate, textTime;
        final Button export, confirm;
        final Dialog dialogNewAppointment, dialogConfirmDelete, popupMoreDetails, dialogConfirmAppointment;
        final View view;
        final ImageView btnDelete, btnExpand;

        ViewHolder(View v) {
            this.textDate = v.findViewById(R.id.manager_appointment_adapter_date);
            this.textTime = v.findViewById(R.id.manager_appointment_adapter_time);
            this.export = v.findViewById(R.id.manager_appointment_adapter_btn_export);
            this.confirm = v.findViewById(R.id.manager_appointment_adapter_btn_confirm);
            this.btnDelete = v.findViewById(R.id.manager_appointment_adapter_delete);
            this.btnExpand = v.findViewById(R.id.manager_appointment_adapter_more_details);
            dialogNewAppointment = new Dialog(v.getContext());
            dialogConfirmDelete = new Dialog(v.getContext());
            popupMoreDetails = new Dialog(v.getContext());
            dialogConfirmAppointment = new Dialog(v.getContext());
            this.dialogConfirmAppointment.setContentView(R.layout.popup_simple_yes_or_no);
            this.popupMoreDetails.setContentView(R.layout.popup_more_details);
            this.dialogConfirmDelete.setContentView(R.layout.dialog_delete_appointment);
            this.dialogNewAppointment.setContentView(R.layout.dialog_make_new_appointment);
            this.view = v;
        }

        public View getView() { return view; }
    }
}
