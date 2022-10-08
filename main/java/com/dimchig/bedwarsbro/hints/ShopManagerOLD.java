package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.MyChatListener;

import net.java.games.input.Keyboard;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ShopManagerOLD {
	Minecraft mc;
	private KeyBinding key_rclick;
	private KeyBinding key_lclick;
	private int unpress_counter;
	private boolean mouseFlag = false;
	
	private ArrayList<BuyQuee> buyQueue = new ArrayList<BuyQuee>();
	private ArrayList<MyShopItem> shopItems = new ArrayList<MyShopItem>();
	private int BUY_TICK_RATE = 5; //1s
	private int BUY_TICK_RATE_COUNTER = 0; //1s
	
	public ShopManagerOLD() {
		mc = Minecraft.getMinecraft();
		key_rclick = mc.gameSettings.keyBindUseItem;
		key_lclick = mc.gameSettings.keyBindAttack;
		buyQueue = new ArrayList<BuyQuee>();
		shopItems = new ArrayList<MyShopItem>();
		initShopItems();
	}
	
	public static boolean isShopOpenedFlag = false;
	
	void initShopItems() {
		shopItems = new ArrayList<MyShopItem>();
		String category_quickbuy = "Быстрые покупки";
		String category_blocks = "Блоки";
		String category_swords = "Мечи";
		String category_armour = "Броня";
		String category_tools = "Инструменты";
		String category_bows = "Луки";
		String category_potions = "Зелья";
		String category_other = "Разное";
		String category_trackers = "Трекеры";
		
		String item_name_stick = "item.stick";
		String item_name_wool = "tile.cloth";
		String item_name_pickaxe = "item.pickaxe";
		String item_name_axe = "item.hatchet";
		String item_name_shears = "item.shears";
		
		//STICK
		/*MyShopItem item_stick = new MyShopItem(22, Items.stick, null, Enchantment.knockback, 1, 1, 0, "Палка", "i8", false);
		item_stick.addBuyQuee(category_swords);
		item_stick.addBuyQuee(item_name_stick);
		item_stick.addBuyQuee(category_quickbuy);
		shopItems.add(item_stick);
		
		//MAXWOOL
		MyShopItem item_wool = new MyShopItem(37, null, Blocks.wool, Enchantment.efficiency, 1, 64, 0, "Стак Шерсти", "i16", true);
		item_wool.addBuyQuee(item_name_wool, 1);
		shopItems.add(item_wool);
		
		//Pickaxe
		MyShopItem item = new MyShopItem(36, Items.wooden_pickaxe, null, Enchantment.efficiency, 1, 1, 0, "", "i10", false);
		item.addBuyQuee(category_tools);
		item.addBuyQuee("" + 19);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//Axe
		item = new MyShopItem(27, Items.wooden_axe, null, Enchantment.efficiency, 1, 1, 0, "", "i10", false);
		item.addBuyQuee(category_tools);
		item.addBuyQuee("" + 20);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		

		//Multi
		item = new MyShopItem(18, Items.wooden_shovel, null, Enchantment.efficiency, 1, 1, 0, "Кирка + Топор + Ножницы", "i36", false);
		item.addBuyQuee(category_tools);
		item.addBuyQuee("" + 19);
		item.addBuyQuee("" + 20);
		item.addBuyQuee("" + 21);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//gaple
		item = new MyShopItem(38, Items.golden_apple, null, null, 1, 1, 0, "", "g3", true);
		item.addBuyQuee(category_other);
		item.addBuyQuee("" + 21);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//fireball
		item = new MyShopItem(39, Items.fire_charge, null, null, 1, 1, 0, "", "i50", true);
		item.addBuyQuee(category_other);
		item.addBuyQuee("" + 24);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//potions
		item = new MyShopItem(26, Items.potionitem, null, null, 1, 1, 8201, "Силка", "e1", true);
		item.addBuyQuee(category_potions);
		item.addBuyQuee("" + 22);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		item = new MyShopItem(35, Items.potionitem, null, null, 1, 1, 8267, "Прыжок", "e1", true);
		item.addBuyQuee(category_potions);
		item.addBuyQuee("" + 23);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		item = new MyShopItem(44, Items.potionitem, null, null, 1, 1, 8194, "Скорка", "e1", true);
		item.addBuyQuee("" + 24);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//Pearl
		item = new MyShopItem(40, Items.ender_pearl, null, null, 1, 1, 0, "Кругляш", "e4", true);
		item.addBuyQuee(category_other);
		item.addBuyQuee("" + 22);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//LADDER
		item = new MyShopItem(41, null, Blocks.ladder, null, 1, 1, 0, "", "i4", true);
		item.addBuyQuee(category_blocks);
		item.addBuyQuee("" + 23);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
		
		//DIAMOND SWORD
		item = new MyShopItem(42, Items.diamond_sword, null, null, 1, 1, 0, "", "e4", false);
		item.addBuyQuee(category_swords);
		item.addBuyQuee("" + 22);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);
			
		//DIAMOND ARMOUR
		item = new MyShopItem(43, Items.diamond_boots, null, null, 1, 1, 0, "", "e6", false);
		item.addBuyQuee(category_armour);
		item.addBuyQuee("" + 21);
		item.addBuyQuee(category_quickbuy);
		shopItems.add(item);*/
		
		
		//INSIDE SHOP
		
		//MAIN MENU
		shopItems.add(new MyShopItem(19, null, Blocks.wool, null, 1, 1, 0, "", "i4", true));
		shopItems.add(new MyShopItem(20, Items.stone_sword, null, null, 1, 1, 0, "", "i10", false));
		shopItems.add(new MyShopItem(21, Items.chainmail_boots, null, null, 1, 1, 0, "", "i40", false));
		shopItems.add(new MyShopItem(23, Items.bow, null, null, 1, 1, 0, "", "g12", false));
		shopItems.add(new MyShopItem(24, Items.potionitem, null, null, 1, 1, 8194, "Скорка", "e1", true));
		shopItems.add(new MyShopItem(25, null, Blocks.tnt, null, 1, 1, 0, "", "g4", true));
		
		shopItems.add(new MyShopItem(28, null, Blocks.planks, null, 1, 1, 0, "", "g4", true));
		shopItems.add(new MyShopItem(29, Items.iron_sword, null, null, 1, 1, 0, "", "g7", false));
		shopItems.add(new MyShopItem(30, Items.iron_boots, null, null, 1, 1, 0, "", "g12", false));
		shopItems.add(new MyShopItem(31, Items.shears, null, null, 1, 1, 0, "", "i16", false));
		shopItems.add(new MyShopItem(32, Items.arrow, null, null, 1, 1, 0, "", "g2", true));
		shopItems.add(new MyShopItem(33, Items.potionitem, null, null, 1, 1, 8197, "", "e1", true));
		shopItems.add(new MyShopItem(34, Items.water_bucket, null, null, 1, 1, 0, "", "g4", false));
	}
	
	public class BuyQuee {
		public String item_name;
		public int click_mode;
		public BuyQuee(String item_name, int click_mode) {
			this.item_name = item_name;
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

		public MyShopItem(int slot_idx, Item item, Block block, Enchantment enchantment, int enchantment_level, int stackSize, int metadata, String display_name, String price, boolean isCountable) {
			this.slot_idx = slot_idx;
			this.buyQueue = new ArrayList<BuyQuee>();
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
		
		public void addBuyQuee(String item_name) { this.addBuyQuee(item_name, 0); };
		public void addBuyQuee(String item_name, int click_mode) {
			this.buyQueue.add(new BuyQuee(item_name, click_mode));
		}
	}
	
	public void scan() {
		//initShopItems();
		//unpress mouse buttons
		//if (TICK_RATE_COUNTER <= TICK_RATE) return;
		
		
		EntityPlayerSP player = mc.thePlayer;
		if (mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null) {
			return;
		}
		
		
		String item_name = "Быстрые покупки";
		int idx = findItemInGui(item_name);
		if (idx == -1) {
			if (buyQueue.size() > 0) buyQueue.clear();
			if (isShopOpenedFlag == true) {
				isShopOpenedFlag = false;		
				//ChatSender.addText("Shop opened = "+ (isShopOpenedFlag ? "&a" : "&c") + isShopOpenedFlag);
			}
			return;
		}
		
		handleBuyQueue();
		
		//boolean isMousePressed = Mouse.getEventButtonState();
		boolean isMousePressed = false;
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) isMousePressed = true;
		
		
		if (mc.currentScreen == null) {
			return;
		}
		if (!(mc.currentScreen instanceof GuiChest)) return;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return;
		
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
			
			if (findItemInHotbar("pickaxe") > 0 && findItemInHotbar("hatchet") > 0 && findItemInHotbar("shears") > 0) {
				items2hide.add(Items.wooden_shovel);
			}
			
			if (findItemInHotbar("shears") > 0) {
				items2hide.add(Items.shears);
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
					shopItem = new MyShopItem(19, null, Blocks.wool, null, 1, 1, wool_id, "", "i4", true);
				}
				
				int cnt = 0;
				if (shopItem.price_iron > 0 && cnt_iron >= shopItem.price_iron) cnt = cnt_iron / shopItem.price_iron;
				else if (shopItem.price_gold > 0 && cnt_gold >= shopItem.price_gold) cnt = cnt_gold / shopItem.price_gold;
				else if (shopItem.price_emeralds > 0 && cnt_emeralds >= shopItem.price_emeralds) cnt = cnt_emeralds / shopItem.price_emeralds;
				if (!shopItem.isCountable && cnt > 1) cnt = 1;
				shopItem.cnt_can_buy = cnt;
				shopItem.itemStack.stackSize = cnt > 1 ? cnt : 1;
				Slot slot = chest.inventorySlots.inventorySlots.get(shopItem.slot_idx);
				if (cnt == 0) {
					slot.putStack(null);
				} else slot.putStack(shopItem.itemStack);
				
				
				
				for (Item single_item: single_items) {
					if (shopItem.itemStack.getItem() == single_item) {
						//check inventory
						if (findItemInHotbar(shopItem.itemStack.getItem().getUnlocalizedName()) != -1) {
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
		
		
			if (isMousePressed == true) {
				if (mouseFlag == true) {
					mouseFlag = false;
					
					if (chest.getSlotUnderMouse() == null) return;
	
					try {
						//ChatSender.addText("used slot " + chest.getSlotUnderMouse().slotNumber);
						if (chest.getSlotUnderMouse() == null) return;
						int slot_idx = chest.getSlotUnderMouse().slotNumber;
						
						for (MyShopItem shopItem: shopItems) {
							if (slot_idx == shopItem.slot_idx) {
								if (shopItem.cnt_can_buy > 0) {
									for (BuyQuee b: shopItem.buyQueue) {
										int click_mode = b.click_mode;
										if (shopItem.isCountable && Mouse.isButtonDown(1)) {
											click_mode = 1;
										}
										
										//buyQueue.add(new BuyQuee(b.item_name, click_mode));
									}
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
	
	
	
	void handleBuyQueue() {
		if (buyQueue == null) buyQueue = new ArrayList<BuyQuee>();
		if (buyQueue.size() <= 0) {
			BUY_TICK_RATE_COUNTER = -1;
			return;
		}
		if (BUY_TICK_RATE_COUNTER >= 0 && BUY_TICK_RATE_COUNTER <= BUY_TICK_RATE) {
			BUY_TICK_RATE_COUNTER++;
			return;
		}
		BUY_TICK_RATE_COUNTER = 0;
		
		BuyQuee b = buyQueue.get(0);
		buyQueue.remove(0);
		
		try {
			int slot_id = Integer.parseInt(b.item_name);
			clickItemInGui(slot_id, b.click_mode);
			return;
		} catch (Exception ex) {}
		useItemInGui(b.item_name, b.click_mode);
		
		//ChatSender.addText("use item &b" + b.item_name + ", click = &d" + b.click_mode);
	}
	
	
	
	
	
	
	void click_r(boolean state) {
		click_key(key_rclick, state);
	}
	void click_l(boolean state) {
		click_key(key_lclick, state);
	}

	void click_key(KeyBinding key, boolean state) {
		key.setKeyBindState(key.getKeyCode(), state);
		if (state) unpress_counter = 5;
	}
	
	int findItemInHotbar(String item2find) {
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

	boolean clickItemInHotbar(String item2find) {

		if (mc.thePlayer.inventory.getCurrentItem() == null || mc.thePlayer.inventory.getCurrentItem().getDisplayName() == null || !mc.thePlayer.inventory.getCurrentItem().getDisplayName().contains(item2find)) {
			mc.thePlayer.inventory.changeCurrentItem(1);

		}
		if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getDisplayName() != null && mc.thePlayer.inventory.getCurrentItem().getDisplayName().contains(item2find)) {
			click_r(true);
			return true;
		}
		return false;
	}

	int findItemInGui(String item2find) {
		if (mc.currentScreen == null) return -1;
		if (!(mc.currentScreen instanceof GuiChest)) return -1;
		GuiChest chest = (GuiChest) mc.currentScreen;
		if (chest == null) return -1;
		List<Slot> chest_slots = chest.inventorySlots.inventorySlots;

		if (chest_slots == null || chest_slots.size() == 0) return -1;
		for (Slot slot: chest_slots) {
			
			if (slot == null || slot.getStack() == null || slot.getStack().getDisplayName() == null) continue;
			if (slot.slotNumber >= 72) continue;
			if (slot.getStack().getItem().getUnlocalizedName().contains(item2find) || slot.getStack().getDisplayName().contains(item2find)) {
				try {
					return slot.slotNumber;
				} catch (Exception ex) {}
				break;
			}
		}
		return -1;
	}

	void clickItemInGui(int slotIdx) {clickItemInGui(slotIdx, 0);}	
	void clickItemInGui(int slotIdx, int mode) {
		mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slotIdx, 0, mode, mc.thePlayer);
	}

	boolean useItemInHotbar(String display_name) {
		int idx = findItemInHotbar(display_name);
		if (idx == -1) return false;
		return clickItemInHotbar(display_name);
	}

	void useItemInGui(String display_name) { useItemInGui(display_name, 0); };
	void useItemInGui(String display_name, int click_mode) {
		int slot_idx = findItemInGui(display_name);
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
			ChatSender.addText(stack.getDisplayName());
		}
		ChatSender.addText("");
	}
}
