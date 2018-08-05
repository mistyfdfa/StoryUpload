package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.Functions;

public class Functions {
	
	public static void exitGracefully(Exception ex) {
		System.out.println(methodName(ex) + " " + ex.getMessage());
		System.exit(1);
	}
	
	public static void exitGracefully(Exception ex, WorkSession ws) {
		ws.end();
		exitGracefully(ex);
		
	}
	
	public static String methodName(Throwable th){
		StackTraceElement stackTraceElements[] = th.getStackTrace();
		return stackTraceElements[0].getMethodName();
	}
	
	public static LinkedList<String> openFileAndReadLines(String filename) {
		try {
			return openFileAndReadLines(new File(filename));
		} catch (Exception ex) {
			exitGracefully(ex);
			return null;
		}
	}
	
	public static LinkedList<String> openFileAndReadLines(File details) {
		BufferedReader bfr = null;
		try {
			do 
				try{
					bfr = new BufferedReader(new FileReader(details));
				} catch (FileNotFoundException fnfe){
					exitGracefully(fnfe);
					continue;
				}
			while (bfr.equals(null));
			
			LinkedList<String> lines = new LinkedList<String>();
			String str;	
		
			while((str = bfr.readLine()) != null)
				if(str.length()>0)
					lines.add(str);
			bfr.close();
			return lines;
		} catch (IOException ioe1) {
			exitGracefully(ioe1);
			try {
				bfr.close();
			} catch (IOException ioe2) {
				exitGracefully(ioe2);
			}
		}
		return null;
	}
	
	public static File openFileAndWriteLines(
			String argsFilename, LinkedList<String> cmdArgs) {
		
		File argsFile = null;
		try{
			try {
				argsFile = new File(argsFilename);
			}  catch (NullPointerException npe) {
				argsFile.createNewFile();
			}
			
			try {
				final BufferedWriter bfw =
					new BufferedWriter(new FileWriter(argsFile));
			
				cmdArgs.forEach((temp) -> {
					try {
						bfw.write(temp);
						bfw.newLine();
						} catch (IOException ioe1){
							Functions.exitGracefully(ioe1);
							System.exit(1);
						}
					});
				bfw.close();
				return argsFile;
			} catch(IOException ioe2) {
				exitGracefully(ioe2);
				return null;
			}
		} catch (IOException ioe1) {
			exitGracefully(ioe1);
			return null;
		}
	}
	
	public static File copyFile(
			File argFile, File destFile){
		return openFileAndWriteLines(destFile, openFileAndReadLines(argFile));
	}
	
	public static File openFileAndWriteLines(
			File destFile, LinkedList<String> lines) {
		try {
			final BufferedWriter bfw =
				new BufferedWriter(new FileWriter(destFile));
		
		lines.forEach((temp) -> {
			try {
				bfw.write(temp);
				bfw.newLine();
				} catch (IOException ioe1){
					Functions.exitGracefully(ioe1);
					System.exit(1);
				}
			});
			bfw.close();
			return destFile;
		} catch(IOException ioe2) {
			Functions.exitGracefully(ioe2);
			return null;
		}
	}

	public static File openFileAndWriteLines(String argsFilename, String text) {
		File argsFile = null;
		try{
			try {
				argsFile = new File(argsFilename);
			}  catch (NullPointerException npe) {
				argsFile.createNewFile();
			}
			
			try {
				final BufferedWriter bfw =
					new BufferedWriter(new FileWriter(argsFile));
				bfw.write(text);
				bfw.newLine();
				bfw.close();
				return argsFile;
			} catch(IOException ioe2) {
				exitGracefully(ioe2);
				return null;
			}
		} catch (IOException ioe1) {
			exitGracefully(ioe1);
			return null;
		}
	}
	
	public static boolean clickElement(ChromeDriver cd, By by){
		WebDriverWait wait = new WebDriverWait(cd, 5);
		try {
			wait.until(	
				ExpectedConditions.presenceOfElementLocated(by));
			cd.findElement(by).click();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public static void scrollTo(WebElement element, ChromeDriver sd){
		((JavascriptExecutor) sd).executeScript(
				"arguments[0].scrollIntoView();", element);
		element.click();
	}
	
	public static ChromeDriverService createService() {
		String pathname = Paths.get("")
				.toAbsolutePath()
	        	.toString() 
	        	+ "\\chromedriver_237.exe";
	    System.setProperty("webdriver.chrome.driver", pathname);		
		ChromeDriverService service = new ChromeDriverService.Builder()
			.usingDriverExecutable(new File(pathname))
			.usingAnyFreePort()
			.build();
		return service;
	}
		
	public static ChromeOptions addChromeOptions(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-extensions");
		options.addArguments("start-maximized");
		options.addArguments("disable-infobars"); 
		options.setExperimentalOption("useAutomationExtension", false); 
		return options;
	}
}	