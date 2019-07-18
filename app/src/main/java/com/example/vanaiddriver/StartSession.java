package com.example.vanaiddriver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class StartSession extends AppCompatActivity implements View.OnClickListener {
    public View account_route_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        account_route_info = (View) findViewById(R.id.account_route_info);
        account_route_info.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(account_route_info)){
            Intent intent = new Intent(this,SelectRoute.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
