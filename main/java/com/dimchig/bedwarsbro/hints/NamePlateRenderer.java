package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.Collection;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
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
	
	public NamePlateRenderer() {
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onMyRender(PlayerEvent.NameFormat event) {
		String color = "&f";
		TEAM_COLOR team_color = MyChatListener.getEntityTeamColor(event.entityPlayer);
		if (team_color != TEAM_COLOR.NONE) color = "&" + CustomScoreboard.getCodeByTeamColor(team_color);
		
		String prefix = getPrefixByName(event.username);
		
		event.displayname = ColorCodesManager.replaceColorCodesInString(prefix + color + event.displayname);
		
		updateGameTab();
	}
	
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
				color_code = "&7&m";
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
			
			String new_name = player_prefix + donation + color_code + player_name;
			//new_name = color_code + player_name;
			
			
			
			info.setDisplayName(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(new_name)));
    	}
	}
	
	public void printSameUsersInGame() {
		if (mc == null || mc.thePlayer == null) return;
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
    	String text = MyChatListener.PREFIX_BEDWARSBRO + "Сейчас в катке с модом игра" + MyChatListener.getNumberEnding(cnt, "ет", "ет", "ют") + " &a&l" + cnt + " &fигрок" + MyChatListener.getNumberEnding(cnt, "", "а", "ов") + "!";
    	String hover = "";
		for (String s: arr) {
			hover += "&8• &r" + s + "\n";
		}
		if (hover.length() > 2) hover = hover.substring(0, hover.length() - 1);		
    	ChatSender.addHoverText(text, hover);
	}
	
	public String getPrefixByName(String player_name) {
		if (!Main.isPropUserAdmin(player_name)) return "";
		//return "&c&l[&6&lA&e&lD&a&lM&b&lI&d&lN&c&l]&r ";
		return "&c&l[&6&lС&e&lо&a&lз&b&lд&d&lа&c&lт&6&lе&e&lл&a&lь&c&l]&r ";
	}
}
