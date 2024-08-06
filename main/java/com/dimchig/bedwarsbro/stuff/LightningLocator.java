package com.dimchig.bedwarsbro.stuff;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LightningLocator {
	static Minecraft mc;
	public static boolean isActive = false;
	public static MyLightning last_lightning = null;
	
	public class MyLightning {
		public EntityLightningBolt lightning;
		public long time_created;
		
		public MyLightning(EntityLightningBolt lightning, long time_created) {
			this.lightning = lightning;
			this.time_created = time_created;
		}
	}
	
	public LightningLocator() {
		mc = Minecraft.getMinecraft();
		last_lightning = null;
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (mc.theWorld == null) return;
		List<Entity> weather_effects = mc.theWorld.weatherEffects;
		if (weather_effects != null && weather_effects.size() > 0) {
		
			EntityLightningBolt last_lig = null;
			double min_ticks = 9999;
			for (Entity en: weather_effects) {
				if (!(en instanceof EntityLightningBolt)) continue;
				EntityLightningBolt lig = (EntityLightningBolt) en;
				int ticks = lig.ticksExisted;
				if (ticks < min_ticks) {
					min_ticks = ticks;
					last_lig = lig;
				}
			}
			
			if (last_lig != null) last_lightning = new MyLightning(last_lig, new Date().getTime());
		}
				
		if (isActive) {
			drawLastLightning(event.partialTicks);
		}
	}
	
	public void drawLastLightning(float partialTicks) {
		if (last_lightning == null) return;
		double size = 1;
		double height = 200;
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);  
		double x = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * (double)partialTicks;
		double y = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * (double)partialTicks;
		double z = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * (double)partialTicks;
		Vec3 playerPos = new Vec3(x, y, z);
		GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(1.0f);
		
		double posX = last_lightning.lightning.posX + 0.5;
		double posY = last_lightning.lightning.posY - 2;
		double posZ = last_lightning.lightning.posZ + 0.5;
		
		height = 255 - posY;
		
		Color color = new Color(0f, 1f, 1f, 1f);
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1F);
		Main.playerFocus.drawBoxAroundBlock(posX, posY, posZ, -size, 0, -size, size, height, size);		
		
		color = new Color(1f, 1f, 1f, 1f);
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1F);
		size += 0.01;
		Main.playerFocus.drawBox(posX - size, posY, posZ -size, posX + size - 1, posY + height - 1, posZ + size - 1);
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
