package com.idohayun.mybusiness;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.Calendar;
import java.util.Objects;

public class OrderView extends Fragment {
    private static final String TAG = "OrderView";
    private SwipeMenuListView listView;
    private TextView selectedDate;
    private Context context;
    private static int calendar_day, calendar_month, calendar_year;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ProgressBar progressBar;
    private static boolean dateSelected = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    final Calendar calendar = Calendar.getInstance();
    private String saveDay = "Day", saveMonth = "Month", saveYear = "Year";

    public OrderView() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            Log.d(TAG, "onActivityCreated: " + savedInstanceState.getInt(saveYear));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called");
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
        Log.d(TAG, "setRetainInstance: return?");
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_view, container, false);
        view.setBackgroundColor(view.getResources().getColor(R.color.colorBackground,null));
        container.removeAllViews();
        Button selectDateBtn;
        context = view.getContext();
        MainActivity.changeTitlePage(getResources().getString(R.string.text_order_title));
        //DatesListAdapter.changeOrientation();
        selectDateBtn = view.findViewById(R.id.order_btn_select_date);
        selectedDate = view.findViewById(R.id.order_show_selected_date);
        listView = view.findViewById(R.id.manage_appointment_list);
        progressBar = view.findViewById(R.id.order_progressBar);

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: " + savedInstanceState.describeContents());
            calendar_year = savedInstanceState.getInt(saveYear,0);
            calendar_month = savedInstanceState.getInt(saveMonth,0);
            calendar_day = savedInstanceState.getInt(saveDay,0);
            Log.d(TAG, "onCreateView recovering date: " + calendar_day + "/" + (calendar_month) + "/" + calendar_year);

            if (calendar_day != 0 && calendar_month != 0 && calendar_year != 0) {
                Log.d(TAG, "onCreateView: in " + calendar_day + "/" + (calendar_month) + "/" + calendar_year);

                String fullDate = (calendar_day + "/" + calendar_month + "/" + calendar_year);
                selectedDate.setText(fullDate);
                GetAppointmentListData.getData(context, calendar_day, calendar_month, calendar_year, listView, progressBar);
            } else {
                setDate(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.YEAR));
            }
        } else {
            setDate(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.YEAR));
        }

        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, calendar_year, calendar_month - 1, calendar_day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
                dialog.getDatePicker().setSpinnersShown(true);
                Log.d(TAG, "onClick: click select button");
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar_year = year;
                calendar_day = dayOfMonth;
                calendar_month = month + 1;
                String fullDate = (calendar_day + "/" + calendar_month + "/" + calendar_year);
                selectedDate.setText(fullDate);
                dateSelected = true;
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                GetAppointmentListData.getData(context, calendar_day, calendar_month, calendar_year, listView, progressBar);
            }
        };

        swipeRefreshLayout = view.findViewById(R.id.manage_appointment_listview_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: here!");
                GetAppointmentListData.getData(context, calendar_day, calendar_month, calendar_year, listView, progressBar);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: " + dateSelected);
        if(dateSelected) {
            Log.d(TAG, "onSaveInstanceState: entering");
            outState.putInt(saveYear, calendar_year);
            outState.putInt(saveMonth, calendar_month);
            outState.putInt(saveDay, calendar_day);
        }
    }

    private void setDate(int i_day, int i_month, int i_year) {
        calendar_year = i_year;
        calendar_month = i_month;
        calendar_day = i_day;
    }
}
