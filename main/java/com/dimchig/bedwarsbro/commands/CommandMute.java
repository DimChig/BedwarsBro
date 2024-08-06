package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
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

public class CommandMute extends CommandBase {

	public static String filename = "BedwarsBro_Список_замученных.txt";
	public static String command_text = "/bwmute";
	Main main_instance;
	
	public CommandMute(Main main) {
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
		return "Help mod";
	}
	
	public void printMuted() {
		ArrayList<String> arr = Main.fileNicknamesManager.readNames(filename);
		if (arr.size() == 0) {
			ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&cЗамученных нету! Добавь через /" + this.command_text + " add [ник]");
			return;
		}
		
		ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&fСписок замученных:");
		for (String s: arr) {
			String command = "/" + this.command_text + " remove " + s.trim();
			ChatSender.addClickAndHoverText("&8 • &f" + s.trim() + "    &c[удалить]", "&c" + command, command);
		}
		ChatSender.addText("");
	}

	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		ChatSender.addText("");
		try {
			
			if (args.length == 0) {
				printMuted();
			} else if (args[0].equals("list")) {
				printMuted();
			} else if (args[0].equals("add") && args.length == 2) {		
				String name = args[1].trim();
				boolean isSuccess = Main.fileNicknamesManager.addName(filename, name);
				if (isSuccess) {		 
					 printMuted();
					 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&aИгрок \"" + name + "\" замучен");
					 Main.chatListener.playSound(Main.chatListener.SOUND_PARTY_CHAT);
				} else {
					 
					 printMuted();
					 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&cДруг \"" + name + "\" уже замучен");
					 Main.chatListener.playSound(Main.chatListener.SOUND_REJECT);
				}
			} else if (args[0].equals("remove") && args.length == 2) {		
				String name = args[1].trim();
				boolean isSuccess = Main.fileNicknamesManager.removeName(filename, name);
				if (isSuccess) {					 
					 printMuted();
					 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&cДруг \"" + name + "\" удален");
					 Main.chatListener.playSound(Main.chatListener.SOUND_PARTY_CHAT);
				} else {					 
					 printMuted();
					 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&cДруг \"" + name + "\" уже удален");
					 Main.chatListener.playSound(Main.chatListener.SOUND_REJECT);
				}
			} else {
				 printMuted();
				 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&f/" + this.command_text + " &aadd &f[ник] &7- &fзамутить");
				 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&f/" + this.command_text + " &cremove &f[ник] &7- &fразмутить");
				 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&f/" + this.command_text + " &blist &7- &fсписок замученных");
				 ChatSender.addText(Main.chatListener.PREFIX_MUTED + "&fМожно мутить игроков, у которых ник начинается на что-то, используя звездочку. Наприме &f/" + this.command_text + " add &cARAB_*&f - будут замученны все игроки типо &cARAB_1234 &fи &cARAB_zxc");
			}
			
		} catch (Exception ex) {
			ChatSender.addText("&cОшибка!");
			ex.printStackTrace();
			return; 
		}
		ChatSender.addText("");
	}	
}
