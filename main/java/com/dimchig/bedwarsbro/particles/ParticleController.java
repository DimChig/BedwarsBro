package com.dimchig.bedwarsbro.particles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;

public class ParticleController {
	
	static boolean isActive = false;
	
	public void updateBooleans() {
		isActive = HintsValidator.isParticlesActive();
	}
	
	public ParticleController() {
		updateBooleans();
	}
	
	public static void spawnFinalKillParticles(double posX, double posY, double posZ, TEAM_COLOR team_color) {
		if (!isActive) return;
		
		Random rand = new Random();
		int n = 150;
		float motion_randomness = 0.2f;
		float position_randomness = 0.1f;
		
		
		Color color = getParticleColorForTeam(team_color);
		float color_r = color.getRed()   / 255f;
		float color_g = color.getGreen() / 255f;
		float color_b = color.getBlue()  / 255f;
		
		for (int i = 0; i < n; i++) {
			spawnColorParticle(motion_randomness, position_randomness, posX, posY, posZ, rand, color_r, color_g, color_b, false);
		}
	}
	
	public static Color getParticleColorForTeam(TEAM_COLOR team_color) {
		if (team_color == TEAM_COLOR.RED) return new Color(255, 0, 0);
		if (team_color == TEAM_COLOR.YELLOW) return new Color(255, 255, 0);
		if (team_color == TEAM_COLOR.GREEN) return new Color(0, 255, 0);
		if (team_color == TEAM_COLOR.AQUA) return new Color(0, 255, 255);
		if (team_color == TEAM_COLOR.BLUE) return new Color(0, 0, 255);
		if (team_color == TEAM_COLOR.PINK) return new Color(255, 0, 255);
		if (team_color == TEAM_COLOR.GRAY) return new Color(128, 128, 128);
		if (team_color == team_color.WHITE) return new Color(255, 255, 255);
		return new Color(0, 0, 0);
	}
	
	public static void spawnGenDiamondParticles(double posX, double posY, double posZ, int cnt_diamonds) {
		if (!isActive) return;
		
		Random rand = new Random();
		int n = 100 + 50 * cnt_diamonds;
		float motion_randomness = 0.2f;
		float position_randomness = 0.0f;
		
		
		Color color = new Color(0, 255, 255);
		float color_r = color.getRed()   / 255f;
		float color_g = color.getGreen() / 255f;
		float color_b = color.getBlue()  / 255f;
		
		for (int i = 0; i < n; i++) {
			spawnColorParticle(motion_randomness, position_randomness, posX, posY, posZ, rand, color_r, color_g, color_b, true);
		}
	}
	
	public static void spawnGenEmeraldParticles(double posX, double posY, double posZ, int cnt_emeralds) {
		if (!isActive) return;
		
		Random rand = new Random();
		int n = 100 + 50 * cnt_emeralds;
		float motion_randomness = 0.3f;
		float position_randomness = 0.0f;
		
		
		Color color = new Color(0, 255, 0);
		float color_r = color.getRed()   / 255f;
		float color_g = color.getGreen() / 255f;
		float color_b = color.getBlue()  / 255f;
		
		for (int i = 0; i < n; i++) {
			spawnColorParticle(motion_randomness, position_randomness, posX, posY, posZ, rand, color_r, color_g, color_b, true);
		}
	}
	
	public static void spawnColorParticle(double motion_rnd, double pos_rnd, double posX, double posY, double posZ, Random rand, float color_r, float color_g, float color_b, boolean isOnlyTop) {
		//if (!isActive) return;
		
	    posX += rand.nextGaussian() * pos_rnd;
	    posY += rand.nextGaussian() * pos_rnd;
	    posZ += rand.nextGaussian() * pos_rnd;
    	double motionX = rand.nextGaussian() * motion_rnd;
    	double motionY = rand.nextGaussian() * motion_rnd;
    	double motionZ = rand.nextGaussian() * motion_rnd;
    	if (motion_rnd == -1000) {
    		motionX = -1000;
    		motionY = -1000;
    		motionZ = -1000;
    	}
	    
	    if (isOnlyTop == true) motionY = Math.abs(motionY);
	       	
	    spawnColorParticle(posX, posY, posZ, motionX, motionY, motionZ, color_r, color_g, color_b);
    }
	
	public static void spawnColorParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float color_r, float color_g, float color_b) {
		//if (!isActive) return;
		
		ParticleFinalKillEffect pe = new ParticleFinalKillEffect(Minecraft.getMinecraft().theWorld, posX, posY, posZ);   
		if (motionX != -1000) pe.motionX = motionX;
		if (motionY != -1000)pe.motionY = motionY;
		if (motionZ != -1000)pe.motionZ = motionZ;
		pe.setRBGColorF(color_r, color_g, color_b);
	    //particleMysterious.getEnti
    	Minecraft.getMinecraft().effectRenderer.addEffect(pe);   
    }
	
	public static void spawnColorParticleSharpness(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float color_r, float color_g, float color_b) {
		if (!isActive) return;
		
		ParticleSharpnessEffect pe = new ParticleSharpnessEffect(Minecraft.getMinecraft().theWorld, posX, posY, posZ);   
		pe.motionX = motionX;
		pe.motionY = motionY;
		pe.motionZ = motionZ;
		pe.setRBGColorF(color_r, color_g, color_b);
	    //particleMysterious.getEnti
    	Minecraft.getMinecraft().effectRenderer.addEffect(pe);   
	}
}
