package com.lukflug.examplemod8forge.module.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BossHudRender {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static class HudMessage {
        final String text;
        long until;

        HudMessage(String text, long durationMs) {
            this.text = text;
            this.until = System.currentTimeMillis() + durationMs;
        }
    }

    private static final BossHudRender INSTANCE = new BossHudRender(true);

    public static BossHudRender getInstance() {
        return INSTANCE;
    }

    private HudMessage current = null;

    private BossHudRender(boolean register) {
        if (register) MinecraftForge.EVENT_BUS.register(this);
    }

    public void setMessage(String msg, long durationMs) {
        long now = System.currentTimeMillis();

        if (current != null) {
            current.until = now + 250;
        }

        current = new HudMessage(msg, durationMs);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        render(2);
    }

    private void render(int scale) {
        long now = System.currentTimeMillis();
        if (current == null || now >= current.until) return;

        String message = current.text;
        int rgb = 0xAA00FF;
        int color = (255 << 24) | rgb;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1f);

        ScaledResolution sr = new ScaledResolution(mc);
        float x = (sr.getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth(message) * scale / 2f) / scale;
        float y = (sr.getScaledHeight() / 4f) / scale;

        mc.fontRendererObj.drawStringWithShadow(message, x, y, color);

        GL11.glPopMatrix();
    }
}
