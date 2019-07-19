package com.example.vanaiddriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanaiddriver.classes.Requestor;
import com.example.vanaiddriver.classes.RouteModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;

public class SelectRoute extends AppCompatActivity {
    private ListView lvRoutes;
    private RouteAdapter routeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        lvRoutes = (ListView)findViewById(R.id.lvRoutes);


        Requestor getRoutes = new Requestor("api/routes", null, this){
            @Override
            public void postExecute(JSONObject response){

                try {
                    Gson gson = new Gson();
                    String jsonOutput = response.getJSONArray("routes").toString();
                    Type listType = new TypeToken<List<RouteModel>>(){}.getType();
                    List<RouteModel> posts = gson.fromJson(jsonOutput, listType);
                    routeAdapter = new RouteAdapter(getApplicationContext(), R.layout.route_item, posts);
                    lvRoutes.setAdapter(routeAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        getRoutes.execute();
    }

    public class RouteAdapter extends ArrayAdapter {
        private List<RouteModel> routeModel;
        private int resource;
        private LayoutInflater inflater;


        public RouteAdapter(Context context, int resource, List<RouteModel> objects) {
            super(context, resource, objects);
            this.routeModel = objects;
            this.resource = resource;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.tvRouteName = (TextView)convertView.findViewById(R.id.tvRouteName);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvRouteName.setText(routeModel.get(position).getName());

            return convertView;
        }

        class ViewHolder{
            private TextView tvRouteName;
        }
    }
}
