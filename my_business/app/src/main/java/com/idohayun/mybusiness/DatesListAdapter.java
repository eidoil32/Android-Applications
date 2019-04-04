package com.idohayun.mybusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.constraint.Guideline;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class DatesListAdapter extends ArrayAdapter {
    private static final String TAG = "DatesListAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<DateArray> dateArrayList;
    private static List<appointment> list;
    private static List<String> typeList;
    private static String fullDate, fullTime;
    private static int type;
    private Map<String, String> appointmentDetails = new HashMap<>();
    private static Context context;
    private static boolean is_addName = false, is_addPhone = false, is_chooseType = false;
    private static Button btnCancel, btnOK;
    private static TextView username, phoneNumber, dialog_date, dialog_time;
    private static Spinner treatmentTypes;
    private int colorBad = Color.RED, colorGood = Color.GRAY;
    private ColorStateList colorStateListBAD = ColorStateList.valueOf(colorBad),
            colorStateListGOOD = ColorStateList.valueOf(colorGood);
    private JsonObjectRequest request;
    private final baseUSER user = new baseUSER();
    private static DateArray currentDate;
    private static RequestQueue queue;
    private static Map<String,String> map = new HashMap<>();
    private final ListView listView;
    private final ProgressBar progressBar;

    public DatesListAdapter(Context context, int resource, List<DateArray> dateArrayList, ListView listView, ProgressBar progressBar) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        user.getUserDetails(viewHolder.getView());
        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        viewHolder.textDate.setText(fullDate);
        viewHolder.textTime.setText(fullTime);
        viewHolder.textStatus.setVisibility(View.VISIBLE);

        if (!currentDate.isAvailable()) {
            if (currentDate.getUserID() == user.getId()) {
                viewHolder.option.setVisibility(View.VISIBLE);
                viewHolder.option.setText(convertView.getResources().getString(R.string.order_adapter_btn_export));
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteButton(position,viewHolder,v);
                    }
                });
                viewHolder.option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: selecting on export button");
                        currentDate = dateArrayList.get(position);
                        Log.d(TAG, "onClick: type = " + currentDate.getType() + " position: " + position);
                        if(currentDate.getType() == -1) {
                            CustomToast.showToast(context,context.getString(R.string.error_export_to_calendar),0);
                        } else {
                            CalendarEvent calendarEvent = new CalendarEvent();
                            calendarEvent.exportEvent(context, currentDate.getType(), currentDate);
                        }
                    }
                });
                viewHolder.textStatus.setText(convertView.getResources().getString(R.string.order_adapter_your_order));
            } else {
                viewHolder.textStatus.setText(convertView.getResources().getString(R.string.order_adapter_already_taken));
            }
        } else { //this window is available
            viewHolder.option.setVisibility(View.VISIBLE);
            viewHolder.option.setText(convertView.getResources().getString(R.string.order_adapter_btn_order_now));
            viewHolder.textStatus.setText(convertView.getResources().getString(R.string.order_adapter_order_now));
            viewHolder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderButton(position,viewHolder,v);
                }
            });
        }

        return convertView;
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
        layoutParams.copyFrom(viewHolder.dialogConfirmDelete.getWindow().getAttributes());
        int orientation = v.getContext().getResources().getConfiguration().orientation;
        int dialogWindowWidth = displayWidth;
        final int dialogWindowHeight = displayHeight;
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight/2;
        Guideline guideline = (Guideline) viewHolder.dialogConfirmDelete.findViewById(R.id.guide_center_line);
        guideline.setGuidelineBegin((displayWidth - (int)(20*displayMetrics.density))/2);
        dialog_date = (TextView) viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_date);
        dialog_time = (TextView) viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_time);
        dialog_date.setText(fullDate);
        dialog_time.setText(fullTime);
        btnCancel = (Button) viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_cancel);
        btnOK = (Button) viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_ok);

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
                                        listView.setAdapter(null);
                                        GetAppointmentListData.getData(context, currentDate.getDay(), currentDate.getMonth(), currentDate.getYear(), listView, progressBar);
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



    private void orderButton(int position, final ViewHolder viewHolder, View v) {
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
        layoutParams.copyFrom(viewHolder.dialogNewAppointment.getWindow().getAttributes());
        int orientation = v.getContext().getResources().getConfiguration().orientation;
        int dialogWindowWidth = displayWidth;
        final int dialogWindowHeight = displayHeight;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = dialogWindowHeight - (int) (50 * displayMetrics.density);
            Guideline guideline = (Guideline) viewHolder.dialogNewAppointment.findViewById(R.id.guide_center_line);
            guideline.setGuidelineBegin((displayWidth - (int) (20 * displayMetrics.density)) / 2);
        } else { // is landscape mode
            layoutParams.width = dialogWindowWidth - (int) (50 * displayMetrics.density);
            layoutParams.height = dialogWindowHeight - (int) (50 * displayMetrics.density) / 2;
            Guideline guideline = (Guideline) viewHolder.dialogNewAppointment.findViewById(R.id.guide_center_line);
            guideline.setGuidelineBegin((displayWidth / 2) - (int) (20 * displayMetrics.density));
        }

        appointmentDetails.put("PersonID", Integer.toString(currentDate.getPersonID()));
        appointmentDetails.put("Day", Integer.toString(currentDate.getDay()));
        appointmentDetails.put("Month", Integer.toString(currentDate.getMonth()));
        appointmentDetails.put("Year", Integer.toString(currentDate.getYear()));
        appointmentDetails.put("Hour", Integer.toString(currentDate.getHour()));
        appointmentDetails.put("Min", Integer.toString(currentDate.getMin()));
        appointmentDetails.put("UserID", Integer.toString(user.getId()));

        dialog_date = (TextView) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_date);
        dialog_time = (TextView) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_time);
        dialog_date.setText(fullDate);
        dialog_time.setText(fullTime);
        treatmentTypes = (Spinner) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_type_list);
        treatmentTypes.setDropDownWidth((int) (displayWidth * 0.5f));
        btnCancel = (Button) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_cancel);
        btnOK = (Button) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_ok);
        username = (TextView) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_edit_name);
        phoneNumber = (TextView) viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_edit_phone);

        GetTypesOfTreatments getTypesOfTreatments = new GetTypesOfTreatments();
        getTypesOfTreatments.getListOfTypes(context, treatmentTypes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.dialogNewAppointment.cancel();
            }
        });

        if(user.isExist()) {
            appointmentDetails.put("Name", user.getName());
            appointmentDetails.put("Phone", Integer.toString(user.getPhone()));
            appointmentDetails.put("UserID", Integer.toString(user.getId()));
            username.setText(user.getName());
            username.setEnabled(false);
            phoneNumber.setText(Integer.toString(user.getPhone()));
            phoneNumber.setEnabled(false);

            is_addName = true;
            is_addPhone = true;
        }

        treatmentTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selecting id: " + position);
                if(position > 0) {
                    is_chooseType = true;
                    type = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                is_chooseType = false;
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.isExist()) {
                    if (!username.getText().toString().isEmpty()) {
                        is_addName = true;
                        appointmentDetails.put("Name", username.getText().toString());
                    } else {
                        ViewCompat.setBackgroundTintList(username, colorStateListBAD);
                        Log.d(TAG, "onClick: name empty");
                    }
                    if (!phoneNumber.getText().toString().isEmpty()) {
                        is_addPhone = true;
                        appointmentDetails.put("Phone", phoneNumber.getText().toString());
                    } else {
                        ViewCompat.setBackgroundTintList(phoneNumber, colorStateListBAD);
                        Log.d(TAG, "onClick: phone empty");
                    }
                }
                if (is_chooseType) {
                    appointmentDetails.put("Type", Integer.toString(type));
                } else {
                    ViewCompat.setBackgroundTintList(treatmentTypes, colorStateListBAD);
                    Log.d(TAG, "onClick: type empty");
                }
                if (is_chooseType && is_addPhone && is_addName) {
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    ViewCompat.setBackgroundTintList(phoneNumber, colorStateListGOOD);
                    ViewCompat.setBackgroundTintList(phoneNumber, colorStateListGOOD);
                    ViewCompat.setBackgroundTintList(treatmentTypes, colorStateListGOOD);
                    final JSONObject jsonObject = new JSONObject(appointmentDetails);
                    Log.d(TAG, "onClick: " + jsonObject.toString());
                    request = new JsonObjectRequest(
                            Request.Method.POST, ServerURLSManager.Appointment_new_appointment, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("true")) {
                                            Log.d(TAG, "onResponse: SUCCESS!!");
                                            CustomToast.showToast(context, context.getString(R.string.new_window_added), 1);
                                            listView.setAdapter(null);
                                            GetAppointmentListData.getData(context, currentDate.getDay(), currentDate.getMonth(), currentDate.getYear(), listView, progressBar);
                                            viewHolder.dialogNewAppointment.cancel();
                                        } else {
                                            Log.d(TAG, "onResponse: Failed!! " + response.getString("data"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onResponse: Failed!!");
                            Toast.makeText(context, context.getString(R.string.new_window_failed), Toast.LENGTH_SHORT).show();

                        }
                    });
                    queue.add(request);
                }
            }
        });
        viewHolder.dialogNewAppointment.show();
        viewHolder.dialogNewAppointment.getWindow().setAttributes(layoutParams);
    }

    private class ViewHolder {
        final TextView textDate, textTime, textStatus;
        final Button option;
        final Dialog dialogNewAppointment, dialogConfirmDelete;
        final View view;
        final ImageView deleteButton;


        ViewHolder(View v) {
            this.textDate = (TextView) v.findViewById(R.id.order_adapter_date);
            this.textTime = (TextView) v.findViewById(R.id.order_adapter_time);
            this.textStatus = (TextView) v.findViewById(R.id.order_adapter_status);
            this.option = (Button) v.findViewById(R.id.order_adapter_btn_option);
            this.deleteButton = (ImageView) v.findViewById(R.id.order_adatper_delete);
            dialogNewAppointment = new Dialog(v.getContext());
            dialogConfirmDelete = new Dialog(v.getContext());
            this.dialogConfirmDelete.setContentView(R.layout.dialog_delete_appointment);
            this.dialogNewAppointment.setContentView(R.layout.dialog_make_new_appointment);
            this.view = v;
        }

        public View getView() { return view; }
    }

}
