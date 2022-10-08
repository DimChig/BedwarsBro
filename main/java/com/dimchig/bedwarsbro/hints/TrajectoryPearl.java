package com.dimchig.bedwarsbro.hints;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.nolegit.BowAimbot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrajectoryPearl {
	static Minecraft mc;
	public boolean isActive = false;
	
	public TrajectoryPearl() {
		mc = Minecraft.getMinecraft();
		updateBooleans();
	}
	
	public void updateBooleans() {
		isActive = Main.getConfigBool(CONFIG_MSG.PEARL_PREDICTION);
	}
	
	public static double gravity = BowAimbot.gravity;
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (!isActive) return;
		
		EntityPlayerSP player = mc.thePlayer;
		float player_yaw = ((player.rotationYaw % 360) + 360) % 360;
		float player_pitch = player.rotationPitch;

        Vec3 playerPos = getVectorWithPartialTicks(player, event.partialTicks);
        
        ArrayList<Vec3> positions = new ArrayList<Vec3>();
        
        

        
        
        
        GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);  
		GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(3.0f);
		
		
		
		
		List<EntityEnderPearl> pearls = mc.theWorld.getEntities(EntityEnderPearl.class, EntitySelectors.selectAnything);
		if (pearls != null && pearls.size() > 0) {
			for (EntityEnderPearl pearl: pearls) {
			
				
				ArrayList<MyPoint> pearl_points = getHitTrajectoryPearl(mc.theWorld, pearl);
				if (pearl_points.size() > 10) {
					MyPoint point = pearl_points.get(pearl_points.size() - 1);
					Vec3 pos = point.pos;
					double c = 0.1;
					double hitX = Math.floor(pos.xCoord);
					double hitY = Math.floor(pos.yCoord);
					double hitZ = Math.floor(pos.zCoord);
					GL11.glColor4f(0f, 1f, 1f, 1f);		
					Main.playerFocus.drawBox(hitX, hitY, hitZ, hitX + 1, hitY + 1, hitZ + 1);
					
					double dist = Math.sqrt(Math.pow(playerPos.xCoord - hitX - 0.5, 2) + Math.pow(playerPos.zCoord - hitZ - 0.5, 2));
					if (dist < 20) {						
						String text = new DecimalFormat("0.0").format(point.step_cnt / 150f);
						GL11.glTranslated(playerPos.xCoord, playerPos.yCoord, playerPos.zCoord);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						Main.draw3DText.drawText(playerPos, new Vec3(hitX + 0.5, hitY + 2, hitZ + 0.5), player, text, new Color(0f, 1f, 1f, 1f).getRGB());
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
					}
					
					GL11.glColor4f(0f, 0.5f, 1f, 1f);
					Vec3 prev_p = null;
					for (MyPoint p: pearl_points) {
						if (prev_p == null) {
							prev_p = p.pos;
							continue;
						}
						
						Main.playerFocus.drawLineWithGL(prev_p, p.pos);
						prev_p = p.pos;
					}
				}
				
			}
		}
		
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.ender_pearl) {
			ArrayList<MyPoint> points = getHitTrajectory(player, mc.theWorld, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, playerPos);
			if (points.size() > 10) {
				MyPoint point = points.get(points.size() - 1);
				Vec3 pos = point.pos;
				
				pos = dropPointOnGround(pos);
				if (pos != null) {
					
					GL11.glColor4f(0f, 1f, 0f, 0.5f);
					double area = myMap(point.step_cnt, 0, 400, 0, 4);
					
					if (!checkHit(pos, (int)Math.ceil(area))) GL11.glColor4f(1f, 0f, 0f, 0.5f);
					
					double hitX = Math.floor(pos.xCoord);
					double hitY = Math.floor(pos.yCoord);
					double hitZ = Math.floor(pos.zCoord);
					
					//Main.playerFocus.drawBox(hitX, hitY, hitZ, hitX + 1, hitY + 2, hitZ + 1);
					Main.playerFocus.drawFilledSquare(hitX - area, hitY, hitZ - area, hitX + area + 1, hitY, hitZ + area + 1);
					Vec3 c1 = new Vec3(hitX - area, hitY, hitZ - area);
					Vec3 c2 = new Vec3(hitX - area, hitY, hitZ + area + 1);
					Vec3 c3 = new Vec3(hitX + area + 1, hitY, hitZ + area + 1);
					Vec3 c4 = new Vec3(hitX + area + 1, hitY, hitZ - area);
					Main.playerFocus.drawLineWithGL(c1, c2);
					Main.playerFocus.drawLineWithGL(c2, c3);
					Main.playerFocus.drawLineWithGL(c3, c4);
					Main.playerFocus.drawLineWithGL(c4, c1);
				}
			}
		}
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	public static class MyPoint {
		public Vec3 pos;
		public int step_cnt;
		
		public MyPoint(Vec3 pos, int step_cnt) {
			this.pos = pos;
			this.step_cnt = step_cnt;
		}
	}
	
	private double myMap(double value, double leftMin, double leftMax, double rightMin, double rightMax) {
		double leftSpan = leftMax - leftMin;
		double rightSpan = rightMax - rightMin;
		double valueScaled = (value - leftMin) / (leftSpan);
	    return rightMin + (valueScaled * rightSpan);
	}

	private ArrayList<MyPoint> getHitTrajectoryPearl(World world, EntityEnderPearl en) {
		ArrayList<MyPoint> points = new ArrayList<MyPoint>();

		double motionX = en.motionX;
		double motionY = en.motionY;
		double motionZ = en.motionZ;

		double gravity = 0.05000000074505806;
		
		double x = en.posX;
		double y = en.posY;
		double z = en.posZ;
		
        double drag = 1.0013;
        int MAX_ITERATIONS = 500;
        double percision = 0.1;
        double collider = 1;
        
        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
        for(int i = 0; i < MAX_ITERATIONS; i++) {
            pos.set((int)x, (int)y, (int)z);
            Vec3 vec = new Vec3(x, y, z);
            //positions.add(new Vec3(x, y, z));

            double lastX = x;
            double lastY = y;
    		double lastZ = z;

            motionY -= gravity * percision;

            motionX *= drag;
            motionZ *= drag;
            
            x += motionX * percision;
            y += motionY * percision;
            z += motionZ * percision;

            
            MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec, new Vec3(x, y, z));
            if (movingobjectposition != null) break;
            
            points.add(new MyPoint(vec, i));
        }
        return points;
	}
	
	private Vec3 dropPointOnGround(Vec3 pos) {
		MutableBlockPos mp = new MutableBlockPos((int)pos.xCoord, (int)Math.ceil(pos.yCoord), (int)pos.zCoord);
		for (int i = mp.getY(); i >= 0; i--) {
			mp.set(mp.getX(), i, mp.getZ());
			IBlockState state = mc.theWorld.getBlockState(mp);
			if (state == null || state.getBlock() == Blocks.air) continue;
			return new Vec3(pos.xCoord, i + 1, pos.zCoord);
		}
		return null;
	}
	
	private boolean checkHit(Vec3 pos, int area) {
		double hitX = Math.floor(pos.xCoord);
		double hitY = Math.floor(pos.yCoord);
		double hitZ = Math.floor(pos.zCoord);
	
		for (int i = -area; i <= area; i++) {
			for (int j = -area; j <= area; j++) {
				if (dropPointOnGround(new Vec3(hitX + i, hitY, hitZ + j)) == null) return false;
			}
		}
		
		
		return true;
	}		
	
	private ArrayList<MyPoint> getHitTrajectory(EntityPlayerSP player, World world, double player_yaw, double player_pitch, Vec3 player_pos) {
		
		ArrayList<MyPoint> points = new ArrayList<MyPoint>();
		
		double yaw = player_yaw;
		double pitch = player_pitch;
        double pitchWithOffset = player_pitch + 0f;
        
        double initialVelocity = 1.8;
        
		double motionX = -MathHelper.sin((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
		double motionY = -MathHelper.sin((float) (pitchWithOffset * 0.017453292f));
		double motionZ = MathHelper.cos((float) (yaw * 0.017453292f)) * MathHelper.cos((float) (pitch * 0.017453292f));
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
        int MAX_ITERATIONS = 500;
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

            MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec, new Vec3(x, y, z));
            if (movingobjectposition != null) break;
            
            points.add(new MyPoint(vec, i));
        }
        return points;
	}
	
	private Vec3 getVectorWithPartialTicks(Entity en, float partialTicks) {
		double x = en.prevPosX + (en.posX - en.prevPosX) * (double)partialTicks;
		double y = en.prevPosY + (en.posY - en.prevPosY) * (double)partialTicks;
		double z = en.prevPosZ + (en.posZ - en.prevPosZ) * (double)partialTicks;
		return new Vec3(x, y, z);
	}
}
