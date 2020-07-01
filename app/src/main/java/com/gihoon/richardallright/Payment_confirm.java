package com.gihoon.richardallright;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Payment_confirm extends AppCompatActivity {
    private static String TAG = "Dynamic_confirm";
    private String PG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkData = appLinkIntent.getStringExtra("pg_token");
        System.out.println(appLinkData);
    }
}