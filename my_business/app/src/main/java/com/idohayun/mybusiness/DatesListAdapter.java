package com.idohayun.mybusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
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
    private static String fullDate, fullTime, updateURL = "http://eidoil32.myhf.in/userMakeNewOrder.php";
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

    public DatesListAdapter(Context context, int resource, List<DateArray> dateArrayList) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.dateArrayList = dateArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dateArrayList.size();
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

        user.getUserDetails(viewHolder.getView());

        final DateArray currentDate = dateArrayList.get(position);
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
                viewHolder.option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: selecting on export button");
                        CalendarEvent calendarEvent = new CalendarEvent();
                        calendarEvent.exportEvent(context,currentDate.getType(),currentDate);

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
                    Log.d(TAG, "onClick: selecting on order button");

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int displayWidth = displayMetrics.widthPixels;
                    int displayHeight = displayMetrics.heightPixels;
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(viewHolder.dialog.getWindow().getAttributes());
                    int orientation = v.getContext().getResources().getConfiguration().orientation;
                    int dialogWindowWidth = displayWidth;
                    final int dialogWindowHeight = displayHeight;
                    layoutParams.width = dialogWindowWidth;
                    layoutParams.height = dialogWindowHeight - (int)(50*displayMetrics.density);
                    Guideline guideline = (Guideline) viewHolder.dialog.findViewById(R.id.guide_center_line);
                    guideline.setGuidelineBegin((displayWidth - (int)(20*displayMetrics.density))/2);

                    appointmentDetails.put("PersonID", Integer.toString(currentDate.getPersonID()));
                    appointmentDetails.put("Day", Integer.toString(currentDate.getDay()));
                    appointmentDetails.put("Month", Integer.toString(currentDate.getMonth()));
                    appointmentDetails.put("Year", Integer.toString(currentDate.getYear()));
                    appointmentDetails.put("Hour", Integer.toString(currentDate.getHour()));
                    appointmentDetails.put("Min", Integer.toString(currentDate.getMin()));
                    appointmentDetails.put("UserID", Integer.toString(user.getId()));

                    dialog_date = (TextView) viewHolder.dialog.findViewById(R.id.dialog_new_apt_date);
                    dialog_time = (TextView) viewHolder.dialog.findViewById(R.id.dialog_new_apt_time);
                    dialog_date.setText(fullDate);
                    dialog_time.setText(fullTime);
                    treatmentTypes = (Spinner) viewHolder.dialog.findViewById(R.id.dialog_new_apt_type_list);
                    treatmentTypes.setDropDownWidth((int) (displayWidth * 0.5f));
                    btnCancel = (Button) viewHolder.dialog.findViewById(R.id.dialog_new_apt_cancel);
                    btnOK = (Button) viewHolder.dialog.findViewById(R.id.dialog_new_apt_ok);
                    username = (TextView) viewHolder.dialog.findViewById(R.id.dialog_new_apt_edit_name);
                    phoneNumber = (TextView) viewHolder.dialog.findViewById(R.id.dialog_new_apt_edit_phone);

                    GetTypesOfTreatments getTypesOfTreatments = new GetTypesOfTreatments();
                    getTypesOfTreatments.getListOfTypes(context, treatmentTypes);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.dialog.cancel();
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
                                        Request.Method.POST, updateURL, jsonObject,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getString("status").equals("true")) {
                                                        Log.d(TAG, "onResponse: SUCCESS!!");
                                                        Toast.makeText(context, context.getString(R.string.new_window_added), Toast.LENGTH_SHORT).show();
                                                        viewHolder.dialog.cancel();
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
                    viewHolder.dialog.show();
                    viewHolder.dialog.getWindow().setAttributes(layoutParams);
                }
            });
        }

        return convertView;
    }


    private class ViewHolder {
        final TextView textDate, textTime, textStatus;
        final Button option;
        final Dialog dialog;
        final View view;


        ViewHolder(View v) {
            this.textDate = (TextView) v.findViewById(R.id.order_adapter_date);
            this.textTime = (TextView) v.findViewById(R.id.order_adapter_time);
            this.textStatus = (TextView) v.findViewById(R.id.order_adapter_status);
            this.option = (Button) v.findViewById(R.id.order_adapter_btn_option);
            dialog = new Dialog(v.getContext());
            this.dialog.setContentView(R.layout.dialog_make_new_appointment);
            this.view = v;
        }

        public View getView() { return view; }
    }


}
