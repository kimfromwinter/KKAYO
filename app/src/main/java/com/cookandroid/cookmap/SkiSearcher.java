package com.cookandroid.cookmap;

import java.util.ArrayList;
import java.util.List;

public class SkiSearcher {
    public static List<Ski> searchSkiResorts(List<Ski> skiList, String query) {
        List<Ski> results = new ArrayList<>();
        for (Ski ski : skiList) {
            if (ski.getName().contains(query) || ski.getAddress().contains(query)) {
                results.add(ski);
            }
        }
        return results;
    }
}
