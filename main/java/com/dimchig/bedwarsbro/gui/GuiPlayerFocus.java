package com.dimchig.bedwarsbro.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.gui.GuiMinimap.Pos;
import com.dimchig.bedwarsbro.gui.GuiMinimap.PosItem;
import com.dimchig.bedwarsbro.hints.BWBed;
import com.dimchig.bedwarsbro.hints.HintsFinder;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class GuiPlayerFocus {
	
	static Minecraft mc;
	public static boolean STATE = false;
	public static boolean isAutoWaterDropActive = false;
	public static boolean isInvulnerableTimerActive = false;
	public static boolean isInvulnerableTimerSoundsActive = false;
	public static boolean isNamePlateActive = false;
	public static boolean isNamePlateRainbowActive = false;
	public static boolean isResourcesHologramActive = false;
	public static int rainbowSpeed = 1;
	public static String namePlateCustomColor = "";
	
	public static boolean isT_Active = false;
	//private static Robot robot;
	
	public GuiPlayerFocus() {
		mc = Minecraft.getMinecraft();
		lines = new ArrayList<GuiPlayerFocus.MyLine>();
		updateBooleans();
		//try { robot = new Robot(); } catch (AWTException e) {}
	}
	
	public void updateBooleans() {
		this.isAutoWaterDropActive = HintsValidator.isAutoWaterDropActive();
		this.isInvulnerableTimerActive = HintsValidator.isInvulnerableTimerActive();
		this.isInvulnerableTimerSoundsActive = HintsValidator.isInvulnerableTimerSoundsActive();
		this.isNamePlateActive = HintsValidator.isNamePlateActive();
		this.isNamePlateRainbowActive = HintsValidator.isNamePlateRainbowActive();
		this.rainbowSpeed = HintsValidator.getRainbowSpeed();
		this.namePlateCustomColor = HintsValidator.getNamePlateCustomColor();
		this.isResourcesHologramActive = HintsValidator.isResourcesHologramActive();
	}
	
	private class MyLine {
		public Vec3 pos1;
		public Vec3 pos2;
		public Color color;
		public MyLine(Vec3 pos1, Vec3 pos2, Color color) {
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.color = color;
		}	
	}
	private ArrayList<MyLine> lines;
	
	private static boolean cflag = false;
	private static ArrayList<Long> cps = new ArrayList<Long>();
	
	/*@SubscribeEvent
	public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
		if (isSidePlaceActive) {
        	try {
        		if (robot == null) try { robot = new Robot(); } catch (AWTException e) {}
        		double hitY = event.target.hitVec.yCoord;
        		double playerY = mc.thePlayer.posY;
        		if (hitY >= playerY) return; 
        		if (event.target.sideHit == EnumFacing.UP) return;
        		if (mc.thePlayer.getDistanceSq(event.target.getBlockPos()) >= 27) return;
        		long t = new Date().getTime();
        		if (t - time_last_sidePlace_click <= 10) return;
        		time_last_sidePlace_click = t;
        		
        		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        		cps.add(t);
        		
		        float avg_cps = 0;
				Iterator<Long> i = cps.iterator();
				while (i.hasNext()) {
					long time = i.next();		
					if (t - time > 1000) i.remove();
					avg_cps++;
				}
				
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        }
        } 
	}*/
	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		
		float partialTicks = event.partialTicks;
		
		EntityPlayerSP player = mc.thePlayer;
		World world = mc.theWorld;
		if (player == null || world == null) return;

		
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks;
        Vec3 pos = new Vec3(d0, d1, d2);
        
        if (this.isAutoWaterDropActive) {
			Main.autoWaterDrop.check(player, pos);
		}
        
        
        
        if (this.isInvulnerableTimerActive && MyChatListener.IS_IN_GAME) {
        	Main.invulnerableTime.scan(world.playerEntities, pos, partialTicks, isInvulnerableTimerSoundsActive);
        }
        
        if (this.isInvulnerableTimerActive && MyChatListener.IS_IN_GAME) {
        	Main.invulnerableTime.scan(world.playerEntities, pos, partialTicks, isInvulnerableTimerSoundsActive);
        }
        //if (true) return;
        
        if (this.isNamePlateActive) {
        	Main.namePlate.draw(pos, isNamePlateRainbowActive, rainbowSpeed, namePlateCustomColor);
        }
        
        if (this.isResourcesHologramActive) {
        	Main.guiResourceHologram.draw(pos);
        }
        
        Main.tntjump.draw(pos, partialTicks);
        
        if (STATE == false) return; 
        
        if (Minecraft.getMinecraft().gameSettings.keyBindStreamToggleMic.isPressed()) {
        	isT_Active = !isT_Active;
        	Main.chatListener.playSound(isT_Active ? Main.chatListener.SOUND_PARTY_CHAT : Main.chatListener.SOUND_REJECT);
        }
   
        GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        
		GL11.glTranslated(-pos.xCoord, -pos.yCoord, -pos.zCoord);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GlStateManager.disableBlend();
		GL11.glLineWidth(1.0f);
		
		
		List<EntityPlayer> entities = Minecraft.getMinecraft().theWorld.playerEntities;
		double playerSpeedY = mc.thePlayer.posY - mc.thePlayer.prevPosY;
		for (EntityPlayer en: entities) {
			if (en == null) continue;
			if (en == player) continue;
			if (en.getTeam() == player.getTeam()) continue;

			double posX = en.prevPosX + (en.posX - en.prevPosX) * partialTicks;
	        double posY = en.prevPosY + (en.posY - en.prevPosY) * partialTicks;
	        double posZ = en.prevPosZ + (en.posZ - en.prevPosZ) * partialTicks;
			
			double posX1 = posX - en.width/2;
			double posZ1 = posZ - en.width/2;
			double posX2 = posX + en.width/2;
			double posZ2 = posZ + en.width/2;
			
			TEAM_COLOR team_color = MyChatListener.getEntityTeamColor(en);
			setLineColor(getColorByTeam(team_color));

			drawBox(posX1, posY, posZ1, posX2, posY + en.height, posZ2);
			
			
			double enSpeedY = en.posY - en.prevPosY;
			if (isT_Active && enSpeedY > -1 && playerSpeedY > -1) {
				Vec3 head_pos = new Vec3(d0, d1 + player.getEyeHeight(), d2);
				
				drawLineWithGL(head_pos, new Vec3(posX, posY + en.eyeHeight, posZ));
			}
		}
		
		//draw lines from array
		if (lines.size() > 0) {
			for (MyLine l: lines) {
				Vec3 p1 = l.pos1;
				if (p1 == null) p1 = new Vec3(d0, d1 + player.getEyeHeight(), d2);
				
				setLineColor(l.color);
				
				drawLineWithGL(p1, l.pos2);
			}
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
	}

	public Color getColorByTeam(TEAM_COLOR team_color) {
		Color color = new Color(0f, 0F, 0F, 1F);
		if (team_color == TEAM_COLOR.RED) {
			color = new Color(1F, 0F, 0F, 1F);
		} else if (team_color == TEAM_COLOR.YELLOW) {
			color = new Color(1F, 1F, 0F, 1F);
		} else if (team_color == TEAM_COLOR.GREEN) {
			color = new Color(0F, 1F, 0F, 1F);
		} else if (team_color == TEAM_COLOR.AQUA) {
			color = new Color(0F, 1F, 1F, 1F);
		} else if (team_color == TEAM_COLOR.BLUE) {
			color = new Color(0F, 0F, 1F, 1F);
		} else if (team_color == TEAM_COLOR.PINK) {
			color = new Color(1F, 0F, 1F, 1F);
		} else if (team_color == TEAM_COLOR.WHITE) {
			color = new Color(1F, 1F, 1F, 1F);
		} else if (team_color == TEAM_COLOR.GRAY) {
			color = new Color(0.6f, 0.6F, 0.6F, 1F);
		}
		return color;
	}
	
	private void setLineColor(Color color) {
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1F);
	}
	
	public void addLine(Vec3 pos1, Vec3 pos2, Color color) {
		lines.add(new MyLine(pos1, pos2, color));
	}
	
	public void clearLines() {
		lines.clear();
	}
	
	public void drawFilledSquare(double x1, double y1, double z1, double x2, double y2, double z2) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glVertex3d(x1, y2, z2);	
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y1, z1);
		
		GL11.glEnd();
	}
	
	public void drawFilledBox(double x1, double y1, double z1, double x2, double y2, double z2) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBegin(GL11.GL_QUADS);
		
		
		
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x1, y2, z1);
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x2, y2, z2);
		
		GL11.glVertex3d(x2, y2, z2);
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y2, z2);		
		GL11.glVertex3d(x1, y2, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x1, y1, z1);
		
		
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y2, z1);
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x2, y2, z2);
		
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x1, y2, z1);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x2, y2, z2);		
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x1, y1, z2);
		
		
		GL11.glVertex3d(x1, y1, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x2, y1, z2);
		GL11.glVertex3d(x2, y1, z1);
		GL11.glVertex3d(x1, y1, z2);
		GL11.glVertex3d(x1, y1, z1);
		
		GL11.glVertex3d(x1, y2, z1);
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glVertex3d(x2, y2, z2);
		GL11.glVertex3d(x2, y2, z1);
		GL11.glVertex3d(x1, y2, z2);
		GL11.glVertex3d(x1, y2, z1);
		


		GL11.glEnd();
	}
	
	public void drawBox(double x1, double y1, double z1, double x2, double y2, double z2) {		
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
	
	public void drawBoxAroundBlock(double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
		drawFilledBox(x + x1, y + y1, z + z1, x + x2 - 1, y + y2 - 1, z + z2 - 1);
	}
	
	public static void drawLineWithGL(Vec3 blockA, Vec3 blockB) {
		GL11.glBegin(GL11.GL_LINE_STRIP);		

		GL11.glVertex3d(blockA.xCoord, blockA.yCoord, blockA.zCoord);
		GL11.glVertex3d(blockB.xCoord, blockB.yCoord, blockB.zCoord);

		GL11.glEnd();
	}
}
