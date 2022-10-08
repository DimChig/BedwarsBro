package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;


public class CommandDexlandMeowSpoof extends CommandBase {

	public static String command_text = "meowspoof";
	
	public CommandDexlandMeowSpoof() {
		this.command_text = this.command_text.replace("/", "");
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
		return "Makes message rainbow setter";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String[] cmds = new String[]{"kill", "death", "bed", "join", "win"};
		
		String av_cmds = "";
		for (String c: cmds) {
			av_cmds += "&b" + c + "&7/";
		}
		av_cmds = av_cmds.substring(0, av_cmds.length() - 3);
		
		if (args.length < 2) {
			ChatSender.addText("&dУстановить все 5 сообщений:\n  /" + this.command_text + " &eник &aсообщение");
			ChatSender.addText("&dУстановить одно сообщение:\n&f  /" + this.command_text + " " + av_cmds + " &eник &aсообщение\n     &7(можно юзать %player% при &bkill&7)");
			ChatSender.addText("&dЗаставить сказать сообщение:\n&f  /" + this.command_text + " &cfake&bdeath&7/&cfake&bjoin&7/&cfake&bwin" + " &eник");
			return;
		}
		
		int start_idx = 1;
		String type = "all";
		String nick = args[0];
		
		
		String local_command = "";
		for (String c: cmds) {
			if (args[0].equals(c)) {
				if (args.length < 3) return;
				type = c;
				nick = args[1];
				start_idx = 2;
			} else if (args[0].contains("fake") && args[0].contains(c)) {
				type = "fake" + c;
				local_command = c;
				nick = args[1];
				start_idx = -1;
			}
		}
		
		if (type.contains("fake")) {
			ChatSender.addText("Фейкую сообщение &e" + local_command + "&f...");
			String s = "";
			if (local_command.equals("death")) {
				s = "Ахах, " + nick + " упал в бездну, вы видели?";
			} else if (local_command.equals("join")) {
				s = "Всем привет! Победит только одна, сильнейшая команда!";
			} else if (local_command.equals("win")) {
				s = "Ребят, вы знали, что Перезагрузка сервера через 10 секунд!";
			} else {
				ChatSender.addText("&cЭто сообщение нельзя сфейковать!\n&cМожно только &bdeath&7, &bjoin&7, &bwin");
			}
			if (s.length() == 0) return;
			Main.chatListener.addBedwarsMeowMessageToQuee(s, false);
			return;
		}
		
		String msg = "";
		for (int i = start_idx; i < args.length; i++) {
			msg += args[i] + " ";
		}
		msg = msg.trim();
				
		String[] commands = new String[] {
				"setkillmsg",
				"setdeathmsg",
				"setbedmsg",
				"setjoinmsg",
				"setwinmsg",
		};
		
		for (String c: cmds) {
			if (type.equals(c)) commands = new String[] {"set" + c + "msg"};
		}
		
		//find nick in tab
		boolean isOnServer = false;
		Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
    	int cnt = 0;
    	for (NetworkPlayerInfo info: players) { 	    	
    		if (info == null || info.getGameProfile() == null) continue;
    		String name = info.getGameProfile().getName();
    		if (name == null) continue;
    		if (name.equals(nick)) {
    			isOnServer = true;
    			break;
    		}
    	}
    	if (!isOnServer) {
    		ChatSender.addText("&cИгрок &c\"&e" + nick + "&c\" &c не найден!");
    		return;
    	}
    	
		for (String cmd: commands) {
			String s = nick + " ." + cmd + " " + msg; 
			Main.chatListener.addBedwarsMeowMessageToQuee(s, false);
			//ChatSender.addText(s);
		}
		
	}
}