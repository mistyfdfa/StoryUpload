package core;

import java.io.File;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import utilities.Functions;

public class Story {
	public LinkedList<String> lines;
	Iterator<String> itr;
	boolean indent = false;
	public String fullname, num, name, setting, linkSetting,
				teaser, rating,	tags, ads, more;
	public int wordCount;
	LinkedList<String> header;
	
	public Story(String fileName){
		this.fullname = fileName;
		num = fullname.split("-")[0];
		name = fullname.split("-")[1];
		lines = Functions.openFileAndReadLines(new File(".\\StoryFiles\\"+fullname+".txt"));
		createHeader();
	}
	
	public Story(File storyFile){
		this.fullname = storyFile.getName();
		num = fullname.split("-")[0];
		name = fullname.split("-")[1];
		name = name.split("\\.")[0];
		lines = Functions.openFileAndReadLines(storyFile);
		createHeader();
	}
	
	public void createHeader(){
		header = new LinkedList<String>();
		itr = lines.iterator();
		String line = "";
		while(!line.contains("<!--more-->")){
			line = lines.pop();
			header.add(line);
		}
		setting = hrefAway(header.get(SETTING));
		linkSetting = header.get(SETTING);
		teaser = header.get(TEASER);
		rating = header.get(RATING);
		tags = header.get(TAGS);
		ads = header.get(ADS);
		
		wordCount = new StringTokenizer(this.toString()).countTokens();
	}
	
	public void updateHeader(int index, String element) {
		header.set(index, element);
	}
	
	private String hrefAway(String string) {
		StringBuilder sb = new StringBuilder(string);
		int hrefs = sb.indexOf("</");
		int hrefe = sb.lastIndexOf("a>");
		sb.delete(hrefs, hrefe+2);
		hrefs = sb.indexOf("<a");
		hrefe = sb.indexOf("\">");
		sb.delete(hrefs, hrefe+2);	
		
		@SuppressWarnings("unused")
		String temp = sb.toString();
		return sb.toString();
	}

	public String nameNoSpaces(){
		return name.replaceAll("_", "");
	}
	
	public String nameWithSpaces(){
		return name.replaceAll("_", " ");
	}
	
	public String toString(){
		itr = lines.iterator();
		String temp="";
		while(itr.hasNext())
			if(temp.length()==0)
				temp=itr.next();
			else
				temp+="\n" + itr.next();
		return temp;
	}

	public String bookID() {
		return num+"_"+LocalDate.now();
	}
	
	public static int SETTING=0;
	public static int TEASER=1;
	public static int RATING=2;
	public static int TAGS=3;
	public static int ADS=4;
	public static int MORE=5;
}
