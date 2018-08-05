package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import core.Story;

public class Html2ePub {
	private Story ts;
	String[] tags = { "<", ">", "</" };

	public Html2ePub(Story htmlStory) {
		ts = htmlStory;
	}

	public void createEpubZip(){
		copyFiles();
		packFileTree();
	}
	
	public void copyFiles() {
		String[] args = {
				".\\Templates\\content.opf",
				".\\Templates\\toc.ncx",
				".\\Templates\\Title.xhtml"
		};
		String[] dests = {
				".\\ePubFolder\\OEBPS\\content.opf",
				".\\ePubFolder\\OEBPS\\toc.ncx",
				".\\ePubFolder\\OEBPS\\Text\\Title.xhtml"
		};
		if (!ts.lines.getFirst().contains("<p>"))
			formatFor(ts.lines);
		Functions.openFileAndWriteLines(".\\ePubFolder\\OEBPS\\Text\\Story.xhtml", ts.lines);
		File arg=null, dest=null;
		for(int i =0;i<dests.length;i++) {
			try {
				arg = new File(args[i]);
			}  catch (NullPointerException npe) {
				Functions.exitGracefully(new Exception(
					"Requested Template is missing!"));
			}
			try {
				dest = new File(dests[i]);
			}  catch (NullPointerException npe) {
				try {
					dest.createNewFile();
				} catch (IOException e) {
					Functions.exitGracefully(e);
				}
			}
			Functions.copyFile(arg, dest);
			updateMeta(dests[i]);
		}
		//place titleText
	}

	public void updateMeta(String dest) {
		findReplaceMeta(dest, "\\$title", ts.nameWithSpaces());
		findReplaceMeta(dest, "\\$setting", ts.setting);
		findReplaceMeta(dest, "\\$teaser", ts.teaser);
		findReplaceMeta(dest, "\\$number", ts.num);
		findReplaceMeta(dest, "\\$bookId", ts.bookID());
		findReplaceMeta(dest, "\\$rating", ts.rating);
		findReplaceMeta(dest, "\\$tags", ts.tags);
		findReplaceMeta(dest, "\\$postedDate", ""+LocalDate.now());
		findReplaceMeta(dest, "\\$wordCount", String.valueOf(ts.wordCount));
	}
		

	public void findReplaceMeta(String fileName, String field, String data) {
		Path path = Paths.get(fileName);
		Charset charset = StandardCharsets.UTF_8;

		try {
			String content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll(field, data);
			Files.write(path, content.getBytes(charset));
		} catch (IOException ioe) {
			Functions.exitGracefully(ioe);
		}
	}

	public void packFileTree() {
		String dirPath = ".\\ePubFolder";
		Path sourceDir = Paths.get(dirPath);
		ZipOutputStream zos;

		try {
			String zipFileName = ts.name + ".zip";
			zos = new ZipOutputStream(new FileOutputStream(zipFileName));

			Files.walkFileTree(sourceDir, new ZipDir(sourceDir,zos));

			zos.close();
		} catch (IOException ex) {
			System.err.println("I/O Error: " + ex);
		}
	}

	public void formatFor(LinkedList<String> lines) {
		Boolean opened = false;
		String tags[] = { "<", ">", "</" };
		LinkedList<String> temp = new LinkedList<String>();
		Iterator<String> itr = lines.iterator();

		while (itr.hasNext()) {
			String line = itr.next();
			while (temp.contains("*")) {
				if (!opened) {
					line = line.replaceFirst("\\*", openTag("i", tags));
					opened = true;
				} else {
					line = line.replaceFirst("\\*", closeTag("i", tags));
					opened = false;
				}
			}
			line = line.replaceAll("“", "\"");
			line = line.replaceAll("”", "\"");
			line = line.replaceAll("’", "'");
			line = line.replaceAll("‘", "'");
			line = line.replaceAll("…", "...");
			
			line = "<p>" + line + "</p>";
			temp.add(line);
		}
		ts.lines = temp;
	}

	public String openTag(String html, String[] tags) {
		return tags[0] + html + tags[1];
	}

	public String closeTag(String html, String[] tags) {
		return tags[2] + html + tags[1];
	}

}

class ZipDir extends SimpleFileVisitor<Path> {

	private static ZipOutputStream zos;

	private Path sourceDir;

	public ZipDir(Path sourceDir, ZipOutputStream zos) {
		this.sourceDir = sourceDir;
		this.zos = zos;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
		try {
			Path targetFile = sourceDir.relativize(file);
try{
			zos.putNextEntry(new ZipEntry(targetFile.toString()));
} catch (Exception ex){
	System.out.println("derp!!");
}
			byte[] bytes = Files.readAllBytes(file);
			zos.write(bytes, 0, bytes.length);
			zos.closeEntry();

		} catch (IOException ex) {
			System.err.println(ex);
		}

		return FileVisitResult.CONTINUE;
	}
}
