package com.lukflug.examplemod8forge.module.impl;

import com.lukflug.examplemod8forge.module.Module;
import com.lukflug.panelstudio.base.IToggleable;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//TODO. not yet working. 1.8.9 is a pain
public class PylonHighlight extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String PYLON_NAME = "MundoSK-Name";
    private static final UUID PYLON_UUID = UUID.fromString("df46467c-7caf-4aff-8a7e-08b27e337f35");

    public PylonHighlight() {
        super("PylonHighlight", "Highlights Iron Giant Pylons", () -> true, false);
        MinecraftForge.EVENT_BUS.register(this);
        loadState();
    }

    @Override
    public IToggleable isEnabled() {
        return super.isEnabled();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled().isOn() || mc.theWorld == null || mc.thePlayer == null) return;

        List<Vec3> skulls = new ArrayList<>();
        for (Object o : mc.theWorld.loadedTileEntityList) {
            if (!(o instanceof TileEntitySkull)) continue;

            TileEntitySkull skull = (TileEntitySkull) o;
            if (skull.getSkullType() != 3 || skull.getPlayerProfile() == null) continue;

            GameProfile profile = skull.getPlayerProfile();
            boolean match = PYLON_NAME.equals(profile.getName()) || (profile.getId() != null && PYLON_UUID.equals(profile.getId()));

            if (!match) continue;
            if (mc.theWorld.getBlockState(skull.getPos().down()).getBlock() != net.minecraft.init.Blocks.acacia_fence) continue;

            skulls.add(new Vec3(
                    skull.getPos().getX() + 0.5,
                    skull.getPos().getY() + 0.5,
                    skull.getPos().getZ() + 0.5
            ));
        }

        if (skulls.isEmpty()) return;

        List<Vec3> golems = new ArrayList<>();
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityIronGolem) {
                golems.add(new Vec3(e.posX, e.posY + e.height / 2.0, e.posZ));
            }
        }

        for (int i = 0; i < skulls.size(); i++) {
            for (int j = i + 1; j < skulls.size(); j++) {
                drawLine(skulls.get(i), skulls.get(j));
            }
        }

        for (Vec3 skull : skulls) {
            for (Vec3 golem : golems) {
                drawLine(skull, golem);
            }
        }
    }

    private void drawLine(Vec3 start, Vec3 end) {
        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glLineWidth(2.0F);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F); // red

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer buffer = tess.getWorldRenderer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(start.xCoord - renderPosX, start.yCoord - renderPosY, start.zCoord - renderPosZ).endVertex();
        buffer.pos(end.xCoord - renderPosX, end.yCoord - renderPosY, end.zCoord - renderPosZ).endVertex();
        tess.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
