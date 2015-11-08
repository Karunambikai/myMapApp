package com.example.gmapsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	SupportMapFragment mFRaFragment;

	String location;

	double lat, lng;
	String gotoLocation = null;
	StringBuilder loc;
	MarkerOptions markerOptions;
	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	@SuppressWarnings("unused")
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9002;
	GoogleMap mMap;
	Button clickNext;
	ProgressDialog progressDialog;

	@SuppressWarnings("unused")
	private static final float DEFAULTZOOM = 15;
	@SuppressWarnings("unused")
	private static final String LOGTAG = "Maps";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);
		IntitialzeMap mapLoad = new IntitialzeMap();
		mapLoad.execute();

		boolean isNet = false;
		ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						isNet = true;
						break;
					}
		}
		if (!isNet) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setMessage(
							"No Internet Connection. Please connect internet and try again!!")
					.setCancelable(false)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
		}

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGPSDisabledAlertToUser();
		}

		loc = new StringBuilder();
		clickNext = (Button) findViewById(R.id.clickNext);
		clickNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						MenuActivity.class);
				i.putExtra("Location", loc.toString());
				i.putExtra("Lat", lat + "");
				i.putExtra("Lng", lng + "");
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Can't connect to Google Play services",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	class IntitialzeMap extends AsyncTask<Void, Void, Void> {

		LocationManager lm;
		Location location;
		List<Address> list = null;
		Geocoder gc;
		String addressText;
		double lat1, lng1;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please Wait");
			progressDialog.setCancelable(false);
			progressDialog.show();
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10,
					locationListener);
			gc = new Geocoder(MainActivity.this);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			android.support.v4.app.FragmentTransaction mTransaction = getSupportFragmentManager()
					.beginTransaction();
			mFRaFragment = new MapFragmentD();
			mTransaction.add(R.id.mainl, mFRaFragment);
			mTransaction.commit();
			try {
				MapsInitializer.initialize(MainActivity.this);
			} catch (GooglePlayServicesNotAvailableException e) {
				e.printStackTrace();
			}
			if (location != null) {
				lng1 = location.getLongitude();
				lat1 = location.getLatitude();
				try {
					list = gc.getFromLocation(lat, lng, 3);
				} catch (IOException e) {
					e.printStackTrace();
				}
				loc = new StringBuilder();
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						Address address = (Address) list.get(i);
						addressText = String.format(
								"%s, %s",
								address.getMaxAddressLineIndex() > 0 ? address
										.getAddressLine(0) : "", address
										.getCountryName());
						if (i == 0)
							break;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
			mMap = mFRaFragment.getMap();
			mMap.setMyLocationEnabled(true);
			MapStateManager mgr = new MapStateManager(MainActivity.this);
			CameraPosition position = mgr.getSavedCameraPosition();
			if (position != null) {
				CameraUpdate update = CameraUpdateFactory
						.newCameraPosition(position);
				mMap.moveCamera(update);
				mMap.setMapType(mgr.getSavedMapType());
			}
			gotoCurrentLocation();
			/*MarkerOptions marker = new MarkerOptions().position(
					new LatLng(lat1, lng1)).title(addressText);
			try {
				mMap.addMarker(marker);
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(lat1, lng1)).zoom(8).build();
				mMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}*/
		}
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);
		EditText et = (EditText) findViewById(R.id.editText1);
		location = et.getText().toString();
		if (location.length() > 0) {
			et.setText(null);
			new LoadLocation().execute();

		} else {
			Toast.makeText(getApplicationContext(), "Enter Search Location!!!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public class LoadLocation extends AsyncTask<Void, Void, Void> {

		Geocoder gc;
		List<Address> list;
		MarkerOptions marker;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please Wait");
			progressDialog.setCancelable(false);
			progressDialog.show();
			gc = new Geocoder(getApplicationContext());
			list = new ArrayList<Address>();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			marker = new MarkerOptions().position(new LatLng(lat, lng)).title(
					loc.toString());
			if (marker != null) {
				try {
					mMap.clear();
					mMap.addMarker(marker);
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(lat, lng)).zoom(8).build();
					mMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

				progressDialog.dismiss();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				list = gc.getFromLocationName(location, 3);
				loc = new StringBuilder();
				if (list != null) {
					for (int i = 0; i < 1; i++) {
						Address address = (Address) list.get(i);
						loc.append(
								String.format(
										"%s, %s",
										address.getMaxAddressLineIndex() > 0 ? address
												.getAddressLine(0) : "",
										address.getCountryName())).append("\n");
						lat = address.getLatitude();
						lng = address.getLongitude();

					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			return null;
		}
	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			break;
		case R.id.mapTypeNormal:
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeTerrain:
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.mapTypeHybrid:
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.gotoCurrentLocation:
			gotoCurrentLocation();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(mMap);
	}

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	protected void gotoCurrentLocation() {

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10,
				locationListener);
		if (location != null) {
			lng = location.getLongitude();
			lat = location.getLatitude();
			Geocoder gc = new Geocoder(this);
			List<Address> list = null;
			try {
				list = gc.getFromLocation(lat, lng, 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loc = new StringBuilder();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Address address = (Address) list.get(i);
					loc.append(
							String.format(
									"%s, %s",
									address.getMaxAddressLineIndex() > 0 ? address
											.getAddressLine(0) : "", address
											.getCountryName())).append("\n");
				}
				for (int i = 0; i < list.size(); i++) {
					Address address = (Address) list.get(i);
					String addressText = String.format(
							"%s, %s",
							address.getMaxAddressLineIndex() > 0 ? address
									.getAddressLine(0) : "", address
									.getCountryName());
					if (i == 0) {
						MarkerOptions marker = new MarkerOptions().position(
								new LatLng(lat, lng)).title(loc.toString());
						try {
							mMap.addMarker(marker);
							CameraPosition cameraPosition = new CameraPosition.Builder()
									.target(new LatLng(lat, lng)).zoom(8)
									.build();
							mMap.animateCamera(CameraUpdateFactory
									.newCameraPosition(cameraPosition));
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),
									e.getMessage(), Toast.LENGTH_LONG).show();
						}

					}
				}
			}
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			lng = location.getLongitude();
			lat = location.getLatitude();
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	};
}
