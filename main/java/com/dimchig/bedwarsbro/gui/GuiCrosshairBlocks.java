package com.dimchig.bedwarsbro.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.stuff.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiCrosshairBlocks extends Gui {
	public static boolean isActive = false;
	private static Minecraft mc;
	
	public GuiCrosshairBlocks() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
	}
	
	public static void updateBooleans() {
		isActive = HintsValidator.isCrosshairBlocksActive();
	}

	public void draw() {
		if (!isActive) return;
		if (mc.thePlayer == null) return;
		if (!Main.chatListener.IS_IN_GAME) return;
		if (mc.thePlayer.rotationPitch < 60) return;		
		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
		if (is == null || is.getItem() != Item.getItemFromBlock(Blocks.wool)) return;
		if (is.stackSize > 10) return;
		
		ScaledResolution sr = new ScaledResolution(mc);
        int screen_width = sr.getScaledWidth();
        int screen_height = sr.getScaledHeight(); 

		GlStateManager.pushMatrix();
		GlStateManager.translate(screen_width / 2f, screen_height / 2f - 5 - mc.fontRendererObj.FONT_HEIGHT, 0);
		float scale = 1f;
		float opacity = 1f;
		if (is.stackSize > 5) {
			opacity = (11 - is.stackSize) * 0.2f;
		}
		Color color = new Color(1f, 0f, 0f, opacity);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.color(1f, 1f, 1f, 1f);		
		String text = "" + is.stackSize;				
		mc.fontRendererObj.drawString(text, -mc.fontRendererObj.getStringWidth(text)/2, 0, color.getRGB(), true);
		GlStateManager.popMatrix();
	}
}
