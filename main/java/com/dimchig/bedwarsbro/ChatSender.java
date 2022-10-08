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
	public static void addText(String text) {
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().thePlayer == null) return;
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
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hover_text));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwarsChatModLookAtPlayer");
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addHoverFileText(String text, String hover_text, String filepath) throws IOException {
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hover_text));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, filepath);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickText(String text, String commandText) {
		IChatComponent mainComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickAndHoverText(String text, String hoverText, String commandText) {
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addLinkAndHoverText(String text, String hoverText, String url) {
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
	
	public static void addClickSuggestAndHoverText(String text, String hoverText, String commandText) {
		IChatComponent mainComponent  = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(text));
		IChatComponent hoverComponent = new ChatComponentText(ColorCodesManager.replaceColorCodesInString(hoverText));
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);
		ClickEvent click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText);
		mainComponent.getChatStyle().setChatHoverEvent(hover);
		mainComponent.getChatStyle().setChatClickEvent(click);
		Minecraft.getMinecraft().thePlayer.addChatMessage(mainComponent);
	}
}
