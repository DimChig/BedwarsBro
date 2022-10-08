package com.dimchig.bedwarsbro.particles;

import java.awt.Color;
import java.util.Random;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ParticlesAlwaysSharpness {
	private Minecraft mc;
    private boolean hasSharpness;
    private boolean flag = false;
    private boolean areCritsRemoved = false;

    public ParticlesAlwaysSharpness() {
        this.mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register((Object) this);
        
        
        
        /*       	HOW I FOUND AND REMOVED IT
    	for (EnumParticleTypes type : EnumParticleTypes.values()) {
    		//ChatSender.addText(type.getParticleID() + " " + type.getParticleName());
    		if (type.getParticleID() == 9) {
    			//Minecraft.getMinecraft().effectRenderer.registerParticle(0, null);
    			
    		}
		}*/
    }
   
    
    public void onMyLeftClick() {
    	if (!ParticleController.isActive) return;
    	if (areCritsRemoved == false) {
    		areCritsRemoved = true;
    		Minecraft.getMinecraft().effectRenderer.registerParticle(9, null); //remove crits
    	}
    	
        if(!this.mc.inGameHasFocus || this.mc.objectMouseOver == null) return;
        this.attemptParticleSpawn();  
    }

    private boolean shouldSpawnParticles() {
        final Entity entity = this.mc.objectMouseOver.entityHit;
        return (entity instanceof EntityLiving || (entity instanceof EntityOtherPlayerMP && ((EntityOtherPlayerMP)entity).isEntityAlive()));
    }

    private void attemptParticleSpawn() {
        try {
        	Entity ent = this.mc.objectMouseOver.entityHit;
        	if (!(ent instanceof EntityPlayer)) return;
        	EntityPlayer en = (EntityPlayer) ent;
        	float color_r = 1f; 
        	float color_g = 1f; 
        	float color_b = 1f;
        	if (en == null || en.getDisplayName() == null || en.getName() == null) return;
        	
        	TEAM_COLOR team_color = MyChatListener.getEntityTeamColor(en);
        	
        	Random rnd = new Random();
    		Color color = new Color(0, 0, 0);
    		color_r = color.getRed() / 255f; 
        	color_g = color.getGreen() / 255f; 
        	color_b = color.getBlue() / 255f; 
        	
        	if (team_color == TEAM_COLOR.NONE) {
        		color = Main.rainbowColorSynchronizer.getColor();
        	} else {
        		color = ParticleController.getParticleColorForTeam(team_color);	
        	}
        	
        	color_r = color.getRed() / 255f; 
        	color_g = color.getGreen() / 255f; 
        	color_b = color.getBlue() / 255f; 
        	

            for (int i = 0; i < 20; ++i) {
                final double x = this.mc.objectMouseOver.entityHit.posX + (Math.random() - 0.5) * 0.1;
                final double y = this.mc.objectMouseOver.entityHit.posY + this.mc.objectMouseOver.entityHit.getEyeHeight() - 0.3 + (Math.random() - 0.5) * 0.5;
                final double z = this.mc.objectMouseOver.entityHit.posZ + (Math.random() - 0.5) * 0.1;
                double speed = 0.2 - Math.random() * 0.1; 
                final double xOffset = Math.random() > 0.5 ? speed : -speed;
                final double yOffset = Math.random() > 0.5 ? speed : -speed;
                final double zOffset = Math.random() > 0.5 ? speed : -speed;
                if(this.mc.theWorld.isRemote) {
                    //this.mc.theWorld.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, xOffset, yOffset, zOffset, new int[0]);
                	ParticleController.spawnColorParticleSharpness(x, y, z, xOffset, yOffset, zOffset, color_r, color_g, color_b);
                }
            }
        }
        catch (Exception e) {

        }
    }
}
