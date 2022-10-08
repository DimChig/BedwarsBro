package com.dimchig.nolegit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.hints.HintsFinder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AimHelper {
	
	//Привет челик, который декомпилировал код! Зацени че написал:
	
	static Minecraft mc;
	static double aim_speed = 0.02;
	public static boolean isActive;
	static double aim_treshold = 50;
	
	public AimHelper() {
		mc = Minecraft.getMinecraft();
	}
	
	public void toggle() {
		isActive = !isActive;		
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!isActive || !Main.chatListener.IS_IN_GAME) return;
		try {
			EntityPlayerSP player = mc.thePlayer;

	        Vec3 playerPos = getVectorWithPartialTicks(player, event.partialTicks);
	        
	        if (!isHoldingSword(player)) return;
	        
			EntityPlayer closestPlayer = getClosestPlayer(player, playerPos, event.partialTicks);
			if (closestPlayer == null) return;
	        
			//check enviroment
			boolean isEnviromentSafe = isEnviromentSafe(player, playerPos);
			if (!isEnviromentSafe) return;
			
			Vec3 closestPlayerPos = getVectorWithPartialTicks(closestPlayer, event.partialTicks);
			
			//calculate angle
			double dX = playerPos.xCoord - closestPlayerPos.xCoord;
			double dY = playerPos.yCoord - closestPlayerPos.yCoord;
			double dZ = playerPos.zCoord - closestPlayerPos.zCoord;
			double t_yaw = myMap(Math.atan2(dZ, dX), -Math.PI, Math.PI, -180, 180) + 90;
			double t_pitch = myMap((Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI), Math.PI, Math.PI*2, 90, -90);
            
            smoothRotate(player, playerPos, closestPlayer, closestPlayerPos, t_yaw, t_pitch);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isEnviromentSafe(EntityPlayerSP player, Vec3 playerPos) {
		//sneak
		if (player.isSneaking()) return false;
		
		//gui
		if (mc.currentScreen != null) return false;
		
		//if area safe
		int range = 1;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		int block_cnt = 0;
		World world = mc.theWorld;
		MutableBlockPos blockPos = new MutableBlockPos(0, 0, 0);
		
		for (int xi = -range; xi <= range; xi++) {	
			for (int zi = -range; zi <= range; zi++) {
				boolean isBlockFound = false;
				for (int yi = 0; yi >= -2; yi--) {
					blockPos.set(x + xi, y + yi, z + zi);
					IBlockState state = world.getBlockState(blockPos);
					if (state != null && state.getBlock() != Blocks.air) {
						isBlockFound = true;
						break;
					}
				}
				if (isBlockFound) block_cnt++;
			}
		}
		int must_have_blocks = (int) Math.pow(range * 2 + 1, 2);
		return must_have_blocks == block_cnt;
	}
	
	private void smoothRotate(EntityPlayerSP player, Vec3 playerPos, EntityPlayer closestPlayer, Vec3 closestPlayerPos, double target_yaw, double target_pitch) {		
		float player_yaw = (float)getRealYaw(player.rotationYaw);
		target_yaw = (float)getRealYaw(target_yaw);
		float player_pitch = player.rotationPitch;
		
		//check distance
		double dist = closestPlayerPos.distanceTo(playerPos);
		if (dist > 5) return;
		
		//check treshold
		double yaw_distance = Math.abs(player_yaw - target_yaw);
		
		if (yaw_distance > aim_treshold) return;
		
		
		//move yaw
		double diff = target_yaw > player_yaw ? 1 : -1;
		double new_yaw = player_yaw + Math.max(diff * 5, Math.abs(target_yaw - player_yaw)) * diff * aim_speed;
		double new_pitch = player_pitch;
		HintsFinder.rotateTo(Minecraft.getMinecraft().thePlayer, (float)new_yaw, (float)new_pitch);
	}
	
	private void GL_start(Vec3 playerPos) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);  
		GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(1.0f);
	}
	
	private void GL_end() {
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	private boolean isHoldingSword(EntityPlayerSP player) {
		if (player.getCurrentEquippedItem() == null) return false;
		String[] items = new String[] {"sword", "stick"};
		for (String item: items) {
			if (player.getCurrentEquippedItem().getItem().getRegistryName().contains(item)) return true;
		}
		return false;
	}
	
	private double getRealYaw(double yaw) {
		return ((yaw % 360) + 360) % 360;
	}
	
	private EntityPlayer getClosestPlayer(EntityPlayerSP mod_player, Vec3 mod_player_pos, float partialTicks) {
		List<EntityPlayer> players = mc.theWorld.getEntities(EntityPlayer.class, EntitySelectors.selectAnything);
		if (players == null || players.size() == 0) return null;
		
		double mod_player_yaw = getRealYaw(mod_player.rotationYaw);
		
		EntityPlayer closestPlayer = null;
		double dist = 1000;
		for (EntityPlayer en: players) {
			if (en == mod_player || en.getTeam() == mod_player.getTeam()) continue;
			if (Main.chatListener.getEntityTeamColor(en) == TEAM_COLOR.NONE) continue;
			//double d = en.getDistance(mod_player_pos.xCoord, mod_player_pos.yCoord, mod_player_pos.zCoord);
			
			
			Vec3 p_pos = getVectorWithPartialTicks(en, partialTicks);
			if (p_pos.distanceTo(mod_player_pos) > 5) continue;
			
			double dX = mod_player_pos.xCoord - p_pos.xCoord;
			double dY = mod_player_pos.yCoord - p_pos.yCoord;
			double dZ = mod_player_pos.zCoord - p_pos.zCoord;
			double t_yaw = getRealYaw(myMap(Math.atan2(dZ, dX), -Math.PI, Math.PI, -180, 180) + 90);
			
			double d = Math.abs(mod_player_yaw - t_yaw); 
			
			if (d < dist) {
				dist = d;
				closestPlayer = en;
			}
		}
		return closestPlayer;
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
	
	private void setLineColor(Color color) {
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1F);
	}
}
