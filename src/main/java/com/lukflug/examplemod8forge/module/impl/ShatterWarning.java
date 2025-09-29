package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.BossHudRender;
import com.lukflug.examplemod8forge.module.helpers.BossMessageParser;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ShatterWarning extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BossMessageParser parser;
    private final BossHudRender hud = BossHudRender.getInstance();

    public ShatterWarning() {
        super("ShatterWarning", "Warns shatter procs", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        this.parser = BossMessageParser.INSTANCE;
        loadState();
    }

    @Override
    public IToggleable isEnabled() {
        return super.isEnabled();
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.thePlayer == null || mc.theWorld == null) return;

        if (parser.sendShtrValue != null && parser.shtrMax != null) {
            int current = parser.sendShtrValue;
            int max = parser.shtrMax;
            boolean ready = parser.sendShtrReady;

            if (current == max || ready) {
                hud.setMessage("SHATTER PROCCING", 2000);
            } else if (current == max - 2 || current == max -1 ) {
                hud.setMessage("SHATTER INCOMING GET BACK", 2000);
            }
        }
    }
}
