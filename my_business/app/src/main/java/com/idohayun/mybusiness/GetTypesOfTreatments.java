package com.idohayun.mybusiness;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class GetTypesOfTreatments  {
    private static final String TAG = "GetTypesOfTreatments";
    private static String serverUrl = "http://eidoil32.myhf.in/getAppointments.php";
    private static Context context;
    private static Spinner spinner;
    private static StringBuilder sb = new StringBuilder();
    private static List<appointment> typeList;

    public void getListOfTypes(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;

        GetJSON_data();
    }

    private void GetJSON_data() {
        class getJSON_data extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (!s.isEmpty()) {
                        loadIntoSpinner(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    con.disconnect();
                    return sb.toString().trim();
                } catch (
                        Exception e) {
                    return null;
                }
            }
        }

        getJSON_data getJSON = new getJSON_data();
        getJSON.execute();
    }

    private void loadIntoSpinner(String JSON_Data) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSON_Data);
        String description, lang;
        int price;
        String deviceLocale = Locale.getDefault().getLanguage();
        typeList = new ArrayList<>();
        typeList.add(new appointment(context.getString(R.string.text_please_choose_type),null,0));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            description = obj.getString("Description");
            lang = obj.getString("Lang");
            price = obj.getInt("Price");
            if((deviceLocale.equals("en") && lang.equals("English")) || (deviceLocale.equals("iw") && lang.equals("Hebrew")))
                typeList.add(new appointment(description,lang,price));
        }

        Log.d(TAG, "loadIntoSpinner: TypeList size: " + typeList.size());
        SpinnerAdapter adapter = new SpinnerAdapter(context,R.layout.adapter_spinner_treatment_types,typeList);
        spinner.setAdapter(adapter);
    }


}
