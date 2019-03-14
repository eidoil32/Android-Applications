package com.idohayun.manageapplication;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import static java.lang.Boolean.TRUE;

public class ManageAppointments extends Fragment {
    private static final String TAG = "ManageDates";
    private Button buttonChooseDate, buttonAddNew;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView chosenDate;
    private TextView textHour, textMin, textDuration;
    private int mDay, mMonth, mYear, mHour, mMin, mDuration, id, currentDay, currentMonth, currentYear;
    private boolean firstTime = true, hourOk = false, minOk = false, durationOk = false, dateOk = false, chooseDateBtnClicked = false;
    private int colorBad = Color.RED, colorGood = Color.GRAY;
    private ColorStateList colorStateListBAD = ColorStateList.valueOf(colorBad),
            colorStateListGOOD = ColorStateList.valueOf(colorGood);
    private String urlAddNewWindow = "http://eidoil32.myhf.in/addNewWindow.php";
    private getInformationFromSQL getID = new getInformationFromSQL();
    private RequestQueue queue;
    private JsonObjectRequest request;
    private Map<String, String> map = new HashMap<String, String>();


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_mng_appoint, container, false);

        id = getID.getLastID();

        buttonAddNew = view.findViewById(R.id.btn_ok_add_new_window);
        buttonChooseDate = view.findViewById(R.id.btn_choose_new_date);

        chosenDate = view.findViewById(R.id.text_date_choosen);

        textHour = view.findViewById(R.id.text_hour);
        textDuration = view.findViewById(R.id.text_duration);
        textMin = view.findViewById(R.id.text_min);

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
                        getContext(),
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, mYear, mMonth - 1, mDay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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

        final String date = mDay + "/" + (mMonth) + "/" + mYear;

        buttonAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseDateBtnClicked) {
                    if(mDay >= currentDay && mMonth >= currentMonth && mYear >= currentYear)
                        dateOk = true;
                }
                if (!textHour.getText().toString().isEmpty()) {
                    mHour = Integer.parseInt(textHour.getText().toString());
                    if (mHour > 24 || mHour < 0) hourOk = false;
                    else hourOk = true;
                } else {
                    hourOk = false;
                }

                if (!textMin.getText().toString().isEmpty()) {
                    mMin = Integer.parseInt(textMin.getText().toString());
                    if (mMin > 60 || mMin < 0) minOk = false;
                    else minOk = true;
                } else {
                    minOk = false;
                }

                if (!textDuration.getText().toString().isEmpty()) {
                    mDuration = Integer.parseInt(textDuration.getText().toString());
                    if (mDuration > 90 || mDuration <= 0) durationOk = false;
                    else durationOk = true;
                } else {
                    durationOk = false;
                }

                if (durationOk && minOk && hourOk && dateOk) {
                    ViewCompat.setBackgroundTintList(textMin, colorStateListGOOD);
                    ViewCompat.setBackgroundTintList(textHour, colorStateListGOOD);
                    ViewCompat.setBackgroundTintList(textDuration, colorStateListGOOD);

                    id = getID.getLastID() + 1;
                    Log.d(TAG, "onClick: id: " + id);
                    DateListArray dateListArray = new DateListArray(
                            "", "", mDay, mMonth, mYear, mHour, mMin, 0, id, TRUE);
                    queue = Volley.newRequestQueue(getContext());
                    map.put("TypeOfJSON", "New");
                    map.put("PersonID", Integer.toString(dateListArray.getPersonID()));
                    map.put("Day", Integer.toString(dateListArray.getDay()));
                    map.put("Month", Integer.toString(dateListArray.getMonth()));
                    map.put("Year", Integer.toString(dateListArray.getYear()));
                    map.put("Hour", Integer.toString(dateListArray.getHour()));
                    map.put("Min", Integer.toString(dateListArray.getMin()));
                    map.put("FullName", dateListArray.getName());
                    map.put("Type", dateListArray.getType());
                    map.put("Available", "TRUE");
                    map.put("Phone", Integer.toString(dateListArray.getPhone()));
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
                                            Toast.makeText(getContext(),getContext().getString(R.string.new_window_added),Toast.LENGTH_SHORT).show();
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
                    if (!durationOk)
                        ViewCompat.setBackgroundTintList(textDuration, colorStateListBAD);
                    else ViewCompat.setBackgroundTintList(textDuration, colorStateListGOOD);
                    if(!dateOk)
                        chosenDate.setTextColor(colorStateListBAD);
                    else chosenDate.setTextColor(colorStateListGOOD);;
                }


            }
        });


        return view;
    }


}
