package com.cookandroid.cookmap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient; // 현재 위치 조회를 위한 객체
    private FavoriteManager favoriteManager; // 즐겨찾기 관리를 위한 객체
    private HashMap<Marker, ArtMuseum> markerMap = new HashMap<>(); // 마커와 미술관 목록인 ArtMuseum 매핑
    private HashMap<Marker, Ski> skiMarkerMap = new HashMap<>(); // 스키장 마커 매핑
    private Button btnViewFavorites, btnShowMuseums, btnSearch, btnMyLocation, btnShowSkiResorts; // 버튼들
    private EditText searchInput; // 검색 입력 필드
    private boolean areMuseumsVisible = false; // 미술관 마커 상태를 관리하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActionBar 설정
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.bonggong1);
        } else {
            Toast.makeText(this, "ActionBar를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 즐겨찾기 관리 객체 초기화
        favoriteManager = new FavoriteManager();

        // 현재 위치 제공자 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 지도 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "지도를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 버튼 초기화
        btnViewFavorites = findViewById(R.id.btn_view_favorites);
        btnShowMuseums = findViewById(R.id.btn_show_museums);
        btnShowSkiResorts = findViewById(R.id.btn_show_ski_resorts);
        searchInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
        btnMyLocation = findViewById(R.id.btn_my_location);

        // "즐겨찾기 보기" 버튼 클릭 이벤트
        btnViewFavorites.setOnClickListener(v -> openFavorites());

        // "미술관 보기" 버튼 클릭 이벤트
        btnShowMuseums.setOnClickListener(v -> {
            if (areMuseumsVisible) {
                hideAllMuseums(); // 마커 숨기기
            } else {
                showAllMuseums(); // 마커 표시
            }
            areMuseumsVisible = !areMuseumsVisible; // 상태 토글
        });

        // "스키장으로 떠나볼까요" 버튼 클릭 이벤트
        btnShowSkiResorts.setOnClickListener(v -> showAllSkiResorts());

        // "검색" 버튼 클릭 이벤트
        btnSearch.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchMarkers(keyword);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // "내 위치" 버튼 클릭 이벤트
        btnMyLocation.setOnClickListener(v -> fetchAndMoveToMyLocation());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 지도 줌 컨트롤 활성화
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 마커 클릭 이벤트 처리
        mMap.setOnMarkerClickListener(marker -> {
            // 미술관 마커 클릭 처리
            ArtMuseum museum = markerMap.get(marker); // 클릭된 마커에 해당하는 ArtMuseum 객체 가져오기
            if (museum != null) {
                showMarkerDetails(museum); // 미술관 상세정보 다이얼로그 표시
            }

            // 스키장 마커 클릭 처리
            Ski ski = skiMarkerMap.get(marker); // 클릭된 마커에 해당하는 Ski 객체 가져오기
            if (ski != null) {
                showSkiMarkerDetails(ski); // 스키장 상세정보 다이얼로그 표시
            }

            return true; // 기본 마커 클릭 동작 방지
        });

        // "내 위치" 버튼 활성화
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // Google Maps 기본 버튼 비활성화

        // 위치 권한 확인 및 동작 처리
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // 내 위치 활성화
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // "미술관 보기" 버튼 클릭 시 모든 마커 표시
    private void showAllMuseums() {
        if (mMap != null) {
            // 기존 마커 제거
            mMap.clear();
            markerMap.clear();

            // CSV 데이터를 로드하고 마커 추가
            List<ArtMuseum> allMuseums = ArtMuseum.loadMuseumsFromRaw(this, R.raw.art_museum_1);
            for (ArtMuseum museum : allMuseums) {
                LatLng location = new LatLng(museum.getLatitude(), museum.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(museum.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))); // 마커 색상 변경
                markerMap.put(marker, museum); // 마커와 데이터를 매핑
            }
            Toast.makeText(this, "미술관 마커가 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 스키장 마커를 지도에 표시
    private void showAllSkiResorts() {
        if (mMap != null) {
            // 기존 마커 제거
            mMap.clear();
            skiMarkerMap.clear(); // 스키장 데이터만 초기화

            // CSV 데이터를 로드하고 마커 추가
            List<Ski> allSkiResorts = Ski.loadSkiResortsFromRaw(this, R.raw.ski);
            for (Ski ski : allSkiResorts) {
                LatLng location = new LatLng(ski.getLatitude(), ski.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(ski.getName())
                        .snippet("전화번호: " + ski.getPhone()) // 전화번호 추가
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))); // 파란색 마커
                skiMarkerMap.put(marker, ski); // 스키장 데이터를 스키 맵에 매핑
            }
            Toast.makeText(this, "스키장 마커가 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 모든 마커 숨기기
    private void hideAllMuseums() {
        if (mMap != null) {
            // 지도 위의 모든 마커 제거
            mMap.clear();
            markerMap.clear();
            Toast.makeText(this, "미술관 마커가 숨겨졌습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 검색 기능 구현
    private void searchMarkers(String keyword) {
        if (mMap != null) {
            // 기존 마커 제거
            mMap.clear();
            markerMap.clear();

            // CSV 데이터 로드
            List<ArtMuseum> allMuseums = ArtMuseum.loadMuseumsFromRaw(this, R.raw.art_museum_1);

            // 검색 로직 실행
            List<ArtMuseum> searchResults = MuseumSearcher.searchMuseums(allMuseums, keyword);

            // 검색 결과 마커 추가
            for (ArtMuseum museum : searchResults) {
                LatLng location = new LatLng(museum.getLatitude(), museum.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(museum.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))); // 보라색으로 마커 추가
                markerMap.put(marker, museum); // 마커와 데이터를 매핑
            }

            // 검색 결과 확인
            if (searchResults.isEmpty()) {
                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "검색 결과가 반영되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 현재 위치로 이동
    private void fetchAndMoveToMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    Toast.makeText(this, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 즐겨찾기 목록 보기
    public void openFavorites() {
        List<Object> favorites = favoriteManager.getFavorites(); // ArtMuseum과 Ski가 모두 포함된 리스트
        if (favorites.isEmpty()) {
            Toast.makeText(this, "즐겨찾기 목록이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // FavoritesActivity로 전환
        Intent intent = new Intent(this, FavoritesActivity.class);
        intent.putExtra("favorites", new ArrayList<>(favorites));
        startActivity(intent);
    }


    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndMoveToMyLocation();
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 미술관 마커 상세정보에서 즐겨찾기 추가
    private void showMarkerDetails(ArtMuseum museum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(museum.getName());
        builder.setMessage("주소: " + museum.getAddress() + "\n우편번호: " + museum.getPostalCode());

        builder.setPositiveButton("즐겨찾기 추가", (dialog, which) -> {
            if (favoriteManager.addFavorite(museum)) {
                Toast.makeText(this, museum.getName() + "을(를) 즐겨찾기에 추가했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "이미 즐겨찾기에 추가된 항목입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("닫기", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 스키 마커 상세정보에서 즐겨찾기 추가
    private void showSkiMarkerDetails(Ski ski) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ski.getName());
        builder.setMessage("주소: " + ski.getAddress() + "\n우편번호: " + ski.getPostalCode() +
                "\n전화번호: " + (ski.getPhone().isEmpty() ? "정보 없음" : ski.getPhone()));

        builder.setPositiveButton("즐겨찾기 추가", (dialog, which) -> {
            if (favoriteManager.addFavorite(ski)) {
                Toast.makeText(this, ski.getName() + "을(를) 즐겨찾기에 추가했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "이미 즐겨찾기에 추가된 항목입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("닫기", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
