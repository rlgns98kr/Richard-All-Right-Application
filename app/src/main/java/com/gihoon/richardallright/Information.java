package com.gihoon.richardallright;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ActionBar ab;
        ab = getSupportActionBar();
        ab.hide();

        ImageView userview = findViewById(R.id.userview);
        TextView username = findViewById(R.id.username);

        GradientDrawable drawable= (GradientDrawable) getDrawable(R.drawable.round_shape);
        userview.setBackground(drawable);
        userview.setClipToOutline(true);

        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userview);
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Button bt = findViewById(R.id.homemove);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}