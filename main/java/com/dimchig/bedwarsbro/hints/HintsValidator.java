package com.dimchig.bedwarsbro.hints;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;

public class HintsValidator {
	public static boolean isPasswordCorrect() {
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null) return false;
		
		if (Main.isPropUserBanned(Minecraft.getMinecraft().thePlayer.getName())) return false;
		
		String mod_last_version = Main.getPropModLastVersion();		
		if (mod_last_version != null && !Main.VERSION.equals(mod_last_version)) {
			MyChatListener.sendUpdateModMessage();
			return false;
		}
					
		return true;
	}
	
	public static boolean isBedScannerActive() {
		return isPasswordCorrect();
	}

	public static boolean isBedESPActive() {
		return Main.getConfigBool(CONFIG_MSG.BED_ESP) && isPasswordCorrect();
	}
	
	public static boolean isBedAutoToolActive() {
		return Main.getConfigBool(CONFIG_MSG.BED_AUTOTOOL) && isPasswordCorrect();
	}
	
	public static boolean isRadarActive() {
		return Main.getConfigBool(CONFIG_MSG.RADAR) && isPasswordCorrect();
	}
	
	public static boolean isHintsRadarPlayersActive() {
		return Main.getConfigBool(CONFIG_MSG.RADAR_PLAYERS) && isPasswordCorrect();
	}
	
	public static boolean isRadarIconActive() {
		return Main.getConfigBool(CONFIG_MSG.RADAR_ICON) && isPasswordCorrect();
	}
	
	public static boolean isRadarChatMessage() {
		return Main.getConfigBool(CONFIG_MSG.RADAR_MESSAGES) && isPasswordCorrect();
	}
	
	public static boolean isRadarChatMessageWithColors() {
		return Main.getConfigBool(CONFIG_MSG.RADAR_MESSAGES_WITH_COLORS) && isPasswordCorrect();
	}
	
	public static boolean isItemCounterActive() {
		return Main.getConfigBool(CONFIG_MSG.ITEM_COUNTER) && isPasswordCorrect();
	}
	
	public static boolean isFinderActive() {
		//return Main.getConfigBool(CONFIG_MSG.PLAYER_FINDER) && isPasswordCorrect();
		return isPasswordCorrect();
	}
	
	public static boolean isResourceFinderActive() {
		//return Main.getConfigBool(CONFIG_MSG.RESOURCE_FINDER) && isPasswordCorrect();
		return isPasswordCorrect();
	}
	
	public static boolean isWinEmoteActive() {	
		return Main.getConfigBool(CONFIG_MSG.WIN_EMOTE) && isPasswordCorrect();
	}
	
	public static boolean isMinimapActive() {
		return Main.getConfigBool(CONFIG_MSG.MINIMAP) && isPasswordCorrect();
	}
	public static boolean isParticlesActive() {
		return Main.getConfigBool(CONFIG_MSG.CUSTOM_PARTICLES) && isPasswordCorrect();
	}
	public static boolean isPotionEffectsTrackerActive() {
		return Main.getConfigBool(CONFIG_MSG.POTION_TRACKER) && isPasswordCorrect();
	}
	public static boolean isPotionEffectsTrackerSoundsActive() {
		return Main.getConfigBool(CONFIG_MSG.POTION_TRACKER_SOUNDS) && isPasswordCorrect();
	}
	public static boolean isDangerAlertActive() {
		return Main.getConfigBool(CONFIG_MSG.DANGER_ALERT) && isPasswordCorrect();
	}

	public static boolean isAutoSprintActive() {
		return Main.getConfigBool(CONFIG_MSG.AUTO_SPRINT) && isPasswordCorrect();
	}
	
	public static boolean isAutoWaterDropActive() {
		return Main.getConfigBool(CONFIG_MSG.AUTO_WATER_DROP) && isPasswordCorrect();
	}
	/*public static boolean isAutoEnderChestActive() {
		return Main.getConfigBool(CONFIG_MSG.AUTO_ENDER_CHEST) && isPasswordCorrect();
	}*/
	public static boolean isBedwarsMeowActive() {
		return Main.getConfigBool(CONFIG_MSG.BEDWARS_MEOW) && isPasswordCorrect();
	}
	public static boolean isBedwarsMeowColorsActive() {
		return Main.getConfigBool(CONFIG_MSG.BEDWARS_MEOW_WITH_COLORS) && isPasswordCorrect();
	}
	
	public static boolean isBetterShopActive() {
		return Main.getConfigBool(CONFIG_MSG.BETTER_SHOP) && isPasswordCorrect();
	}
	public static boolean isParticleTrailActive() {
		return Main.getConfigBool(CONFIG_MSG.PARTICLE_TRAIL);
	}
	public static boolean isParticleTrailRainbowActive() {
		return Main.getConfigBool(CONFIG_MSG.PARTICLE_TRAIL_RAINBOW);
	}
	
	public static boolean isInvulnerableTimerActive() {
		return Main.getConfigBool(CONFIG_MSG.INVULNERABLE_TIMER) && isPasswordCorrect();
	}
	public static boolean isInvulnerableTimerSoundsActive() {
		return Main.getConfigBool(CONFIG_MSG.INVULNERABLE_TIMER_SOUNDS) && isPasswordCorrect();
	}
	
	public static boolean isNamePlateActive() {
		return Main.getConfigBool(CONFIG_MSG.NAMEPLATE);
	}
	public static boolean isNamePlateRainbowActive() {
		return Main.getConfigBool(CONFIG_MSG.NAMEPLATE_RAINBOW);
	}
	
	public static boolean isResourcesHologramActive() {
		return Main.getConfigBool(CONFIG_MSG.RESOURCES_HOLOGRAM);
	}
	
	public static boolean isCrosshairBlocksActive() {
		return Main.getConfigBool(CONFIG_MSG.CROSSHAIR_BLOCKS_COUNT) && isPasswordCorrect();
	}

	public static int getRainbowSpeed() {
		return Main.getConfigInt(CONFIG_MSG.RAINBOW_SPEED);
	}

	public static String getNamePlateCustomColor() {
		return Main.getConfigString(CONFIG_MSG.NAMEPLATE_RAINBOW_CONSTANT_COLOR);
	}
}
