package com.example.vanaiddriver.classes;

import android.app.Activity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ErrorMessages {
    private Activity activity;
    private ArrayList<String> textinputlayouts = new ArrayList<String>();

    public ErrorMessages(Activity activity){
        this.activity = activity;
    }

    public void showErrors(JSONObject response){
        if(!textinputlayouts.isEmpty()){
            for (int i = 0; i < textinputlayouts.size(); i++) {
                TextInputLayout view = activity.findViewById(activity.getApplicationContext().getResources().getIdentifier(textinputlayouts.get(i), "id", activity.getApplicationContext().getPackageName()));
                view.setErrorEnabled(false);
            }
        }
        try {
            JSONObject errors = response.getJSONObject("errors");
            Iterator<String> keys = errors.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                if (errors.getJSONArray(key) != null) {
                    JSONArray messages = errors.getJSONArray(key);
                    if(!textinputlayouts.contains(key + "_layout")) {
                        textinputlayouts.add(key + "_layout");
                    }
                    TextInputLayout view = activity.findViewById(activity.getApplicationContext().getResources().getIdentifier(key + "_layout", "id", activity.getApplicationContext().getPackageName()));
                    view.setError(messages.get(0).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
