package com.dimchig.bedwarsbro.stuff;

import java.util.ArrayList;

import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemArmourLevel;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemColor;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemToolLevel;
import com.dimchig.bedwarsbro.stuff.BWItemsHandler.BWItemType;

public class BWItemsHandler {
	
	public static ArrayList<BWItem> bwitems = new ArrayList<BWItem>();
	
	public enum BWItemType {
		NONE,
		
		BLOCK_WOOL,
		BLOCK_CLAY,
		BLOCK_WOOD,
		BLOCK_ENDSTONE,
		BLOCK_GLASS,
		BLOCK_LADDER,
		BLOCK_OBSIDIAN,
		
		ARMOUR_BOOTS,
		ARMOUR_LEGGINGS,
		ARMOUR_CHESTPLATE,
		ARMOUR_HELMET, //-
		
		SWORD,
		PICKAXE,
		AXE,
		SHEARS,
		STICK,
		
		FOOD,
		BOW,
		ARROW,
		FIREBALL,
		EMERALD,
		DIAMOND,
		IRON_INGOT,
		GOLD_INGOT,
		BUCKET,
		COMPASS,
		BRIDGE_EGG,
		PEARL,
		SPONGE,
		TNT,
		
		POTION_SPEED,
		POTION_HEAL,
		POTION_REGENERATION,
		POTION_STRENGTH,
		POTION_JUMP,
		POTION_EMPTY,
		
		IGNORE
	}
	
	public enum BWItemToolLevel {
		NONE,
		WOOD,
		STONE,
		IRON,
		DIAMOND
	}
	
	public enum BWItemArmourLevel {
		NONE,
		LEATHER,
		CHAIN,
		IRON,
		DIAMOND
	}
	
	public enum BWItemColor {
		NONE,
		RED,
		YELLOW,
		GREEN,
		AQUA,
		BLUE,
		PINK,
		GRAY,
		WHITE
	}
	
	public enum BWItemPotionsID {
		//id's from PotionEffect.getPotionID()
		NONE(-1),
		STRENGTH(5),
		JUMP(8),
		SPEED(1),
		REGEN(10),
		INVIS(14); //for future updates)
		
		public int id;

	    private BWItemPotionsID(int id) {
	        this.id = id;
	    }
	}
	
	public static BWItem findItem(String local_name, String display_name) {
		if (bwitems.size() == 0) initBWItems();
		//initBWItems();
		for (BWItem item: bwitems) {
			if (local_name.contains(item.local_name) && display_name.contains(item.display_name)) {
				if (item.type == BWItemType.BLOCK_WOOL || item.type == BWItemType.BLOCK_CLAY) {
					BWItemColor color = BWItemColor.WHITE; 
					if (local_name.contains(".red")) {
						color = BWItemColor.RED;
					} else if (local_name.contains(".yellow")) {
						color = BWItemColor.YELLOW;
					} else if (local_name.contains(".lime")) {
						color = BWItemColor.GREEN;
					} else if (local_name.contains(".lightBlue")) {
						color = BWItemColor.AQUA;
					} else if (local_name.contains(".blue")) {
						color = BWItemColor.BLUE;
					} else if (local_name.contains(".pink")) {
						color = BWItemColor.PINK;
					} else if (local_name.contains(".gray")) {
						color = BWItemColor.GRAY;
					}
					
					BWItem new_item = new BWItem(local_name, display_name, item.type, BWItemToolLevel.NONE, BWItemArmourLevel.NONE);
					new_item.color = color;
					
					return new_item;
				} else if (item.type == BWItemType.POTION_REGENERATION) {
					if (display_name.contains("егенерац") || display_name.contains("egeneratio")) return item;
					else continue;
				} else if (item.type == BWItemType.POTION_SPEED) {
					if (display_name.contains("тремительнос") || display_name.contains("wiftnes")) return item;
					else continue;
				} else if (item.type == BWItemType.POTION_HEAL) {
					if (display_name.contains("ечебно") || display_name.contains("ealin")) return item;
					else continue;
				}
				
				
				return item;
			}
		}
		
		return null;
	}
	
	public static void initBWItems() {
		bwitems = new ArrayList<BWItem>();
		bwitems.add(new BWItem("appleGold", "", BWItemType.FOOD, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("arrow", "", BWItemType.ARROW, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("bootsCloth", "",    BWItemType.ARMOUR_BOOTS, BWItemToolLevel.NONE, BWItemArmourLevel.LEATHER));
		bwitems.add(new BWItem("bootsChain", "", BWItemType.ARMOUR_BOOTS, BWItemToolLevel.NONE, BWItemArmourLevel.CHAIN));
		bwitems.add(new BWItem("bootsIron", "",    BWItemType.ARMOUR_BOOTS, BWItemToolLevel.NONE, BWItemArmourLevel.IRON));
		bwitems.add(new BWItem("bootsDiamond", "", BWItemType.ARMOUR_BOOTS, BWItemToolLevel.NONE, BWItemArmourLevel.DIAMOND));
		
		bwitems.add(new BWItem("bow", "", BWItemType.BOW, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("bucketWater", "", BWItemType.BUCKET, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("chestplateCloth", "", BWItemType.ARMOUR_CHESTPLATE, BWItemToolLevel.NONE, BWItemArmourLevel.LEATHER));
		bwitems.add(new BWItem("helmetCloth", "", BWItemType.ARMOUR_HELMET, BWItemToolLevel.NONE, BWItemArmourLevel.LEATHER));
		
		bwitems.add(new BWItem("clayHardenedStained", "", BWItemType.BLOCK_CLAY, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("cloth", "", BWItemType.BLOCK_WOOL, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("compass", "§aТрекер команды", BWItemType.COMPASS, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("diamond", "", BWItemType.DIAMOND, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("egg", "§aСпавнер моста", BWItemType.BRIDGE_EGG, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("emerald", "", BWItemType.EMERALD, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("enderPearl", "", BWItemType.PEARL, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("fireball", "", BWItemType.FIREBALL, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("glass", "", BWItemType.BLOCK_GLASS, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("glassBottle", "", BWItemType.POTION_EMPTY, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("hatchetWood", "",  BWItemType.AXE, BWItemToolLevel.WOOD, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("hatchetStone", "",   BWItemType.AXE, BWItemToolLevel.STONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("hatchetIron", "",    BWItemType.AXE, BWItemToolLevel.IRON, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("hatchetDiamond", "", BWItemType.AXE, BWItemToolLevel.DIAMOND, BWItemArmourLevel.NONE));
				
		bwitems.add(new BWItem("ingotGold", "", BWItemType.GOLD_INGOT, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("ingotIron", "", BWItemType.IRON_INGOT, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("ladder", "", BWItemType.BLOCK_LADDER, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("leggingsChain", "", BWItemType.ARMOUR_LEGGINGS, BWItemToolLevel.NONE, BWItemArmourLevel.CHAIN));
		bwitems.add(new BWItem("leggingsCloth", "",     BWItemType.ARMOUR_LEGGINGS, BWItemToolLevel.NONE, BWItemArmourLevel.LEATHER));
		bwitems.add(new BWItem("leggingsDiamond", "", BWItemType.ARMOUR_LEGGINGS, BWItemToolLevel.NONE, BWItemArmourLevel.DIAMOND));
		bwitems.add(new BWItem("leggingsIron", "",    BWItemType.ARMOUR_LEGGINGS, BWItemToolLevel.NONE, BWItemArmourLevel.IRON));
		
		bwitems.add(new BWItem("obsidian", "", BWItemType.BLOCK_OBSIDIAN, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("pickaxeWood", "",  BWItemType.PICKAXE, BWItemToolLevel.WOOD, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("pickaxeStone", "",   BWItemType.PICKAXE, BWItemToolLevel.STONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("pickaxeIron", "",    BWItemType.PICKAXE, BWItemToolLevel.IRON, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("pickaxeDiamond", "", BWItemType.PICKAXE, BWItemToolLevel.DIAMOND, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("potion", "§fЗелье силы", BWItemType.POTION_STRENGTH, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("potion", "§fЗелье прыгучести", BWItemType.POTION_JUMP, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("potion", "", BWItemType.POTION_SPEED, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("potion", "", BWItemType.POTION_REGENERATION, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("potion", "", BWItemType.POTION_HEAL, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("shears", "", BWItemType.SHEARS, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("sponge", "", BWItemType.SPONGE, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("stick", "", BWItemType.STICK, BWItemToolLevel.NONE,  BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("swordWood", "",  BWItemType.SWORD, BWItemToolLevel.WOOD, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("swordStone", "",   BWItemType.SWORD, BWItemToolLevel.STONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("swordIron", "",    BWItemType.SWORD, BWItemToolLevel.IRON, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("swordDiamond", "", BWItemType.SWORD, BWItemToolLevel.DIAMOND,  BWItemArmourLevel.NONE));
		
		bwitems.add(new BWItem("tnt", "", BWItemType.TNT, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("whiteStone", "", BWItemType.BLOCK_ENDSTONE, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("wood", "", BWItemType.BLOCK_WOOD, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		
		
		bwitems.add(new BWItem("compass", "Наблюдение за игроками", BWItemType.IGNORE, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("enderPearl", "Начать новую игру", BWItemType.IGNORE, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));
		bwitems.add(new BWItem("bed", "Назад в лобби", BWItemType.IGNORE, BWItemToolLevel.NONE, BWItemArmourLevel.NONE));

	}
}


