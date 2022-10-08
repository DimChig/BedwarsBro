package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.particles.ParticleController;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WinEmote {
	
	public static int emoteStage = -1;
	public static int all_positions_idx = -1;
	public static int maxEmoteBlocksPerTick = 10000;
	public static int targetRange = 120;
	public static int currentRange = -1;
	public static BlockPos startingPos = null;
	public static long startingTime = 0;
	public static ArrayList<BlockPos> all_positions = new ArrayList<BlockPos>();
	public static TEAM_COLOR emoteStage_team_color;
	
	public static void handleEmote() {
		
		if (!MyChatListener.IS_IN_GAME) {
			emoteStage = -1;
			all_positions.clear();
			return;
		}
		
		if (emoteStage > 0 && startingPos != null) {
			//ChatSender.addText("&b" + all_positions.size());
			if (new Date().getTime() - startingTime > 15 * 1000) {
				emoteStage = -1;
				return;
			}
			if (all_positions_idx >= all_positions.size()) {
				if (currentRange == targetRange) {
					emoteStage = -1;
					return;
				} else {
					fillRange(currentRange);
					currentRange++;
				}
			}
					
			ArrayList<IBlockState> states = getStates(emoteStage_team_color);
			Random rnd = new Random();
			
			World world = Minecraft.getMinecraft().theWorld;
			
			BlockPos pos = null;
			int cnt = 0;
			while (true) {
				cnt++;
				all_positions_idx++;
				if (cnt > maxEmoteBlocksPerTick || all_positions_idx >= all_positions.size()) break;
				pos = all_positions.get(all_positions_idx);
				
				if (world.getBlockState(pos).getBlock() != Blocks.air) {
					//replace on block
					cnt++;
					//
					//ChatSender.addText("&b" + pos);
					//all_positions = new ArrayList<BlockPos>();
					world.setBlockState(pos, states.get(rnd.nextInt(states.size())));
					//ChatSender.addText("" + world.getBlockState(pos).getBlock().getAmbientOcclusionLightValue());
				}
				//all_positions.remove(pos);
			}
			
			//ChatSender.addText("cnt = " + cnt);
		}
	}
	
	public static void fillRange(int range) {
		BlockPos pos = new BlockPos(0, 0, 0);
		for (int xi = -range; xi <= range; xi++) {
			for (int zi = -range; zi <= range; zi++) {
				
				int dist = Math.abs(xi);
				if (Math.abs(xi) < Math.abs(zi)) dist = Math.abs(zi);
				if (dist != range) continue;
				
				for (int yi = 0; yi <= 120; yi++) {
					
					pos = new BlockPos(startingPos.getX() + xi, yi, startingPos.getZ() + zi);
					
					all_positions.add(pos);
				}
			}
			
		}
	}
	
	public static void changeWorldBlocks(TEAM_COLOR team_color) {
		Entity player = Minecraft.getMinecraft().thePlayer;
		emoteStage = 1;
		emoteStage_team_color = team_color;
		all_positions = new ArrayList<BlockPos>();
		all_positions_idx = -1;
		currentRange = 0;
		startingTime = new Date().getTime();
		startingPos = new BlockPos(player);
		
		
		for (int i = 0; i < 10; i++) {
			ParticleController.spawnFinalKillParticles(player.posX, player.posY + player.getEyeHeight()/2, player.posZ, team_color);
		}		
	}
	
	public static ArrayList<IBlockState> getStates(TEAM_COLOR team_color) {
		ArrayList<IBlockState> states = new ArrayList<IBlockState>();
		if (team_color == TEAM_COLOR.RED) {
			states.add(Blocks.redstone_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.RED));
			states.add(Blocks.stained_hardened_clay.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.RED));
		} else if (team_color == TEAM_COLOR.YELLOW) {
			states.add(Blocks.gold_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.YELLOW));
		} else if (team_color == TEAM_COLOR.GREEN) {
			states.add(Blocks.emerald_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.LIME));
		} else if (team_color == TEAM_COLOR.AQUA) {
			states.add(Blocks.diamond_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.LIGHT_BLUE));
		} else if (team_color == TEAM_COLOR.BLUE) {
			states.add(Blocks.lapis_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.BLUE));
		} else if (team_color == TEAM_COLOR.PINK) {
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.PINK));
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.MAGENTA));
		} else if (team_color == TEAM_COLOR.GRAY) {
			states.add(Blocks.stone.getDefaultState());
			states.add(Blocks.cobblestone.getDefaultState());
			states.add(Blocks.stonebrick.getDefaultState());
		} else if (team_color == TEAM_COLOR.WHITE) {
			states.add(Blocks.iron_block.getDefaultState());
			states.add(Blocks.quartz_block.getDefaultState());
			states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.WHITE));
		}
		
		//states.add(Blocks.diamond_block.getDefaultState());
		//states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.LIGHT_BLUE));
		//states.add(Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.WHITE));
		//states.add(Blocks.stained_hardened_clay.getDefaultState().withProperty(Blocks.stained_glass.COLOR, EnumDyeColor.LIGHT_BLUE));
		//states.add(Blocks.ice.getDefaultState());
		//states.add(Blocks.diamond_ore.getDefaultState());
		
		return states;
	}
}
