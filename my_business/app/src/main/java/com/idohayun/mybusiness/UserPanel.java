package com.idohayun.mybusiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class UserPanel extends Fragment {
    private String userName, password, changedPassword;
    private int phone, id, changedPhone;
    private Button btnOK, btnLogout;
    private TextView input_username, input_password, input_phone, title;
    private baseUSER baseUSER = new baseUSER();
    private static final String TAG = "UserPanel";
    private boolean phoneFlag = false, passwordFlag = false;

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

        baseUSER.getUserDetails(view);
        userName = baseUSER.getName();
        phone = baseUSER.getPhone();
        password = baseUSER.getPassword();
        id = baseUSER.getId();

        btnOK = view.findViewById(R.id.btn_save_data);
        btnLogout = view.findViewById(R.id.btn_logout);

        title = (TextView) view.findViewById(R.id.text_welcome);
        String string = getString(R.string.tools_welcome_text, baseUSER.getName());
        title.setText(string);

        input_password = view.findViewById(R.id.user_panel_password);
        input_username = view.findViewById(R.id.user_panel_user_name);
        input_phone = view.findViewById(R.id.user_panel_phone);

        input_phone.setHint(Integer.toString(phone));
        input_username.setText(userName);
        input_password.setHint(getString(R.string.change_password_hint));


        btnOK.setVisibility(View.VISIBLE);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: OK! saved data...");
                String l_password = input_password.getText().toString(), l_phone = input_phone.getText().toString();
                if (!l_password.isEmpty()) {
                    changedPassword = l_password;
                } else {
                    changedPassword = baseUSER.getPassword();
                }
                if (!l_phone.isEmpty()) {
                    changedPhone = Integer.parseInt(l_phone);
                } else {
                    changedPhone = baseUSER.getPhone();
                }

                if (baseUSER.updateUserData(changedPhone, changedPassword)) {
                    Log.d(TAG, "onClick: update successfully!");
                } else {
                    Log.d(TAG, "onClick: update failed!");
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(baseUSER.logout()) {
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
