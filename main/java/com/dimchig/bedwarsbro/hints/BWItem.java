package com.dimchig.bedwarsbro.hints;

import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemArmourLevel;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemColor;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemToolLevel;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemType;

public class BWItem {
	public String local_name;
	public String display_name;
	public BWItemType type;
	public BWItemToolLevel toolLevel;
	public BWItemColor color;
	public BWItemArmourLevel armourLevel;
	
	public BWItem(String local_name, String display_name, BWItemType type, BWItemToolLevel toolLevel, BWItemArmourLevel armourLevel) {
		this.local_name = local_name;
		this.display_name = display_name;
		this.type = type;
		this.toolLevel = toolLevel;
		this.armourLevel = armourLevel;
		
		this.color = BWItemColor.NONE;
	}
}