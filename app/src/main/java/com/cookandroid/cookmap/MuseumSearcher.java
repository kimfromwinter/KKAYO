package com.cookandroid.cookmap;

import java.util.ArrayList;
import java.util.List;

public class MuseumSearcher {

    // 검색 로직
    public static List<ArtMuseum> searchMuseums(List<ArtMuseum> museumList, String query) {
        List<ArtMuseum> results = new ArrayList<>();

        // 검색어와 이름 또는 주소 비교
        for (ArtMuseum museum : museumList) {
            if (museum.getName().contains(query) || museum.getAddress().contains(query)) {
                results.add(museum);
            }
        }

        return results;
    }
}
