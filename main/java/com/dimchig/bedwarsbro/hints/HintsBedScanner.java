package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.MyChatListener;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class HintsBedScanner {
	
	public static void scanBed() {
    	EntityPlayer mod_player = Minecraft.getMinecraft().thePlayer;
    	MovingObjectPosition ray = mod_player.rayTrace(100, 1.0f);
    	
    	if (ray == null) {
    		return;
    	} else {
    		int blockHitX = (int) ray.hitVec.xCoord;
    		int blockHitY = (int) ray.hitVec.yCoord;
    		int blockHitZ = (int) ray.hitVec.zCoord;

        	String prefix = MyChatListener.PREFIX_HINT_BED_SCANNER;
        	//CustomScoreboard.updateScoreboard();
        	ArrayList<BWBed> beds = findBeds(blockHitX, blockHitY, blockHitZ);
        	if (beds == null || beds.size() == 0) {
        		return;
        	} else {
        		//get closest bed
        		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        		
        		BWBed bed = null;
        		int min_dist = 999999;
        		for (BWBed b: beds) {
        			int dist = (int)Math.sqrt(Math.pow(b.part1_posX - blockHitX, 2) + Math.pow(b.part1_posZ - blockHitZ, 2));
        			if (dist < min_dist) {
        				min_dist = dist;
        				bed = b;
        			}
        		}
        		
        		if (bed == null) {
        			ChatSender.addText(prefix + "&fКровать не найдена!");
        			return;
        		}
        		
        		String bed_analisys = bed.getAnalysis();
        		
        		if (Main.getConfigBool(CONFIG_MSG.BED_SCANNER_ANIMATION) == true) {
	        		try {
						bed.showLayers(Minecraft.getMinecraft().theWorld);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        		
        		ChatSender.addText(prefix + bed_analisys);
        	}
    	}
    }
    
    public static ArrayList<BWBed> findBeds(int rayPosX, int rayPosY, int rayPosZ) {
    	try {
    		
    		ArrayList<BWBed> beds_parts = new ArrayList<BWBed>(); 
    		Entity mod_player = Minecraft.getMinecraft().thePlayer;
    		
    		int range = 30; 
    		boolean bed_level = false;
    		int cnt = 0;
    		for (int yi = -range; yi < range; yi++) {
    			for (int xi = -range; xi < range; xi++) {
    				for (int zi = -range; zi < range; zi++) {
    					
    					int bx = rayPosX + xi;
    					int by = rayPosY + yi;
    					int bz = rayPosZ + zi;
    					cnt++;
    					Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(bx, by, bz)).getBlock();
    					/*for (BlockSnapshot block: blocks) {
    						str += "&7(" + block.pos.getX() + ", " + block.pos.getX() + ", " + block.pos.getX() + ") &7-> &b" + block.blockIdentifier + "\n";
    					}*/
    					
    					if (block != null && block != null) {
    						if (block.getUnlocalizedName().substring(5).equals("bed")) {
    							bed_level = true;
    							
    							beds_parts.add(new BWBed(bx, by, bz, 0, 0, 0));
    						}
    					}
    				}
    			}
    			if (bed_level == true) break;
    		}
    		
    		
   
    		ArrayList<BWBed> beds = new ArrayList<BWBed>(); 
        	//group beds
    		int cnt_prevent_loop = 0;
    		while (beds_parts.size() > 0) {
    			cnt_prevent_loop++;
    			if (cnt_prevent_loop > 1000) break;
	        	for (BWBed bed1: beds_parts) {
	        		boolean isBreak = false;
	        		for (BWBed bed2: beds_parts) {
	        			if (bed1.part1_posX != bed2.part1_posX || bed1.part1_posY != bed2.part1_posY || bed1.part1_posZ != bed2.part1_posZ) {
	        				//different beds, count distance
	        				if (BWBed.isBlockConnectsToBlock(bed1.part1_posX, bed1.part1_posY, bed1.part1_posZ, bed2.part1_posX, bed2.part1_posY, bed2.part1_posZ)) {
	        					beds.add(new BWBed(bed1.part1_posX, bed1.part1_posY, bed1.part1_posZ, bed2.part1_posX, bed2.part1_posY, bed2.part1_posZ));       					
	        					beds_parts.remove(bed1);
	        					beds_parts.remove(bed2);
	        					isBreak = true;
	        					break;
	        				}
	        			}
	            	}
	        		if (isBreak) break;
	        	}	
    		}
    		
    		World world = Minecraft.getMinecraft().theWorld;
    		for (BWBed bed: beds) {    			
    			bed.scanDefence(world);
    		}

    		return beds;
    		
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
}
