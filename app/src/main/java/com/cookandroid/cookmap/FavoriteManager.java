package com.cookandroid.cookmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager implements Serializable {
    private List<Object> favorites; // 다양한 타입의 객체를 저장

    public FavoriteManager() {
        favorites = new ArrayList<>();
    }

    // 즐겨찾기 추가
    public boolean addFavorite(Object item) {
        if (!favorites.contains(item)) {
            favorites.add(item);
            return true;
        }
        return false; // 이미 목록에 있을 경우 추가하지 않음
    }

    // 즐겨찾기 목록 가져오기
    public List<Object> getFavorites() {
        return new ArrayList<>(favorites); // Immutable list 반환
    }
}


