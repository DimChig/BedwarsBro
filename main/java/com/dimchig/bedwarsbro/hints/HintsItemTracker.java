package com.dimchig.bedwarsbro.hints;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.gui.GuiOnScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class HintsItemTracker {
	
	public void scan() {
		ItemStack[] items = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
		
		int cnt_emeralds = 0;
		int cnt_diamonds = 0;
		
		for (ItemStack item: items) {
			if (item != null) {
				
				if (item.getUnlocalizedName().equals("item.diamond")) cnt_diamonds += item.stackSize;
				if (item.getUnlocalizedName().equals("item.emerald")) cnt_emeralds += item.stackSize;
			}
		}
		Main.guiOnScreen.setDiamonds(cnt_diamonds);
		Main.guiOnScreen.setEmeralds(cnt_emeralds);
	}
}
