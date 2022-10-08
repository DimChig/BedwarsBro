package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.util.List;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.hints.BedwarsMeow;
import com.dimchig.bedwarsbro.hints.HintsFinder;
import com.dimchig.bedwarsbro.hints.HintsValidator;
import com.dimchig.bedwarsbro.hints.BedwarsMeow.MsgCase;
import com.dimchig.bedwarsbro.particles.ParticleController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;

public class CommandMeow extends CommandBase {

	public static String command_text = "/meow";
	Main main_instance;
	
	public CommandMeow(Main main) {
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
		return "Meow mod";
	}
	
	void printHelpInfo() {
	
		String prefix = MyChatListener.PREFIX_BEDWARS_MEOW;
		ChatSender.addText("");
		ChatSender.addText(prefix + "&fПомощь по моду &f(Нажимай на сообщения)&f:");
		
		try {
			String path2file = Minecraft.getMinecraft().mcDataDir.getCanonicalPath() + "\\" + BedwarsMeow.filename;
			try {
				ChatSender.addHoverFileText("&7• &eДобавить/Изменить свои сообщения", "&eНажми&f, чтоб открыть &8" + path2file, path2file);
			} catch (IOException e) {
				ChatSender.addClickSuggestAndHoverText("&7• &eДобавить/Изменить свои сообщения", "&eНажми&f, чтоб скопировать путь", path2file);
			}
		} catch (Exception e) {
			ChatSender.addText("&7• &cНе удалось найти файл с сообщениями");
		}
		
		ChatSender.addClickText("&7• &bОбновить сообщения из файла", "/" + this.command_text + " update");
		ChatSender.addHoverText("&7• &cВосстановить файл к заводским", "Сбросит файл как при устоновке мода\n&fНапиши сам в чат эту команду:\n\n&c" + "/" + this.command_text + " reset");
		ChatSender.addHoverText("&7• &6Убрать сообщения с матами", "Уберет сообщения с плохими словами\n&fНапиши сам в чат эту команду:\n\n&6" + "/" + this.command_text + " remove_bad_words");
		ChatSender.addText("");
		String starting = "/" + this.command_text + " print ";
		ChatSender.addClickText("&7• &fСообщения после &cкила", starting + "kill");
		ChatSender.addClickText("&7• &fСообщения после &eфинального кила", starting + "final_kill");
		ChatSender.addClickText("&7• &fСообщения после &6смерти", starting + "death");
		ChatSender.addClickText("&7• &fСообщения после &aкровати &7(одиночные)", starting + "bed_single");
		ChatSender.addClickText("&7• &fСообщения после &aкровати &7(командные)", starting + "bed_multi");
		ChatSender.addClickText("&7• &fСообщения в &bначале игры", starting + "game_start");
		ChatSender.addClickText("&7• &fСообщения при &dпобеде", starting + "win");
		ChatSender.addText(     "");
		
		boolean is_mod_active = Main.getConfigBool(CONFIG_MSG.BEDWARS_MEOW);
		
		String s_toggle_mod = "&cВыключен";
		String s_toggle_mod_hover = "&fНажми, чтоб &aвключить";
		if (is_mod_active) {
			s_toggle_mod = "&aВключен";
			s_toggle_mod_hover = "&fНажми, чтоб &cвыключить";
		}
		
		String s_toggle_mod_colors = "&cНе используются";
		String s_toggle_mod_colors_hover = "&fНажми, чтоб &aиспользовать &fцветовую палитру &e/rs";
		if (Main.getConfigBool(CONFIG_MSG.BEDWARS_MEOW_WITH_COLORS)) {
			s_toggle_mod_colors = "&aИспользуются";
			s_toggle_mod_colors_hover = "&fНажми, чтоб &cне использовать &fцветовую палитру &e/rs";
		}
		
		ChatSender.addClickAndHoverText("&7• &fМод &8▸ " + s_toggle_mod, s_toggle_mod_hover, "/" + this.command_text + " toggle");
		
		if (is_mod_active) {
			ChatSender.addClickAndHoverText("&7• &fЦвета &8▸ " + s_toggle_mod_colors + " &7(только для &cд&eо&aн&bа&9т&dе&cр&6о&eв&f, настрой командой &e/rs&7)", s_toggle_mod_colors_hover, "/" + this.command_text + " toggleColors");			
		}
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String prefix = MyChatListener.PREFIX_BEDWARS_MEOW;
		
		if (args.length == 0) {
			printHelpInfo();
			return;
		} else {
			if (args.length == 2 && args[0].trim().equals("print")) {
				String c = args[1].trim();
				Main.bedwarsMeow.IS_USE_COLORS = true;
				if (c.equals("kill")) {
					ChatSender.addText(prefix + "Сообщения после &bКИЛА:&f\n" + Main.bedwarsMeow.getMeows(MsgCase.KILL));
				} else if (c.equals("final_kill")) {
					ChatSender.addText(prefix + "Сообщения после &bФИНАЛЬНОГО КИЛА:&f\n" + Main.bedwarsMeow.getMeows(MsgCase.FINAL_KILL));
				} else if (c.equals("death")) {
					ChatSender.addText(prefix + "Сообщения после &bСМЕРТИ:&f\n" + Main.bedwarsMeow.getMeows(MsgCase.DEATH));
				} else if (c.equals("bed_single")) {
					ChatSender.addText(prefix + "Сообщения про &bКРОВАТЬ &e(одиночный режим):&f\n" + Main.bedwarsMeow.getMeows(MsgCase.BED_SINGLE));
				} else if (c.equals("bed_multi")) {
					ChatSender.addText(prefix + "Сообщения про &bКРОВАТЬ &e(командный режим):&f\n" + Main.bedwarsMeow.getMeows(MsgCase.BED_MULTI));
				} else if (c.equals("win")) {
					ChatSender.addText(prefix + "Сообщения после &bВЫЙГРАША:&f\n" + Main.bedwarsMeow.getMeows(MsgCase.WIN));
				} else if (c.equals("game_start")) {
					ChatSender.addText(prefix + "Сообщения в &bНАЧАЛЕ ИГРЫ:&f\n" + Main.bedwarsMeow.getMeows(MsgCase.GAME_START));
				} 
				Main.bedwarsMeow.updateBooleans();
			} else if (args[0].trim().equals("update")) {
				Main.bedwarsMeow.readFile();
				
				if (Main.bedwarsMeow.meowMessages != null) {
					int cnt = Main.bedwarsMeow.meowMessages.size();
					ChatSender.addText(prefix + "&bОбновлено &l" + cnt + "&b сообщени" + Main.chatListener.getNumberEnding(cnt, "е", "я", "й") + "!");
					MyChatListener.playSound(MyChatListener.SOUND_PARTY_CHAT);
				}
				
			} else if (args[0].trim().equals("toggle")) {
				
				Main.clientConfig.get(CONFIG_MSG.BEDWARS_MEOW.text).set(!Main.clientConfig.get(CONFIG_MSG.BEDWARS_MEOW.text).getBoolean());
				
				printHelpInfo();
			} else if (args[0].trim().equals("toggleColors")) {
				Main.clientConfig.get(CONFIG_MSG.BEDWARS_MEOW_WITH_COLORS.text).set(!Main.clientConfig.get(CONFIG_MSG.BEDWARS_MEOW_WITH_COLORS.text).getBoolean());
				
				printHelpInfo();
			} else if (args[0].trim().equals("reset")) {	
				Main.bedwarsMeow.initMeowMessages();
				Main.bedwarsMeow.readFile();
				
				printHelpInfo();
				
				if (Main.bedwarsMeow.meowMessages != null) {
					int cnt = Main.bedwarsMeow.meowMessages.size();
					ChatSender.addText(prefix + "&cФайл восстановлен&7, &bсчитано &l" + cnt + "&b сообщени" + Main.chatListener.getNumberEnding(cnt, "е", "я", "й") + "!");
					MyChatListener.playSound(MyChatListener.SOUND_PARTY_CHAT);
				}
			} else if (args[0].trim().equals("remove_bad_words")) {	
				Main.bedwarsMeow.removeMeessagesWithBadWords();
				
				printHelpInfo();
				
				if (Main.bedwarsMeow.meowMessages != null) {
					int cnt = Main.bedwarsMeow.meowMessages.size();
					ChatSender.addText(prefix + "&6Файл обновлен&7, &bсчитано &l" + cnt + "&b сообщени" + Main.chatListener.getNumberEnding(cnt, "е", "я", "й") + "!");
					MyChatListener.playSound(MyChatListener.SOUND_PARTY_CHAT);
				}
			} else {
				printHelpInfo();
				return;
			}
			main_instance.saveConfig();
			main_instance.bedwarsMeow.updateBooleans();
		}
	}
}
