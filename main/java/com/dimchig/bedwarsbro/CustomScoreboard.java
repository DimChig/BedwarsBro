package com.dimchig.bedwarsbro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class CustomScoreboard {
	
	private static ScoreboardManager sm;
	private static boolean isEnglishScoreboard = false;
	
	public CustomScoreboard() {
		sm = new ScoreboardManager();
		updateBooleans();
	}
	
	public void updateBooleans() {
		isEnglishScoreboard = Main.getConfigBool(CONFIG_MSG.SCOREBOARD_ENGLISH);
	}
	
	private static String[] russianTranslations = new String[] {
			"Убийств", "Финальных§f убийств", "Сломано к§fроватей"
	};
	private static String[] englishTranslations = new String[] {
			"Kills", "Finals", "Beds"
	};
	
	public static void updateScoreboard() {
		
		String subscribe = "&6Подпишись на &e&lканал&6!";
		String mod_name = "&7Мод &8▸ &cBedwars&fBro";
		String[] servers = new String[] {"MineBlaze", "DexLand", "MasedWorld"};
		
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null) return;
		String mod_player_name = Minecraft.getMinecraft().thePlayer.getName();

		boolean isAdmin = Main.isPropSelfAdmin();
		sm.readScoreboard();
		if (sm.getText("роватей") != null && sm.getText("мерт") != null) {
			//hub scoreboard
			if (isAdmin) {
				sm.replaceText(servers, subscribe);
				if (sm.getText("Донат") != null) {
					sm.replaceText(sm.getText("Донат"), " Донат: &e&lAdmin");
				}
				if (sm.getText("Ник") != null) {
					sm.replaceText(sm.getText("Ник"), " Ник: &a&l" + mod_player_name);
				}
			}
			
			sm.replaceText("Скрыть: /board", " " + mod_name);
			
			try {
				String text = sm.getText("Убийств:");
				if (text != null && text.contains(" ")) sm.replaceText(text, " Килов &7▸ &c" + text.split(" ")[1].trim());
				
				text = sm.getText("Смертей:");
				if (text != null && text.contains(": ")) sm.replaceText(text, " Смертей &7▸ &e" + text.split(": ")[1].trim());
				
				text = sm.getText("K/D:");
				if (text != null && text.contains(": ")) sm.replaceText(text, " K/D &7▸ &a" + text.split(": ")[1].trim());
				
				text = sm.getText("Игр:");
				int games_cnt = -1;
				try {
					games_cnt = Integer.parseInt(sm.getText("Игр:").split(": ")[1].trim());
				} catch (Exception ex) {}
				if (text != null && text.contains(": ")) sm.replaceText(text, " Каток &7▸ &b" + text.split(": ")[1].trim());
				
				text = sm.getText("Побед:");
				double win_rate = -1;
				String new_text = " Побед &7▸ &9" + text.split(": ")[1].trim();
				int wins_cnt = -1;
				try {
					wins_cnt = Integer.parseInt(sm.getText("Побед:").split(": ")[1].trim());
				} catch (Exception ex) {}
				if (games_cnt != -1 && wins_cnt != -1) {
					win_rate = (float)wins_cnt / (float)games_cnt;
				}
				
				if (win_rate > 0) new_text += "&7 | &fWR &7▸ &9" + (int)(win_rate * 100) + "%";
				
				if (text != null && text.contains(": ")) sm.replaceText(text, new_text);
				
				
				text = sm.getText("Слом. кроватей:");
				new_text = " Кроватей &7▸ &d" + text.split(": ")[1].trim();
				int beds_cnt = -1;
				try {
					beds_cnt = Integer.parseInt(sm.getText("Слом. кроватей:").split(": ")[1].trim());
				} catch (Exception ex) {}
				if (beds_cnt > 0 && games_cnt > 0) {
					//float a = (games_cnt / (float)beds_cnt * 10f) / 10f; 
					//new_text += " &7(&d" + a + "&7)";
				}
				if (text != null && text.contains(": ")) sm.replaceText(text, new_text);
			} catch (Exception ex) {}
			
		} else if (sm.getText("Старт через: ") != null || sm.getText("Карта: ") != null) {
			//pregame scoreboard
			if (isAdmin) {
				sm.replaceText(servers, subscribe);
			}
		} else if (sm.getText(ColorCodesManager.removeColorCodes(russianTranslations[1])) != null || sm.getText(russianTranslations[1]) != null || sm.getText(englishTranslations[1]) != null) {
			
			//game scoreboard			
			for (int i = 0; i < team_names.length; i++) {
				String team_name = team_names[i];
				String team_name_new = isEnglishScoreboard ? team_names_english[i] : team_name;
				TEAM_COLOR team_color = getTeamColorByName(team_name);
				String colorcode = "&" + getCodeByTeamColor(team_color);
				String source = sm.getText(team_name);
				if (source == null) continue;
				source = source.trim();
				String replace = team_name;
				if (source.contains(":")) replace += ":";
				else replace += colorcode + " ▸";
				
				String extraFormatting = "";	
				if (source.contains("✗")) extraFormatting = "&m";
				//if (team_color == mod_team_color) extraFormatting += "&l";
				
				sm.hardReplaceText(replace, ColorCodesManager.replaceColorCodesInString(colorcode + extraFormatting + team_name_new + colorcode + " ▸"));

			}
			
			if (isEnglishScoreboard) {
				for (int j = 0; j < russianTranslations.length; j++) {
					sm.hardReplaceText(russianTranslations[j], englishTranslations[j]);
				}
			}
			
			String text_your_team = sm.getText("(Вы)");
			if (text_your_team != null) {
				sm.hardReplaceText("(Вы)", "&8←");
			}
			
			if (isAdmin) {
				sm.replaceText(servers, subscribe);
			} else {
				sm.replaceText(servers, mod_name);
			}
			
			
		}
		
		String tab_header_text = "\n" + mod_name + " &7v&a" + Main.VERSION + " &7(&e/bwbro&7)\n";
		String tab_footer_text = "";
		if (Minecraft.getMinecraft().getCurrentServerData() != null) {
			tab_footer_text =  "\n" + "&7Ты играешь на &e" + Minecraft.getMinecraft().getCurrentServerData().serverIP + "\n";
		}
		
		Minecraft.getMinecraft().ingameGUI.getTabList().setHeader(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(tab_header_text)));
		
		Minecraft.getMinecraft().ingameGUI.getTabList().setFooter(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(tab_footer_text)));
	}
	
	
	public static enum TEAM_COLOR {
		RED,
		YELLOW,
		GREEN,
		AQUA,
		BLUE,
		PINK,
		GRAY,
		WHITE,
		NONE
	}
	
	public static enum TEAM_STATE {
		BED_ALIVE,
		BED_BROKEN,
		DESTROYED,
		NONE
	}
	
	public static class BedwarsPlayer {
		public String name;
		public BedwarsTeam team;
		
		public BedwarsPlayer(String name, BedwarsTeam team) {
			this.name = name;
			this.team = team;
		}
	}
	
	public static class BedwarsTeam {
		public TEAM_COLOR color;
		public List<BedwarsPlayer> players;
		public TEAM_STATE state;
		
		public BedwarsTeam(TEAM_COLOR color) {
			this.color = color;
			this.players = new ArrayList<BedwarsPlayer>();
			this.state = TEAM_STATE.NONE;
		}
	}
	
	public static TEAM_COLOR getTeamColorByCode(String color) {
		if (color.contains("&") || color.contains("§")) color = color.substring(1);
		if (color.equals("c")) return TEAM_COLOR.RED;
		if (color.equals("e")) return TEAM_COLOR.YELLOW;
		if (color.equals("a")) return TEAM_COLOR.GREEN;
		if (color.equals("b")) return TEAM_COLOR.AQUA;
		if (color.equals("9")) return TEAM_COLOR.BLUE;
		if (color.equals("d")) return TEAM_COLOR.PINK;
		if (color.equals("7")) return TEAM_COLOR.GRAY;
		if (color.equals("f")) return TEAM_COLOR.WHITE;
		return TEAM_COLOR.NONE;
	}
	
	public static String getTeamNameByTeamColor(TEAM_COLOR color) {
		if (color == TEAM_COLOR.RED) return team_names[0];
		if (color == TEAM_COLOR.YELLOW) return team_names[1];
		if (color == TEAM_COLOR.GREEN) return team_names[2];
		if (color == TEAM_COLOR.AQUA) return team_names[3];
		if (color == TEAM_COLOR.BLUE) return team_names[4];
		if (color == TEAM_COLOR.PINK) return team_names[5];
		if (color == TEAM_COLOR.GRAY) return team_names[6];
		if (color == TEAM_COLOR.WHITE) return team_names[7];
		return "-";
	}
	
	public static String getTeamNameSinglePlayerByTeamColor(TEAM_COLOR color) {
		if (color == TEAM_COLOR.RED) return team_names_single_player[0];
		if (color == TEAM_COLOR.YELLOW) return team_names_single_player[1];
		if (color == TEAM_COLOR.GREEN) return team_names_single_player[2];
		if (color == TEAM_COLOR.AQUA) return team_names_single_player[3];
		if (color == TEAM_COLOR.BLUE) return team_names_single_player[4];
		if (color == TEAM_COLOR.PINK) return team_names_single_player[5];
		if (color == TEAM_COLOR.GRAY) return team_names_single_player[6];
		if (color == TEAM_COLOR.WHITE) return team_names_single_player[7];
		return "-";
	}
	
	public static String[] team_names = new String[] {
			"Красные", "Желтые", "Зеленые", "Голубые", "Синие", "Розовые", "Серые", "Белые"
	};
	
	public static String[] team_names_english = new String[] {
			"Red", "Yellow", "Green", "Aqua", "Blue", "Pink", "Gray", "White"
	};
	
	public static String[] team_names_single_player = new String[] {
			"Красный", "Желтый", "Зеленый", "Голубый", "Синий", "Розовый", "Серый", "Белый"
	};
	
	public static TEAM_COLOR getTeamColorByName(String name) {
		if (name.equals(team_names[0])) return TEAM_COLOR.RED;
		if (name.equals(team_names[1])) return TEAM_COLOR.YELLOW;
		if (name.equals(team_names[2])) return TEAM_COLOR.GREEN;
		if (name.equals(team_names[3])) return TEAM_COLOR.AQUA;
		if (name.equals(team_names[4])) return TEAM_COLOR.BLUE;
		if (name.equals(team_names[5])) return TEAM_COLOR.PINK;
		if (name.equals(team_names[6])) return TEAM_COLOR.GRAY;
		if (name.equals(team_names[7])) return TEAM_COLOR.WHITE;
		return TEAM_COLOR.NONE;
	}
	
	public static String getCodeByTeamColor(TEAM_COLOR color) {
		if (color == TEAM_COLOR.RED) return "c";
		if (color == TEAM_COLOR.YELLOW) return "e";
		if (color == TEAM_COLOR.GREEN) return "a";
		if (color == TEAM_COLOR.AQUA) return "b";
		if (color == TEAM_COLOR.BLUE) return "9";
		if (color == TEAM_COLOR.PINK) return "d";
		if (color == TEAM_COLOR.GRAY) return "7";
		if (color == TEAM_COLOR.WHITE) return "f";
		return "r";
	}
	
	public static List<BedwarsTeam> readBedwarsGame() {
		ArrayList<BedwarsTeam> teams = new ArrayList<BedwarsTeam>();
		sm.readScoreboard();
		
		for (int i = 0; i < team_names.length; i++) {
			String team_russian = team_names[i];
			String team_english = team_names_english[i];
			String str = sm.getText(team_russian);
			if (str == null) str = sm.getText(team_english);
			if (str != null) {	
				BedwarsTeam team = new BedwarsTeam(getTeamColorByName(team_russian));
				try {
					String icon = "";
					if (str.contains(":")) {
						 icon = str.split(":")[1].trim().split(" ")[0].trim();
					} else if (str.contains("▸")) {
						 icon = str.split("▸")[1].trim().split(" ")[0].trim();
					}
					
					
					if (icon.codePointAt(0) == 10004) {
						team.state = TEAM_STATE.BED_ALIVE;
					} else if (icon.codePointAt(0) == 10007) {
						team.state = TEAM_STATE.DESTROYED;
					} else {
						try {
					        Integer.parseInt(icon);
					        team.state = TEAM_STATE.BED_BROKEN;
					    } catch (NumberFormatException nfe) {
					    	team.state = TEAM_STATE.NONE;
					    }
					}
					if (team.state == TEAM_STATE.NONE) continue;
					teams.add(team);
					
					//ChatSender.addText(team.color + " | " + team.state + " => " + team.players);
				} catch (Exception ex) {
					
				}
			}
		}
		
		//fill players
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null) return teams;
		if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfoMap() == null) return teams;
		Collection<NetworkPlayerInfo> players = mc.getNetHandler().getPlayerInfoMap();
		if (players == null || players.size() == 0) return teams;
		
		EntityPlayerSP mod_player = mc.thePlayer;
		
		for (NetworkPlayerInfo info: players) { 
    		if (info.getGameProfile() == null || info.getPlayerTeam() == null) continue;
    		
			String player_name = info.getGameProfile().getName();
			String team_name = info.getPlayerTeam().getRegisteredName();
			TEAM_COLOR c = Main.chatListener.getEntityTeamColorByTeam(team_name);
			BedwarsTeam team = null;
			for (BedwarsTeam t: teams) {
				if (t.color == c) {
					team = t;
					break;
				}
			}
			if (team == null) continue;
			team.players.add(new BedwarsPlayer(player_name, team));
    	}


		return teams;
	}
	
	public static void printData() {
		try {
    		List<BedwarsTeam> teams = readBedwarsGame();
    		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("teams: " + teams.size()));
    		for (BedwarsTeam t: teams) {
    			String stateColor = "&a";
    			if (t.state == TEAM_STATE.BED_BROKEN) stateColor = "&e";
    			if (t.state == TEAM_STATE.DESTROYED) stateColor = "&c";
    			String str = stateColor + t.state + " " + "&" + getCodeByTeamColor(t.color) + t.color + ":\n   &f- ";
    			for (BedwarsPlayer p: t.players) {
    				str += p.name + ", ";
    			}
    			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(str)));
    		}
    		
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
	}
}
