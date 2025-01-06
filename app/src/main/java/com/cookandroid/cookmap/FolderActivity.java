package com.cookandroid.cookmap;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        // Intent 를 통해 전달받은 폴더 이름을 가져옴
        String folderName = getIntent().getStringExtra("folder_name");
        
        // Intent 를 통해 전달받은 폴더 항목 목록을 가져옴 
        List<Object> folderItems = (List<Object>) getIntent().getSerializableExtra("folder_items");

        // 액션바가 존재하면 제목을 폴더 이름으로 설정하게
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(folderName);
        }

        // 폴더에 저장된 항목을 표시할 리스트뷰를 초기화
        ListView listView = findViewById(R.id.folder_list);
        List<String> descriptions = new ArrayList<>();
        if (folderItems != null) {
            for (Object item : folderItems) {
                if (item instanceof ArtMuseum) {
                    ArtMuseum museum = (ArtMuseum) item;
                    descriptions.add("미술관: " + museum.getName());
                } else if (item instanceof Ski) {
                    Ski ski = (Ski) item;
                    descriptions.add("스키장: " + ski.getName());
                }
            }
        }

        // 문자열 목록을 리스트뷰에 연결하기 위한 ArrayAdapter 생성
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, descriptions);
        // 리스트뷰에 어댑터 설정하는 코드
        listView.setAdapter(adapter);
    }
}
