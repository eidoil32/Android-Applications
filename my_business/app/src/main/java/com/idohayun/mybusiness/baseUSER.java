package com.idohayun.mybusiness;

import android.database.Cursor;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class baseUSER {
    private static final String TAG = "baseUSER";
    private String name, password;
    private int phone, id;
    private boolean exist;
    private View view;
    private static StringBuilder sb = new StringBuilder();
    private DataBaseManager dbHelper;

    public void setView(View view) {
        this.view = view;
    }

    public void createDBHelper(View view) {
        dbHelper = new DataBaseManager(view.getContext());
        this.view = view;
    }

    public String print() {
        return "Name: " + name + "Password: " + password + "Phone: " + phone + "UserID: " + id;
    }

    public void getUserDetails(View view) {
        createDBHelper(view);

        Cursor cursor = dbHelper.getData();
        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);
            name = cursor.getString(1);
            password = cursor.getString(2);
            phone = cursor.getInt(3);
            exist = true;
        } else {
            Log.d(TAG, "getUserDetails: user doesn't exist!");
            this.id = -1;
            this.exist = false;
        }
    }

    public boolean updateUserData(int i_phone, String i_password) {
        this.password = i_password;
        this.phone = i_phone;


        Map<String,String> update = new HashMap<>();
        update.put("UserID",Integer.toString(this.id));
        update.put("Password",password);
        update.put("Phone",Integer.toString(phone));
        func_updateToServerDB(update);

        return dbHelper.updateData(this);
    }

    private void func_updateToServerDB(Map<String,String> data) {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Log.d(TAG, "onDateSet: " + data.toString());
        final JSONObject jsonObject = new JSONObject(data);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_update_user_data, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("true")) {
                                CustomToast.showToast(view.getContext(),"update completed!",1);
                            } else {
                                CustomToast.showToast(view.getContext(),"update failed!",0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                    }
                });

        queue.add(request);
    }

    public void add_newUserToDB(){
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Map<String,String> data = new HashMap<>();
        data.put("UserName",this.name);
        data.put("Password",this.password);
        data.put("Phone",Integer.toString(this.phone));
        Log.d(TAG, "onDateSet: " + data.toString());
        final JSONObject jsonObject = new JSONObject(data);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_register_new_user, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("true")) {
                                String s = response.getString("data");
                                sb.append(s);
                                Log.d(TAG, "onResponse: " + s);
                                JSONArray jsonArray = new JSONArray(s);
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                JSONObject obj = jsonArray.getJSONObject(0);
                                setId(obj.getInt("UserID"));
                                saveDataToPhone();
                            } else {
                                Log.d(TAG, "onResponse: failed!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                    }
                });

        queue.add(request);
    }

    public void updateLocalDB() {
        saveDataToPhone();
    }

    private void saveDataToPhone() {
        if(dbHelper.addData(this))
            setExist(true);
        else
            setExist(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean logout() {
        return dbHelper.onDeleteData();
    }
}
