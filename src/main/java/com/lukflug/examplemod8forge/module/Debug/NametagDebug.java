package com.lukflug.examplemod8forge.module.Debug;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NametagDebug extends Module {
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean enabled = false;

    public NametagDebug() {
        super("NametagDebug", "Debugs into unformatted nametags", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
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

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (!enabled || mc.thePlayer == null || mc.theWorld == null) return;
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityArmorStand)) continue;
            DebugLogger.log("Nametag: " + e.getDisplayName().getFormattedText());
        }
    }
}
