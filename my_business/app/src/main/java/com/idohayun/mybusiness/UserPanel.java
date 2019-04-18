package com.idohayun.mybusiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
                } else if (l_password.length() >= 3) {
                    changedPassword = l_password;
                } else {
                    passwordIsGood = false;
                }

                if(l_username.isEmpty()) {
                    changedPassword = user.getPassword();
                } else if (l_username.length() >= 3) {
                    changedPassword = l_username;
                } else {
                    userIsGood = false;
                }

                if(l_phone.isEmpty()) {
                    changedPassword = user.getPassword();
                } else if (baseUSER.validPhone(l_phone)) {
                    changedPassword = l_phone;
                } else {
                    errorBuilder += getString(R.string.error_password_is_less_or_invalid);
                    userIsGood = false;
                }


                if (allDataIsOK && user.updateUserData(changedName, changedPhone, changedPassword)) {
                    Log.d(TAG, "onClick: update successfully!");
                    String string = getString(R.string.tools_welcome_text, user.getName());
                    title.setText(string);
                } else {
                    Log.d(TAG, "onClick: update failed!");
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
    }
}
