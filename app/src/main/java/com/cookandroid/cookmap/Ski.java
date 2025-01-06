package com.cookandroid.cookmap;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ski implements Serializable {
    private String name;         // 스키장 이름
    private double latitude;     // 위도
    private double longitude;    // 경도
    private String address;      // 주소
    private String postalCode;   // 우편번호
    private String phone;        // 전화번호

    public Ski(String name, double latitude, double longitude, String address, String postalCode, String phone) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public static List<Ski> loadSkiResortsFromRaw(Context context, int rawResourceId) {
        List<Ski> skiResorts = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] tokens = line.split(",");
                if (tokens.length < 6) continue;
                String name = tokens[0].trim();
                double latitude = Double.parseDouble(tokens[1].trim());
                double longitude = Double.parseDouble(tokens[2].trim());
                String address = tokens[3].trim();
                String postalCode = tokens[4].trim();
                String phone = tokens.length > 5 ? tokens[5].trim() : "정보 없음";
                skiResorts.add(new Ski(name, latitude, longitude, address, postalCode, phone));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skiResorts;
    }
}
