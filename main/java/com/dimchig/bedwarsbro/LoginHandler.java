package com.dimchig.bedwarsbro;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.serializer.MySerializer;
import com.dimchig.bedwarsbro.stuff.BWBed;
import com.dimchig.bedwarsbro.stuff.HintsBedScanner;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class LoginHandler {
	Minecraft mc;
	
	public LoginHandler() {
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		Main.baseProps.readProps();
		Main.baseProps.readMessages();
		Main.updateAllBooleans();
		
		new Timer().schedule( 
    	        new TimerTask() {
    	            @Override
    	            public void run() {
    	            	ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "&fВсе настройки мода - &c/bwbro");
    	            	ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "&fАвтосообщения - &e/meow");
    	            	String author = Main.getPropModAuthor();
    	            	if (author == null || author.length() <= 1) author = "DimChig";
    	            	ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "&fАвтор мода играет под ником &a" + author);
    	            	ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "&fДискорд сервер мода - &9/bwdiscord");
    	            	Main.updateAllBooleans();
    	            }
    	        }, 
    	        3000
    	);
	}
}
