package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.BedwarsPlayer;
import com.dimchig.bedwarsbro.CustomScoreboard.BedwarsTeam;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_STATE;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.hints.HintsPlayerScanner.BWPlayer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.util.BlockPos;

public class HintsBaseRadar {
	public static int RADAR_RANGE_1 = 8; //нас ломают
	public static int RADAR_RANGE_2 = 20; //на базе
	public static int RADAR_RANGE_3 = -1; //на нас идут!
	public static int basePosX = -99999;
	public static int basePosY = -99999;
	public static int basePosZ = -99999;
	
	public static ArrayList<RadarAlert> alerts;
	public static TEAM_COLOR mod_team_color = TEAM_COLOR.NONE;
	public static int RADAR_TIME_TRESHOLD = 10000;
	public static String prefix = MyChatListener.PREFIX_HINT_RADAR;
	public static boolean GAME_isBedBroken;
	
	public static List<BedwarsTeam> game_bw_teams;
	
	public void setBase(int x, int y, int z) {
		this.basePosX = x;
		this.basePosY = y;
		this.basePosZ = z;
		
		alerts = new ArrayList<RadarAlert>();
		game_bw_teams = new ArrayList<BedwarsTeam>();
		mod_team_color = TEAM_COLOR.NONE;
		GAME_isBedBroken = false;
		
		recognizeTeamColor();
		//checkBedState();
	}
	
	public static class RadarAlert {
		
		public BWPlayer player; 
		public int range_id; 
		public double posY; 
		public long time;
		public RadarAlert(BWPlayer player, double posY, int range_id) {
			this.player = player;
			this.range_id = range_id;
			this.posY = posY;
			this.time = new Date().getTime();
		}
		
		public long getTimePassed() {
			return new Date().getTime() - this.time;
		}
	}
	
	public static void recognizeTeamColor() {
		List<BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
		game_bw_teams = teams; 
		mod_team_color = MyChatListener.getEntityTeamColor(Minecraft.getMinecraft().thePlayer);
		if (mod_team_color == TEAM_COLOR.NONE) {
			ChatSender.addText(prefix + "&cНе удалось установить цвет твоей команды!");
		}
	}
	
	public static void checkBedState() {
		if (GAME_isBedBroken == true) return;
		List<BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
		for (BedwarsTeam team: teams) {
			if (team.color == mod_team_color) {
				if (team.state != TEAM_STATE.BED_ALIVE) {
					GAME_isBedBroken = true;
					ChatSender.addText(prefix + "&cКровать сломана!" + (Main.getConfigBool(CONFIG_MSG.RADAR_PLAYERS) ? " Радар переключен в режим &lигроков&c!" : ""));
					return;
				}
			}
		}
	}
	
	public static void scan(ArrayList<BWPlayer> players, boolean isRadarBaseActive, boolean isPlayerRadarActive) {
		if (basePosX == -99999 || alerts == null) return;
		
		
		boolean isBedAlert = true;
		if (GAME_isBedBroken == true) isBedAlert = false;
		
		if (isBedAlert && !isRadarBaseActive) return;
		if (!isBedAlert && !isPlayerRadarActive) return;
		
		if (mod_team_color == TEAM_COLOR.NONE) {
			if (Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 50 == 0) {
				recognizeTeamColor();
			}
			return;
		}
		
		if (new Date().getTime() - MyChatListener.GAME_start_time < 3000) return;
		
		/*if (game_bw_teams == null) {}*/
		
		if (isBedAlert && Minecraft.getMinecraft().theWorld.rand.nextInt(5) == 0) checkBedState();
		
		//game_bw_teams = new ArrayList<CustomScoreboard.BedwarsTeam>();
		
		for (BWPlayer player: players) {
			//if (!player.name.equals("DevMe")) continue;
			int player_cnt = 1;
			
			//count if 2 or more players rushing us
			for (RadarAlert alert: alerts) {
				if (alert.player.team_color == player.team_color && player.distToPlayer <= alert.player.distToPlayer && !player.name.equals(alert.player.name)) {
					player_cnt++;
				}
			}
			
			int dist = 99999;
			if (isBedAlert) {			
				dist = (int) Math.sqrt(Math.pow(player.posX - basePosX, 2) + Math.pow(player.posZ - basePosZ, 2));
			} else {
				dist = (int) Math.sqrt(Math.pow(player.posX - Minecraft.getMinecraft().thePlayer.posX, 2) + Math.pow(player.posZ - Minecraft.getMinecraft().thePlayer.posZ, 2));
			}
			
			
			if (Minecraft.getMinecraft().thePlayer == null) return;
			double posY = isBedAlert ? (player.posY - basePosY) : (player.posY - Minecraft.getMinecraft().thePlayer.posY);
			
			if (dist < RADAR_RANGE_1) {
				alertPlayerRange(player, posY, 1, player_cnt, isBedAlert);
			} else if (dist < RADAR_RANGE_2){
				alertPlayerRange(player, posY, 2, player_cnt, isBedAlert);
			}
		}
	}
	
	public static void alertPlayerRange(BWPlayer player, double posY, int range_id, int count, boolean isBedAlert) {
		//check existing
		//RADAR_TIME_TRESHOLD = 10000;
		if (Main.shopManager.findItemInHotbar("Наблюдение за") != -1) return;
		
		if (player.team_color == TEAM_COLOR.NONE || player.team_color == mod_team_color) return;
		
		if (basePosY - player.posY >= 5) return; //player in void
		
		for (int i = 0; i < alerts.size(); i++) {
			if (i < 0 || i >= alerts.size()) break;
			
			RadarAlert alert = alerts.get(i);
			
			if (alert.getTimePassed() > RADAR_TIME_TRESHOLD) {
				alerts.remove(alert);
				i = 0;
				continue;
			} 
			if (alert.player.name.equals(player.name)) {
				if (alert.range_id <= range_id) return;	
				alerts.remove(alert);
				i--;
			}
		}
		RadarAlert radarAlert = new RadarAlert(player, posY, range_id);
		alerts.add(radarAlert);
		
		String str = "";
		String team_color = CustomScoreboard.getCodeByTeamColor(player.team_color);
		String team_name_1_person = CustomScoreboard.getTeamNameSinglePlayerByTeamColor(player.team_color);
		
		double height_diff = posY;
		
		if (range_id == 1) {
			//check if on top of base
			
			if (height_diff > 5) {
				str += isBedAlert ? "&cСверху базы" : "&cСверху";
			} else {
				str += isBedAlert ? "&cНас ломает" : "&cРядом";
			}
			MyChatListener.playSound(MyChatListener.SOUND_RADAR_CLOSE);
			Main.guiRadarIcon.show(isBedAlert);
		} else if (range_id == 2) {
			if (height_diff > 7) {
				str += isBedAlert ? "&eНас сверху рашит" : "&eСверху";
			} else {
				str += isBedAlert ? "&eНас рашит" : "&eРядом";
			}	
			MyChatListener.playSound(MyChatListener.SOUND_RADAR_FAR);
		}
		
		str += "&" + team_color;
		if (count > 1) {
			str += " " + count + "-й";
		}
		
		str += " " + team_name_1_person;
		
		
		if (HintsValidator.isRadarChatMessage() && isBedAlert) {
			String s = str;
			final String str_final = str;
			
			if (!HintsValidator.isRadarChatMessageWithColors()) s = ColorCodesManager.removeColorCodes(s);
			
			Minecraft.getMinecraft().thePlayer.sendChatMessage(s);
		}
		ChatSender.addText(prefix + str);		
	}
	
	public static void visualizeZone() {
		//for
		drawZone(8, EnumDyeColor.RED); 
		drawZone(20, EnumDyeColor.YELLOW); 
		drawZone(35, EnumDyeColor.GREEN); 
	}
	
	public static void drawZone(int range, EnumDyeColor block_color) {
		//for
		for (int yi = 2; yi <= 2; yi++) {
			for (int xi = -range * 2; xi < range * 2; xi++) {
				for (int zi = -range*2; zi < range * 2; zi++) {
					int bx = basePosX + xi;
					int by = basePosY + yi;
					int bz = basePosZ + zi;
					
					int dist = (int)Math.sqrt(Math.pow(bx - basePosX, 2) + Math.pow(bz - basePosZ, 2));
					if (dist == range) {
						Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(bx, by, bz)).getBlock();
						if (block.getLocalizedName().contains("air")) {
							Minecraft.getMinecraft().theWorld.setBlockState(new BlockPos(bx, by, bz), Blocks.stained_glass.getDefaultState().withProperty(Blocks.stained_glass.COLOR, block_color));
						}
					}
				}
			}
		}
	}
}
