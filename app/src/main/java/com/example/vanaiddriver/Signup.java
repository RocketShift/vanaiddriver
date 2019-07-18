package com.example.vanaiddriver;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.example.vanaiddriver.classes.ErrorMessages;
import com.example.vanaiddriver.classes.Requestor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    private Button submit;
    private AppCompatEditText username;
    private AppCompatEditText name;
    private AppCompatEditText address;
    private AppCompatEditText email;
    private AppCompatEditText phone;
    private AppCompatEditText password;
    private AppCompatEditText password_confirmation;
    ErrorMessages errors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);
        password_confirmation = findViewById(R.id.password_confirmation);
        submit = findViewById(R.id.button2);
        submit.setOnClickListener(this);

        errors = new ErrorMessages(Signup.this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(submit)) {
            Button button = (Button) view;
            if (button.getText().equals(getString(R.string.login_text2))) {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("username", username.getText());
            param.put("name", name.getText());
            param.put("address", address.getText());
            param.put("email", email.getText());
            param.put("phonenumber", phone.getText());
            param.put("password", password.getText());
            param.put("password_confirmation", password_confirmation.getText());

            Requestor requestor = new Requestor("api/register", param, this) {
                @Override
                public void preExecute() {
                    submit.setText(R.string.please_wait);
                }

                @Override
                public void postExecute(JSONObject result) {
                    submit.setText(R.string.login_text2);

                    errors.showErrors(result);

                    try {
                        JSONObject user = result.getJSONObject("user");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.putExtra("username", user.get("username").toString());
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            requestor.execute();
        }
        }
    }

//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            // pre-condition
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//    }
}
