package com.dimchig.bedwarsbro.particles;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.world.World;
import scala.util.Random;

public class ParticleFinalKillEffect extends EntityAuraFX {
    public ParticleFinalKillEffect(World parWorld, double parX, double parY, double parZ) {
        super(parWorld, parX, parY, parZ, 0, 0, 0);
        Random rnd = new Random();
        //setParticleTextureIndex(160 + rnd.nextInt(8)); 
        setParticleTextureIndex(65);
        particleScale = 1.2F;
        setRBGColorF(1f, 1f, 1f);
        particleMaxAge = 50 + rnd.nextInt(30);
        this.particleGravity = 1000f;
        
        
        /*
            0 = EntitySmokeFX, EntityAuraFX, EntitySuspendFX, EntityReddustFX, EntitySnowShovelFX and also EntityExplodeFX
			0 to 7 = EntityPortalFX and also EntityCloudFX
			19 to 22 = EntityRainFX and also EntityFishWake
			32 = EntityBubbleFX
			48 = EntityFlameFX
			49 = EntityLavaFX
			64 = EntityNoteFX
			65 = EntityCrit2FX
			80 = EntityHeartFX
			81 = EntityAngryVillagerFX
			82 = EntityHappyVillagerFX
			144 = EntitySpellParticleFX.InstantFactory and also EntitySpellParticleFX.WitchFactory
			112, 113 = EntityDropParticleFX
			128 to 135 = EntitySpellParticleFX
			160 to 167 = EntityFireworksFX
			225 to 250 = EntityEnchantingTableParticleFX 
         */
    }
}