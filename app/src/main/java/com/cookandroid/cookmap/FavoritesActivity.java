package com.cookandroid.cookmap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private List<Object> favorites; // 즐겨찾기 목록
    private ArrayAdapter<String> adapter;
    private List<Object> selectedItems = new ArrayList<>(); // 선택된 항목
    private List<Object> myTripItems = new ArrayList<>(); // "나의 여행" 항목

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Intent로 전달받은 즐겨찾기 목록
        favorites = (List<Object>) getIntent().getSerializableExtra("favorites");

        ListView listView = findViewById(R.id.favorites_list);
        Button btnSelectMode = findViewById(R.id.btn_select_mode);
        Button btnMyTrips = findViewById(R.id.btn_my_trips);

        // ListView 초기화
        updateListView();

        // ListView 선택 모드 항상 활성화
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = favorites.get(position);
            if (selectedItems.contains(item)) {
                selectedItems.remove(item); // 선택 해제
            } else {
                selectedItems.add(item); // 선택 추가
            }
            Toast.makeText(this, selectedItems.size() + "개의 항목 선택됨", Toast.LENGTH_SHORT).show();
        });

        // "이동하기" 버튼 클릭
        btnSelectMode.setOnClickListener(v -> {
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "이동할 항목을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 선택된 항목을 "나의 여행"으로 이동
            myTripItems.addAll(selectedItems);
            favorites.removeAll(selectedItems);
            selectedItems.clear();
            updateListView();
            Toast.makeText(this, "선택한 항목이 나의 여행으로 이동되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // "나의 여행" 버튼 클릭
        btnMyTrips.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyTripsActivity.class);
            intent.putExtra("my_trip_items", new ArrayList<>(myTripItems)); // "나의 여행" 항목 전달
            startActivity(intent);
        });
    }

    // ListView 업데이트
    private void updateListView() {
        List<String> descriptions = new ArrayList<>();
        for (Object item : favorites) {
            if (item instanceof ArtMuseum) {
                ArtMuseum museum = (ArtMuseum) item;
                descriptions.add("미술관: " + museum.getName());
            } else if (item instanceof Ski) {
                Ski ski = (Ski) item;
                descriptions.add("스키장: " + ski.getName());
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, descriptions);
        ListView listView = findViewById(R.id.favorites_list);
        listView.setAdapter(adapter);
    }
}
