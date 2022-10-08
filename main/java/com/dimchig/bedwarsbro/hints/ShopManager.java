package com.dimchig.bedwarsbro.hints;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.input.Mouse;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.MyChatListener;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ShopManager {
	Minecraft mc;
	private KeyBinding key_rclick;
	private KeyBinding key_lclick;
	private int unpress_counter;
	private boolean mouseFlag = false;
	
	private ArrayList<MyShopItem> shopItems = new ArrayList<MyShopItem>();
	private int BUY_TICK_RATE = 5; //1s
	private int BUY_TICK_RATE_COUNTER = 0; //1s
	private static Robot robot;
	
	private String[] favourite_maps = new String[] {};
	
	public ShopManager() {
		mc = Minecraft.getMinecraft();
		key_rclick = mc.gameSettings.keyBindUseItem;
		key_lclick = mc.gameSettings.keyBindAttack;
		shopItems = new ArrayList<MyShopItem>();
		initShopItems();
		initRobot();
	}
	
	void initRobot() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
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
	public static long time_last_teams_closen = 0;
	
	void initShopItems() {
		shopItems = new ArrayList<MyShopItem>();
		String category_quickbuy = "Быстрые покупки"; //9
		String category_blocks = "Блоки";             //10
		String category_swords = "Мечи";              //11
		String category_armour = "Броня";             //12 
		String category_tools = "Инструменты";        //13 
		String category_bows = "Луки";                //14
		String category_potions = "Зелья";            //15
		String category_other = "Разное";             //16
		String category_trackers = "Трекеры";         //17

		//MAIN MENU
		/*shopItems.add(new MyShopItem(new int[] {9}, 19, null, Blocks.wool, null, 1, 1, 0, "", "i4", true));
		shopItems.add(new MyShopItem(new int[] {9}, 20, Items.stone_sword, null, null, 1, 1, 0, "", "i10", false));
		shopItems.add(new MyShopItem(new int[] {9}, 21, Items.chainmail_boots, null, null, 1, 1, 0, "", "i40", false));
		shopItems.add(new MyShopItem(new int[] {9}, 23, Items.bow, null, null, 1, 1, 0, "", "g12", false));
		shopItems.add(new MyShopItem(new int[] {9}, 24, Items.potionitem, null, null, 1, 1, 8194, "Скорка", "e1", true));
		shopItems.add(new MyShopItem(new int[] {9}, 25, null, Blocks.tnt, null, 1, 1, 0, "", "g4", true));
		
		shopItems.add(new MyShopItem(new int[] {9}, 28, null, Blocks.planks, null, 1, 1, 0, "", "g4", true));
		shopItems.add(new MyShopItem(new int[] {9}, 29, Items.iron_sword, null, null, 1, 1, 0, "", "g7", false));
		shopItems.add(new MyShopItem(new int[] {9}, 30, Items.iron_boots, null, null, 1, 1, 0, "", "g12", false));
		shopItems.add(new MyShopItem(new int[] {9}, 31, Items.shears, null, null, 1, 1, 0, "", "i16", false));
		shopItems.add(new MyShopItem(new int[] {9}, 32, Items.arrow, null, null, 1, 1, 0, "", "g2", true));
		shopItems.add(new MyShopItem(new int[] {9}, 33, Items.potionitem, null, null, 1, 1, 8197, "", "e1", true));
		shopItems.add(new MyShopItem(new int[] {9}, 34, Items.water_bucket, null, null, 1, 1, 0, "", "g4", false));*/
	}
	
	
	public class MyShopItem {
		public int slot_idx;
		public ItemStack itemStack;
		public int[] categories;
		public int price_iron;
		public int price_gold;
		public int price_emeralds;
		public boolean isCountable;
		public int cnt_can_buy;

		public MyShopItem(int[] categories, int slot_idx, Item item, Block block, Enchantment enchantment, int enchantment_level, int stackSize, int metadata, String display_name, String price, boolean isCountable) {
			this.slot_idx = slot_idx;
			this.categories = categories;
			this.cnt_can_buy = 0;
			
			if (item != null) this.itemStack = new ItemStack(item, stackSize, metadata);
			else if (block != null) this.itemStack = new ItemStack(block, stackSize, metadata);
			if (enchantment != null) this.itemStack.addEnchantment(enchantment, enchantment_level);
			if (display_name.length() > 0) this.itemStack.setStackDisplayName(display_name);
			
			this.price_iron = 0;
			this.price_gold = 0;
			this.price_emeralds = 0;
			if (price.startsWith("i")) price_iron = Integer.parseInt(price.substring(1));
			if (price.startsWith("g")) price_gold = Integer.parseInt(price.substring(1));
			if (price.startsWith("e")) price_emeralds = Integer.parseInt(price.substring(1));
			this.isCountable = isCountable;
		}
	}
	
	public void scan(boolean isBetterShopActive) {
		//initShopItems();

		//unpress mouse buttons
		//if (TICK_RATE_COUNTER <= TICK_RATE) return;
		//ChatSender.addText("asd");
		
		EntityPlayerSP player = mc.thePlayer;
		if (mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null) {
			return;
		}
			
		
		
		int idx1 = findItemInGui("Быстрые покупки", true);
		int idx3 = findItemInHotbar("Быстрый старт");
		
		//printGuiContent();
		if (findItemInGui("item.skull", true) != -1 && findItemInHotbar("Наблюдение за") != -1) {
			//ChatSender.addText("here");
			if (mc.currentScreen == null) return;
			if (!(mc.currentScreen instanceof GuiChest)) return;
			GuiChest chest = (GuiChest) mc.currentScreen;
			if (chest == null) return;
			List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

			if (chest_slots == null || chest_slots.size() == 0) return;
			for (Slot slot: chest_slots) {

				if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;
				List<String> descriptions = slot.getStack().getTooltip(player, false);
				if (descriptions == null || descriptions.size() < 2) continue;
				String name = descriptions.get(0);
				if (name.length() < 5) continue;
				TEAM_COLOR team_color = Main.customScoreboard.getTeamColorByCode("&" + name.charAt(3));
				//ChatSender.addText("" + team_color);
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
		
		//ChatSender.addText("" + findItemInGui("tile.stainedGlass", false) + ", " + findItemInGui("item.bed", false));
		
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
		
		
		if (!isBetterShopActive) return;

		if (idx1 == -1 && idx3 == -1) {
			return;
		}
		if (mc.currentScreen == null) {
			return;
		}
		if (!(mc.currentScreen instanceof GuiChest)) return;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return;	
		
		
		if (idx3 != -1) {
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
			//printHotbarContent();
			/*long t = new Date().getTime();
			if (t - time_last_teams_closen > 3000) {
				time_last_teams_closen = t;
				mc.thePlayer.closeScreen();
				clickItemInHotbar("Выбор команды");
			}*/
			
			//useItemInGui(null);
			//printGuiContent();
			
			/*[13:41:35][Client thread/INFO]:[CHAT] 10) §dРозовые [1/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 12) §cКрасные [0/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 14) §bГолубые [0/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 16) §aЗеленые [1/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 28) §fБелые [0/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 30) §7Серые [0/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 32) §9Синие [1/1] | tile.cloth
			[13:41:35] [Client thread/INFO]: [CHAT] 34) §eЖелтые [0/1] | tile.cloth*/
			
		}
		
		//single items
		ArrayList<Item> items2hide = new ArrayList<Item>();
		if (findItemInHotbar("stick") > 0) {
			items2hide.add(Items.stick);
		}
		
		List<Slot> chest_slots = chest.inventorySlots.inventorySlots;
		if (findItemInGui("Быстрые покупки", true) == -1) return;
		if (chest_slots == null || chest_slots.size() == 0) return;
		for (Slot slot: chest_slots) {

			if (slot == null || slot.getStack() == null || slot.getStack().getItem() == null) continue;
			if (slot.slotNumber >= 45) continue; //inventory
			
			List<String> descriptions = slot.getStack().getTooltip(player, false);
			if (descriptions == null) return;
			for (String s: descriptions) {
				if (s.contains("Недостаточно ресурсов") || s.contains("Уже куплено")) {
					slot.putStack(null);
					break;
				}
			}
			if (slot.getStack() == null || slot.getStack().getItem() == null) continue;
			for (Item it: items2hide) {
				if (slot.getStack().getItem() == it) {
					slot.putStack(null);
					break;
				}
			}
		}
	}

	public int findItemInHotbar(String item2find) {
		for (int i = 0; i < mc.thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			if (stack.getDisplayName().contains(item2find) || item.getUnlocalizedName().contains(item2find)) return i;
		}
		return -1;
	}

	public int findItemInGui(String item2find, boolean justGui) {
		if (mc.currentScreen == null) return -1;
		if (!(mc.currentScreen instanceof GuiChest)) return -1;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return -1;
		List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

		if (chest_slots == null || chest_slots.size() == 0) return -1;		
		for (Slot slot: chest_slots) {
			
			if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;

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
	
	public boolean useItemInHotbar(String display_name) {
		int idx = findItemInHotbar(display_name);
		if (idx == -1) return false;
		return clickItemInHotbar(display_name);
	}
	
	public boolean clickItemInHotbar(String item2find) {

		if (mc.thePlayer.inventory.getCurrentItem() == null || mc.thePlayer.inventory.getCurrentItem().getDisplayName() == null || !mc.thePlayer.inventory.getCurrentItem().getDisplayName().contains(item2find)) {
			mc.thePlayer.inventory.changeCurrentItem(1);

		}
		if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getDisplayName() != null && mc.thePlayer.inventory.getCurrentItem().getDisplayName().contains(item2find)) {
			if (robot == null) initRobot();
			try {
				robot.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
			} catch (Exception ex) {}
			
			return true;
		}
		return false;
	}

	public void useItemInGui(String display_name) { useItemInGui(display_name, 0); };
	public void useItemInGui(String display_name, int click_mode) {
		int slot_idx = findItemInGui(display_name, true);
		if (slot_idx == -1) return;
		clickItemInGui(slot_idx, click_mode);
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
			ChatSender.addText(slot.slotNumber + ") " + slot.getStack().getDisplayName() + " | " + slot.getStack().getItem().getUnlocalizedName());
		}
		ChatSender.addText("");
	}

	void printHotbarContent() {
		if (mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null) return;
		for (int i = 0; i < mc.thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			ChatSender.addText(stack.getUnlocalizedName());
		}
		ChatSender.addText("");
	}
}
