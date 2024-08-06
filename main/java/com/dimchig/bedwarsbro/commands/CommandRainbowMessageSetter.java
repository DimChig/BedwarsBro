package com.dimchig.bedwarsbro.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class CommandRainbowMessageSetter extends CommandBase {

	public static String command_text = "rs";
	
	public CommandRainbowMessageSetter() {
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
	
	String randomFilterString = "lno";
	
	public String setRandomPalitra() {
		String[] available_colors = new String[]{"2", "3", "5", "6", "9", "a", "b", "c", "d", "e", "f"};
		Random rnd = new Random();
		int min_size = 2;
		int max_size = 5;
		int count = (int)Math.min(min_size + rnd.nextInt(max_size - min_size + 1), max_size);
		ArrayList<String> generated_colors = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			String rnd_color = "0";
			while (true) {
				rnd_color = available_colors[rnd.nextInt(available_colors.length)];
				if (!generated_colors.contains(rnd_color)) break;
			}
			generated_colors.add(rnd_color);
		}
		String output_str = "";
		for (String s: generated_colors) {
			output_str += s;
		}
		output_str += "+l";
		for (String s: randomFilterString.split("")) {
			if (rnd.nextBoolean() && !output_str.contains(s)) output_str += s;
		}
		
		Main.clientConfig.get(CONFIG_MSG.RAINBOW_MESSAGE_COLORS.text).set(output_str);
		return output_str;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String current_palitra = Main.getConfigString(CONFIG_MSG.RAINBOW_MESSAGE_COLORS);
		String tutorial_colorcodes =  "&fЦвета:\n &11 &22 &33 &44 &55 &66 &77 &88 &99 &aa &bb &cc &dd &ee &ff \n" + 
				 "&fФорматирвока:\n &fl &7- &f&lжирный&7\n &fn&7 - &f&nподчеркнутый&7\n &fo &7- &f&oкурсив\n &fm &7- &f&mзачеркнутый&7\n&e&l&n&oМожно совмещать&7 (e+lno)";
		
		if (args.length <= 0 || args[0].equals("help")) {
			ChatSender.addText("&aТекущаяя палитра: &f" + current_palitra);
			 ChatSender.addText("&e/rs &bпалитра &7- &fустановить палитру &7(&e/rs &bade+ln&7)");
			 ChatSender.addText("&e/rs rainbow &7- &cр&6а&eд&aу&bг&dа");
			 ChatSender.addText("&e/rs add &aкод &7- &fдобавить что-то");
			 ChatSender.addText("&e/rs remove &cкод &7- &fубрать символы из существующего");
			 ChatSender.addText("&e/rs mirror &7- &fотзеркалить палитру");
			 ChatSender.addText("&e/rs team &7- &fустановить цвет под команду");
			 ChatSender.addHoverText("&e/rs mode &7[&b0&7/&b1&7] &7- &fустановить режим&c*", "&e0 &7- &fадаптивная длина\n&e1 &7- &fцвета меняются через каждое слово");
			 ChatSender.addText("&e/rs global &7[&bon&7/&boff&7]&7- &fписать всегда в глобальный чат &7(в лобби не будет \"!\"");
			 ChatSender.addText("&e/rs random &7- &fрандомная палитра");			 
			 ChatSender.addText("&e/rs randomstyle &7[lnom&7] &7- &fУстановить доступные рандомные форматировки");			 
			 ChatSender.addText("&e/rs autorandom &7[&bon&7/&boff&7]&7- &fпосле каждого сообщения ставить рандомную палитру");			 
			 
			 ChatSender.addHoverText("&aНаведи &a&nсюда&a, чтоб увидеть все цвет-коды", tutorial_colorcodes);
			 return;
		};
		
		String[] available_colors_to_set = new String[] {
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "l", "m", "n", "o", "k", "+"
				};
		
		String output_str = "f";
		String arg0 = args[0];
		
		Main.bedwarsMeow.IS_AUTO_RANDOM_ACTIVE = false;
		
		if (arg0.equals("random")) {
			output_str = setRandomPalitra();						
		} else if (arg0.equals("randomstyle") && args.length == 2) {
			randomFilterString = args[1];
			ChatSender.addText("Стили установлены!");
			return;
		} else if (arg0.equals("rainbow")) {
			output_str = "c6eabd+l";
		} else if (arg0.equals("add") && args.length == 2) {
			output_str = current_palitra;
			
			for (int i = 0; i < args[1].length(); i++) {
				String s = "" + args[1].charAt(i);
				if (s.equals("l") || s.equals("n") || s.equals("o") || s.equals("m")) {
					if (!output_str.contains("+")) output_str += "+";
					
					output_str += s;
				} else {
					if (output_str.contains("+")) {
						output_str = output_str.split(Pattern.quote("+"))[0] + s + "+" + output_str.split(Pattern.quote("+"))[1];
					} else output_str += s;
				}
			}
			
		} else if (arg0.equals("remove") && args.length == 2) {
			output_str = current_palitra.replace(args[1], "");
			if (output_str.endsWith("+")) {
				output_str = output_str.substring(0, output_str.length() - 1);
			}
		} else if (arg0.equals("mirror") && args.length == 1) {
			String[] split = current_palitra.split(Pattern.quote("+"));
			String colors = split[0];
			
			String reverse = new StringBuilder(colors).reverse().toString();
			//if (reverse.length() > 1) reverse = reverse.substring(1);
			output_str = colors + reverse;
			if (split.length > 1) output_str += "+" + split[1];
			
		} else if (arg0.equals("global") && args.length == 2) {
			output_str = current_palitra;
			boolean b = false;
			if (args[1].equals("on")) b = true;
			Main.clientConfig.get(CONFIG_MSG.RAINBOW_MESSAGE_GLOBAL.text).set(b);
			ChatSender.addText("Писать в глобальный чат - " + (b ? "&aвключено" : "&cвыключено"));
			return;
		} else if (arg0.equals("autorandom") && args.length == 2) {
			output_str = current_palitra;
			boolean b = false;
			if (args[1].equals("on")) b = true;
			Main.bedwarsMeow.IS_AUTO_RANDOM_ACTIVE = b;
			ChatSender.addText("Рандомная палитра - " + (b ? "&aвключена" : "&cвыключена"));
			return;
		} else if (arg0.equals("mode")) {
			output_str = current_palitra;
			try {
				int x = Integer.parseInt(args[1]);
				if (x != 0 && x != 1) {
					ChatSender.addText("&cСтавь только 1 или 0!");
					return; 
				}
				Main.clientConfig.get(CONFIG_MSG.RAINBOW_MESSAGE_MODE.text).set(x);								
			} catch (Exception ex) {
				ChatSender.addText("&cСтавь только 1 или 0!");
				return; 
			}
		} else if (arg0.equals("team")) {
			try {
				TEAM_COLOR tc = Main.chatListener.getEntityTeamColorByTeam(Minecraft.getMinecraft().thePlayer.getTeam().getRegisteredName());
				if (tc == TEAM_COLOR.RED) output_str = "c";
				else if (tc == TEAM_COLOR.YELLOW) output_str = "e6";
				else if (tc == TEAM_COLOR.GREEN) output_str = "a";
				else if (tc == TEAM_COLOR.AQUA) output_str = "b";
				else if (tc == TEAM_COLOR.BLUE) output_str = "b";
				else if (tc == TEAM_COLOR.PINK) output_str = "d";
				else if (tc == TEAM_COLOR.GRAY) output_str = "f7";
				else if (tc == TEAM_COLOR.WHITE) output_str = "f";
				else {
					ChatSender.addText("&cКоманда не разпознана!");
					return;	
				}
				output_str += "+l";
			} catch (Exception e) {
				ChatSender.addText("&cКоманда не разпознана!");
				return;
			}
		} else if (arg0.length() > 0) {
			//check each symbol
			String new_str = arg0;
			for (int i = 0; i < new_str.length(); i++) {
				boolean contains = false;
				for (int j = 0; j < available_colors_to_set.length; j++) {
					if (available_colors_to_set[j].equals("" + new_str.charAt(i))) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					ChatSender.addText("&cКод \"&e" + new_str.charAt(i) + "&c\" не существует!\n" + tutorial_colorcodes);
					return;
				}
			}
			output_str = arg0;
		}
		
		ChatSender.addText("&7Сгенерированная строка: &f" + output_str + "&7, примеры:");
		
		Main.clientConfig.get(CONFIG_MSG.RAINBOW_MESSAGE_COLORS.text).set(output_str);
		
		String msg = Main.commandRainbowMessage.generateRainbowMessage("Бро, тебе надо больше тренироваться!", 1, -1);
		if (msg != null) {
			ChatSender.addText(msg);
		}
		msg = Main.commandRainbowMessage.generateRainbowMessage("Унизил лоха", 1, -1);
		if (msg != null) {
			ChatSender.addText(msg);
		}
		
		Main.saveConfig();
		
	}
}