package com.gihoon.richardallright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;


public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private GoogleMap mMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LatLng sydney;
    HashMap<String, HashMap> markers;
    FrameLayout fl1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fl1 = findViewById(R.id.fl1);
        fl1.bringToFront();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            update_location();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 100);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                fl1.bringToFront();
                ImageButton a=findViewById(R.id.realconfim);
                a.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == 100 && grandResults.length == 2) update_location();
    }

    @SuppressLint("MissingPermission")
    public void update_location() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sydney = new LatLng(37.5015492, 127.0353802);
        } else {
            GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
            sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.logo);
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        markers=new HashMap<String, HashMap>();
        db.collection("parkingLot")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap a = (HashMap) document.getData();

                                Object x = a.get("x");
                                Object y = a.get("y");
                                LatLng b = new LatLng(Double.parseDouble(x.toString()), Double.parseDouble(y.toString()));
                                MarkerOptions op = new MarkerOptions().position(b).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                Marker l = mMap.addMarker(op.title(a.get("title").toString()));
                                markers.put(l.getId(), a);
                            }
                        } else {
                            System.out.println(task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(markers.containsKey(marker.getId())) {
            System.out.println(markers.get(marker.getId()).get("title"));
            FrameLayout fl2 = findViewById(R.id.fl2);
            ImageView realimage = findViewById(R.id.realimage);
            TextView realname = findViewById(R.id.realname);
            TextView confirm = findViewById(R.id.confirm);
            TextView realprice = findViewById(R.id.realprice);
            ImageButton realconfirm = findViewById(R.id.realconfim);

            confirm.setText("예약하기");
            realname.setText(marker.getTitle());
            realprice.setText(markers.get(marker.getId()).get("price").toString());


            fl2.bringToFront();
            confirm.bringToFront();
            realimage.bringToFront();
            realname.bringToFront();
            realprice.bringToFront();

            realconfirm.bringToFront();
            realconfirm.setVisibility(View.VISIBLE);
            fl2.setOnClickListener(new FrameLayout.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            realconfirm.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://kapi.kakao.com/v1/payment/ready";

                    ContentValues params = new ContentValues();
                    params.put("cid", "TC0ONETIME");
                    params.put("partner_order_id", "1001");
                    params.put("partner_user_id", "gorany");
                    params.put("item_name", "갤럭시S9");
                    params.put("quantity", "1");
                    params.put("total_amount", "2100");
                    params.put("tax_free_amount", "100");
                    params.put("approval_url", "https://gihoonrar.page.link/?link=https://richardallright.com/payment&apn=com.gihoon.richardallright");
                    params.put("cancel_url", "https://gihoonrar.page.link/cancel");
                    params.put("fail_url", "https://gihoonrar.page.link/fail");
                    NetworkTask networkTask = new NetworkTask(url, params);
                    networkTask.execute();
                }
            });
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
        return false;
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values, getResources().getString(R.string.kakao_admin_key)); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JsonObject jsonObj = new JsonParser().parse(s).getAsJsonObject();
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            String redirectUrl = jsonObj.get("next_redirect_app_url").getAsString();
            Intent intent = new Intent(getApplicationContext(), Kakaopay.class);
            intent.putExtra("url", redirectUrl);
            startActivity(intent);
        }
    }
}