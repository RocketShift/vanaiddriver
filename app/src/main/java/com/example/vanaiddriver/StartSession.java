package com.example.vanaiddriver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanaiddriver.classes.RouteModel;
import com.example.vanaiddriver.classes.VehicleModel;

public class StartSession extends AppCompatActivity implements View.OnClickListener {
    public View account_route_info;
    public View vehicle_info;
    public TextView tvRouteName;
    public TextView tvVehiclePlate;
    public TextView tvVehicleColor;
    public RouteModel route;
    public VehicleModel vehicle;
    public Button btnStart;
    public AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        tvRouteName = (TextView) findViewById(R.id.tvRouteName);
        tvVehiclePlate = (TextView) findViewById(R.id.tvVehiclePlate);
        tvVehicleColor = (TextView) findViewById(R.id.tvVehicleColor);
        account_route_info = (View) findViewById(R.id.account_route_info);
        vehicle_info = (View) findViewById(R.id.vehicle_info);
        btnStart = (Button) findViewById(R.id.btnStart);
        alertDialog = new AlertDialog.Builder(this).create();

        account_route_info.setOnClickListener(this);
        vehicle_info.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(account_route_info)){
            Intent intent = new Intent(this,SelectRoute.class);
            startActivityForResult(intent, 1);
        }else if(view.equals(vehicle_info)){
            Intent intent = new Intent(this,SelectVehicle.class);
            startActivityForResult(intent, 2);
        }else if(view.equals(btnStart)){
            if(route == null){
                alertDialog.setTitle(getApplicationContext().getString(R.string.routeisrequired));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }else if(vehicle == null){
                alertDialog.setTitle(getApplicationContext().getString(R.string.vehicleisrequired));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                route = (RouteModel) data.getExtras().getSerializable("route");
                tvRouteName.setText(route.getName());
            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                vehicle = (VehicleModel) data.getExtras().getSerializable("vehicle");
                tvVehiclePlate.setText(vehicle.getPlate_no());
                tvVehicleColor.setText(vehicle.getColor());
            }
        }
    }
}
