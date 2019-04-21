package com.idohayun.mybusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;

public class DatesListAdapter extends ArrayAdapter {
    private static final String TAG = "DatesListAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<DateArray> dateArrayList;
    private static String fullDate, fullTime;
    private static int type;
    private Map<String, String> appointmentDetails = new HashMap<>();
    private final Context context;
    private static boolean is_addName = false, is_addPhone = false, is_chooseType = false;
    private Button btnCancel, btnOK;
    private TextView username, phoneNumber, dialog_date, dialog_time;
    private Spinner treatmentTypes;
    private int colorBad = Color.RED, colorGood = Color.GRAY;
    private ColorStateList colorStateListBAD = ColorStateList.valueOf(colorBad),
            colorStateListGOOD = ColorStateList.valueOf(colorGood);
    private JsonObjectRequest request;
    private final baseUSER user = new baseUSER();
    private static DateArray currentDate;
    private static RequestQueue queue;
    private static Map<String, String> map = new HashMap<>();
    private final SwipeMenuListView listView;
    private final ProgressBar progressBar;
    private ViewHolder viewHolder = null;

    DatesListAdapter(Context context, int resource, List<DateArray> dateArrayList, SwipeMenuListView listView, ProgressBar progressBar) {
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

        user.getUserDetails(viewHolder.getView());
        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        viewHolder.textDate.setText(fullDate);
        viewHolder.textTime.setText(fullTime);
        viewHolder.textStatus.setVisibility(View.VISIBLE);

        if (user.getId() == 1) {
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0x9C,
                            0x2D, 0x2D)));
                    // set item width
                    deleteItem.setWidth((int) (40 * displayMetrics.density));
                    // set a icon
                    deleteItem.setIcon(R.drawable.baseline_delete_white_18dp);
                    // add to menu
                    menu.addMenuItem(deleteItem);

                    // create "delete" item
                    SwipeMenuItem details = new SwipeMenuItem(context);
                    // set item background
                    details.setBackground(new ColorDrawable(Color.rgb(0x2E, 0x6D, 0xA8)));
                    // set item width
                    details.setWidth((int) (40 * displayMetrics.density));
                    // set a icon
                    details.setIcon(R.drawable.baseline_contact_support_black_18dp);
                    // add to menu
                    menu.addMenuItem(details);
                }
            };

            listView.setMenuCreator(creator);


            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int i_position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            DeleteRowPopup(dateArrayList.get(i_position), i_position);
                            break;
                        case 1:
                            moreDetails(i_position, viewHolder.getView());
                            break;
                    }
                    return false;
                }
            });

            listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
//            viewHolder.getView().setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    DeleteRowPopup(currentDate,position);
//                    return false;
//                }
//            });
        }

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        boolean today = true;
        if (currentDay == currentDate.getDay() && currentHour >= currentDate.getHour()) {
            today = false;
        }

        if (dateIsLessThanToday(currentDate.getDay(), currentDate.getMonth(), currentDate.getYear()) && today) {
            if (!currentDate.isAvailable()) {
                if (currentDate.getUserID() == user.getId() || user.getId() == 1) {
                    viewHolder.option.setVisibility(View.VISIBLE);
                    viewHolder.option.setText(convertView.getResources().getString(R.string.order_adapter_btn_export));
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteButton(position, viewHolder, v);
                        }
                    });
                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
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
                        orderButton(position, viewHolder, viewHolder.getView());
                    }
                });
            }
        } else {
            viewHolder.getView().setBackgroundColor(viewHolder.getView().getResources().getColor(R.color.background_window_is_over, null));
            viewHolder.textStatus.setText(convertView.getResources().getString(R.string.window_time_already_passed));
            viewHolder.option.setVisibility(View.VISIBLE);
            viewHolder.option.setBackgroundColor(viewHolder.getView().getResources().getColor(R.color.button_not_useable, null));
        }

        return convertView;
    }

    private boolean dateIsLessThanToday(int day, int month, int year) {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Log.d(TAG, "dateIsLessThanToday: " + currentDay + currentMonth + currentYear);
        if (year <= currentYear) {
            if (month <= currentMonth) {
                return day >= currentDay;
            }
        }

        return true;
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
        layoutParams.height = displayHeight / 2;

        TextView user_phone = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_phone),
                user_name = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_name),
                type_of_treatment = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_treatment_type),
                date = viewHolder.popupMoreDetails.findViewById(R.id.popup_more_details_date);

        getUserDetailsFromID(user_name, user_phone, currentDate.getUserID());
        getTreatmentTypeFromID(type_of_treatment, currentDate.getType());

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
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                StringBuilder sb = new StringBuilder();
                                String s = response.getString("data");
                                sb.append(s);
                                JSONArray jsonArray = new JSONArray(s);
                                String deviceLocale = Locale.getDefault().getLanguage();
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                if (jsonArray.length() > 0) {
                                    if (deviceLocale.equals("iw")) {
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
                        Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
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
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
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
                        Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
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
        layoutParams.height = displayHeight / 2;
        Guideline guideline = viewHolder.dialogConfirmDelete.findViewById(R.id.guide_center_line);
        guideline.setGuidelineBegin((displayWidth - (int) (20 * displayMetrics.density)) / 2);
        dialog_date = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_date);
        dialog_time = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_time);
        dialog_date.setText(fullDate);
        dialog_time.setText(fullTime);
        btnCancel = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_cancel);
        btnOK = viewHolder.dialogConfirmDelete.findViewById(R.id.dialog_new_apt_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue = Volley.newRequestQueue(getContext());
                Log.d(TAG, "onClick: DEL button clicked!");
                currentDate.resetDate();
                map.put("TypeOfJSON", "Delete");
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
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("true")) {
                                        CustomToast.showToast(context, viewHolder.getView().
                                                        getResources().getString(R.string.dialog_appointment_cancled_successfully),
                                                1);
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
                                Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
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

    private void DeleteRowPopup(final DateArray catchDate, final int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_simple_yes_or_no);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
                Map<String, String> tempMap = new HashMap<>();
                tempMap.put("TypeOfJSON", "DeleteForEver");
                tempMap.put("PersonID", Integer.toString(catchDate.getPersonID()));
                tempMap.put("Day", Integer.toString(catchDate.getDay()));
                tempMap.put("Month", Integer.toString(catchDate.getMonth()));
                tempMap.put("Year", Integer.toString(catchDate.getYear()));
                tempMap.put("Hour", Integer.toString(catchDate.getHour()));
                tempMap.put("Min", Integer.toString(catchDate.getMin()));
                tempMap.put("Available", "FALSE");
                final JSONObject jsonObject = new JSONObject(tempMap);
                request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        ServerURLSManager.Appointment_delete_appointment, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("true")) {
                                        CustomToast.showToast(getContext(), getContext().getString(R.string.appointment_deleted_successfully), 1);
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
                                Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
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

    private void orderButton(int position, final ViewHolder viewHolder, View v) {
        Log.d(TAG, "onClick: selecting on order button, position = " + position);
        viewHolder.dialogConfirmDelete.setContentView(R.layout.dialog_make_new_appointment);
        currentDate = dateArrayList.get(position);
        String min = currentDate.getMin() < 10 ? currentDate.getMin() + "0" : Integer.toString(currentDate.getMin());
        fullDate = currentDate.getDay() + "/" + currentDate.getMonth() + "/" + currentDate.getYear();
        fullTime = currentDate.getHour() + ":" + min;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(viewHolder.dialogNewAppointment.getWindow()).getAttributes());
        int orientation = v.getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.width = displayWidth;
            layoutParams.height = displayHeight - (int) (50 * displayMetrics.density);
            Guideline guideline = viewHolder.dialogNewAppointment.findViewById(R.id.guide_center_line);
            guideline.setGuidelineBegin((displayWidth - (int) (20 * displayMetrics.density)) / 2);
        } else { // is landscape mode
            layoutParams.width = displayWidth - (int) (50 * displayMetrics.density);
            layoutParams.height = displayHeight - (int) (50 * displayMetrics.density) / 2;
            Guideline guideline = viewHolder.dialogNewAppointment.findViewById(R.id.guide_center_line);
            guideline.setGuidelineBegin((displayWidth / 2) - (int) (20 * displayMetrics.density));
        }

        appointmentDetails.put("PersonID", Integer.toString(currentDate.getPersonID()));
        appointmentDetails.put("Day", Integer.toString(currentDate.getDay()));
        appointmentDetails.put("Month", Integer.toString(currentDate.getMonth()));
        appointmentDetails.put("Year", Integer.toString(currentDate.getYear()));
        appointmentDetails.put("Hour", Integer.toString(currentDate.getHour()));
        appointmentDetails.put("Min", Integer.toString(currentDate.getMin()));

        dialog_date = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_date);
        dialog_time = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_time);
        dialog_date.setText(fullDate);
        dialog_time.setText(fullTime);
        treatmentTypes = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_type_list);
        treatmentTypes.setDropDownWidth((int) (displayWidth * 0.5f));
        btnCancel = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_cancel);
        btnOK = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_ok);
        username = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_edit_name);
        phoneNumber = viewHolder.dialogNewAppointment.findViewById(R.id.dialog_new_apt_edit_phone);

        GetTypesOfTreatments getTypesOfTreatments = new GetTypesOfTreatments();
        getTypesOfTreatments.getListOfTypes(context, treatmentTypes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.dialogNewAppointment.cancel();
            }
        });

        if (user.isExist()) {
            appointmentDetails.put("Name", user.getName());
            appointmentDetails.put("Phone", Integer.toString(user.getPhone()));
            appointmentDetails.put("UserID", Integer.toString(user.getId()));
            username.setText(user.getName());
            username.setEnabled(false);
            String userPhone = Integer.toString(user.getPhone());
            phoneNumber.setText(userPhone);
            phoneNumber.setEnabled(false);

            is_addName = true;
            is_addPhone = true;
        }

        treatmentTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selecting id: " + position);
                if (position > 0) {
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
                if (!user.isExist()) {
                    if (!username.getText().toString().isEmpty()) {
                        is_addName = true;
                        appointmentDetails.put("Name", username.getText().toString());
                    } else {
                        ViewCompat.setBackgroundTintList(username, colorStateListBAD);
                        Log.d(TAG, "onClick: name empty");
                    }
                    int int_phoneNumber;
                    try {
                        int_phoneNumber = Integer.parseInt(phoneNumber.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        int_phoneNumber = 0;
                    }
                    if (int_phoneNumber != 0) {
                        if (Integer.toString(int_phoneNumber).length() != 10 || int_phoneNumber < 0 || baseUSER.firstTwoDigitsNotExist(Integer.toString(int_phoneNumber))) {
                            is_addPhone = true;
                            appointmentDetails.put("Phone", phoneNumber.getText().toString());
                        } else {
                            ViewCompat.setBackgroundTintList(phoneNumber, colorStateListBAD);
                            Log.d(TAG, "onClick: phone invalid");
                        }
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
                    if (!user.isExist()) {
                        user.setGuestUser(viewHolder.getView(), Integer.parseInt(phoneNumber.getText().toString()), username.getText().toString(), appointmentDetails);
                    }
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
                                            GetAppointmentListData.getData(context, currentDate.getDay(), currentDate.getMonth(), currentDate.getYear(), listView, progressBar);
                                            viewHolder.dialogNewAppointment.cancel();
                                        } else {
                                            CustomToast.showToast(context, viewHolder.getView().getResources().getString(R.string.new_window_failed), 0);
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
                            CustomToast.showToast(context, viewHolder.getView().getResources().getString(R.string.new_window_failed), 0);

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
        final Dialog dialogNewAppointment, dialogConfirmDelete, popupMoreDetails;
        final View view;
        final ImageView deleteButton;

        ViewHolder(View v) {
            this.textDate = v.findViewById(R.id.order_adapter_date);
            this.textTime = v.findViewById(R.id.order_adapter_time);
            this.textStatus = v.findViewById(R.id.order_adapter_status);
            this.option = v.findViewById(R.id.order_adapter_btn_option);
            this.deleteButton = v.findViewById(R.id.order_adapter_delete);
            dialogNewAppointment = new Dialog(v.getContext());
            dialogConfirmDelete = new Dialog(v.getContext());
            popupMoreDetails = new Dialog(v.getContext());
            this.popupMoreDetails.setContentView(R.layout.popup_more_details);
            this.dialogConfirmDelete.setContentView(R.layout.dialog_delete_appointment);
            this.dialogNewAppointment.setContentView(R.layout.dialog_make_new_appointment);
            this.view = v;
        }

        public View getView() {
            return view;
        }
    }
}
