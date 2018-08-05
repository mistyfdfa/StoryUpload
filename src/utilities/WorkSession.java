package utilities;

import java.io.File;
import java.util.LinkedList;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import core.Story;

public interface WorkSession {
	
	ChromeDriver cd  = new ChromeDriver(
			Functions.createService(),
			Functions.addChromeOptions());;
	WebDriverWait wait = new WebDriverWait(cd, 5);
	
	public void logAndNav();
	public boolean searchForPost(String csName);
	public File fromSite(String csName);
	public String dlRawStory();
	public void postRawStory(Story cs);
	//TODO public void copyFormattedStory();
	//TODO public void toSite(String csName, String rawtxt);
	//TODO public void setTags(String csName, String[] tagNames);
	public void end();

	
}
