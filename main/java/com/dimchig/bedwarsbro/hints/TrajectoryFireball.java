package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.nolegit.BowAimbot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrajectoryFireball {
	static Minecraft mc;
	public boolean isActive = false;
	
	public TrajectoryFireball() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
	}
	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.FIREBALL_PREDICTION);
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!isActive) return;
		
		EntityPlayerSP player = mc.thePlayer;
		float player_yaw = ((player.rotationYaw % 360) + 360) % 360;
		float player_pitch = player.rotationPitch;

        Vec3 playerPos = getVectorWithPartialTicks(player, event.partialTicks);
        
        ArrayList<Vec3> positions = new ArrayList<Vec3>(); 
        
        GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);  
		GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(3.0f);
		
		
		
		List<EntityFireball> fireballs = mc.theWorld.getEntities(EntityFireball.class, EntitySelectors.selectAnything);
		if (fireballs != null && fireballs.size() > 0) {
			for (EntityFireball fireball: fireballs) {				
				Vec3 vecStart = getVectorWithPartialTicks(fireball, event.partialTicks);
				
				double x = vecStart.xCoord;
				double y = vecStart.yCoord;
				double z = vecStart.zCoord;
				
				double motionX = fireball.motionX;
				double motionY = fireball.motionY;
				double motionZ = fireball.motionZ;
				
				double percision = 1;
				
				if (y > 300 || y < -10) continue;
				
				Vec3 prev_pos = vecStart;
				
				int MAX_ITERATIONS = 100;
				boolean isCollision = false;
				GL11.glColor4f(1f, 0.5f, 0f, 1f);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				for (int i = 0; i < MAX_ITERATIONS; i++) {
					 Vec3 pos = new Vec3(x, y, z);
					 x += motionX * percision;
				     y += motionY * percision;
				     z += motionZ * percision;
				     
				     MovingObjectPosition movingobjectposition = mc.theWorld.rayTraceBlocks(pos, new Vec3(x, y, z));
			         if (movingobjectposition != null) {
			        	 isCollision = true;
			        	 break;
			         }
				     
				     pos = new Vec3(x, y, z);
				     if (prev_pos == null) {
				    	 prev_pos = pos;
				    	 continue;				    	
				     }
				     
				     Main.playerFocus.drawLineWithGL(prev_pos, pos);
				     prev_pos = pos;
				}
				
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				if (isCollision) {
					double c = 0.4; 
					GL11.glColor4f(1f, 0f, 0f, 1f);
					Main.playerFocus.drawBox(x - c, y - c, z - c, x + c, y + c, z + c);
				}
				/*ArrayList<MyPoint> pearl_points = getHitTrajectoryPearl(mc.theWorld, pearl);
				if (pearl_points.size() > 10) {
					MyPoint point = pearl_points.get(pearl_points.size() - 1);
					Vec3 pos = point.pos;
					double c = 0.1;
					double hitX = Math.floor(pos.xCoord);
					double hitY = Math.floor(pos.yCoord);
					double hitZ = Math.floor(pos.zCoord);
					GL11.glColor4f(0f, 1f, 1f, 1f);		
					Main.playerFocus.drawBox(hitX, hitY, hitZ, hitX + 1, hitY + 1, hitZ + 1);
					
					double dist = Math.sqrt(Math.pow(playerPos.xCoord - hitX - 0.5, 2) + Math.pow(playerPos.zCoord - hitZ - 0.5, 2));
					if (dist < 20) {						
						String text = new DecimalFormat("0.0").format(point.step_cnt / 150f);
						GL11.glTranslated(playerPos.xCoord, playerPos.yCoord, playerPos.zCoord);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						Main.draw3DText.drawText(playerPos, new Vec3(hitX + 0.5, hitY + 2, hitZ + 0.5), player, text, new Color(0f, 1f, 1f, 1f).getRGB());
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
					}
					
					GL11.glColor4f(0f, 0.5f, 1f, 1f);
					Vec3 prev_p = null;
					for (MyPoint p: pearl_points) {
						if (prev_p == null) {
							prev_p = p.pos;
							continue;
						}
						
						Main.playerFocus.drawLineWithGL(prev_p, p.pos);
						prev_p = p.pos;
					}
				}*/
				
			}
		}
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	public static class MyPoint {
		public Vec3 pos;
		public int step_cnt;
		
		public MyPoint(Vec3 pos, int step_cnt) {
			this.pos = pos;
			this.step_cnt = step_cnt;
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
}
