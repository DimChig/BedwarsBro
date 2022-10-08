package com.dimchig.bedwarsbro.particles;

import java.util.Random;

import com.dimchig.bedwarsbro.ChatSender;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.world.World;

public class ParticleSharpnessEffect extends EntityCrit2FX  {
	 public ParticleSharpnessEffect(World parWorld, double parX, double parY, double parZ) {
	        super(parWorld, parX, parY, parZ, 0, 0, 0);
	        Random rnd = new Random();
	        particleMaxAge = 7 + rnd.nextInt(3);
	        
	        //setParticleTextureIndex(160 + rnd.nextInt(8)); 
	       /* setParticleTextureIndex(65);
	       
	        setRBGColorF(1f, 1f, 1f);
	        particleMaxAge = 10 + rnd.nextInt(3);
	        this.particleGravity = 2f;*/
	 }
}
