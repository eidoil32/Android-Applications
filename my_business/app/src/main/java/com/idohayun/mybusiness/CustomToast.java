package com.idohayun.mybusiness;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static void showToast(Context context,String message, int Type) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) ((Activity) context).findViewById(R.id.customToast));
        layout.setClipToOutline(true);

        TextView text = (TextView) layout.findViewById(R.id.customToastText);
        switch (Type) {
            case 0: //ERROR
                text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error,0,0,0);
                text.setBackground(context.getDrawable(R.drawable.custom_toast_background_error));
                break;
            case 1: //SUCCESS
                text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done,0,0,0);
                text.setBackground(context.getDrawable(R.drawable.custom_toast_background_success));
                break;
            default:
                text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notification,0,0,0);
                text.setBackground(context.getDrawable(R.drawable.custom_toast_background_message));
                break;
        }
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
