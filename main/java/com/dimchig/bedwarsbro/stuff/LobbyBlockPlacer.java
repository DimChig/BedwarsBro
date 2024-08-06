package com.dimchig.bedwarsbro.stuff;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;

public class LobbyBlockPlacer {
	public static boolean state;
	public static int block_idx = 0;
	public static EnumDyeColor[] colors = new EnumDyeColor[] {
			EnumDyeColor.RED, EnumDyeColor.ORANGE, EnumDyeColor.YELLOW, EnumDyeColor.LIME, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.BLUE, EnumDyeColor.PURPLE, EnumDyeColor.MAGENTA, EnumDyeColor.PINK
	};
	
	public static void place() {
		if (!state) return;
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null) return;
		
		EntityPlayerSP player = mc.thePlayer;
		Random rnd = new Random();
    	BlockPos pos = new BlockPos(player.posX, player.posY - 1, player.posZ);
    	if (Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() != Blocks.air) return;
    	
    	block_idx = (block_idx + 1) % colors.length;
    	Minecraft.getMinecraft().theWorld.setBlockState(pos, Blocks.wool.getDefaultState().withProperty(Blocks.stained_glass.COLOR, colors[block_idx]));
	}
}
