package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.FileManager;
import com.dimchig.bedwarsbro.FileNicknamesManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.particles.ParticleController;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow;
import com.dimchig.bedwarsbro.stuff.HintsFinder;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow.MsgCase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;

public class CommandFriends extends CommandBase {

	public static String filename = "BedwarsBro_Список_друзей.txt";
	public static String command_text = "/bwf";
	Main main_instance;
	
	public CommandFriends(Main main) {
		this.command_text = command_text.replace("/", "");
		main_instance = main;
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
		return "";
	}
	
	public void printFriends() {
		ArrayList<String> arr = Main.fileNicknamesManager.readNames(filename);
		if (arr.size() == 0) {
			ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&cДрузей нету! Добавь через &f/" + this.command_text + " add [ник]&c* &f[текст]");
			return;
		}
		
		ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&fСписок друзей &7(удалить друга - &c/" + this.command_text + " remove [ник]&7)&f:");
		for (String s: arr) {
			String command = "/party " + s.trim();
			String remove_friend = "/" + this.command_text + " remove " + s.trim();
			ChatSender.addClickAndHoverText("&8 • &f" + s.trim() + "    &e[кинуть пати]", "&e" + command, command);
		}
		String command = "/" + this.command_text + " party_all";
		ChatSender.addClickAndHoverText(Main.chatListener.PREFIX_FRIENDS + "&e[Кинуть пати ВСЕМ друзьям]", "&e" + command, command);
		ChatSender.addText("");
	}
	
	public void sendPartyToAll() {
		ArrayList<String> arr = Main.fileNicknamesManager.readNames(filename);

		for (int i = 0; i < arr.size(); i++) {
			final String name = arr.get(i).trim();
			new Timer().schedule( 
	    	        new TimerTask() {
	    	            @Override
	    	            public void run() {
	    	            	ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&fКидаю пати &e" + name + "...");
	    	            	ChatSender.sendText("/party " + name);
	    	            }
	    	        }, 
	    	        i * 500
	    	);
		}
		ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&aКидаю пати " + arr.size() + " игрок" + Main.chatListener.getNumberEnding(arr.size(), "у", "ам", "ам") + "...");
	}

	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		ChatSender.addText("");
		try {
			
			if (args.length == 0) {
				printFriends();
			} else if (args[0].equals("list")) {
				printFriends();
			} else if (args[0].equals("party_all")) {
				sendPartyToAll();
			} else if (args[0].equals("add") && args.length == 2) {		
				String name = args[1].trim();
				boolean isSuccess = Main.fileNicknamesManager.addName(filename, name);
				if (isSuccess) {		 
					 printFriends();
					 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&aДруг \"" + name + "\" добавлен");
					 Main.chatListener.playSound(Main.chatListener.SOUND_PARTY_CHAT);
				} else {
					 
					 printFriends();
					 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&cДруг \"" + name + "\" уже добавлен");
					 Main.chatListener.playSound(Main.chatListener.SOUND_REJECT);
				}
			} else if (args[0].equals("remove") && args.length == 2) {		
				String name = args[1].trim();
				boolean isSuccess = Main.fileNicknamesManager.removeName(filename, name);
				if (isSuccess) {					 
					 printFriends();
					 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&cДруг \"" + name + "\" удален");
					 Main.chatListener.playSound(Main.chatListener.SOUND_PARTY_CHAT);
				} else {					 
					 printFriends();
					 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&cДруг \"" + name + "\" не найден");
					 Main.chatListener.playSound(Main.chatListener.SOUND_REJECT);
				}
			} else {
				 printFriends();
				 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&f/" + this.command_text + " &aadd &f[ник] &7- &fдобавить друга");
				 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&f/" + this.command_text + " &cremove &f[ник] &7- &fудалить друга");
				 ChatSender.addText(Main.chatListener.PREFIX_FRIENDS + "&f/" + this.command_text + " &blist &7- &fсписок друзей");
			}
			
		} catch (Exception ex) {
			ChatSender.addText("&cОшибка!");
			ex.printStackTrace();
		}
		ChatSender.addText("");
	}
}
