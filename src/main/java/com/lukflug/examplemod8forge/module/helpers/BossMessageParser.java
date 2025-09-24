package com.lukflug.examplemod8forge.module.helpers;

import com.lukflug.examplemod8forge.module.Debug.DebugLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BossMessageParser {
    public static final BossMessageParser INSTANCE = new BossMessageParser();
    public BossMessageParser() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    private static final Pattern COLOR_ENCODING_PATTERN = Pattern.compile("[§�].");
    private static final Pattern SHTR_READY_PATTERN = Pattern.compile("SHTR: READY");
    private static final Pattern SHDW_READY_PATTERN = Pattern.compile("SHDW: READY");
    private static final Pattern SHTR_PATTERN = Pattern.compile("SHTR:\\s*(\\d+)\\s*/\\s*(\\d+)");
    private static final Pattern SHDW_PATTERN = Pattern.compile("SHDW:\\s*(\\d+)\\s*/\\s*(\\d+)");
    public Integer sendShtrValue = null;
    public Integer sendShdwValue = null;
    public Integer shdwMax = null;
    public Integer shtrMax = null;
    public boolean sendShtrReady = false;
    public boolean sendShdwReady = false;


    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        sendShtrValue = null;
        sendShdwValue = null;
        sendShtrReady = false;
        sendShdwReady = false;
        shtrMax = null;
        shdwMax = null;

        double range = 25;
        double rangeSq = range * range;
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityArmorStand)) continue;

            if (mc.thePlayer.getDistanceSqToEntity(e) > rangeSq) continue;

            String plain = stripAll(e.getDisplayName().getFormattedText());
            DebugLogger.log("Plain: " + plain);

            int[] shtrProg = getShtrProgress(plain);
            if (shtrProg != null) {
                sendShtrValue = shtrProg[0];
                shtrMax = shtrProg[1];
                sendShtrReady = isShtrReady(plain) && sendShtrValue >= shtrMax;
                DebugLogger.log("SHTR: " + sendShtrValue + "/" + shtrMax + " READY: " + sendShtrReady);
            }

            int[] shdwProg = getShdwProgress(plain);
            if (shdwProg != null) {
                sendShdwValue = shdwProg[0];
                shdwMax = shdwProg[1];
                sendShdwReady = isShdwReady(plain) && sendShdwValue >= shdwMax;
                DebugLogger.log("SHDW: " + sendShdwValue + "/" + shdwMax + " READY: " + sendShdwReady);
            }

            if (sendShtrValue != null && sendShdwValue != null) break;
        }
    }


    public static String stripAll(String input) {
        return COLOR_ENCODING_PATTERN.matcher(input).replaceAll("");
    }

    public static Integer getShtrValue(String plain) {
        Matcher m = SHTR_PATTERN.matcher(plain);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    public static boolean isShtrReady(String plain) {
        return SHTR_READY_PATTERN.matcher(plain).find();
    }

    public static Integer getShdwValue(String plain) {
        Matcher m = SHDW_PATTERN.matcher(plain);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    public static int[] getShtrProgress(String plain) {
        Pattern pattern = Pattern.compile("SHTR:\\s*(\\d+)\\s*/\\s*(\\d+)");
        Matcher matcher = pattern.matcher(plain);
        if (matcher.find()) {
            int current = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            return new int[]{current, max};
        }
        return null;
    }

    public static int[] getShdwProgress(String plain) {
        Pattern pattern = Pattern.compile("SHDW:\\s*(\\d+)\\s*/\\s*(\\d+)");
        Matcher matcher = pattern.matcher(plain);
        if (matcher.find()) {
            int current = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            return new int[]{current, max};
        }
        return null;
    }


    public static boolean isShdwReady(String plain) {
        return SHDW_READY_PATTERN.matcher(plain).find();
    }

}
