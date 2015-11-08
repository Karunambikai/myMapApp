package com.example.gmapsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapFragmentD extends SupportMapFragment {

	GoogleMap mapView;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	public View onCreateView(LayoutInflater mInflater, ViewGroup arg1,
			Bundle arg2) {
		return super.onCreateView(mInflater, arg1, arg2);
	}

	@Override
	public void onInflate(Activity arg0, AttributeSet arg1, Bundle arg2) {
		super.onInflate(arg0, arg1, arg2);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mapView = getMap();

		mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mapView.setMyLocationEnabled(true);
		mapView.getUiSettings().setZoomControlsEnabled(true);
		mapView.getUiSettings().setMyLocationButtonEnabled(true);
		mapView.getUiSettings().setCompassEnabled(true);
		mapView.getUiSettings().setRotateGesturesEnabled(true);
		mapView.getUiSettings().setZoomGesturesEnabled(true);

		/*MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.draggable(true);
		markerOptions.position(new LatLng(12.936892, 77.624862));
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
		mapView.addMarker(markerOptions);*/
	}


}