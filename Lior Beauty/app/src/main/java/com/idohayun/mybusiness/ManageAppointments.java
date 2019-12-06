package com.idohayun.mybusiness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class ManageAppointments extends Fragment {
    private static final String TAG = "ManageAppointments";

    public ManageAppointments() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manage_appointments, container, false);

        final SwipeMenuListView listView = view.findViewById(R.id.manage_appointment_list);
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.manage_appointment_listview_refresh);

        getData(view,listView);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(view,listView);
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void getData(final View view, final SwipeMenuListView listView) {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        final List<DateArray> dateArrayList = new ArrayList<>();
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_manage_appointments);
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("GetAll", " ");
        Log.d(TAG, "onDateSet: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.Appointment_get_all_appointment_manager, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int func_day, func_month, func_year, func_hour, func_min, func_personID ,
                                    func_tempAvailable, func_userId , func_pending_approve , func_type;
                            String convertTypeToInt_ifNull;
                            boolean available;
                            dateArrayList.clear();
                            String s = response.getString("data");
                            StringBuilder sb = new StringBuilder(s);
                            JSONArray jsonArray = new JSONArray(s);
                            Log.d(TAG, "onResponse: " + jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Log.d(TAG, "onResponse: " + obj.toString());
                                func_day = obj.getInt("Day");
                                func_month = obj.getInt("Month");
                                func_year = obj.getInt("Year");
                                func_hour = obj.getInt("Hour");
                                func_min = obj.getInt("Min");
                                func_personID = obj.getInt("PersonID");
                                func_tempAvailable = obj.getInt("Available");
                                func_userId = obj.getInt("OrderPersonAppID");
                                convertTypeToInt_ifNull = obj.getString("Type");
                                try {
                                    func_pending_approve = Integer.parseInt(obj.getString("PendingApproval"));
                                } catch (Exception e) {
                                    func_pending_approve = 0;
                                }
                                if(convertTypeToInt_ifNull == null || convertTypeToInt_ifNull.equals(" ") || convertTypeToInt_ifNull.equals("")) {
                                    func_type = -1;
                                } else {
                                    func_type = Integer.parseInt(convertTypeToInt_ifNull);
                                }
                                available = (func_tempAvailable == 1 ? TRUE : FALSE);
                                DateArray dateArray = new DateArray(func_day, func_month, func_year, func_hour, func_min, func_type, func_personID, available,func_userId,func_pending_approve);
                                dateArrayList.add(dateArray);
                                Log.d(TAG, "onResponse: creating (" + i + ") dates dateArray= " +  dateArray.toString() + " type: " + func_type);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            Collections.sort(dateArrayList, new Comparator() {
                                public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
                                    return ((DateArray) synchronizedListOne).getHour() - (((DateArray) synchronizedListTwo).getHour());
                                }
                            });
                            DatesListAdapterManager datesListAdapter
                                    = new DatesListAdapterManager(view.getContext(), R.layout.manage_appointment_list_adapter, dateArrayList,listView,progressBar);
                            listView.setAdapter(datesListAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(view.getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}
