package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemArmourLevel;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemColor;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemToolLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HintsPlayerScanner {
	public HintsPlayerScanner() {

	}
	
	public static class BWPlayer {
		public EntityPlayer en;
		public String name;
		public BWItem item_in_hand;
		public int item_in_hand_amount;
		public BWItemArmourLevel armourLevel;
		public double posX;
		public double posY;
		public double posZ;
		public double distToPlayer;
		public TEAM_COLOR team_color;
		
		public BWPlayer(EntityPlayer en, String name, BWItem item_in_hand, int item_in_hand_amount, BWItemArmourLevel armourLevel, double posX, double posY, double posZ, double distToPlayer) {
			this.en = en;
			this.name = name;
			this.item_in_hand = item_in_hand;
			this.item_in_hand_amount = item_in_hand_amount;
			this.armourLevel = armourLevel;
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.distToPlayer = distToPlayer;
			
			team_color = TEAM_COLOR.NONE;
		}
	}
	
	
	public static ArrayList<BWPlayer> scanPlayers() {
		try {
			ArrayList<BWPlayer> players = new ArrayList<BWPlayer>();
			
            Entity mod_player = Minecraft.getMinecraft().thePlayer;
            ArrayList<EntityPlayer> entities = (ArrayList<EntityPlayer>) Minecraft.getMinecraft().theWorld.playerEntities;
            for (EntityPlayer en: entities) {	
            	try {
	            	double posX = en.posX;
	            	double posY = en.posY;
	            	double posZ = en.posZ;	
	            	double dist = Math.sqrt(Math.pow(mod_player.posX - posX, 2) + Math.pow(mod_player.posZ - posZ, 2));
	            	
	            	BWPlayer player = new BWPlayer(en, en.getName(), null, 0, BWItemArmourLevel.NONE, posX, posY, posZ, dist);
	            	player.team_color = MyChatListener.getEntityTeamColor(en);
	            	//item 	
	            	setPlayerHoldingItem(en, player);
	            	//armour
	            	player.armourLevel = getPlayerArmourLevel(en);
	            	
	            	players.add(player);
	            	//if (entities.size() > 40) continue;   	
            	} catch (Exception ex) {
            		//ChatSender.addText(ex.toString());
            	}
            	
            }
            
            /*String str = "\n\n\n\n\n&fFound " + players.size() + " players!\n";
            for (BWPlayer p: players) {
            	
            	str += "&d" + p.name + " &7(" + (int)p.posX + ", " + (int)p.posY + ", " + (int)p.posZ + ") => &9" + (int)p.distToPlayer + "&7:\n";
            	str += "&bItem: &r";
            	if (p.item_in_hand == null) {
					str += "&cNULL";
				} else {
    				str += "&e" + p.item_in_hand.type + "&7";
    				if (p.item_in_hand.color != BWItemColor.NONE) {
    					str += ", " + p.item_in_hand.color;
    				}
    				if (p.item_in_hand.toolLevel != BWItemToolLevel.NONE) {
    					str += ", " + p.item_in_hand.toolLevel;
    				}
    				if (p.item_in_hand.armourLevel != BWItemArmourLevel.NONE) {
    					str += ", " + p.item_in_hand.armourLevel;
    				}
				}
            	str += " &7x&b" + p.item_in_hand_amount;
            	
            	str += "\n&aArmour: &r";
            	if (p.armourLevel == BWItemArmourLevel.LEATHER) {
    				str += "&cLeather";
    			} else if (p.armourLevel == BWItemArmourLevel.CHAIN) {
    				str += "&7CHAIN";
    			} else if (p.armourLevel == BWItemArmourLevel.IRON) {
    				str += "&fIRON";
    			} else if (p.armourLevel == BWItemArmourLevel.DIAMOND) {
    				str += "&bDIAMOND";
    			} else {
    				str += "&dUNKNOWN";
    			}
            	str += "\n\n";
            }*/
            
            return players;
    	} catch (Exception ex) {
			//ChatSender.addText(ex.toString());
		}
		return new ArrayList<BWPlayer>();
	}
	
	public static void setPlayerHoldingItem(EntityPlayer en, BWPlayer player) {
		if (en.getInventory() != null) {
    		
    		ItemStack item = en.inventory.getCurrentItem();
    		if (item != null) {
    			String displayer_name = item.getDisplayName();
				String local_name = item.getUnlocalizedName().substring(5);
				
				
				player.item_in_hand = BWItemsHandler.findItem(local_name, displayer_name);
				player.item_in_hand_amount = item.stackSize;
    		}
    	}
		return;
	}
	
	public static BWItemArmourLevel getPlayerArmourLevel(EntityPlayer en) {
		
		if (en.getInventory().length == 0) return BWItemArmourLevel.NONE;
		ItemStack itemStack = en.getInventory()[1];
		if (itemStack == null) {
			return BWItemArmourLevel.NONE;
		} else {
			String displayer_name = itemStack.getDisplayName();
			String local_name = itemStack.getUnlocalizedName().substring(5);
			BWItem item_armour = BWItemsHandler.findItem(local_name, displayer_name);
			
			return item_armour.armourLevel;
		}
	}
}
