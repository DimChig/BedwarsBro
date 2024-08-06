package com.dimchig.bedwarsbro.stuff;

import java.util.ArrayList;
import java.util.Collection;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.MyChatListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NamePlateRenderer {
	
	static Minecraft mc;
	
	static boolean isCrossedNicknamesActive = true;
	
	public NamePlateRenderer() {
		mc = Minecraft.getMinecraft();
	}
	
	public void updateBooleans() {
		isCrossedNicknamesActive = Main.getConfigBool(CONFIG_MSG.BETTER_TAB_CROSSED_NICKNAMES);	
	}
	
	@SubscribeEvent
	public void onMyRender(PlayerEvent.NameFormat event) {
		String color = "&f";
		TEAM_COLOR team_color = MyChatListener.getEntityTeamColor(event.entityPlayer);
		if (team_color != TEAM_COLOR.NONE) color = "&" + CustomScoreboard.getCodeByTeamColor(team_color);
		
		String prefix = getPrefixByName(event.username);
		
		String display_name = event.displayname;
		display_name = ChatSender.parseText(display_name);
		if (prefix.length() > 0 && display_name.length() > 2 && !Main.chatListener.IS_IN_GAME) {
			color = "";
		}
		event.displayname = ColorCodesManager.replaceColorCodesInString(prefix + color + display_name);
		
		updateGameTab();
	}
	
	public static ArrayList<String> friends = new ArrayList<String>();
	public void updateGameTab() {
		//game tab
		
		if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfoMap() == null) return;
		Collection<NetworkPlayerInfo> players = mc.getNetHandler().getPlayerInfoMap();
		int cnt = 0;
		boolean isInGame = Main.chatListener.IS_IN_GAME;
    	for (NetworkPlayerInfo info: players) { 
    		cnt++;
    		if (cnt > 500) return;
    		
    		if (info.getGameProfile() == null || info.getPlayerTeam() == null) continue;
    		
    		
			String player_name = info.getGameProfile().getName();
			String player_prefix = getPrefixByName(player_name);
			
			String color_code = "&7";
			String donation = "";
			boolean hasFlag = Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(info).contains("⚑") || info.getPlayerTeam().getColorPrefix().contains("⚑");
			
			if (hasFlag) {
				
			}
			if (isInGame || hasFlag) {
				color_code = "&7";
				if (isCrossedNicknamesActive) color_code += "&m";
				if (info.getPlayerTeam() == null) continue;
				String team_name = info.getPlayerTeam().getRegisteredName();
				TEAM_COLOR c = Main.chatListener.getEntityTeamColorByTeam(team_name);
				
				//if (c != TEAM_COLOR.NONE) color_code = "&" + CustomScoreboard.getCodeByTeamColor(team_color);
				if (c != TEAM_COLOR.NONE) color_code = "&" + CustomScoreboard.getCodeByTeamColor(c);
			} else {
			
			//new_name = player_prefix + color_code + new_name;
				donation = info.getPlayerTeam().getColorPrefix().trim();
				if (donation.length() > 4) {
					donation = donation.replace("§0", "&f");
					color_code = donation.substring(donation.length() - 2, donation.length());
					donation += " ";
				}
				else donation = "";
			}
			
			if (player_prefix.length() > 0 && !Main.chatListener.IS_IN_GAME) color_code = "";
			
			String new_name = player_prefix + donation + color_code + player_name;
			//new_name = color_code + player_name;
			new_name = ChatSender.parseText(new_name);
			
			if (friends.contains(player_name)) new_name = Main.chatListener.PREFIX_FRIEND_IN_CHAT + new_name;
			
			Main.chatListener.PREFIX_FRIEND_IN_CHAT = "&c&l<&6&lД&e&lр&a&lу&b&lг&d&l> &r";
			
			
			info.setDisplayName(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(new_name)));
    	}
	}
	
	public void printSameUsersInGame() {
		if (mc == null || mc.thePlayer == null) return;
		if (Main.getPropAuthorPrefix().equals("none")) return;
		if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfoMap() == null) return;
		Collection<NetworkPlayerInfo> players = mc.getNetHandler().getPlayerInfoMap();
		if (players == null || players.size() == 0) return;
		
		EntityPlayerSP mod_player = mc.thePlayer;
		
		int cnt = 0;
		ArrayList<String> arr = new ArrayList<String>();
		for (NetworkPlayerInfo info: players) { 
    		if (info.getGameProfile() == null || info.getPlayerTeam() == null) continue;
    		
			String player_name = info.getGameProfile().getName();
			String player_prefix = getPrefixByName(player_name);
			//find bro
			
			if (player_prefix.length() == 0 || mod_player.getName().equals(player_name)) {
				continue;
			}
			
			String team_name = info.getPlayerTeam().getRegisteredName();
			TEAM_COLOR c = Main.chatListener.getEntityTeamColorByTeam(team_name);
			
			String color_code = "&f";
			if (c != TEAM_COLOR.NONE) color_code = "&" + CustomScoreboard.getCodeByTeamColor(c);
			
			cnt++;
			arr.add(color_code + ColorCodesManager.removeColorCodes(player_name));
    	}
		
		if (cnt <= 0 || arr.size() == 0) return;
    	String text = MyChatListener.PREFIX_BEDWARSBRO + "С тобой в катке играет &aDimChig &fпод ником \"" + arr.get(0) + "&f\"! Это автор и создатель мода &r&cBedwars&fBro!";
    	ChatSender.addText(text);
	}
	
	public String getPrefixByName(String player_name) {
		if (!Main.isPropUserAdmin(player_name)) return "";
		//return "&c&l[&6&lA&e&lD&a&lM&b&lI&d&lN&c&l]&r ";
		String prefix = Main.getPropAuthorPrefix();
		if (prefix.equals("none")) return "";
		if (prefix == null || prefix.length() == 0) return "&c&l[&6&lС&e&lо&a&lз&b&lд&d&lа&c&lт&6&lе&e&lл&a&lь&c&l]&r ";
		return prefix;
	}
}
