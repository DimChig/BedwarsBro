package com.dimchig.bedwarsbro.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.hints.HintsValidator;
import com.dimchig.bedwarsbro.hints.LightningLocator.MyLightning;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemPotionsID;
import com.dimchig.bedwarsbro.hints.GeneratorTimers;
import com.dimchig.bedwarsbro.hints.HintsFinder;

public class GuiOnScreen extends Gui {
	
	private Main asInstance;
    private Minecraft mc;
    private Field renderEventTypeField;
    private Field configChangedEventModIDField;
	
    private MyItem item_diamond;
    private MyItem item_emerald;
    
    private boolean isPotionEffectTrackerActive = false;
    private boolean isPotionEffectTrackerSoundsActive = false;
    private boolean isTabBroListActive = false;
    
    public int COUNT_EMERALDS = 0;
    public int COUNT_DIAMONDS = 0;
    
    private ResourceLocation resourceLoc_potions = new ResourceLocation("bedwarsbro:textures/gui/potions.png");
    private ResourceLocation resourceLoc_other = new ResourceLocation("bedwarsbro:textures/gui/other.png");
    private TextureManager textureManager;
    
    private GuiMinimap minimap;
    private GeneratorTimers generatorTimers;
    
    private KeyBinding keyTab;
    
    public class MyItem {
    	public int offsetX;
    	public int offsetY;
    	public int width;
    	public int height;
    	public ItemStack stack;
    	
		public MyItem(int offsetX, int offsetY, int width, int height, Item item) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.width = width;
			this.height = height;
			this.stack = new ItemStack(item);
		}
		
		public void drawOnGui(int posX, int posY) {
			mc.getRenderItem().renderItemIntoGUI(this.stack, posX + this.offsetX, posY + this.offsetY);
		}
    }
    
    public void setDiamonds(int x) {
    	COUNT_DIAMONDS = x;
    }
    public void setEmeralds(int x) {
    	COUNT_EMERALDS = x;
    }
    
	public GuiOnScreen(Main asInstance) {
        this.asInstance = asInstance;
        mc = Minecraft.getMinecraft();
        minimap = Main.minimap;
        generatorTimers = Main.generatorTimers;
        
        item_diamond = new MyItem(-2, -2, 12, 13, Items.diamond);
        item_emerald = new MyItem(-3, -1, 11, 14, Items.emerald);
        
        textureManager = mc.getTextureManager();
        updateBooleans(); 
        
        keyTab = mc.gameSettings.keyBindPlayerList;
        
        try {
            //Ugly workaround to get minecraft 1.8 to work with the same jar
            renderEventTypeField = RenderGameOverlayEvent.class.getDeclaredField("type");
            renderEventTypeField.setAccessible(true);

            configChangedEventModIDField = ConfigChangedEvent.class.getDeclaredField("modID");
            configChangedEventModIDField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot find field", e);
        }
    }
	
	public void updateBooleans() {
		isPotionEffectTrackerActive = HintsValidator.isPotionEffectsTrackerActive();
		isPotionEffectTrackerSoundsActive = HintsValidator.isPotionEffectsTrackerSoundsActive();
		isTabBroListActive = Main.getConfigBool(CONFIG_MSG.BRO_TAB_LIST);
	}
	
	long lastPlaySoundTime = 0;
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) throws IllegalAccessException {
		boolean isF3 = mc.gameSettings.showDebugInfo;
        if(renderEventTypeField.get(event) == RenderGameOverlayEvent.ElementType.TEXT) {
        	ScaledResolution sr = new ScaledResolution(mc);
            int screen_width = sr.getScaledWidth();
            int screen_height = sr.getScaledHeight(); 
        	
            if (!isF3 && minimap != null) minimap.draw(mc);
            
            if (!isF3 && generatorTimers != null) generatorTimers.draw(screen_width, screen_height);
            
        	if (item_emerald != null && item_diamond != null && !isF3) {
	        	//draw stats
	            String text_emeralds = "" + COUNT_EMERALDS;
	            int color_emerals = getColor("ffffffff");
	            
	            String text_diamonds = "" + COUNT_DIAMONDS;
	            int color_diamonds = getColor("ffffffff");
	            
	           
	            
	            int font_emeralds_width = mc.fontRendererObj.getStringWidth(text_emeralds);
	            int font_emeralds_height = mc.fontRendererObj.FONT_HEIGHT;
	            int font_diamonds_width = mc.fontRendererObj.getStringWidth(text_diamonds);
	            int font_diamonds_height = mc.fontRendererObj.FONT_HEIGHT;
	            
	            int offsetX = 0;
	            if (COUNT_EMERALDS > 0) {
		            if (text_emeralds.length() == 1) offsetX = 6;
		            if (text_emeralds.length() == 2) offsetX = 12;
		            if (text_emeralds.length() == 3) offsetX = 18;
		            
		            int offsetY = 5;
		            int topX = screen_width - offsetX - item_emerald.width;
		            int topY = screen_height- offsetY - item_emerald.height;
		            int bottomX = screen_width - offsetX;
		            int bottomY = screen_height - offsetY;
		
		            item_emerald.drawOnGui(topX, topY);
		            mc.fontRendererObj.drawString(text_emeralds, bottomX - 1, bottomY - 4, color_emerals, true);
	            }
	            
	            if (COUNT_DIAMONDS > 0) {
		            if (text_diamonds.length() == 1) offsetX += 6;
		            if (text_diamonds.length() == 2) offsetX += 12;
		            if (text_diamonds.length() == 3) offsetX += 18;
		            
		            if (COUNT_EMERALDS > 0) offsetX += 12;
		            
		            int offsetY = 5;
		            int topX = screen_width - offsetX - item_diamond.width;
		            int topY = screen_height- offsetY - item_diamond.height;
		            int bottomX = screen_width - offsetX;
		            int bottomY = screen_height - offsetY;
		
		            item_diamond.drawOnGui(topX, topY-1);
		            mc.fontRendererObj.drawString(text_diamonds, bottomX - 1, bottomY - 4, color_diamonds, true);
	            }
        	}
        	if (isPotionEffectTrackerActive && !isF3) {
        		
	        	//===POTIONS===
	        	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	        	Collection<PotionEffect> pe = player.getActivePotionEffects();
	        	if (pe.size() > 0) {
	        		
	        		if (textureManager == null) textureManager = mc.getTextureManager();
	        		
	        		
	        		ArrayList<PotionEffect> potion_effects = new ArrayList<PotionEffect>();
	        		BWItemPotionsID[] potions_id_to_display = new BWItemPotionsID[] {
	        				BWItemPotionsID.STRENGTH, BWItemPotionsID.JUMP, BWItemPotionsID.SPEED, BWItemPotionsID.REGEN
	        		};
	        		for (int i = 0; i < potions_id_to_display.length; i++) {
	        			for (PotionEffect effect: pe) {
	        				if (effect.getPotionID() == potions_id_to_display[i].id) {
	        					potion_effects.add(effect);
	        					break;
	        				}
	        			}
	        		}
	        		
	        		float scale = 3f;
	        		int cx = 5;
	        		int cy = screen_height/2;
	        		int space_y = 30;
	        		int start_y = cy - space_y * (potion_effects.size() / 2);
	        		if (potion_effects.size() % 2 == 0) start_y += space_y / 2;
	        		int current_y = start_y;
	        		
		        	for (PotionEffect p: potion_effects) {
		        		BWItemPotionsID potion = null;
		        		
		        		int texX = 0;
		        		int texY = 0;
		        		Color color = new Color(1, 1, 1, 1f);
		        		String text = "-";
		        		mc.renderEngine.bindTexture(resourceLoc_potions);
		        		
		        		if (BWItemPotionsID.STRENGTH.id == p.getPotionID()) {
		        			texX = 0;
		        			texY = 0;
		        			color = new Color(1, 0, 0, 1f);
		        			text = "Силка";
		        			potion = BWItemPotionsID.STRENGTH;
		        		}
		        		if (BWItemPotionsID.JUMP.id == p.getPotionID()) {
		        			texX = 1;
		        			texY = 0;
		        			potion = BWItemPotionsID.JUMP; 
		        			text = "Прыжок";
		        			color = new Color(0, 1, 0, 1f);
		        		}
		        		if (BWItemPotionsID.SPEED.id == p.getPotionID()) {
		        			texX = 2;
		        			texY = 0;
		        			color = new Color(0, 1, 1, 1f);
		        			text = "Скорка";
		        			potion = BWItemPotionsID.SPEED; 
		        		}
		        		if (BWItemPotionsID.REGEN.id == p.getPotionID()) {
		        			texX = 0;
		        			texY = 1;
		        			color = new Color(1, 0, 1, 1f);
		        			text = "Реген";
		        			potion = BWItemPotionsID.REGEN; 
		        		}
		        		if (potion == null) continue;
		        		//draw
		        		GlStateManager.pushMatrix();
			   			//GlStateManager.rotate(50F, 0F, 0F, 0F);
			   			GlStateManager.translate(cx, current_y, 0);
			   			GlStateManager.scale(1/scale, 1/scale, 1/scale);
			   			GlStateManager.color(1f, 1f, 1f, 1f);
			   			drawTexturedModalRect(0, -40, texX * 80, texY * 80, 80, 80);
			   			
			   			
			   			GlStateManager.popMatrix();
			   			
			   			mc.fontRendererObj.drawString(text, cx + 80/scale + 5, current_y - 30/scale, color.getRGB(), false);
			   			
			   			int time = p.getDuration() / 20;
		        		String s_time = "" + time + "s";
		        		if (time > 60) {
		        			int mins = time / 60;
		        			int secs = time % 60;
		        			s_time = mins + ":" + (secs < 10 ? "0" : "") + secs;
		        		}
		        		Color text_color = new Color(1, 1, 1, 1f);
		        		boolean isShadow = false;
		        		if (p.getDuration() <= 6*20) {
		        			text_color = new Color(1, 0, 0, 1f);
		        			isShadow = true;
		        		}
		        		
		        		if (isPotionEffectTrackerSoundsActive && p.getDuration() == 6*20 && potion.id != BWItemPotionsID.REGEN.id) {
		        			long t = new Date().getTime();
		        			if (t - lastPlaySoundTime > 100) {
		        				MyChatListener.playSound("note.hat", 1f);
		        				lastPlaySoundTime = t;
		        			}
		        		}
		        		if (isPotionEffectTrackerSoundsActive && p.getDuration() == 3 && potion.id != BWItemPotionsID.REGEN.id) {
		        			long t = new Date().getTime();
		        			if (t - lastPlaySoundTime > 100) {
		        				MyChatListener.playSound("random.fizz", 1f);
		        				lastPlaySoundTime = t;
		        			}
		        		}
			   			
			   			mc.fontRendererObj.drawString(s_time, cx + 80/scale + 5, current_y, text_color.getRGB(), isShadow);
			   			
			   			current_y += space_y;
		        	}
	        	}
        	}
        	
        	if (!isF3) Main.guiRadarIcon.draw();
        	
        	Main.guiCrosshairBlocks.draw();
        	
        	if (Main.lightningLocator.isActive && Main.lightningLocator.last_lightning != null) {
        		MyLightning lightning = Main.lightningLocator.last_lightning;
        		
        		int dist = (int)Math.sqrt(Math.pow(lightning.lightning.posX - mc.thePlayer.posX, 2) + Math.pow(lightning.lightning.posZ - mc.thePlayer.posZ, 2));
        		
         		int game_time = Math.max((int)((new Date().getTime() - lightning.time_created) / 1000f) - 1, 0);
    			int seconds = game_time % 60;
    	        String time = (game_time / 60) + ":" + (seconds >= 10 ? "" : "0") + seconds;
    	        if (game_time < 60) time = game_time + " с";
        		String s = ColorCodesManager.replaceColorCodesInString("Дистанция: &a" + dist + " &l" + HintsFinder.getArrowDirection(lightning.lightning.posX, lightning.lightning.posZ) + "&f, время: &e" + time + "");
        		mc.fontRendererObj.drawString(s, screen_width / 2 - mc.fontRendererObj.getStringWidth(s) / 2, screen_height / 3 * 2, new Color(1f, 1f, 1f, 1f).getRGB(), true);        		
        	}
        	
        	if (Main.lobbyFly.isActive) {
        		GlStateManager.pushMatrix();
        		GlStateManager.translate(screen_width - 32, screen_height - 20, 0);
        		float scale = 0.5f;
        		GlStateManager.scale(scale, scale, scale);
        		GlStateManager.color(1f, 1f, 1f, 1f);
        		
        		mc.renderEngine.bindTexture(resourceLoc_other);
        		
        		drawTexturedModalRect(-75, -34.5f, 126, 187, 130, 69);

        		GlStateManager.popMatrix();
        		
        		String s = "" + Main.lobbyFly.speed;
        		if (Main.lobbyFly.speed >= 1) s = "" + (int)Main.lobbyFly.speed;
        		mc.fontRendererObj.drawString(s, screen_width - mc.fontRendererObj.getStringWidth(s) / 2 - 36, screen_height - 30, new Color(1f, 1f, 1f, 1f).getRGB(), true);
        	}
        }
    }
	
	private int getColor(String hexColor) {
        Color colorNoAlpha = Color.decode("0x" + hexColor.substring(0, 6));
        int alpha = Integer.parseInt(hexColor.substring(6, 8), 16);
        return new Color(colorNoAlpha.getRed(), colorNoAlpha.getGreen(), colorNoAlpha.getBlue(), alpha).getRGB();
    }
}
