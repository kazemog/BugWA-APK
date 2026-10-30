package com.kaze.bugwa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUser = findViewById(R.id.loginUser);
        final EditText etPass = findViewById(R.id.loginPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString().trim();
            
            if(u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Isi username dan password!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setText("MENGECEK...");
            btnLogin.setEnabled(false);

            new Thread(() -> {
                try {
                    // GANTI URL INI DENGAN URL WEB LO (Ngrok/Cloudflare/Render)
                    URL url = new URL("https://1cd5b92cdb8a53.lhr.life/api/login"); 
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String jsonInputString = "{\"username\": \"" + u + "\", \"password\": \"" + p + "\"}";
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) response.append(responseLine.trim());

                    JSONObject json = new JSONObject(response.toString());
                    boolean success = json.optBoolean("success");
                    String tier = json.optString("tier");

                    runOnUiThread(() -> {
                        // CEK ROLE: Hanya VVIP & DEVELOPER yang boleh masuk
                        if (success && (tier.equals("VVIP") || tier.equals("DEVELOPER"))) {
                            Toast.makeText(LoginActivity.this, "AKSES DITERIMA!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "AKSES DITOLAK! Khusus VVIP.", Toast.LENGTH_LONG).show();
                            btnLogin.setText("MASUK"); btnLogin.setEnabled(true);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Koneksi Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnLogin.setText("MASUK"); btnLogin.setEnabled(true);
                    });
                }
            }).start();
        });
    }
}
