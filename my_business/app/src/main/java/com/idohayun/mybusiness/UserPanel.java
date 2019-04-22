package com.idohayun.mybusiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPanel extends Fragment {
    private String changedPassword, changedName;
    private int changedPhone;
    private TextView input_password, input_phone;
    private baseUSER user = new baseUSER();
    private static final String TAG = "UserPanel";
    private boolean allDataIsOK = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.changeTitlePage(inflater.getContext().getResources().getString(R.string.text_tools_title));
        return inflater.inflate(R.layout.fragment_tools_user_panel_exist, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(view.getResources().getColor(R.color.colorBackground,null));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        Guideline centerGuideLine = view.findViewById(R.id.center_guideline);
        centerGuideLine.setGuidelineBegin((displayWidth - (int)(20*displayMetrics.density))/2);

        user.getUserDetails(view);
        final String userName = user.getName();
        int phone = user.getPhone();
        int id = user.getId();
        final TextView text_error = (TextView) view.findViewById(R.id.text_error);
        Log.d(TAG, "onViewCreated: user id " + id);
        Button btnOK = view.findViewById(R.id.btn_save_data);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        final TextView title = (TextView) view.findViewById(R.id.text_welcome);
        String string = getString(R.string.tools_welcome_text, user.getName());
        title.setText(string);

        input_password = view.findViewById(R.id.user_panel_password);
        final TextView input_username = view.findViewById(R.id.user_panel_user_name);
        input_phone = view.findViewById(R.id.user_panel_phone);

        input_phone.setHint(Integer.toString(phone));
        input_username.setText(userName);
        input_password.setHint(getString(R.string.change_password_hint));


        btnOK.setVisibility(View.VISIBLE);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: OK! saved data...");
                String l_password = input_password.getText().toString(), l_phone = input_phone.getText().toString(),
                        l_username = input_username.getText().toString();
                String errorBuilder = " ";
                boolean phoneIsGood = true, passwordIsGood = true, userIsGood = true;

                if(l_password.isEmpty()) {
                    changedPassword = user.getPassword();
                } else if (l_password.length() >= 8) {
                    changedPassword = l_password;
                } else {
                    passwordIsGood = false;
                    errorBuilder += getString(R.string.error_password_is_less_or_invalid);
                }

                if(l_username.isEmpty()) {
                    changedName = user.getPassword();
                } else if (l_username.length() >= 3) {
                    changedName = l_username;
                } else {
                    errorBuilder += getString(R.string.error_username_is_less);
                    userIsGood = false;
                }

                if(l_phone.isEmpty()) {
                    changedPhone = user.getPhone();
                } else if (baseUSER.validPhone(l_phone)) {
                    changedPhone = Integer.parseInt(l_phone);
                } else {
                    errorBuilder += getString(R.string.error_phone_is_invalid);
                    phoneIsGood = false;
                }

                if (userIsGood && passwordIsGood && phoneIsGood) {
                    allDataIsOK = true;
                }

                if (allDataIsOK && user.updateUserData(changedName, changedPhone, changedPassword)) {
                    Log.d(TAG, "onClick: update successfully!");
                    String string = getString(R.string.tools_welcome_text, user.getName());
                    title.setText(string);
                } else {
                    CustomToast.showToast(getContext(),getString(R.string.update_user_data_unsuccessfully),0);
                }

                if (errorBuilder.isEmpty()) {
                    text_error.setVisibility(View.INVISIBLE);
                } else {
                    text_error.setText(errorBuilder);
                    text_error.setTextColor(getResources().getColor(R.color.toast_error, null));
                    text_error.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.logout()) {
                    CustomToast.showToast(getContext(), getString(R.string.user_logout_success), 1);
                    try {
                        getFragmentManager().popBackStack();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onClick: " + e.getMessage());
                    }

                }
                else {
                    CustomToast.showToast(getContext(),getString(R.string.user_logout_failed),0);
                }
            }
        });

        showMyAppointments(view);
    }

    private void showMyAppointments(final View view) {
        final ProgressBar progressBar = new ProgressBar(view.getContext());
        final SwipeMenuListView listView = view.findViewById(R.id.manage_appointment_list);
        final SwipeRefreshLayout swipeRefreshLayout;
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Map<String,String> map = new HashMap<>();
        map.put("UserID", Integer.toString(user.getId()));
        Log.d(TAG, "onClick: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);
        swipeRefreshLayout = view.findViewById(R.id.manage_appointment_listview_refresh);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.Appointment_get_all_user_appointments, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                StringBuilder sb = new StringBuilder();
                                String s = response.getString("data");
                                sb.append(s);
                                JSONArray jsonArray = new JSONArray(s);
                                List<DateArray> listOfAppointments = new ArrayList<>();
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                int func_id, func_day, func_month, func_year, func_hour , func_min, func_approved, func_type;
                                for (int i = 0 ; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    func_id = obj.getInt("PersonID");
                                    func_day = obj.getInt("Day");
                                    func_month = obj.getInt("Month");
                                    func_year = obj.getInt("Year");
                                    func_hour = obj.getInt("Hour");
                                    func_min = obj.getInt("Min");
                                    func_type = obj.getInt("Type");
                                    func_approved = obj.getInt("PendingApproval");
                                    listOfAppointments.add(new DateArray(func_day,func_month,func_year,func_hour,func_min,func_type,func_id,false,user.getId(),func_approved));
                                }

                                DatesListAdapterPersonal adapterPersonal = new DatesListAdapterPersonal
                                        (view.getContext(),R.layout.order_list_adapter_personal,listOfAppointments,listView,progressBar);
                                listView.setAdapter(adapterPersonal);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
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
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                        Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}
