package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;

public class NamePlate extends Gui {
	static Minecraft mc;
	
	private static float color_idx;
	
	public NamePlate() {
		mc = Minecraft.getMinecraft();
	}
	
	
	public void draw(Vec3 pos, boolean isRainbow, int rainbowSpeed, String constantColor) {
		if (mc == null || mc.thePlayer == null) return;
		int view_idx = mc.gameSettings.thirdPersonView;
		if (view_idx == 0) return; //pov		
		//ChatSender.addText(name);
		double name_plate_scale = 3 * 0.01;

		Vec3 text_pos = pos.add(new Vec3(0, mc.thePlayer.getEyeHeight() + 0.6, 0));
		//draw
		GL11.glPushMatrix();
		GlStateManager.disableBlend();
		GL11.glTranslated(-pos.xCoord + text_pos.xCoord, -pos.yCoord + text_pos.yCoord, -pos.zCoord + text_pos.zCoord);
        
        //GL11.glTranslatef((float) (x + 0.0), (float) (y + en.height + 0.5), (float) z);
        GL11.glRotatef(-mc.thePlayer.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.thePlayer.rotationPitch, 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef(tntRenderer.getRenderManager().playerViewX * (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 1.0f, 0.0f, 0.0f);
        GL11.glScaled(view_idx == 1 ? -name_plate_scale : name_plate_scale, -name_plate_scale, name_plate_scale);

        /* Render the text **/
        //GlStateManager.disableLighting();

        //GlStateManager.disableBlend();
        FontRenderer fontRenderer = mc.fontRendererObj;
        
        String player_name = mc.thePlayer.getName();
        String text = player_name;
        int text_width = fontRenderer.getStringWidth(player_name);
        if (isRainbow == false && constantColor.length() != 7) {
        	text = mc.thePlayer.getDisplayName().getFormattedText();
        	text_width = fontRenderer.getStringWidth(mc.thePlayer.getDisplayName().getUnformattedText());
        }

        GL11.glTranslated(0, -fontRenderer.FONT_HEIGHT/2, 0);
        
        if (constantColor.length() == 7 && constantColor.startsWith("#")) {
        	int c = -1;
        	try {
        		c = getColor(constantColor.substring(1) + "ff"); 
        	} catch (Exception ex) {}
        	if (c == -1) {
        		String str = "Ошибка цвета в конфиге!";
        		fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, new Color(1f, 0f, 0f, 1f).getRGB()); //white name (for reference)
        	} else {
        		fontRenderer.drawString(text, -text_width / 2, 0, c); //white name (for reference)
        	}
        } else if (isRainbow == false) {
        	fontRenderer.drawString(text, -text_width / 2, 0, new Color(1f, 1f, 1f, 1f).getRGB()); //white name (for reference)
        } else {
	        //draw letters 
	        int start_x = -text_width/2;
	        int gradient_hargness = 10;

	        for (int i = 0; i < player_name.length(); i++) {
	        	String t = "" + player_name.charAt(i);
	        	int t_width = fontRenderer.getStringWidth(t); 
	        	fontRenderer.drawString(t, start_x, 0, Main.rainbowColorSynchronizer.getColor(i * gradient_hargness - text.length()/2).getRGB());
	        	start_x += t_width;
	        }
        }
       
        //rect
        double d = 0.1f;
        GL11.glTranslated(0, 0, d);
             
        
        drawRect(-text_width/2 - 1, -1, text_width/2 + 1, 8, new Color(0f, 0f, 0f, 0.2f).getRGB());
        
        //GlStateManager.enableLighting();
        //GlStateManager.enableBlend();
        GL11.glPopMatrix();
	}
	
	private int getColor(String hexColor) {
        Color colorNoAlpha = Color.decode("0x" + hexColor.substring(0, 6));
        int alpha = Integer.parseInt(hexColor.substring(6, 8), 16);
        return new Color(colorNoAlpha.getRed(), colorNoAlpha.getGreen(), colorNoAlpha.getBlue(), alpha).getRGB();
    }
}
