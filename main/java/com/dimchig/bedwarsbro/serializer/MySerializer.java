package com.dimchig.bedwarsbro.serializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.ColorCodesManager;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class MySerializer {
	
    public static String url_origin = "https://t.me/bedwarsbro_minecraft/2"; //круто, да?

    public static String separator = ";==BWBRO==;";
    public static String separator_secondary = ";=BRO=;";
        
    public static String readProps() {
    	String content;
		try {				
			content = getText(url_origin);	
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}    
		return null;
    }
    
  
    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}