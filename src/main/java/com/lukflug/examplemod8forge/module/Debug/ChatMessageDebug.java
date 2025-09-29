package com.lukflug.examplemod8forge.module.Debug;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.setting.KeybindSetting;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatMessageDebug extends Module {
    private static Minecraft mc = Minecraft.getMinecraft();
    private boolean enabled = false;
    private static KeybindSetting keybind = new KeybindSetting("keybind", "keybind", "Key to toggle debugger", () -> true, 0);

    public ChatMessageDebug() {
        super("ChatMessageDebugger", "Parses chat messages", () -> true, true);
        settings.add(keybind);
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
    public void onChat(net.minecraftforge.client.event.ClientChatReceivedEvent event) {
        if (!enabled || mc.thePlayer == null || mc.theWorld == null) return;
        String message = event.message.getUnformattedText();
        DebugLogger.log("Chat Message: " + message);

        String stripped = event.message.getFormattedText().replaceAll("[§�].", "");
        DebugLogger.log("Stripped Message: " + stripped);
    }
}
