package com.idohayun.manageapplication;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class ManageDates extends Fragment {
    private static final String TAG = "ManageDates";
    private Button btnChooseDate;
    private TextView details;
    private int mday, mmonth, myear;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ListView listView;
    private String databaseURL = "http://example.com/file";
    private boolean firstTime = true;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("savedDay",mday);
        outState.putInt("savedMonth",mmonth);
        outState.putInt("savedYear",myear);
        Log.d(TAG, "onSaveInstanceState: Date: " + mday + "-" + mmonth + "-" + myear);
        super.onSaveInstanceState(outState);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_mng_dates, container, false);

        if(savedInstanceState != null) {
            mday = savedInstanceState.getInt("savedDay");
            mmonth = savedInstanceState.getInt("savedMonth");
            myear = savedInstanceState.getInt("savedYear");
            listView = (ListView) view.findViewById(R.id.available_dates_list);
            details = (TextView) view.findViewById(R.id.text_details);
            String date =   mday + "/" + mmonth + "/" + myear;
            details.setText(date);
            Log.d(TAG, "onCreateView: Rotate Screen, Date: " + mday + "-" + mmonth + "-" + myear);
            getInformationToListview getInformationToListview = new getInformationToListview(databaseURL,listView,mday,mmonth,myear,getContext());
            getInformationToListview.getJSON();
        }

        final Calendar calendar = Calendar.getInstance();
        myear = calendar.get(Calendar.YEAR);
        if(firstTime) {
            mmonth = calendar.get(Calendar.MONTH) + 1;
            firstTime = false;
        } else {
            mmonth = calendar.get(Calendar.MONTH);
        }

        mday = calendar.get(Calendar.DAY_OF_MONTH);

        btnChooseDate = (Button) view.findViewById(R.id.btn_choose_date);
        details = (TextView) view.findViewById(R.id.text_details);

        listView = (ListView) view.findViewById(R.id.available_dates_list);

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, myear, mmonth-1, mday);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date =   dayOfMonth + "/" + (month +1) + "/" + year;
                mday = dayOfMonth;
                mmonth = month + 1;
                myear = year;

                details.setText(date);
                getInformationToListview getInformationToListview = new getInformationToListview(databaseURL,listView,mday,mmonth,myear,getContext());
                getInformationToListview.getJSON();
            }
        };


        return view;
    }


}
