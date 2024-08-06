package com.dimchig.bedwarsbro.stuff;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.MyChatListener;

import net.java.games.input.Keyboard;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ShopManager {
	Minecraft mc;
	private int unpress_counter;
	private boolean mouseFlag = false;
	
	private ArrayList<BuyQuee> buyQueue = new ArrayList<BuyQuee>();
	private ArrayList<MyShopItem> shopItems = new ArrayList<MyShopItem>();
	private int BUY_DELAY = 5; //ms
	
	public ShopManager() {
		mc = Minecraft.getMinecraft();
		buyQueue = new ArrayList<BuyQuee>();
		shopItems = new ArrayList<MyShopItem>();
		initShopItems();
	}
	
	private String[] favourite_maps = new String[] {};
	
	public void updateBooleans() {
		String[] split = Main.getConfigString(CONFIG_MSG.MAP_AUTO_SELECTER).split(",");
		ArrayList<String> arr = new ArrayList<String>();
		ArrayList<String> all_maps = new ArrayList<String>();
		for (String s: split) {
			String map = s.trim();
			if (map.length() < 3) continue;
			all_maps.add(map);
		}
		favourite_maps = new String[all_maps.size()];
		for (int i = 0; i < favourite_maps.length; i++) {
			favourite_maps[i] = all_maps.get(i);
		}
	}
	
	public static boolean isShopOpenedFlag = false;
	
	enum CATEGORY {
		MAIN(0),
		BLOCK(1),
		SWORD(2),
		ARMOUR(3),
		TOOL(4),
		BOWS(5),
		POTION(6),
		OTHER(7);
		
		
		public int slot_id;
		CATEGORY(int slot_id) {
			this.slot_id = slot_id;
		}
	}
	
	void initShopItems() {
		shopItems = new ArrayList<MyShopItem>();
		
		//STICK
		MyShopItem item_stick = new MyShopItem(22, Items.stick, null, Enchantment.knockback, 1, 1, 0, "Палка", "i8", false);
		item_stick.addBuyQuee(0, CATEGORY.SWORD);
		item_stick.addBuyQuee(1, 19, 0);
		item_stick.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item_stick);
		
		
		
		//Pickaxe
		MyShopItem item = new MyShopItem(18, Items.wooden_pickaxe, null, Enchantment.efficiency, 1, 1, 0, "Кирка", "i10", false);
		item.addBuyQuee(0, CATEGORY.TOOL);
		item.addBuyQuee(1, 19, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//Axe
		item = new MyShopItem(27, Items.wooden_axe, null, Enchantment.efficiency, 1, 1, 0, "Топор", "i10", false);
		item.addBuyQuee(0, CATEGORY.TOOL);
		item.addBuyQuee(1, 20, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//Shears
		item = new MyShopItem(36, Items.shears, null, null, 1, 1, 0, "Ножницы", "i16", false);
		item.addBuyQuee(0, CATEGORY.TOOL);
		item.addBuyQuee(1, 21, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		

		//Multi
		item = new MyShopItem(37, Items.wooden_shovel, null, Enchantment.efficiency, 1, 1, 0, "Кирка+Топор+Ножницы", "i36", false);
		item.addBuyQuee(0, CATEGORY.TOOL);
		item.addBuyQuee(1, 19, 0);
		item.addBuyQuee(2, 20, 0);
		item.addBuyQuee(3, 21, 0);
		item.addBuyQuee(4, CATEGORY.MAIN);
		item.buy_delay = 1000;
		shopItems.add(item);
		
		//gaple
		item = new MyShopItem(38, Items.golden_apple, null, null, 1, 1, 0, "Gapple", "g3", true);
		item.addBuyQuee(0, CATEGORY.OTHER);
		item.addBuyQuee(1, 21, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//fireball
		item = new MyShopItem(39, Items.fire_charge, null, null, 1, 1, 0, "Фаербол", "i50", true);
		item.addBuyQuee(0, CATEGORY.OTHER);
		item.addBuyQuee(1, 24, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);		
		shopItems.add(item);
		
		//potions
		item = new MyShopItem(26, Items.potionitem, null, null, 1, 1, 8201, "Силка", "e1", true);
		item.addBuyQuee(0, CATEGORY.POTION);
		item.addBuyQuee(1, 22, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		item = new MyShopItem(35, Items.potionitem, null, null, 1, 1, 8267, "Прыгучесть", "e1", true);
		item.addBuyQuee(0, CATEGORY.POTION);
		item.addBuyQuee(1, 23, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		item = new MyShopItem(44, Items.spawn_egg, null, null, 1, 1, 2, "Голем", "i192", true);
		item.addBuyQuee(0, CATEGORY.OTHER);
		item.addBuyQuee(1, 29, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//Pearl
		item = new MyShopItem(40, Items.ender_pearl, null, null, 1, 1, 0, "Пёрка (Кругляш)", "e4", true);
		item.addBuyQuee(0, CATEGORY.OTHER);
		item.addBuyQuee(1, 22, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//LADDER
		item = new MyShopItem(41, null, Blocks.ladder, null, 1, 1, 0, "Лестница x16", "i4", true);
		item.addBuyQuee(0, CATEGORY.BLOCK);
		item.addBuyQuee(1, 23, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		//DIAMOND SWORD
		item = new MyShopItem(42, Items.diamond_sword, null, null, 1, 1, 0, "Алмазный меч", "e4", false);
		item.addBuyQuee(0, CATEGORY.SWORD);
		item.addBuyQuee(1, 22, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
			
		//DIAMOND ARMOUR
		item = new MyShopItem(43, Items.diamond_boots, null, null, 1, 1, 0, "Алмазка", "e6", false);
		item.addBuyQuee(0, CATEGORY.ARMOUR);
		item.addBuyQuee(1, 21, 0);
		item.addBuyQuee(2, CATEGORY.MAIN);
		shopItems.add(item);
		
		
		//INSIDE SHOP
		
		//MAIN MENU
		shopItems.add(new MyShopItem(19, null, Blocks.wool, null, 1, 1, 0, "Блоки x16", "i4", true));
		shopItems.add(new MyShopItem(20, Items.stone_sword, null, null, 1, 1, 0, "Каменный меч", "i10", false));
		shopItems.add(new MyShopItem(21, Items.chainmail_boots, null, null, 1, 1, 0, "Кольчужка", "i40", false));
		shopItems.add(new MyShopItem(23, Items.bow, null, null, 1, 1, 0, "Лук (обычный)", "g12", false));
		shopItems.add(new MyShopItem(24, Items.potionitem, null, null, 1, 1, 8194, "Скорка", "e1", true));
		shopItems.add(new MyShopItem(25, null, Blocks.tnt, null, 1, 1, 0, "TNT", "g4", true));
		
		shopItems.add(new MyShopItem(28, null, Blocks.planks, null, 1, 1, 0, "Доски x16", "g4", true));
		shopItems.add(new MyShopItem(29, Items.iron_sword, null, null, 1, 1, 0, "Железный меч", "g7", false));
		shopItems.add(new MyShopItem(30, Items.iron_boots, null, null, 1, 1, 0, "Железка", "g12", false));
		shopItems.add(new MyShopItem(31, Items.shears, null, null, 1, 1, 0, "Ножницы", "i16", false));
		shopItems.add(new MyShopItem(32, Items.arrow, null, null, 1, 1, 0, "Стрелы x8", "g2", true));
		shopItems.add(new MyShopItem(33, Items.potionitem, null, null, 1, 1, 8197, "Зелье хила &c&l(БЕСПОЛЕЗНОЕ)", "e1", true));
		shopItems.add(new MyShopItem(34, Items.water_bucket, null, null, 1, 1, 0, "Вода", "g4", false));
	}
	
	public class BuyQuee {
		public int window_id;
		public int slot_id;		
		public int click_mode;
		
		public BuyQuee(int window_id, int slot_id, int click_mode) {
			this.window_id = window_id;
			this.slot_id = slot_id;
			this.click_mode = click_mode;
		}	
	}
	
	public class MyShopItem {
		public int slot_idx;
		public ItemStack itemStack;
		public ArrayList<BuyQuee> buyQueue;
		public int price_iron;
		public int price_gold;
		public int price_emeralds;
		public boolean isCountable;
		public int cnt_can_buy;
		public int buy_delay;
		public boolean isRemoved = false;
		public String display_name = "?";

		public MyShopItem(int slot_idx, Item item, Block block, Enchantment enchantment, int enchantment_level, int stackSize, int metadata, String display_name, String price, boolean isCountable) {
			this.slot_idx = slot_idx;
			this.buyQueue = new ArrayList<BuyQuee>();
			this.cnt_can_buy = 0;
			
			if (item != null) this.itemStack = new ItemStack(item, stackSize, metadata);
			else if (block != null) this.itemStack = new ItemStack(block, stackSize, metadata);
			if (enchantment != null) this.itemStack.addEnchantment(enchantment, enchantment_level);
			if (display_name.length() > 0) this.itemStack.setStackDisplayName(ColorCodesManager.replaceColorCodesInString("&r&b&l" + display_name));
			this.setPrice(price);
			
			this.isCountable = isCountable;
			this.buy_delay = BUY_DELAY;
			this.isRemoved = false;
			this.display_name = display_name;
		}
		
		public void setItem(Item item, int stackSize, int metadata) {
			this.itemStack = new ItemStack(item, stackSize, metadata);
		}
		
		public void setPrice(String price) {
			this.price_iron = 0;
			this.price_gold = 0;
			this.price_emeralds = 0;
			if (price.startsWith("i")) price_iron = Integer.parseInt(price.substring(1));
			if (price.startsWith("g")) price_gold = Integer.parseInt(price.substring(1));
			if (price.startsWith("e")) price_emeralds = Integer.parseInt(price.substring(1));					
		}
		
		public void setLore(Slot slot, boolean isEnoughResources) {
			String lore = "%n&r&7Стоимость: ";
			if (price_iron > 0) lore += "&f" + price_iron + "x Железа";
			if (price_gold > 0) lore += "&6" + price_gold + "x Золота";
			if (price_emeralds > 0) lore += "&2" + price_emeralds + "x Изумруда";
			if (!isEnoughResources) {
				lore += "%n%n&c&lНедостаточно ресов";
			} else {
				lore += "%n%n&a&lНажми, чтоб купить";
			}
			setSlotLore(slot, lore);
		}
		
		public void addBuyQuee(int window_id, CATEGORY category) { this.addBuyQuee(window_id, category.slot_id, 0); };
		public void addBuyQuee(int window_id, int slot_id, int click_mode) {
			this.buyQueue.add(new BuyQuee(window_id, slot_id, click_mode));
		}
	}
	
	private void handleFavouriteMaps() {
		if (findItemInGui("tile.stainedGlass", false) != -1 && findItemInGui("item.bed", false) != -1 && findItemInGui("item.arrow", false) != -1) {
			//printGuiContent();
			//tile.stainedGlass
			
			if (mc.currentScreen == null) return;
			if (!(mc.currentScreen instanceof GuiChest)) return;
			GuiChest chest = (GuiChest) mc.currentScreen;
			if (chest == null) return;
			List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

			if (chest_slots == null || chest_slots.size() == 0) return;
			if (favourite_maps == null || favourite_maps.length == 0) return;
			for (Slot slot: chest_slots) {

				if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null || slot.slotNumber > 34) continue;
				String slot_name = slot.getStack().getDisplayName();
				if (slot_name.contains("Нет доступной")) {
					slot.putStack(null);
					continue;
				}
	
				if (slot.slotNumber > 34) continue;
				
				boolean isFavMap = false;
				for (String fav_map: favourite_maps) {
					if (slot_name.contains(fav_map)) {
						isFavMap = true;
						break;
					}
				}
				if (!isFavMap) {
					slot.putStack(new ItemStack(Blocks.stained_glass, 1, 14));
					slot.getStack().setStackDisplayName(slot_name);
					continue;
				}
				//ChatSender.addText(slot.slotNumber + ") " + slot.getStack().getDisplayName() + " | " + slot.getStack().getItem().getUnlocalizedName());
			}
			
			ArrayList<String> fastMaps = new ArrayList<String>();
			for (int k = 0; k < favourite_maps.length; k++) {
				boolean isSlotFound = false;
				for (Slot slot: chest_slots) {
	   			    if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null || slot.slotNumber > 34) continue;
					if (slot.getStack().getDisplayName().contains(favourite_maps[k])) {
						fastMaps.add(favourite_maps[k]);
						break;
					}
				}
			}
			
			boolean isMousePressed = Mouse.isButtonDown(0);
			
			for (int i = 0; i < 9; i++) {
				if (i >= favourite_maps.length) break;
				boolean isMapFound = fastMaps.contains(favourite_maps[i]);
				int slot_number = i + 37;
				for (Slot slot: chest_slots) {
					if (slot.slotNumber != slot_number) continue;	
					slot.putStack(new ItemStack(Blocks.stained_glass, 1, isMapFound ? 3 : 8));
					slot.getStack().setStackDisplayName(ColorCodesManager.replaceColorCodesInString((isMapFound ? "&b" : "&7") + favourite_maps[i]));
				}
				
				Slot slot = chest.getSlotUnderMouse();
				if (isMousePressed && slot != null) {
					if (slot.slotNumber == slot_number) {
						if (isMapFound) {
							useItemInGui(favourite_maps[i]);
							ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "Запускаю &a" + favourite_maps[i] + "&f...");
						} else {
							int slot_idx = findItemInGui("item.arrow", false);
							if (slot_idx == -1) return;
							clickItemInGui(slot_idx);
						}
					}
				}
				
			}
		}
	}
	
	private void handleSpectatorsPlayerSelector() {
		if (findItemInGui("item.skull", true) != -1 && findItemInInventory("Наблюдение за") != -1) {
			//ChatSender.addText("here");
			if (mc.currentScreen == null) return;
			if (!(mc.currentScreen instanceof GuiChest)) return;
			GuiChest chest = (GuiChest) mc.currentScreen;
			if (chest == null) return;
			List<Slot> chest_slots = chest.inventorySlots.inventorySlots;
			
			EntityPlayerSP player = mc.thePlayer;

			if (chest_slots == null || chest_slots.size() == 0) return;
			for (Slot slot: chest_slots) {

				if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;
				List<String> descriptions = slot.getStack().getTooltip(player, false);
				if (descriptions == null || descriptions.size() < 2) continue;
				String name = descriptions.get(0);
				if (name.length() < 5) continue;
				TEAM_COLOR team_color = Main.customScoreboard.getTeamColorByCode("&" + name.charAt(3));
				
				ItemStack is = slot.getStack();
				is.setItem(Item.getItemFromBlock(Blocks.wool));
				
				int meta = 0;
				if (team_color == TEAM_COLOR.RED) {
					meta = 14;
				} else if (team_color == TEAM_COLOR.YELLOW) {
					meta = 4;
				} else if (team_color == TEAM_COLOR.GREEN) {
					meta = 	5;
				} else if (team_color == TEAM_COLOR.AQUA) {
					meta = 3;
				} else if (team_color == TEAM_COLOR.BLUE) {
					meta = 11;
				} else if (team_color == TEAM_COLOR.PINK) {
					meta = 	6;
				} else if (team_color == TEAM_COLOR.WHITE) {
					meta = 	0;
				} else if (team_color == TEAM_COLOR.GRAY) {
					meta = 8;
				}
				
				is.setItemDamage(meta);				
			}
		}

	}
	
	private void handleTeamSelection() {
		if (findItemInInventory("Быстрый старт") != -1) {
			if (mc.currentScreen == null) return;
			if (!(mc.currentScreen instanceof GuiChest)) return;
			GuiChest chest = (GuiChest) mc.currentScreen;
			if (chest == null) return;
			List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

			if (chest_slots == null || chest_slots.size() == 0) return;		
			for (Slot slot: chest_slots) {
				
				if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;

				if (slot.slotNumber > 34) continue;
				
				if (slot.getStack().getItem().getUnlocalizedName().contains("tile.")) {
					
					String display_name = slot.getStack().getDisplayName();
					if (display_name == null || !display_name.contains("[")) continue;
					try {
						int cnt = Integer.parseInt("" + display_name.split(" ")[1].charAt(1));
						int max_cnt = Integer.parseInt("" + display_name.split(" ")[1].charAt(3));
						ItemStack itemStack = slot.getStack();
						itemStack.stackSize = cnt;
						if (cnt != max_cnt && max_cnt != 0) {
							
							//EnumDyeColor color = Block.getMetaFromState(Block.getActualState(Block.getBlockFromItem(slot.getStack().getItem()).get, null, null));
							//ItemStack is = new ItemStack(, cnt, itemStack.getMetadata());
							ItemStack is = slot.getStack();
							is.setStackDisplayName(slot.getStack().getDisplayName());
	
							slot.getStack().setItem(Item.getItemFromBlock(Blocks.stained_glass));
							
							//slot.putStack(is);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			
		}
	}
	
	private void handleMainShop() {
		
		initShopItems();
		
		//printGuiContent();
		
		if (mc.currentScreen == null) {
			return;
		}
		if (!(mc.currentScreen instanceof GuiChest)) return;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return;	
		
		
		String item_name = "Быстрые покупки";
		int idx = findItemInGui(item_name);
		if (idx == -1) {
			if (buyQueue.size() > 0) buyQueue.clear();
			if (isShopOpenedFlag == true) {
				isShopOpenedFlag = false;		
				
			}
			return;
		}
			
		
		EntityPlayerSP player = mc.thePlayer;
		
		//boolean isMousePressed = Mouse.getEventButtonState();
		boolean isMousePressed = false;
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) isMousePressed = true;
		
		
		if (isShopOpenedFlag == false) {
			isShopOpenedFlag = true;
			mouseFlag = false;
		}
		
		
		//edit buy menu		
		if (chest.inventorySlots.inventorySlots.get(9).getStack().getMetadata() == 5) {
			
			//get my resources
			int cnt_iron = 0;
			int cnt_gold = 0;
			int cnt_emeralds = 0;
			for (ItemStack itemStack: player.inventory.mainInventory) {
				if (itemStack == null || itemStack.getItem() == null) continue;
				if (itemStack.getItem() == Items.iron_ingot) {
					cnt_iron += itemStack.stackSize;
				} else if (itemStack.getItem() == Items.gold_ingot) {
					cnt_gold += itemStack.stackSize;
				} else if (itemStack.getItem() == Items.emerald) {
					cnt_emeralds += itemStack.stackSize;
				} 
			}
			
			//single items
			Item[] single_items = new Item[] {Items.stick, Items.water_bucket};
			ArrayList<Item> items2hide = new ArrayList<Item>();
			//player armour
			if (mc.thePlayer.getInventory() != null && mc.thePlayer.getInventory().length == 4 && mc.thePlayer.getInventory()[1] != null) {
				String armour_name = mc.thePlayer.getInventory()[1].getUnlocalizedName().substring(5);
				//Cloth, Chain, Iron,Diamond
				if (armour_name.contains("Chain")) {
					items2hide.add(Items.chainmail_boots);
				} else if (armour_name.contains("Iron")) {
					items2hide.add(Items.iron_boots);
					items2hide.add(Items.chainmail_boots);
				} else if (armour_name.contains("Diamond")) {
					items2hide.add(Items.iron_boots);
					items2hide.add(Items.chainmail_boots);
					items2hide.add(Items.diamond_boots);
				} 
			}
			
			if (findItemInInventory("pickaxe") > 0 && findItemInInventory("hatchet") > 0 && findItemInInventory("shears") > 0) {
				items2hide.add(Items.wooden_shovel);
			}
			
			
			//special items with upgrades
			
			for (MyShopItem shopItem: shopItems) {
				Item item = shopItem.itemStack.getItem();
				
				if (item instanceof ItemPickaxe) {
					int slot_id = findItemInInventory("pickaxe");
					if (slot_id >= 0) {
						Item invItem = mc.thePlayer.inventory.getStackInSlot(slot_id).getItem();		          
			            shopItem.isRemoved = false;
			            if (invItem == Items.wooden_pickaxe) {
			            	shopItem.setPrice("i10");
			            	shopItem.setItem(Items.stone_pickaxe, 1, 0);      	
			            } else if (invItem == Items.stone_pickaxe) {
			            	shopItem.setPrice("g3");
			            	shopItem.setItem(Items.iron_pickaxe, 1, 0);   
			            } else if (invItem == Items.iron_pickaxe) {
			            	shopItem.setPrice("g6");
			            	shopItem.setItem(Items.diamond_pickaxe, 1, 0);
			            } else if (invItem == Items.diamond_pickaxe) {
			            	shopItem.isRemoved = true;
			            }
			            shopItem.itemStack.addEnchantment(Enchantment.efficiency, 1);			           
					}
					continue;
				}
				else if (item instanceof ItemAxe) {
					int slot_id = findItemInInventory("hatchet");
					if (slot_id >= 0) {
						Item invItem = mc.thePlayer.inventory.getStackInSlot(slot_id).getItem();
			            shopItem.isRemoved = false;
			            if (invItem == Items.wooden_axe) {
			            	shopItem.setPrice("i10");
			            	shopItem.setItem(Items.stone_axe, 1, 0);      	
			            } else if (invItem == Items.stone_axe) {
			            	shopItem.setPrice("g3");
			            	shopItem.setItem(Items.iron_axe, 1, 0);   
			            } else if (invItem == Items.iron_axe) {
			            	shopItem.setPrice("g6");
			            	shopItem.setItem(Items.diamond_axe, 1, 0);
			            } else if (invItem == Items.diamond_axe) {
			            	shopItem.isRemoved = true;
			            }
			            shopItem.itemStack.addEnchantment(Enchantment.efficiency, 1);
					}
					continue;
				} else if (item instanceof ItemShears) {
					int slot_id = findItemInInventory("shears");
					if (slot_id >= 0) {
						shopItem.isRemoved = true;
					}
					continue;
				}
			} 
			
			
			for (MyShopItem shopItem: shopItems) {				
				if (shopItem.slot_idx == 19) {
					//add wool
					int wool_id = 0;
					TEAM_COLOR team_color = MyChatListener.getEntityTeamColor(mc.thePlayer);
					if (team_color == team_color.RED) wool_id = 14;
					else if (team_color == team_color.YELLOW) wool_id = 4;
					else if (team_color == team_color.GREEN) wool_id = 5;
					else if (team_color == team_color.AQUA) wool_id = 3;
					else if (team_color == team_color.BLUE) wool_id = 11;
					else if (team_color == team_color.PINK) wool_id = 6;
					else if (team_color == team_color.WHITE) wool_id = 0;
					else if (team_color == team_color.GRAY) wool_id = 7; 
					shopItem = new MyShopItem(19, null, Blocks.wool, null, 1, 1, wool_id, "Блоки x16", "i4", true);
				}
				
				int cnt = 0;
				if (shopItem.price_iron > 0 && cnt_iron >= shopItem.price_iron) cnt = cnt_iron / shopItem.price_iron;
				else if (shopItem.price_gold > 0 && cnt_gold >= shopItem.price_gold) cnt = cnt_gold / shopItem.price_gold;
				else if (shopItem.price_emeralds > 0 && cnt_emeralds >= shopItem.price_emeralds) cnt = cnt_emeralds / shopItem.price_emeralds;
				if (!shopItem.isCountable && cnt > 1) cnt = 1;
				shopItem.cnt_can_buy = cnt;
				shopItem.itemStack.stackSize = cnt > 1 ? cnt : 1;
				Slot slot = chest.inventorySlots.inventorySlots.get(shopItem.slot_idx);
				if (cnt == 0 || shopItem.isRemoved) {
					shopItem.itemStack = new ItemStack(Item.getItemFromBlock(Blocks.stained_glass_pane), 1, 15);
					shopItem.itemStack.setStackDisplayName(ColorCodesManager.replaceColorCodesInString("&r&b&l" + shopItem.display_name));
				} 
				
				slot.putStack(shopItem.itemStack);
				
				shopItem.setLore(slot, cnt > 0);
				
				
				//if (true) return;
				
				for (Item single_item: single_items) {
					if (shopItem.itemStack.getItem() == single_item) {
						//check inventory
						if (findItemInInventory(shopItem.itemStack.getItem().getUnlocalizedName()) != -1) {
							slot.putStack(null);
							break;
						}
					}
				}
				
				for (Item i: items2hide) {
					if (shopItem.itemStack.getItem() == i) {
						slot.putStack(null);
						break;
					}
				}
				
			}
			
			//remove unneeded items (31 shears)
			Slot slot2remove = chest.inventorySlots.inventorySlots.get(31);
			if (slot2remove != null) {
				slot2remove.putStack(null);
			}
		
		
			if (isMousePressed == true) {
				if (mouseFlag == true) {
					mouseFlag = false;
					
					if (chest.getSlotUnderMouse() == null) return;
	
					try {
						
						if (chest.getSlotUnderMouse() == null) return;
						int slot_idx = chest.getSlotUnderMouse().slotNumber;
						
						for (MyShopItem shopItem: shopItems) {
							if (slot_idx == shopItem.slot_idx) {
								if (shopItem.cnt_can_buy > 0) {
									
									GuiScreen currentScreen = mc.currentScreen;
							        int offsetWindowId = -1; // default value for when there is no active window
							        
							        if (currentScreen instanceof GuiInventory) {
							            offsetWindowId = 0;
							        } else if (currentScreen instanceof GuiContainer) {
							            offsetWindowId = ((GuiContainer) currentScreen).inventorySlots.windowId;
							        }
							        
									int[] windowIds = new int[shopItem.buyQueue.size()];
									int[] slotIds = new int[shopItem.buyQueue.size()];
									int[] clickModes = new int[shopItem.buyQueue.size()];
								
									for (int i = 0; i < shopItem.buyQueue.size(); i++) {
										BuyQuee b = shopItem.buyQueue.get(i);
										windowIds[i] = b.window_id + offsetWindowId;
										slotIds[i] = b.slot_id;
										
										int click_mode = b.click_mode;
										if (shopItem.isCountable && Mouse.isButtonDown(1)) {
											click_mode = 1;
										}
										
										clickModes[i] = click_mode;
									}
									
									PacketLogger.sendCustomClickWindowPackets(windowIds, slotIds, clickModes, BUY_DELAY);
								}
								//else buyQueue.add(new BuyQuee("Быстрые покупки", 0));
								break;
							}
						}
						
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else if (mouseFlag == false) {
				mouseFlag = true;
			}
		}
	}
	
//	@SubscribeEvent
//	public void playerTick(TickEvent.ClientTickEvent event){
	
	public void scan(boolean isBetterShopActive) {
		if (mc == null) return; 
		if (mc.thePlayer == null) return; 

		
		
		EntityPlayerSP player = mc.thePlayer;
		if (mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null) {
			return;
		}
		
	
		
		//printGuiContent();
				
		
		
		handleSpectatorsPlayerSelector();
		
		handleFavouriteMaps();
		
		handleTeamSelection();
				
		//com
		if (isBetterShopActive) handleMainShop();
		//printGuiContent();

	}
	
	
	public int findItemInInventory(String item2find) {
	    for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
	        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
	        if (stack == null) continue;
	        Item item = stack.getItem();
	        if (item == null) continue;
	        if (stack.getDisplayName() == null) continue;
	        if (stack.getDisplayName().contains(item2find) || item.getUnlocalizedName().contains(item2find)) return i;
	    }
	    return -1;
	}
	
	int findItemInGui(String item2find) {
		return findItemInGui(item2find, false);
	}

	int findItemInGui(String item2find, boolean justGui) {
		if (mc.currentScreen == null) return -1;
		if (!(mc.currentScreen instanceof GuiChest)) return -1;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return -1;
		List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

		if (chest_slots == null || chest_slots.size() == 0) return -1;
		for (Slot slot: chest_slots) {
			
			if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;
			if (slot.slotNumber >= 72) continue;
			if (justGui && slot.slotNumber >= 45) continue;
			if (slot.getStack().getItem().getUnlocalizedName().contains(item2find) || slot.getStack().getDisplayName().contains(item2find)) {
				try {
					return slot.slotNumber;
				} catch (Exception ex) {}
				break;
			}
		}
		return -1;
	}
	
	public void clickItemInGui(int slotIdx) {clickItemInGui(slotIdx, 0);}	
	public void clickItemInGui(int slotIdx, int mode) {
		mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slotIdx, 0, mode, mc.thePlayer);
	}
	public void useItemInGui(String display_name) { useItemInGui(display_name, 0); };
	public void useItemInGui(String display_name, int click_mode) {
		int slot_idx = findItemInGui(display_name, true);
		if (slot_idx == -1) return;
		clickItemInGui(slot_idx, click_mode);
	}

	void setSlotLore(Slot slot, String loreText) {
		if (slot == null) return;
		ItemStack itemStack = slot.getStack();
		if (itemStack == null) return;
		try {
			
			
			NBTTagCompound stackTagCompound = itemStack.getTagCompound();
		    if (stackTagCompound == null) {
		        stackTagCompound = new NBTTagCompound();
		        itemStack.setTagCompound(stackTagCompound);
		    }	    
		    
		    // Check if the "display" tag exists, and create it if it doesn't
		    NBTTagCompound displayTag = stackTagCompound.getCompoundTag("display");
		    
		    // Check if the "Lore" tag exists, and create it as an empty list if it doesn't
		    NBTTagList loreList;
		    if (displayTag.hasKey("Lore", 9)) {
		        loreList = displayTag.getTagList("Lore", 8);
		    } else {
		        loreList = new NBTTagList();
		        displayTag.setTag("Lore", loreList);
		    }
		    
		    //loreText = "&r&aNew Lore Line 1";
		    // Set the new lore text at a specific index (e.g., index 1)
		    if (loreList.tagCount() == 0) {
		    	String t = ColorCodesManager.replaceColorCodesInString(loreText);
		    	String[] split = t.split("%n");
		    	for (int i = 0; i < split.length; i++) {		    		
		    		loreList.appendTag(new NBTTagString(split[i]));
		    	}
		    } else {
		        // Otherwise, set the new lore text at a specific index (e.g., index 0)
		        loreList.set(0, new NBTTagString(ColorCodesManager.replaceColorCodesInString(loreText)));
		    }

		    // Update the display tag in the stack's NBT data
		    stackTagCompound.setTag("display", displayTag);
		    
		    // Optionally, you can send a message to confirm the change
		    
		    // Save the modified ItemStack back to the slot if needed
		    slot.putStack(itemStack);			
		} catch (Exception ex) {};
	}

	void printGuiContent() {
		if (mc.currentScreen == null) return;
		if (!(mc.currentScreen instanceof GuiChest)) return;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return;
		List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

		if (chest_slots == null || chest_slots.size() == 0) return;
		for (Slot slot: chest_slots) {

			if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;	
			//ChatSender.addText(slot.slotNumber + ") " + slot.getStack().getDisplayName() + " | " + slot.getStack().getItem().getUnlocalizedName());			
		}
		//ChatSender.addText("");
	}

	void printHotbarContent() {
		if (mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null) return;
		for (int i = 0; i < mc.thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			//ChatSender.addText(stack.getDisplayName());
		}
		//ChatSender.addText("");
	}
}
