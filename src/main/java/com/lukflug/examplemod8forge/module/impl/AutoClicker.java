package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.setting.BooleanSetting;
import com.lukflug.examplemod8forge.setting.IntegerSetting;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoClicker extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final IntegerSetting cps = new IntegerSetting(
            "CPS", "Clicks per second", "Clicks Per Second", () -> true, 1, 14, 14
    );
    private final BooleanSetting mouseDown = new BooleanSetting(
            "MouseDown", "Require Mouse Down", "Only clicks when mouse button is held", () -> true, true
    );

    private long lastClickTime = 0;

    public AutoClicker() {
        super("AutoClicker", "AutoClicker", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        settings.add(cps);
        settings.add(mouseDown);

        cps.setOnChange((newValue) -> {
            PersistenceHelper.setInt("AutoClicker", "cps", newValue);
            PersistenceHelper.save();
        });

        mouseDown.setOnChange((newValue) -> {
            PersistenceHelper.setBoolean("AutoClicker", "mouseDown", newValue);
            PersistenceHelper.save();
        });

        loadState();
    }

    @Override
    public IToggleable isEnabled() {
        return super.isEnabled();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.theWorld == null || mc.thePlayer == null) return;
        if (mc.currentScreen != null) return;

        boolean requireMouseDownNow = mouseDown.isOn();
        boolean isMouseHeldNow = org.lwjgl.input.Mouse.isButtonDown(0);
        if (requireMouseDownNow && !isMouseHeldNow) return;

        long currentTime = System.currentTimeMillis();
        int cpsValue = Math.max(cps.getValue(), 1);
        long delay = 1000L / cpsValue;

        if (currentTime - lastClickTime >= delay) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
            lastClickTime = currentTime;
        }
    }

    @Override
    protected void loadState() {
        super.loadState();
        int savedCPS = PersistenceHelper.getInt("AutoClicker", "cps", cps.getValue());
        cps.setValue(savedCPS);

        boolean savedMouseDown = PersistenceHelper.getBoolean("AutoClicker", "mouseDown", mouseDown.isOn());
        mouseDown.setValue(savedMouseDown);
    }

    @Override
    protected void saveState() {
        super.saveState();
        PersistenceHelper.setInt("AutoClicker", "cps", cps.getValue());
        PersistenceHelper.setBoolean("AutoClicker", "mouseDown", mouseDown.isOn());
        PersistenceHelper.save();
    }
}