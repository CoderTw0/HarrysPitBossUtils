package com.lukflug.examplemod8forge.module.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MapDetectionHelper {
    public static final MapDetectionHelper INSTANCE = new MapDetectionHelper();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public MapDetectionHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String Map = "None Found";
    public long lastCheck = 0L;
    private long worldLoadTime = 0L;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        Map = "None Found";
        worldLoadTime = System.currentTimeMillis();
        lastCheck = 0;

    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!"None Found".equals(Map)) return;

        long now = System.currentTimeMillis();

        if (now - worldLoadTime < 2500L) return;

        if (now - lastCheck < 2500L) return;
        lastCheck = now;

        if (MatchBlock(-18, 115, 0, Block.getBlockFromName("minecraft:enchanting_table"))) {
            Map = "Seasons";
        } else if (MatchBlock(0, 97, -17, Block.getBlockFromName("minecraft:enchanting_table"))) {
            Map = "Kings Map";
        } else if (MatchBlock(100, 50, 100, Block.getBlockFromName("minecraft:air"))) {
            Map = "Paradise Grove";
        }
    }

    public boolean MatchBlock(int x, int y, int z, Block expected) {
        if (mc.theWorld == null) return false;
        BlockPos pos = new BlockPos(x, y, z);
        Block blockAtPos = mc.theWorld.getBlockState(pos).getBlock();
        return blockAtPos == expected;
    }

}
