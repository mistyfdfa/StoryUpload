package utilities;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import core.Story;

public class WPWorkSession implements WorkSession {
	
	String home = "https://wordpress.com";
	String[] tags = {"<",">","</"};
	
	public WPWorkSession(){
		cd.get(home);
		logAndNav();
	}
	
	public boolean searchForPost(String num){
		if(Functions.clickElement(cd, By.partialLinkText("Blog Posts")))
			if(Functions.clickElement(cd,By.partialLinkText(num)))
				return true;
			else {
				int attempts = 0;
				do {
					((JavascriptExecutor) cd).executeScript("window.scrollBy(0,document.body.scrollHeight/2)");
					try{
						Thread.sleep(500);
					}catch (Exception ignored){}
					if(Functions.clickElement(cd, By.partialLinkText(num)))
						return true;
				} while(attempts++<5);
			}
		return false;
	}
	
	public File fromSite(String csName) {
		String num = csName.split("-")[0];
		if(searchForPost(num))
			return Functions.openFileAndWriteLines(".\\StoryFiles\\"+csName+".txt",dlRawStory());
		Functions.exitGracefully(
			new Exception("There was no story with that name...s"), this);
		return null;
	}
	
	public String dlRawStory(){
		try{
			Thread.sleep(500);
		}catch (Exception ignored){}
		try {
			By by = By.xpath("//button[@text='Don't restore']");
			cd.findElement(by);
			Functions.clickElement(cd, by);
		} catch (WebDriverException ex) {
			System.err.println("Didn't need to close that pop-up");
		}
		By by = By.partialLinkText("HTML");
		if(!Functions.clickElement(cd, by)) {
			by = By.xpath("//li/a/span");
			Functions.clickElement(cd, by);
		}
		WebElement temp = cd.findElement(By.id("tinymce-1"));
		String str = temp.getText();
		if (!(str.length()>0)){
			Functions.exitGracefully(
				new Exception("Story text was empty"), this);
		}
		Functions.clickElement(cd, By.xpath("//div/button[@aria-label='Close']"));
		return str;
	}
	
	public void postRawStory(Story cs){
		By by = By.xpath("//li[@data-post-type='post']");
		WebElement temp = null;
		temp = cd.findElement(by).findElement(By.partialLinkText("Add"));
		temp.click();
		try{ Thread.sleep(500); } catch (Exception ignored){}
		by = By.tagName("textarea");
		cd.findElement(by).sendKeys(
			"#" + cs.num + " - " + cs.nameWithSpaces());		
		clickCategories(By.className("editor-categories-tags__accordion"), cs.rating);
		by = By.partialLinkText("HTML");
		if(!Functions.clickElement(cd, by)) {
			by = By.xpath("//li/a/span");
			Functions.clickElement(cd, by);
		}
		temp = cd.findElement(By.id("tinymce-1"));
		try{
		((JavascriptExecutor)cd)
			.executeScript(
			"arguments[0].value = arguments[1];", temp, cs.toString());
			try{ Thread.sleep(750); } catch (Exception ignored){}
		}catch (Exception ex){
			System.err.println("Something happened with the jScript");
		}
		try{ Thread.sleep(750); } catch (Exception ignored){}
		Functions.clickElement(cd, By.xpath("//div/button[@aria-label='Close']"));
	}
	
	public void clickCategories(By topLevel, String elem) {
		Functions.clickElement(cd, topLevel);
		try{ Thread.sleep(750); } catch (Exception ignored){}
		By by = By.className("term-tree-selector__label");
		List<WebElement> categories = cd.findElements(by);
		Iterator<WebElement> itr = categories.iterator();
		while(itr.hasNext()){
			WebElement temp = itr.next();
			if(elem.contains(temp.getText()))
				temp.findElement(By.xpath("..")).click();
		}
	}
	
	
	public void logAndNav() {
		if(Functions.clickElement(cd, By.partialLinkText("Log In"))) {
			WebElement temp = cd.findElement(By.id("usernameOrEmail"));
			temp.sendKeys("mstfdfa@gmail.com");
			temp.sendKeys(Keys.ENTER);
			wait.until(	
					ExpectedConditions.visibilityOfElementLocated(By.id("password")));
			temp = cd.findElement(By.id("password"));
			temp.sendKeys("M@nyw0r)$?");
			temp.sendKeys(Keys.ENTER);
		}
		try {
			wait.until(	
				ExpectedConditions.visibilityOfElementLocated(
					By.partialLinkText("My Sites")));
			if(Functions.clickElement(cd, By.partialLinkText("My Sites")))
				if(Functions.clickElement(cd, By.cssSelector(".current-site__switch-sites-label")))
					Functions.clickElement(cd, By.cssSelector(".is-jetpack"));
		} catch (TimeoutException | NoSuchElementException ex) {
			cd.navigate().refresh();
			wait.until(	
					ExpectedConditions.visibilityOfElementLocated(
						By.partialLinkText("My Sites")));
				if(Functions.clickElement(cd, By.partialLinkText("My Sites")))
					if(Functions.clickElement(cd, By.cssSelector(".current-site__switch-sites-label")))
						if(Functions.clickElement(cd, By.cssSelector(".is-jetpack")))
							wait.until(	
								ExpectedConditions
								.visibilityOfElementLocated(By.partialLinkText("Blog Posts")));
		}
	}

	@Override
	public void end() {
		cd.close();
		cd.quit();
	}
}
