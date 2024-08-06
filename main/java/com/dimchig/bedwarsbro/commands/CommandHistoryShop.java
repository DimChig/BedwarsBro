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

public class CommandHistoryShop extends CommandBase {

	public static String command_text = "/bwbroshop";
	Main main_instance;
	
	public CommandHistoryShop(Main main) {
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
	
	private class MyItem {
		public String name;
		public int total_amount;
		public int buy_amount;
		public ArrayList<MyItem> game_log = new ArrayList<MyItem>();
		
		public MyItem(String name, int total_amount, int buy_amount) {
			this.name = name;
			this.total_amount = total_amount;
			this.buy_amount = buy_amount;
			this.game_log = new ArrayList<MyItem>();
		}	
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String str = "";
		str += "&8<===============================================>\n";
		
		ArrayList<MyItem> items = new ArrayList<MyItem>();
		
		try {
			String text = Main.fileManager.readFile("resourceBedwarslog.txt");
			String[] games = text.split(Pattern.quote("========"));

			int games_count = 0;
			
			if (games != null && games.length != 0) {
				for (String game: games) {

					//if (cnt > 100) break;
					String[] log = game.split(Pattern.quote(";&;"));
					if (log == null || log.length == 0) continue;
					
					boolean isGameEmpty = true;
					
					for (String item: log) {
						//if (true) break;
						String s = item.replace(Pattern.quote("\n"), "").replace("" + (char)13, "").replace("" + (char)10, "");
						if (s.length() <= 3 || s.contains("�")) continue;
						String[] split = s.split(";");
						if (split.length != 2) continue;
						try {
							String name = ColorCodesManager.removeColorCodes(split[0].trim());
							int count = Integer.parseInt(split[1].trim());
							
							//find item
							boolean isFound = false;
							for (MyItem i: items) {
								if (i.name.equals(name)) {
									isFound = true;
									
									i.total_amount += count;
									i.buy_amount++;
								
									break;
								}
							}
							if (!isFound) items.add(new MyItem(name, count, 1));
							
							isGameEmpty = false;
							
						} catch (Exception ex) {}
					}
					
					if (!isGameEmpty) games_count++;
										
				}
			}
			
			str += "\n                      &f&lВсего сыграно &a&l" + games_count + " &f&lигр\n\n";
			str += "                    &f&lТоп &e&lпопулярных &f&lпредметов:\n\n";
			
			Collections.sort(items, new Comparator<MyItem>() {
				public int compare(MyItem i1, MyItem i2)
			    {
			        return -Integer.compare(i1.buy_amount, i2.buy_amount);
			    }
			});
			
			int cnt = 0;
			for (MyItem i: items) {
				cnt++;
				String total_amount = "" + i.total_amount + " &7шт";
				if (i.total_amount > 64) {
					int x = (int)(i.total_amount / 64f);
					total_amount = x + " &7стак" + Main.chatListener.getNumberEnding(x, "", "а", "ов");
				}
				String percentage = "" + (int)(i.buy_amount * 100f / games_count) + "%";
				
				String name = i.name;
				if (name.equals("Wool")) name = "Шерсть";
				else if (name.equals("Stick")) name = "&cП&6а&eл&aк&bа";
				else if (name.equals("Arrow")) name = "Стрелы";
				else if (name.equals("Stone Sword")) name = "Меч каменный";
				else if (name.equals("Golden Apple")) name = "Золотое &eяблоко";
				else if (name.equals("Fire Charge")) name = "&6Фаербол";
				else if (name.equals("TNT")) name = "&cДинамит";
				else if (name.equals("Железная броня")) name = "Броня железная";
				else if (name.equals("Iron Sword")) name = "Меч железный";
				else if (name.equals("Wooden Pickaxe")) name = "Кирка деревянная";
				else if (name.equals("Wooden Axe")) name = "Топор деревянный";
				else if (name.equals("Shears")) name = "Ножницы";
				else if (name.equals("Bow")) name = "&6Лук";
				else if (name.equals("Зелье силы")) name = "&4Зелье силы";
				else if (name.equals("End Stone")) name = "Эндерняк";
				else if (name.equals("Stone Pickaxe")) name = "Кирка каменная";
				else if (name.equals("Ender Pearl")) name = "&9Эндер перл";
				else if (name.equals("Water Bucket")) name = "Ведро воды";
				else if (name.equals("Stone Axe")) name = "Топор каменный";
				else if (name.equals("Зелье прыгучести")) name = "Зелье прыгучести";
				else if (name.equals("Iron Pickaxe")) name = "Кирка железная";
				else if (name.equals("Алмазная броня")) name = "Броня алмазная";
				else if (name.equals("Diamond Pickaxe")) name = "Кирка алмазная";
				else if (name.equals("Wooden Planks")) name = "Дерево";
				else if (name.equals("Спавнер моста")) name = "Спавнер моста";
				else if (name.equals("Stained Clay")) name = "Глина";
				else if (name.equals("Obsidian")) name = "&9Обсидиан";
				else if (name.equals("Iron Axe")) name = "Железный топор";
				else if (name.equals("Diamond Sword")) name = "Меч алмазный";
				else if (name.equals("Potion")) name = "Зелье регена";
				else if (name.equals("Diamond Axe")) name = "Топор алмазный";
				else if (name.equals("Кольчужная броня")) name = "Броня кольчужная";
				else if (name.equals("Ladder")) name = "Лестницы";
				else if (name.equals("Glass")) name = "Стелко";
				else if (name.equals("tile.sponge.name")) name = "Губка";

				
				
				str += "&7" + cnt + ". " + (cnt <= 3 ? "&e&l" : "&f") + (name.contains("алмаз") ? "&b" : "") + name + " &8(&7куплено &a" + i.buy_amount + " &7раз" + ", &b" + total_amount + ", частота &c" + percentage + "&8)" + "\n"; 
			}
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		str += "&8<===============================================>";
		ChatSender.addText(str);
	}
}
