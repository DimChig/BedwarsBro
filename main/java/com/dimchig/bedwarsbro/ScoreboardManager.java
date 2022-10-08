package com.dimchig.bedwarsbro;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardManager {
	
	public static List<MyScoreboardLine> lines = new ArrayList<MyScoreboardLine>();
	
	public static class MyScoreboardLine {
		public ScorePlayerTeam team;
		String value;
		
		public MyScoreboardLine(ScorePlayerTeam team, String value) {
			this.team = team;
			this.value = value;
		}
		
		public void setText(String text) {
			String new_val = text.replace("&", "§");
			this.team.setNamePrefix(new_val);
			this.team.setNameSuffix("");
			this.value = new_val;
		}
	}
	
	public static String readRawScoreboard() {
		String s = "";
		try {        
			Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        	
            for (ScorePlayerTeam team : scoreboard.getTeams()) {
            	s += (team.getColorPrefix() + team.getColorSuffix()).trim();
            }
            return s;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
	}
	
	public static void readScoreboard() {
		try {        
			lines = new ArrayList<MyScoreboardLine>();
			
        	Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        	
            for (ScorePlayerTeam team : scoreboard.getTeams()) {
            	String s = ColorCodesManager.removeColorCodes(team.getColorPrefix() + team.getColorSuffix());
            	if (s.length() > 1) {
            		lines.add(new MyScoreboardLine(team, s));
            		//FileManager.writeToFile(team.getColorPrefix() + team.getColorSuffix(), "tempfile");
            	}
            }
            
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
	}
	
	public static void hardReplaceText(String from, String to) {
		hardReplaceText(from, from, to);
	}
	
	public static void hardReplaceText(String findby, String from, String to) {
		readScoreboard();
		ArrayList<MyScoreboardLine> lines2 = new ArrayList<MyScoreboardLine>();
		lines2.addAll(lines);
		for (MyScoreboardLine l: lines2) {
			String prefix = l.team.getColorPrefix();
			String suffix = l.team.getColorSuffix(); 
			String new_text = (prefix + suffix).replace("&", "§").replace("§r", "").trim();
			l.team.setNameSuffix(new_text);
			l.team.setNamePrefix("");
			
			if (new_text.contains(findby)) {
				l.team.setNameSuffix(l.team.getColorSuffix().replace(from, ColorCodesManager.replaceColorCodesInString(to)));
			}
			l.value = ColorCodesManager.removeColorCodes(new_text);
		}
	}
	
	public static void replaceText(String from, String to) {
		readScoreboard();
		
		for (MyScoreboardLine l: lines) {
			if (l.value.contains(from)) {
				l.setText(to);
				break;
			}
		}
	}
	
	public static void replaceTextPartly(String from, String to) {
		readScoreboard();
		
		for (MyScoreboardLine l: lines) {
			if (l.value.contains(from)) {
				l.setText(l.value.replace(from, to));
				break;
			}
		}
	}
	
	public static void replaceText(String[] from_array, String to) {
		for (String s: from_array) {
			replaceText(s, to);
		}
	}
	
	public static String getText(String ref) {
		readScoreboard();
		
		for (MyScoreboardLine l: lines) {
			if (l == null || l.value == null) continue;
			if (l.value.contains(ref)) {
				return l.value;
			}
		}
		return null;
	}
	
	public static String getRawText(String ref) {
		readScoreboard();
		
		for (MyScoreboardLine l: lines) {
			if (l.value.contains(ref)) {
				return (l.team.getColorPrefix() + l.team.getColorSuffix()).trim();
			}
		}
		return null;
	}

}
