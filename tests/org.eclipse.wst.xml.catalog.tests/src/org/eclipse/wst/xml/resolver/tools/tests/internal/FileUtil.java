package org.eclipse.wst.xml.resolver.tools.tests.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;

public class FileUtil {

	public FileUtil() {
		super();
	}
	
	public static void copyFile(String src, String dest) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			int c = 0;
			byte[] array = new byte[1024];
			while ((c = is.read(array)) >= 0) {
				fos.write(array, 0, c);
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
				is.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static File createFileAndParentDirectories(String fileName)
		throws Exception {
		File file = new File(fileName);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		file.createNewFile();
		return file;
	}
	
	public static void deleteDirectories(File dir)
		throws Exception {

		File[] children = dir.listFiles();
		for(int i=0; i< children.length; i++){
			if(children[i].list() != null && children[i].list().length > 0){
				deleteDirectories(children[i]);
			}
			else{
				children[i].delete();
			}
		}
		dir.delete();
			
	}
	
	public static boolean textualCompare(final File first, final File second, File diff) throws java.io.IOException {
        BufferedReader r1 = new BufferedReader(new FileReader(first));
        BufferedReader r2 = new BufferedReader(new FileReader(second));
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(diff));             
                             
        boolean diffFound = false;
        while (r1.ready()) {
            String s1 = r1.readLine();
            String s2 = r2.readLine();
            if (s1 == null) {
                if (s2 == null)
                    return diffFound;   // files equal
                else
                    diffFound = true;  // files differ in length
            }
            if (!s1.equals(s2)) {
            	printWriter.println("<<<< " + s1);
            	printWriter.println("<<<< " + s2);
                diffFound = true;    // files differ
            }
        } 
        
        printWriter.flush(); 
        
        return  diffFound;
     
    }

}
