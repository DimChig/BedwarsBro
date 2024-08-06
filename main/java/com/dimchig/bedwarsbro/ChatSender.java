package com.dimchig.bedwarsbro;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatSender {
	

	public static String parseText(String text) {
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().thePlayer.getName() == null) return text;
		String nick = Main.chatListener.nickChanger;
		if (nick.length() > 0) {
			text = text.replaceAll(Minecraft.getMinecraft().thePlayer.getName(), nick);
		}
		
		String nickSpoof_name = Main.chatListener.nickSpoof_name;
		String nickSpoof_new_name = ColorCodesManager.replaceColorCodesInString(Main.chatListener.nickSpoof_new_name);
		if (nickSpoof_name.length() > 0 && nickSpoof_new_name.length() > 0) {
			text = text.replaceAll(nickSpoof_name, nickSpoof_new_name);
		}
		
		return text;
	}
	
	public static void addText(String text) {		
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().thePlayer == null) return;
		text = parseText(text);
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text)));
	}
	public static void addText(boolean b) { addText((b ? "&a" : "&c") + b); }
	public static void addText(int x) { addText("" + x); }
	public static void addText(double x) { addText("" + x); }
	public static void addText(float x) { addText("" + x); }
	public static <T> void addText(T[] x) { addText("" + x); }
	public static <T> void addText(List<T> x) { addText("" + x); }
	
	public static void sendText(String text) {
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().thePlayer == null) return;
		Minecraft.getMinecraft().thePlayer.sendChatMessage(text.replace("§", "&"));
	}
	
	public static void addHoverText(String text, String hover_text) {
		text = parseText(text);
		hover_text = parseText(hover_text);
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hover_text));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwarsChatModLookAtPlayer");
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addHoverFileText(String text, String hover_text, String filepath) throws IOException {
		text = parseText(text);
		hover_text = parseText(hover_text);
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hover_text));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, filepath);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickText(String text, String commandText) {
		text = parseText(text);
		IChatComponent mainComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickAndHoverText(String text, String hoverText, String commandText) {
		text = parseText(text);
		hoverText = parseText(hoverText);
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addLinkAndHoverText(String text, String hoverText, String url) {
		text = parseText(text);
		hoverText = parseText(hoverText);
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickSuggestAndHoverText(String text, String hoverText, String commandText) {
		text = parseText(text);
		hoverText = parseText(hoverText);
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
}
