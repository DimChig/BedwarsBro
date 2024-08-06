package com.dimchig.bedwarsbro.serializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class MySerializer {
	
    //public static String url_origin = "https://t.me/bedwarsbro_minecraft/2"; //круто, да?  v2.2 and less 
    public static String url_origin = "https://t.me/bedwarsbro_minecraft/4?embed=1"; //круто, да?
    public static String url_messages = "https://t.me/bedwarsbro_minecraft/3?embed=1"; //можешь распарсить, мне пох)
    

    public static String separator = ";==BWBRO==;";
    public static String separator_secondary = ";=BRO=;";
        
    public static String readProps() {
    	return extractMyData(readContentByUrl(url_origin));
    }
    
    public static String readMessages() {
    	return extractMyData(readContentByUrl(url_messages));
    }
    
    public static String extractMyData(String content) {		    		
		String tag_start = "BWBROTAGSTART";
		String tag_end = "BWBROTAGEND";
		
		try {	
			if (content == null || content.length() < 10 || !content.contains(tag_start) || !content.contains(tag_end)) return null;
			
			String data_text = content.split(tag_end)[0].trim();
			
			if (!data_text.contains(tag_start)) return null;
			String[] splt = data_text.split(tag_start);
			
			if (splt.length == 0) return null;
			data_text = URLDecoder.decode(splt[splt.length - 1], "UTF-8");
			return data_text;
		} catch (Exception e) {
			return null;
		}
    }
    
    public static String readContentByUrl(String url) {
    	String content;
		try {				
			content = getText(url);	
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}    
		return null;
    }
    
  
    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}