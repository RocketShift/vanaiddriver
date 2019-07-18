package com.example.vanaiddriver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.example.vanaiddriver.classes.Requestor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    final private String GRANT_TYPE = "password";
    final private Integer CLIENT_ID = 3;
    final private String CLIENT_SECRET = "aczZI6bURpYjal4pgyoB3ZbW6kIMsJwzkyLpHzco";
    final private String SCOPE = "*";
    private Button mButton;
    private Button mButton2;
    private AppCompatEditText etUsername;
    private AppCompatEditText etPassword;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mButton = (Button) findViewById(R.id.login_button);
        mButton2 = (Button) findViewById(R.id.signup_button);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        alertDialog = new AlertDialog.Builder(Login.this).create();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> param = new LinkedHashMap<>();
                param.put("username", etUsername.getText().toString());
                param.put("password", etPassword.getText().toString());
                param.put("grant_type", GRANT_TYPE);
                param.put("client_id", CLIENT_ID);
                param.put("client_secret", CLIENT_SECRET);
                param.put("scope", SCOPE);

                Requestor requestor = new Requestor("oauth/token", param, getApplicationContext()){
                    @Override
                    public void preExecute() {
                        mButton.setText(R.string.please_wait);
                    }

                    @Override
                    public void postExecute(JSONObject response) {
                        mButton.setText(R.string.login_bttext);
                        try {
                            response.get("error");
                            alertDialog.setTitle(getApplicationContext().getString(R.string.invalidusernameorpassword));
                            alertDialog.setMessage(getApplicationContext().getString(R.string.pleasetryagain));
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String access_token = response.getString("access_token");
                            SharedPreferences.Editor editor = getSharedPreferences(Requestor.SHARED_REFERENCES, MODE_PRIVATE).edit();
                            editor.putString("access_token", access_token);
                            editor.apply();

                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                requestor.execute();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity2();
            }
        });

        Intent signup = getIntent();
        String username = signup.getStringExtra("username");
        if(username != null){
            alertDialog.setTitle(getApplicationContext().getString(R.string.signup_successful));
            alertDialog.setMessage(getApplicationContext().getString(R.string.cannowlogin));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            etUsername.setText(username);
        }

    }
    public void open_activity(){
        Intent intent = new Intent(this, Home2.class);
        startActivity(intent);
    }
    public void open_activity2(){
        Intent intent2 = new Intent(this, Signup.class);
        startActivity(intent2);
    }
}
