package com.kaze.bugwa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static boolean isRunning = false;
    public static String targetNum = "";
    public static int maxCount = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etTarget = findViewById(R.id.targetNum);
        EditText etCount = findViewById(R.id.msgCount);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        TextView status = findViewById(R.id.status);

        btnStart.setOnClickListener(v -> {
            targetNum = etTarget.getText().toString().trim();
            try { maxCount = Integer.parseInt(etCount.getText().toString()); } catch (Exception e) { maxCount = 999; }
            if(targetNum.isEmpty()) { Toast.makeText(this, "Isi nomor target!", Toast.LENGTH_SHORT).show(); return; }

            if(!isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "Aktifkan Aksesibilitas dulu untuk Kaze Bug WA!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } else {
                isRunning = true;
                status.setText("Status: MENEMBAK...");
                Toast.makeText(this, "Mulai nembak " + targetNum, Toast.LENGTH_SHORT).show();
                startService(new Intent(this, SpamService.class).setAction("START"));
            }
        });

        btnStop.setOnClickListener(v -> {
            isRunning = false;
            status.setText("Status: Berhenti");
            startService(new Intent(this, SpamService.class).setAction("STOP"));
        });
    }

    private boolean isAccessibilityServiceEnabled() {
        String service = getPackageName() + "/" + SpamService.class.getCanonicalName();
        try {
            String enabledServices = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return enabledServices != null && enabledServices.contains(service);
        } catch (Exception e) { return false; }
    }
}