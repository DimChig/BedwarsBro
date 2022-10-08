package com.dimchig.bedwarsbro.hints;

import com.dimchig.bedwarsbro.hints.LightningLocator.MyLightning;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LobbyFly {
	static Minecraft mc;
	public static boolean isActive = false;
	public static MyLightning last_lightning = null;
	public static float speed = 1;
	
	public LobbyFly() {
		mc = Minecraft.getMinecraft();
		isActive = false;
		speed = 1;
	}
	
	@SubscribeEvent
	public void playerTick(TickEvent.ClientTickEvent event){
		if (isActive) {
			if (speed > 1f) speed = (float) Math.floor(speed);
			double yaw = mc.thePlayer.rotationYaw;
    		double pitch = mc.thePlayer.rotationPitch - 15d;
    		if (mc.thePlayer.rotationPitch == -90) pitch = mc.thePlayer.rotationPitch;
    		double motionX = -MathHelper.sin((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
    		double motionY = -MathHelper.sin((float) (pitch * 0.017453292f));
    		double motionZ = MathHelper.cos((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
    		
    		mc.thePlayer.setVelocity(motionX * speed, motionY * speed, motionZ * speed);
		} else {
			speed = 1;
		}
	}
}
