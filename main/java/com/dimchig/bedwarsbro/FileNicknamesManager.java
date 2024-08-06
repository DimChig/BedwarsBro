package com.dimchig.bedwarsbro;

import java.util.ArrayList;

public class FileNicknamesManager {
	
	public FileNicknamesManager() {}
	
	public ArrayList<String> readNames(String filename) {
		ArrayList<String> arr = new ArrayList<String>();
		try {
			String s = FileManager.readFile(filename);
			if (s == null || s.length() == 0) {
				FileManager.writeToFile("", filename, false);
				return arr;
			}
			String[] split = s.split("\n");
			if (split.length == 0) return arr;
			for (String name: split) {
				name = name.trim();
				if (name.length() == 0 || arr.contains(name)) continue;
				arr.add(name);
			}
			return arr;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
	
	public void writeNames(String filename, ArrayList<String> names) {
		String s = "";
		for (String name: names) {
			s += name + "\n";
		}
		FileManager.writeToFile(s, filename, false);
	}
	
	public boolean addName(String filename, String name) {
		ArrayList<String> arr = readNames(filename);
		if (arr.contains(name)) return false;
		arr.add(name);
		writeNames(filename, arr);
		return true;
	}
	
	public boolean removeName(String filename, String name) {
		ArrayList<String> arr = readNames(filename);
		if (!arr.contains(name)) return false;
		arr.remove(name);
		writeNames(filename, arr);
		return true;
	}
}
