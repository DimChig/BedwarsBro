package com.dimchig.bedwarsbro;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.input.Keyboard;

import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.gui.GuiPlayerFocus;
import com.dimchig.bedwarsbro.gui.GuiMinimap;
import com.dimchig.bedwarsbro.gui.GuiMinimap.Pos;
import com.dimchig.bedwarsbro.particles.ParticleController;
import com.dimchig.bedwarsbro.particles.ParticleFinalKillEffect;
import com.dimchig.bedwarsbro.serializer.MySerializer;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow;
import com.dimchig.bedwarsbro.stuff.BridgeAutoAngle;
import com.dimchig.bedwarsbro.stuff.FreezeClutch;
import com.dimchig.bedwarsbro.stuff.HintsBaseRadar;
import com.dimchig.bedwarsbro.stuff.HintsBedScanner;
import com.dimchig.bedwarsbro.stuff.HintsFinder;
import com.dimchig.bedwarsbro.stuff.HintsValidator;
import com.dimchig.bedwarsbro.stuff.LobbyBlockPlacer;
import com.dimchig.bedwarsbro.stuff.NamePlateRenderer;
import com.dimchig.bedwarsbro.stuff.TNTJump;
import com.dimchig.bedwarsbro.stuff.WinEmote;
import com.dimchig.bedwarsbro.stuff.BedwarsMeow.MsgCase;

import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeybindHandler {
	static Minecraft mc;
    KeyBinding keyHintsBedScanner;
    KeyBinding keyHintsFinder;
    KeyBinding keyBridgeautoAngle;
    KeyBinding keyPlayerFocus;
    KeyBinding keyCommandLeave;
    KeyBinding keyCommandRejoin;
    KeyBinding keyCommandLeaveRejoin;
    KeyBinding keyCommandPartyWarp;
    KeyBinding keyRotateBind;
    KeyBinding keyZoomMinimap;
    KeyBinding keyPlaceBlockUnderPlayer;
    KeyBinding keyLobbyFly;
    KeyBinding keyLookAtMyBase;
    KeyBinding keyFreezeCluth;
    KeyBinding keyShowLastLightning;
    KeyBinding keySpawnFakeFireball;
    
    
    
    //built-in
    KeyBinding keyMCAttack;
    KeyBinding keyMCUseItem;
    KeyBinding keyTab;
    
    public static String filename = "BedwarsBro_Keybindings_" + Main.VERSION + ".txt";
    
    public KeybindHandler(Main asInstance) {
    	mc = Minecraft.getMinecraft();
    	//setting keys
    	readKeys();
    }
    
    void readKeys() {
    	String readFile = FileManager.readFile(filename);
		if (readFile == null || readFile.length() < 3) {
			initKeys();
		}
		
		readFile = FileManager.readFile(filename);
		if (readFile == null || readFile.length() < 3) {
			return;
		}
    	
    	String category = ColorCodesManager.replaceColorCodesInString(((char)32) + "&c&lBedwars&f&lBro" + ((char)32));
    	//String categoryEmotes = ColorCodesManager.replaceColorCodesInString(((char)32) + "&c&lBedwars&f&lBro &7→ &aЭмоции" + ((char)32));
    	
    	try {
    		String[] keys = readFile.split(";");
    		int key1 = Integer.parseInt(keys[0]);
    		int key2 = Integer.parseInt(keys[1]);
    		int key3 = Integer.parseInt(keys[2]);
    		int key4 = Integer.parseInt(keys[3]);
    		int key5 = Integer.parseInt(keys[4]);
    		int key6 = Integer.parseInt(keys[5]);
    		int key7 = Integer.parseInt(keys[6]);
    		int key8 = Integer.parseInt(keys[7]);
    		int key9 = Integer.parseInt(keys[8]);
    		int key10 = Integer.parseInt(keys[9]);
    		int key11 = Integer.parseInt(keys[10]);
    		int key12 = Integer.parseInt(keys[11]);
    		int key13 = Integer.parseInt(keys[12]);
    		int key14 = Integer.parseInt(keys[13]);
    		int key15 = Integer.parseInt(keys[14]);

    		//System.out.println("READING KEYS = " + Arrays.toString(keys));
    		
    		
	    	int k = 57344;
	    	keyHintsBedScanner = new KeyBinding(ColorCodesManager.replaceColorCodesInString((      (char)(k + 1)) + "&fСканер &cкровати"), key1, category);	    	
	    	keyHintsFinder = new KeyBinding(ColorCodesManager.replaceColorCodesInString((          (char)(k + 2)) + "&fНайти &bигроков"), key2, category);
	    	keyCommandLeave = new KeyBinding(ColorCodesManager.replaceColorCodesInString((         (char)(k + 3)) + "&fКоманда &c/leave"), key3, category);
	    	keyCommandRejoin = new KeyBinding(ColorCodesManager.replaceColorCodesInString((        (char)(k + 4)) + "&fКоманда &a/rejoin"), key4, category);
	    	keyCommandLeaveRejoin = new KeyBinding(ColorCodesManager.replaceColorCodesInString((   (char)(k + 5)) + "&fКоманда &c/leave &7+ &a/rejoin"), key5, category);
	    	keyCommandPartyWarp = new KeyBinding(ColorCodesManager.replaceColorCodesInString((     (char)(k + 6)) + "&fКоманда &e/party warp"), key6, category);
	    	keyRotateBind = new KeyBinding(ColorCodesManager.replaceColorCodesInString((           (char)(k + 7)) + "&fБинд на &cрозворот"), key7, category);
	    	keyZoomMinimap = new KeyBinding(ColorCodesManager.replaceColorCodesInString((          (char)(k + 8)) + "&bZoom &fминикрты"), key8, category);
	    	keyBridgeautoAngle = new KeyBinding(ColorCodesManager.replaceColorCodesInString((      (char)(k + 9)) + "&fУстановить &6угол для GodBridge"), key9, category);
	    	keyPlaceBlockUnderPlayer = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 10)) + "&fПрыжки по воздуху в &eлобби &7(Не чит)"), key10, category);
	    	keyLobbyFly = new KeyBinding(ColorCodesManager.replaceColorCodesInString((             (char)(k + 11)) + "&aFly &fв лобби"), key11, category);
	    	keyLookAtMyBase = new KeyBinding(ColorCodesManager.replaceColorCodesInString((         (char)(k + 12)) + "&fПосмотреть на &cсвою базу"), key12, category);	    	
	    	keyFreezeCluth = new KeyBinding(ColorCodesManager.replaceColorCodesInString((          (char)(k + 13)) + "&bЗафризить &fигру для клатча"), key13, category);
	    	keyShowLastLightning = new KeyBinding(ColorCodesManager.replaceColorCodesInString((    (char)(k + 14)) + "&bПоказать последнюю &bмолнию"), key14, category);
	    	keySpawnFakeFireball = new KeyBinding(ColorCodesManager.replaceColorCodesInString((    (char)(k + 15)) + "&fЗаспавнить &6фейк-фаербол &7(видишь только ты)"), key15, category);

	        ClientRegistry.registerKeyBinding(this.keyHintsBedScanner);
	        ClientRegistry.registerKeyBinding(this.keyHintsFinder);
	        ClientRegistry.registerKeyBinding(this.keyBridgeautoAngle);
	        ClientRegistry.registerKeyBinding(this.keyCommandLeave);
	        ClientRegistry.registerKeyBinding(this.keyCommandRejoin);
	        ClientRegistry.registerKeyBinding(this.keyCommandLeaveRejoin);
	        ClientRegistry.registerKeyBinding(this.keyCommandPartyWarp);
	        ClientRegistry.registerKeyBinding(this.keyRotateBind);
	        ClientRegistry.registerKeyBinding(this.keyZoomMinimap);
	        ClientRegistry.registerKeyBinding(this.keyPlaceBlockUnderPlayer);
	        ClientRegistry.registerKeyBinding(this.keyLobbyFly);
	        ClientRegistry.registerKeyBinding(this.keyLookAtMyBase);
	        ClientRegistry.registerKeyBinding(this.keyFreezeCluth);
	        ClientRegistry.registerKeyBinding(this.keyShowLastLightning);
	        ClientRegistry.registerKeyBinding(this.keySpawnFakeFireball);

	        keyPlayerFocus = mc.gameSettings.keyBindStreamCommercials;
	        keyMCAttack = mc.gameSettings.keyBindAttack;
	        keyMCUseItem = mc.gameSettings.keyBindUseItem;
	        keyTab = mc.gameSettings.keyBindPlayerList;
	        System.out.println("SUCCESFULLY REGISTERED");
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    void initKeys() {
    	String s = "";
    	s += Keyboard.KEY_R + ";";    	
    	s += Keyboard.KEY_NUMPAD0 + ";";    	
    	s += Keyboard.KEY_NUMPAD7 + ";";
    	s += Keyboard.KEY_NUMPAD8 + ";";
    	s += Keyboard.KEY_J + ";";
    	s += Keyboard.KEY_NUMPAD9 + ";";
    	s += Keyboard.KEY_GRAVE + ";";
    	s += Keyboard.KEY_NONE + ";";
    	s += Keyboard.KEY_B + ";";
    	s += Keyboard.KEY_F + ";";
    	s += Keyboard.KEY_L + ";";
    	s += Keyboard.KEY_K + ";";    	
    	s += Keyboard.KEY_NONE + ";";
    	s += Keyboard.KEY_NONE + ";";
    	s += Keyboard.KEY_NONE + ";";

    	FileManager.writeToFile(s, filename, false);
    }
    
    void saveKeybindings() {
    	try {
	    	String s = "";
	    	s += keyHintsBedScanner.getKeyCode() + ";";	    	
	    	s += keyHintsFinder.getKeyCode() + ";";	    	
	    	s += keyCommandLeave.getKeyCode() + ";";
	    	s += keyCommandRejoin.getKeyCode() + ";";
	    	s += keyCommandLeaveRejoin.getKeyCode() + ";";
	    	s += keyCommandPartyWarp.getKeyCode() + ";";
	    	s += keyRotateBind.getKeyCode() + ";";
	    	s += keyZoomMinimap.getKeyCode() + ";";
	    	s += keyBridgeautoAngle.getKeyCode() + ";";
	    	s += keyPlaceBlockUnderPlayer.getKeyCode() + ";";
	    	s += keyLobbyFly.getKeyCode() + ";";
	    	s += keyLookAtMyBase.getKeyCode() + ";";  	
	    	s += keyFreezeCluth.getKeyCode() + ";";
	    	s += keyShowLastLightning.getKeyCode() + ";";
	    	s += keySpawnFakeFireball.getKeyCode() + ";";
	    	FileManager.writeToFile(s, filename, false);
    	} catch (Exception ex) {}
    }
    
    @SubscribeEvent
    public void changeKeybind(GuiScreenEvent.KeyboardInputEvent.Post event)
    {
    	if(event.gui instanceof GuiControls)
    	{
    		saveKeybindings();
    	}
    }
    
    boolean flagPlaceBlockUnderPlayer = false;
    boolean flagTowerDefence = false;
    
    private ArrayList<Pos> mybeds = new ArrayList<Pos>();
    
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent e) throws Exception {
    	handleKeys();
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) throws Exception {
    	handleKeys();
    }

    public void handleKeys() {
    	if (Minecraft.getMinecraft().thePlayer == null) return;
    	
    	if (keyHintsBedScanner == null) {
    		ChatSender.addText(Main.chatListener.PREFIX_BEDWARSBRO + "&cПроизошла ошибка с файлом... Попытка настройки...");
    		initKeys();
    		readKeys();
    		saveKeybindings();
    		return;
    	}
    	
        if(keyHintsBedScanner.isPressed()) {
        	
        	if (HintsValidator.isBedScannerActive()) HintsBedScanner.scanBed();
    		//note.bass
    		//note.bassattack
    		//note.bd
    		//note.harp
    		//note.hat
    		//note.pling
    		//note.snar            	        	
        }	
        
        if (keyRotateBind.isPressed() && !Main.rotateBind.isActive) {
        	Main.rotateBind.startRotate();
        }

        if (keyHintsFinder.isPressed()) {	
    		if (HintsValidator.isFinderActive()) {
    			ChatSender.addText(MyChatListener.PREFIX_HINT_FINDER + "&fПоиск игроков...");
    			HintsFinder.findAll(true);
    		}
        }
        
        if (keyBridgeautoAngle.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) BridgeAutoAngle.aim();
        }
        
        if (keyPlayerFocus.isPressed() && keyPlayerFocus.getKeyCode() == Keyboard.KEY_W) {
        	if (HintsValidator.isPasswordCorrect()) {
        		Main.playerFocus.STATE = !Main.playerFocus.STATE;
        		keyPlayerFocus.setKeyCode(Keyboard.CHAR_NONE);
        		Minecraft.getMinecraft().gameSettings.keyBindForward.setKeyCode(Keyboard.KEY_W);
        		Minecraft.getMinecraft().gameSettings.saveOptions();
        	}
        }
        
        if (keyCommandLeaveRejoin.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) {
        		ChatSender.sendText("/leave");
        		Main.myTickEvent.zeroDeathHandlerRejoinVar = 10;
        	}
        }
        
        if (keyCommandLeave.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) ChatSender.sendText("/leave");
        }
        
        if (keyCommandRejoin.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) ChatSender.sendText("/rejoin");
        }
        
        if (keyCommandPartyWarp.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) ChatSender.sendText("/party warp");
        }
        
        if (keyZoomMinimap.isPressed()) {
        	Main.minimap.handleZoom();
        }
        
        if (keyTab.isPressed()) {
        	Main.minimap.showNicknames = true;
        	
        	Main.namePlateRenderer.friends = Main.fileNicknamesManager.readNames(Main.commandFriends.filename); //updater
        } else if (!keyTab.isKeyDown()) {
        	Main.minimap.showNicknames = false;
        }
        
        
        if (keyLobbyFly.isPressed()) {
        	Main.lobbyFly.isActive = true;
        } else if (!keyLobbyFly.isKeyDown()) {
        	Main.lobbyFly.isActive = false;
        }
        
        if (keyLookAtMyBase.isPressed()) {
        	Main.playerFocus.isLookAtBaseActive = true;
        	if (Main.chatListener.GAME_BED == null) ChatSender.addText("&cНет кровати 0_o");
        } else if (!keyLobbyFly.isKeyDown()) {
        	Main.playerFocus.isLookAtBaseActive = false;
        }
        
        if (keyFreezeCluth.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) Main.freezeClutch.startFreeze();
        }
        
        if (keyShowLastLightning.isPressed()) {
        	Main.lightningLocator.isActive = true;
        } else if (!keyShowLastLightning.isKeyDown()) {
        	Main.lightningLocator.isActive = false;
        }

        if (keyPlaceBlockUnderPlayer.isPressed()) {
        	if (flagPlaceBlockUnderPlayer == false) {
	        	flagPlaceBlockUnderPlayer = true;
	        	LobbyBlockPlacer.state = !LobbyBlockPlacer.state;
        	}
        } else if (flagPlaceBlockUnderPlayer == true) {
        	flagPlaceBlockUnderPlayer = false;
        }
        
        if (keySpawnFakeFireball.isKeyDown()) {
        	spawnFakeFireball();
        }
    }
    
    private void spawnFakeFireball() {
    	double posX = mc.thePlayer.posX;
        double posY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double posZ = mc.thePlayer.posZ;  
        double d1 = 1000D;
        Vec3 vec3 = mc.thePlayer.getLook(1.0F);
        double d2 = posX;
        double d3 = mc.thePlayer.height;
        double d4 = posZ;
        EntityLargeFireball entitylargefireball = new EntityLargeFireball(mc.theWorld, mc.thePlayer, vec3.xCoord * d1, vec3.yCoord * d1, vec3.zCoord * d1);
        entitylargefireball.explosionPower = 0;
        entitylargefireball.setCustomNameTag("fake_fireball");
        entitylargefireball.posX = mc.thePlayer.posX;
        entitylargefireball.posY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        entitylargefireball.posZ = mc.thePlayer.posZ;
        mc.theWorld.spawnEntityInWorld(entitylargefireball);
    }
    
    
    
}
