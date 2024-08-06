package com.dimchig.bedwarsbro;

import com.dimchig.bedwarsbro.stuff.HintsFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AutoWaterDrop {
	static Minecraft mc;
	
	public boolean isPressed = false;
	public boolean isWaterDropStarted = false;
	KeyBinding keyUse;
	private int delay_cnt = -1;
	
	public AutoWaterDrop() {
		mc = Minecraft.getMinecraft();
		isPressed = false;
		keyUse = mc.gameSettings.keyBindUseItem;
	}

	public void check(EntityPlayerSP player, Vec3 pos) {
	
		
		if (player.motionY < -1f) {
			
			//scan blocks under player
			World world = mc.theWorld;
			double dist = -1;
			for (int y = (int)pos.yCoord - 1; y > 0; y--) {
				if (world.getBlockState(new BlockPos((int)pos.xCoord, y, (int)pos.zCoord)).getBlock() != Blocks.air) {
					dist = pos.yCoord - y - 1;
					break;
				}
			}
			
			if (dist == -1) return;
			
			boolean hasWater = false;
			if (player.inventory != null && player.inventory.mainInventory != null) {
				if (player.inventory.getCurrentItem() == null || player.inventory.getCurrentItem().getItem() != Items.water_bucket) {
					for (int i = 0; i < player.inventory.getHotbarSize(); i++) {
						ItemStack stack = player.inventory.mainInventory[i];
						if (stack == null) continue;
						Item item = stack.getItem();
						if (item != null && item == Items.water_bucket) {
							if (dist < 20) player.inventory.currentItem = i;
							hasWater = true;
							break;
						}
					}
				} else if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Items.water_bucket) {
					hasWater = true;
				}
			}
			
			if (!hasWater) return;
			
			if (!isWaterDropStarted) {
				isWaterDropStarted = true;
				isPressed = false;
				ChatSender.addText(MyChatListener.PREFIX_WATER_DROP + "&fАктивирован");
			}
			
			
			if (dist < 10) {
				HintsFinder.rotateTo(player, player.rotationYaw, 90);
				
				MovingObjectPosition object = mc.objectMouseOver;
				if (object.typeOfHit == MovingObjectType.BLOCK && object.sideHit == EnumFacing.UP) {
					placeWater();
				}
				//isPressed = true;
				
				//mc.objectMouseOver
			}
			
		} else {
			
			if (mc.thePlayer.onGround && mc.thePlayer.motionY > -0.05) {
				//ChatSender.addText("mc.thePlayer.motionY = " + mc.thePlayer.motionY);
				if (isWaterDropStarted) {
					isWaterDropStarted = false;
					placeWater();
					isPressed = false;
				}
				
			}
			
		}
	}
	
	void placeWater() {
		//keyUse.setKeyBindState(keyUse.getKeyCode(), true);
		if (mc.thePlayer.getCurrentEquippedItem() == null) return;
		isPressed = true;
		mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
	}
	
	void pressUp() {
		//keyUse.setKeyBindState(keyUse.getKeyCode(), false);
	}
}
