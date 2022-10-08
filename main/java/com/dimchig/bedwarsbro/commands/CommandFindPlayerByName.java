package com.dimchig.bedwarsbro.commands;

import java.util.Date;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.hints.HintsFinder;
import com.dimchig.bedwarsbro.particles.ParticleController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CommandFindPlayerByName extends CommandBase {

	public static String command_text = "/findplayer";
	
	public CommandFindPlayerByName() {
		this.command_text = command_text.replace("/", "");
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

	@Override
	public String getCommandName() {
		return this.command_text;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Find player by name";
	}
	
	public void work(String name) {
		List<EntityPlayer> players = Minecraft.getMinecraft().theWorld.playerEntities;
		for (EntityPlayer p: players) {
			if (p.getName().equalsIgnoreCase(name)) {
				
				Main.myTickEvent.FIND_PLAYER_COMMAND_SEARCH = "";
				Main.chatListener.playSound(Main.chatListener.SOUND_PARTY_CHAT);
				ChatSender.addText("&fНайден &e" + p.getName());
				EntityPlayerSP mod_palayer = Minecraft.getMinecraft().thePlayer;
				HintsFinder.lookAtPlayer(mod_palayer.posX, mod_palayer.posY, mod_palayer.posZ, p.posX, p.posY, p.posZ);
			}
		}
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length != 1) return;
		try {
			String name = args[0].trim();
			
			if (!Main.myTickEvent.FIND_PLAYER_COMMAND_SEARCH.equals(name)) {
				ChatSender.addText("&fПоиск &e" + name + "&f...");
				Main.myTickEvent.FIND_PLAYER_COMMAND_SEARCH = name;
				Main.myTickEvent.FIND_PLAYER_COMMAND_SEARCH_TIME = new Date().getTime();
			}
			
			work(name);
		} catch (Exception ex) {
			ChatSender.addText("&cInvalid arguments!");
		}
	}
}
