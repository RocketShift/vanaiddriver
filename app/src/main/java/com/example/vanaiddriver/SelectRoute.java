package com.example.vanaiddriver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.vanaiddriver.classes.Requestor;

import org.json.JSONObject;

public class SelectRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        Requestor getRoutes = new Requestor("api/routes", null, this){
            @Override
            public void postExecute(JSONObject response){
                Toast toast = Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG);
                toast.show();
            }
        };

        getRoutes.execute();
    }
}
