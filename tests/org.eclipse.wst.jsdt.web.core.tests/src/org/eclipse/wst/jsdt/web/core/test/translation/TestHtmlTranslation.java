package org.eclipse.wst.jsdt.web.core.test.translation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.jsdt.web.core.tests.Activator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;



public class TestHtmlTranslation extends TestCase {

	
	protected IModelManager fModelManager = null;
	private static final String testFilesDirectory = "testFiles";
	public TestHtmlTranslation(){
		fModelManager = StructuredModelManager.getModelManager();
	}
	
	public void testHTMLFormat() {
		// get model
		IStructuredModel structuredModel = getModel("test1.html");
		assertNotNull("missing test model", structuredModel);
		
		// compare
		String formatted = structuredModel.getStructuredDocument().get();
		String expectedFileName = "test1.html";
		String expected = getFile(expectedFileName);
		
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJSPTranslation(false);
		assertTrue("expected function definition is missing", translation.getJsText().indexOf("function blah()") >= 0);

		// release model
		structuredModel.releaseFromRead();
	}

	protected String readFile(String fileName) {		
		String inputString = null;
		InputStream fileInputStream = null;

		try {
			URL url = Activator.getDefault().getBundle().getEntry(fileName);
			fileInputStream = url.openStream();

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
		return readFile("/testfiles/".concat(fileName));
	}
}
