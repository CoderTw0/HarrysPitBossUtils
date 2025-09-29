package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.examplemod8forge.module.helpers.BossHudRender;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RoombaWarning extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String ROOMBA =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc3MTM5OTQ0MTM2MDg5NjI2ZWM1OTZhODlhOGRjMDRhMjViMDU3YjA1Mzc5ODllNDdhYmE2Mjg5YjgyMWVmMCJ9fX0=";

    private final BossHudRender hud = BossHudRender.getInstance();

    public RoombaWarning() {
        super("RoombaWarning", "Warning for Roomba Bomb", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
        loadState();
    }

    private long lastRoomba = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        long now = System.currentTimeMillis();
        double range = 25;
        double rangeSq = range * range;
        if (!isEnabled().isOn() || mc.thePlayer == null ||mc.theWorld == null) return;

        boolean found = false;
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityArmorStand) {
                if (mc.thePlayer.getDistanceSqToEntity(e) > rangeSq) continue;
                EntityArmorStand stand = (EntityArmorStand) e;
                ItemStack head = stand.getCurrentArmor(3);
                if (head != null && isRoomba(head)) {
                    found = true;
                    break;
                }
            }
        }
        if (found && now - lastRoomba > 3000) {
            hud.setMessage("ROOMBA INCOMING", 3000);
            lastRoomba = now;

        }
    }

    private boolean isRoomba(ItemStack stack) {
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("SkullOwner", 10)) return false;
        NBTTagCompound skullOwner = tag.getCompoundTag("SkullOwner");
        if (!skullOwner.hasKey("Properties", 10)) return false;
        NBTTagCompound props = skullOwner.getCompoundTag("Properties");
        if (!props.hasKey("textures", 9)) return false;
        for (int i = 0; i < props.getTagList("textures", 10).tagCount(); ++i) {
            NBTTagCompound tex = props.getTagList("textures", 10).getCompoundTagAt(i);
            String value = tex.getString("Value");
            if (ROOMBA.equals(value)) return true;
        }
        return false;
    }
}