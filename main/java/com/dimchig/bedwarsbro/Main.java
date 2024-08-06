package com.dimchig.bedwarsbro;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import com.dimchig.bedwarsbro.commands.CommandEnableESP;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.commands.CommandBWDiscord;
import com.dimchig.bedwarsbro.commands.CommandDexlandMeowSpoof;
import com.dimchig.bedwarsbro.commands.CommandFindPlayerByName;
import com.dimchig.bedwarsbro.commands.CommandFriends;
import com.dimchig.bedwarsbro.commands.CommandHintsFinderLookAt;
import com.dimchig.bedwarsbro.commands.CommandHintsFinderLookAtPlayer;
import com.dimchig.bedwarsbro.commands.CommandHistoryShop;
import com.dimchig.bedwarsbro.commands.CommandMeow;
import com.dimchig.bedwarsbro.commands.CommandModHelp;
import com.dimchig.bedwarsbro.commands.CommandMute;
import com.dimchig.bedwarsbro.commands.CommandNameChanger;
import com.dimchig.bedwarsbro.commands.CommandRainbowMessage;
import com.dimchig.bedwarsbro.commands.CommandRainbowMessageSetter;
import com.dimchig.bedwarsbro.gui.Draw3DText;
import com.dimchig.bedwarsbro.gui.GuiBedESP;
import com.dimchig.bedwarsbro.gui.GuiCrosshairBlocks;
import com.dimchig.bedwarsbro.gui.GuiMinimap;
import com.dimchig.bedwarsbro.gui.GuiMinimapOLD;
import com.dimchig.bedwarsbro.gui.GuiOnScreen;
import com.dimchig.bedwarsbro.gui.GuiPlayerFocus;
import com.dimchig.bedwarsbro.gui.GuiRadarIcon;
import com.dimchig.bedwarsbro.gui.GuiResourceHologram;
import com.dimchig.bedwarsbro.particles.ParticleController;
import com.dimchig.bedwarsbro.particles.ParticleTrail;
import com.dimchig.bedwarsbro.particles.ParticlesAlwaysSharpness;
import com.dimchig.bedwarsbro.serializer.MySerializer;
import com.dimchig.bedwarsbro.stuff.BedAutoTool;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow;
import com.dimchig.bedwarsbro.stuff.DangerAlert;
import com.dimchig.bedwarsbro.stuff.FireballSpread;
import com.dimchig.bedwarsbro.stuff.FreezeClutch;
import com.dimchig.bedwarsbro.stuff.GeneratorTimers;
import com.dimchig.bedwarsbro.stuff.HintsBaseRadar;
import com.dimchig.bedwarsbro.stuff.HintsFinder;
import com.dimchig.bedwarsbro.stuff.HintsItemTracker;
import com.dimchig.bedwarsbro.stuff.HintsPlayerScanner;
import com.dimchig.bedwarsbro.stuff.InvulnerableTime;
import com.dimchig.bedwarsbro.stuff.LightningLocator;
import com.dimchig.bedwarsbro.stuff.LobbyFly;
import com.dimchig.bedwarsbro.stuff.NamePlate;
import com.dimchig.bedwarsbro.stuff.NamePlateRenderer;
import com.dimchig.bedwarsbro.stuff.RainbowColorSynchronizer;
import com.dimchig.bedwarsbro.stuff.RotateBind;
import com.dimchig.bedwarsbro.stuff.ShopManager;
import com.dimchig.bedwarsbro.stuff.TNTJump;
import com.dimchig.bedwarsbro.stuff.TakeMaxSlotBlocks;
import com.dimchig.bedwarsbro.stuff.TrajectoryFireball;
import com.dimchig.bedwarsbro.stuff.TrajectoryPearl;
import com.dimchig.bedwarsbro.stuff.ZeroDeathHandler;
import com.dimchig.bedwarsbro.testing.AimHelper;
import com.dimchig.bedwarsbro.testing.BowAimbot;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, clientSideOnly = true, acceptedMinecraftVersions = Main.MCVERSIONS, guiFactory = "com.dimchig.bedwarsbro.gui.GuiFactory")
public class Main {
    public static final String MODID = "bedwarsbro";
    public static final String NAME = "BedwarsBro";
    public static final String VERSION = "2.6";
    public static final String MCVERSIONS = "[1.8, 1.12.2]";
    
    public static int ANTIMUT_DELAY = 1000 * 60; //1 minute
    
    public static Main instance;

    private static Configuration config;
    public static ConfigCategory clientConfig;
    public static KeybindHandler keybindHandler;
    public static  MyChatListener chatListener;
    public static  FileManager fileManager;
    public static  OnMyTickEvent myTickEvent;
    
    public static BaseProps baseProps;
    
    public static HintsBaseRadar hintsBaseRadar;
    public static HintsPlayerScanner hintsPlayerScanner;
    public static ScoreboardManager scoreboardManager;
    public static CustomScoreboard customScoreboard;
    public static GuiOnScreen guiOnScreen;
    public static HintsItemTracker itemTracker;
    public static ParticleController particleController;
    public static ParticleTrail particleTrail;
    public static GuiMinimap minimap;    
    public static GeneratorTimers generatorTimers;
    public static GuiPlayerFocus playerFocus;
    public static TNTJump tntjump;
    public static ParticlesAlwaysSharpness particlesAlwaysSharpness;
    public static DangerAlert dangerAlert;
    public static AutoSprint autoSprint;
    public static BedwarsMeow bedwarsMeow;
    public static AutoWaterDrop autoWaterDrop;
    private Field configChangedEventModIDField;
    public static CommandHintsFinderLookAtPlayer commandHintsFinderLookAtPlayer;
    public static CommandHintsFinderLookAt commandHintsFinderLookAt;
    public static CommandRainbowMessage commandRainbowMessage;
    public static CommandMeow commandMeow;
    public static CommandHistoryShop commandHistoryShop;
    public static CommandFindPlayerByName commandFindPlayerByName;
    public static CommandRainbowMessageSetter commandRainbowMessageSetter;
    public static CommandDexlandMeowSpoof commandDexlandMeowSpoof;
    public static CommandEnableESP commandEnableESP;
    public static CommandNameChanger commandNameChanger;
    public static CommandBWDiscord commandBWDiscord;
    public static CommandMute commandMute;
    public static CommandFriends commandFriends;
    public static MySerializer mySerializer;
    public static ShopManager shopManager;
    public static LoginHandler loginHandler;
    public static InvulnerableTime invulnerableTime;
    public static NamePlate namePlate;
    public static Draw3DText draw3DText;
    public static RainbowColorSynchronizer rainbowColorSynchronizer;   
    public static NamePlateRenderer namePlateRenderer;
    public static GuiResourceHologram guiResourceHologram;
    public static BowAimbot bowAimbot;
    public static AimHelper aimHelper;
    public static GuiBedESP guiBedESP;
    public static GuiRadarIcon guiRadarIcon;
    public static BedAutoTool bedAutoTool;
    public static GuiCrosshairBlocks guiCrosshairBlocks;
    public static TakeMaxSlotBlocks takeMaxSlotBlocks;
    public static TrajectoryPearl trajectoryPearl;
    public static TrajectoryFireball trajectoryFireball;
    public static FireballSpread fireballSpread;
    public static FreezeClutch freezeClutch;
    public static LightningLocator lightningLocator;
    public static LobbyFly lobbyFly;
    public static ZeroDeathHandler zeroDeathHandler;
    public static RotateBind rotateBind;
    public static FileNicknamesManager fileNicknamesManager;
    
    private boolean state = false;

    public Main() {
    	String config_name = this.MODID + "_" + this.VERSION + "_CONFIG.cfg";
        File configFile = new File(Loader.instance().getConfigDir(), config_name);

        try {
            //More ugly workarounds to get minecraft 1.8 to work with the same jar
            configChangedEventModIDField = ConfigChangedEvent.class.getDeclaredField("modID");
            configChangedEventModIDField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot find field", e);
        }

        config = new Configuration(configFile);
        initConfig();

        clientConfig = config.getCategory(Configuration.CATEGORY_CLIENT);
        //if(clientConfig.get("rememberAutoSprintState").getBoolean()) state = clientConfig.get("autoSprintState").getBoolean();
    }
    
    public static boolean getConfigBool(CONFIG_MSG type) {
    	return clientConfig.get(type.text).getBoolean();
    }
    public static String getConfigString(CONFIG_MSG type) {
    	return clientConfig.get(type.text).getString();
    }
    public static int getConfigInt(CONFIG_MSG type) {
    	return clientConfig.get(type.text).getInt();
    }
    public static double getConfigDouble(CONFIG_MSG type) {
    	return clientConfig.get(type.text).getDouble();
    }

    private static int config_messages_sort_idx_cnt = 1;
    public enum CONFIG_MSG {
    	ENABLE_BETTER_CHAT(2, "&eУлучшенный чат"),
    	ENABLE_BETTER_CHAT_STATISTIC_PREFIX(47, "&eУлучшенный чат &7→ &fСтатистика в начале сообщения"),
    	SCOREBOARD_ENGLISH(55, "&eEnglish scoreboard &7(команды на англ.)"),
    	AUTO_SPRINT(3, "Авто &dСпринт &7(Вечный бег)"),
    	
    	MINIMAP(4, "&6Миникарта"),
    	MINIMAP_FRAME(90, "&6Миникарта &7→ &fРамка"),
    	MINIMAP_X(5, "&6Миникарта &7→ &cX &7положение по горизонтали <-->&c*"),
    	MINIMAP_Y(6, "&6Миникарта &7→ &bY &7положение по вертикали ||"),
    	MINIMAP_SIZE(7, "&6Миникарта &7→ &fРазмер"),
    	MINIMAP_ADDITIONAL_INFORMTAION(68, "&6Миникарта &7→ &fДоп инфа про игроков&c*"),
    	MINIMAP_SHOW_HEIGHT(8, "&6Миникарта &7→ &fПоказывать разницы в высоте"),
    	MINIMAP_HIDE_PLAYERS_ON_SHIFT(49, "&6Миникарта &7→ &fСкрыть игроков на шифте&c*"),
    	
    	RADAR(9, "&aРадар"),
    	RADAR_PLAYERS(60, "&aРадар &7→ &fРадар игроков&c*"),
    	RADAR_ICON(57, "&aРадар &7→ &fПоказывать иконку&c*"),    	
    	RADAR_MESSAGES(10, "&aРадар &7→ &fОтправлять сообщения тиммейтам&c*"),
    	RADAR_RANGE_SECOND(83, "&aРадар &7→ &fРадиус срабатывания на раш&c*"),
    	RADAR_RANGE_FIRST(84, "&aРадар &7→ &fРадиус срабатывания на ломание&c*"),
    	    
    	BED_ESP(56, "&bПодсветка кроватей &7(bed ESP)"),
    	BED_AUTOTOOL(63, "&bЛомание кровати &7→ &fАвтовыбор инструментов&c*"),
    	BED_SCANNER(12, "&bСканер кровати&c*"),    	
    	BED_SCANNER_ANIMATION(13, "&bСканер кровати &7→ &fАнимация по слоям&c*"),
    	BED_SCANNER_ANIMATION_DELAY(14, "&bСканер кровати &7→ &fСкорость анимации (миллисекунды)"),  
    	BED_SCANNER_SEND_TEAM(72, "&bСканер кровати &7→ &fОтправлять сообщения тиммейтам&c*"), 
    		
    	PLAYER_FINDER(15, "&9Поиск игроков&c*"),
    	
    	ITEM_COUNTER(17, "&fКоличество &aизумрудов &fи &bалмазов &fв углу экрана"),
    	ITEM_COUNTER_BLOCKS(69, "&fКоличество &aблоков &fв углу экрана (сумарно)"),
    	
    	BEDWARS_MEOW(18, "&eMeow &7→ &fАвто-Сообщения после &cкила&f, &eсмерти&f, &aкровати &fи &dвыйграша&c*"),
    	BEDWARS_MEOW_WITH_COLORS(19, "&eMeow &7→ &fИспользовать цвета &7(только для &cд&6о&eн&aа&bт&9е&dр&cо&6в&7)"),    	
    	
    	POTION_TRACKER(21, "&aЭффекты &fот зелий &7→ &fВизуализация&c*"),
    	POTION_TRACKER_SOUNDS(22, "&aЭффекты &fот зелий &7→ &fЗвуки&c*"),
    	
    	DANGER_ALERT(23, "Предупреждение про &6Фаербол &fи &cЛук&c*"),
    	DANGER_ALERT_SOUND(24, "Предупреждение про &6Фаербол &fи &cЛук &7→ &fЗвуки"),
    	
    	GENERATOR_TIMERS(50, "&6Таймера для генераторов"),
    	GENERATOR_TIMERS_GAME_TIME(61, "&6Таймера для генераторов &7→ &fОбщее время игры"),
    	GENERATOR_TIMERS_ADVANCED(51, "&6Таймера для генераторов &7→ &fПродвинутая информация&c*"),
    	GENERATOR_TIMERS_POSITION(52, "&6Таймера для генераторов &7→ &fПозиция таймера&c*"),
    	GENERATOR_TIMERS_TIMELINE(53, "&6Таймера для генераторов &7→ &fТаймлайн игры&c*"),
    	GENERATOR_TIMERS_TIMELINE_WIDTH(54, "&6Таймера для генераторов &7→ &fТаймлайн игры &7→ &fШирина в &eпроцентах&c*"),
    	
    	BETTER_SHOP(25, "&eМагазин &7→ &fУлучшенная версия&c&l*"),
    	REMOVE_BUY_MESSAGE(26, "&eМагазин &7→ &fУбрать сообщение при покупке"),
    	
    	ZERO_DEATH(74, "&aZero &cDeath &f(Авто /leave /rejoin)"),
    	ZERO_DEATH_HEIGHT_TRESHOLD(75, "&aZero &cDeath &7→ &fКоличество блоков снизу от кровати&c*"),
    	ZERO_DEATH_FALL_CHECK(76, "&aZero &cDeath &7→ &fЛивать от смертельного урона от падения&c*"),
    	ZERO_DEATH_HEALTH_TRESHOLD(77, "&aZero &cDeath &7→ &fМин уровень здоровья&c*"),
    	ZERO_DEATH_HEALTH_CHECK_NEARBY(79, "&aZero &cDeath &7→ &fМин уровень здоровья &7→ &aПроверять окружение&c*"),
    	ZERO_DEATH_WRITE_IN_CHAT(78, "&aZero &cDeath &7→ &fПисать в чат&c*"),
    	
    	ROTATE_BIND_DEGREES(81, "&fБинд на &cрозворот &7→ &fУгол поворота&c*"),
    	ROTATE_BIND_SPEED(82, "&fБинд на &cрозворот &7→ &fСкорость поворота&c*"),
    	
    	NAMEPLATE(27, "&fНик от 3-его лица"),
    	NAMEPLATE_RAINBOW(28, "&fНик от 3-его лица &7→ &f&cР&eа&aд&bу&9ж&dн&cы&eй &fник"),
    	NAMEPLATE_RAINBOW_CONSTANT_COLOR(29, "&fНик от 3-его лица &7→ &fПостоянный цвет &7(в формате hex)&c&l*"),
    	NAMEPLATE_TEAM_COLOR(80, "&fНик от 3-его лица &7→ &fПостоянный цвет команды&c*"),
    	
    	CUSTOM_PARTICLES(30, "&fРазноцветные &cп&eа&aр&bт&9и&dк&cл&eы&c*"),
    	
    	PARTICLE_TRAIL(31, "&fСлед из &cп&eа&aр&bт&9и&dк&cл&eо&aв &7(во время бега)"),
    	PARTICLE_TRAIL_RAINBOW(32, "&fСлед из &cп&eа&aр&bт&9и&dк&cл&eо&aв &7→ &f&cР&eа&aд&bу&9ж&dн&cы&eе &fпартиклы"),
    	
    	RAINBOW_SPEED(33, "&fСкорость переливания &cр&eа&aд&bу&9г&dи &f"),
    	
    	
    	RAINBOW_MESSAGE_COMMAND(34, "Разноцветные &cс&eо&aо&bб&9щ&dе&cн&eи&aя &7→ &fКоманда в чате &7(только для &fLEGEND&7)&c*"),
    	RAINBOW_MESSAGE_MODE(35, "Разноцветные &cс&eо&aо&bб&9щ&dе&cн&eи&aя &7→ &fРежим&c*"),
    	RAINBOW_MESSAGE_GLOBAL(36, "Разноцветные &cс&eо&aо&bб&9щ&dе&cн&eи&aя &7→ &fПисать в глобальный чат"),
    	RAINBOW_MESSAGE_COLORS(37, "Разноцветные &cс&eо&aо&bб&9щ&dе&cн&eи&aя &7→ &fЦветовая палитра&c*"),
    	RAINBOW_MESSAGE_REPLACE_CHARS(38, "Разноцветные &cс&eо&aо&bб&9щ&dе&cн&eи&aя &7→ &fЗаменить символ"),
    	
    	WIN_EMOTE(39, "&cВыигрыш &7→ &fзаменить мир другими блоками&c&l*"), 		
    	
    	INVULNERABLE_TIMER(40, "&eВремя неуязвимости игроков&c&l* &7(после спавна)"),
    	INVULNERABLE_TIMER_SOUNDS(41,"&eВремя неуязвимости игроков &7→ &fЗвуки"),
    	
    	BRIDGE_AUTOANGLE_PITCH(43, "&aАвтоУгол для GodBridge &7→ &fНаклон по вертикали &7(45°, &a76°&7)&c*"),
    	BRIDGE_AUTOANGLE_MESSAGES(44, "&aАвтоУгол для GodBridge &7→ &fВыводить в чат сообщения"),
    	
    	AUTO_WATER_DROP(45, "&bАвто WaterDrop &7(Мод поставит ведро воды под тебя)"),
    	RESOURCES_HOLOGRAM(46, "&fГолограмма над ресурсами"),
    	
    	MAP_AUTO_SELECTER(48, "&fЛюбимые карты &7(только для &fLEGEND&7)"),
    	CROSSHAIR_BLOCKS_COUNT(64, "&fКоличество блоков рядом с прицелом"),
    	TAKE_BLOCKS_FROM_MAX_SLOT(65, "&fБрать блоки из максимального слота справа"),
    	PEARL_PREDICTION(66, "&fТраектория эндер-перла"),    	
    	FIREBALL_PREDICTION(67, "&fТраектория &6фаербола&c*"),
    	FIREBALL_SPREAD(85, "&fРазброс &6фаербола&c*"),
    	FIREBALL_SPREAD_OFFSET_X(86, "&fРазброс &6фаербола&7→ &fСместить на пару пикселей право&c*"),
    	NICK_CHANGER(71, "&fНик &7→ &fПоменять на свой &c(только у тебя)"),
    	AUTO_LOGIN_PWD(73, "&fАвтологин &7(за тебя вводит пароль)&7→ &fТвой пароль /l &b****"),
    	BETTER_TAB_CROSSED_NICKNAMES(88, "&fУлучшенный таб (зачеркивать если игрок в спектаторах или без кровати)");
    	
    	
    	//use 91 next
    	
    	
    	public final int id;
    	public final int sort_idx;
    	public final String text;

        private CONFIG_MSG(int id, String text) {
        	this.id = id;
        	
        	this.sort_idx = config_messages_sort_idx_cnt;
        	config_messages_sort_idx_cnt++;
        	
        	int char_code = 57344 + sort_idx;
        	String t = "&0" + (char)(char_code) + "&f" + text;
            this.text = ColorCodesManager.replaceColorCodesInString(t);
        }
        
        public String getName() {
        	String s = this.text.substring(3);
        	if (s.charAt(s.length() - 1) == '*') s = s.substring(0, s.length() - 3);
        	return s;
        }
    };
    
    public static String getConfigSettings() {
    	String text = "";
    	for (CONFIG_MSG m: CONFIG_MSG.values()) {
    		if (clientConfig.get(m.text) == null) continue; 
    		text += m.id + "=;=;=" + clientConfig.get(m.text).getString() + "==;===;==";
    	}
    	return text;
	}
    
    private void initConfig() {
    	//check same id
        if (CONFIG_MSG.values() == null || CONFIG_MSG.values().length == 0) return;
        for (CONFIG_MSG m1: CONFIG_MSG.values()) {
        	 for (CONFIG_MSG m2: CONFIG_MSG.values()) {
	        	if (m1.id == m2.id && !m1.text.equals(m2.text)) {
	        		for (int i = 0; i < 100; i++) {
	        			System.out.println("SAME ID " + m1.id + "!!!");
	        		}
	        	}
        	 }
        }
    	
    	
        config.load();

        Property prop;
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ENABLE_BETTER_CHAT.text, true,                              ColorCodesManager.replaceColorCodesInString("&f&aВключить&7/&cВыключить &fизменение чата"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ENABLE_BETTER_CHAT_STATISTIC_PREFIX.text, false,                              ColorCodesManager.replaceColorCodesInString("&f&aВключить&7/&cВыключить &fпоказ статистики игрока\n\"&7[&aкол-во килов&7, &aK/D&7, &aWinRate&7]&f\""));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.SCOREBOARD_ENGLISH.text, false,                              ColorCodesManager.replaceColorCodesInString("&f&aВключить&7/&cВыключить &fпоказ команд и текста на английском языке (как на Hypixel)\n&bНачни новую игру после включения!"));        
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_SCANNER.text, true,                                ColorCodesManager.replaceColorCodesInString("&fСканер кровати!\n&fПри нажатии на кнопку &7(смотри в настройках -> управление) &fсмотри на базу противников (кровать найдеться в радиусе 20 блоков). В чате напишет чем застроена кровать, например есть ли у них обса"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_SCANNER_ANIMATION.text, true, 					   ColorCodesManager.replaceColorCodesInString("&fЕсли вкючить это, то при нажатии на кнопку сканера кровати, у тебя кровать будет показываться по слоям! Это экспериментальная опция, если находиться близко к кровати у тебя может лагать и ты застрянешь в блоках. Использовать можно чтоб увидеть с какой стороны лучше начать ломать"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_SCANNER_ANIMATION_DELAY.text, 200,                 ColorCodesManager.replaceColorCodesInString("&fЭто задержка для анимации. То-есть каждый слой будет показываться по &e300 &fмиллисекунд, тоесть &e0.3 &fсекунды. Можешь поменять, но не ставь больше &e1000&f."));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_ESP.text, true,                 ColorCodesManager.replaceColorCodesInString("&fЭто типо xray для кроватей (Bed ESP), будет видно их сквозь блоки, очень удобно когда большая застройка"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_AUTOTOOL.text, true,                 ColorCodesManager.replaceColorCodesInString("&fКогда ты начнешь ломать блок, у тебя будут выбираться &bнужные инструменты &f(если куплены), чтоб сломать кровать быстрее. &cНе работает, если далеко от кровати, и если не выбран один из инструментов (выбери любой и начни копать)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BED_SCANNER_SEND_TEAM.text, false,                 ColorCodesManager.replaceColorCodesInString("&fСообщения от сканнера кровати будут слаться команде (не чаще чем раз в минуту), чтоб избежать мут. Чтоб писать цветным в чате, надо иметь донат LEGEND и включить \"&eMeow с цветами&f\"!"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR.text, true,                               ColorCodesManager.replaceColorCodesInString("&fЕсли враг подойдет близко к твоей базе, тебе прийдет сообщение со звуком типо:\n\"&eНас рашит &aЗеленый&f\"\nК сожалению, если &cдалеко уйти от базы&f, то работать &cне будет&f!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR_PLAYERS.text, false,                               ColorCodesManager.replaceColorCodesInString("&fЕсли &c&lкровать сломана&f, и враг подойдет близко к тебе, тебе прийдет сообщение со звуком типо:\n\"&eРядом &aЗеленый&f\"!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR_ICON.text, true,                               ColorCodesManager.replaceColorCodesInString("&fЕсли враг подойдет близко к твоей базе, будт иконка кровати на 1 секунду поцентру сверху, чтоб ты заметил. Точно также будет картинка игрока, если радар игроков включен"));        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR_MESSAGES.text, false,                               ColorCodesManager.replaceColorCodesInString("&fСообщения от радара будут слаться команде (не чаще чем раз в минуту), чтоб избежать мут. Чтоб писать цветным в чате, надо иметь донат LEGEND и включить \"&eMeow с цветами&f\"!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR_RANGE_SECOND.text, 20,                               ColorCodesManager.replaceColorCodesInString("&fЕсли игрок войдет в эту зону, то радар напишет \"&eНас рашит игрок&f\""));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RADAR_RANGE_FIRST.text, 8,                               ColorCodesManager.replaceColorCodesInString("&fЕсли игрок войдет в эту зону, то радар напишет \"&cНас ломает игрок&f\""));
        
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ITEM_COUNTER.text, true,                               ColorCodesManager.replaceColorCodesInString("&fПоказывает количество &aизумрудов&f, и &bалмазов &fв правом нижнем углу. Очень удобно"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ITEM_COUNTER_BLOCKS.text, true,                               ColorCodesManager.replaceColorCodesInString("&fПоказывает количество &bблоков &fв правом нижнем углу. Очень удобно"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.PLAYER_FINDER.text, true,                                     ColorCodesManager.replaceColorCodesInString("&fПоиск игроков. При нажатии на кнопку &7(смотри в настройках -> управление) &fмод попытается найти всех игроков в зоне видимости, это приблизительно &e60 блоков&f. В чате будет сообщение с ником игрока. Если &aнавести мышкой &fна сообщение, то ты увидешь:\n\n\"&7(&fx&7, &fy&7, &fz&7, &c[дистанция до игрока]&7) &fтип брони и &fпредмет в руке\"\n\n&fКрасная звездочка&c*&f, или &c**&f, означают что у игрока либо алмазка, либо лук, перл или силка. Это как сложность игрока типо\n\n&fЕсли &aНАЖАТЬ &fна сообщение, то твоя голова повернется в сторону этого игрока"));
         
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.WIN_EMOTE.text, true,                                        ColorCodesManager.replaceColorCodesInString("&fАнимация при выйграше. В конце катки мир вокруг тебя изменится на блоки цвета команды. Если выйграли зеленые, то мир измениться на изумрудные блоки, шерсть и тд =)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP.text, true,                                          ColorCodesManager.replaceColorCodesInString("&fМиникарта, показывает игроков в виде разноцветных стрелок.\n&c&lЖрет около 40FPS (у меня без карты 200, с ней 160). Включайте на свое усмотрение.\n&fВесь мир - серый, так удобнее. Так же показывает числами количество ресурсов. Не показывает меньше &b3-х алмазов&f, если на базе, то < &e12 золота&f и < &f64 железа."));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_FRAME.text, false,                                          ColorCodesManager.replaceColorCodesInString("&fВозможность убрать эту квадратную рамку"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_X.text, "0",                                  ColorCodesManager.replaceColorCodesInString("&fПозиция миникарты (X это <--->, Y это по вертикали)\n &fЕсли &cX &eположительный &7(не меньше 0)&f, то это будет отступ &aслева&f\n &fЕсли &cX &eотрицательный &7(меньше 0)&f, будет отступ &aсправа\n &fЕсли &bY &eположительный &7(не меньше 0)&f, то это будет отступ &aсверху&f\n &fЕсли &bY &eотрицательный &7(меньше 0)&f, будет отступ &aснизу\n\nВот примеры настроек:\n &fЛевый верхний угол:  &cX = 0&7,&bY = 0\n &fПравый верхний угол: &cX = -0&7, &bY = 0\n &fЛевый нижний угол:   &cX = 0&7, &bY = -0\n &fПравый нижний угол:  &cX = -0&7, &bY = -0\n\n &fМиникарта с отступом &c5 &fот левого края, и &b15 &fсвеху&f: &cX = -5&7, &bY = 15"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_Y.text, "0",                                  ColorCodesManager.replaceColorCodesInString("&fПрочитай в \"" + CONFIG_MSG.MINIMAP_X.getName() + "\""));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_SIZE.text, 100,                                      ColorCodesManager.replaceColorCodesInString("&fРазмер миникарты &7(от &e50 &7до &e150&7)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_ADDITIONAL_INFORMTAION.text, true,                                      ColorCodesManager.replaceColorCodesInString("&fНа карте будут отображаться предметы над игроками - это то, что они держат в руке. Так же &a&lполоска &fнад игроком - &aон строится блоками&f!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_SHOW_HEIGHT.text, true,                                      ColorCodesManager.replaceColorCodesInString("&fНа карте будут отображаться числа под игроками - это разнице по высоте с тобой (только если ты ниже их)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MINIMAP_HIDE_PLAYERS_ON_SHIFT.text, false,                                      ColorCodesManager.replaceColorCodesInString("&fТипо это будет &aлегитная &fминикарта, но блин, кому она нужна)"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.CUSTOM_PARTICLES.text, false,                                ColorCodesManager.replaceColorCodesInString("&fПри каждом ударе будет разноцветные партиклы, если враг &aзеленый &f- то &aзеленого &fцвета. Не работает с модом Particle Customizer"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BRIDGE_AUTOANGLE_PITCH.text, 76f,                            ColorCodesManager.replaceColorCodesInString("&fПри нажатии на кнопку &7(смотри в настройках -> управление) &fсмотри на край блока. Твой курсор развернеться в &b(45°, 76°)&f. Вот эти 76° можно поменять. Кому как удобнее для godbridge."));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BRIDGE_AUTOANGLE_MESSAGES.text, true,                   ColorCodesManager.replaceColorCodesInString("&fБудет/Не будет писать в чат каждый раз сообщение"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_MESSAGE_COMMAND.text, "/r",                          ColorCodesManager.replaceColorCodesInString("&fТОЛЬКО ДЛЯ ДОНАТЕРОВ!\n&fКоманда чтоб создать радужный цвет\nНапример &c/r всем привет! &7= &cВ&6с&eе&aм &bп&9р&dи&cв&6е&eт&a!" + 
        "\n&fЕсть 2 способа поставить &eсвои цвета&b\n"
        + "&f1 медленный: в конфиге найди &f\"&eЦветовая палитра&f\". Если навести мышкой будут написаны все цветовый кода, от 0 до 9 и &aa &bb &cc &dd &ee &ff. Чтоб создать свою палитру тебе надо просто написать слитно кода, типо &f\"&aa&dd&ee&f\" это &aзелено&7-&dрозово&7-&eжелтый&f. Или &f\"&cc&66&ee&aa&bb&dd&f\" это &cр&6а&eд&aу&bж&dн&cы&6й&f. И в конце можно добавить &f\"&e+&f\" и код для &lжырного&f, &oнаклоненного&f, &nподчёркнутого&f. Типо &f\"&aa&dd&ee&f+l\" это жирным все будет, или например \"&dd&55&f+lno\" это &d&l&n&oвот&f &5&l&n&oтакой&f &d&l&n&oтекст\n"
        + "&f2 способ это тоже самое, но не надо заходить в конфиг если знаешь на память кода. Команда &b/rs help &fтам все написано"));
        String description = "&fСтавь цифру &e0 &fили &e1\n&60 (Mix):\n&e- если текст &bмаленький &e- &cк&6а&eж&aд&bы&9й &cс&6и&eм&aв&bо&9л &cр&6а&eз&aн&bо&9г&dо &cц&6в&eе&aт&bа&9\n";
        description       += "&e- если текст &bсредний &e- &cк&6аж&eды&aе &b2 &9си&dмв&cол&6а &eра&aзн&bог&9о &dцв&cет&6а\n";
        description       += "&e- если текст &bбольшой &e- &cкаждое &6слово &eбудет &aразного &bцвета\n";
        description       += "&61 (Слова):\n&e- &cкаждое &6слово &eвсегда &aбудет &bразного &9цвета";
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_MESSAGE_MODE.text, 0,                                ColorCodesManager.replaceColorCodesInString(description));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_MESSAGE_GLOBAL.text, true,                        ColorCodesManager.replaceColorCodesInString("&fДобавляет \"!\" в начало сообщения"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_MESSAGE_COLORS.text, "e",            ColorCodesManager.replaceColorCodesInString("&fТут ты можешь поменять цвета для сообщения. Вводи в правильном порядке, разделяй запятыми, либо сначала цвета, а потом + и цвета, которые будут добавлены ко всем в конец. Например enl,anl,bnl или eab+nl будет &e&n&lт&a&n&lе&b&n&lк&e&n&lс&a&n&lт&f.\n&fВот все цвета: &11 &22 &33 &44 &55 &66 &77 &88 &99 &f(0 - черный) &aa &bb &cc &dd &ee &ff &f&ll (жирный)&r &f&nn (подчеркнутый) &f&mm (зачеркнутый) &f&oo (наклон)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_MESSAGE_REPLACE_CHARS.text, "і=ы",               ColorCodesManager.replaceColorCodesInString("&fЗаменяет 1-й символ на 2-й. Пиши вот так:\n&c[символ для замены]&7=&a[символ на который]"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.POTION_TRACKER.text, true,                           ColorCodesManager.replaceColorCodesInString("&fПоказывает слева все эффекты, типо силка, скорка... и их время"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.POTION_TRACKER_SOUNDS.text, true,                    ColorCodesManager.replaceColorCodesInString("&fКогда меньше 5 секунд на зельке, будет такой звук \"тик\", и потом когда зелька пропадет, будет звук шипения"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.DANGER_ALERT.text, true,                                     ColorCodesManager.replaceColorCodesInString("&fЕсли кто-то будем целиться в тебя из лука или фаерболом, то мод напишет в чат и будет звук!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.DANGER_ALERT_SOUND.text, true,                               ColorCodesManager.replaceColorCodesInString("&fБудет ли пищать мод"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.AUTO_SPRINT.text, true,                                      ColorCodesManager.replaceColorCodesInString("&fВечный Control &7(всегда бежишь &eбыстро&7, как нажать W 2 раза)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.AUTO_WATER_DROP.text, false,                                  ColorCodesManager.replaceColorCodesInString("&fЕсли у тебя в слотах есть вода и ты падаешь с высоты, то мод клачнет за тебя"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BEDWARS_MEOW.text, false,                                    ColorCodesManager.replaceColorCodesInString("&fЭто типо &eDexlandMeow&f, только намного лучше. После каждого кила, или сломаной кровати в чат будут писаться сообщения от тебя типо \"&aPlayer&f, ты лох!\"\n\n&f&lСвои сообщения можно добавить командой &e/meow"));        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BEDWARS_MEOW_WITH_COLORS.text, false,                        ColorCodesManager.replaceColorCodesInString("&fТолько для ДОНАТЕРОВ\n&fВсе сообщения будут переливаться радугой, вот так:\n\n&cБро, &eтебе &aнадо &bбольше &9тренероваться!\n&fВсе настройки разноцветности берутся из \"" + CONFIG_MSG.RAINBOW_MESSAGE_COLORS.getName() + "\""));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.REMOVE_BUY_MESSAGE.text, true,                        ColorCodesManager.replaceColorCodesInString("&fВ магазине не будут отображаться сообщения"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BETTER_SHOP.text, true,                        ColorCodesManager.replaceColorCodesInString("&fВ магазине предметы, на которых нету ресов, будут невидимыми\n\n&b&lНапиши команду &e/bwbroshop &b&lдля детальной статистики своих покупок!"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH.text, false,                        ColorCodesManager.replaceColorCodesInString("&fЕсли хочешь себе очень высокий &eK/D&f, то перед своей смертью ты можешь прописать &c/leave &fи &a/rejoin&f, тогда смерть не засчитается. Будет срабатывать АвтоLeave по одному из условий:\n &f1) Либо ты упал ниже, чем &aN &fблоков от высоты своей кровати\n &f2) &fЛибо если ты падаешь с смертельной высоты &7(это если ты ударишся об землю! &aЕсли снизу вода, или у тебя в руке вода, или активирован Автоватердроп, то не ливнешь&7)\n &f3) Либо у тебя <= указанного &aколичества здоровья&f &7(максимум 20) &7(также учитывается окружение, смотри опцию ниже)&f\n\n&cИногда может забагатся и не ливнуть тебя. Например когда тебя убили с силкой, и ни одно из условий не выполнилось. Советую НЕ МЕНЯТЬ значения ниже, так как я их подобрал оптимально. Если ты уверен можешь поменять. &c&lНЕ РАБОТАЕТ В ПАТИ, РАБОТАЕТ ТОЛЬКО В ИГРЕ!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH_HEIGHT_TRESHOLD.text, 10,                        ColorCodesManager.replaceColorCodesInString("&fУсловие если ты упал ниже, чем &aN &fблоков от высоты своей кровати\n\n&b&lПОСТАВЬ &c&l999 &b&lЧТОБ ИГНОРИРОВАТЬ УСЛОВИЕ"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH_FALL_CHECK.text, true,                        ColorCodesManager.replaceColorCodesInString("&fУсловие если ты падаешь с смертельной высоты &7(это если ты ударишся об землю! &aЕсли снизу вода, или у тебя в руке вода, или включен Автоватердроп, то не ливнешь&7)&f\n\n&b&lПОСТАВЬ &c&lFALSE &b&lЧТОБ ИГНОРИРОВАТЬ УСЛОВИЕ\n\n&fБудет учитыватся твое здоровье. Тоесть если у тебя осталось пол хп, ты сможешь упасть только с 13 блоков не умерев. Рассчет по формуле &aN &7(max) = &a[твое хп] &e+ &c[высота падения] &e+ &b3.5 &e+ &d[уровень чара брони на защиту, тоже формула] &e+ &a[уровень эффекта прыгучести]&f. Если ты не знал, то твоя броня &cбез чаров &fникак не влияет на урон падения"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH_HEALTH_TRESHOLD.text, 4f,                        ColorCodesManager.replaceColorCodesInString("&fУсловие если у тебя <= &7(меньше или равно) &fуказанного &aколичества здоровья&f &7(максимум 20)\n\n&b&lПОСТАВЬ &c&l999 &b&lЧТОБ ИГНОРИРОВАТЬ УСЛОВИЕ"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH_HEALTH_CHECK_NEARBY.text, true,                        ColorCodesManager.replaceColorCodesInString("&fУсловие если у тебя <= &7(меньше или равно) &fуказанного &aколичества здоровья&f И ТАКЖЕ:\n &f1) Либо рядом есть игрок &7(не тиммейт)\n &f2) Либо рядом летит стрела\n\n&aТоесть если ты упадешь с высоты &7(или получишь большой урон) &a&lв дали от других игроков&a, то мод &cне перезайдет &aв игру."));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ZERO_DEATH_WRITE_IN_CHAT.text, true,                        ColorCodesManager.replaceColorCodesInString("&fМод будет писать в чате когда он ливает за тебя, чтоб ты понимал когда он сработал. Можно навести мышкой и напишет причину лива"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ROTATE_BIND_DEGREES.text, 180,                        ColorCodesManager.replaceColorCodesInString("&fПо нажатию на кнопку &7(Настройки -> Управление) &fтвой прицел развернется на &aN &fградусов.\n"
        		+ "\n &7- &fПолуоборот вправо &7= &e180"
        		+ "\n &7- &fПолуоборот влево  &7= &e-180"
        		+ "\n &7- &fПолный оборот вправо &7= &e360"
        		+ "\n &7- &fПолный оборот влево  &7= &e-360"
		        + "\n &7- &fДвойной оборот &7= &e720"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.ROTATE_BIND_SPEED.text, 1f,                        ColorCodesManager.replaceColorCodesInString("&fСкорость поворота. Чем &aбольше &fскорость &7- &fТем &aдольше &fповорот! &b0 &7= &fмгновенный поворот"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.PARTICLE_TRAIL.text, false,                        ColorCodesManager.replaceColorCodesInString("&fБудут спавниться партиклы за тобой во время бега. Во время строительства не будут мешать"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.PARTICLE_TRAIL_RAINBOW.text, true,                        ColorCodesManager.replaceColorCodesInString("&2&ltrue &7- &fВсегда будут спавниться разноцветные партиклы\n&f&4&lfalse &7- &fВо время катки будут партиклы цвета твоей команды"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.NAMEPLATE.text, true,                        ColorCodesManager.replaceColorCodesInString("&fЕсли нажать &eF5&f, то ты увидишь свой ник"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.NAMEPLATE_RAINBOW.text, true,                        ColorCodesManager.replaceColorCodesInString("&fНик будет красиво переливаться радужным &cг&eр&aа&bд&9и&dе&cн&eт&aо&bм"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.NAMEPLATE_RAINBOW_CONSTANT_COLOR.text, "",                        ColorCodesManager.replaceColorCodesInString("&fОставь пустым, если хочешь радужный\nВводи в цвет в таком формате: &a#00ff00&7, или &9#4287f5 &7(можешь загуглить)"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.NAMEPLATE_TEAM_COLOR.text, false,                        ColorCodesManager.replaceColorCodesInString("&fПоставь &atrue&f, если хочешь чтоб твой ник был цвета твоей команды"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RAINBOW_SPEED.text, 1,                        ColorCodesManager.replaceColorCodesInString("&fСкорость переливания радуги\n&fСтавь от &e1 &fдо &e100 &7(рекомендую около 10)"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.INVULNERABLE_TIMER.text, true,                        ColorCodesManager.replaceColorCodesInString("&fПосле спавна ты не можешь бить игроков 3 секунды. Над ними будет показываться время"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.INVULNERABLE_TIMER_SOUNDS.text, true,                        ColorCodesManager.replaceColorCodesInString("&fБудет возпроизводиться 3 звука, разной тональности и громкости"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.RESOURCES_HOLOGRAM.text, true,                        ColorCodesManager.replaceColorCodesInString("&fБудет такая надпись над алмазами, изумрудами показывать &eсколько их лежит на земле"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.MAP_AUTO_SELECTER.text, "",                        ColorCodesManager.replaceColorCodesInString("&fМожно заходить в игру &cбыстрым стартом&f, а можно выбирать карту. Тут ты можешь добавить свои &bлюбимые &fкарты, и они буду отображаться &bотдельно &fв &bголубом стекле\n\n&e&lПиши через запятую, вот так:\n\n&fAcropolis, SubwaySurfers, SkyRise\n"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS.text, true,                        ColorCodesManager.replaceColorCodesInString("&fВ углу экрана будут находиться 2 таймера, на &bалмазы &fи &aизумруды"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS_GAME_TIME.text, true,                        ColorCodesManager.replaceColorCodesInString("&fВ углу экрана будет длительность катки"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS_ADVANCED.text, false,                        ColorCodesManager.replaceColorCodesInString("&fБудет добавлен еще 1 таймер на улучшения генераторов на новый уровень"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS_POSITION.text, 2,                        ColorCodesManager.replaceColorCodesInString("&fСтавь от &e1 &fдо &e4&f. Это в каком углу будут таймера\n\n&b1 &7- &eлевый верхний\n&b2 &7- &eправый верхний\n&b3 &7- &eправый нижний\n&b4 &7- &eлевый нижний\n"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS_TIMELINE.text, false,                        ColorCodesManager.replaceColorCodesInString("&fВключи, посмотри. Это временная полоса игры, как компас"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.GENERATOR_TIMERS_TIMELINE_WIDTH.text, 75,                        ColorCodesManager.replaceColorCodesInString("&fСтавь от &e50 &fдо &e100&f. Это ширина таймлайна в процентах от ширины экрана"));
                               
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.CROSSHAIR_BLOCKS_COUNT.text, true,                        ColorCodesManager.replaceColorCodesInString("&fЕсли у тебя будет меньше 6 блоков в слоте, и ты будешь строиться, появится цифра над твоим прицелом. Это удобно, чтоб не отводить взгляд во время строительства"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.TAKE_BLOCKS_FROM_MAX_SLOT.text, false,                        ColorCodesManager.replaceColorCodesInString("&fЕсли у тебя будет в хотбаре больше 1 слота с блоками, будет выбираться слот как можно правее"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.PEARL_PREDICTION.text, true,                        ColorCodesManager.replaceColorCodesInString("&fЕсли у тебя будет перл в руке, то будет показана &aзеленая &7(если попадаешь) &fили &cкрасная зона&f. Так же будет показано куда летит перл"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.FIREBALL_PREDICTION.text, true,                        ColorCodesManager.replaceColorCodesInString("&fЕсли будут лететь фаерболы, то будет показано куда он летит"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.FIREBALL_SPREAD.text, true,                        ColorCodesManager.replaceColorCodesInString("&fЕсли взять в руки фаербол, то будет на месте прицела 3 круга разных цветов:"
        		+ "\n &cКрасный &7- &fшанс попадания &c100%"
        		+ "\n &eЖелтый  &7- &fшанс попадания &e50%"
        		+ "\n &aЗеленый &7- &fшанс попадания &a25%"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.FIREBALL_SPREAD_OFFSET_X.text, true,                        ColorCodesManager.replaceColorCodesInString("&fОригинальный майнкрафт прицел немного смещен право на пару пикселей, по этому чтоб центр кружечка был идеально по центру прицела, можно его немного сместить. Если ты &aпользуешься модом &fна прицел, у тебя может быть кружочек не очень по центру, по этому &aвыключи эту опцию&f"));
        
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.NICK_CHANGER.text, "",                        ColorCodesManager.replaceColorCodesInString("&fРаботает только у тебя, другие видят твой РЕАЛЬНЫЙ ник! Оставь пустым, если хочешь выключить. Можешь писать что хочешь, даже с пробелами)\n\n&c&lРАБОТАЕТ ТОЛЬКО С УЛУЧШЕННЫМ ЧАТОМ!"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.AUTO_LOGIN_PWD.text, "",                        ColorCodesManager.replaceColorCodesInString("&fЕсли тебе лень вписывать свой пароль через &e/l &b1234 &fможешь написать его тут, и мод будет вводить команду за тебя. Пароль никуда не отправляется и &cХРАНИТСЯ ТОЛЬКО У ТЕБЯ&f. Если не хочешь - оставь пустым"));
        prop = config.get(Configuration.CATEGORY_CLIENT, CONFIG_MSG.BETTER_TAB_CROSSED_NICKNAMES.text, true,                        ColorCodesManager.replaceColorCodesInString("&fДля серверов на виживании можно выключить, или если тебе не нравится что зачеркнуто"));
        
        config.save();
    }
    
    public static void saveConfig() { 
    	config.save();	
    }
    
    public static void readNickChanger() {
    	clientConfig.get(CONFIG_MSG.NICK_CHANGER.text).set(ColorCodesManager.replaceColorCodesInString(clientConfig.get(CONFIG_MSG.NICK_CHANGER.text).getString()));
    	Property s = clientConfig.get(CONFIG_MSG.NICK_CHANGER.text);
    	if (s == null || s.getString() == null) {
    		chatListener.nickChanger = null;
    		return;
    	}
    	chatListener.nickChanger = s.getString();
    }
    
    private static long time_last_update = 0;
    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent event) throws IllegalAccessException {
        if(configChangedEventModIDField.get(event).equals(MODID)) {
        	saveConfig();
            updateAllBooleans();
            
            Date date = new Date();
            if (date.getTime() - time_last_update > 1000) {
            	ChatSender.addText("&c&lBedwars&f&lBro: &aобновлен успешно!");
            	time_last_update = date.getTime();
            }
        }
    }
    
    public static void updateAllBooleans() {
    	chatListener.IS_MOD_ACTIVE = getConfigBool(CONFIG_MSG.ENABLE_BETTER_CHAT);
        
        myTickEvent.updateHintsBooleans();
        minimap.updateBooleans();                    
        generatorTimers.updateBooleans();
        particleController.updateBooleans();
        guiOnScreen.updateBooleans();
        autoSprint.updateBooleans();
        playerFocus.updateBooleans();
        bedwarsMeow.updateBooleans();
        particleTrail.updateBooleans();
        hintsBaseRadar.updateBooleans();
        rainbowColorSynchronizer.updateBooleans();
        shopManager.updateBooleans();
        guiBedESP.updateBooleans();
        guiRadarIcon.updateBooleans();
        customScoreboard.updateBooleans();
        bedAutoTool.updateBooleans();
        guiCrosshairBlocks.updateBooleans();
        takeMaxSlotBlocks.updateBooleans();
        trajectoryPearl.updateBooleans();
        trajectoryFireball.updateBooleans();
        fireballSpread.updateBooleans();
        zeroDeathHandler.updateBooleans();
        rotateBind.updateBooleans();
        namePlateRenderer.updateBooleans();
        readCommandRainbowMessage();
        readNickChanger();
    }

    @Mod.EventHandler
    public void preInitialization(FMLPreInitializationEvent event) {
    	
    }
    
    static void readCommandRainbowMessage() {
    	commandRainbowMessage = new CommandRainbowMessage(getConfigString(CONFIG_MSG.RAINBOW_MESSAGE_COMMAND));
    	ClientCommandHandler.instance.registerCommand(commandRainbowMessage);
    }

    @Mod.EventHandler
    public void postInitialization(FMLPostInitializationEvent event) {
    	 keybindHandler = new KeybindHandler(this);
    	
         chatListener = new MyChatListener();
         fileManager = new FileManager();
         
         minimap = new GuiMinimap();
         generatorTimers = new GeneratorTimers();
         playerFocus = new GuiPlayerFocus();
         tntjump = new TNTJump();
         dangerAlert = new DangerAlert();
         guiOnScreen = new GuiOnScreen(this);
         itemTracker = new HintsItemTracker();
         scoreboardManager = new ScoreboardManager();
         customScoreboard = new CustomScoreboard();
         hintsBaseRadar = new HintsBaseRadar();
         hintsPlayerScanner = new HintsPlayerScanner();
         myTickEvent = new OnMyTickEvent();
         particleController = new ParticleController();
         particleTrail = new ParticleTrail();
         bedwarsMeow = new BedwarsMeow();
         autoSprint = new AutoSprint();
         autoWaterDrop = new AutoWaterDrop();
         //autoEnderChest = new AutoEnderChest();
         shopManager = new ShopManager();
         loginHandler = new LoginHandler();
         particlesAlwaysSharpness = new ParticlesAlwaysSharpness();
         invulnerableTime = new InvulnerableTime();
         namePlate = new NamePlate();
         draw3DText = new Draw3DText(); 
         mySerializer = new MySerializer(); 
         rainbowColorSynchronizer = new RainbowColorSynchronizer(); 
         namePlateRenderer = new NamePlateRenderer(); 
         guiResourceHologram = new GuiResourceHologram(); 
         bowAimbot = new BowAimbot(); 
         aimHelper = new AimHelper(); 
         guiBedESP = new GuiBedESP(); 
         guiRadarIcon = new GuiRadarIcon(); 
         bedAutoTool = new BedAutoTool(); 
         guiCrosshairBlocks = new GuiCrosshairBlocks(); 
         takeMaxSlotBlocks = new TakeMaxSlotBlocks(); 
         trajectoryPearl = new TrajectoryPearl(); 
         trajectoryFireball = new TrajectoryFireball(); 
         fireballSpread = new FireballSpread(); 
         zeroDeathHandler = new ZeroDeathHandler(); 
         rotateBind = new RotateBind(); 
         freezeClutch = new FreezeClutch(); 
         lightningLocator = new LightningLocator();
         lobbyFly = new LobbyFly();
         fileNicknamesManager = new FileNicknamesManager();
         new HintsFinder();
         
         //fileManager.clearLog();
         
         MinecraftForge.EVENT_BUS.register(this);
         MinecraftForge.EVENT_BUS.register(keybindHandler);
         MinecraftForge.EVENT_BUS.register(playerFocus);
         MinecraftForge.EVENT_BUS.register(tntjump);
         MinecraftForge.EVENT_BUS.register(chatListener);
         MinecraftForge.EVENT_BUS.register(myTickEvent);
         MinecraftForge.EVENT_BUS.register(guiOnScreen);
         MinecraftForge.EVENT_BUS.register(bedwarsMeow);
         MinecraftForge.EVENT_BUS.register(autoSprint);
         MinecraftForge.EVENT_BUS.register(particlesAlwaysSharpness);
         MinecraftForge.EVENT_BUS.register(shopManager);
         MinecraftForge.EVENT_BUS.register(loginHandler);
         MinecraftForge.EVENT_BUS.register(namePlateRenderer);
         MinecraftForge.EVENT_BUS.register(bowAimbot);
         MinecraftForge.EVENT_BUS.register(aimHelper);
         MinecraftForge.EVENT_BUS.register(guiBedESP);
         MinecraftForge.EVENT_BUS.register(guiRadarIcon);
         MinecraftForge.EVENT_BUS.register(bedAutoTool);
         MinecraftForge.EVENT_BUS.register(guiCrosshairBlocks);
         MinecraftForge.EVENT_BUS.register(takeMaxSlotBlocks);
         MinecraftForge.EVENT_BUS.register(trajectoryPearl);
         MinecraftForge.EVENT_BUS.register(trajectoryFireball);
         MinecraftForge.EVENT_BUS.register(fireballSpread);
         MinecraftForge.EVENT_BUS.register(lightningLocator);
         MinecraftForge.EVENT_BUS.register(lobbyFly);
         MinecraftForge.EVENT_BUS.register(zeroDeathHandler);
         MinecraftForge.EVENT_BUS.register(rotateBind);
         
         
         
        commandHintsFinderLookAtPlayer = new CommandHintsFinderLookAtPlayer();
        commandHintsFinderLookAt = new CommandHintsFinderLookAt();
        commandMeow = new CommandMeow(this);
        commandRainbowMessageSetter = new CommandRainbowMessageSetter();
        commandDexlandMeowSpoof = new CommandDexlandMeowSpoof();
        commandEnableESP = new CommandEnableESP();
        commandNameChanger = new CommandNameChanger();
        commandBWDiscord = new CommandBWDiscord();
        commandMute = new CommandMute(this);
        commandFriends = new CommandFriends(this);
        commandHistoryShop = new CommandHistoryShop(this);
        commandFindPlayerByName = new CommandFindPlayerByName();
     	ClientCommandHandler.instance.registerCommand(commandHintsFinderLookAtPlayer);
     	ClientCommandHandler.instance.registerCommand(commandHintsFinderLookAt);
     	ClientCommandHandler.instance.registerCommand(commandMeow);
     	ClientCommandHandler.instance.registerCommand(commandHistoryShop);
     	ClientCommandHandler.instance.registerCommand(commandFindPlayerByName);
     	ClientCommandHandler.instance.registerCommand(commandRainbowMessageSetter);
     	ClientCommandHandler.instance.registerCommand(commandDexlandMeowSpoof);
     	ClientCommandHandler.instance.registerCommand(commandEnableESP);
     	ClientCommandHandler.instance.registerCommand(commandNameChanger);
     	ClientCommandHandler.instance.registerCommand(commandBWDiscord);
     	ClientCommandHandler.instance.registerCommand(commandMute);
     	ClientCommandHandler.instance.registerCommand(commandFriends);
     	
     	ClientCommandHandler.instance.registerCommand(new CommandModHelp(this, "/bwbro"));
     	ClientCommandHandler.instance.registerCommand(new CommandModHelp(this, "/bedwarswbro"));
     	ClientCommandHandler.instance.registerCommand(new CommandModHelp(this, "/bro"));
     	
     	readCommandRainbowMessage();
     	     	
     	baseProps = new BaseProps();
     	baseProps.readProps();
     	baseProps.readMessages();
    	
    	updateAllBooleans();
    }

    public ConfigCategory getClientConfig() {
        return clientConfig;
    }
    
    //porps
	public static String getPropModLastVersion() { 
		if (baseProps == null) return null;
    	return baseProps.getModLastVersion(); 
	}
	public static String getPropModUpdateLink() { 
		if (baseProps == null) return null;
    	return baseProps.getModUpdateLink(); 
	}
	public static String getPropDiscordLink() { 
		if (baseProps == null) return null;
    	return baseProps.getDiscordLink(); 
	}
	public static String getPropModAuthor() { 
		if (baseProps == null) return null;
    	return baseProps.getModAuthor(); 
	}
	public static String getPropAuthorPrefix() { 
		if (baseProps == null) return null;
		return baseProps.getModAuthorPrefix(); 
	}
	public static ArrayList<String> getPropModBannedUsers() { 
		if (baseProps == null) return new ArrayList<String>();
    	return baseProps.getModBannedUsers();  
	}
	public static boolean isPropUserBanned(String player_name) {
		if (baseProps == null) return false;
    	return baseProps.isUserBanned(player_name); 
	}
	
	public static ArrayList<String> getPropModAdminUsers() { 
		if (baseProps == null) return new ArrayList<String>();
    	return baseProps.getModAdminUsers();  
	}
	public static boolean isPropUserAdmin(String player_name) {
		if (baseProps == null) return false;
    	return baseProps.isUserAdmin(player_name); 
	}
	public static boolean isPropSelfAdmin() {
		if (baseProps == null || Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().thePlayer.getName() == null) return false;
    	return baseProps.isUserAdmin(Minecraft.getMinecraft().thePlayer.getName()); 
	}
}
