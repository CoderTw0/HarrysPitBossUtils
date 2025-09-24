package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.setting.IntegerSetting;
import com.lukflug.examplemod8forge.setting.Setting;
import com.lukflug.panelstudio.base.IToggleable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;

public class CurseDropEstimate extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private int meter = 0;
    private final IntegerSetting height = new IntegerSetting(
            "Height", "Height", "Vertical placement of orb meter", () -> true, 1, 100, 70
    );

    private static final Pattern ORB_PATTERN = Pattern.compile(".*CURSE ORB!.*");
    private static final Pattern BOSS_PATTERN = Pattern.compile(".*BOSS DOWN!.*");

    public CurseDropEstimate() {
        super("CurseDropEstimate", "Curse drop meter", () -> true, true);
        settings.add(height);
        MinecraftForge.EVENT_BUS.register(this);

        height.setOnChange((newValue) -> {
            PersistenceHelper.setInt("CurseDropEstimate", "Height", newValue);
            PersistenceHelper.save();
        });

        loadState();
    }

    @Override
    public IToggleable isEnabled() {
        return super.isEnabled();
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!isEnabled().isOn()) return;

        String msg = event.message.getUnformattedText();

        if (ORB_PATTERN.matcher(msg).matches()) {
            meter = 0;
            PersistenceHelper.setInt("CurseDropEstimate", "Meter", meter);
            PersistenceHelper.save();
        } else if (BOSS_PATTERN.matcher(msg).matches()) {
            meter++;
            if (meter > 4) meter = 4;
            PersistenceHelper.setInt("CurseDropEstimate", "Meter", meter);
            PersistenceHelper.save();
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled().isOn() || event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() / 2;
        int y = (int) ((height.getValue() / 100.0) * sr.getScaledHeight());

        String line = "Curse Orb Meter: " + meter + "/4";
        int width = mc.fontRendererObj.getStringWidth(line);

        mc.fontRendererObj.drawStringWithShadow(line, x - width / 2, y, 0xAA00FF);
    }

    @Override
    protected void loadState() {
        super.loadState();
        int savedHeight = PersistenceHelper.getInt("CurseDropEstimate", "Height", height.getValue());
        height.setValue(savedHeight);
        meter = PersistenceHelper.getInt("CurseDropEstimate", "Meter", 0);
    }

    @Override
    protected void saveState() {
        super.saveState();
        PersistenceHelper.setInt("CurseDropEstimate", "Height", height.getValue());
        PersistenceHelper.save();

        PersistenceHelper.setInt("CurseDropEstimate", "Meter", meter);
        PersistenceHelper.save();
    }
}
