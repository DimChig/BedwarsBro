package com.dimchig.bedwarsbro.stuff;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GeneratorTimers extends Gui {
	static Minecraft mc;
	static boolean isActive = false;
	static int start_diamond_time = 30;
	static long time_last_diamond = 0;
	static int time_diamond_max = start_diamond_time;
	static int time_diamond_buffer = time_diamond_max; 
	
	static int start_emerald_time = 65;
	static long time_last_emerald = 0;
	static int time_emerald_max = start_emerald_time;
	static int time_emerald_buffer = time_emerald_max; 
	
	static long time_game_start = 0;
	
	static int corner_position = 2;
	static boolean isAdvanced = false;
	static boolean isGameTime = false;
	static boolean isTimeline = false;
	static int timeline_width_percentage = 80;
	ResourceLocation resourceLoc_textures;
	
	public GeneratorTimers() {
		mc = Minecraft.getMinecraft();
		resourceLoc_textures = new ResourceLocation("bedwarsbro:textures/gui/timeline_icons.png");
	}
	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.GENERATOR_TIMERS);
		corner_position = Main.getConfigInt(CONFIG_MSG.GENERATOR_TIMERS_POSITION);
		isAdvanced = Main.getConfigBool(CONFIG_MSG.GENERATOR_TIMERS_ADVANCED);
		isTimeline = Main.getConfigBool(CONFIG_MSG.GENERATOR_TIMERS_TIMELINE);
		isGameTime = Main.getConfigBool(CONFIG_MSG.GENERATOR_TIMERS_GAME_TIME);
		timeline_width_percentage = Main.getConfigInt(CONFIG_MSG.GENERATOR_TIMERS_TIMELINE_WIDTH);
	}
	
	public void setMaxTimeDiamonds(int new_time) {
		time_diamond_buffer = new_time;
		//ChatSender.addText("update happended after &b" + ((new Date().getTime() - time_game_start) / 1000 - 1) + " &fseconds");
	}
	
	
	public void setMaxTimeEmeralds(int new_time) {
		time_emerald_buffer = new_time;		
	}
	
	public void setHardTimeDiamonds(int hard_time) {
		time_last_diamond = new Date().getTime() - (time_diamond_max - hard_time + 1) * 1000;
	}
	
	public void setHardTimeEmeralds(int hard_time) {
		time_last_emerald = new Date().getTime() - (time_emerald_max - hard_time + 1) * 1000;
	}
	
	public void onTick() {
		
		//ChatSender.addText("time: &a" + ((new Date().getTime() - time_game_start) / 1000 - 1) + " &fseconds");
		
		try {
			List<EntityArmorStand> entities = mc.theWorld.getEntities(EntityArmorStand.class, EntitySelectors.selectAnything);
			if (entities != null) {
				boolean isDiamondGenSet = false;
				boolean isEmeraldGenSet = false;
				for (EntityArmorStand en: entities) {
					if (en == null || en.getDisplayName() == null) continue;
					String name = en.getDisplayName().getUnformattedText();
					
					if (name.contains("енератор")) {

						String connected_stand_text = null;
						
						final BlockPos minPos = new BlockPos(en.posX - 1.0, en.posY - 1.0, en.posZ - 1.0);
	                    final BlockPos maxPos = new BlockPos(en.posX + 1.0, en.posY + 1.0, en.posZ + 1.0);
	                    final AxisAlignedBB box = new AxisAlignedBB(minPos, maxPos);
	                    List<EntityArmorStand> armorStands = mc.theWorld.getEntitiesWithinAABB((Class)EntityArmorStand.class, box);
	                    if (armorStands == null) continue;
	                    for (EntityArmorStand en2: armorStands) {
	                    	if (en2 == null || en2.getDisplayName() == null) continue;
							String en2_name = en2.getDisplayName().getFormattedText();
							if (en2_name.contains("через")) {
								connected_stand_text = en2_name;
								break;
							}
	                    }
	                    
	                    if (connected_stand_text == null) continue;
	                    
	                    
	                    int hard_time = -1;
	                    try {
	                    	hard_time = Integer.parseInt(connected_stand_text.split("§c")[1].trim().split(" ")[0].trim());
	                    } catch (Exception ex) {};
	                    if (hard_time != -1) {
	                    	if (!isDiamondGenSet && name.contains("алмаз")) {
	                    		Main.generatorTimers.setHardTimeDiamonds(hard_time);
	                    		isDiamondGenSet = true;
	                    	}
	                    	else if (!isEmeraldGenSet && name.contains("изумр")) {
	                    		Main.generatorTimers.setHardTimeEmeralds(hard_time);
	                    		isEmeraldGenSet = true;
	                    	}
	                    }
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setStartTimesOnGameStart() {
		start_diamond_time = 30;
		long t = new Date().getTime();
		
		time_last_diamond = t;
		time_diamond_max = start_diamond_time;
		time_diamond_buffer = start_diamond_time;
		
		time_last_emerald = t;
		time_emerald_max = start_emerald_time;
		time_emerald_buffer = start_emerald_time;
		
		time_game_start = t;
	}
	
	public void draw(int screen_width, int screen_height) {
		if (!isActive || !Main.chatListener.IS_IN_GAME) return;
		
		int padding = 6;
		int gameTime_posX = padding;
		int gameTime_posY = padding;
		

		if (corner_position == 1) {
			gameTime_posX = 2;
			gameTime_posY = padding;
		} else if (corner_position == 2) {
			gameTime_posX = screen_width - 23 - padding;
			gameTime_posY = padding;
		} else if (corner_position == 3) {
			gameTime_posX = screen_width - 23 - padding;
			gameTime_posY = screen_height - 24 - padding + (isAdvanced ? -16 : 0) + (isGameTime ? -16 : 0) - 18;
		} else {
			gameTime_posX = 2;
			gameTime_posY = screen_height - 24 - padding + (isAdvanced ? -16 : 0) + (isGameTime ? -16 : 0);
		}				
		
		float opacity = 0.1f;				
		
		long t = new Date().getTime();
		
		boolean isAliningToLeft = corner_position == 1 || corner_position == 4;
		
		if (isGameTime) {
			
			int game_time = Math.max((int)((t - time_game_start) / 1000f) - 1, 0);
			int seconds = game_time % 60;
	        String text_gameTime = (game_time / 60) + ":" + (seconds >= 10 ? "" : "0") + seconds;
	        
	        int total_width = mc.fontRendererObj.getStringWidth(text_gameTime) + padding + mc.fontRendererObj.getStringWidth(text_gameTime)/2;
	 
	        
	        
	        int px = gameTime_posX + 1;
	        
	        if (!isAliningToLeft) {
	        	px = screen_width - total_width - 3;
	        }
	        
	        Gui.drawRect(px - 1, gameTime_posY - 4, px + total_width, gameTime_posY + 12, new Color(0f, 0f, 0f, opacity).getRGB());
	        
			int color_gameTime = new Color(1f, 1f, 1f, 1f).getRGB();
			//icon clock
			float scale_clock = 0.8f;
			mc.renderEngine.bindTexture(resourceLoc_textures);
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.translate(px + 7, gameTime_posY + 3, 0);
			GlStateManager.scale(scale_clock, scale_clock, scale_clock);
			GlStateManager.color(1f, 1f, 1f, 1f);
			drawTexturedModalRect(-6, -6, 0, 0, 12, 12);
			GlStateManager.popMatrix();
			
	        mc.fontRendererObj.drawString(text_gameTime, px + 15, gameTime_posY, color_gameTime, true);
			
		}
        
		int diamonds_posX = gameTime_posX;
		int diamonds_posY = gameTime_posY + (isGameTime ? 16 : 0);
		
		
		int time_diamonds = (int)((t - time_last_diamond) / 1000f);
		
		if (time_diamonds > time_diamond_max) {
			time_last_diamond = t;
			time_diamond_max = time_diamond_buffer;
		}
		
		int time_diamonds_diff = time_diamond_max - time_diamonds + 1;
		
		String text_diamonds = "" + time_diamonds_diff;
		int color_diamonds = new Color(0f, 1f, 1f, 1f).getRGB();
		
		if (time_diamonds_diff > time_diamond_max) {
			text_diamonds = "0";
			color_diamonds = new Color(1f, 0f, 0f, 1f).getRGB();
		}
		
		Gui.drawRect(diamonds_posX, diamonds_posY - 4, diamonds_posX + 26, diamonds_posY + 12, new Color(0f, 0f, 0f, opacity).getRGB());
		
		mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.diamond), diamonds_posX -2, diamonds_posY - 5);
        mc.fontRendererObj.drawString(text_diamonds, diamonds_posX + 19 - mc.fontRendererObj.getStringWidth(text_diamonds)/2, diamonds_posY, color_diamonds, true);
        
        //emeralds
        int time_emeralds = (int)((t - time_last_emerald) / 1000f);
		
		if (time_emeralds > time_emerald_max) {
			time_last_emerald = t;
			time_emerald_max = time_emerald_buffer;
		}
        
		int time_emeralds_diff = time_emerald_max - time_emeralds + 1;
		
        String text_emeralds = "" + time_emeralds_diff;
		int color_emeralds = new Color(0f, 1f, 0f, 1f).getRGB();
		
		if (time_emeralds_diff > time_emerald_max) {
			text_emeralds = "0";
			color_emeralds = new Color(1f, 0f, 0f, 1f).getRGB();
		}
		
		int emeralds_posX = diamonds_posX;
		int emeralds_posY = diamonds_posY + 16;
        
		Gui.drawRect(emeralds_posX, emeralds_posY - 4, emeralds_posX + 26, emeralds_posY + 12, new Color(0f, 0f, 0f, opacity).getRGB());
		
		mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.emerald), emeralds_posX - 2, emeralds_posY - 4);
        mc.fontRendererObj.drawString(text_emeralds, emeralds_posX + 19 - mc.fontRendererObj.getStringWidth(text_emeralds)/2, emeralds_posY, color_emeralds, true);
        
        
        //advanced
        
        
        int[] times = new int[] {300, 450, 900, 1400, 2400, 3000};
        int diff = (int)((t - time_game_start) / 1000f) - 1;
        String name = "";
        
        if (isTimeline) drawTimeline(diff, times, screen_width, screen_height);
        
        if (!isAdvanced) return;
        
        int nearest_upgrate_time = -1;
        for (int i = 0; i < times.length; i++) {
        	if (diff < times[i]) {
        		nearest_upgrate_time = times[i] - diff - 1;
        		break;
        	}
        }
        
        if (nearest_upgrate_time == -1) return;
        
        int seconds = nearest_upgrate_time % 60;
        String time = (nearest_upgrate_time / 60) + ":" + (seconds >= 10 ? "" : "0") + seconds;
        int color = new Color(1f, 1f, 1f, 1f).getRGB();
        
        
        
        int advanced_posX = diamonds_posX;
        if (isAliningToLeft) advanced_posX = diamonds_posX;
        int advanced_posY = emeralds_posY + 16;
                
        int offsetX = 0;
        ItemStack itemStack = new ItemStack(Items.snowball);
        double itemStackOffsetX = 0;
        double itemStackOffsetY = 0;
        int itemStackWidth = 0;
        float scale = 1f;
        
        if (diff < times[0]) {
        	name = "I";
        	color = new Color(0f, 1f, 1f, 1f).getRGB();
        	itemStack = new ItemStack(Items.diamond);
        	itemStackOffsetX = -2;
        	itemStackOffsetY = -5;
        	itemStackWidth = 12;
        } else if (diff < times[1]) {
        	name = "II";
        	color = new Color(0f, 1f, 1f, 1f).getRGB();
        	itemStack = new ItemStack(Items.diamond);
        	itemStackOffsetX = -2;
        	itemStackOffsetY = -5;
        	itemStackWidth = 12;
        } else if (diff < times[2]) {
        	name = "I";
        	color = new Color(0f, 1f, 0f, 1f).getRGB();
        	itemStack = new ItemStack(Items.emerald);
        	itemStackOffsetX = -2;
        	itemStackOffsetY = -4;
        	itemStackWidth = 11;
        } else if (diff < times[3]) {
        	name = "II";
        	color = new Color(0f, 1f, 0f, 1f).getRGB();
        	itemStack = new ItemStack(Items.emerald);
        	itemStackOffsetX = -2;
        	itemStackOffsetY = -4;
        	itemStackWidth = 11;
        } else if (diff < times[4]) {
        	name = "Без кроватей";       	
        	color = new Color(1f, 0f, 0f, 1f).getRGB();
        	itemStack = new ItemStack(Items.bed);
        	itemStackOffsetX = 0;
        	itemStackOffsetY = -5;
        	itemStackWidth = 15;
        } else if (diff < times[5]) {
        	name = "Конец игры";
        	color = new Color(1f, 1f, 1f, 1f).getRGB();
        	itemStack = new ItemStack(Item.getItemFromBlock(Blocks.barrier));
        	itemStackOffsetX = 1;
        	itemStackOffsetY = -2.5;
        	itemStackWidth = 10;
        	scale = 0.8f;
        }
        
        padding = 15;
        
        int total_width = mc.fontRendererObj.getStringWidth(name) + itemStackWidth + padding + mc.fontRendererObj.getStringWidth(time)/2;
        
        int px = advanced_posX + 1;
        
        if (!isAliningToLeft) {
        	px = screen_width - total_width - 3;
        }
        
    	Gui.drawRect(px - 1, advanced_posY - 4, px + total_width, advanced_posY + 12, new Color(0f, 0f, 0f, opacity).getRGB());
        
        
        
        
        mc.fontRendererObj.drawString(name, px, advanced_posY, color, true);
        px += mc.fontRendererObj.getStringWidth(name);
        
        if (scale != 1f) {
        	GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
        }
        
    	mc.getRenderItem().renderItemIntoGUI(itemStack, (int)((px + itemStackOffsetX) / scale), (int)((advanced_posY + itemStackOffsetY) / scale));
    	
    	if (scale != 1f) GlStateManager.popMatrix();
    	
    	px += itemStackWidth;
        	
        
    	px += padding;
    	mc.fontRendererObj.drawString(time, px - mc.fontRendererObj.getStringWidth(time)/2, advanced_posY, color, true);
	}
	
	public void drawTimeline(int game_time, int[] times, int screen_width, int screen_height) {
		int margin_x = 40;
		int margin_y = 12;
		int offset_from_right = 20;
		int tm_width = (int)(screen_width * timeline_width_percentage / 100f) - margin_x; 
		tm_width = (tm_width * 2) / 2;
		
		int tm_height = 2;
		
		int x1 = (screen_width - tm_width) / 2;
		tm_width += -offset_from_right;
		int x2 = x1 + tm_width;
		
		List<EntityDragon> dragons = mc.theWorld.getEntities(EntityDragon.class, EntitySelectors.selectAnything);
		if (dragons != null && dragons.size() > 0) {
			margin_y += 17;
			int py = margin_y + 20;
			for (EntityDragon dragon: dragons) {
				if (dragon == null || dragon.getDisplayName() == null) continue;			
				//ChatSender.addText(dragon.getDisplayName().getFormattedText() + " => &c" + dragon.getHealth());
				String health_s = "" + (int)(dragon.getHealth() / 2) + "%";
				mc.fontRendererObj.drawString(dragon.getDisplayName().getFormattedText() + "" + EnumChatFormatting.GRAY + " ▸ " + EnumChatFormatting.RED + health_s, x1, py, new Color(1f, 1f, 1f, 1f).getRGB());
				py += 10;
				
			}
		}
		
		
		int y1 = margin_y;
		int y2 = y1 + tm_height;
		
		Gui.drawRect(x1, y1, x2, y2, new Color(1f, 1f, 1f, 1f).getRGB());
		
		//adding "0" at start
		int anchor_times[] = new int[times.length + 2];  
		for(int i = 0; i < times.length; i++) {  
			anchor_times[i + 1] = times[i];  
		}  
		anchor_times[0] = 0;
		anchor_times[anchor_times.length - 1] = 4000;
		
		
		float scaling_factor = tm_width * 1f / times[times.length - 1];
		int stick_height = 10;
		int stick_width = 2;
		
		int color_lightblue = new Color(0f, 1f, 1f, 1f).getRGB();
		int color_green = new Color(0f, 1f, 0f, 1f).getRGB();
		int color_purple = new Color(178 / 255f, 84 / 255f, 1f, 1f).getRGB();
		int color_white = new Color(1f, 1f, 1f, 1f).getRGB();
		int color_black = new Color(0f, 0f, 0f, 1f).getRGB();
		
		for (int i = 1; i < anchor_times.length; i++) {
			int tx1 = x1 + (int)(scaling_factor * anchor_times[i - 1]);
			int tx2 = x1 + (int)(scaling_factor * anchor_times[i]);
			//ChatSender.addText(i + ") &e" + anchor_times[i] + " &7=> &a" + tx1 + ", " + tx2);
			String text_name = "-";
			ItemStack itemStack = new ItemStack(Blocks.stone);
			float itemStack_offsetX;
			
			int color = color_black;
			if (i == 1) {
				color = color_white;
				text_name = "";
				itemStack = null;
			} else if (i == 2) {
				color = color_lightblue;
				text_name = "I";
				itemStack = new ItemStack(Items.diamond);
			} else if (i == 3) {
				color = color_lightblue;
				text_name = "II";
				itemStack = new ItemStack(Items.diamond);
			} else if (i == 4) {
				color = color_green;
				text_name = "I";
				itemStack = new ItemStack(Items.emerald);
			} else if (i == 5) {
				color = color_green;
				text_name = "II";
				itemStack = new ItemStack(Items.emerald);
			} else if (i >= 6) {
				color = color_purple;
				text_name = "Драконы";
				itemStack = new ItemStack(Item.getItemFromBlock(Blocks.dragon_egg));
			}
			
			
			//stick
			Gui.drawRect(tx1 - stick_width/2, y1 - stick_height / 2, tx1 + stick_width/2, y2 + stick_height / 2, color);
			//text time
			int label_time = anchor_times[i - 1];
			int seconds = label_time % 60;
			String text_time = (label_time / 60) + ":" + (seconds >= 10 ? "" : "0") + seconds;
			float width = mc.fontRendererObj.getStringWidth(text_time);
			mc.fontRendererObj.drawString(text_time, tx1 - width/2, y2 + stick_height / 2 + 1, color, true);
			
			if (i == 7) continue;
			//line
			Gui.drawRect(tx1, y1, tx2, y2, color);
			//text name
			if (itemStack == null) continue;
			width = mc.fontRendererObj.getStringWidth(text_name);
			int px = (tx1 + tx2) / 2 - 4;
			int py = y1 - stick_height / 2 - 5;
			mc.fontRendererObj.drawString(text_name, px - width/2, py, color, true);
			  //item
			float scale = 0.6f;
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
			mc.getRenderItem().renderItemIntoGUI(itemStack, (int)((px + width/2) / scale), (int)((py) / scale) - 0);
			GlStateManager.popMatrix();
		}
		
		
		if (!isGameTime) {
			//draw current time
			//text time				
			//game_time = (int)(mc.theWorld.getTotalWorldTime() * 10 % times[times.length - 1]);
			game_time = game_time > 0 ? game_time : 0;
			int offsetX = 10;
			int seconds = game_time % 60;
			String text_time = (game_time / 60) + ":" + (seconds >= 10 ? "" : "0") + seconds;
			float width = mc.fontRendererObj.getStringWidth(text_time);
			mc.fontRendererObj.drawString(text_time, x2 + offsetX + 7, y2 - 4, new Color(1f, 1f, 1f, 1f).getRGB(), true);
			//icon clock
			float scale = 0.8f;
			mc.renderEngine.bindTexture(resourceLoc_textures);
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.translate(x2 + offsetX, y2, 0);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.color(1f, 1f, 1f, 1f);
			drawTexturedModalRect(-6, -6, 0, 0, 12, 12);
			GlStateManager.popMatrix();
		}
		
		//icon current time
		mc.renderEngine.bindTexture(resourceLoc_textures);
		float scale = 0.5f;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x1 + game_time * scaling_factor, (y1 + y2) / 2, 0);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.color(1f, 1f, 1f, 1f);
		drawTexturedModalRect(-8, -8, 12, 0, 16, 16);
		GlStateManager.popMatrix();
	}
}
