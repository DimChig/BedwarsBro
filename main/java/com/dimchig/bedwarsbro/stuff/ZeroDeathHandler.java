package com.dimchig.bedwarsbro.stuff;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.dimchig.bedwarsbro.AutoWaterDrop;
import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.stuff.LightningLocator.MyLightning;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ZeroDeathHandler {
	static Minecraft mc;
	public static boolean isActive = false;
	
	public static int height_treshold = 10; //below bed by 20 blocks
	public static boolean fall_check = true; // >= fall distance
	public static double health_treshold = 5; // <= health
	public static boolean health_checker = true; // <= health
	public static boolean isWriteInChat = true;
	
	
	public static int MIN_TIME_INTERVAL = 9000; //9s
	
	public ZeroDeathHandler() {
		mc = Minecraft.getMinecraft();
		isActive = false;		
	}
	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.ZERO_DEATH);
		height_treshold = Main.getConfigInt(CONFIG_MSG.ZERO_DEATH_HEIGHT_TRESHOLD);
		fall_check = Main.getConfigBool(CONFIG_MSG.ZERO_DEATH_FALL_CHECK);		
		health_treshold = Main.getConfigDouble(CONFIG_MSG.ZERO_DEATH_HEALTH_TRESHOLD);
		health_checker = Main.getConfigBool(CONFIG_MSG.ZERO_DEATH_HEALTH_CHECK_NEARBY);
		isWriteInChat = Main.getConfigBool(CONFIG_MSG.ZERO_DEATH_WRITE_IN_CHAT);		
	}
	
	long last_time_rejoin = 0;
	
	double min_fall_speed = 0.7;
	
	double temp_val_1 = 0;
	double temp_val_2 = 0;
	double temp_val_3 = 0;
	
	public void scan() {
		
		if (mc == null || mc.thePlayer == null) return;

		/*if (temp_val_1 == 1) {
			temp_val_1 = 0;
			double damage = (20 - mc.thePlayer.getHealth());
			ChatSender.addText("&4Damage received = &c" + damage + "&7, &e" + Math.round(100 - damage / 36f * 100) + "%");
			
		}*/
		
		int bed_pos_y = 0;
		if (!mc.isSingleplayer()) {
			if (!isActive || !Main.chatListener.IS_IN_GAME) return;
			BWBed bed = Main.chatListener.GAME_BED;
			if (bed == null) return;
			bed_pos_y = bed.part1_posY;
			if (!isPlayerHasArmour()) return;
		}

		//height
		if (height_treshold != 999 && bed_pos_y - mc.thePlayer.posY >= height_treshold && mc.thePlayer.motionY < -min_fall_speed && !isWaterUnderMe()) {								
			rejoin(0, bed_pos_y - mc.thePlayer.posY);
			return;
		}
		
		//fall distance
		if (fall_check && !Main.autoWaterDrop.isWaterDropStarted && isDeadFromFall() && mc.thePlayer.motionY < -min_fall_speed && !isWaterUnderMe() && !isHoldingWaterBucket()) {
			rejoin(1, mc.thePlayer.fallDistance);
			return;
		}
		
		//damage
		if (health_treshold != 999 && mc.thePlayer.getHealth() <= health_treshold) {
			
			if (health_checker) {
				int nearby_call = isHealthChecker();
				if (nearby_call != 0) rejoin(2, mc.thePlayer.getHealth(), nearby_call);
				return;
			}
			
			rejoin(2, mc.thePlayer.getHealth());
			return;
		}
	}
	
	@SubscribeEvent
	public void onDamage(LivingHurtEvent e) {
		//temp_val_1 = 1;
	}

	public double calculateCurrentFallDamage() {
		double fall_damage = mc.thePlayer.fallDistance;
		if (fall_damage <= 3) return 0;
		
		
		double epf = getInventoryEPF(); //sum of enchantments from table

		double armour_defence = epf * 2;
		fall_damage = (1 - armour_defence / 100f) * (mc.thePlayer.fallDistance - 2);
		
		//jump boost
		Collection<PotionEffect> pe = mc.thePlayer.getActivePotionEffects();
		if (pe.size() > 0) {
			for (PotionEffect effect: pe) {
				if (effect.getPotionID() == 8) {
					fall_damage -= effect.getAmplifier();
					break;
				}
			}
		}
		
		//ChatSender.addText("Armour defence = &b" + armour_defence + "%");
		return fall_damage;
	}
	
	public double getInventoryEPF() {
		//levl 1, 2, 3 same, level 4 = 5
		
		if (mc.thePlayer.getInventory() == null) return 0;
		int eps_total = 0;
		try {
			for (int i = 0; i < 4; i++) {
				ItemStack itemStack =  mc.thePlayer.getInventory()[i];
				if (itemStack == null || itemStack.getItem() == null) continue;
				Integer val = EnchantmentHelper.getEnchantments(itemStack).get(0);
				if (val == null) continue;
				eps_total += val;
				if (val == 4) eps_total += 1;
			}
		} catch (Exception ex) { return 0; }
		
		return eps_total;
	}
	
	public boolean isDeadFromFall() {
		double damage = calculateCurrentFallDamage();
		if (damage <= 0) return false;
		if (mc.thePlayer.getHealth() < damage) return true;
		return false;
	}
	
	public boolean isPlayerHasArmour() {
		if (mc.thePlayer.getInventory() == null || mc.thePlayer.getInventory()[1] == null) return false;
		return true;
	}
	
	public int isHealthChecker() {
		//check nearby players by 15 blocks
		int nearby_player_radius = 15;
		int nearby_projectile_radius = 10;
		
		List<EntityPlayer> entities = mc.theWorld.playerEntities;		
		for (EntityPlayer en: entities) {
			if (en == null || en.getName() == null|| en.getDisplayName() == null) continue;
			if (en.getTeam() == mc.thePlayer.getTeam() || en.getName().equals(mc.thePlayer.getName())) continue;
			double dist = en.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			if (dist < nearby_player_radius) {
				//ChatSender.addText("Located player &e" + en.getName() + " &fnear by &c" + (int)dist + " &fblocks");
				return 1;
			}
		}
		
		BlockPos minPos = new BlockPos(mc.thePlayer.posX - nearby_projectile_radius, mc.thePlayer.posY - nearby_projectile_radius, mc.thePlayer.posZ - nearby_projectile_radius);
		BlockPos maxPos = new BlockPos(mc.thePlayer.posX + nearby_projectile_radius, mc.thePlayer.posY + nearby_projectile_radius, mc.thePlayer.posZ + nearby_projectile_radius);        
        AxisAlignedBB box = new AxisAlignedBB(minPos, maxPos);
        List<EntityArrow> arr_arrows = mc.theWorld.getEntitiesWithinAABB((Class)EntityArrow.class, box);
        if (arr_arrows != null && arr_arrows.size() > 0) {
        	for (EntityArrow en: arr_arrows) { 
        		Entity sender = en.shootingEntity;
        		if (sender == null || !(sender instanceof EntityPlayer) || sender == mc.thePlayer) continue;
        		if (en.posY - en.prevPosY == 0) continue;
        		//ChatSender.addText("Located &carrow &fnearby");
            	return 2;
        	}
        	
        }
        return 0;
	}
	
	public boolean isHoldingWaterBucket() {
		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
		if (is == null) return false;
		if (is.getItem() == Items.water_bucket) return true;
		return false;
	}
	
	public boolean isWaterUnderMe() {
		EntityPlayerSP player = mc.thePlayer;
		if (player == null) return false;
		if (player.posY < 10) return false;
		
		int px = (int)Math.floor(player.posX);
		int pz = (int)Math.floor(player.posZ);
		
		MutableBlockPos pos = new MutableBlockPos();
		World world = mc.theWorld;

		for (int i = (int)player.posY; i > 0; i--) {
			pos = pos.set(px, i,  pz);
			
			IBlockState state = world.getBlockState(pos);
			if (state == null) continue;
			Block b = state.getBlock();
			if (b == Blocks.water) return true;
			if (b != Blocks.air) return false;
		}
		return false;
	}
	
	public void rejoin(int reason, double val) {	
		rejoin(reason, val, 0);
	}	
	
	public void rejoin(int reason, double val, double extra_val) {		
		long t = new Date().getTime();
		if (t - last_time_rejoin < MIN_TIME_INTERVAL) return;
		last_time_rejoin = t;
		ChatSender.sendText("/leave");
		Main.myTickEvent.zeroDeathHandlerRejoinVar = 10;
		
		if (isWriteInChat) {
			String hover = "&fПричина: ";
			if (reason == 0) hover += "Падение ниже кровати на &e" + height_treshold + "&f блоков. В твоем случае было &c" + (int)(val) + "&f блок" + Main.chatListener.getNumberEnding((int)val, "", "а", "ов");
			else if (reason == 1) hover += "Падение с &cсмертельной высоты&f. Ты падал с высоты в &c" + (int)(val) + "&f блок" + Main.chatListener.getNumberEnding((int)val, "", "а", "ов");
			else if (reason == 2) {
				hover += "количество здоровья <= &e" + health_treshold + "&f. В твоем случае было &c" + new DecimalFormat("0.0").format(val) + "&f здоровья";
				if (extra_val == 1) hover += " и рядом был &cигрок";
				if (extra_val == 2) hover += " и рядом летела &cстрела";
				if (extra_val == 3) hover += " и рядом летел &cфаербол";
			}
			else hover += "&cНеизвестна...";
			
			Main.chatListener.PREFIX_ZERO_DEATH = "&r&a&lZero&c&lDeath&8 ▸ §r";
			ChatSender.addHoverText(Main.chatListener.PREFIX_ZERO_DEATH + "&fПерезаход...", hover);
		}	
	}
}
