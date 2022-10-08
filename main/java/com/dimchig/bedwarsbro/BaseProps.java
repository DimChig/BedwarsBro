package com.dimchig.bedwarsbro;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.serializer.MySerializer;

public class BaseProps {
	private String mod_last_version; 
	private String mod_update_link;
	private String discord_link;
	private String mod_autor;
	private ArrayList<String> banned_users;
	private ArrayList<String> admin_users;
	
	public BaseProps() {
		this.mod_last_version = null; 
		this.mod_update_link = null; 
		this.discord_link = null; 
		this.mod_autor = null; 
		this.banned_users = new ArrayList<String>(); 
		this.admin_users = new ArrayList<String>(); 
	}
	
	public String getModLastVersion() { return this.mod_last_version; }
	public String getModUpdateLink() { return this.mod_update_link; }
	public String getDiscordLink() { return this.discord_link; }
	public String getModAuthor() { return this.mod_autor; }
	public ArrayList<String> getModBannedUsers() { return this.banned_users; }
	public boolean isUserBanned(String player_name) {
		for (String n: this.banned_users) if (n.equals(player_name)) return true;
		return false;
	}
	public ArrayList<String> getModAdminUsers() { return this.admin_users; }
	public boolean isUserAdmin(String player_name) {
		for (String n: this.admin_users) if (n.equals(player_name)) return true;
		return false;
	}

	public void readProps() {
    	Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	try {
		    		String s = Main.mySerializer.readProps();
		    		if (s == null) return;
		    		if (s.length() < 100 || !s.contains("BWBROTAGSTART")) return;
		    		
		    		String data_text = s.split("BWBROTAGSTART")[1].split("BWBROTAGEND")[0].trim();
		    		data_text = data_text.replaceAll("\\<[^>]*>","").replace("<br />", "").trim();
		    		String[] split = data_text.split(MySerializer.separator);
		    		if (split.length != 6) return;
		    		
		    		mod_last_version = split[0].trim();
		    		mod_update_link = split[1].trim();
		    		discord_link = split[2].trim();
		    		mod_autor = split[3].trim();
		    		
		    		admin_users.clear();
		    		
		    		String[] mod_admin_users = split[4].trim().split(Main.mySerializer.separator_secondary);	
		    		for (String l: mod_admin_users) {
		    			if (l.length() <= 1) continue;
		    			admin_users.add(l);
		    		}
		    		
		    		banned_users.clear();
		    		
		    		String[] mod_banned_users = split[5].trim().split(Main.mySerializer.separator_secondary);	
		    		for (String l: mod_banned_users) {
		    			if (l.length() <= 1) continue;
		    			banned_users.add(l);
		    		}
		    		
		    		Main.updateAllBooleans();
		    		
				} catch (Exception e) {
					ChatSender.addText("&cEXCEPTION TREAD" + e);
					e.printStackTrace();
					
				} 
		    }
		}); 
    	t1.start();	
	}
}
