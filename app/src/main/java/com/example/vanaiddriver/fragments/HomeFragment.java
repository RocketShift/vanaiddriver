package com.example.vanaiddriver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vanaiddriver.R;
import com.example.vanaiddriver.StartSession;
import com.example.vanaiddriver.classes.RouteModel;
import com.example.vanaiddriver.classes.SessionModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeolocationApi;
import com.google.maps.RoadsApi;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.SnappedPoint;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private GoogleMap googleMap;
    private Button startSession;
    private SessionModel sessionModel;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                sessionModel = (SessionModel) data.getExtras().getSerializable("session");

                try {

                    Log.e("Origin", sessionModel.getRoute().getOrigin_lat() + "," + sessionModel.getRoute().getOrigin_lng());
                    Log.e("Destination", sessionModel.getRoute().getDestination_lat() + "," + sessionModel.getRoute().getDestination_lng());
                    DirectionsApiRequest request = DirectionsApi.newRequest(getGeoContext())
                            .mode(TravelMode.DRIVING)
                            .origin(sessionModel.getRoute().getOrigin_lat() + "," + sessionModel.getRoute().getOrigin_lng())
                            .destination(sessionModel.getRoute().getDestination_lat() + "," + sessionModel.getRoute().getDestination_lng())
                            .departureTime(new DateTime());

                    String waypoints = "place_id:";
                    for (int i = 0; i < sessionModel.getRoute().getWaypoints().size(); i++) {
                        request.waypoints("place_id:" + sessionModel.getRoute().getWaypoints().get(i));
                    }

                    DirectionsResult result = request.await();

                    List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
                    List<com.google.maps.model.LatLng> converted = new ArrayList<>();
                    for(int i = 0; i < decodedPath.size(); i++){
                        converted.add(new com.google.maps.model.LatLng(decodedPath.get(i).latitude, decodedPath.get(i).longitude));
                    }

                    googleMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(getActivity().getResources().getColor(R.color.red)));

                    com.google.maps.model.LatLng[] latLngs = converted.toArray(new com.google.maps.model.LatLng[converted.size()]);
                    SnappedPoint[] interpolated = RoadsApi.snapToRoads(getGeoContext(), true, latLngs).await();
                    for(int i = 0; i < interpolated.length; i++){
                        Log.e("LatLngs", interpolated[i].toString());
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(Double.parseDouble(sessionModel.getRoute().getOrigin_lat()), Double.parseDouble(sessionModel.getRoute().getOrigin_lng())));
                    builder.include(new LatLng(Double.parseDouble(sessionModel.getRoute().getDestination_lat()), Double.parseDouble(sessionModel.getRoute().getDestination_lng())));
                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        startSession = (Button) rootView.findViewById(R.id.startSession);
        startSession.setOnClickListener(this);
        initMap();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(startSession)){
            Intent startStartSessionActivity = new Intent(getActivity(), StartSession.class);
            startActivityForResult(startStartSessionActivity, 1);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext distCalcer = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();
        return distCalcer;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap = mMap;

        LatLng center = new LatLng(17.127995,120.485980);
        if(mLastKnownLocation != null){
            center = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        } else {
            mLocationPermissionGranted = true;
        }

        if (mLocationPermissionGranted) {
            updateMap();
        }
//                LatLng orig = new LatLng(16.5542868,120.3232215);
//                LatLng dest = new LatLng(16.6169121,120.31527);
//                String o = orig.latitude + "," +  orig.longitude;
//                String d = dest.latitude + "," +  dest.longitude;
//                DateTime now = new DateTime();
//                try {
//                    DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
//                            .mode(TravelMode.DRIVING)
//                            .origin(o)
//                            .destination(d)
//                            .departureTime(now)
//                            .await();
//
//                    Log.e("Waypoints1:", String.valueOf(result.geocodedWaypoints[1].placeId));
//                    Log.e("Waypoints2:", String.valueOf(result.geocodedWaypoints[0].placeId));
//
////                    Geocoder geocoder;
////                    List<Address> addresses;
////                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
////
////                    addresses = geocoder.getFromLocation(orig.latitude, orig.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
////
////                    String city = addresses.get(0).getLocality();
////
////                    addresses = geocoder.getFromLocation(dest.latitude, dest.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
////
////                    String city2 = addresses.get(0).getLocality();
////
////                    Log.e("City1:", city);
////                    Log.e("City2:", city2);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (com.google.maps.errors.ApiException e) {
//                    e.printStackTrace();
//                }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.e("Permission:", String.valueOf(requestCode));
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateMap();
                }
                return;
            }
        }
    }

    public void setMapCenter(){
        try {
            SnappedPoint[] points = RoadsApi.snapToRoads(getGeoContext(), true, new com.google.maps.model.LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).await();
            if(points != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points[0].location.lat, points[0].location.lng), 15));
            }else{
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMap(){
        try {
            Log.e("mPermissionGranted:", String.valueOf(mLocationPermissionGranted));
            if (googleMap == null) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mLastKnownLocation = location;
                                setMapCenter();
                            }
                        }
                    });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
