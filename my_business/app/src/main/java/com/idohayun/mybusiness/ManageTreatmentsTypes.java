package com.idohayun.mybusiness;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ManageTreatmentsTypes extends Fragment {
    private static final String TAG = "ManageTreatmentsTypes";
    @SuppressLint("StaticFieldLeak")
    private static ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetInformation getInformation = new GetInformation();
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private RequestQueue queue;
    private JsonObjectRequest request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mng_treatment_types, container, false);
        Log.d(TAG, "onCreateView: created");

        context = view.getContext();
        listView = view.findViewById(R.id.list_appointment_types);
        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);

        Button btnAdd = view.findViewById(R.id.btn_add_new);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTreatmentType();
                getInformation = new GetInformation();
                getInformation.execute();
            }
        });

        getInformation = new GetInformation();
        getInformation.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setAdapter(null);
                getInformation = new GetInformation();
                getInformation.execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void addNewTreatmentType() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_edit_treatment);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        int orientation = context.getResources().getConfiguration().orientation;
        float multiple_Width = 0, multiple_Height = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "Portrait");
            multiple_Width = 0.8f;
            multiple_Height = 0.5f;
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "Landscape");
            multiple_Width = 0.7f;
            multiple_Height = 0.8f;
        }
        int dialogWindowWidth = (int)(displayWidth * multiple_Width);
        int dialogWindowHeight = (int)(displayHeight * multiple_Height);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        Guideline leftGuide = dialog.findViewById(R.id.guideline_left),
                rightGuide = dialog.findViewById(R.id.guideline_right),
                bottomGuide = dialog.findViewById(R.id.bottom_guideline);
        leftGuide.setGuidelineBegin(0);
        rightGuide.setGuidelineBegin(dialogWindowWidth - (int)(30*displayMetrics.density));
        bottomGuide.setGuidelineBegin(dialogWindowHeight - (int)(30*displayMetrics.density));

        Button btnOK = dialog.findViewById(R.id.btn_save_treatment);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnOK.setEnabled(true);
        btnCancel.setEnabled(true);

        final EditText hebrewDescription = dialog.findViewById(R.id.edit_text_lang_hebrew);
        final EditText englishDescription = dialog.findViewById(R.id.edit_text_lang_english);
        final EditText price = dialog.findViewById(R.id.edit_price);

        hebrewDescription.setHint(getResources().getString(R.string.hebrew_description_hint));
        englishDescription.setHint(getResources().getString(R.string.english_description_hint));
        price.setHint(getResources().getString(R.string.hint_price));

        TextView title = dialog.findViewById(R.id.text_title_popup_treatment);
        title.setText(Objects.requireNonNull(getContext()).getString(R.string.title_new_treatment_type));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hebrewInput, englishInput, priceInput;
                hebrewInput = hebrewDescription.getText().toString();
                englishInput = englishDescription.getText().toString();
                priceInput = price.getText().toString();
                if(hebrewInput.isEmpty() || englishInput.isEmpty() || priceInput.isEmpty()) {
                    Log.d(TAG, "onClick: error");
                } else { // every editText is filled
                    hebrewInput = TypeListAdapter.checkHebrewDescription(hebrewInput);
                    Map<String, String> editedMap = new HashMap<>();
                    editedMap.put("Description_Hebrew", hebrewInput);
                    editedMap.put("Description_English", englishInput);
                    editedMap.put("Price", priceInput);
                    queue = Volley.newRequestQueue(context);
                    Log.d(TAG, "onClick: 'New' button clicked!");
                    final JSONObject jsonObject = new JSONObject(editedMap);
                    request = new JsonObjectRequest(
                            Request.Method.POST, // the request method
                            ServerURLSManager.Appointment_add_new_appointment_type, jsonObject,
                            new Response.Listener<JSONObject>() { // the response listener
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("true")) {
                                            CustomToast.showToast(getContext(), context.getString(R.string.treatment_added_successfully), 1);
                                            Log.d(TAG, "onResponse: " + response.getString("data"));
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
                    ManageTreatmentsTypes.GetInformation getInformation = new ManageTreatmentsTypes.GetInformation();
                    getInformation.execute();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static class GetInformation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(s);
                    List<Treatment> list = new ArrayList<>();
                    String lang, price, description_Hebrew = null, description_English = null;
                    int id;
                    Treatment tempTreatment;
                    boolean whatLang; // true == hebrew, false == english

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.d(TAG, "onPostExecute: " + obj.toString());
                        id = obj.getInt("ID");
                        lang = obj.getString("Lang");
                        if(lang.equals("Hebrew")) {
                            description_Hebrew = obj.getString("Description");
                            whatLang = true;
                        } else {
                            description_English = obj.getString("Description");
                            whatLang = false;
                        }
                        price = obj.getString("Price");
                        tempTreatment = idIsAlreadyExist(id,list);
                        if(tempTreatment != null) {
                            if(whatLang) {
                                tempTreatment.setDescription_Hebrew(description_Hebrew);
                            } else {
                                tempTreatment.setDescription_English(description_English);
                            }
                        } else {
                            list.add(new Treatment(lang,price,description_Hebrew,description_English,id));
                        }
                    }

                    TypeListAdapter adapter = new TypeListAdapter(context,R.layout.adapter_list_treatments_type,listView,list);
                    listView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private Treatment idIsAlreadyExist(int id, List<Treatment> list) {

            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).getId() == id) {
                    return list.get(i);
                }
            }

            return null;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = ServerURLSManager.Appointments_get_all_types;
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json);
                    sb.append("\n");
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
