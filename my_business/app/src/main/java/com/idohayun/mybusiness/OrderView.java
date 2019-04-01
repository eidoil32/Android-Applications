package com.idohayun.mybusiness;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Boolean.valueOf;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderView extends Fragment {
    private static final String TAG = "OrderView";
    private ListView listView;
    private TextView selectedDate;
    private Button selectDateBtn;
    private static Context context;
    private static int calendar_day, calendar_month, calendar_year;
    private static List<DateArray> dateArrayList = new ArrayList<>();
    private static int func_day, func_month, func_year, func_hour, func_min, func_personID, func_tempAvailable;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static boolean available, firstTime = true;
    private static String serverUrl = "http://eidoil32.myhf.in/showListOfAppointment.php";
    private static StringBuilder sb = new StringBuilder();
    private static ProgressBar progressBar;
    private boolean dateSelected = false;
    private static FrameLayout frameLayout;
    private static int userID = MainActivity.getUserID();
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnSaveClicked mSaveListener = null;
    private static Bundle arguments = new Bundle();

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public OrderView(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }

    public OrderView() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: called");
        arguments = getArguments();
        if(arguments != null) {
            Log.d(TAG, "onActivityCreated: " + arguments.getInt("Year"));
        } else {
            arguments = new Bundle();
        }
        if(savedInstanceState != null) {
            Log.d(TAG, "onViewStateRestored: restored! " + savedInstanceState.getInt("Selected Year"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called");
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setRetainInstance(true);
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

        // Activities containing this fragment must implement it's callbacks.
        Activity activity = getActivity();
        arguments = getArguments();
        if(arguments != null) {
            Log.d(TAG, "onAttach:  " + arguments.getInt("Year"));
        } else {
            arguments = new Bundle();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_view, container, false);
        container.removeAllViews();
        Log.d(TAG, "onCreateView: ?");
        selectDateBtn = (Button) view.findViewById(R.id.order_btn_select_date);
        selectedDate = (TextView) view.findViewById(R.id.order_show_selected_date);
        listView = (ListView) view.findViewById(R.id.order_appointment_list);
        progressBar = (ProgressBar) view.findViewById(R.id.order_progressBar);

        final Calendar calendar = Calendar.getInstance();
        context = view.getContext();
        calendar_year = calendar.get(Calendar.YEAR);
        calendar_month = calendar.get(Calendar.MONTH) + 1;
        calendar_day = calendar.get(Calendar.DAY_OF_MONTH);

        arguments = getArguments();
        if(arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task details.");
            Log.d(TAG, "onCreateView: " + arguments.getInt("Year"));
            
        } else {
            Log.d(TAG, "onCreateView: first time here");
        }

        if (savedInstanceState != null) {
            frameLayout.removeAllViews();
            calendar_day = savedInstanceState.getInt("Selected Day");
            calendar_month = savedInstanceState.getInt("Selected Month");
            calendar_year = savedInstanceState.getInt("Selected Year");
            Log.d(TAG, "onViewCreated: recover " + calendar_day + "/" + (calendar_month) + "/" + calendar_year);

            if (calendar_day != 0 && calendar_month != 0 && calendar_year != 0) {
                Log.d(TAG, "onViewCreated: in " + calendar_day + "/" + (calendar_month) + "/" + calendar_year);

                selectedDate.setText(calendar_day + "/" + (calendar_month) + "/" + calendar_year);
                GetAppointmentListData.getData(context, calendar_day, calendar_month, calendar_year, listView, progressBar);
            } else {
                calendar_year = calendar.get(Calendar.YEAR);
                calendar_month = calendar.get(Calendar.MONTH) + 1;
                calendar_day = calendar.get(Calendar.DAY_OF_MONTH);
            }
        }

        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        dateSetListener, calendar_year, calendar_month - 1, calendar_day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dateSelected = true;
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dateSelected = false;
                    }
                });
                dialog.show();
                dialog.getDatePicker().setSpinnersShown(true);
                dateSelected = true;
                Log.d(TAG, "onClick: click select button");
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar_year = year;
                calendar_day = dayOfMonth;
                calendar_month = month + 1;
                selectedDate.setText(calendar_day + "/" + (calendar_month) + "/" + calendar_year);
                arguments.putInt("Day",calendar_day);
                arguments.putInt("Month",calendar_month);
                arguments.putInt("Year",calendar_year);

                GetAppointmentListData.getData(context, calendar_day, calendar_month, calendar_year, listView, progressBar);
            }
        };

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.order_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: here!");
                listView.setAdapter(null);
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
        Log.d(TAG, "onSaveInstanceState: " + calendar_day + "/" + (calendar_month) + "/" + calendar_year);
        if(dateSelected) {
            outState.putInt("Selected Year", calendar_year);
            outState.putInt("Selected Month", calendar_month);
            outState.putInt("Selected Day", calendar_day);
        }
        super.onSaveInstanceState(outState);
    }


}
