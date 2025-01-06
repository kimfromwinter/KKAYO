package com.cookandroid.cookmap;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// ArtMuseum 클래스는 Serializable을 구현하여 Intent를 통해 전달 가능
public class ArtMuseum implements Serializable {
    private String name;         // 사업장명
    private double latitude;     // WGS84 위도
    private double longitude;    // WGS84 경도
    private String address;      // 소재지도로명주소
    private String postalCode;   // 소재지우편번호

    // 생성자
    public ArtMuseum(String name, double latitude, double longitude, String address, String postalCode) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.postalCode = postalCode;
    }

    // Getter 메서드
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

    // toString 메서드 (ListView 등에서 표시하기 위해 사용)
    @Override
    public String toString() {
        return name + " (" + address + ")";
    }

    // raw 리소스에서 CSV 데이터를 읽고 ArtMuseum 리스트로 변환
    public static List<ArtMuseum> loadMuseumsFromRaw(Context context, int rawResourceId) {
        List<ArtMuseum> museums = new ArrayList<>();

        try {
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // 첫 번째 헤더 행 건너뛰기
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] tokens = line.split(","); // 쉼표로 구분
                if (tokens.length < 5) { // 최소 5개의 열이 있어야 함
                    continue;
                }

                String name = tokens[0].trim();           // 사업장명
                double latitude = Double.parseDouble(tokens[1].trim()); // WGS84 위도
                double longitude = Double.parseDouble(tokens[2].trim()); // WGS84 경도
                String address = tokens[3].trim();       // 소재지도로명주소
                String postalCode = tokens[4].trim();    // 소재지우편번호

                museums.add(new ArtMuseum(name, latitude, longitude, address, postalCode));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return museums;
    }
}
