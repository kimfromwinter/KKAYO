package com.cookandroid.cookmap;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class ArtMuseumDetails {
    public static void showDetails(Context context, String name, String address, String postalCode) {
        // 상세 정보를 표시하는 AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("상세 정보");
        builder.setMessage(
                "이름: " + name + "\n" +
                        "주소: " + address + "\n" +
                        "우편번호: " + postalCode
        );
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 대화 상자를 닫습니다.
            }
        });

        // AlertDialog 표시
        builder.show();
    }
}

