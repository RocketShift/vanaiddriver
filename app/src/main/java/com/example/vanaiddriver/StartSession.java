package com.example.vanaiddriver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanaiddriver.classes.RouteModel;

public class StartSession extends AppCompatActivity implements View.OnClickListener {
    public View account_route_info;
    public TextView tvRouteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        tvRouteName = (TextView) findViewById(R.id.tvRouteName);
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

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                RouteModel routeModel = (RouteModel) data.getExtras().getSerializable("route");
                tvRouteName.setText(routeModel.getName());

                // OR
                // String returnedResult = data.getDataString();
            }
        }
    }
}
