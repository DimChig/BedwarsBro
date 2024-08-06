package com.dimchig.bedwarsbro;

import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.dimchig.bedwarsbro.commands.CommandFindPlayerByName;
import com.dimchig.bedwarsbro.particles.ParticleTrail;
import com.dimchig.bedwarsbro.stuff.BWItem;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler;
import com.dimchig.bedwarsbro.stuff.HintsFinder;
import com.dimchig.bedwarsbro.stuff.HintsItemTracker;
import com.dimchig.bedwarsbro.stuff.HintsPlayerScanner;
import com.dimchig.bedwarsbro.stuff.HintsValidator;
import com.dimchig.bedwarsbro.stuff.LobbyBlockPlacer;
import com.dimchig.bedwarsbro.stuff.WinEmote;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemArmourLevel;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemColor;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemToolLevel;
import com.dimchig.bedwarsbro.stuff.HintsPlayerScanner.BWPlayer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class OnMyTickEvent {
	
	private static ArrayList<Integer> myfps = new ArrayList<Integer>();
	
	public static String prevScoreboard = "";
	public static boolean isHintsRadarBaseActive = false;
	public static boolean isHintsRadarPlayersActive = false;
	public static boolean isDangerAlertActive = false;
	public static boolean isHintsItemCounterActive = false;
	public static boolean isHintsBlockCounterActive = false;
	public static boolean isBetterShopActive = false;
	public static boolean isWinEmoteActive = false;
	public static boolean isParticleTrailActive = false;	
	
	
	public static int SCANNER_FREQUENCY = 10;
	public static int SCANNER_FREQUENCY_CNT = 0;
	public static boolean FINDER_IS_SEARCH_LOOP = false;
	public static String FIND_PLAYER_COMMAND_SEARCH = "";
	public static long FIND_PLAYER_COMMAND_SEARCH_TIME = 0;
	Minecraft mc;
	private KeyBinding keyTab;
	public static GuiScreen gui2open = null;
	
	public OnMyTickEvent() {
		mc = Minecraft.getMinecraft();
		keyTab = mc.gameSettings.keyBindPlayerList;
		
		updateHintsBooleans();
	}
	
	public void updateHintsBooleans() {
		this.isHintsRadarBaseActive = HintsValidator.isRadarActive();
		this.isHintsRadarPlayersActive = HintsValidator.isHintsRadarPlayersActive();
		this.isDangerAlertActive = HintsValidator.isDangerAlertActive();
		this.isHintsItemCounterActive = HintsValidator.isItemCounterActive();
		this.isHintsBlockCounterActive = HintsValidator.isBlockCounterActive();
		this.isBetterShopActive = HintsValidator.isBetterShopActive();
		this.isWinEmoteActive = HintsValidator.isWinEmoteActive();
		this.isParticleTrailActive = HintsValidator.isParticleTrailActive();
		
		Main.guiOnScreen.setDiamonds(-1);
		Main.guiOnScreen.setEmeralds(-1);
		Main.guiOnScreen.setBlocks(-1);
		
		key_lclick = mc.gameSettings.keyBindAttack;
		key_rclick = mc.gameSettings.keyBindUseItem;
	}
	

	private boolean flag_rclick = false; 
	private boolean flag_lclick = false;
	private KeyBinding key_lclick;
	private KeyBinding key_rclick;
	
	ArrayList<BWPlayer> scanned_players = new ArrayList<BWPlayer>();
	
	public ArrayList<BWPlayer> getCurrentScannedPlayers() {
		return scanned_players;
	}
	
	public int zeroDeathHandlerRejoinVar = 0;
	
	@SubscribeEvent
	public void playerTick(TickEvent.ClientTickEvent event){
		if (mc == null) return; 
		if (mc.thePlayer == null) return; 
		
//		if (false) {
//			myfps.add(mc.getDebugFPS());
//			if (myfps.size() > 40) myfps.remove(0);
//			
//			double avg_fps = 0;
//			for (int x: myfps) avg_fps += x;
//			avg_fps /= 40;
//			//ChatSender.addText("" + (int)(avg_fps));
//		}

		String s = Main.scoreboardManager.readRawScoreboard();
		if (s != null && s.length() >= 0) {
			String current_scoreboard = ColorCodesManager.removeColorCodes(s);
			if (!current_scoreboard.equals(prevScoreboard) || mc.theWorld.rand.nextInt(20) == 0) {
				prevScoreboard = current_scoreboard;
				CustomScoreboard.updateScoreboard();
			}
		}
		
		if (gui2open != null) {
			Minecraft.getMinecraft().displayGuiScreen(gui2open);
			gui2open = null;
		}
		

		Main.shopManager.scan(isBetterShopActive);
		
		if (MyChatListener.IS_IN_GAME) {
			SCANNER_FREQUENCY_CNT++;
			if (SCANNER_FREQUENCY_CNT > SCANNER_FREQUENCY) {
				SCANNER_FREQUENCY_CNT = 0;

				scanned_players = HintsPlayerScanner.scanPlayers();
				Main.hintsBaseRadar.scan(scanned_players, isHintsRadarBaseActive, isHintsRadarPlayersActive);
				if (isDangerAlertActive) Main.dangerAlert.scan(scanned_players, mc.thePlayer);
				
				Main.generatorTimers.onTick();
				
				Main.takeMaxSlotBlocks.handle();
				
				
			}
			
			Main.zeroDeathHandler.scan();
			
			Main.bedAutoTool.handleTools();
			
			if (isHintsItemCounterActive || isHintsBlockCounterActive) Main.itemTracker.scan();
			
			
			if (FINDER_IS_SEARCH_LOOP) {
				HintsFinder.findAll(false);
			}
						
		} else {
			
			Main.guiOnScreen.setDiamonds(-1);
			Main.guiOnScreen.setEmeralds(-1);
			Main.guiOnScreen.setBlocks(-1);
		}		
		
		if (LobbyBlockPlacer.state == true && (!Main.chatListener.IS_IN_GAME || Main.shopManager.findItemInInventory("Наблюдение за") != -1)) {
			if (Main.shopManager.findItemInInventory("Выбор коман") == -1)	LobbyBlockPlacer.place();
		}
		
		if (zeroDeathHandlerRejoinVar > 0) {
			zeroDeathHandlerRejoinVar -= 1;
			ChatSender.sendText("/rejoin");
			Main.chatListener.recoverGame();
		}
		
		if (FIND_PLAYER_COMMAND_SEARCH.length() > 0) {
			long t = new Date().getTime();
			if (t - FIND_PLAYER_COMMAND_SEARCH_TIME > 30000) {
				ChatSender.addText("&cНе удалось найти &e" + FIND_PLAYER_COMMAND_SEARCH);
				Main.chatListener.playSound(Main.chatListener.SOUND_REJECT);
				FIND_PLAYER_COMMAND_SEARCH = "";
			} else {
				Main.commandFindPlayerByName.work(FIND_PLAYER_COMMAND_SEARCH);
			}
		}
		
		
		if (isWinEmoteActive) WinEmote.handleEmote();
		
		Main.freezeClutch.handle();
		
		
		if (isParticleTrailActive) Main.particleTrail.drawPlayerTrail();
		
		MyChatListener.handleBedwarsMeowMessagesQuee();
		
		if (keyTab.isKeyDown()) {
			Main.namePlateRenderer.updateGameTab();
		}
		
		//RCLICK
		if (key_rclick.isKeyDown()) {
			if (flag_rclick == true) {
				flag_rclick = false;
				//Main.autoEnderChest.onMyRightClick();
				
				if (Main.lobbyFly.speed == 1) Main.lobbyFly.speed = 0.5f;
				else if (Main.lobbyFly.speed == 0.5f) Main.lobbyFly.speed = 0.25f;
				else if (Main.lobbyFly.speed == 0.25f) Main.lobbyFly.speed = 0.25f;
				else Main.lobbyFly.speed -= 1;
			}
		} else {
			if (!flag_rclick) flag_rclick = true;
		}
		
		//LCLICK
		if (key_lclick.isKeyDown()) {
			if (flag_lclick == true) {
				flag_lclick = false;
				Main.particlesAlwaysSharpness.onMyLeftClick();
				Main.lobbyFly.speed += 1;
			}
		} else {
			if (!flag_lclick) flag_lclick = true;
		}

		
	}
	
	
	
}
