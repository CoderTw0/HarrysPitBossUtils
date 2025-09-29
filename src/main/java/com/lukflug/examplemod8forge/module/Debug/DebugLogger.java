package com.lukflug.examplemod8forge.module.Debug;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.panelstudio.base.IToggleable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DebugLogger extends Module {
    public static boolean enabled = false;
    private static final long DEBUG_INTERVAL = 1000L;
    private static final Map<String, Long> lastPrintTime = new ConcurrentHashMap<>();
    public DebugLogger() {
        super("Debug", "Debug Message Helper", () -> true, true);
    }

    @Override
    public IToggleable isEnabled() {
        return new IToggleable() {
            @Override
            public boolean isOn() {
                return enabled;
            }

            @Override
            public void toggle() {
                enabled = !enabled;
            }
        };
    }

    public static void log(String msg) {
        if (!enabled) return;
        long now = System.currentTimeMillis();
        long last = lastPrintTime.getOrDefault(msg, 0L);

        if (now - last >= DEBUG_INTERVAL) {
            lastPrintTime.put(msg, now);
            System.out.println("[Debug] " + msg);
        }
    }
}
