package com.dimchig.bedwarsbro.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiBedESP {
	
	static Minecraft mc;
	public static boolean STATE = false;
	//private static Robot robot;
	
	public GuiBedESP() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
		//try { robot = new Robot(); } catch (AWTException e) {}
	}
	
	public void updateBooleans() {
		this.STATE = HintsValidator.isBedESPActive();
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!STATE) return;
		
		ArrayList<MyBed> beds = Main.minimap.bedsFound;
		if (beds == null || beds.size() == 0) return;
		
		float partialTicks = event.partialTicks;
		
		EntityPlayerSP player = mc.thePlayer;
		World world = mc.theWorld;
		if (player == null || world == null) return;

		
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks;
        Vec3 pos = new Vec3(d0, d1, d2);
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        
		GL11.glTranslated(-pos.xCoord, -pos.yCoord, -pos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(1.0f);
		
		
		if (beds != null && beds.size() > 0) {
			for (MyBed b: beds) {
				double x = b.pos.getX();
				double y = b.pos.getY();
				double z = b.pos.getZ();
				double x2 = b.pos_feet.getX();
				double y2 = b.pos_feet.getY();
				double z2 = b.pos_feet.getZ();
				
				GL11.glColor4f(1F, 0F, 0F, 1F);
				if (b.isPlayersBed) GL11.glColor4f(0F, 1F, 0F, 1F);
				
				Main.playerFocus.drawBox(x, y, z, x2 + 1, y2 + 0.56, z2 + 1);
				
				ArrayList<BlockPos> obby = b.obsidianPoses;
				if (obby.size() > 0 && mc.thePlayer.getDistance(x, y, z) > 10) {
					GL11.glColor4f(0f, 1f, 1f, 1f);
					for (BlockPos bp: obby) {
						double px = bp.getX();
						double py = bp.getY();
						double pz = bp.getZ();
						double c = 0.02;
						Main.playerFocus.drawBox(px + c, py + c, pz + c, px + 1 - c, py + 1 - c, pz + 1 - c);
					}
				}
			}
		}
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
