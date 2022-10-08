package com.dimchig.bedwarsbro.gui;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.gui.GuiMinimap.PosItem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class GuiResourceHologram {
	static Minecraft mc;
	
	public GuiResourceHologram() {
		mc = Minecraft.getMinecraft();
	}
	
	private int[] last_item_cnt = new int[] {0, 0, 0, 0};
	private int[] last_item_time_counter = new int[] {0, 0, 0, 0};
	
	public void draw(Vec3 playerPos) {
		List<Entity> items = mc.theWorld.loadedEntityList;
		
		ArrayList<PosItem> my_items = new ArrayList<PosItem>();
		int total_iron = 0;
		int cnt_iron = 0;
		for (Entity en: items) {
			//GROUP
			if (en instanceof EntityItem) {
				EntityItem itemEntity = (EntityItem) en;
				Item item = itemEntity.getEntityItem().getItem();
				if (item == null) continue;
				
				int item_type = -1;
				if (item == Items.emerald) {	
					item_type = 0;
				} else if (item == Items.diamond) {
					item_type = 1;
				} else if (item == Items.gold_ingot) {
					item_type = 2;
				} else if (item == Items.iron_ingot) {
					item_type = 3;
				}
				int cnt = itemEntity.getEntityItem().stackSize;
				
				if (en.isDead) continue;

				//find similar
				boolean isFound = false;
				for (PosItem p: my_items) {
					if (p.type != item_type) continue;
					double dist = Math.sqrt(Math.pow(p.x - en.posX, 2) + Math.pow(p.z - en.posZ, 2)); 
					if (dist < 3) {
						//ChatSender.addText(" " + p.cnt + " + " + cnt);
						p.cnt += cnt;
						
						isFound = true;
						break;
					}
				}
				if (!isFound) my_items.add(new PosItem(en.posX, en.posY, en.posZ, item_type, cnt));								
			}
		}
		
		for (PosItem item: my_items) {
			if (item.type < 0) continue;
			Color color = new Color(1f, 1f, 1f, 1f);
			if (item.type == 3) {
				color = new Color(0.5f, 0.5f, 0.5f, 1f);
			} else if (item.type == 2) {
				color = new Color(1f, 0.85f, 0f, 1f);
			} else if (item.type == 1) {
				color = new Color(0f, 1f, 1f, 1f);
			} else if (item.type == 0) {
				color = new Color(0f, 1f, 0f, 1f);
			}
			//ChatSender.addText(item.type + " => &d" + item.cnt);
			Main.draw3DText.drawText(playerPos, new Vec3(item.x, item.y + 1.5, item.z), mc.thePlayer, "" + item.cnt, color.getRGB());
		}
	}
}
