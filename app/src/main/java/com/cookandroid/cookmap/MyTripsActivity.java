package com.cookandroid.cookmap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyTripsActivity extends AppCompatActivity {

    private TextView tvTripDate;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        // "나의 여행" 항목 받기
        List<Object> myTripItems = (List<Object>) getIntent().getSerializableExtra("my_trip_items");

        // UI 요소 초기화
        tvTripDate = findViewById(R.id.tv_trip_date_label);
        Button btnSelectTripDate = findViewById(R.id.btn_select_trip_date);
        Button btnSearchWeather = findViewById(R.id.btn_search_weather);
        ListView listView = findViewById(R.id.trip_list);

        // "나의 여행" 항목 표시
        List<String> descriptions = new ArrayList<>();
        if (myTripItems != null) {
            for (Object item : myTripItems) {
                if (item instanceof ArtMuseum) {
                    ArtMuseum museum = (ArtMuseum) item;
                    descriptions.add("미술관: " + museum.getName());
                } else if (item instanceof Ski) {
                    Ski ski = (Ski) item;
                    descriptions.add("스키장: " + ski.getName());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, descriptions);
        listView.setAdapter(adapter);

        // 날짜 선택 버튼 클릭 이벤트
        btnSelectTripDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // DatePickerDialog 생성 및 표시
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        tvTripDate.setText("여행 날짜: " + selectedDate);
                        Toast.makeText(this, "날짜가 설정되었습니다: " + selectedDate, Toast.LENGTH_SHORT).show();
                    }, year, month, day);
            datePickerDialog.show();
        });

        // 날씨 검색 버튼 클릭 이벤트
        btnSearchWeather.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "여행 날짜를 먼저 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 구글 검색 URL로 이동
            String query = selectedDate + " 날씨";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
            startActivity(intent);
        });
    }
}
