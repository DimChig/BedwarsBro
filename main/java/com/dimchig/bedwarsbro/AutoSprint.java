package com.dimchig.bedwarsbro;

import com.dimchig.bedwarsbro.stuff.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class AutoSprint {
	
	private Minecraft mc;
    private int sprintKeyBind;
    private boolean isAutoSprintActive = false;
    
    public AutoSprint() {
    	mc = Minecraft.getMinecraft();
    	updateBooleans();
    }
    
    public void updateBooleans() {
    	isAutoSprintActive = HintsValidator.isAutoSprintActive();
    	setSprint(isAutoSprintActive);
    }
	
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (isAutoSprintActive) {
			setSprint(true);
		}
    }
	
	public void setSprint(boolean state) {
		sprintKeyBind = Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode();
        KeyBinding.setKeyBindState(sprintKeyBind, state);
	}
}	
