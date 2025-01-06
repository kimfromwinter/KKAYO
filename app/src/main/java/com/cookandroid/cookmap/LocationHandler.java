package com.cookandroid.cookmap;

import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;

// 사용자의 현재 위치를 처리하는 클래스
public class LocationHandler {
    private final FusedLocationProviderClient fusedLocationClient;

    // FusedLocationProviderClient를 초기화하는 생성자
    public LocationHandler(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient; // 클라이언트가 위치 데이터 제공 담당
    }

    // 기기의 Location 을 가져와서 지도에 표시하는 메소드
    public void getDeviceLocation(Context context, GoogleMap mMap) {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> { // 위치 요청에 성공했을 때 실핼됨
                        if (location != null) { // 위치 데이터가 있을 때
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude()); // 현재 위치를 LatLng 객체로 변환
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); //지도 뷰를 현재 위치로 이동
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
