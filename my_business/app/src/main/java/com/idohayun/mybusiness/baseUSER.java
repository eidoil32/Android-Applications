package com.idohayun.mybusiness;

import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import java.util.Random;

public class baseUSER {
    private static final String TAG = "baseUSER";
    private String name, password;
    private int phone, id;
    private boolean exist, isGuest;
    private View view;
    private static StringBuilder sb = new StringBuilder();
    private DataBaseManager dbHelper;
    private static NavigationView navigationView;

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

    public static void setNavigationView(NavigationView i_navigationView) {
        navigationView = i_navigationView;
    }

    public void getUserDetails(View view) {
        createDBHelper(view);

        Cursor cursor = dbHelper.getData();
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            name = cursor.getString(1);
            password = cursor.getString(2);
            phone = cursor.getInt(3);
            exist = true;
            if(name.contains("Guest")) {
                this.isGuest = true;
            }
        } else {
            Log.d(TAG, "getUserDetails: user doesn't exist!");
            this.id = -2;
            this.exist = false;
            this.isGuest = true;
        }

        if(navigationView != null) {
            if(id == 1) {
                navigationView.getMenu().setGroupVisible(R.id.group_manager,true);
            } else {
                navigationView.getMenu().setGroupVisible(R.id.group_manager, false);
            }
        }

        Log.d(TAG, "getUserDetails: username: " + name + " guest? " + isGuest);
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public void setGuestUser(View view, int phone, String name, Map<String, String> map) {
        this.password = "no_password";
        this.phone = phone;
        Random r = new Random();
        this.name = "Guest_" + r.nextInt(10000);
        updateIDFromServer(map);
        this.exist = true;
        this.isGuest = true;
    }

    private void updateIDFromServer(final Map<String, String> i_map) {
        final baseUSER currentUser = this;
        currentUser.setId(-2);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Map<String, String> map = new HashMap<>();
        map.put("LastID", " ");
        final JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_get_last_id, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                currentUser.setId(Integer.parseInt(response.getString("data")));
                                currentUser.saveDataToPhone();
                                i_map.put("UserID", Integer.toString(currentUser.getId()));
                            } else {
                                Log.d(TAG, "onResponse: error from getLastUserID php");
                                currentUser.setId(-2);
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

    public boolean updateUserData(String i_username, int i_phone, String i_password) {
        this.password = i_password;
        this.phone = i_phone;
        this.name = i_username;

        Map<String, String> update = new HashMap<>();
        update.put("UserID", Integer.toString(this.id));
        update.put("Password", password);
        update.put("Phone", Integer.toString(phone));
        update.put("UserName", name);

        Log.d(TAG, "updateUserData: guest state: " + isGuest);
        if (isGuest) {
            Log.d(TAG, "updateUserData: is guest");
            addLocalUserToDB();
        } else {
            Log.d(TAG, "updateUserData: isn't guest");
            func_updateToServerDB(update);
        }

        return dbHelper.updateData(this);
    }

    private void addLocalUserToDB() {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Map<String, String> data = new HashMap<>();
        data.put("UserName", this.name);
        data.put("Password", this.password);
        data.put("Phone", Integer.toString(this.phone));
        data.put("UserID",Integer.toString(this.id));
        Log.d(TAG, "onDateSet: " + data.toString());
        final JSONObject jsonObject = new JSONObject(data);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_register_new_user, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                String s = response.getString("data");
                                sb.append(s);
                                Log.d(TAG, "onResponse: " + s);
                                JSONArray jsonArray = new JSONArray(s);
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                JSONObject obj = jsonArray.getJSONObject(0);
                                setGuest(false);
                            } else {
                                String errorMessageFromServer = response.getString("message");
                                if (errorMessageFromServer.equals("user_already_exist"))
                                    CustomToast.showToast(view.getContext(),
                                            view.getResources().getString(R.string.register_user_already_exist), 0);
                                Log.d(TAG, "onResponse: failed!, message: " + errorMessageFromServer);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: exception!!!");
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

    private void func_updateToServerDB(Map<String, String> data) {
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
                            if (response.getString("status").equals("true")) {
                                CustomToast.showToast(view.getContext(), "update completed!", 1);
                            } else {
                                CustomToast.showToast(view.getContext(), "update failed!", 0);
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

    public void add_newUserToDB() {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        Map<String, String> data = new HashMap<>();
        data.put("UserName", this.name);
        data.put("Password", this.password);
        data.put("Phone", Integer.toString(this.phone));
        Log.d(TAG, "onDateSet: " + data.toString());
        final JSONObject jsonObject = new JSONObject(data);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.User_manager_register_new_user, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                String s = response.getString("data");
                                sb.append(s);
                                Log.d(TAG, "onResponse: " + s);
                                JSONArray jsonArray = new JSONArray(s);
                                Log.d(TAG, "onResponse: " + jsonArray.toString());
                                JSONObject obj = jsonArray.getJSONObject(0);
                                setId(obj.getInt("UserID"));
                                saveDataToPhone();
                            } else {
                                String errorMessageFromServer = response.getString("message");
                                if (errorMessageFromServer.equals("user_already_exist"))
                                    CustomToast.showToast(view.getContext(),
                                            view.getResources().getString(R.string.register_user_already_exist), 0);
                                Log.d(TAG, "onResponse: failed!, message: " + errorMessageFromServer);
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
        if (dbHelper.addData(this))
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

    public boolean validData(TextView errorText) {
        StringBuilder buildErrorMessage = new StringBuilder();
        boolean foundSomethingWrong = false;
        String phoneString = Integer.toString(phone);
        if (phoneString.length() != 9 || phone < 0 || firstTwoDigitsNotExist(phoneString)) {
            buildErrorMessage.append(view.getResources().getString(R.string.error_phone_is_invalid));
            buildErrorMessage.append("\n");
            foundSomethingWrong = true;
        }
        if (name.length() < 3) {
            buildErrorMessage.append(view.getResources().getString(R.string.error_username_is_less));
            buildErrorMessage.append("\n");
            foundSomethingWrong = true;
        }
        if (password.length() < 8) {
            buildErrorMessage.append(view.getResources().getString(R.string.error_password_is_less));
            buildErrorMessage.append("\n");
            foundSomethingWrong = true;
        }
        errorText.setText(buildErrorMessage);
        return !foundSomethingWrong;
    }

    public static boolean validPhone(String phone) {
        if(phone.length() < 8 || phone.length() > 10) {
            return firstTwoDigitsNotExist(phone);
        }
        return false;
    }

    public static boolean firstTwoDigitsNotExist(String phoneString) {
        String firstTwoDigits = Character.toString(phoneString.charAt(0)) + Character.toString(phoneString.charAt(1));
        int twoDigit = Integer.parseInt(firstTwoDigits);

        if ((twoDigit < 50 || twoDigit > 59))
            if (twoDigit < 64 || twoDigit > 69)
                return true;

        return false;
    }
}
