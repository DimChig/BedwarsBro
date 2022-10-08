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
import com.dimchig.bedwarsbro.hints.BridgeAutoAngle;
import com.dimchig.bedwarsbro.hints.FreezeClutch;
import com.dimchig.bedwarsbro.hints.HintsBaseRadar;
import com.dimchig.bedwarsbro.hints.HintsBedScanner;
import com.dimchig.bedwarsbro.hints.HintsFinder;
import com.dimchig.bedwarsbro.hints.HintsValidator;
import com.dimchig.bedwarsbro.hints.LobbyBlockPlacer;
import com.dimchig.bedwarsbro.hints.TNTJump;
import com.dimchig.bedwarsbro.hints.WinEmote;
import com.dimchig.bedwarsbro.hints.BedwarsMeow;
import com.dimchig.bedwarsbro.hints.BedwarsMeow.MsgCase;
import com.dimchig.bedwarsbro.particles.ParticleController;
import com.dimchig.bedwarsbro.particles.ParticleFinalKillEffect;
import com.dimchig.bedwarsbro.serializer.MySerializer;

import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
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
    KeyBinding keyTNTJump;
    KeyBinding keyPlayerFocus;
    KeyBinding keyCommandLeave;
    KeyBinding keyCommandRejoin;
    KeyBinding keyCommandPartyWarp;
    KeyBinding keyZoomMinimap;
    KeyBinding keyPlaceBlockUnderPlayer;
    KeyBinding keyLobbyFly;
    KeyBinding keyFreezeCluth;
    KeyBinding keyShowLastLightning;
    
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
    	String categoryEmotes = ColorCodesManager.replaceColorCodesInString(((char)32) + "&c&lBedwars&f&lBro &7→ &aЭмоции" + ((char)32));
    	
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

    		//System.out.println("READING KEYS = " + Arrays.toString(keys));
    		
    		
	    	int k = 57344;
	    	keyHintsBedScanner = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 1)) + "&fСканер &cкровати"), key1, category);	    	
	    	keyHintsFinder = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 2)) + "&fНайти &eигроков"), key2, category);
	    	keyCommandLeave = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 3)) + "&fКоманда &9/leave"), key3, category);
	    	keyCommandRejoin = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 4)) + "&fКоманда &d/rejoin"), key4, category);
	    	keyCommandPartyWarp = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 5)) + "&fКоманда &c/party warp"), key5, category);
	    	keyZoomMinimap = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 6)) + "&eZoom &fминикрты"), key6, category);
	    	keyBridgeautoAngle = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 7)) + "&fУстановить &6угол для GodBridge"), key7, category);
	    	keyPlaceBlockUnderPlayer = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 8)) + "&fПрыжки по воздуху в &eлобби &7(Не чит)"), key8, category);
	    	keyLobbyFly = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 9)) + "&fFly в &eлобби"), key9, category);
	    	keyTNTJump = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 10)) + "&fАвто &cT&4N&cT &fПрыжок"), key10, category);
	    	keyFreezeCluth = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 11)) + "&bЗафризить &fигру для клатча"), key11, category);
	    	keyShowLastLightning = new KeyBinding(ColorCodesManager.replaceColorCodesInString(((char)(k + 12)) + "&bПоказать последнюю &bмолнию"), key12, category);

	        ClientRegistry.registerKeyBinding(this.keyHintsBedScanner);
	        ClientRegistry.registerKeyBinding(this.keyHintsFinder);
	        ClientRegistry.registerKeyBinding(this.keyBridgeautoAngle);
	        ClientRegistry.registerKeyBinding(this.keyTNTJump);
	        ClientRegistry.registerKeyBinding(this.keyCommandLeave);
	        ClientRegistry.registerKeyBinding(this.keyCommandRejoin);
	        ClientRegistry.registerKeyBinding(this.keyCommandPartyWarp);
	        ClientRegistry.registerKeyBinding(this.keyZoomMinimap);
	        ClientRegistry.registerKeyBinding(this.keyPlaceBlockUnderPlayer);
	        ClientRegistry.registerKeyBinding(this.keyLobbyFly);
	        ClientRegistry.registerKeyBinding(this.keyFreezeCluth);
	        ClientRegistry.registerKeyBinding(this.keyShowLastLightning);

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
    	s += Keyboard.KEY_NUMPAD9 + ";";
    	s += Keyboard.KEY_NONE + ";";
    	s += Keyboard.KEY_B + ";";
    	s += Keyboard.KEY_F + ";";
    	s += Keyboard.KEY_L + ";";
    	s += Keyboard.KEY_J + ";";
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
	    	s += keyCommandPartyWarp.getKeyCode() + ";";
	    	s += keyZoomMinimap.getKeyCode() + ";";
	    	s += keyBridgeautoAngle.getKeyCode() + ";";
	    	s += keyPlaceBlockUnderPlayer.getKeyCode() + ";";
	    	s += keyLobbyFly.getKeyCode() + ";";
	    	s += keyTNTJump.getKeyCode() + ";";
	    	s += keyFreezeCluth.getKeyCode() + ";";
	    	s += keyShowLastLightning.getKeyCode() + ";";
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
    public void onKeyInput(InputEvent.KeyInputEvent e) throws Exception {
    	if (Minecraft.getMinecraft().thePlayer == null) return;
    	
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
        
        if (keyTNTJump.isPressed()) {
        	if (HintsValidator.isPasswordCorrect()) {
        		ChatSender.addText(MyChatListener.PREFIX_TNT_JUMP + "&6Не трогай клавиатуру и мышку...");
        		TNTJump.lookAtNearestTNT();
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
        } else if (!keyTab.isKeyDown()) {
        	Main.minimap.showNicknames = false;
        }
        
        
        if (keyLobbyFly.isPressed()) {
        	Main.lobbyFly.isActive = true;
        } else if (!keyLobbyFly.isKeyDown()) {
        	Main.lobbyFly.isActive = false;
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
    }
    
    
    
    
    
}
