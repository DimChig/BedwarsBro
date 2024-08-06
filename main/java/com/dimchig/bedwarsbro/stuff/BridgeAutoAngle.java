package com.dimchig.bedwarsbro.stuff;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class BridgeAutoAngle {
	public static float rotation_godbridge_pitch = 75f;
    public static boolean prop_show_chat_messages = true;
    public static String mod_prefix = "&6&lAutoAngle: &r";
    
    public static void aim() {
    	rotation_godbridge_pitch = (float)Main.getConfigDouble(CONFIG_MSG.BRIDGE_AUTOANGLE_PITCH);
    	prop_show_chat_messages = Main.getConfigBool(CONFIG_MSG.BRIDGE_AUTOANGLE_MESSAGES);
    	
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	
    	if (Math.abs(player.rotationPitch - rotation_godbridge_pitch) > 10) {
    		if (prop_show_chat_messages == true) {
    			ChatSender.addText(mod_prefix + "&cПосмотри на угол блока!");
    		}
    		return;
    	}
    	
    	float[] directions = new float[]{45, -45, 135, -135, 225, -225, 315, -315};
    	
    	float min_dist = 1000;
    	float direction = 0;
    	
    	for (float dir: directions) {
    		float dist = Math.abs(getPlayerYaw(player) - dir);
    		if (dist < min_dist) {
    			min_dist = dist;
    			direction = dir;
    		}
    	}
    	
    	if (direction == 0 && prop_show_chat_messages == true) {
    		return;
    	}
    	
    	setPlayerPitchAndYaw(player, direction, rotation_godbridge_pitch);
    	if (prop_show_chat_messages == true) {
    		int yaw = (int)direction % 180;
    		ChatSender.addText(mod_prefix + "&fУгол установлен в (&b" + yaw + ", " + rotation_godbridge_pitch + "&f)");
    	}
    }
    
    public static float getPlayerYaw(Entity player) {
    	return player.rotationYaw % 360;
    }
        
    
    public static void setPlayerPitchAndYaw(Entity player, float target_angle_yaw, float target_angle_pitch) {
    	float prev_rot_yaw = player.rotationYaw;
    	float prev_rot_pitch = player.rotationPitch;
        
        float angle_yaw = target_angle_yaw - prev_rot_yaw;
        float angle_pitch = target_angle_pitch - prev_rot_pitch;
        
        rotateAngles(player, angle_yaw, angle_pitch);
        
        double delta_yaw = player.rotationYaw - prev_rot_yaw;
        double delta_pitch  = player.rotationPitch - prev_rot_pitch;	
        
        if (target_angle_yaw != player.rotationYaw || target_angle_pitch != player.rotationPitch) {
        	 setPlayerPitchAndYaw(player, target_angle_yaw, target_angle_pitch);
        }
    }
    
    public static void rotateAngles(Entity player, float angle_yaw, float angle_pitch) {
    	 player.setAngles(angle_yaw / 0.15f, angle_pitch / -0.15f);        
    }
}
