package com.example.vanaiddriver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vanaiddriver.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeolocationApi;
import com.google.maps.RoadsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.SnappedPoint;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
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
    private Marker pickupMarker;
    public HomeFragment() {
        // Required empty public constructor
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

        initMap();
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.google_maps_key), Locale.getDefault());
        }
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                new LatLng(15.932911, 119.944304),
                new LatLng(18.342771, 121.046823)
        ));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());

                    addresses = geocoder.getFromLocationName(place.getName(), 1);

                    com.google.maps.model.LatLng center = new com.google.maps.model.LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    SnappedPoint[] points = RoadsApi.snapToRoads(getGeoContext(), true, center).await();
                    if(points != null){
                        pickupMarker.setPosition(new LatLng(points[0].location.lat, points[0].location.lng));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points[0].location.lat, points[0].location.lng), 20));
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

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
    public void onCameraMove() {
        pickupMarker.setPosition(googleMap.getCameraPosition().target);
    }

    @Override
    public void onCameraIdle() {
        try {
            SnappedPoint[] points = RoadsApi.snapToRoads(getGeoContext(), true, new com.google.maps.model.LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude)).await();
            if(points != null){
                pickupMarker.setPosition(new LatLng(points[0].location.lat, points[0].location.lng));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
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
            pickupMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(points[0].location.lat, points[0].location.lng)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points[0].location.lat, points[0].location.lng), 15));
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
