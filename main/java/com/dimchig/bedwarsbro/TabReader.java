package com.dimchig.bedwarsbro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;

public class TabReader {
	public static List<String> getTabNames() {
		
		ArrayList<String> arr = new ArrayList<String>();
		
		try {
	    	Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
	    	
	    	int cnt = 0;
	    	for (NetworkPlayerInfo info: players) { 	    		
	    		String name = Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(info).trim();
	    		arr.add(name);
	    		
	    		//avoid inf loop
	    		if (cnt > 10000) break;
	    		cnt += 1;
	    	}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return arr;
	}
}
