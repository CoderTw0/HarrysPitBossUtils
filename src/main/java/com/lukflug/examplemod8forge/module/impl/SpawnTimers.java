package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Debug.DebugLogger;
import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.MapDetectionHelper;
import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.module.helpers.QuadrentLabelHelper;
import com.lukflug.examplemod8forge.setting.IntegerSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SpawnTimers extends Module {
    private static final Pattern COLOR_ENCODING_PATTERN = Pattern.compile("[§�].");
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final Map<String, Long> spawnerTimers = new HashMap<>();

    private final IntegerSetting Height = new IntegerSetting(
            "Height", "Height", "Height of timer display", () -> true, 1, 100, 50
    );

    public SpawnTimers() {
        super("SpawnTimers", "Boss spawner status list", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        settings.add(Height);
        Height.setOnChange(newValue -> {
            PersistenceHelper.setInt("SpawnTimers", "Height", newValue);
            PersistenceHelper.save();
        });
        loadState();
    }

    @SubscribeEvent
    public void onTick(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.thePlayer == null || mc.theWorld == null) return;

        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityArmorStand)) continue;
            EntityArmorStand stand = (EntityArmorStand) e;

            String plain = stripAll(stand.getDisplayName().getFormattedText());
            if (plain.startsWith("READY!")) {
                String quadrant = getQuadrant(stand.posX, stand.posZ);

                Long existing = spawnerTimers.get(quadrant);
                if (existing == null || existing == 0L) {
                    spawnerTimers.put(quadrant, 0L);
                    DebugLogger.log("[SpawnTimers] Found READY! spawner at " + quadrant);
                } else {

                    long now = System.currentTimeMillis();
                    if (existing > now) {
                        DebugLogger.log("[SpawnTimers] Ignored READY! at " + quadrant + " (still on cooldown)");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!isEnabled().isOn()) return;

        String msg = stripAll(event.message.getUnformattedText());
        if (msg.startsWith("BOSS! You challenged")) {
            EntityArmorStand nearest = null;
            double bestDist = Double.MAX_VALUE;
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (!(e instanceof EntityArmorStand)) continue;
                double dist = e.getDistanceSqToEntity(mc.thePlayer);
                if (dist < bestDist) {
                    bestDist = dist;
                    nearest = (EntityArmorStand) e;
                }
            }

            if (nearest != null) {
                String quadrant = getQuadrant(nearest.posX, nearest.posZ);
                long expiry = System.currentTimeMillis() + (12 * 60 * 1000);
                spawnerTimers.put(quadrant, expiry);
                DebugLogger.log("[SpawnTimers] Started 12m timer for " + quadrant);
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

        String mapName = MapDetectionHelper.INSTANCE.Map;
        String mapLine = "Map: " + mapName;
        int mapWidth = mc.fontRendererObj.getStringWidth(mapLine);
        int mapX = screenWidth - mapWidth - 5;
        mc.fontRendererObj.drawStringWithShadow(mapLine, mapX, y, 0x55FF55);
        y += mc.fontRendererObj.FONT_HEIGHT + 4;

        if (spawnerTimers.isEmpty()) {
            String line = "No spawners tracked";
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            int x = screenWidth - lineWidth - 5;
            mc.fontRendererObj.drawStringWithShadow(line, x, y, 0xAA00FF);
            return;
        }

        long now = System.currentTimeMillis();

        for (Map.Entry<String, Long> entry : spawnerTimers.entrySet()) {
            String quadrant = entry.getKey();
            long expiry = entry.getValue();

            String label = QuadrentLabelHelper.getLabelForQuadrant(mapName, quadrant);

            String text;
            if (expiry == 0L) {
                text = label + ": READY!";
            } else {
                long remaining = expiry - now;
                if (remaining <= 0) {
                    text = label + ": READY!";
                    entry.setValue(0L);
                } else {
                    long minutes = (remaining / 1000) / 60;
                    long seconds = (remaining / 1000) % 60;
                    text = label + ": " + minutes + "m " + seconds + "s";
                }
            }

            int lineWidth = mc.fontRendererObj.getStringWidth(text);
            int x = screenWidth - lineWidth - 5;
            mc.fontRendererObj.drawStringWithShadow(text, x, y, 0xAA00FF);
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
