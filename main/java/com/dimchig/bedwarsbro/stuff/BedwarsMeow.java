package com.dimchig.bedwarsbro.stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.CustomScoreboard;
import com.dimchig.bedwarsbro.FileManager;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.MyChatListener;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.MyChatListener.MsgMeowQuee;
import com.dimchig.bedwarsbro.commands.CommandRainbowMessage;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.gui.GuiMinimap.Pos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.command.CommandException;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedwarsMeow {
	public static String filename = "BedwarsBro_MeowMod_" + Main.VERSION + ".txt";
	
	public static ArrayList<MeowMsg> meowMessages;
	String string_for_bed_single = "(1 игрок)";
	String string_for_bed_multi = "(2 и больше игроков)";
	String category_kill_messages = "ПОСЛЕ УБИЙСТВА (заменится слово \"игрок\")";
	String category_final_kill_messages = "ПОСЛЕ ФИНАЛЬНОГО УБИЙСТВА (заменится слово \"игрок\")";
	String category_death_messages = "ПРИ СМЕРТИ (заменится слово \"игрок\" [тот, кто тебя убил])";
	String category_death_void_messages = "ПРИ ПАДЕНИИ В БЕЗДНУ (тут ничего не заменяется, без ников)";
	String category_bed_messages = "СЛОМАНА КРОВАТЬ (заменятся слова \"команда\", \"команды\") Можешь использовать \"" + string_for_bed_single + "\", и \"" + string_for_bed_multi + "\" чтоб отделить сообщения для одиночной игры";
	String category_bed_own_messages = "СЛОМАНА ТВОЯ КРОВАТЬ (заменится слово \"игрок\", тот кто сломал твою кровать)";
	String category_wins_messages = "ПРИ ПОБЕДЕ";
	String category_game_start_messages = "ПРИ ВХОДЕ В ИГРУ (Как только началась катка)";
	String prefix = MyChatListener.PREFIX_BEDWARS_MEOW;
	
	ArrayList<MeowMsg> meowMessagesQueue;
	public boolean IS_USE_COLORS = false;
	public boolean IS_ACTIVE = false;
	public static boolean IS_AUTO_RANDOM_ACTIVE = false;
	
	public enum MsgCase {
		KILL,
		FINAL_KILL,
		DEATH,
		DEATH_VOID,
		BED_SINGLE,
		BED_MULTI,
		BED_OWN,
		WIN,
		GAME_START
	}
	
	public class MeowMsg {
		public MsgCase msgcase;
		public String text;
		public MeowMsg(MsgCase msgcase, String text) {
			this.msgcase = msgcase;
			this.text = text;
		}		
		
		public String getText(String variable) {
			if (Main.isPropUserAdmin(variable)) return null;
			String new_text = "&f" + this.text;
			if (IS_AUTO_RANDOM_ACTIVE) Main.commandRainbowMessageSetter.setRandomPalitra();
			
			if (this.msgcase == MsgCase.KILL || this.msgcase == MsgCase.FINAL_KILL || this.msgcase == MsgCase.DEATH || this.msgcase == MsgCase.BED_OWN) {
				String killer = "игрок";
				new_text = new_text.replace(killer, variable + "&f").replace(upperCaseFirstLetter(killer), variable + "&f").trim();
			} else if (this.msgcase == MsgCase.BED_SINGLE || this.msgcase == MsgCase.BED_MULTI) {
				String team_normal = "команда";
				String team_edited = "команды";
				//variable is a colorcode &c
				TEAM_COLOR team_color = CustomScoreboard.getTeamColorByCode(variable);
				String replace_normal = "ботики";
				String replace_edited = "ботиков";
				String replace_normal_single = "ботик";
				String replace_edited_single = "ботика";
				String color_code = "&" + CustomScoreboard.getCodeByTeamColor(team_color);
				
				if (team_color == TEAM_COLOR.RED) {
					replace_normal = "красные";
					replace_edited = "красных";
					replace_normal_single = "красный";
					replace_edited_single = "красного";
				} else if (team_color == TEAM_COLOR.YELLOW) {
					replace_normal = "желтые";
					replace_edited = "желтых";
					replace_normal_single = "желтый";
					replace_edited_single = "желтого";
				} else if (team_color == TEAM_COLOR.GREEN) {
					replace_normal = "зеленые";
					replace_edited = "зеленых";
					replace_normal_single = "зеленый";
					replace_edited_single = "зеленого";
				} else if (team_color == TEAM_COLOR.AQUA) {
					replace_normal = "голубые";
					replace_edited = "голубых";
					replace_normal_single = "голубой";
					replace_edited_single = "голубого";
				} else if (team_color == TEAM_COLOR.BLUE) {
					replace_normal = "синие";
					replace_edited = "синих";
					replace_normal_single = "синий";
					replace_edited_single = "синего";
				} else if (team_color == team_color.PINK) {
					replace_normal = "розовые";
					replace_edited = "розовых";
					replace_normal_single = "розовый";
					replace_edited_single = "розового";
				} else if (team_color == TEAM_COLOR.GRAY) {
					replace_normal = "серые";
					replace_edited = "серых";
					replace_normal_single = "серый";
					replace_edited_single = "серого";
				} else if (team_color == TEAM_COLOR.WHITE) {
					replace_normal = "белые";
					replace_edited = "белых";
					replace_normal_single = "белый";
					replace_edited_single = "белого";
				}
				
				
				if (this.msgcase == MsgCase.BED_SINGLE) {
					replace_normal = replace_normal_single;
					replace_edited = replace_edited_single;
				}
				
				new_text = new_text
						.replace(team_normal, color_code + replace_normal + "&f")
						.replace(upperCaseFirstLetter(team_normal), color_code + upperCaseFirstLetter(replace_normal) + "&f")
						.replace(team_edited, color_code + replace_edited + "&f")
						.replace(upperCaseFirstLetter(team_edited), color_code + upperCaseFirstLetter(replace_edited) + "&f");
						
			} else if (this.msgcase == MsgCase.WIN || this.msgcase == MsgCase.GAME_START || this.msgcase == MsgCase.DEATH_VOID) {
				new_text = this.text;
			} else {
				return null;
			}
			
			new_text = "&f" + new_text;
			
			if (IS_USE_COLORS) {
				String rainbow_text = CommandRainbowMessage.generateRainbowMessage(ColorCodesManager.removeColorCodes(new_text), 1, -1);
				if (rainbow_text != null) new_text = rainbow_text;
			} else new_text = ColorCodesManager.removeColorCodes(new_text);
			
			if (new_text.length() >= 100) new_text = new_text.substring(0, 100);
			
			return new_text;
		}
	}
	
	public String upperCaseFirstLetter(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}
	
	public BedwarsMeow() {
		meowMessages = new ArrayList<MeowMsg>();
		
		
		
		String readFile = FileManager.readFile(filename);
		if (readFile == null || readFile.length() < 10) {
			initMeowMessages();
		}
		
		readFile();
		
		updateBooleans();
	}
	
	public void updateBooleans() {
		IS_ACTIVE = HintsValidator.isBedwarsMeowActive();
		IS_USE_COLORS = HintsValidator.isBedwarsMeowColorsActive();
	}
	
	public void readFile() {		
		meowMessages = new ArrayList<MeowMsg>();
		meowMessagesQueue = new ArrayList<MeowMsg>();
		String readFile = FileManager.readFile(filename);
		if (readFile == null || readFile.length() < 10) {
			initMeowMessages();
			readFile = FileManager.readFile(filename);
		}
		
		
		if (readFile == null) {
			ChatSender.addText(prefix + "&cФайл с текстом не найден! Проверь &e%appdata%\\Roaming\\.minecraft\\" + filename);
			return;
		}
		
		try {
			
			String[] splitters_text = new String[] {
					category_kill_messages, category_final_kill_messages, category_death_messages, category_death_void_messages,
					category_bed_messages, category_bed_own_messages, category_wins_messages, category_game_start_messages, "randomtexttoletmycodeparselastline"
			};
			MsgCase[] splitters_cases = new MsgCase[] {
					MsgCase.KILL, MsgCase.FINAL_KILL, MsgCase.DEATH, MsgCase.DEATH_VOID, 
					MsgCase.BED_SINGLE, MsgCase.BED_OWN, MsgCase.WIN, MsgCase.GAME_START
			};
			
			//check
			for (int i = 0; i < splitters_cases.length - 1; i++) {
				if (!readFile.contains(splitters_text[i])) {
					ChatSender.addText("\n" + Main.chatListener.PREFIX_BEDWARS_MEOW + "&c&lОшибка файла, категории отсутствуют! Восстанови файл к &lзаводским настройкам &7(&e/meow&7)&c!\n");
					return;
				}
			}
			
			for (int i = 0; i < splitters_cases.length; i++) {
				String lines[] = readFile.split(Pattern.quote(splitters_text[i]))[1].trim().split(Pattern.quote(splitters_text[i + 1]))[0].trim().split("\n");
				for (String line: lines) {
					if (line.contains("===")) continue;
					String l = line.replace("\n", "").trim();
					if (l.length() <= 0) continue;
					MeowMsg m = new MeowMsg(splitters_cases[i], l);
					
					if (m.msgcase == MsgCase.BED_SINGLE) {
						if (!m.text.contains(string_for_bed_single) && !m.text.contains(string_for_bed_multi)) {
							meowMessages.add(new MeowMsg(MsgCase.BED_SINGLE, l));
							meowMessages.add(new MeowMsg(MsgCase.BED_MULTI, l));
							continue;
						} else {
							if (m.text.contains(string_for_bed_single)) {
								m.msgcase = MsgCase.BED_SINGLE;
								m.text = m.text.replace(string_for_bed_single, "").trim();
							} 
							if (m.text.contains(string_for_bed_multi)) {
								m.msgcase = MsgCase.BED_MULTI;
								m.text = m.text.replace(string_for_bed_multi, "").trim();
							}
							
						}
					}
					
					meowMessages.add(m);
				}
			}
		} catch (Exception ex) {
			ChatSender.addText(prefix + "&cФайл с текстом содержит ошибки! Проверь &e%appdata%\\Roaming\\.minecraft\\" + filename);
			return;
		}
	}
	
	public void printMeows() {
		ChatSender.addText(prefix + "&7Загрузка...\n");
		ChatSender.addText("&fСообщения после &cкила&f:");
		String start_prefix = " &8• &f";
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.KILL) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения после &eфинального кила&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.FINAL_KILL) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения после &6смерти&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.DEATH) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения после &6падении в бездну&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.DEATH_VOID) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения после &aкровати&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.BED_SINGLE) ChatSender.addText(start_prefix + m.text + " &a" + string_for_bed_single);
			if (m.msgcase == MsgCase.BED_MULTI) ChatSender.addText(start_prefix + m.text + " &c" + string_for_bed_multi);
		}
		ChatSender.addText("\n&fСообщения после &cсломанной твоей кровати&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.BED_OWN) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения после &bвыиграша&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.WIN) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText("\n&fСообщения при &9входе в игру&f:");
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == MsgCase.GAME_START) ChatSender.addText(start_prefix + m.text);
		}
		ChatSender.addText(prefix + "&eСообщения считаны!");
	}
	
	public String getMeows(MsgCase msgcase) {
		String s = "";
		
		String variable = "";
		String player_name = Minecraft.getMinecraft().thePlayer.getName();
		if (Main.isPropUserAdmin(player_name)) player_name = "Vanya1337";
		if (msgcase == MsgCase.KILL || msgcase == MsgCase.FINAL_KILL || msgcase == MsgCase.BED_OWN) {
			variable = "&e" + player_name;
		} else if (msgcase == MsgCase.DEATH) {
			variable = "&e" + player_name;
		} else if (msgcase == MsgCase.BED_SINGLE || msgcase == MsgCase.BED_MULTI) {
			String[] colors = new String[] {"c","e","a","b","d","9"};
			Random rnd = new Random();
			variable = "&" + colors[rnd.nextInt(colors.length)];
		} else if (msgcase == MsgCase.WIN || msgcase == MsgCase.GAME_START || msgcase == MsgCase.DEATH_VOID) {
			variable = "";
		}
		
		for (MeowMsg m: meowMessages) {
			if (m.msgcase == msgcase) s += " &8• &f" + m.getText(variable) + "\n";
		}
		return s;
	}
	
	public void removeMeessagesWithBadWords() {
		initMeowMessages(true);
		readFile();
	}
	
	public void initMeowMessages() {
		initMeowMessages(false);
	}
	
	public void initMeowMessages(boolean isRemoveBadWords) {
		String[] bad_words = new String[] {
				"соси", "соса", "сосну", "ебал", "трах", "хуе", "ебат", "ебан", "сучк", "сук", "хуй", "пизд", "бля", "пидор", "ебло", "уебо"
		};
		//default messages OLD
		/*String[] killMessages = new String[] {
				"Лох",
				"Изичка",
				"Соси, игрок",
				"Легчайший",
				"Легчайшая",
				"Отсосал",
				"Анскил",
				"Разъебал",
				"Разъебал лоха",
				"игрок ботиха",
				"Соснул, игрок?",
				"На колени, сучка",
				"Игрок, тебе нужно больше тренироваться!",
				"Бот",
				"Вынес мусор",
				"Игрок, ливни пж, не позорься",
				"Лежать + сосать",
				"Ебать ты бот конечно",
				"Ахахахахах",
				"Аххахахаах игрок научись играть",
				"Боже, чел, какой ты нулевый",
				"Дружок, тебе надо подучиться играть",				
				"Игрок аххаха ты полный ноль",
				"Игрок = анскил",
				"Скил игрок = 0",
				"Игрок не позорься, пж",
				"Тебя твоя бабушка играть учила, игрок?",		
				"Игрок, ты не няшка <3",								
				"Игрок = мусор",
				"Игрок = позорище",
				"Игрок = кринж",
				"Предупредил",
				"Игрок, ты счас дико кринжанул",
				"Игрок, тебя походу твои 2 отца играть учили",
				"Игрок, попробуй во время пвп нажимать на мышку",
				"Игрок, суть игры не умирать, а убивать",
				"Спасибо, + килл в статистику",
				"Больше так не делай)",
				"Игрок, ты что, бот?"
		};
			
			String[] finalKillMessages = new String[] {
					"0",
					"L",
					"Удали игру, позорище",
					"Удали игру, и не позорься",
					"Бля, я таких ботов как игрок давно не видел))",
					"Игрок я тебе советую поиграть в крестики-нолики, с твоей реакцией",
					"Вынес в 0",
					"Не расстраивайся, игрок, в следующий раз получиться!",
					"Не расстраивайся, игрок, тебе повезет в следующий раз!",
					"Не грусти, игрок, потренируйся немного и все получиться!",
					"Не грусти, игрок, в следующий раз все получиться!",
					"Игрок, потренируйся и приходи в следующий раз!",
					"Игрок, у тебя скоро все получиться, главное верить в себя!",
					"Это нормально, игрок, нужно уметь принимать поражение",
					"В лобби, ботик",
					"Вот тебе билет в лобби",
					"Этот прицел просто имба",
					"Даже не почуствовал",
					"В лобби, мусор)",
					"Играть научись, клоун",
					"АХхаха что за бота я только что вынес",
					"Игрок, только не плачь",		
					"Ой-ой-ой, не повезло тебе, игрок",
					"Ой, игрок, прости, что трахнул",
					"Я счас трахнул игрок так же, как его отчим",
					"Ахах, игрок, а что с еблом?",
					"Игрок отсосал по полной программе",
					"Вынес безмамного клоуна",
					"Игрок, давай, иди пожалуйся на меня своему отцу, а лучше сразу обоим",
					"Что это сейчас было, игрок. Разучился играть?",
					"Прости, что трахнул",
					"Сори, если трахнул",
					
					"Что, игрок, без кроватки сложно получилось?",
					"Игрок, надо было кровать дефать",
					
					"Я гений этой игры",
					"Я сейчас в очень жосткой форме!",
					"Вот почему я лучший в мире!",
					"Игрок, просто я тренируюсь, пока все отдыхают",
					"Пиздец, что за глупые враги!",
					"Я просто бог в этой игре!",
					"Игрок, с такой игрой тебе бы в роблокс!",
					"Твой отец хуесос, сынок",
					"Поймал на ашибке!",
					"Па аднаму!"
			};
			
			String s = " " + string_for_bed_single;
			String m = " " + string_for_bed_multi;
			
			String[] bedMessages = new String[] {
					"Фуу... Какая не вкусная кроватка у команды",
					"Ой, команда, а что с ебалом?" + s,
					"Ой, команда, а что с ебалами?" + m,
					"Ахаха, кто-то теперь не возродится",
					"Команда это нулевый бот" + s,
					"Команда это нулевые ботики" + m,
					"У команды теперь не только мамы нету" + s,
					"У команды теперь не только мам нету" + m,
					"Походу команда счас проиграет" + s,
					"Походу команда счас проиграют" + m,
					"Мда, команда это анскильный мусор",
					"Не повезло тебе, команда, попасть со мной в одну катку" + s,
					"Не повезло вам, команда, попасть со мной в одну катку" + m,
					"Не позвело, не повезло тебе, команда" + s,
					"Не позвело, не повезло вам, команда" + m,
					"Команда, а что это у тебя с ебалом? Кровать жива?" + s,
					"Команда, а что это вас с ебалом? Кровать жива?" + m,
					"Команда скоро отправится в лобби)" + s,
					"Команда скоро отправятся в лобби)" + m,
					"Ой, команда, прости что трахнул" + s,
					"Ой, команда, простите что трахнул" + m, 
					"Команда ботик теперь без кроватки(" + s,
					"Команда ботики теперь без кроватки(" + m,
					"Команда, только не плачь" + s,
					"Команда, только не плачьте" + m,
					
					"Бля, какой же команда глупый!" + s,
					"Бля, какие же команда глупые!" + m,
			};
			
			String[] deathMessages = new String[] {
					"Игрок, тебе просто повезло)",
					"На лакичах чисто",
					"Игрок, только с палкой и умеешь",
					"Игрок, тебе пизда",
					"Тебе повезло",
					"Игрок, с читами норм?",
					"У меня залагало)",
					"Игрок тебе пиздец!",
					"Я тебе сейчас ебало сломаю",
					"Готовь попку, игрок",
					"Я просто даже не напрягался",
					
					"Игрок, ты что, с читом играешь?!",
					"Как же меня бесит этот уебок",
					"Мне не везет, это пиздец!"
			};
			
			String[] winMessages = new String[] {
					"ez соснули лалки",
					"Ахах сервер даунов",
					"Как же это было легко",
					"Я даже не напрягался",
					"А че, мы уже победили? Лол",
					"Даже не почувствовал",
					"Очередная победа...",
					"Чего так просто?",
					"Лежать + сосать",
					"Мда, я думал будет по сложнее(",
					"Бля, я устал выигрывать!"
			};
			
			String[] gameStartMessages = new String[] {
					"Ребятки, всем удачной игры!",
					"Ребят, давайте играть без читов",
					"Кто ливнет - гей",
					"*Смех* а патом \"Пагнали!\"",
					"На этом сервере играют одни дауны",
					"Сейчас будет очередная изичная катка...",
					"Я новичек, по этому можете, пожалуйста, не убивать меня?",
					"Я буду строить форт, не нападайте пж",
					"Ну что ж, очередная катка с ботиками",
					"Народ, давайте не обзываться, ведь это очень обидно. Давайте играть дружно и мирно!",
					"Кто тоже играет с флюском + в чат",
					"Кто тоже играет с матиксом + в чат",
					"Кто тоже играет с модом &c&lBedwars&f&lBro?",
					"мод &eDexland Meow &f< &c&lBedwars&f&lBro",
					"Всем привет! Кто меня будет рашить - дурак",
					"Я в топ форме, вам пиздец!"
			};*/
		
		String[] killMessages = new String[] {
				"Игрок, тебе нужно больше тренироваться!",
				"Дружок, тебе надо тренироваться",
				"Малыш, тебе надо тренироваться",
				"Игрок, ты не няшка <3",								
				"Игрок, последнее предупреждение!",
				"Игрок, попробуй во время пвп нажимать на мышку",
				"Игрок, суть игры не умирать, а убивать",
				"Спасибо, + килл в статистику",
				"Малыш, больше так не делай)",
		};
			
			String[] finalKillMessages = new String[] {
					"0",
					"L",
					"Не расстраивайся, игрок, в следующий раз получиться!",
					"Не расстраивайся, игрок, тебе повезет в следующий раз!",
					"Не грусти, игрок, потренируйся немного и все получиться!",
					"Не грусти, игрок, в следующий раз все получиться!",
					"Игрок, потренируйся и приходи в следующий раз!",
					"Игрок, у тебя скоро все получиться, главное верить в себя!",
					"Это нормально, игрок, нужно уметь принимать поражение",
					"Этот прицел просто имба",
					"В лобби, малыш)",

					"Игрок, только не плачь",		
					"Ой-ой-ой, не повезло тебе, игрок",	
					"Что, игрок, без кроватки сложно получилось?",
					"Игрок, надо было кровать дефать",
					
					"Я гений этой игры",
					"Я сейчас в очень жосткой форме!",
					"Вот почему я лучший в мире!",
					"Игрок, просто я тренируюсь, пока все отдыхают",
					"Я просто бог в этой игре!",
					"Поймал на ашибке!",
					"А что, игрок, без кроватки сложно получилось?"
			};
			
			String s = " " + string_for_bed_single;
			String m = " " + string_for_bed_multi;
			
			String[] bedMessages = new String[] {
					"Фуу... Какая не вкусная кроватка у команды",
					"Походу команда счас проиграет" + s,
					"Походу команда счас проиграют" + m,
					"Не повезло тебе, команда, попасть со мной в одну катку" + s,
					"Не повезло вам, команда, попасть со мной в одну катку" + m,
					"Команда скоро отправится в лобби)" + s,
					"Команда скоро отправятся в лобби)" + m,
					"Ой, команда, прости" + s,
					"Ой, команда, простите" + m, 
					"Команда теперь без кроватки(" + s,
					"Команда теперь без кроватки(" + m,
					"Команда, только не плачь" + s,
					"Команда, только не плачьте" + m,
			};
			
			String[] bedOwnMessages = new String[] {
					"Игрок, как ты посмел сломать мою кровать?!",
					"Игрок, спасибо, теперь я не усну(",
					"Как мне теперь возродиться, игрок?",
					"Да я и без кровати справлюсь, игрок",
					"НЕЕЕТ! Моя кроватка(",
			};
			
			String[] deathMessages = new String[] {
					"Игрок, тебе просто повезло)",
					"Игрок, на лакичах чисто",
					"Игрок, только с палкой и умеешь",
					"Тебе просто повезло, игрок",
					"Игрок, с читами норм?",
					"У меня залагало)",
					"Готовь попку, игрок",
					"Ну, игрок, ты доигрался",
			};
			
			String[] deathVoidMessages = new String[] {
					
			};
			
			String[] winMessages = new String[] {
					"Ура, победа!",
					"GG",
					"Не почувствовал",
					"Как же было легко",
					"Какой же это сервер ботиков...",
			};
			
			String[] gameStartMessages = new String[] {
					"Ребятки, всем удачной игры!",
					"Ребят, давайте играть без читов",
					"Малыши, не обзывайтесь во время игры! Играйте дружно и мирно!",
					"Так, ботики, ливните из катки!",		
					"Кто тоже играет с самым имбовым модом для бедварса?",
					"Кто тоже играет с самым крутым модом для бедварса?",
					"Кто тоже играет с самым лучшим модом для бедварса?"
			};
		
		
		
		String str = "В этом файле ты можешь редактировать и добавлять свои сообщения\nПосле изменений сохрани файл и в майнкрафте напиши /meow, и там ОБНОВИТЬ СООБЩЕНИЯ\nЕсли возникла ошибка, или что-то не работает, удали этот файл и нажми ОБНОВИТЬ СООБЩЕНИЯ\nНе используй \"===\" и не используй перенос строки внутри сообщений!\n\n";
		
		String[] splitters_text = new String[] {
				category_kill_messages, category_final_kill_messages, category_death_messages, category_death_void_messages,
				category_bed_messages, category_bed_own_messages, category_wins_messages, category_game_start_messages
		};
		String[][] splitters_messages = new String[][] {
				killMessages, finalKillMessages, deathMessages, deathVoidMessages,
				bedMessages, bedOwnMessages, winMessages, gameStartMessages
		};
		
		if (isRemoveBadWords) {
			for (String[] messages: splitters_messages) {
				for (int i = 0; i < messages.length; i++) {
					boolean isOk = true;
					
					for (String bad_word: bad_words) {
						if (messages[i].toLowerCase().contains(bad_word)) {
							isOk = false;
							break;
						}
					}
					if (!isOk) {
						ChatSender.addText(Main.chatListener.PREFIX_BEDWARS_MEOW + "&fУдалено &c" + messages[i]);
						messages[i] = "";
					}
				}
			}
		}
		
		
		for (int i = 0; i < splitters_text.length; i++) {
			str += "===" + splitters_text[i] + "===\n";
			for (String s2: splitters_messages[i]) {
				if (s2.length() == 0) continue;
				str += s2 + "\n";
			}
			str += "\n\n";
		}
		
		FileManager.initFile(filename);
		FileManager.writeToFile(str, filename, false);
	}
	
	
	
	
	
	
	
	
	public String getNextMessage(MsgCase msgcase, String variable) {
		if (!IS_ACTIVE) return null;
		
		//ChatSender.addText(prefix + "&fVariable = &b" + variable);
		
		MeowMsg msg = null;
		if (meowMessagesQueue == null) meowMessagesQueue = new ArrayList<MeowMsg>();
		for (MeowMsg m: meowMessagesQueue) {
			if (m.msgcase == msgcase) {
				msg = m;
				meowMessagesQueue.remove(m);
				break;
			}
		}
		
		boolean areMessagesLeft = false;
		for (MeowMsg m: meowMessagesQueue) {
			if (m.msgcase == msgcase) {
				areMessagesLeft = true;
				break;
			}
		}
		if (!areMessagesLeft) {
			//no messages, add to queue
			ArrayList<MeowMsg> arr = new ArrayList<MeowMsg>();
			for (MeowMsg m: meowMessages) {
				if (m.msgcase == msgcase) arr.add(m);
			}
			if (arr.size() == 0) return null;
			
			Collections.shuffle(arr);
			if (msg == null) {
				msg = arr.get(0);
				arr.remove(0);
			} else if (arr.size() > 1) {
				while (arr.get(0).text.equals(msg.text)) Collections.shuffle(arr);
			}
			meowMessagesQueue.addAll(arr);
		}
		
		/*ChatSender.addText("queue:\n");
		for (MeowMsg m: meowMessagesQueue) {
			//ChatSender.addText("  &f" + m.msgcase + " &8: &e" + m.text);
		}*/

		return msg.getText(variable);
	}
}
