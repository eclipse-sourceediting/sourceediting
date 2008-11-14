

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.web.ui.tests.Activator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;



public class TestHtmlTranslation extends TestCase {

	
	protected IModelManager fModelManager = null;
	private static final String testFilesDirectory = "testFiles";
	public TestHtmlTranslation(){
		fModelManager = StructuredModelManager.getModelManager();
	}
	
	public void testHTMLFormat() {
		// get model
		IStructuredModel structuredModel = getModel("test1.html");

		
		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "test1.html";
		String expected = getFile(expectedFileName);
		

		// release model
		structuredModel.releaseFromRead();
	}

	protected String readFile(String fileName) {
		
		
		String inputString = null;
		InputStream fileInputStream = null;

		try {
			fileInputStream =  FileLocator.openStream(Platform.getBundle(Activator.PLUGIN_ID),new Path(fileName), false);

			byte[] inputBuffer = new byte[1024];
			inputString = new String();
			int bytesRead = -1;

			while (true) {
				bytesRead = fileInputStream.read(inputBuffer);
				if (bytesRead == -1)
					break;
				String bufferString = new String(inputBuffer);
				bufferString = bufferString.substring(0, bytesRead);
				inputString = inputString.concat(bufferString);
			}

			if (fileInputStream != null)
				fileInputStream.close();
		}
		catch (IOException exception) {
			StringWriter s = new StringWriter();
			exception.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}

		return inputString;
	}

	protected static void printException(Exception exception) {
		exception.printStackTrace();
	}

	protected IStructuredModel getModel(String fileName) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;

		try {
			String input = getFile(fileName);
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8"));
			String id = inputStream.toString().concat(fileName);
			
			structuredModel = fModelManager.getModelForRead(id, inputStream, null);
		}
		catch (Exception exception) {
			StringWriter s = new StringWriter();
			exception.printStackTrace(new PrintWriter(s));
			fail(s.toString());
		}
		finally {
			try {
				inputStream.close();
			}
			catch (Exception exception) {
				// hopeless
				StringWriter s = new StringWriter();
				exception.printStackTrace(new PrintWriter(s));
				fail(s.toString());
			}
		}

		return structuredModel;
	}

	protected String getFile(String fileName) {
		return readFile("testfiles/".concat(fileName));
	}
}
