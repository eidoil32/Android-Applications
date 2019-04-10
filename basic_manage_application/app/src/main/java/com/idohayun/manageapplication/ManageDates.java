package com.idohayun.manageapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Objects;

public class ManageDates extends Fragment {
    private static final String TAG = "ManageDates";
    private TextView details;
    private int m_day, m_month, m_year;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ListView listView;
    private String databaseURL = ServerURLManager.Date_database_download;
    private boolean firstTime = true;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("savedDay", m_day);
        outState.putInt("savedMonth", m_month);
        outState.putInt("savedYear", m_year);
        Log.d(TAG, "onSaveInstanceState: Date: " + m_day + "-" + m_month + "-" + m_year);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab1_mng_dates, container, false);

        if(savedInstanceState != null) {
            m_day = savedInstanceState.getInt("savedDay");
            m_month = savedInstanceState.getInt("savedMonth");
            m_year = savedInstanceState.getInt("savedYear");
            listView = view.findViewById(R.id.available_dates_list);
            details = view.findViewById(R.id.text_details);
            String date =   m_day + "/" + m_month + "/" + m_year;
            details.setText(date);
            Log.d(TAG, "onCreateView: Rotate Screen, Date: " + m_day + "-" + m_month + "-" + m_year);
            getInformationToListView getInformationToListview = new getInformationToListView(databaseURL,listView, m_day, m_month, m_year,getContext());
            getInformationToListview.getJSON();
        }

        final Calendar calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        if(firstTime) {
            m_month = calendar.get(Calendar.MONTH) + 1;
            firstTime = false;
        } else {
            m_month = calendar.get(Calendar.MONTH);
        }

        m_day = calendar.get(Calendar.DAY_OF_MONTH);

        Button btnChooseDate = view.findViewById(R.id.btn_choose_date);
        details = view.findViewById(R.id.text_details);

        listView = view.findViewById(R.id.available_dates_list);

        swipeRefreshLayout = view.findViewById(R.id.swipe_date_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setAdapter(null);
                getInformationToListView getInformationToListview = new getInformationToListView(databaseURL,listView, m_day, m_month, m_year,getContext());
                getInformationToListview.getJSON();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        view.getContext(),
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, m_year, m_month -1, m_day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date =   dayOfMonth + "/" + (month +1) + "/" + year;
                m_day = dayOfMonth;
                m_month = month + 1;
                m_year = year;

                details.setText(date);
                getInformationToListView getInformationToListview = new getInformationToListView(databaseURL,listView, m_day, m_month, m_year,getContext());
                getInformationToListview.getJSON();
            }
        };


        return view;
    }


}
