package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;

public class RainbowColorSynchronizer {
	public static ArrayList<Color> gradient_colors;
	static Random rnd;
	static Minecraft mc;
	public static int rainbowSpeed = 1;
	
	public RainbowColorSynchronizer() {	
		mc = Minecraft.getMinecraft();		
		//init colors
		gradient_colors = new ArrayList<Color>();
		for (int r=0; r<100; r++) gradient_colors.add(new Color(r*255/100,       255,         0));
		for (int g=100; g>0; g--) gradient_colors.add(new Color(      255, g*255/100,         0));
		for (int b=0; b<100; b++) gradient_colors.add(new Color(      255,         0, b*255/100));
		for (int r=100; r>0; r--) gradient_colors.add(new Color(r*255/100,         0,       255));
		for (int g=0; g<100; g++) gradient_colors.add(new Color(        0, g*255/100,       255));
		for (int b=100; b>0; b--) gradient_colors.add(new Color(        0,       255, b*255/100));
								  gradient_colors.add(new Color(        0,       255,         0));
								  
		updateBooleans();						  
	}
	
	
	
	public void updateBooleans() {
		rainbowSpeed = HintsValidator.getRainbowSpeed();
	}
	
	public Color getColor() {
		return getColor(0);
	}
	
	public Color getColor(int x) {
		if (gradient_colors.size() == 0) return new Color(1f, 1f, 1f);
		int idx = (int) ((mc.theWorld.getTotalWorldTime() * rainbowSpeed + x + gradient_colors.size()) % gradient_colors.size());
		return gradient_colors.get((gradient_colors.size() - idx) % gradient_colors.size());
	}
	
	public Color getRandomColor() {
		if (rnd == null) rnd = mc.theWorld.rand;
		return gradient_colors.get(rnd.nextInt(gradient_colors.size()));
	}
}
