package com.dimchig.bedwarsbro.stuff;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;

public class RotateBind {
	static Minecraft mc;
	public boolean isActive = false;
	public int degrees = 180;
	public float speed = 1;
	public double startAngle = 0;
	public int iteration = 0;
	
	public RotateBind() {
		mc = Minecraft.getMinecraft();
	}
	
	public void updateBooleans() {
		degrees = Main.getConfigInt(CONFIG_MSG.ROTATE_BIND_DEGREES);
		speed = (float)Main.getConfigDouble(CONFIG_MSG.ROTATE_BIND_SPEED) * 10;		
	}
	
	public void rotate() {
		//isActive = false;
		if (!isActive) return;


		if (iteration < speed) {
			BridgeAutoAngle.setPlayerPitchAndYaw(mc.thePlayer, mc.thePlayer.rotationYaw + degrees / speed, mc.thePlayer.rotationPitch);
			iteration++;
		} else {
			BridgeAutoAngle.setPlayerPitchAndYaw(mc.thePlayer, (float)(startAngle + degrees), mc.thePlayer.rotationPitch);
			isActive = false;
		}
	}
	
	public void startRotate() {
		
		float yaw = mc.thePlayer.rotationYaw;
		if (speed == 0) {
			BridgeAutoAngle.setPlayerPitchAndYaw(mc.thePlayer, yaw + degrees, mc.thePlayer.rotationPitch);
		} else {
			startAngle = yaw;
			iteration = 0;
			isActive = true;
		}
	}
}
