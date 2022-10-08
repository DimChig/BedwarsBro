package com.dimchig.bedwarsbro.hints;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;

import net.minecraft.client.Minecraft;

public class FreezeClutch {
	private boolean isActive = false;
	private long time_last_freeze = 0;	
	private long time_next_freeze = 0;	
	private int total_steps = 0;	
	private int FREEZE_STEPS = 5;
	private float FREEZE_TIME = 5 * 1000f;
	private int UNFREEZE_TIME = 10;
	
	public FreezeClutch() {
		isActive = false;
		total_steps = 0;
	}
	
	public void startFreeze() {
		isActive = true;
		total_steps = 0;
		time_next_freeze = new Date().getTime();		
	}
	
	public void handle() {
		
		if (!isActive) return;		
		long t = new Date().getTime();
		if (t > time_next_freeze) {
			time_last_freeze = t;
			while (true) {
				t = new Date().getTime();
				if (t - time_last_freeze > FREEZE_TIME / FREEZE_STEPS) {
					total_steps += 1;
					ChatSender.addText("&bFreeze &8▸ &fИгра фризится &e" + (total_steps) + "&7/&f" + FREEZE_STEPS);
					if (total_steps >= FREEZE_STEPS || Minecraft.getMinecraft().thePlayer.onGround) {
						isActive = false;
						Main.chatListener.playSound("note.hat");
						
					} else if (total_steps < FREEZE_STEPS) {
						time_next_freeze = t + UNFREEZE_TIME;
					} 
					return;
				}
			}
		}
	}
}
