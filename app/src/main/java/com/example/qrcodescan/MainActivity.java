package com.example.qrcodescan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.qrcodescan.databinding.ActivityMainBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "activity_main";
    private IntentIntegrator qrScan;
    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private static final String URL = Config.URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue = Volley.newRequestQueue(this);
        qrScan = new IntentIntegrator(this);

        // button onClick
        binding.buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("Scanning...");
                qrScan.setCameraId(0);
//                qrScan.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
    }

    // Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            // qrcode 결과가 없으면
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            } else {
                // qrcode 결과가 있으면
                Toast.makeText(MainActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    // data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    Log.i(TAG, "qrcode data: " + obj);
                    QRCodeCheck(obj);

                    qrScan.setPrompt("Scanning...");
//                    qrScan.setOrientationLocked(false);
                    qrScan.initiateScan();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // http volley
    private void QRCodeCheck(JSONObject obj) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, obj, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                boolean success = false;
                try {
                    Log.i(TAG, "check data: " + response);
//                    success = response.getBoolean("success");
                    if (response != null) {
                        Toast.makeText(MainActivity.this, "출석하셨습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.getMessage());
                Log.i(TAG, "Volley Error in receiv");
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}