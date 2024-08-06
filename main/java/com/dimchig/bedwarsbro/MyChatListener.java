package com.dimchig.bedwarsbro;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.BaseProps.MyMessage;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_STATE;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiMinimap.MyBed;
import com.dimchig.bedwarsbro.stuff.BWBed;
import com.dimchig.bedwarsbro.stuff.HintsBaseRadar;
import com.dimchig.bedwarsbro.stuff.HintsBedScanner;
import com.dimchig.bedwarsbro.stuff.HintsValidator;
import com.dimchig.bedwarsbro.stuff.LobbyBlockPlacer;
import com.dimchig.bedwarsbro.stuff.WinEmote;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow.MsgCase;
import com.dimchig.bedwarsbro.stuff.LightningLocator.MyLightning;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class MyChatListener {
	Minecraft mc;
	public static boolean IS_MOD_ACTIVE;
	public static boolean IS_IN_GAME;
	public static long GAME_start_time = -1;
	public static int GAME_total_death = -1;
	public static int GAME_total_kills = -1;
	public static int GAME_total_beds = -1;

	public static String nickChanger = "";

	public static String nickSpoof_name = "";
	public static String nickSpoof_new_name = "";

	public MyChatListener() {
		mc = Minecraft.getMinecraft();
		IS_MOD_ACTIVE = true;
		IS_IN_GAME = false;
		removeNextMessage = false;
		initChatMessages();

		for (ChatMessage chatMessage : chatMessages) {
			for (ChatMessage chatMessage2 : chatMessages) {
				if (chatMessage.type == chatMessage2.type && !chatMessage.message.equals(chatMessage2.message)) {
					ChatSender.addText("DUBLICATE CHAT MESSAGE - " + chatMessage.type);
				}
			}
			// System.out.println("Type: " + chatMessage.type + "\nMessage: " +
			// chatMessage.message + "\nArgs: " + Arrays.toString(chatMessage.elements) +
			// "\n");
		}

		meowMessagesQuee = new ArrayList<MsgMeowQuee>();
	}

	public static class GameRecovery {
		public long time_started;
		public BWBed game_bed;
		public ArrayList<MyBed> minimap_beds;
		public MyLightning last_lightning;

		public GameRecovery(long time_started, BWBed game_bed, ArrayList<MyBed> minimap_beds,
				MyLightning last_lightning) {
			this.time_started = time_started;
			this.game_bed = game_bed;
			this.minimap_beds = minimap_beds;
			this.last_lightning = last_lightning;
		}
	}

	public static BWBed GAME_BED;
	public static GameRecovery GAME_RECOVERY;
	public static boolean removeNextMessage = false;

	public static class ChatMessage {
		public CHAT_MESSAGE type;
		public String message;
		public String[] elements;
		public String[] element_values;
		public boolean isMustBeEqual;

		public ChatMessage(CHAT_MESSAGE type, String message, String[] elements) {
			this.init(type, message, elements, false);
		}

		public ChatMessage(CHAT_MESSAGE type, String message, String[] elements, boolean isMustBeEqual) {
			this.init(type, message, elements, isMustBeEqual);
		}

		private void init(CHAT_MESSAGE type, String message, String[] elements, boolean isMustBeEqual) {
			this.type = type;
			this.message = message.trim();

			ArrayList<String> arr = new ArrayList<String>();
			// clear elements from empty
			for (int i = 0; i < elements.length; i++) {
				if (elements[i].length() > 0) {
					arr.add("%" + elements[i].trim() + "%");
				}
			}
			this.elements = arr.toArray(new String[arr.size()]);
			this.element_values = new String[this.elements.length];
			this.isMustBeEqual = isMustBeEqual;
		}

	}

	public static int TIME_MEOW_MESSAGES_CHECK_FREQUENCY = 3000;

	public static class MsgMeowQuee {
		public String text;
		public long time;
		public boolean isInGameOnly;

		public MsgMeowQuee(String text, long time, boolean isInGameOnly) {
			this.text = text;
			this.time = time;
			this.isInGameOnly = isInGameOnly;
		}
	}

	public static List<MsgMeowQuee> meowMessagesQuee;

	public static MyStatistic myStatistic = null;

	public static class MyStatistic {
		public String player;
		public int category_kills_cnt;
		public int category_kills_place;
		public int category_deathes_cnt;
		public int category_deathes_place;
		public int category_games_cnt;
		public int category_games_place;
		public int category_wins_cnt;
		public int category_wins_place;
		public int category_beds_cnt;
		public int category_beds_place;

		public float percentage_kill_to_death;
		public float percentage_games_to_wins;
		public int best_ranking;

		public MyStatistic(String player) {
			this.player = player;
			this.category_kills_cnt = 0;
			this.category_kills_place = 0;
			this.category_deathes_cnt = 0;
			this.category_deathes_place = 0;
			this.category_games_cnt = 0;
			this.category_games_place = 0;
			this.category_wins_cnt = 0;
			this.category_wins_place = 0;
			this.category_beds_cnt = 0;
			this.category_beds_place = 0;

			this.percentage_kill_to_death = -1;
			this.percentage_games_to_wins = -1;
			this.best_ranking = -1;
		}
	}

	// &c&6&e&a&b&9&d - rainbow
	// &c&l&6&l&e&l&a&l&b&l&9&l&d&l - rainbow bold
	public static String DIMCHIG_NAME = "&c&lD&6&li&e&lm&a&lC&b&lh&9&li&d&lg";
	public static String DIMCHIG_NAME_GOLD = "&e&lDim&6&lChig";

	enum CHAT_MESSAGE {
		NONE, CHAT_LEFT_GAME, CHAT_GAME_STARTED, CHAT_BEDWARS_GAME_STARTED_TIPS, CHAT_JOINED_MIDGAME,
		CHAT_JOINED_PREGAME, CHAT_LEFT_PREGAME, CHAT_SUICIDE, CHAT_SUICIDE_VOID, CHAT_KILLED_BY_VOID,
		CHAT_KILLED_BY_PLAYER, CHAT_TEAM_DESTROYED, CHAT_TEAM_BED_BROKEN, CHAT_TEAM_COLOR_CHOSEN,
		CHAT_TEAM_ALREADY_CHOSEN, CHAT_TEAM_IS_FULL, CHAT_SHOP_ITEM_BOUGHT, CHAT_SHOP_NOT_ENOUGH_RESOURCES,
		CHAT_UPGRADE_BOUGHT, CHAT_GENERATOR_DIAMOND_LEVELED_UP, CHAT_GENERATOR_EMERALD_LEVELED_UP, CHAT_TRAP_ACTIVATED,
		CHAT_SERVER_RESTART, CHAT_TELEPORTATION_TO_HUB, CHAT_CONNECTING_TO_LOBBY, CHAT_HUB_CHAT_PLAYER_MESSAGE,
		CHAT_YOU_WERE_KICKED, CHAT_ADS, CHAT_GAME_STARTS_IN_SECONDS, CHAT_GAME_CHAT_LOCAL, CHAT_GAME_CHAT_GLOBAL,
		CHAT_GAME_CHAT_SPECTATOR, CHAT_GAME_CHAT_PREGAME, CHAT_PREGAME_FASTSTART_REJECT, CHAT_LOBBY_DONATER_GREETING,
		CHAT_PLAYER_BANNED, CHAT_PREGAME_NOT_ENOUGH_PLAYERS_TO_START, CHAT_BEDWARS_END_TEAM_WON,
		CHAT_BEDWARS_END_TOP_KILLER, CHAT_BEDWARS_END_TOP_KILLER_UNKNOWN,

		CHAT_LOBBY_PARTY_INVITE, CHAT_LOBBY_PARTY_INVITE_REJECTED, CHAT_LOBBY_PARTY_WARP,
		CHAT_LOBBY_PARTY_PLAYER_OFFLINE, CHAT_LOBBY_PARTY_DISBANDED, CHAT_LOBBY_PARTY_ALREADY_IN_PARTY,
		CHAT_LOBBY_PARTY_OFFLINE, CHAT_LOBBY_PARTY_YOU_ARE_NOT_IN_PARTY, CHAT_LOBBY_PARTY_PLAYER_KICKED,
		CHAT_LOBBY_PARTY_PLAYER_LEFT, CHAT_LOBBY_PARTY_CHAT_ENTER_MESSAGE, CHAT_LOBBY_PARTY_CHAT_MESSAGE,
		CHAT_LOBBY_PARTY_NO_PERMISSION, CHAT_LOBBY_PARTY_DISBAND_REQUEST, CHAT_LOBBY_PARTY_NOT_ENOUGH_SPACE,
		CHAT_LOBBY_PARTY_REQUEST_ALREADY_SENT, CHAT_LOBBY_PARTY_YOU_WERE_WARPED, CHAT_LOBBY_PARTY_OWNER_LEFT,
		CHAT_LOBBY_PARTY_REQUEST, CHAT_LOBBY_PARTY_YOU_ACCEPTED_REQUEST, CHAT_LOBBY_PARTY_NEW_LEADER,
		CHAT_LOBBY_PARTY_COMMANDS_DONT_WORK_IN_LOBBY, CHAT_LOBBY_PARTY_INFO, CHAT_LOBBY_PARTY_JOIN_REQUEST,

		CHAT_LOBBY_STATS_PLAYER, CHAT_LOBBY_STATS_CATEGORY, CHAT_PARTY_ON_CREATE, CHAT_GAME_CANT_USE_COMMANDS,
		CHAT_GAME_WAIT_SECONDS, CHAT_GAME_YOU_CANT_USE_COLORS, CHAT_GAME_YOU_CANT_USE_COLOR, CHAT_GAME_ANTI_CHEAT_KICK,
		CHAT_HUB_ANTIFLOOD, CHAT_YOU_ARE_TELEPORTED_TO_LOBBY_DUE_TO_FULL_ARENA, CHAY_YOU_ALREADY_ON_SERVER,
		LOGIN_WITH_PASSWORD, LOGIN_WITH_PASSWORD_CHEATMINE,

	}

	public static String SOUND_BED_BROKEN = "random.wood_click";
	public static String SOUND_TEAM_DESTROYED = "fireworks.blast";
	public static String SOUND_GAME_END = "fireworks.twinkle_far";
	public static String SOUND_REJECT = "note.bassattack";
	public static String SOUND_UPGRADE_BOUGHT = "random.anvil_land";
	public static String SOUND_TRAP_ACTIVATED = "note.pling";
	public static String SOUND_PLAYER_STATS = "random.orb";
	public static String SOUND_TEAM_CHOSEN = "random.click";
	public static String SOUND_PARTY_CREATED = "fireworks.twinkle_far";
	public static String SOUND_PARTY_CHAT = "random.orb";
	public static String SOUND_RADAR_FAR = "note.harp";
	public static String SOUND_RADAR_CLOSE = "note.pling";
	public static String SOUND_EMOTE = "random.pop";

	// PREFIXES
	public static String PREFIX_BEDWARSBRO = "&r&cBedwars&fBro &8▸ &r";

	public static String PREFIX_PARTY = "&r&6&lParty &8▸ &r";
	public static String PREFIX_BED = "&r&c&lBed&8 ▸ &r";
	public static String PREFIX_TEAM = "&r&6&lTeam&8 ▸ &r";
	public static String PREFIX_ANTICHEAT = "&r&6&lAntiCheat&8 ▸ §r";
	public static String PREFIX_UPGRADES = "&r&b&lUpgrades&8 ▸ §r";
	public static String PREFIX_HINT = "&r&a&lHints&8 ▸ §r";
	public static String PREFIX_HINT_BED_SCANNER = "&r&cЗащита &cкровати&8 ▸ §r";
	public static String PREFIX_HINT_RADAR = "&r&a&lRadar&8 ▸ §r";
	public static String PREFIX_HINT_FINDER = "&r&b&lПоиск&8 ▸ §r";
	public static String PREFIX_HINT_RESOURCES_FINDER = "&r&e&lРесурсы&8 ▸ §r";
	public static String PREFIX_ANTIFLOOD = "§6&lАнтиФлуд &8▸ &r";
	public static String PREFIX_DANGER_ALERT = "§c&lОПАСНОСТЬ &8▸ &r";
	public static String PREFIX_TNT_JUMP = "§4&lTNT &8▸ &r";
	public static String PREFIX_WATER_DROP = "§b&lWater Drop &8▸ &r";
	public static String PREFIX_BEDWARS_MEOW = "§e&lMeow &8▸ &r";
	public static String PREFIX_DIMCHIG_JOINED = "§r§c§l› &c[&c&lYou&f&lTube&f] &r";
	public static String PREFIX_ZERO_DEATH = "&r&a&lZero&c&lDeath&8 ▸ §r";
	public static String PREFIX_FRIENDS = "&r&6Друзья&8 ▸ §r";
	public static String PREFIX_MUTED = "&r&cMuted&8 ▸ §r";
	public static String PREFIX_FRIEND_IN_CHAT = "&f&l<Друг> &r";

	public static int GAME_MAP_PLAYERS_MAX_SIZE = 0;

	public static ArrayList<ChatMessage> chatMessages;

	public static void initChatMessages() {
		chatMessages = new ArrayList<ChatMessage>();
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LEFT_GAME,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fпокинул игру§r", new String[] { "skip", "player", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LEFT_PREGAME,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fпокинул игру §r§c%cnt_players_current%/%cnt_players_total%§r",
				new String[] { "skip", "player", "cnt_players_current", "cnt_players_total" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_STARTED,
				"§r§eЗащити свою кровать и сломай чужие кровати.", new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_JOINED_PREGAME,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fподключился к игре §r§a%cnt_players_current%/%cnt_players_total%§r",
				new String[] { "skip", "player", "cnt_players_current", "cnt_players_total" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_JOINED_MIDGAME,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fподключился к игре§r", new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_SUICIDE, "§r§6§lBedWars §r§8%skip% §r%player% §r§fпогиб.§r",
				new String[] { "skip", "player", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_SUICIDE_VOID,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fупал в бездну.§r", new String[] { "skip", "player", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_KILLED_BY_VOID,
				"§r§6§lBedWars §r§8%skip% §r%victim% §r§fбыл скинут в бездну игроком §r%killer%§r§f.§r",
				new String[] { "skip", "victim", "killer" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_KILLED_BY_PLAYER,
				"§r§6§lBedWars §r§8%skip% §r%victim% §r§fбыл убит игроком §r%killer%§r§f.§r",
				new String[] { "skip", "victim", "killer" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TEAM_DESTROYED,
				"§r§6§lBedWars §r§8%skip% §r§fКоманда §r%team% §r§fуничтожена!§r",
				new String[] { "skip", "team", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TEAM_BED_BROKEN,
				"§r§6§lBedWars §r§8%skip% §r%player% §r§fразрушил кровать команды §r%team%§r§f!§r",
				new String[] { "skip", "player", "team" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TEAM_COLOR_CHOSEN,
				"§r§6§lBedWars §r§8%skip% §r§fВы выбрали команду §r%team%§r§f!§r",
				new String[] { "skip", "team", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TEAM_ALREADY_CHOSEN,
				"§r§6§lBedWars §r§8%skip% §r§cВы уже выбрали текущую команду.§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TEAM_IS_FULL,
				"§r§6§lBedWars §r§8%skip% §r§cДанная команда заполнена.§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_SHOP_ITEM_BOUGHT, "§r§7Вы купили §r§a%item%§r §ax%amount%§r",
				new String[] { "item", "amount" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_SHOP_NOT_ENOUGH_RESOURCES,
				"§r§6§lBedWars §r§8%skip% §r§cУ Вас недостаточно ресурсов!§r", new String[] { "skip", "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_UPGRADE_BOUGHT,
				"§r§6§lBedWars §r§8%skip% §r§fИгрок §r§f%player% §r§fпрокачал улучшение §r§b%upgrade% §r§fдо уровня §r§b%level%§r§f!",
				new String[] { "skip", "player", "upgrade", "level" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GENERATOR_DIAMOND_LEVELED_UP,
				"§r§6§lBedWars §r§8%skip% §r§fГенераторы §r§bалмазов§r§f прокачаны до уровня §r§a%level%§r§f",
				new String[] { "skip", "level", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GENERATOR_EMERALD_LEVELED_UP,
				"§r§6§lBedWars §r§8%skip% §r§fГенераторы §r§aизумрудов§r§f прокачаны до уровня §r§a%level%§r§f.§r",
				new String[] { "skip", "level", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TRAP_ACTIVATED,
				"§r§6§lBedWars §r§8%skip% §r§cЛовушка сработала, на вашем острове враг!§r",
				new String[] { "skip", "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_SERVER_RESTART,
				"§r§cПерезагрузка сервера через §r§c§l%seconds%§r§c секунд!§r", new String[] { "seconds", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_TELEPORTATION_TO_HUB,
				"§r§6§lBedWars §r§8%skip% §r§aТелепортация в лобби....§r", new String[] { "skip", "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_CONNECTING_TO_LOBBY, "§aПодключение к %lobby%...§r",
				new String[] { "lobby", "" })); // if not %lobby% contains lobby - it is game

		// chatMessages.add(new
		// ChatMessage(CHAT_MESSAGE.CHAT_HUB_CHAT_PLAYER_MESSAGE,"§r§r%player% §8
		// §8%skip% §7§r%message%§r", new String[]{"player", "skip", "message"}));
		// §r§r§r§d[§r§d§lLEGEND§r§d]§r§f ~§r§d§lGirlPro§r§e §r§8 §r§8→
		// §r§7§r§r§e1§r§e2§r§e3§r§e§r
		// §r§r§r%player% §r§8 §r§8%skip% §r§7§r§r§e1§r§e2§r§e3§r§e§r
		// §r§r§r%player% §r§8 §r§8%skip% §r§7§r%message%§r"

		// §r§r§7[Игрок]§r 2011an §r§8 §r§8→ §r§7§r§7кто в дс пиши я 11 лет§r
		// §r§r§r%player% §r§8 §r§8%skip% §r§7§r%message%$bwbrotagend$

		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_YOU_WERE_KICKED,
				"§r§fТы перемещен в лобби §r§8▸ §r§cИзвините, но вас кикнули", new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_STARTS_IN_SECONDS,
				"§r§6§lBedWars §r§8%skip% §r§eДо старта осталось §r§c%seconds%§r§e секунд...§r",
				new String[] { "skip", "seconds", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_CHAT_LOCAL,
				"§r§r%teamcolor%[⚑] %player%§8%skip% §r§7%message%§r",
				new String[] { "teamcolor", "player", "skip", "message" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_CHAT_GLOBAL,
				"§r§r%teamcolor%[Всем] %player%§8%skip% §r§7%message%§r",
				new String[] { "teamcolor", "player", "skip", "message" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_CHAT_SPECTATOR,
				"§r§r§7Наблюдатель §8| §f%player% §8%skip% §7§r%message%§r",
				new String[] { "player", "skip", "message" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_CHAT_PREGAME, "§r§r§f%player% §8→ §7§r§7§7%message%§r",
				new String[] { "player", "message" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_PREGAME_FASTSTART_REJECT,
				"§cНедостаточно прав для запуска игры.§r", new String[] { "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_DONATER_GREETING,
				"§r§c§l› §r§fИгрок §r%player% §r§f%greeting%§r", new String[] { "player", "greeting" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_PLAYER_BANNED,
				"§r§6АнтиЧит§r§8 %skip% §rИгрок §r§c%player% §r§fбыл временно §r§cзабанен §r§fпо подозрению в использовании читов.§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_PREGAME_NOT_ENOUGH_PLAYERS_TO_START,
				"§r§6§lBedWars §r§8%skip% §r§cНедостаточно игроков для старта.§r", new String[] { "skip" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_BEDWARS_END_TEAM_WON,
				"§r§f                 Победила команда - §r%team%§r", new String[] { "team" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_BEDWARS_END_TOP_KILLER,
				"§r%skip%                      §r%place% §r§7- §r§7%player% §r§7(%kills_cnt%)§r",
				new String[] { "skip", "place", "player", "kills_cnt" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_BEDWARS_END_TOP_KILLER_UNKNOWN,
				"§r%skip%                      §r%place% §r§7- §r§cN/A§r", new String[] { "skip", "place" }));

		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_HUB_CHAT_PLAYER_MESSAGE,
				"§r§r%player%§r§8%skip%§r§7%message%$bwbrotagend$", new String[] { "player", "skip", "message" }));

		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_INVITE,
				"§r§6Party §r§8%skip% §r§fВы отправили приглашение игроку §r§e%player%§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_INVITE_REJECTED,
				"§r§6Party §r§8%skip% §r§cВаше приглашение в пати игрока §r§c§l%player% §r§cистекло§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_WARP,
				"§r§6Party §r§8%skip% §r§cВы отправили запрос на перемещение игроков из пати к вам§r",
				new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_PLAYER_OFFLINE,
				"§r§6Party §r§8%skip% §r§cИгрок §r§c§l%player%§r§c кикнут из пати поскольку он оффлайн %reason%§r",
				new String[] { "skip", "player", "reason" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_DISBANDED,
				"§r§6Party §r§8%skip% §r§cПати расформировано.§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_STATS_PLAYER,
				"§r§e§lСтатистика §r§c§lBedWars §r§8%skip% §r§f%player%§r", new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_STATS_CATEGORY,
				"§r§e%skip% §r§f%category%: §r§e%cnt% §r§a(Место в топе #%place%)§r",
				new String[] { "skip", "category", "cnt", "place" }));
		// ==PARTY==
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_ALREADY_IN_PARTY,
				"§r§6Party §r§8%skip% §r§cИгрок уже состоит в пати!§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_OFFLINE,
				"§r§6Party §r§8%skip% §r§fИгрок с таким ником не в сети§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_YOU_ARE_NOT_IN_PARTY,
				"§r§6Party §r§8%skip% §r§cВы не состоите в пати§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_PLAYER_KICKED,
				"§r§6Party §r§8%skip% §r§fИгрок §r§e%player%§r§f был кикнут из пати§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_PLAYER_LEFT,
				"§r§6Party §r§8%skip% §r§fИгрок §r§e%player%§r§f вышел из пати§r", new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_CHAT_ENTER_MESSAGE,
				"§r§6Party §r§8%skip% §r§cУкажите свое сообщение§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_YOU_ACCEPTED_REQUEST,
				"§r§6Party §r§8%skip% §r§fВы приняли запрос!§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_NEW_LEADER,
				"§r§6Party §r§8%skip% §r§fИгрок §r§e%player% §r§fстал новым создателем пати§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_COMMANDS_DONT_WORK_IN_LOBBY,
				"§r§6Party §r§8%skip% §r§fКоманды пати работают только в бедрварс лобби/игре.§r",
				new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_CHAT_MESSAGE,
				"§r§6Party §r§8%skip% §r§e%player% §r§fпишет:§r§7 %message%§r",
				new String[] { "skip", "player", "message" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_NO_PERMISSION,
				"§r§6Party §r§8%skip% §r§cНа это есть права только у создателя пати§r", new String[] { "skip" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_DISBAND_REQUEST,
				"§r§6Party §r§8%skip% §r§cСоздатель пати %player% запросил расформирование!§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_NOT_ENOUGH_SPACE,
				"§r§6Party §r§8%skip% §r§cНа этой арене не хватает слотов для всего пати, выберите другую арену§r",
				new String[] { "skip" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_REQUEST_ALREADY_SENT,
				"§r§6Party §r§8%skip% §r§cЭтому игроку уже отправили запрос, подождите§r", new String[] { "skip" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_OWNER_LEFT,
				"§r§6Party §r§8%skip% §r§cСоздатель пати %player% покинул его, пати сейчас будет расфомировано!§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_YOU_WERE_WARPED,
				"§r§6Party §r§8%skip% §r§cВас переместил к себе создатель пати§r", new String[] { "skip" }));
		chatMessages
				.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_INFO, "▸ Информация о пати", new String[] { "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_LOBBY_PARTY_JOIN_REQUEST,
				"§e--------------------------------------------%skip1%"
						+ "§e%player_name% §fпригласил вас в пати%skip2%"
						+ "§eНажмите сюда §fчтобы присоединиться! У вас есть 60 секунд.%skip3%$bwbrotagend$",
				new String[] { "skip1", "player_name", "skip2", "skip3" }));

		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_YOU_ARE_TELEPORTED_TO_LOBBY_DUE_TO_FULL_ARENA,
				"§r§c§l| §r§fТы перемещен в лобби §r§8%skip% §r§cАрена заполнена.§r", new String[] { "skip", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAY_YOU_ALREADY_ON_SERVER, "§cВы уже на сервере!§r",
				new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_PARTY_ON_CREATE,
				"Используйте §r§e/party ник §r§fчтобы пригласить новых игроков!", new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_CANT_USE_COMMANDS,
				"§r§cНельзя использовать такую команду в игре, для выхода пишите /leave§r", new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_WAIT_SECONDS,
				"§r§c§l| §r§fПожайлуста подождите §r§c%seconds% секунд §r§fсек.§r", new String[] { "seconds", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_YOU_CANT_USE_COLORS,
				"У тебя нет прав использовать цвета в чате", new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_YOU_CANT_USE_COLOR, "Вы не можете использовать цвет",
				new String[] { "", "" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_GAME_ANTI_CHEAT_KICK,
				"§r§6АнтиЧит§r§8 %skip% §rИгрок §r§c%player% §r§fбыл §r§cкикнут §r§fпо подозрению в использовании читов.§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_HUB_ANTIFLOOD,
				"§r§6АнтиФлуд§r§8 %skip% §rИгрок §r§c%player% §r§fбыл §r§cзамучен §r§fс причиной: '§r§cПовторение однотипных сообщений§r§f'§r",
				new String[] { "skip", "player" }));
		chatMessages.add(new ChatMessage(CHAT_MESSAGE.LOGIN_WITH_PASSWORD,
				"§r§c§l| §r§fАвторизируйтесь %skip% §r§e/login [пароль]§r", new String[] { "skip" }));

		chatMessages.add(new ChatMessage(CHAT_MESSAGE.LOGIN_WITH_PASSWORD_CHEATMINE,
				"§r§c§l%skip% §r§fАвторизируйтесь §r§7§l› §r§c§l/login §r§c[§r§c§lпароль§r§c]",
				new String[] { "skip" }));

		// add bedwars game started
		String[] chatMessagesGameStarted = { "§r§c§lКРОВАТНЫЕ ВОЙНЫ", "§r§f                          ",
				"§r§eПокупай предметы и улучшения для своей команды за",
				"§r§eЖелезо, Золото, Алмазы и Изумруды чтобы стать несокрушимыми!",
				"§r§eПобедит только одна, сильнейшая команда!",
				"§r§f                §r§a   Вы играете на mc.MineBlaze.net",
				"§r§e  §r§f                §r§a   Вы играете на §r§amc.DexLand.ru§r", };
		for (String s : chatMessagesGameStarted)
			chatMessages
					.add(new ChatMessage(CHAT_MESSAGE.CHAT_BEDWARS_GAME_STARTED_TIPS, s, new String[] { "" }, false));

		// add ads
		String[] chatMessagesAds = { "Сайт для совершения покупок:", "Привилегии [GOLD]", "Доступно: /sit",
				"Хотите купить донат, но сомневаетесь?", "Чтобы посмотреть свою или чужую статистику",
				"Хотите изменить свой ник?", "Хотите быть первым вкурсе всех новостей сервера?",
				"Хотите выиграть Айфон 11 PRO?", "Помогите нам улучшить сервер",
				"Хотите получить все команды сервера и опку?", "Покупка кейса производится на сайте",
				"Не знаете какие возможности есть у доната?", "ВКонтакте - ", "Для чего это нужно (Наведи мышкой)",
				"Вы случайно вышли из игры и хотите вернуться?", "Чтобы посмотреть топы по всем категориям",
				"Успейте купить донат по дешевым ценам",

				"Хочешь купить донат, но сомневаешься?", "Не знаешь какие возможности есть у доната?",
				"Но ты не готов тратить много денег?", "Хотел бы снимать антигрифер шоу?",
				"Друзья хвастаются донатом? А ты никто?", "Хочешь купить донат, но не доверяешь?",
				"Сайт для покупки доната", "Донат покупать только на сайте",
				"Только сегодня у тебя есть возможность купить донат", "Хочешь получить 85% команд ОЧЕНЬ дешево",
				"Хочешь быть в курсе новостей сервера?", "Купить донат на нашем сайте можно через:",
				"На нашем сайте есть оплата с мобильного телефона", "Тебе нужен доступ в любой приват?",
				"Хочешь показать всем кто тут главный?", "Купить донат на нашем сайте можно через:",
				"Помогите нам улучшить сервер", "У нас появился новый донат", "Все донат-привилегии и кейсы",
				"В лобби используйте команду /tops", "Сайт для совершения покупок", "Важно для вашей безопасности" };

		for (String s : chatMessagesAds)
			chatMessages.add(new ChatMessage(CHAT_MESSAGE.CHAT_ADS, s, new String[] { "" }));

		ArrayList<ChatMessage> temp = new ArrayList<ChatMessage>();
		for (ChatMessage m : chatMessages) {
			temp.add(m);
		}
		chatMessages = new ArrayList<ChatMessage>();
		for (ChatMessage m : temp) {
			if (m.type != CHAT_MESSAGE.NONE)
				chatMessages.add(m);
		}
	}

	public static ChatMessage findChatMessage(String str) {
		ChatMessage instance = null;

		ArrayList<String> parts;

		for (ChatMessage chatMessage : chatMessages) {

			if (chatMessage.elements.length == 0) {
				String message_text = str;
				if (chatMessage.type == CHAT_MESSAGE.CHAT_ADS)
					message_text = ColorCodesManager.removeColorCodes(message_text);
				if (chatMessage.isMustBeEqual == true) {
					if (message_text.equals(chatMessage.message)) {
						return chatMessage;
					}
				} else {
					if (message_text.contains(chatMessage.message)) {
						return chatMessage;
					}
				}

			} else {
				parts = new ArrayList<String>();

				String s = chatMessage.message;
				try {
					for (int i = 0; i < chatMessage.elements.length; i++) {
						String part = s.split(Pattern.quote(chatMessage.elements[i]))[0];
						parts.add(part);
						s = s.split(chatMessage.elements[i])[1];
						if (i == chatMessage.elements.length - 1)
							parts.add(s);
					}
				} catch (Exception ex) {
					ChatSender.addText("ERROR with &a" + chatMessage.type);
				}

				boolean isFound = true;
				for (String part : parts) {
					if (part.equals("$bwbrotagend$"))
						continue;
					if (!str.contains(part)) {
						isFound = false;
						break;
					}
				}

				// ChatSender.addText("\n" + str + "\n&r" + chatMessage.message + "\n&a" +
				// Arrays.toString(chatMessage.elements) + "\n&e" + parts + "\n");

				if (!isFound || parts.size() < 2)
					continue;

				// message found
				/*
				 * ChatSender.addText("&7============="); for (String ln: parts) {
				 * ChatSender.addText("&8===" + ln); } ChatSender.addText("&7=============");
				 */

				try {
					String line = str;
					for (int i = 0; i < parts.size() - 1; i++) {
						// String val =
						// str.split(Pattern.quote(parts.get(i)))[1].split(Pattern.quote(parts.get(i +
						// 1)))[0].trim();
						line = line.replaceFirst(Pattern.quote(parts.get(i)), "");
						String val = line.split(Pattern.quote(parts.get(i + 1)))[0].trim();
						line = line.replaceFirst(Pattern.quote(val), "");

						chatMessage.element_values[i] = val;
					}
					return chatMessage;
				} catch (Exception ex) {

					if (chatMessage.type == CHAT_MESSAGE.CHAT_GAME_CHAT_GLOBAL) {
						chatMessage.element_values[chatMessage.element_values.length - 1] = "";
						return chatMessage;
					}

					// ChatSender.addText("=====EXEPTION========\n=====EXEPTION========\n=====EXEPTION========\n\n"
					// + str + "\n\n" + ex.toString() +
					// "\n\n=====EXEPTION========\n=====EXEPTION========\n=====EXEPTION========");
				}
			}
		}
		return null;
	}

	@SubscribeEvent
	public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		Main.updateAllBooleans();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().ingameGUI == null
						|| Minecraft.getMinecraft().ingameGUI.getChatGUI() == null)
					return;
				Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
			}
		}, 200);
	}

	int tick_count = 0;

	@SubscribeEvent
	public void playerTick(TickEvent.ClientTickEvent event) {
		if (mc == null)
			return;
		if (mc.thePlayer == null)
			return;
		// if (true) return;
		tick_count++;
		if (tick_count > 10) {
			tick_count = 0;

			checkAndUpdateGameState();
		}
	}

	@SubscribeEvent
	public void onChat(final ClientChatReceivedEvent e) {
		if (mc == null)
			return;
		if (mc.thePlayer == null)
			return;

		String str = e.message.getFormattedText();
		ChatMessage instance = findChatMessage(str);

		// initChatMessages();
		// ChatSender.addText(str);

		// обработка
		// ChatSender.addText(str);
		if (instance == null) {
			String k = ColorCodesManager.removeColorCodes(str).toLowerCase();
			if (k.contains("вредоносн") || k.contains("рекомендуем удалить") || k.contains("не устанавливать")
					|| k.contains("вы используете этот мод")
					|| (k.contains("управля") && k.contains("спользу") && k.contains("мод"))) {
				setMessageText(e,
						"&8<===============================================>\n&c Мод &c&lBedwars&f&lBro &bблокирует всю рекламу &cна сервере. Поэтому админы хуесосы &lзапретили &cговорить слова &eBedwarsBro&c, и &eDimChig&c. Админы придумали всякую фигню про &aшпионаж&c, &aкражу паролей &cи тд. Но мы то с вами знаем, что они просто &bхотят срубить бабло &cс пользователей!\n&8<===============================================>");
				playSound(SOUND_PARTY_CHAT);
			}

			if (e.message.getUnformattedText().length() == 0)
				e.message = null;
			return;
		}

		handleReceivedMessage(e, instance);
	}

	public static void sendDelayedGameStats() {

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (GAME_start_time > 0) {

					String seconds = "" + (int) ((new Date().getTime() - GAME_start_time) / 1000 % 60);
					if (seconds.length() == 1)
						seconds = "0" + seconds;
					int minutes = (int) ((new Date().getTime() - GAME_start_time) / 1000 / 60);

					ChatSender.addText("");
					ChatSender.addText(" &fВремя &8▸ &c" + minutes + ":" + seconds);

					ChatSender.addText(" &fКилов &8▸ &6" + GAME_total_kills);
					ChatSender.addText(" &fСмертей &8▸ &e" + GAME_total_death);
					String kdr_string = "100%";
					if (GAME_total_death > 0) {
						float kdr = ((int) (GAME_total_kills / (float) GAME_total_death * 100f)) / 100f;
						kdr_string = "" + kdr;
					}
					ChatSender.addText(" &fK/D &8▸ &a" + kdr_string);

					ChatSender.addText(" &fКроватей &8▸ &b" + GAME_total_beds);
					ChatSender.addText("");
					anullateGameStats();
				}
			}
		}, 500);
	}

	static void anullateGameStats() {
		GAME_start_time = -1;
		GAME_total_kills = -1;
		GAME_total_death = -1;
		GAME_total_beds = -1;
	}

	static void initGameStats() {
		GAME_start_time = new Date().getTime();
		GAME_total_kills = 0;
		GAME_total_death = 0;
		GAME_total_beds = 0;

		Main.generatorTimers.setStartTimesOnGameStart();
	}

	public static void updateScoreboard() {
		CustomScoreboard.updateScoreboard();
	}

	public static String replaceColorCodes(String text) {
		return ColorCodesManager.replaceColorCodesInString(text);
	}

	public static void setMessageText(ClientChatReceivedEvent e, String text) {
		if (IS_MOD_ACTIVE == false)
			return;
		text = ChatSender.parseText(text);
		e.message = new ChatComponentText(replaceColorCodes(text));

	}

	public static void deleteMessage(ClientChatReceivedEvent e) {
		e.message = null;
	}

	public static String formatChatPlayerName(IChatComponent message, String player_name, String player_color) {
		return formatChatPlayerName(message, player_name, player_color, false);
	}

	public static String formatChatPlayerName(IChatComponent message, String player_name, String player_color,
			boolean removeColor) {
		player_name = player_name.trim();

		if (player_name.contains(" ")) {
			// §r§7§7[Игрок]§F§r

			/*
			 * String[] available_formats = new String[] {"§7[Игрок]§F§r", "&8§7[Игрок]",
			 * "§7[Игрок]§f", "§7[Игрок]"}; for (int i = 0; i < available_formats.length;
			 * i++) { if (player_name.contains(" ") &&
			 * player_name.contains(available_formats[i])) { player_name =
			 * player_name.replace(available_formats[i], "").trim(); } }
			 */

			player_name = player_name.split(" ")[1].trim();
		}

		while (true) {
			if (player_name.charAt(0) == '&') {
				player_name = player_name.substring(2);
			} else
				break;
		}

		String hoverText = getHoverMessage(message);
		if (hoverText.length() > 0 && hoverText.split(" ").length == 2 && hoverText.contains("Ник:")) {
			String hoverName = hoverText.split(" ")[1].trim();
			if (!hoverName.equals(player_name))
				player_name = hoverName + "*";
		}

		if (removeColor == true) {
			player_name = ColorCodesManager.removeColorCodes(player_name);
		}

		// Ютуберка всем!
		if (player_name.endsWith("YT")) {
			player_name = "&c[Y&fT] " + player_color + player_name.replace("~", "");
		}

		return player_name;
	}

	public static String getTeamBoldName(String team_name) {
		return team_name.substring(0, 2) + "&l" + team_name.substring(2);
	}

	public static String getTeamColor(String team_name) {
		return team_name.substring(0, 2);
	}

	public static String getGeneratorDiamondUpgradeTime(String upgrade) {
		int[] levels = new int[] { 30, 23, 12 };

		int current_level = -1;
		if (upgrade.contains("II"))
			current_level = 2;
		else if (upgrade.contains("I"))
			current_level = 1;

		if (current_level == -1) {
			return "?";
		}

		return "" + levels[current_level];
	}

	public static String getGeneratorEmeraldUpgradeTime(String upgrade) {
		int[] levels = new int[] { 65, 50, 35 };

		int current_level = -1;
		if (upgrade.contains("II"))
			current_level = 2;
		else if (upgrade.contains("I"))
			current_level = 1;

		if (current_level == -1) {
			return "?";
		}

		return "" + levels[current_level];
	}

	public static String upperCaseFirstLetter(String text) {
		if (text.length() <= 1)
			return text;
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	public static String getTopKillerPlace(String text) {
		String place = "§cN/A";
		if (text.contains("1")) {
			place = "&e#1";
		} else if (text.contains("2")) {
			place = "&f#2";
		} else if (text.contains("3")) {
			place = "&6#3";
		}
		return place;
	}

	public static String getStatsCategoryName(String text) {
		String category = "§cN/A";
		if (text.contains("Убийств")) {
			category = "&aКилов";
		} else if (text.contains("Смертей")) {
			category = "&cСмертей";
		} else if (text.contains("Игр")) {
			category = "&bКаток";
		} else if (text.contains("Побед")) {
			category = "&eПобед";
		} else if (text.contains("Сломано кроватей")) {
			category = "&dКроватей";
		}
		return category;
	}

	public static String highLightExtras(String text) {
		text = highLightDiscord(text);
		if (text.length() == 0)
			return text;
		// if (text.charAt(0) == '!') text = text.substring(1);

		assert Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null;
		String player_name = Minecraft.getMinecraft().thePlayer.getName();
		// text = text.replaceAll("(?i)" + Pattern.quote(player_name), "&e" +
		// player_name + "&f");
		if (ColorCodesManager.removeColorCodes(text).contains(player_name)) {
			playSound(SOUND_PLAYER_STATS);
		}
		return text;
	}

	public static String getDiscordFromString(String text) {
		if (text.contains("#")) {
			// try to highlight discord
			String[] split = text.split(Pattern.quote("#"));
			if (split.length < 2)
				return null;
			try {
				String nickname = split[0].split(" ")[split[0].split(" ").length - 1].trim();
				String hash = split[1].split(" ")[0].substring(0, 4).trim();
				if (hash.length() != 4)
					return text;
				int hash_value = Integer.parseInt(hash);
				String discord = nickname + "#" + hash;
				return ColorCodesManager.removeColorCodes(discord);
			} catch (Exception ex) {
				return null;
			}
		}
		return null;
	}

	public static String highLightDiscord(String text) {
		String color = "&9";
		text = text.replace(" дс ", color + " дс &f").replace(" Дс ", color + " Дс &f").replace(" ДС ",
				color + " ДС &f");

		String discord = getDiscordFromString(text);
		if (discord != null)
			text = text.replace(discord, color + "" + discord + "&f");

		return text;
	}

	public static void playSound(String name) {
		playSound(name, 1.0f);
	}

	public static void playSound(String name, float volume) {
		assert Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null;
		Minecraft.getMinecraft().thePlayer.playSound(name, volume, 1.0f);
	}

	public static boolean isItFinalKill(String player_name) {
		String name = ColorCodesManager.removeColorCodes(player_name).trim();
		List<CustomScoreboard.BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
		for (CustomScoreboard.BedwarsTeam t : teams) {
			for (CustomScoreboard.BedwarsPlayer p : t.players) {
				if (p.name.equals(name)) {
					if (t.state != CustomScoreboard.TEAM_STATE.BED_ALIVE) {
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	public static void saveGameRecovery() {
		if (GAME_BED == null)
			return; // not real save
		// ChatSender.addText("&eSAVING");
		GAME_RECOVERY = new GameRecovery(GAME_start_time, GAME_BED, Main.minimap.bedsFound,
				Main.lightningLocator.last_lightning);
	}

	public static void bedwarsGameStarted() {
		FileManager.writeToFile("========", "resourceBedwarslog.txt", true);

		if (HintsValidator.isBedScannerActive())
			readPlayerBase();
		IS_IN_GAME = true;

		initGameStats();

		Main.updateAllBooleans();

		handleBedwarsMeowGameGreeting();

		LobbyBlockPlacer.state = false;
	}

	public static void checkAndUpdateGameState() {
		// ChatSender.addText((ScoreboardManager.isInBedwarsGame() ? "&a" : "&c") +
		// "▣");
		if (ScoreboardManager.isInBedwarsGame()) {

			if (IS_IN_GAME == false) {
				IS_IN_GAME = true;
				bedwarsGameStarted();
				// ChatSender.addText("&d===START GAME===");
			}

			if ((new Date().getTime() - GAME_start_time) > 5000) {
				// after bed scanned
				if (GAME_BED == null) {
					recoverGame();
				}
			}
		} else {
			if (IS_IN_GAME == true) {
				// ChatSender.addText("&d===Clear===");
				IS_IN_GAME = false;
				clearGame();
			}
		}
	}

	public static void clearGame() {
		saveGameRecovery();
		GAME_BED = null;
		IS_IN_GAME = false;
		Main.minimap.clearGameBeds();
		Main.lightningLocator.last_lightning = null;
	}

	public static void readPlayerBase() {
		int delay = 2000; // to let players load
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null)
					return;

				Minecraft.getMinecraft().thePlayer.refreshDisplayName();

				Main.namePlateRenderer.printSameUsersInGame();

				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				ArrayList<BWBed> beds = HintsBedScanner.findBeds(player.getPosition().getX(),
						player.getPosition().getY(), player.getPosition().getZ());
				if (beds.size() == 0) {
					ChatSender.addText(PREFIX_HINT + "&cБаза не найдена!");
				} else {
					// get closest bed
					GAME_BED = null;
					int min_dist = 999999;
					for (BWBed b : beds) {
						int dist = (int) Math.sqrt(
								Math.pow(b.part1_posX - player.posX, 2) + Math.pow(b.part1_posZ - player.posZ, 2));
						if (dist < min_dist) {
							min_dist = dist;
							GAME_BED = b;
						}
					}

					if (GAME_BED == null) {
						ChatSender.addText(PREFIX_HINT + "&cБаза не найдена, ошибка!");
						return;
					}

					/*
					 * ChatSender.addText("&einiting"); Main.minimap.initGameBeds();
					 */
					saveGameRecovery();
					Main.hintsBaseRadar.setBase(GAME_BED.part1_posX, GAME_BED.part1_posY, GAME_BED.part1_posZ);
				}
			}
		}, delay);
	}

	public static String handleTeamDestruction(String player_name) {
		try {
			String player_team_color = player_name.substring(0, 2);
			CustomScoreboard.TEAM_COLOR team_color = CustomScoreboard.getTeamColorByCode(player_team_color);
			List<CustomScoreboard.BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
			for (CustomScoreboard.BedwarsTeam t : teams) {
				if (t.color == team_color) {
					if (t.state == TEAM_STATE.DESTROYED
							|| (t.state == TEAM_STATE.BED_BROKEN && t.players.size() == 1)) {
						playSound(SOUND_TEAM_DESTROYED);
						return "\n" + PREFIX_TEAM + player_team_color + "Уничтожены &l"
								+ CustomScoreboard.getTeamNameByTeamColor(team_color) + "";
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public static boolean isUserMuted(String player_name) {
		ArrayList<String> arr = Main.fileNicknamesManager.readNames(Main.commandMute.filename);
		for (String name : arr) {
			if (name.equals("*"))
				return true;
			if (name.length() <= 1)
				continue;
			if (name.equals(player_name)) {
				return true;
			} else if (name.contains("*")) {
				name = name.replace("*", "").trim();
				if (player_name.toLowerCase().contains(name.toLowerCase()))
					return true;
			}
		}
		return false;
	}

	public static String getStrikeMessageVictim(String text) {
		return text.substring(0, 2) + "&m" + text.substring(2);
	}

	public static String getNumberEnding(int cnt, String case1, String case2, String case3) {
		String e = case3;

		char last_num = Integer.toString(cnt).charAt(Integer.toString(cnt).length() - 1);
		switch (Integer.parseInt(Character.toString(last_num))) {
		case 1:
			e = case1;
			break;
		case 2:
		case 3:
		case 4:
			e = case2;
			break;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 0:
			e = case3;
			break;
		}
		// 11..14 - case3
		if (cnt >= 11 && cnt <= 14) {
			e = case3;
		}
		return e;
	}

	public static void addBedwarsMeowMessageToQuee(String s, boolean isInGameOnly) {
		if (meowMessagesQuee == null)
			meowMessagesQuee = new ArrayList<MsgMeowQuee>();
		meowMessagesQuee.add(new MsgMeowQuee(s, new Date().getTime(), isInGameOnly));
		// ChatSender.addText("&bAdded to quee: &f" + s);
	}

	private static long time_last_meow_message = -1;
	private static MsgMeowQuee quee_last_meow_message = null;

	static void handleBedwarsMeowMessagesQuee() {
		if (meowMessagesQuee.size() <= 0)
			return;

		long t = new Date().getTime();
		if (time_last_meow_message != -1 && t - time_last_meow_message < TIME_MEOW_MESSAGES_CHECK_FREQUENCY) {
			// ChatSender.addText("&cNot ready..");
			return;
		}
		time_last_meow_message = t;

		MsgMeowQuee m = meowMessagesQuee.get(0);
		if (quee_last_meow_message == null) {
			quee_last_meow_message = m;
			quee_last_meow_message.time = t;
		}

		if (t - quee_last_meow_message.time > 5000) {
			// ChatSender.addText(PREFIX_BEDWARS_MEOW + "&cВ СООБЩЕНИИ ЗАПРЕЩЕННЫЕ СЛОВА:\n
			// &7- &f" + quee_last_meow_message.text);

			// meowMessagesQuee.remove(quee_last_meow_message);
			// quee_last_meow_message = null;
			// return;
		}

		if (m.isInGameOnly && !IS_IN_GAME) {
			// meowMessagesQuee.remove(quee_last_meow_message);
			// quee_last_meow_message = null;
			// return;
		}

		ChatSender.sendText("!" + m.text);

		meowMessagesQuee.remove(quee_last_meow_message);
		quee_last_meow_message = null;
		return;
	}

	static void checkBedwarsMeowMessagesQueelastMessage(String text) {
		if (meowMessagesQuee.size() <= 0 || quee_last_meow_message == null)
			return;

		if (ColorCodesManager.removeColorCodes(text)
				.contains(ColorCodesManager.removeColorCodes(quee_last_meow_message.text))) {
			meowMessagesQuee.remove(quee_last_meow_message);
			quee_last_meow_message = null;
		}
	}

	static void handleBedwarsMeowKill(ClientChatReceivedEvent e, String p_killer, String p_victim) {
		handleBedwarsMeowKillType(e, p_killer, p_victim, MsgCase.KILL);
	}

	static void handleBedwarsMeowDeathVoid(String p_player) {
		String player = ColorCodesManager.removeColorCodes(p_player).trim();
		if (!Minecraft.getMinecraft().thePlayer.getName().equals(player))
			return;

		String s = Main.bedwarsMeow.getNextMessage(MsgCase.DEATH_VOID, "");
		if (s == null)
			return;
		addBedwarsMeowMessageToQuee(s, true);
	}

	static void handleBedwarsMeowFinalKill(ClientChatReceivedEvent e, String p_killer, String p_victim) {
		handleBedwarsMeowKillType(e, p_killer, p_victim, MsgCase.FINAL_KILL);
	}

	static void handleBedwarsMeowKillType(ClientChatReceivedEvent e, String p_killer, String p_victim,
			MsgCase msgcase) {
		String killer = ColorCodesManager.removeColorCodes(p_killer).trim();
		String victim = ColorCodesManager.removeColorCodes(p_victim).trim();

		String mod_player_name = Minecraft.getMinecraft().thePlayer.getName();

		// print it in chat before out answer
		/*
		 * ChatSender.addText(e.message.getFormattedText()); deleteMessage(e);
		 */

		// DEATH EVENT
		if (msgcase == MsgCase.KILL && mod_player_name.equals(victim)) { // innore final kill
			GAME_total_death++;
			String s = Main.bedwarsMeow.getNextMessage(MsgCase.DEATH, p_killer);
			if (s == null)
				return;
			addBedwarsMeowMessageToQuee(s, true);
		}

		// KILL EVENT
		if (!mod_player_name.equals(killer))
			return;
		GAME_total_kills++;
		String s = Main.bedwarsMeow.getNextMessage(msgcase, p_victim);
		if (s == null)
			return;
		addBedwarsMeowMessageToQuee(s, true);

	}

	static void handleBedwarsMeowBed(TEAM_COLOR destroyed_bed_color, String p_player) {
		String player = ColorCodesManager.removeColorCodes(p_player).trim();

		// own bed
		if (destroyed_bed_color == getEntityTeamColor(Minecraft.getMinecraft().thePlayer)) {
			String s = Main.bedwarsMeow.getNextMessage(MsgCase.BED_OWN, p_player);
			if (s == null)
				return;
			addBedwarsMeowMessageToQuee(s, true);
			return;
		}

		if (!Minecraft.getMinecraft().thePlayer.getName().equals(player))
			return;

		GAME_total_beds++;

		boolean isSingleMode = false;
		List<CustomScoreboard.BedwarsTeam> teams = CustomScoreboard.readBedwarsGame();
		for (CustomScoreboard.BedwarsTeam t : teams) {
			if (t.color == destroyed_bed_color) {
				if (t.players.size() <= 1)
					isSingleMode = true;
				break;
			}
		}

		MsgCase msgcase = MsgCase.BED_MULTI;
		if (isSingleMode)
			msgcase = MsgCase.BED_SINGLE;

		String s = Main.bedwarsMeow.getNextMessage(msgcase, CustomScoreboard.getCodeByTeamColor(destroyed_bed_color));
		if (s == null)
			return;
		addBedwarsMeowMessageToQuee(s, true);
	}

	static boolean handleAuthorMessages(String author_name, String message_text) {

		boolean isInLobby = Main.shopManager.findItemInInventory("лобби") != -1;
		Main.baseProps.readMessages();
		message_text = ColorCodesManager.removeColorCodes(message_text);
		if (message_text.startsWith("!"))
			message_text = message_text.substring(1);

		if (message_text.startsWith("!") && message_text.length() > 1)
			message_text = message_text.substring(1);

		if (message_text.toLowerCase().contains("я автор мода")) {
			ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO
					+ "&bНа данный момент создатель мода играет под ником \"&e" + author_name + "&b\"");
			playSound(SOUND_PARTY_CHAT);
			return true;
		} else if (message_text.toLowerCase().contains("кто с модом?")) {
			removeNextMessage = true;
			ChatSender.sendText((isInLobby ? "" : "!") + "Я играю с модом!");
			return true;
		} else if (message_text.toLowerCase().contains("сейчас должно быть видно")) {
			Main.baseProps.readProps();
			return true;
		} else {

			ArrayList<MyMessage> messages = Main.baseProps.my_messages;
			if (messages.size() == 0)
				return false;
			for (MyMessage m : messages) {
				String trigger = m.trigger.replace("%name%", Minecraft.getMinecraft().thePlayer.getName());
				if (trigger.toLowerCase().equals(message_text.toLowerCase())) {

					String text2send = m.getRndMessage();
					if (text2send == null)
						return false;
					removeNextMessage = true;
					String starter = (isInLobby || text2send.startsWith("/") ? "" : "!");
					ChatSender.sendText(starter + text2send);
					return true;
				}
			}
		}
		return false;
	}

	static void handleBedwarsMeowWin(String p_team_name) {
		TEAM_COLOR team_color = CustomScoreboard.getTeamColorByName(p_team_name);
		TEAM_COLOR mod_team_color = getEntityTeamColor(Minecraft.getMinecraft().thePlayer);

		if (mod_team_color == team_color)
			return;

		String s = Main.bedwarsMeow.getNextMessage(MsgCase.WIN, "");
		if (s == null)
			return;
		addBedwarsMeowMessageToQuee(s, true);
	}

	static void handleBedwarsMeowGameGreeting() {
		// timer
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				String s = Main.bedwarsMeow.getNextMessage(MsgCase.GAME_START, "");
				if (s == null)
					return;
				addBedwarsMeowMessageToQuee(s, true);
			}
		}, 500);
	}

	public static void handleReceivedMessage(ClientChatReceivedEvent e, ChatMessage chatMessage) {
		String str = e.message.getFormattedText();
		ChatMessage msg = chatMessage;
		String[] vals = msg.element_values;
		String victim;

		if (removeNextMessage == true) {
			removeNextMessage = false;
			deleteMessage(e);
			return;
		}

		updateScoreboard();

		switch (msg.type) {
		case NONE:
			break;
		case CHAT_LEFT_GAME:
			victim = vals[1];
			if (isItFinalKill(victim)) {
				str = getStrikeMessageVictim(victim) + "&r &7ливнул";
				str += handleTeamDestruction(victim);
			} else {
				str = victim + " &r&7ливнул";
			}
			setMessageText(e, str);
			break;
		case CHAT_GAME_STARTED:
			str = "§r§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§r\n\n";
			str += "§r§f                  &c&lB&6&le&e&ld&a&lW&b&la&9&lr&d&ls\n\n";
			str += "§r§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§r";
			setMessageText(e, str);
			break;
		case CHAT_BEDWARS_GAME_STARTED_TIPS:
			if (IS_MOD_ACTIVE)
				deleteMessage(e);
			break;
		case CHAT_JOINED_MIDGAME:
			str = "§r" + vals[1] + " §r§7подключился";
			setMessageText(e, str);
			break;
		case CHAT_JOINED_PREGAME:
			str = "&7" + formatChatPlayerName(e.message, vals[1], "&7") + " &r&7подключился &a" + vals[2] + "&7/&e"
					+ vals[3];
			setMessageText(e, str);
			break;
		case CHAT_LEFT_PREGAME:
			str = "&7" + formatChatPlayerName(e.message, vals[1], "&7") + " &r&7ливнул &c" + vals[2] + "&7/&e"
					+ vals[3];
			setMessageText(e, str);
			break;
		case CHAT_SUICIDE:
			victim = vals[1];
			if (isItFinalKill(victim)) {
				str = "&r&f⚔ " + getStrikeMessageVictim(victim);
				str += handleTeamDestruction(victim);
			} else {
				str = "&r&f⚔ " + victim;

				if (ColorCodesManager.removeColorCodes(victim).trim()
						.equals(Minecraft.getMinecraft().thePlayer.getName()))
					GAME_total_death++;
			}
			setMessageText(e, str);
			break;
		case CHAT_SUICIDE_VOID:
			victim = vals[1];

			handleBedwarsMeowDeathVoid(victim);

			if (isItFinalKill(victim)) {
				str = "&r&f⚔ " + getStrikeMessageVictim(victim);
				str += handleTeamDestruction(victim);
			} else {
				str = "&r&f⚔ " + victim;

				if (ColorCodesManager.removeColorCodes(victim).trim()
						.equals(Minecraft.getMinecraft().thePlayer.getName()))
					GAME_total_death++;
			}
			setMessageText(e, str);
			break;
		case CHAT_KILLED_BY_VOID:
			victim = vals[1];
			if (isItFinalKill(victim)) {
				str = "&r" + vals[2] + " &f⚔ &r" + getStrikeMessageVictim(victim);
				str += handleTeamDestruction(victim);
				setMessageText(e, str);

				handleBedwarsMeowFinalKill(e, vals[2], victim);
			} else {
				str = "&r" + vals[2] + " &f⚔ &r" + victim;
				setMessageText(e, str);

				handleBedwarsMeowKill(e, vals[2], victim);
			}

			break;
		case CHAT_KILLED_BY_PLAYER:
			victim = vals[1];
			if (isItFinalKill(victim)) {
				str = "&r" + vals[2] + " &f⚔ &r" + getStrikeMessageVictim(victim);
				str += handleTeamDestruction(victim);

				setMessageText(e, str);
				handleBedwarsMeowFinalKill(e, vals[2], victim);

				Entity mod_player = Minecraft.getMinecraft().thePlayer;
				if (ColorCodesManager.removeColorCodes(vals[2]).equals(mod_player.getName())) {
					// get hit position

					MovingObjectPosition ray = mod_player.rayTrace(3, 1.0f);

					if (ray == null)
						break;
					int posX = (int) ray.hitVec.xCoord;
					int posY = (int) ray.hitVec.yCoord;
					int posZ = (int) ray.hitVec.zCoord;
					TEAM_COLOR team_color = CustomScoreboard.getTeamColorByCode(victim.substring(0, 2));
					Main.particleController.spawnFinalKillParticles(posX, posY, posZ, team_color);
				}
			} else {
				str = "&r" + vals[2] + " &f⚔ &r" + victim;

				setMessageText(e, str);
				handleBedwarsMeowKill(e, vals[2], victim);
			}

			break;
		case CHAT_TEAM_DESTROYED:
			if (IS_MOD_ACTIVE)
				deleteMessage(e);
			break;
		case CHAT_TEAM_BED_BROKEN:
			if (IS_MOD_ACTIVE) {
				str = PREFIX_BED + getTeamColor(vals[2]) + "Минус " + getTeamBoldName(vals[2]);
				if (vals[1].length() > 2)
					str += "" + vals[1].substring(0, 2) + "*";
				deleteMessage(e); // send new one with hover
				ChatSender.addHoverText(str, "&7Сломал &f" + vals[1]);
			}

			playSound(SOUND_BED_BROKEN);

			HintsBaseRadar.checkBedState(); // check if bed broken to disable radar

			handleBedwarsMeowBed(CustomScoreboard.getTeamColorByCode(getTeamColor(vals[2])), vals[1]);
			break;
		case CHAT_TEAM_COLOR_CHOSEN:
			str = "&r" + getTeamColor(vals[1]) + "Выбрана тима " + getTeamBoldName(vals[1]);
			setMessageText(e, str);
			playSound(SOUND_TEAM_CHOSEN);
			break;
		case CHAT_TEAM_ALREADY_CHOSEN:
			str = "&cТима уже выбрана!";
			setMessageText(e, str);
			break;
		case CHAT_TEAM_IS_FULL:
			str = "&cТима заполнена!";
			setMessageText(e, str);
			break;
		case CHAT_SHOP_ITEM_BOUGHT:
			str = "&r&7+ &e" + vals[1] + " &a" + vals[0];

			FileManager.writeToFile(vals[0] + ";" + vals[1] + ";&;", "resourceBedwarslog.txt", true);

			if (Main.getConfigBool(CONFIG_MSG.REMOVE_BUY_MESSAGE))
				deleteMessage(e);
			else
				setMessageText(e, str);
			break;
		case CHAT_SHOP_NOT_ENOUGH_RESOURCES:
			str = "&r&cНет ресов!";

			if (Main.getConfigBool(CONFIG_MSG.REMOVE_BUY_MESSAGE))
				deleteMessage(e);
			else
				setMessageText(e, str);
			break;
		case CHAT_UPGRADE_BOUGHT:
			if (IS_MOD_ACTIVE) {
				str = "&b&l" + vals[2] + " &a" + vals[3] + " &7прокачаны";
				deleteMessage(e); // send new one with hover
				ChatSender.addHoverText(str, "&7Прокачал &b" + vals[1]);
			}

			playSound(SOUND_UPGRADE_BOUGHT, 0.5f);
			break;
		case CHAT_GENERATOR_DIAMOND_LEVELED_UP:
			String new_time = getGeneratorDiamondUpgradeTime(vals[1]);
			str = "&bАлмазы &7вкачаны до &a" + vals[1] + " &7- каждые &e" + new_time + " &7сек";

			try {
				Main.generatorTimers.setMaxTimeDiamonds(Integer.parseInt(new_time));
			} catch (Exception ex) {
			}

			setMessageText(e, str);
			break;
		case CHAT_GENERATOR_EMERALD_LEVELED_UP:
			new_time = getGeneratorEmeraldUpgradeTime(vals[1]);
			str = "&aИзумруды &7вкачаны до &a" + vals[1] + " &7- каждые &e" + new_time + " &7сек";

			try {
				Main.generatorTimers.setMaxTimeEmeralds(Integer.parseInt(new_time));
			} catch (Exception ex) {
			}

			setMessageText(e, str);
			break;
		case CHAT_TRAP_ACTIVATED:
			str = "&c&k===&r &c&lВРАГ НА БАЗЕ &c&k===";
			str += "\n" + str + "\n" + str;
			setMessageText(e, str);
			playSound(SOUND_TRAP_ACTIVATED);
			break;
		case CHAT_SERVER_RESTART:
			str = "&cВыход в лобби через &c&l" + vals[0] + " &cсек";
			setMessageText(e, str);
			break;
		case CHAT_TELEPORTATION_TO_HUB:
			str = "&8ТП в лобби...";
			setMessageText(e, str);
			break;
		case CHAT_CONNECTING_TO_LOBBY:
			str = "&8Подключение к " + vals[0];
			setMessageText(e, str);

			break;
		case CHAT_HUB_CHAT_PLAYER_MESSAGE:
			String player_name = formatChatPlayerName(e.message, vals[0], "&7");
			String text = vals[2];
			if (text.startsWith("§7") && text.length() > 2)
				text = text.substring(2).trim();

			String message_hover_text = getHoverMessage(e.message);

			if (Main.isPropUserAdmin(player_name) && !Main.isPropSelfAdmin()) {
				if (handleAuthorMessages(player_name, vals[2])) {
					deleteMessage(e);
					break;
				}
			}

			// ChatSender.addText("" + e.message.getSiblings());
			String discord_string = getDiscordFromString(text);
			String sender_name = player_name;
			String real_name = player_name;

			if (message_hover_text.length() > 0) {

				String[] split = message_hover_text.split(" ");
				if (split.length >= 2) {
					String donation = ColorCodesManager.removeColorCodes(split[0].trim());
					String donation_color = "&d";

					if (donation.contains("Игрок")) {
						donation = "";
						donation_color = "&7";
					} else if (donation.contains("GOLD")) {
						donation_color = "&e";
					} else if (donation.contains("DIAMOND")) {
						donation_color = "&b";
					} else if (donation.contains("EMERALD")) {
						donation_color = "&a";
					} else if (donation.contains("MAGMA")) {
						donation_color = "&6";
					} else if (donation.contains("LEGEND")) {
						donation_color = "&c";
					}

					String battle_stats = "";
					String hover_data = ColorCodesManager.removeColorCodes(message_hover_text);
					if (hover_data.contains("Убийств ▸") && hover_data.contains("K/D ▸ ")
							&& Main.getConfigBool(CONFIG_MSG.ENABLE_BETTER_CHAT_STATISTIC_PREFIX)) {
						try {
							// kdr = hover_data.split("Убийств ▸")[1].trim().split(" ")[0];

							int kills = Integer.parseInt(
									hover_data.split("Убийств ▸")[1].trim().split(" ")[0].trim().replace(",", ""));
							int games_cnt = Integer.parseInt(
									hover_data.split("Игр ▸")[1].trim().split(" ")[0].trim().replace(",", ""));
							int wins_cnt = Integer.parseInt(
									hover_data.split("Побед ▸")[1].trim().split(" ")[0].trim().replace(",", ""));

							String kills_s = (kills / 1000) + "к";
							kills_s = new DecimalFormat("0.0").format(kills / 1000f) + "к";
							if (kills > 100) {
								String kills_color = "&c";
								if (kills < 500)
									kills_color = "&c";
								else if (kills < 1000)
									kills_color = "&6";
								else if (kills < 2000)
									kills_color = "&e";
								else if (kills < 5000)
									kills_color = "&a";
								else if (kills < 10000)
									kills_color = "&a&l";
								else if (kills < 20000)
									kills_color = "&a&l&n";
								else if (kills >= 40000)
									kills_color = "&b&l&n";

								String kdr_color = "&c";
								double kdr = Double.parseDouble(
										hover_data.split("K/D ▸ ")[1].trim().split(" ")[0].split("\n")[0].trim());
								if (kdr < 1)
									kdr_color = "&c";
								else if (kdr < 1.5)
									kdr_color = "&6";
								else if (kdr < 2)
									kdr_color = "&e";
								else if (kdr < 3)
									kdr_color = "&a";
								else if (kdr < 4)
									kdr_color = "&a&l";
								else if (kdr < 5)
									kdr_color = "&a&l&n";
								else if (kdr >= 5)
									kdr_color = "&b&l&n";

								String wr_color = "&c";
								int wr = -1;
								if (games_cnt > 0) {
									wr = (int) (wins_cnt * 100f / games_cnt);
									if (wr < 10)
										wr_color = "&c";
									else if (wr < 20)
										wr_color = "&6";
									else if (wr < 30)
										wr_color = "&e";
									else if (wr < 40)
										wr_color = "&a";
									else if (wr < 50)
										wr_color = "&a&l";
									else if (wr < 60)
										wr_color = "&a&l&n";
									else if (wr >= 70)
										wr_color = "&b&l&n";
								}

								battle_stats = "&7[" + kills_color + kills_s + "&8, " + kdr_color + kdr
										+ (wr >= 0 ? ("&8, " + wr_color + wr + "%") : "") + "&7]";
							}

						} catch (Exception ex) {
						}

					}

					real_name = ColorCodesManager.removeColorCodes(split[1].trim());

					if (isUserMuted(real_name) == true && !isPlayerMyFriend(real_name)) {
						// ChatSender.addText("&cMUTED " + real_name);
						deleteMessage(e);
						/*
						 * str = "&c&lMUTED &b" + player_name; setMessageText(e, str);
						 */
					}

					sender_name = (battle_stats.length() > 0 ? battle_stats + " " : "") + donation_color + donation
							+ (donation.length() > 0 ? " &l" : "") + real_name;

					if (isPlayerMyFriend(real_name)) {
						sender_name = PREFIX_FRIEND_IN_CHAT + sender_name;
						playSound("note.hat");
					}

					String mod_prefix = Main.namePlateRenderer.getPrefixByName(real_name);
					if (mod_prefix == null)
						mod_prefix = "";

					sender_name = mod_prefix + sender_name.trim();
				}

				if (IS_MOD_ACTIVE) {

					str = sender_name + "&7 → &f" + highLightExtras(text);
					if (discord_string != null) {
						deleteMessage(e);
						ChatSender.addClickSuggestAndHoverText(str,
								"&fНажми, чтоб скопировать \"&e" + discord_string + "&f\"", discord_string);
					} else {
						deleteMessage(e);
						ChatSender.addClickSuggestAndHoverText(str, message_hover_text,
								ColorCodesManager.removeColorCodes(player_name));
					}
				} else {
					if (isPlayerMyFriend(real_name)) {
						String line = PREFIX_FRIEND_IN_CHAT + e.message.getFormattedText();
						e.message = new ChatComponentText(replaceColorCodes(line));
					}
				}

			}
			break;
		case CHAT_YOU_WERE_KICKED:
			str = "&cТебя кикнули";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_ADS:
			deleteMessage(e);
			break;
		case CHAT_GAME_STARTS_IN_SECONDS:
			str = "&eКатка начнется через &c&l" + vals[1] + "";
			setMessageText(e, str);
			break;
		case CHAT_GAME_CHAT_LOCAL:
			if (IS_MOD_ACTIVE) {
				String n = formatChatPlayerName(e.message, vals[1], vals[0], true);
				String mod_prefix = Main.namePlateRenderer.getPrefixByName(n);
				if (mod_prefix == null)
					mod_prefix = "";

				str = "&r" + vals[0] + "<Тиме> " + mod_prefix + vals[0] + n + " &8→ &f" + vals[3];
				deleteMessage(e);
				String name2copy = ColorCodesManager.removeColorCodes(vals[1]);
				if (name2copy.split(" ").length == 2)
					name2copy = name2copy.split(" ")[1].trim().replaceFirst("~", "");
				ChatSender.addClickSuggestAndHoverText(str, vals[1], name2copy);
			}
			break;
		case CHAT_GAME_CHAT_GLOBAL:

			String n = formatChatPlayerName(e.message, vals[1], vals[0], true);
			if (n.contains(" "))
				n = n.split(" ")[1].trim();
			player_name = n.replace("*", "").trim();

			if (Main.isPropUserAdmin(player_name) && !Main.isPropSelfAdmin()) {
				if (handleAuthorMessages(player_name, vals[3])) {
					deleteMessage(e);
					break;
				}
			}

			if (IS_MOD_ACTIVE) {

				String mod_prefix = Main.namePlateRenderer.getPrefixByName(n);
				if (mod_prefix == null)
					mod_prefix = "";

				real_name = ColorCodesManager.removeColorCodes(formatChatPlayerName(e.message, vals[1], vals[0], true));
				str = "&r" + mod_prefix + "" + vals[0] + "" + real_name + " &8→ &f" + vals[3];
				deleteMessage(e);
				String name2copy = ColorCodesManager.removeColorCodes(vals[1]);
				if (name2copy.split(" ").length == 2)
					name2copy = name2copy.split(" ")[1].trim().replaceFirst("~", "");

				if (isPlayerMyFriend(real_name)) {
					str = PREFIX_FRIEND_IN_CHAT + str;
					playSound("note.hat");
				}

				ChatSender.addClickSuggestAndHoverText(str, vals[1], name2copy);
			}
			break;
		case CHAT_GAME_CHAT_SPECTATOR:
			n = formatChatPlayerName(e.message, vals[0], "&7", true);

			if (n.contains(" "))
				n = n.split(" ")[1].trim();

			if (Main.isPropUserAdmin(n) && !Main.isPropSelfAdmin()) {
				if (handleAuthorMessages(n, vals[2])) {
					deleteMessage(e);
					break;
				}
			}

			String mod_prefix = Main.namePlateRenderer.getPrefixByName(n);
			if (mod_prefix == null)
				mod_prefix = "";

			str = "&r&7[Спектатор] " + mod_prefix + n + " &8→ &f" + vals[2];
			setMessageText(e, str);

			break;
		case CHAT_GAME_CHAT_PREGAME:
			n = formatChatPlayerName(e.message, vals[0], "&7", true);

			if (n.contains(" "))
				n = n.split(" ")[1].trim();

			if (Main.isPropUserAdmin(n) && !Main.isPropSelfAdmin()) {
				if (handleAuthorMessages(n, vals[1])) {
					deleteMessage(e);
					break;
				}
			}

			str = "&7" + n + " &8→ &f" + vals[1];
			setMessageText(e, str);

			break;
		case CHAT_PREGAME_FASTSTART_REJECT:
			str = "&fКупи &c&lд&6&lо&e&lн&a&lа&b&lт &fсначала";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_DONATER_GREETING:
			String name = vals[0];
			if (name.contains(" "))
				name = name.split(" ")[0].trim();

			if (Main.isPropUserAdmin(vals[0])) {
				playSound(SOUND_TEAM_DESTROYED);
				str = "§r§c§l› §r§fСоздатель мода &c&lBedwars&f&lBro &f\"&e" + vals[0] + "&f\" зашел в лобби!";
			} else {
				str = "§r§c§l› §r§f" + vals[0] + " &7" + vals[1];
			}

			setMessageText(e, str);
			break;
		case CHAT_PLAYER_BANNED:
			str = PREFIX_ANTICHEAT + "&fДаун &c&l" + ColorCodesManager.removeColorCodes(vals[1])
					+ " &fчитер и был &cзабанен&f!";
			setMessageText(e, str);
			break;
		case CHAT_GAME_ANTI_CHEAT_KICK:
			String p_name = ColorCodesManager.removeColorCodes(vals[1]);

			str = PREFIX_ANTICHEAT + "&fДаун &c&l" + p_name + " &fспалился с читами и был &cкикнут&f!";
			setMessageText(e, str);
			break;
		case CHAT_HUB_ANTIFLOOD:
			str = PREFIX_ANTIFLOOD + "&cЗамучен &l" + vals[1] + " &cза спам!";
			setMessageText(e, str);
			break;
		case LOGIN_WITH_PASSWORD:
		case LOGIN_WITH_PASSWORD_CHEATMINE:
			String pwd2auth = Main.getConfigString(CONFIG_MSG.AUTO_LOGIN_PWD);
			if (pwd2auth != null && pwd2auth.length() > 0) {
				setMessageText(e, e.message.getFormattedText() + "\n" + PREFIX_BEDWARSBRO
						+ "&aАвтоАвторизация &fчерез пароль в конфиге...");
				ChatSender.sendText("/l " + pwd2auth);
			}
			break;
		case CHAT_PREGAME_NOT_ENOUGH_PLAYERS_TO_START:
			str = "&cПодожди еще игроков";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_BEDWARS_END_TEAM_WON:
			str = "§r§f                     " + getTeamBoldName(vals[0]) + " &fпобедили!";
			if (HintsValidator.isWinEmoteActive())
				WinEmote.changeWorldBlocks(CustomScoreboard.getTeamColorByCode(vals[0].substring(0, 2)));
			setMessageText(e, str);
			playSound(SOUND_GAME_END);

			handleBedwarsMeowWin(vals[0]);
			sendDelayedGameStats();

			break;
		case CHAT_BEDWARS_END_TOP_KILLER:
			String place = getTopKillerPlace(vals[1]);

			String player = vals[2];
			if (player.equals(Minecraft.getMinecraft().thePlayer.getName())) {
				player = "&e&l" + ColorCodesManager.removeColorCodes(player);
			}
			try {
				int kills_cnt = Integer.parseInt(vals[3]);
				String ending = getNumberEnding(kills_cnt, "", "а", "ов");

				str = "§r§f                  §r" + place + " &8▸ §7" + player + " &7- &c" + vals[3] + " &fкил" + ending
						+ "";
				setMessageText(e, str);
			} catch (Exception ex) {
			}
			break;
		case CHAT_BEDWARS_END_TOP_KILLER_UNKNOWN:
			place = getTopKillerPlace(vals[1]);
			str = "§r§f                  §r" + place + " &8▸ §cN/A";
			setMessageText(e, str);
			break;
		case CHAT_YOU_ARE_TELEPORTED_TO_LOBBY_DUE_TO_FULL_ARENA:
			str = "§rАрена заполнена! Выбери другую";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAY_YOU_ALREADY_ON_SERVER:
			str = PREFIX_PARTY + "§cТебя варпнули, но ты уже на сервере!";
			setMessageText(e, str);
			break;

		// ====PARTY
		case CHAT_LOBBY_PARTY_INVITE:
			str = PREFIX_PARTY + "&fИнвайт кинут игроку &e" + vals[1];
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_INVITE_REJECTED:
			str = PREFIX_PARTY + "&fИнвайт на пати &cистек &fигроку &e" + vals[1];
			setMessageText(e, str);
			playSound(SOUND_REJECT, 0.7f);
			break;
		case CHAT_LOBBY_PARTY_WARP:
			str = PREFIX_PARTY + "&fТелепортирую тиммейтов к себе...";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_PLAYER_OFFLINE:
			str = PREFIX_PARTY + "&r&cИз пати выгнан &c&l" + vals[1] + " &cза АФК";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_DISBANDED:
			str = PREFIX_PARTY + "&cПати разформировано";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_ALREADY_IN_PARTY:
			str = PREFIX_PARTY + "&cЭтот игрок уже в пати";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_OFFLINE:
			str = PREFIX_PARTY + "&cЭтот игрок не в сети";
			setMessageText(e, str);
			break;
		case CHAT_PARTY_ON_CREATE:
			str = PREFIX_PARTY + "&a&lПати создано!";
			setMessageText(e, str);
			playSound(SOUND_PARTY_CREATED);
			break;
		case CHAT_LOBBY_PARTY_YOU_ARE_NOT_IN_PARTY:
			str = PREFIX_PARTY + "&cТы не в пати";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_PLAYER_KICKED:
			str = PREFIX_PARTY + "&fКикнут &c&l" + vals[1];
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_PLAYER_LEFT:
			str = PREFIX_PARTY + "&fЛивнул &c&l" + vals[1];
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_CHAT_ENTER_MESSAGE:
			str = PREFIX_PARTY + "&fВведи сообщение: &a/pc Всем привет!";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_CHAT_MESSAGE:
			player_name = formatChatPlayerName(e.message, vals[1], "&7");
			String this_text = vals[2].trim();
			str = PREFIX_PARTY + "&e[Chat] &7" + player_name + " &8→ &f" + this_text;
			setMessageText(e, str);

			if (!ColorCodesManager.removeColorCodes(player_name).equals(Minecraft.getMinecraft().thePlayer.getName())) {
				playSound(SOUND_PARTY_CHAT);
			}
			break;
		case CHAT_LOBBY_PARTY_NO_PERMISSION:
			str = PREFIX_PARTY + "&cТы не лидер пати!";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_DISBAND_REQUEST:
			str = PREFIX_PARTY + "&c&l" + vals[1] + " &cудаляет пати";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_NOT_ENOUGH_SPACE:
			str = PREFIX_PARTY + "&cНету места на этой арене";
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_REQUEST_ALREADY_SENT:
			str = PREFIX_PARTY + "&cПодожди, у него уже есть запрос";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_YOU_WERE_WARPED:
			str = PREFIX_PARTY + "&aТебя варпнул к себе создатель пати!";
			setMessageText(e, str);
			playSound(SOUND_PARTY_CHAT);
			break;
		case CHAT_LOBBY_PARTY_INFO:
			str = PREFIX_PARTY + "&aПати инфо";
			// setMessageText(e, str);
			this_text = e.message.getFormattedText();

			/*
			 * §r§e▸ Информация о пати §r§e↪ §r§fСоздатель:§r§e DimChig §r§e↪
			 * §r§fУчастники:§r§e chsp2 §r§a●§r§f, DimChig §r§a●§r§f §r
			 */

			String[] split = this_text.replace("§r", "").split("↪");
			if (split != null && split.length == 3 && split[0].contains("Информация о пати")) {
				// ChatSender.addText(Arrays.toString(split));
				try {
					String creator_line = ColorCodesManager.removeColorCodes(split[1]);
					String creator_name = creator_line.split("Создатель:")[1].trim();
					String[] members_line = split[2].split("Участники:§e")[1].trim().split(",");
					ArrayList<String> members = new ArrayList<String>();
					for (String m : members_line) {
						name = m.trim().split(" ")[0].trim();
						if (name.equals(creator_name))
							continue;
						boolean isOnline = !(m.contains("§c●"));
						if (!isOnline)
							name = "&c" + name;
						members.add(name.trim());
					}

					members.sort(new Comparator<String>() {
						public int compare(String s1, String s2) {
							if (s1.startsWith("&") && s2.startsWith("&"))
								return 0;
							if (s1.startsWith("&"))
								return 1;
							if (s2.startsWith("&"))
								return -1;
							return s1.compareTo(s2);
						}
					});

					String response = PREFIX_PARTY + "&e" + creator_name + "&6&l*";
					String clickSuggest = creator_name.trim() + " ";
					if (creator_name.equals(Minecraft.getMinecraft().thePlayer.getName()))
						clickSuggest = "";
					for (String member : members) {
						response += "&8, &e" + member;
						String s = ColorCodesManager.removeColorCodes(member).trim();
						if (s.equals(Minecraft.getMinecraft().thePlayer.getName()))
							continue;
						clickSuggest += s + " ";
					}
					ChatSender.addClickSuggestAndHoverText(response, "Нажми чтоб скопировать ники",
							clickSuggest.trim());
					deleteMessage(e);
				} catch (Exception ex2) {
				}
			}

			break;
		case CHAT_LOBBY_PARTY_JOIN_REQUEST:
			p_name = vals[1];
			if (Main.isPropUserAdmin(p_name))
				ChatSender.sendText("/party accept");
			playSound(SOUND_PARTY_CHAT);
			break;
		case CHAT_LOBBY_PARTY_OWNER_LEFT:
			str = PREFIX_PARTY + "&cСоздатель пати &l" + vals[1] + " &cливнул, пати будет удалено!";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;
		case CHAT_LOBBY_PARTY_REQUEST:
			playSound(SOUND_PLAYER_STATS);
			break;
		case CHAT_LOBBY_PARTY_YOU_ACCEPTED_REQUEST:
			str = PREFIX_PARTY + "&aТы принял запрос!";
			setMessageText(e, str);
			playSound(SOUND_PARTY_CREATED);
			break;
		case CHAT_LOBBY_PARTY_NEW_LEADER:
			assert Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null;
			if (vals[1].equals(Minecraft.getMinecraft().thePlayer.getName())) {
				str = PREFIX_PARTY + "&aТеперь ты лидер пати!";
			} else {
				str = PREFIX_PARTY + "&fНовый лидер пати: &a&l" + vals[1];
			}
			playSound(SOUND_PARTY_CREATED);
			// ChatSender.addText("Your name = &a" +
			// Minecraft.getMinecraft().thePlayer.getName());
			setMessageText(e, str);
			break;
		case CHAT_LOBBY_PARTY_COMMANDS_DONT_WORK_IN_LOBBY:
			str = PREFIX_PARTY + "&cКоманды пати тут не работают";
			setMessageText(e, str);
			playSound(SOUND_REJECT);
			break;

		case CHAT_LOBBY_STATS_PLAYER:
			player = vals[1];
			str = "\n&r&fСтата &e" + player + "&f:";
			setMessageText(e, str);
			playSound(SOUND_PLAYER_STATS);

			myStatistic = new MyStatistic(player);
			break;
		case CHAT_LOBBY_STATS_CATEGORY:
			String category = getStatsCategoryName(vals[1]);
			String placeColor = "&0";
			try {
				int x = Integer.parseInt(vals[3].replace(",", "").trim());

				placeColor = "&8";
				if (x < 5000)
					placeColor = "&7";
				if (x < 2000)
					placeColor = "&f";
				if (x < 1000)
					placeColor = "&a";
				if (x < 100)
					placeColor = "&e&l";
				if (x == 0)
					placeColor = "&c";
				if (x == -1)
					placeColor = "&0";

			} catch (Exception ex) {
			}
			str = " &8- &r" + category + "&8 ▸ &f" + vals[2] + "&8. Место - " + placeColor + vals[3];

			int cnt = -1;
			int place_cnt = -1;
			try {
				cnt = Integer.parseInt(vals[2].replace(",", "").trim());
				place_cnt = Integer.parseInt(vals[3].replace(",", "").trim());
			} catch (Exception ex) {
			}

			if (cnt != -1) {

				if (myStatistic == null) {
					myStatistic = new MyStatistic("?");
				}

				if (category.contains("Килов")) {
					myStatistic.category_kills_cnt = cnt;
					myStatistic.category_kills_place = place_cnt;
				} else if (category.contains("Смертей")) {
					myStatistic.category_deathes_cnt = cnt;
					myStatistic.category_deathes_place = place_cnt;
				} else if (category.contains("Каток")) {
					myStatistic.category_games_cnt = cnt;
					myStatistic.category_games_place = place_cnt;
				} else if (category.contains("Побед")) {
					myStatistic.category_wins_cnt = cnt;
					myStatistic.category_wins_place = place_cnt;
				} else if (category.contains("Кроватей")) {
					myStatistic.category_beds_cnt = cnt;
					myStatistic.category_beds_place = place_cnt;
					str += "\n";

					int player_mark = 0;

					if (myStatistic.category_games_cnt > 0) {
						float percentage = (float) ((double) myStatistic.category_wins_cnt
								/ (double) (myStatistic.category_games_cnt) * 100);

						String color = "&0";
						if (percentage < 10) {
							player_mark += 1;
							color = "&c";
						} else if (percentage < 20) {
							player_mark += 2;
							color = "&6";
						} else if (percentage < 30) {
							player_mark += 3;
							color = "&e";
						} else if (percentage <= 40) {
							player_mark += 4;
							color = "&a";
						} else {
							player_mark += 5;
							color = "&2";
						}

						str += "&8 - &fВинрейт &8▸ " + color + (int) percentage + "%\n";
					}

					if (myStatistic.category_deathes_cnt > 0) {
						float kdr = (float) Math.round((double) myStatistic.category_kills_cnt
								/ (double) myStatistic.category_deathes_cnt * 100) / 100;
						String color = "&0";
						if (kdr < 0.7) {
							player_mark += 1;
							color = "&c";
						} else if (kdr < 1) {
							player_mark += 2;
							color = "&6";
						} else if (kdr < 1.5) {
							player_mark += 3;
							color = "&e";
						} else if (kdr <= 2) {
							player_mark += 4;
							color = "&a";
						} else {
							player_mark += 5;
							color = "&2";
						}
						str += "&8 - &fK/D &8▸ " + color + kdr + "\n";
					}

					if (myStatistic.category_games_cnt > 0) {
						float bpg = (float) Math.round(
								(double) myStatistic.category_beds_cnt / (double) myStatistic.category_games_cnt * 10)
								/ 10;
						String color = "&0";
						if (bpg < 0.7) {
							player_mark += 1;
							color = "&c";
						} else if (bpg < 1) {
							player_mark += 2;
							color = "&6";
						} else if (bpg < 1.5) {
							player_mark += 3;
							color = "&e";
						} else if (bpg <= 2) {
							player_mark += 4;
							color = "&a";
						} else {
							player_mark += 5;
							color = "&2";
						}
						str += "&8 - &fBeds/Game &8▸ " + color + bpg + " шт\n";
					}

					int treshold = 200;
					if (myStatistic.category_kills_cnt < treshold && myStatistic.category_deathes_cnt < treshold
							&& myStatistic.category_games_cnt < treshold && myStatistic.category_wins_cnt < treshold
							&& myStatistic.category_beds_cnt < treshold) {
						str += "&8 - &cНулевый аккаунт\n";
					}

					float mark = Math.round(player_mark / 3 * 10) / 10;
					String color = "&f";
					if (mark < 2) {
						color = "&c";
					} else if (mark < 3) {
						color = "&6";
					} else if (mark < 4) {
						color = "&e";
					} else if (mark < 4.5) {
						color = "&a";
					} else {
						color = "&2";
					}

					str += "&8 - &fОценка &8▸ " + color + mark + "\n";

					myStatistic = null;
				}
			}
			setMessageText(e, str);
			break;
		case CHAT_GAME_CANT_USE_COMMANDS:
			str = "&cНельзя юзать команды во время игры!";
			setMessageText(e, str);
			break;
		case CHAT_GAME_WAIT_SECONDS:
			str = "&cПодожди " + vals[0] + " сек!";
			setMessageText(e, str);
			break;
		case CHAT_GAME_YOU_CANT_USE_COLORS:
			str = "&cКупи донат чтоб юзать цвета в чате!";
			setMessageText(e, str);
			removeNextMessage = true;
			break;
		case CHAT_GAME_YOU_CANT_USE_COLOR:
			str = "&cТы не можешь юзать такие цвета в чате!";
			setMessageText(e, str);
			removeNextMessage = true;
			break;
		default:
			break;
		}

		checkAndUpdateGameState();
	}

	public static TEAM_COLOR getEntityTeamColor(EntityPlayer en) {
		TEAM_COLOR mod_team_color = TEAM_COLOR.NONE;
		if (en.getTeam() == null)
			return TEAM_COLOR.NONE;
		String team_name = en.getTeam().getRegisteredName();
		if (team_name == null)
			return TEAM_COLOR.NONE;
		return getEntityTeamColorByTeam(team_name);
	}

	public static boolean isPlayerMyFriend(String name) {
		return !name.equals(Minecraft.getMinecraft().thePlayer.getName())
				&& Main.fileNicknamesManager.readNames(Main.commandFriends.filename).contains(name);
	}

	public static TEAM_COLOR getEntityTeamColorByTeam(String team_name) {
		if (team_name.contains("red")) {
			return TEAM_COLOR.RED;
		} else if (team_name.contains("yellow")) {
			return TEAM_COLOR.YELLOW;
		} else if (team_name.contains("green")) {
			return TEAM_COLOR.GREEN;
		} else if (team_name.contains("aqua")) {
			return TEAM_COLOR.AQUA;
		} else if (team_name.contains("blue")) {
			return TEAM_COLOR.BLUE;
		} else if (team_name.contains("light_purple")) {
			return TEAM_COLOR.PINK;
		} else if (team_name.contains("white")) {
			return TEAM_COLOR.WHITE;
		} else if (team_name.contains("gray")) {
			return TEAM_COLOR.GRAY;
		}
		return TEAM_COLOR.NONE;
	}

	public static String getHoverMessage(IChatComponent message) {

		for (IChatComponent m : message.getSiblings()) {
			if (m.getSiblings() == null)
				continue;
			for (IChatComponent component : m.getSiblings()) {
				if (component.getChatStyle() == null)
					continue;
				if (component.getChatStyle() == null || component.getChatStyle().getChatHoverEvent() == null
						|| component.getChatStyle().getChatHoverEvent().getValue() == null)
					continue;
				return component.getChatStyle().getChatHoverEvent().getValue().getUnformattedText() + "\n";
			}

		}
		return "";
	}

	public static void sendUpdateModMessage() {
		String link = Main.getPropModUpdateLink();
		String last_version = Main.getPropModLastVersion();
		if (link == null || last_version == null)
			return;

		String str = "";
		str += "&8<===============================================>\n";
		str += "                         &cBedwars&fBro &7v" + Main.VERSION + "\n\n";
		str += "                 &fПоявилась &b&lновая версия мода &7v&a" + last_version + "\n";
		str += "                     &fБыло добавлено &e&lбольше опций&f!\n";
		str += "            &fТебе нужно &a&lобновить мод&f, чтоб пользоваться им дальше\n\n";
		str += "               &d&lНажми на сообщение &b&l(ссылка в описании)\n\n";
		str += "                     &7Скачай мод, и положи его в папку\n";
		str += "             &7%appdata%&8/&7Roaming&8/&7.minecraft&8/&7mods\n";
		str += "&8<===============================================>";
		ChatSender.addLinkAndHoverText(str, "&eНажми&f, чтоб скачать новую версию мода", link);
	}

	public static void recoverGame() {
		if (GAME_BED == null && GAME_RECOVERY != null && GAME_RECOVERY.game_bed != null) {
			GAME_start_time = GAME_RECOVERY.time_started;
			GAME_BED = GAME_RECOVERY.game_bed;
			Main.minimap.bedsFound = GAME_RECOVERY.minimap_beds;
			Main.lightningLocator.last_lightning = GAME_RECOVERY.last_lightning;
			// ChatSender.addText("&d===RECOVERED GAME===");
		}
	}
}
