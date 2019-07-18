package com.example.vanaiddriver.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Requestor {
    private Boolean isRunning = false;
    private Map<String, Object> param;
    private Boolean asynchronus = false;
    private Integer PAGE = null;
    private String url = "https://vanaid.000webhostapp.com/";
    private Context context;
    final public static String SHARED_REFERENCES = "vanaidpreferences";

    public Requestor(String url, Map<String, Object> param, Context context){
        this.url = this.url + url;
        this.context = context;

        if(param == null){
            this.param = new LinkedHashMap<>();
        }else{
            this.param = param;
        }
    }

    public void execute(){
        if(PAGE != null){
            param.put("page", PAGE);
        }

        Boolean network = isNetworkAvailable();
        if (network == true) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wInfo = wifiManager.getConnectionInfo();
//            String macAddress = wInfo.getMacAddress();
//            if(macAddress.equals("02:00:00:00:00:00")){
//                macAddress = getWifiMacAddress();
//            }
//            param.put("macaddress", macAddress);
            param.put("type", "driver");
            if (isRunning == true && asynchronus == true) {
                new Ajaxer().execute();
            }else{
                new Ajaxer().execute();
            }
        } else {
            onNetworkError();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public void onNetworkError(){
        Toast.makeText(context, "Could not connect to the internet.", Toast.LENGTH_LONG).show();
    }

    public static byte[] urlParams(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        return postDataBytes;
    }

    public void preExecute(){
        Log.d("Requestor", "pre-executed");
    }

    public void postExecute(JSONObject response){
        Log.d("Requestor", "Post Execute: " + response);
    }

    public void cancelled(){
        Log.d("Requestor", "cancelled");
    }

    public class Ajaxer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;
            preExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isRunning = false;
            cancelled();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Requestor.SHARED_REFERENCES, MODE_PRIVATE);
                String access_token = sharedPreferences.getString("access_token", null);
                String username = sharedPreferences.getString("username", null);
                if(access_token != null){
                    param.put("username", username);
                }

                byte[] postDataBytes = urlParams(param);
                java.net.URL urlj = new URL(url);
                connection = (HttpURLConnection) urlj.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                connection.setRequestProperty("Authorization", "Bearer " + access_token);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.getOutputStream().write(postDataBytes);
                connection.setConnectTimeout(30000);
                connection.connect();
                int status = connection.getResponseCode();
                InputStream stream = null;
                if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    stream = connection.getInputStream();
                } else {
                    stream = connection.getErrorStream();
                }
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString().trim();
                return finalJson;
            } catch (MalformedURLException e) {
                cancel(true);
            } catch (IOException e) {

                cancel(true);
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    cancel(true);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isRunning = false;
            try {
                JSONObject response = new JSONObject(s);
                postExecute(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
