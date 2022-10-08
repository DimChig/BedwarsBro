package com.dimchig.bedwarsbro.hints;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TakeMaxSlotBlocks {
	static Minecraft mc;
	public boolean isActive;
	
	public TakeMaxSlotBlocks() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
	}
	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.TAKE_BLOCKS_FROM_MAX_SLOT);
	}
	
	public void handle() {		
		if (!isActive) return;		
		if (mc.thePlayer == null) return;
		if (!Main.chatListener.IS_IN_GAME) return;
		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
		if (is == null || is.getItem() != Item.getItemFromBlock(Blocks.wool)) return;
		
		
		int select_type = 0;
		//0 - select max to the right
		//1 - select max > treshold (16)
		//2 - select max stack

		int treshold = 16;
		if (is.stackSize <= treshold) select_type = 1;
		if (is.stackSize <= 4) select_type = 2;
		//find optimal slot
		int max_slot_idx = -1;
		int max_stack_size = -1;
		int current_slot = mc.thePlayer.inventory.currentItem;
		for (int i = 0; i < mc.thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			if (item.getUnlocalizedName().contains("tile.cloth")) {
				if (select_type == 0 && i >= current_slot && stack.stackSize > treshold) {
					max_stack_size = stack.stackSize;
					max_slot_idx = i;
				} else if (select_type == 1 && stack.stackSize > treshold && i != mc.thePlayer.inventory.currentItem) {
					max_slot_idx = i;
				} else if (select_type == 2 && stack.stackSize > max_stack_size) {
					max_stack_size = stack.stackSize;
					max_slot_idx = i;
				}
			}
		}
		
		if (max_slot_idx == -1 || max_slot_idx == current_slot) return;
		mc.thePlayer.inventory.currentItem = max_slot_idx;
		//ChatSender.addText("Select &a" + max_slot_idx + " &f type &e" + select_type);
		//Main.chatListener.playSound(Main.chatListener.SOUND_TEAM_CHOSEN);
	}
}
