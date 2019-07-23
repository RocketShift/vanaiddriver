package com.example.vanaiddriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanaiddriver.classes.Requestor;
import com.example.vanaiddriver.classes.VehicleModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class SelectVehicle extends AppCompatActivity {
    private ListView lvVehicles;
    private SelectVehicle.VehicleAdapter vehicleAdapter;
    private List<VehicleModel> vehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvVehicles = (ListView)findViewById(R.id.lvVehicles);


        Requestor getVehicles = new Requestor("api/vehicles", null, this){
            @Override
            public void postExecute(JSONObject response){

                try {
                    Gson gson = new Gson();
                    String jsonOutput = response.getJSONArray("vehicles").toString();
                    Type listType = new TypeToken<List<VehicleModel>>(){}.getType();
                    vehicles = gson.fromJson(jsonOutput, listType);
                    vehicleAdapter = new SelectVehicle.VehicleAdapter(getApplicationContext(), R.layout.vehicle_item, vehicles);
                    lvVehicles.setAdapter(vehicleAdapter);

                    lvVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent data = new Intent();
                            data.putExtra("vehicle", vehicles.get(i));
                            setResult(RESULT_OK,data);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        getVehicles.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class VehicleAdapter extends ArrayAdapter {
        private List<VehicleModel> vehicleModel;
        private int resource;
        private LayoutInflater inflater;


        public VehicleAdapter(Context context, int resource, List<VehicleModel> objects) {
            super(context, resource, objects);
            this.vehicleModel = objects;
            this.resource = resource;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SelectVehicle.VehicleAdapter.ViewHolder holder = null;
            if(convertView == null){
                holder = new SelectVehicle.VehicleAdapter.ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.tvPlateNo = (TextView)convertView.findViewById(R.id.tvPlateNo);
                holder.tvColor = (TextView)convertView.findViewById(R.id.tvColor);
                convertView.setTag(holder);
            }else{
                holder = (SelectVehicle.VehicleAdapter.ViewHolder)convertView.getTag();
            }

            holder.tvPlateNo.setText(vehicleModel.get(position).getPlate_no());
            holder.tvColor.setText(vehicleModel.get(position).getColor());

            return convertView;
        }

        class ViewHolder{
            private TextView tvPlateNo;
            private TextView tvColor;
        }
    }
}
