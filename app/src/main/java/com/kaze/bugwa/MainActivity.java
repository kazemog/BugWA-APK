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
    public static String senderType = "private"; // Default private

    private LinearLayout currentBugBtn;
    private Button btnPublic, btnPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Banner (Anti Crash)
        ImageView banner = findViewById(R.id.bannerImage);
        int bannerResId = getResources().getIdentifier("bannerbug", "drawable", getPackageName());
        if (bannerResId != 0) banner.setImageResource(bannerResId);

        final EditText etTarget = findViewById(R.id.targetNum);
        Button btnStart = findViewById(R.id.btnStart);
        
        // Setup Bug Gradient Selection
        LinearLayout bugCrash = findViewById(R.id.bugCrash);
        LinearLayout bugDelay = findViewById(R.id.bugDelay);
        LinearLayout bugFreeze = findViewById(R.id.bugFreeze);
        LinearLayout bugQuota = findViewById(R.id.bugQuota);
        currentBugBtn = bugCrash; // Default

        bugCrash.setOnClickListener(v -> selectBug("crash", bugCrash));
        bugDelay.setOnClickListener(v -> selectBug("delay", bugDelay));
        bugFreeze.setOnClickListener(v -> selectBug("freeze", bugFreeze));
        bugQuota.setOnClickListener(v -> selectBug("quota", bugQuota));

        // Setup Sender Gradient Selection
        btnPublic = findViewById(R.id.btnPublic);
        btnPrivate = findViewById(R.id.btnPrivate);
        
        btnPublic.setOnClickListener(v -> selectSender("public", btnPublic, btnPrivate));
        btnPrivate.setOnClickListener(v -> selectSender("private", btnPrivate, btnPublic));

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

            // Validasi Sender
            if (senderType.equals("private") && senderNum.isEmpty()) {
                Toast.makeText(this, "Private Sender kosong! Silakan Manage Sender dulu.", Toast.LENGTH_LONG).show();
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
    }

    private void selectBug(String type, LinearLayout btn) {
        bugType = type;
        // Reset semua ke inactive
        LinearLayout[] btns = {findViewById(R.id.bugCrash), findViewById(R.id.bugDelay), findViewById(R.id.bugFreeze), findViewById(R.id.bugQuota)};
        for(LinearLayout b : btns) b.setBackgroundResource(R.drawable.bg_bug_inactive);
        // Set yang dipilih ke active (gradient emas)
        btn.setBackgroundResource(R.drawable.bg_bug_active);
        currentBugBtn = btn;
    }

    private void selectSender(String type, Button selected, Button unselected) {
        senderType = type;
        selected.setBackgroundResource(R.drawable.bg_bug_active);
        unselected.setBackgroundResource(R.drawable.bg_bug_inactive);
    }

    private void showSenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sender, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        final EditText etSender = dialogView.findViewById(R.id.etSenderNumber);
        Button btnSave = dialogView.findViewById(R.id.btnSaveSender);

        // Load nomor yang udah disimpen
        SharedPreferences prefs = getSharedPreferences("kaze", MODE_PRIVATE);
        String savedNum = prefs.getString("sender", "");
        if (!savedNum.isEmpty()) etSender.setText(savedNum);

        btnSave.setOnClickListener(v -> {
            String num = etSender.getText().toString().trim();
            if(num.startsWith("0")) num = "62" + num.substring(1);
            if(!num.isEmpty()) {
                senderNum = num;
                prefs.edit().putString("sender", num).apply();
                Toast.makeText(this, "Sender Private tersimpan!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Nomor tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void launchWA() {
        isRunning = true;
        String s = senderType.equals("public") ? "Global Sender" : senderNum;
        Toast.makeText(this, "Menembak via " + s, Toast.LENGTH_SHORT).show();
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
