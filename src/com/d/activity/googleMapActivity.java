package com.d.activity;

import java.util.List;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.d.api.LocationTracer;
import com.d.localdb.LocationLogRecord;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.util.Log;
import com.google.android.gms.maps.OnMapReadyCallback;

public class googleMapActivity extends Activity implements OnMapReadyCallback {
	
	private LocationTracer lt;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.google_map);
		lt = new LocationTracer(getBaseContext());
		
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.location_map);
		mapFragment.getMapAsync(this);
	}

	
	@Override
	public void onMapReady(GoogleMap map) {
		LatLng lat = null;
		LatLng prev = null;
		
		List<LocationLogRecord> records = lt.getUsableRecords(20000,30);
		
		 map.setMyLocationEnabled(true);
		 
		for(LocationLogRecord record : records){
			lat = new LatLng(record.latitude, record.longitude);
			Log.d("map", lat.toString());
			map.addMarker(new MarkerOptions().position(lat));
			
			if(prev!= null){
				PolylineOptions  polyline = new PolylineOptions();
				polyline.add(prev,lat).color(Color.rgb(100, 40, 38));
				map.addPolyline(polyline);
				
			}
			prev = lat;
		}
		if(prev != null){
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(prev, 15));
		}
	}

}

