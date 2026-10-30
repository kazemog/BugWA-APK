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
            
            // SISTEM ANTI-GAGAL: Terima apapun bentuknya, asalkan passwordnya bener
            // Lo bisa pake "KazeMorales1" atau "admin123"
            boolean isUserValid = u.equalsIgnoreCase("Kaze Morales") || u.equalsIgnoreCase("KazeMorales") || u.equalsIgnoreCase("admin") || u.isEmpty();
            boolean isPassValid = p.equals("KazeMorales1") || p.equals("admin123") || p.equals("kaze123");

            if (isUserValid && isPassValid) {
                Toast.makeText(this, "AKSES DITERIMA! Selamat datang.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Biar gampang, kasih tau passwordnya salah
                Toast.makeText(this, "Password Salah! Gunakan: KazeMorales1", Toast.LENGTH_LONG).show();
            }
        });
    }
}
