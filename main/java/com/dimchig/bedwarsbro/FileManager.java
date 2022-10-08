package com.dimchig.bedwarsbro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {
	public FileManager() {
		//initFile(filename);
	}
	
	public static void initFile(String name) {
		try {
		      File myObj = new File(name);
		      myObj.createNewFile();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	public static void clearFile(String filename) {
		writeToFile(filename, "", false);
	}
	
	public static void writeToFile(String str, String name, boolean append) {
		initFile(name);
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name, append), "UTF-8"));
		    out.write((append ? "\n" : "") + str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String readFile(String filename) {
		initFile(filename);
		try {
			List<String> list = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
			StringBuilder builder = new StringBuilder();
			for (String s: list) builder.append(s + "\n");
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}