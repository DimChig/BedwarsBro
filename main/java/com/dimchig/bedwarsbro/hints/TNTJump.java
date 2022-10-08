package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.plaf.basic.BasicSplitPaneUI.KeyboardUpLeftHandler;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TNTJump extends Gui {
	
	private final DecimalFormat timeFormatter = new DecimalFormat("0.0");
	private static EntityTNTPrimed tntForJump = null;
	private static int tntForJumpState = -1;
	private float TIME_STRAIGHT_FOR_RUN = 1.1f;
	private float TIME_STRAIGHT_FOR_JUMP = 0.5f;
	private float TIME_DIAGONAL_FOR_RUN = 1.2f;
	private float TIME_DIAGONAL_FOR_JUMP = 0.4f;
	private float TIME_UP_FOR_JUMP = 0.05f;
	
	private float TIME_FOR_RUN = TIME_STRAIGHT_FOR_RUN;
	private float TIME_FOR_JUMP = TIME_STRAIGHT_FOR_JUMP;
	private int unpress_keys_counter = -1;
	
	private double max_height = -1; 
	private int last_fuse = -1;

	public void draw(Vec3 playerPos, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
	
		
		//player.setPosition(player.posX - 0.004, player.posY, player.posZ - 0.004);
		if (unpress_keys_counter >= 0) {
			unpress_keys_counter--;
			
			
			if (unpress_keys_counter == 0) {
				pressUpKey(Minecraft.getMinecraft().gameSettings.keyBindJump);
				pressUpKey(Minecraft.getMinecraft().gameSettings.keyBindSprint);
				unpress_keys_counter = -1;
			}
		}
		
		
		
		List<EntityTNTPrimed> entities = mc.theWorld.getEntities(EntityTNTPrimed.class, 
				new Predicate<EntityTNTPrimed>() {
					@Override
					public boolean apply(EntityTNTPrimed input) {
						return true;
					}
				});
		
		if (entities == null || entities.size() == 0) return;
		for (EntityTNTPrimed en: entities) {
			//TNTRenderer
			
			
			
			String text = "" + formatTNTTime((en.fuse - partialTicks) / 20f);
			
			double x = en.posX;
			double y = en.posY + en.height + 0.7;
			double z = en.posZ;
	        float green = Math.min(en.fuse / 50f, 1f);
	        Color color = new Color(1f - green, green, 0);
	        
	        Main.draw3DText.drawText(playerPos, new Vec3(x, y, z), player, text, color.getRGB());
	        
	        if (en.fuse != last_fuse) {

		        if ((last_fuse == -1 && en.fuse > 70) || (en.fuse > 0 && en.fuse % 20 == 0)) {
		        	//ChatSender.addText("" + en.fuse);
		        	float pitch = 1.5f - en.fuse / 20f * 0.2f;
		        	Minecraft.getMinecraft().theWorld.playSound(x, y, z, "note.hat", 1.0f, pitch, false);
		        }
		        
		        if (en.fuse == 0) {
		        	last_fuse = -1;
		        }
		        
		        last_fuse = en.fuse;
	        }
	        
			
		}
		
		
		if (tntForJump != null) {
			if (tntForJumpState == 0) {
				
				
				
				
				//no ->  10.11
				//0 ->   01.249
				//0.025 --
				//0.04---
				//0.045
				//0.05 -> 10.5724006
				//0.1 -> 10.3614975
				//0.2 -> 10.1144039
				//0.3 -> 10.0696853
				//0.4 -> 9.238633
				//0.5 -> 8.60
				
				//slightly move player closer to tnt
				double dX = player.posX - tntForJump.posX;
				double dZ = player.posZ - tntForJump.posZ;
				double angle = Math.toDegrees(Math.atan2(dZ, dX));
				double new_angle = 0;
				int[] angles = new int[] {-180, -135, -90, -45, 0, 45, 90, 135, 180};
				double min_dist = 9999;
				for (int a: angles) {
					double d = Math.abs(angle - a);
					if (d < min_dist) {
						min_dist = d;
						new_angle = a;
					}
				}
				//ChatSender.addText(angle + ", &e" + new_angle);
				
				angle = Math.toRadians(new_angle);
				double distance = 3;
				
				if (tntForJump.getDistance(player.posX, tntForJump.posY, player.posZ) <= 1) {
					distance = 0;
					angle = 0;
				}
				
				
				double new_x = tntForJump.posX + distance * Math.cos(angle); 
				double new_z = tntForJump.posZ + distance * Math.sin(angle);
				
				//move player to it slightly
				float speed = 0.004f;
				double dirX = player.posX < new_x ? 1 : -1;
				double dirZ = player.posZ < new_z ? 1 : -1;
				
				
				double dist = tntForJump.getDistance(player.posX, tntForJump.posY, player.posZ);
				
				if (dirX != 0 || dirZ != 0 || dist != distance) {	
					player.setPosition(player.posX + dirX * speed, player.posY, player.posZ + dirZ * speed);
				}
				
				if (Math.abs(player.posX - new_x) <= speed && Math.abs(player.posZ - new_z) <= speed) {
					player.setPosition(new_x, player.posY, new_z);
					dirX = 0;
					dirZ = 0;
				}
				
				TIME_FOR_RUN = TIME_STRAIGHT_FOR_RUN;
				TIME_FOR_JUMP = TIME_STRAIGHT_FOR_JUMP;
				if (new_angle % 90 != 0) {
					TIME_FOR_RUN = TIME_DIAGONAL_FOR_RUN;
					TIME_FOR_JUMP = TIME_DIAGONAL_FOR_JUMP;
				} 
				if (distance == 0) {
					//ChatSender.addText("UP");
					TIME_FOR_RUN = -1;
					TIME_FOR_JUMP = TIME_UP_FOR_JUMP;
				} else {
					lookAtNearestTNT();
				}
				
				
				if (distance == 0) {
					if (tntForJump.fuse / 20f <= TIME_FOR_JUMP) {
						pressDownKey(Minecraft.getMinecraft().gameSettings.keyBindJump);
						tntForJumpState = -1;
						tntForJump = null;
						unpress_keys_counter = 300;
						max_height = player.posY;
					}
				} else if (tntForJump.fuse / 20f < TIME_FOR_RUN) {
					dist = tntForJump.getDistance(player.posX, tntForJump.posY, player.posZ);
					if (Math.abs(dist - distance) > 0.1) {
						ChatSender.addText(MyChatListener.PREFIX_TNT_JUMP + "&cПлохой тайминг");
						tntForJumpState = -1;
						tntForJump = null;
						return;
					}
					//ChatSender.addText("&aGO!");
					pressDownKey(Minecraft.getMinecraft().gameSettings.keyBindSprint);
					pressDownKey(Minecraft.getMinecraft().gameSettings.keyBindForward);
					
					tntForJumpState = 1;
				}
			} else if (tntForJumpState == 1) {
				if (tntForJump.fuse / 20f < TIME_FOR_JUMP) {
					pressDownKey(Minecraft.getMinecraft().gameSettings.keyBindJump);
					tntForJumpState = -1;
				}
			} else if (tntForJumpState == -1) {
				//pressUpKey(Minecraft.getMinecraft().gameSettings.keyBindJump);
				tntForJump = null;
				unpress_keys_counter = 80;
			}
		}
		
	}
	
	void pressDownKey(KeyBinding key) {
		key.setKeyBindState(key.getKeyCode(), true);
	}
	
	void pressUpKey(KeyBinding key) {
		key.setKeyBindState(key.getKeyCode(), false);
	}
	
	public static void lookAtNearestTNT() {
		List<EntityTNTPrimed> entities = Minecraft.getMinecraft().theWorld.getEntities(EntityTNTPrimed.class, 
				new Predicate<EntityTNTPrimed>() {
					@Override
					public boolean apply(EntityTNTPrimed input) {
						return true;
					}
				});
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (entities == null || entities.size() == 0) return;
		EntityTNTPrimed tnt = null;
		double min_dist = 999999;
		for (EntityTNTPrimed en: entities) {
			double x = en.posX;
			double y = en.posY;
			double z = en.posZ;
			double dist = en.getDistance(player.posX, player.posY, player.posZ);
			if (dist < min_dist) {
				min_dist = dist;
				tnt = en;
			}
		}
		if (tnt == null) return;
		
		HintsFinder.lookAtPlayer(player.posX, player.posY + player.getEyeHeight(), player.posZ, tnt.posX, tnt.posY, tnt.posZ);
		tntForJump = tnt;
		tntForJumpState = 0;
	}
	
	String formatTNTTime(float time) {
		String str = "";
		if (time < 0) time = 0;
		return timeFormatter.format(time);
	}
}
