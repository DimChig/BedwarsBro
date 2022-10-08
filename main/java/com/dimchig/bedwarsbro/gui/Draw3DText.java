package com.dimchig.bedwarsbro.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;

public class Draw3DText extends Gui {
	
	static Minecraft mc;
	
	public Draw3DText() {
		mc = Minecraft.getMinecraft();
	}
	
	public void drawText(Vec3 pos, Vec3 text_pos, EntityPlayerSP player, String text, int color) {
		GL11.glPushMatrix();

		GL11.glTranslated(-pos.xCoord + text_pos.xCoord, -pos.yCoord + text_pos.yCoord, -pos.zCoord + text_pos.zCoord);
        
        //GL11.glTranslatef((float) (x + 0.0), (float) (y + en.height + 0.5), (float) z);
        GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef(tntRenderer.getRenderManager().playerViewX * (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 1.0f, 0.0f, 0.0f);
        GL11.glScaled(-0.05, -0.05, 0.05);

        /* Render the text **/

        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        FontRenderer fontRenderer = mc.fontRendererObj;


        fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, 0, color);
        
        //rect
        double d = 0.1f;
        GL11.glTranslated(0, 0, d);
        int width = fontRenderer.getStringWidth(text);        
        
        drawRect(-width/2, -1, width/2, 8, new Color(0f, 0f, 0f, 0.2f).getRGB());
        
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();

        GL11.glPopMatrix();
	}
}
