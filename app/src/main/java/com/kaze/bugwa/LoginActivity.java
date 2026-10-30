package com.kaze.bugwa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            ImageView logo = findViewById(R.id.loginLogo);
            int logoResId = getResources().getIdentifier("logo", "drawable", getPackageName());
            if (logoResId != 0) logo.setImageResource(logoResId);
        } catch (Exception e) {}

        final EditText etUser = findViewById(R.id.loginUser);
        final EditText etPass = findViewById(R.id.loginPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString().trim();
            
            if(u.equals("Kaze Morales") && p.equals("KazeMorales1")) {
                Toast.makeText(this, "AKSES VVIP DITERIMA!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else if (u.equals("admin") && p.equals("admin123")) {
                Toast.makeText(this, "AKSES DEVELOPER DITERIMA!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "AKSES DITOLAK! Cek Username/Password.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
