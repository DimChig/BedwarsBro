package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.OnMyTickEvent;
import com.dimchig.bedwarsbro.CustomScoreboard.BedwarsPlayer;
import com.dimchig.bedwarsbro.CustomScoreboard.BedwarsTeam;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.commands.CommandHintsFinderLookAtPlayer;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemArmourLevel;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemToolLevel;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemType;
import com.dimchig.bedwarsbro.hints.HintsPlayerScanner.BWPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class HintsFinder {
	static Minecraft mc;
	
	public HintsFinder() {
		mc = Minecraft.getMinecraft();
	}
	
	public static void findAll(boolean withMessageOnFail) {
		ArrayList<BWPlayer> players = HintsPlayerScanner.scanPlayers();
		if (players == null) {
			ChatSender.addText(MyChatListener.PREFIX_HINT_FINDER + "&cОшибка");
			return;
		} else if (players.size() <= 1) {
			OnMyTickEvent.FINDER_IS_SEARCH_LOOP = true;
			return;
		}
		
		String mod_player_name = ColorCodesManager.removeColorCodes(mc.thePlayer.getName());
		
		List<BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
		
		//find mod player team
		TEAM_COLOR mod_team_color = TEAM_COLOR.NONE;
		double mod_pos_x = Minecraft.getMinecraft().thePlayer.posX;
		double mod_pos_y = Minecraft.getMinecraft().thePlayer.posY;
		double mod_pos_z = Minecraft.getMinecraft().thePlayer.posZ;
		
		outerloop:
		for (BedwarsTeam team: teams) {
			for (BedwarsPlayer p: team.players) {
				if (p.name.equals(mod_player_name)) {
					mod_team_color = team.color;
					break outerloop;
				}				
			}
		}
			
		String str = "";
		int cnt_found = 0;
		BWPlayer closest_player = null;
		boolean isFirst = true;
		int min_distance = 9999;
		for (BWPlayer player: players) {
			if (player.name.equals(mod_player_name)) continue;
			
			//find player team
			boolean isTeamFound = false;
			for (BedwarsTeam team: teams) {
				for (BedwarsPlayer p: team.players) {
					if (p.name.equals(player.name)) {
						player.team_color = team.color;
						isTeamFound = true;
						break;
					}				
				}
				if (isTeamFound) break;
			}
			
			if (player.team_color == TEAM_COLOR.NONE || player.team_color == mod_team_color) continue;
			//if (player.team_color == TEAM_COLOR.NONE) continue;
			
			int dist = (int)Math.sqrt(Math.pow(mod_pos_x - player.posX, 2) + Math.pow(mod_pos_z - player.posZ, 2));
			if (dist < min_distance) {
				min_distance = dist;
				closest_player = player;
			}
			
			if (isFirst) {
				isFirst = false;
			}
			
			String stars = "";
			String player_color_code = "&" + CustomScoreboard.getCodeByTeamColor(player.team_color);
			str = MyChatListener.PREFIX_HINT_FINDER + player_color_code + "" + player.name;
			String hoverText = "&7(&f" + (int)player.posX + "&7, &f" + (int)player.posY + "&7, &f" + (int)player.posY + "&7, &c" + (int)dist + "&7)";
			
			if (player.armourLevel == BWItemArmourLevel.LEATHER) {
				hoverText += " &7Без брони";
				stars += "○";
			}
			if (player.armourLevel == BWItemArmourLevel.CHAIN) hoverText += " &7Кольчуга";
			if (player.armourLevel == BWItemArmourLevel.IRON) hoverText += " &fЖелезник";
			if (player.armourLevel == BWItemArmourLevel.DIAMOND) {
				hoverText += " &bАлмазник";
				stars += "&b&l*";
			}
			
			if (player.item_in_hand != null) {
				if (player.item_in_hand.type == BWItemType.BOW) {
					hoverText += "&8, &cЛучник";
					stars += "&c&l*";
				}
				if (player.item_in_hand.type == BWItemType.SWORD) {
					if (player.item_in_hand.toolLevel == BWItemToolLevel.WOOD) hoverText += "&8, &7Деревянный меч";
					if (player.item_in_hand.toolLevel == BWItemToolLevel.STONE) hoverText += "&8, &7Каменный меч";
					if (player.item_in_hand.toolLevel == BWItemToolLevel.IRON) hoverText += "&8, &fЖелезный меч";
					if (player.item_in_hand.toolLevel == BWItemToolLevel.DIAMOND) {
						hoverText += "&8, &bАлмазный меч";
						stars += "&6&l*";
					}
				} 
				if (player.item_in_hand.type == BWItemType.POTION_STRENGTH) {
					hoverText += "&8, &cСилка";
					stars += "&4&l*";
				}
				if (player.item_in_hand.type == BWItemType.PEARL) {
					hoverText += "&8, &aПерл";
					stars += "&9&l*";
				}
			}
			
			str += stars;
			
			EntityPlayerSP mod_player = mc.thePlayer;
			
			
			str += " " + player_color_code + getArrowDirection(player.posX, player.posZ);
			
			String commandText = "/" + CommandHintsFinderLookAtPlayer.command_text + " " + player.posX + " " + player.posY + " " + player.posZ + " " + player.name;
			//send message
			IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(str));
			IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
			HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
			ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText);
			mainComponent.getChatStyle().setChatHoverEvent(hover);
			mainComponent.getChatStyle().setChatClickEvent(click);
			Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);						
			
			cnt_found++;
		}
		if (cnt_found > 0) {
			OnMyTickEvent.FINDER_IS_SEARCH_LOOP = false;
			if (withMessageOnFail == false) {
				MyChatListener.playSound(MyChatListener.SOUND_PARTY_CHAT);
			}
		} else {
			OnMyTickEvent.FINDER_IS_SEARCH_LOOP = true;
		}
	}
	
	public static void findAndlookAtPlayer(double posX, double posY, double posZ, String p_name) {
		Entity player = Minecraft.getMinecraft().thePlayer;
		
		ArrayList<BWPlayer> players = HintsPlayerScanner.scanPlayers();
		if (players != null && players.size() > 1) {
			//find player
			for (BWPlayer p: players) {
				if (p.name.equals(p_name)) {
					lookAtPlayer(player.posX, player.posY, player.posZ, p.posX, p.posY, p.posZ);
					return;
				}
			}
		} 
		lookAtPlayer(player.posX, player.posY, player.posZ, posX, posY, posZ);
	}
	
	public static void lookAtPlayer(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x1 - x2;
		double dY = y1 - y2;
		double dZ = z1 - z2;
		float yaw = (float)Math.atan2(dZ, dX);
		float pitch = (float)(Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI);
		
		//Minecraft.getMinecraft().thePlayer.setAngles((float)yaw, (float)pitch);
		float t_yaw = myMap(yaw, (float)-Math.PI, (float)Math.PI, -180, 180);
		float t_pitch = myMap(pitch, (float)Math.PI, (float)Math.PI*2, 90, -90);
		rotateTo(Minecraft.getMinecraft().thePlayer, t_yaw + 90, t_pitch);
	}
	
	public static String getArrowDirection(double x, double z) {
		float angle_diff = (float)Math.toDegrees(Math.atan2(z - mc.thePlayer.posZ, x - mc.thePlayer.posX));
		angle_diff += (180 - mc.thePlayer.rotationYaw) % 360;
		angle_diff = (angle_diff + 90 + 360*2) % 360;
		
		int[] angles = new int[] {0, 45, 90, 135, 180, 225, 270, 315, 360};
		String[] angle_strings = new String[] {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖", "↑"};
		double min_diff = 1000;
		
		String angle_str = "-";
		for (int i = 0; i < angles.length; i++) {
			double diff = Math.abs(angles[i] - angle_diff);
			if (diff < min_diff) {
				min_diff = diff;
				angle_str = angle_strings[i];
			}
		}
		return angle_str;
	}
	
	public static float myMap(float value, float leftMin, float leftMax, float rightMin, float rightMax) {
		float leftSpan = leftMax - leftMin;
		float rightSpan = rightMax - rightMin;
		float valueScaled = (value - leftMin) / (leftSpan);
	    return rightMin + (valueScaled * rightSpan);
	}
	
	public static void rotateTo(Entity player, float target_angle_yaw, float target_angle_pitch) {
    	float prev_rot_yaw = player.rotationYaw;
    	float prev_rot_pitch = player.rotationPitch;
        
        float angle_yaw = target_angle_yaw - prev_rot_yaw;
        float angle_pitch = target_angle_pitch - prev_rot_pitch;
        
        rotateAngles(player, angle_yaw, angle_pitch);
        
        double delta_yaw = player.rotationYaw - prev_rot_yaw;
        double delta_pitch  = player.rotationPitch - prev_rot_pitch;	
        
        if (Math.abs(target_angle_pitch) > 90) return;
        
        if (target_angle_yaw != player.rotationYaw || target_angle_pitch != player.rotationPitch) {
        	 rotateTo(player, target_angle_yaw, target_angle_pitch);
        }
    }
    
    public static void rotateAngles(Entity player, float angle_yaw, float angle_pitch) {
    	 player.setAngles(angle_yaw / 0.15f, angle_pitch / -0.15f);        
    }
}
