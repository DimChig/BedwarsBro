package com.dimchig.bedwarsbro.gui;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;

import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiPlayer extends GuiScreen {
	static Minecraft mc;
	private String chosen_player_name = "";
	private static boolean isPartyMod = false;
	
	public GuiPlayer() {
		mc = Minecraft.getMinecraft();
	}
	
	
	 @Override
     public void initGui() {
	     super.initGui();
	 }



    /**

     * Called from the main game loop to update the screen.

     */
	 
    @Override
    public void updateScreen() {
    	ScaledResolution sr = new ScaledResolution(mc);
        int screen_width = sr.getScaledWidth();
        int screen_height = sr.getScaledHeight(); 
 
        int cx = screen_width / 2;
        int cy = screen_height / 2;        
	        
        buttonList.clear(); 
	    int btn_width = 100;
	    int btn_height = 20;
	    int x = cx - btn_width/2;
	    int y = cy;
	    
	    ArrayList<String> arr = new ArrayList<String>();
    	arr.add((Main.bowAimbot.isActive ? "&a" : "&c") + "Bow Aimbot");
    	arr.add((Main.bowAimbot.isDrawActive ? "&a" : "&c") + "Bow Aimbot Visualization");
    	arr.add((Main.aimHelper.isActive ? "&a" : "&c") + "Aim Helper");
    	arr.add((Main.playerFocus.STATE ? "&a" : "&c") + "ESP");
    	arr.add((Main.playerFocus.isT_Active ? "&a" : "&c") + "Tracers");

    	btn_width = 200;
    	x = cx - btn_width/2;
    			
    	y -= (int)((arr.size() / 2f + 1) * btn_height);

    	for (int i = 0; i < arr.size(); i++) {
    		buttonList.add(new GuiButton(i, x, y, btn_width, 20, ColorCodesManager.replaceColorCodesInString(arr.get(i))));
    		y += btn_height + 5;
    	}		    

   	}



    /**

     * Draws the screen and all the components in it.

     */


    @Override
    public void drawScreen(int parWidth, int parHeight, float p_73863_3_) {    	    	    	
        drawDefaultBackground();
        super.drawScreen(parWidth, parHeight, p_73863_3_);
    }        
    
    
    @Override
    protected void actionPerformed(GuiButton parButton) {    	
    	for (GuiButton btn: buttonList) {
    		if (btn == parButton) {
    			
    			if (btn.id == 0) {
    				Main.bowAimbot.toggle();
    			} else if (btn.id == 1) {
    				Main.bowAimbot.toggleDraw();
    				if (Main.bowAimbot.isDrawActive) Main.bowAimbot.isActive = true;
    			} else if (btn.id == 2) {
    				Main.aimHelper.toggle();
    			} else if (btn.id == 3) {
    				Main.playerFocus.STATE = !Main.playerFocus.STATE;
    			} else if (btn.id == 4) {
    				Main.playerFocus.isT_Active = !Main.playerFocus.isT_Active;
    				if (Main.playerFocus.isT_Active) Main.playerFocus.STATE = true;
    			} 
    			
    			return;
    		}
    	}
    }


    @Override
    public boolean doesGuiPauseGame(){
        return true;
    }
   
   
    /**

     * Called when the screen is unloaded. Used to disable keyboard repeat 

     * events

     */

    @Override

    public void onGuiClosed()

    {

    }
}
