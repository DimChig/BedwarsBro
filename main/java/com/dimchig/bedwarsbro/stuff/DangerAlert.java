package com.dimchig.bedwarsbro.stuff;

import java.util.ArrayList;
import java.util.Date;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.gui.GuiPlayerFocus;
import com.dimchig.bedwarsbro.particles.ParticleController;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemType;
import com.dimchig.bedwarsbro.stuff.HintsPlayerScanner.BWPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class DangerAlert {
	private static int danger_zone_radius = 5;
	private static int max_ray_distance = 100;
	private static long prev_sound_time = 0;
	private static long sound_freq = 150;
	private static long prev_message_time = 0;
	private static long message_freq = 3000;
	
	public void scan(ArrayList<BWPlayer> players, EntityPlayerSP mod_player) {
		long t = new Date().getTime();
		World world = Minecraft.getMinecraft().theWorld;
		Main.playerFocus.clearLines();
		for (BWPlayer p: players) {
			if (p.en.getName().equals(mod_player.getName())) continue;
			if (mod_player.getTeam() == p.en.getTeam()) continue;
			if (p.item_in_hand == null) continue;
			if (p.item_in_hand.type == BWItemType.BOW || p.item_in_hand.type == BWItemType.FIREBALL) {
				//trace 
				MovingObjectPosition ray = null;
				for (int i = 1; i < max_ray_distance; i++) {
					ray = p.en.rayTrace(i, 1.0f);
			    	if (ray == null) continue;
		    		boolean isInDanger = isPlayerInDangerZone(mod_player, ray.hitVec.xCoord, ray.hitVec.yCoord, ray.hitVec.zCoord);
		    		if (isInDanger) {
		    			
		    			if (GuiPlayerFocus.STATE == true) {
		    				Vec3 p1 = null;
		    				Vec3 p2 = new Vec3(p.en.posX, p.en.posY + p.en.eyeHeight, p.en.posZ);
		    				
		    				Main.playerFocus.addLine(p1, p2, Main.playerFocus.getColorByTeam(Main.chatListener.getEntityTeamColor(p.en)));
		    			}
		    			
		    			//
		    			if (t - prev_sound_time > sound_freq && Main.getConfigBool(CONFIG_MSG.DANGER_ALERT_SOUND)) {
		    				prev_sound_time = t;
		    				float volume = mod_player.getDistanceToEntity(p.en) / 12f;
		    				//note.hat - less agressive
		    				world.playSound(p.en.posX, p.en.posY + p.en.eyeHeight, p.en.posZ, "note.pling", volume, 1.0f, false);
		    			}
		    			if (t - prev_message_time > message_freq) {
		    				prev_message_time = t;
		    				if (p.item_in_hand.type == BWItemType.BOW) { 
		    					ChatSender.addText(MyChatListener.PREFIX_DANGER_ALERT + "&fНа тебя целятся из &cЛУКА");
		    				} else if (p.item_in_hand.type == BWItemType.FIREBALL) {
		    					ChatSender.addText(MyChatListener.PREFIX_DANGER_ALERT + "&fНа тебя целятся &6ФАЕРБОЛОМ");
		    				}
		    			}
		    			break;
		    		}
				}
	    		
			}
		}
	}
	
	boolean isPlayerInDangerZone(EntityPlayerSP mod_player, double x, double y, double z) {
		double dist = Math.sqrt(Math.pow(mod_player.posX - x, 2) + Math.pow(mod_player.posY + mod_player.getEyeHeight() - y, 2) + Math.pow(mod_player.posZ - z, 2));
		if (dist < danger_zone_radius) return true;
		return false;
	}
}
