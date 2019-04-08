package com.idohayun.manageapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageAppointmentTypes extends Fragment {
    private static final String TAG = "ManageAppointmentTypes";
    private static ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetInformation getInformation = new GetInformation();
    private static Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab2_mng_appointment_types, container, false);

        context = view.getContext();
        listView = (ListView)view.findViewById(R.id.list_appointment_types);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);

        getInformation = new GetInformation();
        getInformation.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                listView.setAdapter(null);
//                getInformation.execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private static class GetInformation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(s);
                    List<AppointmentTypes> list = new ArrayList<>();
                    String lang, price, description;
                    int id;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.d(TAG, "onPostExecute: " + obj.toString());
                        id = obj.getInt("ID");
                        description = obj.getString("Description");
                        lang = obj.getString("Lang");
                        price = obj.getString("Price");
                        list.add(new AppointmentTypes(lang,price,description,id));
                    }

                    TypeListAdapter adapter = new TypeListAdapter(context,R.layout.adapter_list_appointment_type,listView,list);
                    listView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = ServerURLManager.Appointments_get_all_types;
            try {
                java.net.URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
