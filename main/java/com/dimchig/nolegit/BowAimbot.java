package com.dimchig.nolegit;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.hints.HintsFinder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import static java.lang.Math.*;

import java.awt.Color;

public class BowAimbot {
	
	//Привет челик, который декомпилировал код! Зацени мой BowAimbot с предикшмном. Только у меня не очень получилось и тут много говнокода, не судите строго:
	
	static Minecraft mc;
	public static boolean isActive;
	public static boolean isToggle = false;
	public static boolean isDrawActive;
	
	public BowAimbot() {
		mc = Minecraft.getMinecraft();
	}
	
	//public static ArrayList<Vec3> trail = new ArrayList<Vec3>();
	public static ArrayList<Double> avg = new ArrayList<Double>();
	public static Vec3 last_movement_vector = new Vec3(0, 0, 0);
	public static double last_predict_ticks = 0;
	public static double prefict_delta = 0.05;
	public static ArrayList<Vec3> temp_arr = new ArrayList<Vec3>();
	public static double gravity = 0.00625;
	
	static Color normal_hitbox = new Color(1f, 1f, 1f, 1f);
	static Color predict_hitbox = new Color(1f, 0f, 0f, 1f);
	static Color predict_target = new Color(0f, 1f, 0f, 1f);
	static Color arrow_collider = new Color(0f, 1f, 0f, 1f);
	
	public void toggle() {
		isActive = !isActive;		
	}
	
	public void toggleDraw() {
		isDrawActive = !isDrawActive;		
	}
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!isActive) return;
		try {
			EntityPlayerSP player = mc.thePlayer;
			float player_yaw = ((player.rotationYaw % 360) + 360) % 360;
			float player_pitch = player.rotationPitch;

	        Vec3 playerPos = getVectorWithPartialTicks(player, event.partialTicks);
	        
	        ArrayList<Vec3> positions = new ArrayList<Vec3>();
	        
	        if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.bow) {
	        	isToggle = false;
	        	return;
	        }

            
	        double timeLeft = player.getItemInUseCount();
	        
	        if (timeLeft < 70000) {
	        	isToggle = false;
	        	return;
	        }
	        
	        if (Mouse.isButtonDown(0)) isToggle = true;
	        
	        if (!isToggle) return;
	        
	        double charge = Items.bow.getMaxItemUseDuration(player.getCurrentEquippedItem()) - timeLeft;	        
	        //get closest entity
	        List<EntityPlayer> villagers = mc.theWorld.getEntities(EntityPlayer.class, EntitySelectors.selectAnything);
			
			if (villagers == null || villagers.size() == 0) return;
			EntityPlayer closestPlayer = getClosestPlayer(player, playerPos, event.partialTicks);
			if (closestPlayer == null) return;
			Vec3 closestPlayerPos = getVectorWithPartialTicks(closestPlayer, event.partialTicks);
			
			//calculate angle
			double dX = playerPos.xCoord - closestPlayerPos.xCoord;
			double dY = playerPos.yCoord - closestPlayerPos.yCoord;
			double dZ = playerPos.zCoord - closestPlayerPos.zCoord;
			double t_yaw = myMap(Math.atan2(dZ, dX), -Math.PI, Math.PI, -180, 180);
			double t_pitch = myMap((Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI), Math.PI, Math.PI*2, 90, -90);
	        
            
            GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);  
			GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GlStateManager.disableBlend();
			GL11.glLineWidth(1.0f);
            
			
			if (isDrawActive) {
				List<EntityArrow> arrows = mc.theWorld.getEntities(EntityArrow.class, EntitySelectors.selectAnything);
				if (arrows != null && arrows.size() > 0) {
					setLineColor(arrow_collider);
					for (EntityArrow en: arrows) {
						Vec3 pos = getVectorWithPartialTicks(en, event.partialTicks);
						double c = 0.1;
						Main.playerFocus.drawBox(pos.xCoord - c, pos.yCoord - c, pos.zCoord - c, pos.xCoord + c, pos.yCoord + c, pos.zCoord + c);
					}
				}
			}
			
			//draw player outline		
			if (!Main.playerFocus.STATE) {
				setLineColor(normal_hitbox);
				AxisAlignedBB box = closestPlayer.getEntityBoundingBox();
				drawBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
			}
			
			Vec3 target_pos = new Vec3(closestPlayerPos.xCoord, closestPlayerPos.yCoord + closestPlayer.height / 2, closestPlayerPos.zCoord);
			
			ArrayList<MyHit> hits = new ArrayList<MyHit>();
			for (int i = -20; i <= 20; i++) {
				float new_pitch_delta = i/1f;
				MyHit hit = getHitTrajectory(player, mc.theWorld, t_yaw + 90, t_pitch + new_pitch_delta, playerPos, target_pos);
				
				if (hit == null) continue;
				hits.add(hit);
			}
			
			if (hits.size() > 0) {
				MyHit hit = hits.get(hits.size() / 2);
				Vec3 pos = hit.pos;
				double c = 0.4;
				double hit_length = (hit.step_cnt * 0.1);

				//get player speed
				double collider = 2;
				BlockPos minPos = new BlockPos(hit.pos.xCoord - collider, hit.pos.yCoord - collider, hit.pos.zCoord - collider);
	            BlockPos maxPos = new BlockPos(hit.pos.xCoord + collider, hit.pos.yCoord + collider, hit.pos.zCoord + collider);
	            AxisAlignedBB box = new AxisAlignedBB(minPos, maxPos);
	            List<EntityPlayer> vs = mc.theWorld.getEntitiesWithinAABB((Class)EntityPlayer.class, box);
	            if (vs == null || vs.size() == 0) return;
	            EntityPlayer villager = vs.get(0);
	            if (villager == null) return;
	            
	            double predict_ticks_extender = 4;
	            
	            double speedX = villager.posX - villager.prevPosX;
	            double speedY = villager.posY - villager.prevPosY;
	            double speedZ = villager.posZ - villager.prevPosZ;
	            double predict_ticks = hit_length + predict_ticks_extender;
	            speedY = 0;
	            
	            prefict_delta = 0.05;
	            
	            last_predict_ticks += (predict_ticks - last_predict_ticks) * prefict_delta;
	            predict_ticks = last_predict_ticks;
	            
	            if (charge < 20) {
	            	predict_ticks += (20 - charge) * 0.1;
	            }
	            
	            double min_ticks = 5;
	            if (predict_ticks < min_ticks) predict_ticks = min_ticks;
            	//ChatSender.addText("ticks &a" + predict_ticks);
	            //ChatSender.addText("speed Y = &b" + speedY);
	            
	            setLineColor(predict_hitbox);
	
				Vec3 predict_pos = new Vec3(closestPlayerPos.xCoord, closestPlayerPos.yCoord + villager.height / 2, closestPlayerPos.zCoord);
				Vec3 movement_vector = new Vec3(speedX * predict_ticks, speedY * predict_ticks, speedZ * predict_ticks);
				double dfx = last_movement_vector.xCoord + (movement_vector.xCoord - last_movement_vector.xCoord) * prefict_delta;
				double dfy = last_movement_vector.yCoord + (movement_vector.yCoord - last_movement_vector.yCoord) * prefict_delta;
				double dfz = last_movement_vector.zCoord + (movement_vector.zCoord - last_movement_vector.zCoord) * prefict_delta;
				Vec3 vec_difference = new Vec3(dfx, dfy, dfz);
				predict_pos = predict_pos.add(vec_difference);
				last_movement_vector = vec_difference;
				drawBox(predict_pos.xCoord - villager.width/2, predict_pos.yCoord - villager.height / 2, predict_pos.zCoord - villager.width/2,
						                 predict_pos.xCoord + villager.width/2, predict_pos.yCoord + villager.height / 2, predict_pos.zCoord + villager.width/2);
				
				
				
				//calculate angle
				dX = playerPos.xCoord - predict_pos.xCoord;
				dY = playerPos.yCoord - predict_pos.yCoord;
				dZ = playerPos.zCoord - predict_pos.zCoord;
				t_yaw = myMap(Math.atan2(dZ, dX), (float)-Math.PI, (float)Math.PI, -180, 180);
				t_pitch = myMap(Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI, (float)Math.PI, (float)Math.PI*2, 90, -90);
				
				
				
				//calculate actual hit
				hits = new ArrayList<MyHit>();
				for (int i = -20; i <= 20; i++) {
					float new_pitch_delta = i/1f;
					hit = getHitTrajectory(player, mc.theWorld, t_yaw + 90, t_pitch + new_pitch_delta, playerPos, predict_pos);
					
					if (hit == null) continue;
					hits.add(hit);
				}
				if (hits.size() > 0) {
					hit = hits.get((int)(hits.size() / 2));
					pos = hit.pos;
					c = 0.1;
					
					setLineColor(predict_target);
					drawBox(pos.xCoord - c, pos.yCoord - c, pos.zCoord - c, pos.xCoord + c, pos.yCoord + c, pos.zCoord + c);
					
					KeyBinding key = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
					if (charge > 7000) {
						//key.setKeyBindState(key.getKeyCode(), true);
					} else {
						HintsFinder.rotateTo(Minecraft.getMinecraft().thePlayer, (float)hit.yaw, (float)hit.pitch);
					}
					
				}
				
			}
            
            
            
            GL11.glPopAttrib();
			GL11.glPopMatrix();
            
			
		} catch (Exception ex) { ex.printStackTrace(); }
	}
	
	private class MyHit {
		public Vec3 pos;
		public int step_cnt;
		public double yaw;
		public double pitch;
		
		public MyHit(Vec3 pos, int step_cnt, double player_yaw, double player_pitch) {
			super();
			this.pos = pos;
			this.step_cnt = step_cnt;
			this.yaw = player_yaw;
			this.pitch = player_pitch;
		}
	}
	
	private double getRealYaw(double yaw) {
		return ((yaw % 360) + 360) % 360;
	}
	
	private EntityPlayer getClosestPlayer(EntityPlayerSP mod_player, Vec3 mod_player_pos, float partialTicks) {
		List<EntityPlayer> players = mc.theWorld.getEntities(EntityPlayer.class, EntitySelectors.selectAnything);
		if (players == null || players.size() == 0) return null;
		
		double mod_player_yaw = getRealYaw(mod_player.rotationYaw);
		
		EntityPlayer closestPlayer = null;
		double dist = 1000;
		for (EntityPlayer en: players) {
			if (en == mod_player || en.getTeam() == mod_player.getTeam()) continue;
			//double d = en.getDistance(mod_player_pos.xCoord, mod_player_pos.yCoord, mod_player_pos.zCoord);
			
			
			Vec3 p_pos = getVectorWithPartialTicks(en, partialTicks);
			//if (p_pos.distanceTo(mod_player_pos) > 90) continue;
			
			double dX = mod_player_pos.xCoord - p_pos.xCoord;
			double dY = mod_player_pos.yCoord - p_pos.yCoord;
			double dZ = mod_player_pos.zCoord - p_pos.zCoord;
			double t_yaw = getRealYaw(myMap(Math.atan2(dZ, dX), -Math.PI, Math.PI, -180, 180) + 90);
			
			double d = Math.abs(mod_player_yaw - t_yaw); 
			
			if (d < dist) {
				dist = d;
				closestPlayer = en;
			}
		}
		return closestPlayer;
	}
	
	private double myMap(double value, double leftMin, double leftMax, double rightMin, double rightMax) {
		double leftSpan = leftMax - leftMin;
		double rightSpan = rightMax - rightMin;
		double valueScaled = (value - leftMin) / (leftSpan);
	    return rightMin + (valueScaled * rightSpan);
	}
	
	private Vec3 getVectorWithPartialTicks(Entity en, float partialTicks) {
		double x = en.prevPosX + (en.posX - en.prevPosX) * (double)partialTicks;
		double y = en.prevPosY + (en.posY - en.prevPosY) * (double)partialTicks;
		double z = en.prevPosZ + (en.posZ - en.prevPosZ) * (double)partialTicks;
		return new Vec3(x, y, z);
	}
	
	private MyHit getHitTrajectory(EntityPlayerSP player, World world, double player_yaw, double player_pitch, Vec3 player_pos, Vec3 target_pos) {
		
		double yaw = player_yaw;
		double pitch = player_pitch;
        double pitchWithOffset = player_pitch + 0f;
        
        double initialVelocity = 3f;
        double timeLeft = player.getItemInUseCount();
        double charge = Items.bow.getMaxItemUseDuration(player.getCurrentEquippedItem()) - timeLeft;
        double baseVelocity = Math.min(1, HintsFinder.myMap((float)charge, 0, 20, 0.03f, 1));        
        initialVelocity = baseVelocity * 3;
        
		double motionX = -MathHelper.sin((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
		double motionY = -MathHelper.sin((float) (pitchWithOffset * 0.017453292f));
		double motionZ = MathHelper.cos((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
		float length = (float)Math.sqrt((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= length;
        motionY /= length;
        motionZ /= length;
        motionX *= initialVelocity;
        motionY *= initialVelocity;
        motionZ *= initialVelocity;

        if(player.onGround) {
            motionY += (float)player.motionY;
        }
        motionX += player.motionX;
        motionZ += player.motionZ;
        double x = player_pos.xCoord;
		double y = (player_pos.yCoord + player.getEyeHeight() - 0.10000000149011612);
		double z = player_pos.zCoord;
		
        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
        
        double gravity = 0.05000000074505806;
        double drag = 0.99;
        int MAX_ITERATIONS = 300;
        double percision = 0.1;
        double collider = 1;
        
        for(int i = 0; i < MAX_ITERATIONS; i++) {
            pos.set((int)x, (int)y, (int)z);
            Vec3 vec = new Vec3(x, y, z);
            //positions.add(new Vec3(x, y, z));

            double lastX = x;
            double lastY = y;
    		double lastZ = z;

            motionY -= gravity * percision;

            //motionX *= drag;
            //motionZ *= drag;

            x += motionX * percision;
            y += motionY * percision;
            z += motionZ * percision;

            IBlockState iblockstate = world.getBlockState(pos);

            if (iblockstate != null && iblockstate.getBlock() != Blocks.air) {
            	return null;
            }
            
            if (target_pos.distanceTo(vec) > collider) continue; 
            
            return new MyHit(vec, i, player_yaw, player_pitch);
        }
        return null;
	}
	
	private void setLineColor(Color color) {
		if (!isDrawActive) return;
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1F);
	}
	
	public void drawBox(double x1, double y1, double z1, double x2, double y2, double z2) {		
		if (!isDrawActive) return;
		//copied from Main.playerFocus.drawBox(x1, y1, z1, x2, y2, z2);
		drawLineWithGL(new Vec3(x1, y1, z1), new Vec3(x2, y1, z1));
		drawLineWithGL(new Vec3(x2, y1, z1), new Vec3(x2, y1, z2));
		drawLineWithGL(new Vec3(x2, y1, z2), new Vec3(x1, y1, z2));
		drawLineWithGL(new Vec3(x1, y1, z2), new Vec3(x1, y1, z1));
		drawLineWithGL(new Vec3(x1, y2, z1), new Vec3(x2, y2, z1));
		drawLineWithGL(new Vec3(x2, y2, z1), new Vec3(x2, y2, z2));
		drawLineWithGL(new Vec3(x2, y2, z2), new Vec3(x1, y2, z2));
		drawLineWithGL(new Vec3(x1, y2, z2), new Vec3(x1, y2, z1));
		
		drawLineWithGL(new Vec3(x1, y1, z1), new Vec3(x1, y2, z1));
		drawLineWithGL(new Vec3(x1, y1, z2), new Vec3(x1, y2, z2));
		drawLineWithGL(new Vec3(x2, y1, z1), new Vec3(x2, y2, z1));
		drawLineWithGL(new Vec3(x2, y1, z2), new Vec3(x2, y2, z2));
	}
	
	public static void drawLineWithGL(Vec3 blockA, Vec3 blockB) {
		GL11.glBegin(GL11.GL_LINE_STRIP);		

		GL11.glVertex3d(blockA.xCoord, blockA.yCoord, blockA.zCoord);
		GL11.glVertex3d(blockB.xCoord, blockB.yCoord, blockB.zCoord);

		GL11.glEnd();
	}
	
	private Vec3 calcVelocityForArrow(double yaw, double pitch) {
		
		double power = 1;
		final double actualPower = power * 3;

		final double xVel = (-sin(toRadians(yaw))) * cos(toRadians(pitch));
		final double yVel = (-sin(toRadians(pitch)));
		final double zVel = cos(toRadians(yaw)) * cos(toRadians(pitch));

		return new Vec3(xVel, yVel, zVel).normalize();
	}
}
