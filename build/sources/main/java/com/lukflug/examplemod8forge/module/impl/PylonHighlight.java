package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class PylonHighlight extends Module {
    private boolean enabled = false;
    private static final Minecraft mc = Minecraft.getMinecraft();
    //TODO
    public PylonHighlight() {
        super("PylonHighlight", "WORK IN PROGRESS", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
