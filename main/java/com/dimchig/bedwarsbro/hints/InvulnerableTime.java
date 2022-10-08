package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.gui.Draw3DText;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class InvulnerableTime extends Gui {
	static Minecraft mc;
	public static int INVULNERABLE_TICKS_AMOUNT = 40;
	public static long time_last_sound = -1;
	
	public InvulnerableTime() {
		mc = Minecraft.getMinecraft();
	}
	
	public static void scan(List<EntityPlayer> players, Vec3 pos, float partialTicks, boolean areSoundsActive) {
		EntityPlayerSP player = mc.thePlayer;
		
		if (players.size() < 2 || player == null) return;
		
		BWBed bed = MyChatListener.GAME_BED;
		if (bed != null) {
			double dist = player.getDistance(bed.part1_posX, bed.part1_posY, bed.part1_posZ);
			if (dist < 30) {
				return;
			}
		}
		
		for (EntityPlayer en: players) {
						
			if (en == player || en.getTeam() == player.getTeam() || en.posY > 100) continue;
			if (en.ticksExisted <= INVULNERABLE_TICKS_AMOUNT) {
				double dist = Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posZ - en.posZ, 2));
				if (dist > 15) continue;
				//draw timer
				
				//check
				if (Main.shopManager.findItemInHotbar("Наблюдение за") != -1) return;
				
				double x = en.prevPosX + (en.posX - en.prevPosX) * (double)partialTicks;
		        double y = en.prevPosY + (en.posY - en.prevPosY) * (double)partialTicks;
		        double z = en.prevPosZ + (en.posZ - en.prevPosZ) * (double)partialTicks;
		        y += en.height + 1.3;
		        
		        if (areSoundsActive) {
			        String sound_name = "note.hat";
			        long t = new Date().getTime();
			        if (t - time_last_sound > 100) {	
				        if (en.ticksExisted == 2) {
				        	mc.theWorld.playSound(x, y, z, sound_name, 0.5f, 0.6f, false);
				        	time_last_sound = t;
				        } else if (en.ticksExisted == 20) {
				        	mc.theWorld.playSound(x, y, z, sound_name, 0.75f, 1f, false);
				        	time_last_sound = t;
				        } else if (en.ticksExisted == 40) {
				        	mc.theWorld.playSound(x, y, z, sound_name, 1.0f, 1.4f, false);
				        	time_last_sound = t;
				        } 
			        }
		        }
		        
		        int ticks_cnt = INVULNERABLE_TICKS_AMOUNT - en.ticksExisted;
		        String text = new DecimalFormat("0.0").format(ticks_cnt / 20f);
				drawText(pos, new Vec3(x, y, z), player, ticks_cnt, text);	
			}
		}
	}
	
	static void drawText(Vec3 pos, Vec3 text_pos, EntityPlayerSP player, float ticks_cnt, String text) {
		
        
        float green = Math.min((INVULNERABLE_TICKS_AMOUNT - ticks_cnt) / (float)INVULNERABLE_TICKS_AMOUNT, 1f);
        Color color = new Color(1f - green, green, 0);
        
        Main.draw3DText.drawText(pos, text_pos, player, text, color.getRGB());
	}
}

