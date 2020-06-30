package com.gihoon.richardallright;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Button pB = findViewById(R.id.payButton);

        pB.setOnClickListener(new Button.OnClickListener() {
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
        // AsyncTask를 통해 HttpURLConnection 수행.
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
            Intent intent = new Intent(getApplicationContext(), kakaopay.class);
            intent.putExtra("url", redirectUrl);
            startActivity(intent);
        }
    }
}
