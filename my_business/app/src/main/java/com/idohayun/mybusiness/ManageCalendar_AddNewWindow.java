package com.idohayun.mybusiness;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Boolean.TRUE;


public class ManageCalendar_AddNewWindow extends Fragment {
    private static final String TAG = "ManageDates";
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView textHour, textMin, modeDescription, chosenDate;
    private Button buttonAddNew;
    private int mDay, mMonth, mYear, mHour, mMin, id, currentDay, currentMonth, currentYear;
    private boolean firstTime = true, hourOk = false, minOk = false, dateOk = false, chooseDateBtnClicked = false;
    private int colorBad = Color.RED, colorGood = Color.GRAY;
    private ColorStateList colorStateListBAD = ColorStateList.valueOf(colorBad),
            colorStateListGOOD = ColorStateList.valueOf(colorGood);
    private String urlAddNewWindow = ServerURLSManager.Appointments_add_new_appointment;
    private boolean isAllDayMode = false;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private Map<String, String> map = new HashMap<>();

    public ManageCalendar_AddNewWindow() {
        // Required empty public constructor
        Log.d(TAG, "ManageCalendar_AddNewWindow: starting...");
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manage_calendar__add_new_window, container, false);

        Button buttonChooseDate, buttonModeSingle, buttonModeAllDay;

        buttonModeAllDay = view.findViewById(R.id.btn_all_day_appointments);
        buttonModeSingle = view.findViewById(R.id.btn_add_single_appointment);
        buttonAddNew = view.findViewById(R.id.btn_ok_add_new_window);
        buttonChooseDate = view.findViewById(R.id.btn_choose_new_date);
        chosenDate = view.findViewById(R.id.text_date_choosen);
        textHour = view.findViewById(R.id.text_hour);
        textMin = view.findViewById(R.id.text_min);
        modeDescription = view.findViewById(R.id.text_mode_description);

        buttonModeSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMin.setVisibility(View.VISIBLE);
                textHour.setVisibility(View.VISIBLE);
                modeDescription.setText(view.getResources().getString(R.string.single_appointment_description));
                buttonAddNew.setVisibility(View.VISIBLE);
            }
        });

        buttonModeAllDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllDayMode = true;
                textMin.setVisibility(View.INVISIBLE);
                textHour.setVisibility(View.INVISIBLE);
                modeDescription.setText(view.getResources().getString(R.string.all_day_description));
                buttonAddNew.setVisibility(View.VISIBLE);
            }
        });

        final Calendar calendar = Calendar.getInstance();
        currentYear = mYear = calendar.get(Calendar.YEAR);
        if (firstTime) {
            mMonth = calendar.get(Calendar.MONTH) + 1;
            firstTime = false;
        } else {
            mMonth = calendar.get(Calendar.MONTH);
        }
        currentMonth = mMonth;
        mDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        buttonChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        view.getContext(),
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, mYear, mMonth - 1, mDay);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                mDay = dayOfMonth;
                mMonth = month + 1;
                mYear = year;

                chosenDate.setText(date);
                chooseDateBtnClicked = true;
            }
        };

        buttonAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseDateBtnClicked) {
                    if(mDay >= currentDay && mMonth >= currentMonth && mYear >= currentYear)
                        dateOk = true;
                }
                if(!isAllDayMode) {
                    if (!textHour.getText().toString().isEmpty()) {
                        mHour = Integer.parseInt(textHour.getText().toString());
                        hourOk = (mHour <= 24 && mHour >= 0);
                    } else {
                        hourOk = false;
                    }

                    if (!textMin.getText().toString().isEmpty()) {
                        mMin = Integer.parseInt(textMin.getText().toString());
                        minOk = (mMin <= 60 && mMin >= 0);
                    } else {
                        minOk = false;
                    }
                    if (minOk && hourOk && dateOk) {
                        ViewCompat.setBackgroundTintList(textMin, colorStateListGOOD);
                        ViewCompat.setBackgroundTintList(textHour, colorStateListGOOD);

                        Log.d(TAG, "onClick: id: " + id);
                        DateArray dateListArray = new DateArray(mDay, mMonth, mYear, mHour, mMin, 0, 0, TRUE, 0);
                        queue = Volley.newRequestQueue(view.getContext());
                        map.put("TypeOfJSON", "New");
                        map.put("PersonID", Integer.toString(dateListArray.getPersonID()));
                        map.put("Day", Integer.toString(dateListArray.getDay()));
                        map.put("Month", Integer.toString(dateListArray.getMonth()));
                        map.put("Year", Integer.toString(dateListArray.getYear()));
                        map.put("Hour", Integer.toString(dateListArray.getHour()));
                        map.put("Min", Integer.toString(dateListArray.getMin()));
                        map.put("FullName", " ");
                        map.put("Type", " ");
                        map.put("Available", "TRUE");
                        map.put("Phone", " ");
                        Log.d(TAG, "onClick: " + dateListArray.toString());
                        final JSONObject jsonObject = new JSONObject(map);
                        request = new JsonObjectRequest(
                                Request.Method.POST, // the request method
                                urlAddNewWindow, jsonObject,
                                new Response.Listener<JSONObject>() { // the response listener
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.getString("status").equals("true")) {
                                                Log.d(TAG, "onResponse: SUCCESS!!");
                                                Toast.makeText(getContext(), Objects.requireNonNull(getContext()).getString(R.string.new_window_added), Toast.LENGTH_SHORT).show();
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
                    } else {
                        if (!minOk) ViewCompat.setBackgroundTintList(textMin, colorStateListBAD);
                        else ViewCompat.setBackgroundTintList(textMin, colorStateListGOOD);
                        if (!hourOk) ViewCompat.setBackgroundTintList(textHour, colorStateListBAD);
                        else ViewCompat.setBackgroundTintList(textHour, colorStateListGOOD);
                        if (!dateOk)
                            chosenDate.setTextColor(colorStateListBAD);
                        else chosenDate.setTextColor(colorStateListGOOD);
                    }
                } else { //is All day mode
                    Log.d(TAG, "onClick: all day mode");
                    DateArray dateListArray = new DateArray(mDay, mMonth, mYear, mHour, mMin, 0, 0, TRUE, 0);
                    queue = Volley.newRequestQueue(view.getContext());
                    map.put("TypeOfJSON", "NewAllDay");
                    map.put("PersonID", Integer.toString(dateListArray.getPersonID()));
                    map.put("Day", Integer.toString(dateListArray.getDay()));
                    map.put("Month", Integer.toString(dateListArray.getMonth()));
                    map.put("Year", Integer.toString(dateListArray.getYear()));
                    map.put("FullName", " ");
                    map.put("Type", " ");
                    map.put("Available", "TRUE");
                    map.put("Phone", " ");
                    Log.d(TAG, "onClick: " + dateListArray.toString());
                    final JSONObject jsonObject = new JSONObject(map);
                    request = new JsonObjectRequest(
                            Request.Method.POST, // the request method
                            urlAddNewWindow, jsonObject,
                            new Response.Listener<JSONObject>() { // the response listener
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("true")) {
                                            Log.d(TAG, "onResponse: SUCCESS!!");
                                            Toast.makeText(getContext(), Objects.requireNonNull(getContext()).getString(R.string.new_window_added), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "onResponse: " + response.getString("message") + " query was: " + response.getString("data"));
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
            }
        });

        return view;
    }
}
