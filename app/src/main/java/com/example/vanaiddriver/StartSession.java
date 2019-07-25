package com.example.vanaiddriver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanaiddriver.classes.Requestor;
import com.example.vanaiddriver.classes.RouteModel;
import com.example.vanaiddriver.classes.SessionModel;
import com.example.vanaiddriver.classes.VehicleModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        }else if(view.equals(btnStart)) {
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
            }else{
                Map<String, Object> param = new LinkedHashMap<>();
                param.put("route_id", route.getId());
                param.put("vehicle_id", vehicle.getId());

                Requestor sessionRequest = new Requestor("api/sessions/store", param, this) {
                    @Override
                    public void preExecute() {
                        btnStart.setText(R.string.please_wait);
                    }

                    @Override
                    public void postExecute(final JSONObject response) {
                        btnStart.setText(R.string.start_new_session);

                        try {
                            String error = response.getString("error");
                            Log.e("Error", error);
                            alertDialog.setTitle(error);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            saveSession(response);
                                        }
                                    });
                            alertDialog.show();
                        } catch (JSONException e) {
                            saveSession(response);
                        }
                    }
                };

                sessionRequest.execute();
            }
        }
    }

    private void killActivity() {
        finish();
    }

    private void saveSession(JSONObject response){
        Gson gson = new Gson();
        String jsonOutput = null;
        try {
            jsonOutput = response.getJSONObject("session").toString();
            Type listType = new TypeToken<SessionModel>(){}.getType();
            SessionModel sessionModel = gson.fromJson(jsonOutput, listType);
            Log.e("Waypoints", route.getWaypoints().toString());
            sessionModel.setRoute(route);

            Gson gson2 = new Gson();
            String json = gson2.toJson(sessionModel);
            SharedPreferences.Editor editor = getSharedPreferences(Requestor.SHARED_REFERENCES, MODE_PRIVATE).edit();
            editor.putString("session", json);
            editor.apply();

            Intent data = new Intent();
            data.putExtra("session", sessionModel);
            setResult(RESULT_OK,data);
            killActivity();
        } catch (JSONException e) {
            e.printStackTrace();
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
