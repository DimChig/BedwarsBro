package com.dimchig.bedwarsbro.gui;

import java.util.ArrayList;
import java.util.Date;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class GuiRadarIcon extends Gui {
	public static boolean isActive = false;
	private ResourceLocation resourceLoc_other = new ResourceLocation("bedwarsbro:textures/gui/other.png");
	private static Minecraft mc;
	private TextureManager textureManager;
	
	private long time_started = -1;
	private int time_visible = 1500;
	private static boolean isBedIcon = true;
	
	public GuiRadarIcon() {
		mc = Minecraft.getMinecraft();
		textureManager = mc.getTextureManager();
		updateBooleans();
	}
	
	public static void updateBooleans() {
		isActive = HintsValidator.isRadarIconActive();
	}
	
	public void show(boolean b) {
		if (!isActive) return;
		time_started = new Date().getTime();
		isBedIcon = b;
	}
	
	public void draw() {
		//initGameBeds();
		if (!isActive) return;
		if (time_started < 0) return;
		if (textureManager == null) textureManager = mc.getTextureManager();
		
		ScaledResolution sr = new ScaledResolution(mc);
        int screen_width = sr.getScaledWidth();
        int screen_height = sr.getScaledHeight(); 
		
        long t = new Date().getTime();
        
        if (t - time_started > time_visible) {
        	time_started = -1;
        	return;
        }
        
        float opacity = 1f;
        double dist = Math.abs((t - time_started - time_visible / 2f) / time_visible);
        opacity = (float) Math.pow((1 - dist*2), 0.5);

		GlStateManager.pushMatrix();
		GlStateManager.translate(screen_width / 2, 25, 0);
		float scale = 0.33f;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.color(1f, 1f, 1f, opacity);
		
		mc.renderEngine.bindTexture(resourceLoc_other);
		
		if (isBedIcon) {
			drawTexturedModalRect(-88, -64, 0, 0, 176, 128);
		} else {
			drawTexturedModalRect(-64, -64, 0, 128, 128, 128);
		}
		
		
		
		GlStateManager.popMatrix();
	}
}
