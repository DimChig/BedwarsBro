package com.dimchig.bedwarsbro.stuff;

import java.awt.MouseInfo;

import org.lwjgl.input.Mouse;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class BedAutoTool {
	static Minecraft mc;
	public boolean isActive = false;
	
	public BedAutoTool() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
	}
	
	public void updateBooleans() {
		isActive = HintsValidator.isBedAutoToolActive();
	}
	
	public void handleTools() {
		if (!isActive) return;
		if (!Mouse.isButtonDown(0)) return;
		MovingObjectPosition obj = mc.objectMouseOver;
		if (obj == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
		IBlockState state = mc.theWorld.getBlockState(obj.getBlockPos());
		if (state == null || state.getBlock() == null) return;
		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
		if (is == null || is.getItem() == null) return;
		String name = is.getItem().getUnlocalizedName();
		if (!(name.contains("pickaxe") || name.contains("hatchet") || name.contains("shears"))) return;
		
		double min_dist = 99999;		
		for (MyBed bed: Main.minimap.bedsFound) {
			double dist = Math.sqrt(Math.pow(mc.thePlayer.posX - bed.pos.getX(), 2) + Math.pow(mc.thePlayer.posZ - bed.pos.getZ(), 2));
			if (dist < min_dist) min_dist = dist;
		}

		if (min_dist > 20) return;
		
		Block block = state.getBlock();
		
		
		Block[] available_blocks = new Block[] {
				Blocks.wool,
				Blocks.planks,
				Blocks.ladder,
				Blocks.stained_hardened_clay,				
				Blocks.end_stone,				
				Blocks.obsidian
		};
		
		boolean isFound = false;
		for (Block b: available_blocks) {
			if (b == block) {
				isFound = true;
				break;
			}
		}
		if (!isFound) return;
		
		int pickaxe_slot = -1;
		int axe_slot = -1;
		int shears_slot = -1;
		
		for (int i = 0; i < mc.thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			if (item.getUnlocalizedName().contains("pickaxe")) pickaxe_slot = i;
			if (item.getUnlocalizedName().contains("hatchet")) axe_slot = i;
			if (item.getUnlocalizedName().contains("shears")) shears_slot = i;
		}		
		int slot = -1;
		if (shears_slot != -1 && (block == Blocks.wool)) slot = shears_slot;
		else if (pickaxe_slot != -1 && (block == Blocks.end_stone || block == Blocks.obsidian || block == Blocks.stained_hardened_clay)) slot = pickaxe_slot;
		else if (axe_slot != -1 && (block == Blocks.planks || block == Blocks.ladder)) slot = axe_slot;
		if (slot == -1) return;
		//ChatSender.addText(pickaxe_slot);
		mc.thePlayer.inventory.currentItem = slot;
		
		//ChatSender.addText("" + block);
	}
}
