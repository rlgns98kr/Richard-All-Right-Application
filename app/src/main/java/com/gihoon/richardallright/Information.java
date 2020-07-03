package com.gihoon.richardallright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;

public class Information extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ActionBar ab;
        ab = getSupportActionBar();
        ab.hide();

        ImageView userview = findViewById(R.id.userview);
        TextView username = findViewById(R.id.username);
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userview);
        }
        if (!currentUser.getDisplayName().equals("")) {
            username.setText(currentUser.getDisplayName());
        }
        Button bt = findViewById(R.id.homemove);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ab = new Intent(getApplication(), Map.class);
                ab.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ab);
            }
        });
        Date a = new Date(System.currentTimeMillis());
        db.collection("reservation")
                .whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap a = (HashMap) document.getData();
                                String docname = a.get("parkingLotId").toString();

                                db.collection("parkingLot").document(docname).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> taska) {
                                        if (taska.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = taska.getResult();
                                            TextView resertt = findViewById(R.id.reservText);
                                            TextView address = findViewById(R.id.roomAddress);
                                            TextView name = findViewById(R.id.oneroomname);
                                            Button remove = findViewById(R.id.remove);
                                            if (documentSnapshot.exists()) {
                                                resertt.setVisibility(View.VISIBLE);
                                                ImageView imageView = findViewById(R.id.oneroomimage);
                                                Glide.with(getApplicationContext())
                                                        .load(documentSnapshot.get("imageUrl"))
                                                        .into(imageView);
                                                address.setText(documentSnapshot.get("address").toString());
                                                name.setText(documentSnapshot.get("title").toString());

                                                remove.setVisibility(View.VISIBLE);
                                            } else {
                                                System.out.println("No such document");
                                                remove.setVisibility(View.INVISIBLE);
                                                resertt.setVisibility(View.INVISIBLE);
                                            }
                                        } else
                                            System.out.println("get failed with " + taska.getException());
                                    }
                                });
                            }
                        } else {
                            System.out.println(task.getException());
                        }
                    }
                });
    }
}