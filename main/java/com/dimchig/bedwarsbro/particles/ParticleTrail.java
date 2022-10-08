package com.dimchig.bedwarsbro.particles;

import java.util.Random;

import java.awt.Color;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;


//WORKS, BUT WHEN YOU BRIDGE LOOKS UGLY + ОНО ПРЕРЫВАЕТЬСЯ ПО НЕМНОГУ ИЗ ЗА PlayerTickevent 

public class ParticleTrail {	
	public static ParticleController particleController = Main.particleController;
	public static boolean isRainbowAlways = false;
	
	Minecraft mc;
	
	public ParticleTrail() {
		updateBooleans();
		mc = Minecraft.getMinecraft();
	}
	
	public void updateBooleans() {
		this.isRainbowAlways = HintsValidator.isParticleTrailRainbowActive();
	}
	
	public void drawPlayerTrail() {
		EntityPlayerSP player = mc.thePlayer;
		
		if (player.rotationPitch > 50) return; //prevent spawning while bridging
		
    	
		double px = player.lastTickPosX + 4;
		double py = player.lastTickPosY + player.getEyeHeight() - mc.theWorld.rand.nextFloat() * 0.5;
		double pz = player.lastTickPosZ;
		
		double angle = Math.toRadians(player.rotationYaw - 90);
		//angle = Math.toRadians(Minecraft.getMinecraft().theWorld.rand.nextInt(360));
		double distance = 0.6;
		px = (float)(distance * Math.cos(angle)) + px - 4;
		pz = (float)(distance * Math.sin(angle)) + pz;
		if ((player.motionX == 0 && player.motionZ == 0) || player.motionY < -2) return;
		
		Color color = new Color(0, 0, 0);
    	float color_r = 0;
    	float color_g = 0;
    	float color_b = 0;
    	
    	TEAM_COLOR mod_team_color = MyChatListener.getEntityTeamColor(player);
    	if (!isRainbowAlways && mod_team_color != TEAM_COLOR.NONE) {
    		
    		color = ParticleController.getParticleColorForTeam(mod_team_color);
    	} else {
    		color = Main.rainbowColorSynchronizer.getColor();
    	}
    	
    	
    	color_r = color.getRed() / 255f; 
    	color_g = color.getGreen() / 255f; 
    	color_b = color.getBlue() / 255f; 
		
		//ChatSender.addText("" + player.rotationPitch);
		particleController.spawnColorParticle(-1000, 0.05, px, py, pz, mc.theWorld.rand, color_r, color_g, color_b, false);
	}
}
