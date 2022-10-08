package com.dimchig.bedwarsbro.commands;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.google.common.base.Splitter;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class CommandRainbowMessage extends CommandBase {

	public static String command_text = "r";
	
	public CommandRainbowMessage(String command) {
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
		return "Makes message rainbow";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length <= 0) {
			ChatSender.addText("Напиши &e/r &aтекст");
			return;
		}

		String str = "";
		for (String s: args) {
			str += s + " ";
		}
		str = str.trim();
		if (str.length() <= 0) return;
		
		boolean isGlobal = Main.getConfigBool(CONFIG_MSG.RAINBOW_MESSAGE_GLOBAL);
		if (str.startsWith("!")) {
			isGlobal = true;
			str = str.substring(1, str.length());
		}
		
		int text_mode = Main.getConfigInt(CONFIG_MSG.RAINBOW_MESSAGE_MODE);
		
		
		String[] config_replace_character = Main.getConfigString(CONFIG_MSG.RAINBOW_MESSAGE_REPLACE_CHARS).split("=");
		if (config_replace_character.length == 2) {
			str = str.replace(config_replace_character[0], config_replace_character[1]);
		}
		
		String generated_message = generateRainbowMessage(str, 1, text_mode);
		if (generated_message == null) {
			ChatSender.addText("&cСообщение слишком большое!");
			return;
		}
		
		str = "" + generated_message;
		
		
		//check hub
		if (Main.shopManager.findItemInHotbar("ыбор лобби") == -1 && isGlobal) str = "!" + str;
		
		//ChatSender.addClickSuggestAndHoverText(str, "&eЛКМ &f - скопировать", str);
		Minecraft.getMinecraft().thePlayer.sendChatMessage(str);
	}
	
	//public static String[] color_codes = new String[] {"c", "6", "e", "a", "b", "9", "d"};
	public static String[] color_codes = new String[] {"c", "6", "e", "a", "b", "9", "d"};
	public static int MAX_MESSAGE_LENGTH = 100;
	
	private static void readConfigParams() {
		String color_codes_str = Main.getConfigString(CONFIG_MSG.RAINBOW_MESSAGE_COLORS);
		color_codes = color_codes_str.split(",");
		
		if (color_codes_str.contains("+")) {
			try {
				
				String colors = color_codes_str.split(Pattern.quote("+"))[0];
				String additional = color_codes_str.split(Pattern.quote("+"))[1];
				color_codes = new String[colors.length()];
				
				for (int i = 0; i < colors.length(); i++) {
					color_codes[i] = colors.charAt(i) + additional;
				}
				
			} catch (Exception ex) {
				ChatSender.addText("&cОшибка цветов в конфиге! &7(&e/bwbro&7)");
				return;
			}
		} else {
			if (!color_codes_str.contains(",")) {
				color_codes = color_codes_str.split("");
			}
		}
		
		if (color_codes_str.length() == 0 || color_codes.length <= 0) {
			ChatSender.addText("&cДобавь цвета в конфиге! &7(&e/bwbro&7)");
			return;
		}
	}
	public static String generateRainbowMessage(String s, int step, int text_mode) {
		readConfigParams();
		
		if (step > MAX_MESSAGE_LENGTH)
			return null;
		
		if (text_mode == -1) text_mode = Main.getConfigInt(CONFIG_MSG.RAINBOW_MESSAGE_MODE);
		s = s.replace("і", "ы");
		
		String str = "";

		//format
		Iterable<String> chunks = Splitter.fixedLength(step).split(s);
		if (text_mode == 1) chunks = Splitter.on(" ").split(s);
		int code_idx = -1;
        for (String i_chunk: chunks) { 
        	String chunk = i_chunk;
        	
        	if (text_mode == 1) {
        		str += "&0 ";
        	}
        	
        	code_idx = (code_idx + 1) % color_codes.length;
        	String this_chunk_code = "";
        	for (String c: color_codes[code_idx].split("")) {
        		 this_chunk_code += "&" + c;
			}
        	
        	if (this_chunk_code.contains("n") && step == 1 && chunk.contains(" ")) {
        		chunk = chunk.replace(" ", "&0 " + this_chunk_code);
        		code_idx = (code_idx - 1) % color_codes.length;
        	}
        	
        	str += this_chunk_code + chunk;
        }
		
        if (color_codes.length > 0) str += "&" + color_codes[0].charAt(0);
        str = str.trim();
        
		if (str.length() > MAX_MESSAGE_LENGTH) {
			return generateRainbowMessage(s, step + 1, text_mode);
		}

		return str;
	}	
}