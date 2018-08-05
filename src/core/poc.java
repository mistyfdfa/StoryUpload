package core;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.Functions;
import utilities.Html2ePub;
import utilities.WPWorkSession;
import utilities.WorkSession;

public class poc {
	static WorkSession ws;
	
	public static void main(String args[]){
		try {
			if(args.length==1)
				processStoryList(args[0]);
			else
				for(int i=0;i<args.length;i++)
					processStoryFile(new Story(args[i]));
		} catch (Exception ex) {
			if(ws!=null)
				ws.end();
			Functions.exitGracefully(ex);
		}
	}
	
	public static void processStoryList(String filename){
		LinkedList<String> storyNames = 
			Functions.openFileAndReadLines(filename);
		LinkedList<Story> stories = new LinkedList<Story>();
		Iterator<String> itr = storyNames.iterator();
		String site = itr.next();
		while(itr.hasNext()){
			String csName = itr.next();
			File temp = new File(".\\StoryFiles\\"+csName+".txt");
			if(!temp.exists()){
				if(ws == null)
					ws = launchSession(site);
				stories.add(new Story(ws.fromSite(csName)));
			}
		}
		Iterator<Story> itr2 = stories.iterator();
		while(itr.hasNext()){
			Html2ePub h2e = new Html2ePub(itr2.next());
			h2e.createEpubZip();
		}
	}
		
	public static void processStoryFile(Story curr){
		System.out.println("current story: "+ curr.name);
	}
	
	public static WorkSession launchSession(String site){
		switch(site){
		case "wp":
			return new WPWorkSession();
		}
		return null;
	}
}

