package com.dimchig.bedwarsbro.stuff;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.stuff.TrajectoryPearl.MyPoint;
import com.dimchig.bedwarsbro.testing.BowAimbot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FireballSpread {
	static Minecraft mc;
	public boolean isActive = false;
	public boolean isOffsetActive = false;

	private static double MAIN_CALCULATED_VARIABLE = 0.00115;
	
	private static double MAIN_CALCULATED_RADIUS_MAX = 100;
	private static double MAIN_CALCULATED_RADIUS_MID = 50;
	private static double MAIN_CALCULATED_RADIUS_MIN = 25;
	
	private static Color color_zone_max = new Color(1f, 0f, 0f, 0.2f);
	private static Color color_zone_mid = new Color(1f, 1f, 0f, 0.25f);
	private static Color color_zone_min = new Color(0f, 1f, 0f, 0.3f);
	
	
	private static double PI_TIMES_2 = Math.PI * 2;
	private static double RADIANS_180 = Math.toRadians(180);
	
	Tessellator tessellator;
	WorldRenderer worldRenderer;
	
	public FireballSpread() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
		
		tessellator = Tessellator.getInstance();
		worldRenderer = tessellator.getWorldRenderer();
	}

	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.FIREBALL_SPREAD);
		isOffsetActive = Main.getConfigBool(CONFIG_MSG.FIREBALL_SPREAD_OFFSET_X);
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!isActive) return;
		EntityPlayerSP player = mc.thePlayer;

		if (mc.gameSettings.thirdPersonView != 0) return;
		if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.fire_charge) return;		
		
		float player_yaw = ((player.rotationYaw % 360) + 360) % 360;
		float player_pitch = player.rotationPitch;
        //Code was used to measure cirle distance
        
        /*GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);		
		GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glLineWidth(3.0f);

		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.fire_charge) {
			int range = 7;
			GL11.glColor4f(0f, 1f, 0f, 0.5f);
			for (int i = 0; i < 20; i++) drawSquare(-1, player, playerPos, event.partialTicks);
			drawSquare(1, player, playerPos, event.partialTicks);
		}
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();*/
		
		
		
		
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glLineWidth(3.0f);
		
		GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
		
		//я мучался 4 часа чтоб эти 3 строчки написать... ))
		GL11.glTranslated(0, mc.thePlayer.getEyeHeight(), 0);
		GL11.glRotated(mc.thePlayer.rotationPitch, 1, 0, 0);
		GL11.glTranslated(0, 0, 1);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		
		
		double r = 0.01;
		double bx = 0.1;
		double by = 0;
		double bz = 0;
		//Main.playerFocus.drawBox(bx + r, by + r, bz + r, bx - r, by - r, bz - r);
		
//		double max = 0;
//		double total = 0;
//		double sub = 0;
//		for (int i = 0; i < 10; i++) {
//			double k = mc.theWorld.rand.nextGaussian();
//			if (k > max) max = k;
//			total += 1;
//			if (Math.abs(k) < 1) sub++;
//		}		
		
		
		
		/*double accelX = mc.theWorld.rand.nextGaussian() * 0.4D;
		double accelY = mc.theWorld.rand.nextGaussian() * 0.4D;
		double accelZ = mc.theWorld.rand.nextGaussian() * 0.4D;		
		double d0 = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
		double accelerationX = accelX / d0 * 0.1D;
		double accelerationY = accelY / d0 * 0.1D;
		double accelerationZ = accelZ / d0 * 0.1D;
		
		double max = 0;
		double total = 0;
		double sub = 0;
		for (int i = 0; i < 10000; i++) {
			
			accelX = mc.theWorld.rand.nextGaussian() * 0.4D;
			accelY = mc.theWorld.rand.nextGaussian() * 0.4D;
			accelZ = mc.theWorld.rand.nextGaussian() * 0.4D;		
			d0 = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
			accelerationX = accelX / d0 * 0.1D;
			accelerationY = accelY / d0 * 0.1D;
			accelerationZ = accelZ / d0 * 0.1D;
			
			if (accelerationX > max) max = accelerationX;
			total++;
			if (Math.abs(accelerationX) < 0.09) sub++;
		}
		
		ChatSender.addText("&bMAX = " + max + "&7, &2sub x &7= &a" + (sub / total * 100) + "%");*/
		
		
		
		int sides = 40;
		
		double x = 0;
		double y = 0;
		double z = 0;
		
		double offset_x = -0.002;
		double offset_y = 0;
		if (isOffsetActive) {
			offset_x = -0.004;
			offset_y = -0.002;
		}
		
		drawCircle(offset_x, offset_y, 0, 64, MAIN_CALCULATED_RADIUS_MAX * MAIN_CALCULATED_VARIABLE, color_zone_max);			
		drawCircle(offset_x, offset_y, 0, 32, MAIN_CALCULATED_RADIUS_MID * MAIN_CALCULATED_VARIABLE, color_zone_mid);
		drawCircle(offset_x, offset_y, 0, 16, MAIN_CALCULATED_RADIUS_MIN * MAIN_CALCULATED_VARIABLE, color_zone_min);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	public void drawCircle(double x, double y, double z, double sides, double radius, Color color) {
		GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		
		worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
		worldRenderer.pos(x, y, z).endVertex();
		
		
		for(int i = 0; i <= sides;i++) 
		{
			double angle = (PI_TIMES_2 * i / sides) + RADIANS_180;
			worldRenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, z).endVertex();
		}
		tessellator.draw();			
	}
	
	//was used to measure circle width and height
	
     private void drawSquare(int range, EntityPlayerSP player, Vec3 playerPos, float partialTicks) {
		float p_yaw = mc.thePlayer.rotationYaw;
		float p_pitch = mc.thePlayer.rotationPitch;
       
		p_yaw = 0;
		p_pitch = 0;
		
        double posX = 0;
        double posY = 0 + mc.thePlayer.getEyeHeight();
        double posZ = 0;
        
        double yaw = p_yaw;
		double pitch = p_pitch;
        double pitchWithOffset = p_pitch + 0f;
        double motionX = -MathHelper.sin((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
		double motionY = -MathHelper.sin((float) (pitchWithOffset * 0.017453292f));
		double motionZ = MathHelper.cos((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
        
		double v = range;

		double accelX = mc.theWorld.rand.nextGaussian() * 0.4D;
		double accelY = mc.theWorld.rand.nextGaussian() * 0.4D;
		double accelZ = mc.theWorld.rand.nextGaussian() * 0.4D;
		
		if (range == 1) {
			accelX = 20 * 0.4D;
			accelY = 0 * 0.4D;
			accelZ = 0 * 0.4D;
			GL11.glColor4f(1f, 0f, 0f, 1f);
		}
		
		double d0 = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
		double accelerationX = accelX / d0 * 0.1D;
		double accelerationY = accelY / d0 * 0.1D;
		double accelerationZ = accelZ / d0 * 0.1D;
		
		if (range == 1) accelerationX = 0.00;
	   // ChatSender.addText(accelerationX);
	    
	    posX += accelerationX;
        posY += accelerationY;
        posZ += 1;
        //if (range == 1) ChatSender.addText(posX + " " + posY + " " + motionZ);

        //ChatSender.addText(posX);
        
      
        Vec3 pos = new Vec3(posX, posY, posZ);

		if (pos != null) {

			double hitX = pos.xCoord;
			double hitY = pos.yCoord;
			double hitZ = pos.zCoord;
			
			double area = 0.005;
			Main.playerFocus.drawBox(hitX - area, hitY - area, hitZ - area, hitX + area, hitY + area, hitZ + area);
		}
	}

	private double myMap(double value, double leftMin, double leftMax, double rightMin, double rightMax) {
		double leftSpan = leftMax - leftMin;
		double rightSpan = rightMax - rightMin;
		double valueScaled = (value - leftMin) / (leftSpan);
	    return rightMin + (valueScaled * rightSpan);
	}

	
	private Vec3 getVectorWithPartialTicks(Entity en, float partialTicks) {
		double x = en.prevPosX + (en.posX - en.prevPosX) * (double)partialTicks;
		double y = en.prevPosY + (en.posY - en.prevPosY) * (double)partialTicks;
		double z = en.prevPosZ + (en.posZ - en.prevPosZ) * (double)partialTicks;
		return new Vec3(x, y, z);
	}
	
	
	//WAS IN TICK EVENT TO MEASURE FIREBALL SPREAD
	/*List<EntityFireball> fireballs = mc.theWorld.getEntities(EntityFireball.class, EntitySelectors.selectAnything);
	if (fireballs != null && fireballs.size() > 0) {
		for (EntityFireball fireball: fireballs) {
			
			double x2 = fireball.posX;
			double y2 = fireball.posY;
			double z2 = fireball.posZ;
			
			double dX = mc.thePlayer.posX - x2;
			double dY = mc.thePlayer.posY - y2 + mc.thePlayer.height;
			double dZ = mc.thePlayer.posZ - z2;
			float yaw = (float)Math.atan2(dZ, dX);
			float pitch = (float)(Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI);
			
			//Minecraft.getMinecraft().thePlayer.setAngles((float)yaw, (float)pitch);
			float t_yaw = HintsFinder.myMap(yaw, (float)-Math.PI, (float)Math.PI, -180, 180) + 90;
			float t_pitch = HintsFinder.myMap(pitch, (float)Math.PI, (float)Math.PI*2, 90, -90);
			
			double p_yaw = ((mc.thePlayer.rotationYaw % 360) + 360) % 360;
			double p_pitch = mc.thePlayer.rotationPitch;
			double ball_yaw = (((fireball.rotationYaw + 180) % 360) + 360) % 360;
			double ball_pitch = fireball.rotationPitch;
			double d1 = Math.abs(p_yaw - ball_yaw);
			double d2 = Math.abs(p_pitch - ball_pitch);
			
			ChatSender.addText("&bYAW   &7: &a" + p_yaw + "&7, &c" + ball_yaw);
			ChatSender.addText("&bPITCH &7: &a" + p_pitch + "&7, &c" + ball_pitch);
			ChatSender.addText("&bDIFF  &7: &e" + d1 + "&7, &e" + d2 + "&f,");
			ChatSender.addText("&b&l" + (d1 < 3.5 && d2 < 3.5));
			
			break;
		}
	}*/
}
