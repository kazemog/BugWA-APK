package com.kaze.bugwa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static boolean isRunning = false;
    public static String targetNum = "";
    public static String senderNum = "";
    public static String bugType = "crash";
    public static String senderType = "private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);

            ImageView banner = findViewById(R.id.bannerImage);
            int bannerResId = getResources().getIdentifier("bannerbug", "drawable", getPackageName());
            if (bannerResId != 0 && banner != null) banner.setImageResource(bannerResId);

            final EditText etTarget = findViewById(R.id.targetNum);
            Button btnStart = findViewById(R.id.btnStart);
            
            LinearLayout bugCrash = findViewById(R.id.bugCrash);
            LinearLayout bugDelay = findViewById(R.id.bugDelay);
            LinearLayout bugFreeze = findViewById(R.id.bugFreeze);
            LinearLayout bugQuota = findViewById(R.id.bugQuota);

            if(bugCrash != null) bugCrash.setOnClickListener(v -> bugType = "crash");
            if(bugDelay != null) bugDelay.setOnClickListener(v -> bugType = "delay");
            if(bugFreeze != null) bugFreeze.setOnClickListener(v -> bugType = "freeze");
            if(bugQuota != null) bugQuota.setOnClickListener(v -> bugType = "quota");

            Button btnPublic = findViewById(R.id.btnPublic);
            Button btnPrivate = findViewById(R.id.btnPrivate);

            if(btnPublic != null) btnPublic.setOnClickListener(v -> senderType = "public");
            if(btnPrivate != null) btnPrivate.setOnClickListener(v -> senderType = "private");

            findViewById(R.id.btnManageSender).setOnClickListener(v -> showSenderDialog());

            btnStart.setOnClickListener(v -> {
                targetNum = etTarget.getText().toString().trim();
                if(targetNum.startsWith("0")) targetNum = "62" + targetNum.substring(1);
                if(targetNum.isEmpty()) { 
                    Toast.makeText(this, "Isi nomor target!", Toast.LENGTH_SHORT).show(); 
                    return; 
                }
                
                SharedPreferences prefs = getSharedPreferences("kaze", MODE_PRIVATE);
                senderNum = prefs.getString("sender", "");

                if (senderType.equals("private") && senderNum.isEmpty()) {
                    Toast.makeText(this, "Private Sender kosong! Isi dulu.", Toast.LENGTH_LONG).show();
                    showSenderDialog();
                    return;
                }
                
                if(!isAccessibilityEnabled()) {
                    Toast.makeText(this, "Aktifkan Aksesibilitas dulu di Pengaturan HP!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } else {
                    launchWA();
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "Terjadi kesalahan layout: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showSenderDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_sender, null);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();
            if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            
            final EditText etSender = dialogView.findViewById(R.id.etSenderNumber);
            Button btnSave = dialogView.findViewById(R.id.btnSaveSender);

            SharedPreferences prefs = getSharedPreferences("kaze", MODE_PRIVATE);
            String savedNum = prefs.getString("sender", "");
            if (!savedNum.isEmpty()) etSender.setText(savedNum);

            btnSave.setOnClickListener(v -> {
                String num = etSender.getText().toString().trim();
                if(num.startsWith("0")) num = "62" + num.substring(1);
                if(!num.isEmpty()) {
                    senderNum = num;
                    prefs.edit().putString("sender", num).apply();
                    Toast.makeText(this, "Sender tersimpan!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Nomor kosong!", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal buka dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void launchWA() {
        isRunning = true;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + targetNum));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private boolean isAccessibilityEnabled() {
        String service = getPackageName() + "/" + SpamService.class.getCanonicalName();
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return enabled != null && enabled.contains(service);
        } catch (Exception e) { return false; }
    }
}
