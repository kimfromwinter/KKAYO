package com.cookandroid.cookmap;

import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;

public class MapHandler {
    private GoogleMap mMap;

    // SupportMapFragment를 초기화하고 OnMapReadyCallback 연결
    public void initializeMap(SupportMapFragment mapFragment, Context context, OnMapReadyCallback callback) {
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        } else {
            Toast.makeText(context, "MapFragment is null", Toast.LENGTH_SHORT).show();
        }
    }

    // GoogleMap 객체 저장
    public void setMap(GoogleMap googleMap) {
        this.mMap = googleMap;
    }

    // 저장된 GoogleMap 객체 반환
    public GoogleMap getMap() {
        return mMap;
    }
}
