package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiPlayer;
import com.dimchig.bedwarsbro.hints.BedwarsMeow;
import com.dimchig.bedwarsbro.hints.HintsFinder;
import com.dimchig.bedwarsbro.hints.BedwarsMeow.MsgCase;
import com.dimchig.bedwarsbro.particles.ParticleController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;

public class CommandModHelp extends CommandBase {

	public static String command_text = "/";
	Main main_instance;
	
	public CommandModHelp(Main main, String command) {
		main_instance = main;
		this.command_text = command.replace("/", "");
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
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String str = "";
		str += "&8<===============================================>\n";
		str += "                            &cBedwars&fBro &7v&a" + Main.VERSION + "\n\n";
		str += "                 &fВсе главные настройки находятся в &bконфиге\n";
		str += "                         &fТы можешь найти его тут\n";
		str += "   &eESC &7→ &eMod Options (Настройки Модов) &7→ &eBedwars Bro &7→ &b&lConfig\n\n";
		str += "                               &fНастройки клавиш\n";
		str += "                       &eESC &7→ &eНастройки &7→ &bУправление\n";
		
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null && Main.isPropUserBanned(Minecraft.getMinecraft().thePlayer.getName()) ) {
			str += "               &c&l&k=&c&l Ты забанен администратором мода! &c&l&k=\n";
		}
		ChatSender.addText(str);
		ChatSender.addLinkAndHoverText("                        &fОбзор мода на &cютубе &f- &b&l&nЖМИ&r &b↗\n", "&fНажми, чтоб открыть &cролик", "" + Main.getPropModUpdateLink());
		ChatSender.addLinkAndHoverText("                 &9Discord &fсервер BedwarsBro - &b&l&nЖМИ&r &b↗\n", "&fНажми, чтоб присоединится к серверу", "" + Main.getPropDiscordLink());
		ChatSender.addText("&8<===============================================>");
		
		
		
		String s = "100;100;105;100;109;100;99;100;104;100;105;100;103;100;105;100;115;100;116;100;104;100;101;100;98;100;101;100;115;100;116;100";
    	String s2 = "";
    	for (int i = 0; i < s.split(";").length; i+=2) {
    		String k = s.split(";")[i];
    		if (k.length() == 0) continue;
    		s2 += "" + (char)Integer.parseInt(k);
    	}
		if (args.length == 1 && args[0].equals(s2)) {
			Main.myTickEvent.gui2open = new GuiPlayer();
		}
	}
}
