package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.SidebarHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SessionStats extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final SidebarHelper sidebar = SidebarHelper.getInstance();

    private boolean lastStreakState = false;

    public SessionStats() {
        super("SessionStats", "Tracks coins gained during streaks", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        loadState();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.thePlayer == null || mc.theWorld == null) return;

        boolean active = sidebar.isStreakActive();

        if (active && !lastStreakState) {
            sendChat("[SessionStats] Streak started!");
        } else if (!active && lastStreakState) {
            sendChat("[SessionStats] Streak ended!");
            sendChat("[SessionStats] Gold gained: " + EnumChatFormatting.GOLD + sidebar.getGainedCoins());
        }

        lastStreakState = active;
    }

    private void sendChat(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(message));
        }
    }
}
