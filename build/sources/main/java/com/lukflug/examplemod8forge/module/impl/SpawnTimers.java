package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Debug.DebugLogger;
import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.setting.IntegerSetting;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawnTimers extends Module {
    private static final Pattern COLOR_ENCODING_PATTERN = Pattern.compile("[§�].");
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final Pattern RECHARGING_PATTERN = Pattern.compile("Recharging\\.\\.\\. \\(\\s*(?:(\\d+)m\\s*)?(\\d+)s\\s*\\)");
    private static final Pattern READY_PATTERN = Pattern.compile("READY! Expires: (?:(\\d+)m\\s*)?(\\d+)s");

    private final Map<String, String> spawnerTimers = new HashMap<>();
    private final IntegerSetting Height = new IntegerSetting(
            "Height", "Height", "Height of timer display", () -> true, 1, 100, 50
    );

    public SpawnTimers() {
        super("SpawnTimers", "Boss spawner status list", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        settings.add(Height);
        Height.setOnChange((newValue) -> {
            PersistenceHelper.setInt("SpawnTimers", "Height", newValue);
            PersistenceHelper.save();
        });
        loadState();
    }

    @Override
    public IToggleable isEnabled() {
        return super.isEnabled();
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.thePlayer == null || mc.theWorld == null) return;

        spawnerTimers.clear();

        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityArmorStand)) continue;

            EntityArmorStand stand = (EntityArmorStand) e;

            DebugLogger.log("Found ArmorStand: " + stand.getDisplayName().getFormattedText());

            String plain = stripAll(stand.getDisplayName().getFormattedText());
            DebugLogger.log("[SpawnTimers] Plain: " + plain);

            Matcher m1 = RECHARGING_PATTERN.matcher(plain);
            Matcher m2 = READY_PATTERN.matcher(plain);

            String quadrant = getQuadrant(stand.posX, stand.posZ);

            if (m1.find()) {
                String minutes = m1.group(1);
                String seconds = m1.group(2);
                String time = (minutes != null ? minutes + "m " : "") + seconds + "s";
                spawnerTimers.put(quadrant, "Recharging (" + time + ")");
            } else if (m2.find()) {
                String expires = m2.group(1);
                spawnerTimers.put(quadrant, "READY! (" + expires + ")");
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled().isOn() || event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        int baseY = (int) ((Height.getValue() / 100.0) * screenHeight);
        int y = baseY;

        if (spawnerTimers.isEmpty()) {
            String line = "No spawners found";
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            int x = screenWidth - lineWidth - 5;
            mc.fontRendererObj.drawStringWithShadow(line, x, y, 0xAA00FF);
            return;
        }

        for (Map.Entry<String, String> entry : spawnerTimers.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue();
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            int x = screenWidth - lineWidth - 5;
            mc.fontRendererObj.drawStringWithShadow(line, x, y, 0xAA00FF);
            y += mc.fontRendererObj.FONT_HEIGHT + 2;
        }
    }

    public static String stripAll(String input) {
        if (input == null) return "";
        return COLOR_ENCODING_PATTERN.matcher(input).replaceAll("");
    }

    private String getQuadrant(double x, double z) {
        if (x >= 0 && z >= 0) return "+,+";
        if (x >= 0 && z < 0) return "+,-";
        if (x < 0 && z >= 0) return "-,+";
        return "-,-";
    }

    @Override
    protected void loadState() {
        super.loadState();
        int savedHeight = PersistenceHelper.getInt("SpawnTimers", "Height", Height.getValue());
        Height.setValue(savedHeight);
    }

    @Override
    protected void saveState() {
        super.saveState();
        PersistenceHelper.setInt("SpawnTimers", "Height", Height.getValue());
        PersistenceHelper.save();
    }
}
