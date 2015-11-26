/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.tests.translation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.jsdt.web.core.tests.Activator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;



public class TestHtmlTranslation extends TestCase {
	private IModelManager fModelManager = null;
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
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		assertTrue("expected function definition is missing", translation.getJsText().indexOf("function blah()") >= 0);

		// release model
		structuredModel.releaseFromRead();
	}

	private String readFile(String fileName) {		
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

	private static void printException(Exception exception) {
		exception.printStackTrace();
	}

	private IStructuredModel getModel(String fileName) {
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
			catch (IOException exception) {
				// should already be closed
			}
		}

		return structuredModel;
	}

	private IStructuredModel getSharedModel(String id, String contents) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;

		try {
			inputStream = new ByteArrayInputStream(contents.getBytes("UTF8"));
			
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
			catch (IOException exception) {
				// should already be closed
			}
		}

		return structuredModel;
	}

	private String getFile(String fileName) {
		return readFile("/testFiles/".concat(fileName));
	}
	
	public void testMangleTagInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var a = <custom:tag/>5; if(a < 4) {} ; </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("tag included", translated.indexOf("<custom") < 0);
		assertTrue("tag included", translated.indexOf("/>") < 0);

		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleServerSideAndClientTagInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var a = <custom:tag/>5;\nif(a < <%= 4 %>) {} ; </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("<%") < 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("%>") < 0);
		assertTrue("tag included\n" + translated, translated.indexOf("custom") < 0);
		assertTrue("tag included\n" + translated, translated.indexOf("/>") < 0);
		assertTrue("var dropped\n" + translated, translated.indexOf("var a = ") > -1);
		assertTrue("if dropped\n" + translated, translated.indexOf("5;\nif(a <") > -1);
		assertTrue("block dropped\n" + translated, translated.indexOf(") {} ; ") > -1); 

		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleTagAndServerSideInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var a = <%= 4 %>5;\nif(a < <custom:tag/>) {} ; </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("<%") < 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("%>") < 0);
		assertTrue("tag included\n" + translated, translated.indexOf("custom") < 0);
		assertTrue("tag included\n" + translated, translated.indexOf("/>") < 0);
		assertTrue("var dropped\n" + translated, translated.indexOf("var a = ") > -1);
		assertTrue("if dropped\n" + translated, translated.indexOf("5;\nif(a < ") > -1);
		assertTrue("block dropped\n" + translated, translated.indexOf(") {} ; ") > -1); 

		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
//	public void testMangleOverlappingTagAndServerSideInJS() {		
//		// get model
//		String fileName = getName() + ".html";
//		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var a = <%= 4 %>5;\nif(a < <custom:tag attr=\"<%=%>\"/>) {} ; </script>");
//		assertNotNull("missing test model", structuredModel);
//		
//		// do translation
//		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
//		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
//		IJsTranslation translation = translationAdapter.getJsTranslation(false);
//		String translated = translation.getJsText();
//		assertTrue("server-side script block included\n" + translated, translated.indexOf("<%") < 0);
//		assertTrue("server-side script block included\n" + translated, translated.indexOf("%>") < 0);
//		assertTrue("tag included\n" + translated, translated.indexOf("custom") < 0);
//		assertTrue("tag included\n" + translated, translated.indexOf("/>") < 0);
//		assertTrue("var dropped\n" + translated, translated.indexOf("var a = ") > -1);
//		assertTrue("if dropped\n" + translated, translated.indexOf("5;\nif(a < ") > -1);
//		assertTrue("block dropped\n" + translated, translated.indexOf(") {} ; ") > -1); 
//
//		assertTrue("problems found in translation ", translation.getProblems().isEmpty());
//
//		// release model
//		structuredModel.releaseFromRead();
//	}
	public void testMangleServerSide1InJSCheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <%= javaObject.getText() %>; </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleServerSide2InJSCheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <? serverObject.getText() ?>; </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleServerSide1InJSCheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <%= javaObject.getText() %>;  <%= javaObject.getText() %></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleServerSide2InJSCheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <? serverObject.getText() ?>;  <? serverObject.getText() ?></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleMixedServerSideInJS_and_CheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <? serverObject.getText() ?>; <%=\"a\"%> <%=\"b\"%> <? serverObject.getText() ?><%=\"c\"%> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated contents not as expected", "         var text = _$tag_______________________; _$tag___ _$tag___ _$tag________________________$tag___ ",translated);
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleMixedServerSideInJSwithXMLcomment_and_CheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> <!-- \nvar text = <? serverObject.getText() ?>; <%=\"a\"%> <%=\"b\"%> <? serverObject.getText() ?><%=\"c\"%> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated contents not as expected", "              \nvar text = _$tag_______________________; _$tag___ _$tag___ _$tag________________________$tag___ ",translated);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var declaration not found ", translated.indexOf("var text = ") > -1);
		assertTrue("Problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	
	public void testMangleMultipleMixedServerSideInJSwithXMLcommentOnSameLine() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> <!-- var text = <? serverObject.getText() ?>; <%=\"a\"%> <%=\"b\"%> <? serverObject.getText() ?><%=\"c\"%> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("script with leading comment but no new line should be blank",
					"                                                                                                              ",translated);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleMixedServerSideAndClientTagInJS_and_CheckProblems() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <? serverObject.getText() ?>; <%=\"a\"%> <%=\"b\"%> <server:tag/> <? serverObject.getText() ?><%=\"c\"%> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated contents not as expected", "         var text = _$tag_______________________; _$tag___ _$tag___ _$tag________ _$tag________________________$tag___ ",translated);
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testMangleMultipleMixedServerSideAndClientTagInJS_and_CheckProblems2() {
		// get model
		String fileName = getName() + ".jsp";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <? serverObject.getText() ?>; <%=\"a\"%> <%=\"b\"%> <server:tag/> <? serverObject.getText() ?><%=\"c\"%> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated contents not as expected", "         var text = _$tag_______________________; _$tag___ _$tag___ _$tag________ _$tag________________________$tag___ ",translated);
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included", translated.indexOf("<?") < 0);
		assertTrue("server-side script block included", translated.indexOf("?>") < 0);
		assertTrue("server-side script block included", translated.indexOf("<%") < 0);
		assertTrue("server-side script block included", translated.indexOf("%>") < 0);
		assertTrue("var dropped", translated.indexOf("var text = ") > -1);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testJustClientTagInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script><custom:tag /></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation too short", translated.length() > 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("<") < 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("/>") < 0);
		assertTrue("content not included\n" + translated, translated.length() != 0); 

		// release model
		structuredModel.releaseFromRead();
	}
	public void testJustServer1SideInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script><%= %></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("<%") < 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("%>") < 0);
		assertTrue("content not included\n" + translated, translated.length() != 0); 

		// release model
		structuredModel.releaseFromRead();
	}
	public void testJustServer2SideInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script><? !!!!!!!!!!!!!! ?></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("<?") < 0);
		assertTrue("server-side script block included\n" + translated, translated.indexOf("?>") < 0);
		assertTrue("content not included\n" + translated, translated.length() != 0); 

		// release model
		structuredModel.releaseFromRead();
	}
	public void testLeadingXMLComment() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "0<script> <!--12</script>3");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("script with leading comment but no new line should be empty", "                ", translated);
		// release model
		structuredModel.releaseFromRead();
	}
	public void testXMLComment() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> if(a) <!-- --> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated content differs", "         if(a) _$ta _$t ", translated);

		// release model
		structuredModel.releaseFromRead();
	}
	public void testCDATAInJS() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> var text = <![CDATA[ serverObject.getText() ;\n//]]> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("CDATA start found", translated.indexOf("CDATA") < 0);
		assertTrue("CDATA start found", translated.indexOf("[") < 0);
		assertTrue("CDATA end found", translated.indexOf("]") < 0);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}

	public void testEmptyEventHandlerValueCausesStringIndexOutOfBounds() throws Exception {
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<html><body><span onclick=\"\"></body></html>");
		assertNotNull("missing test model", structuredModel);
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		try {
			// do translation
			JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
			IJsTranslation translation = translationAdapter.getJsTranslation(false);
			String translated = translation.getJsText();
		}
		catch (StringIndexOutOfBoundsException e) {
			fail(e.getMessage());
		}
		finally {
			if (structuredModel != null) {
				structuredModel.releaseFromRead();
			}
		}
	}
	public void testFunctionReturnInEventHandler() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<div                                     onClick=\"return myMethod();\">");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("translated content differs", "                                      (function(){return myMethod();;})();", translated);
		StringBuffer problems = new StringBuffer();
		translation.reconcileCompilationUnit();
		List problemList = translation.getProblems();
		for (int i = 0; i < problemList.size(); i++) {
			problems.append(((IProblem)problemList.get(i)).getMessage());
			problems.append('\n');
		}
		assertEquals("problems were found", "", problems.toString());
		// release model
		structuredModel.releaseFromRead();
	}
	
	public void testTwoFunctionReturnsInEventHandlers() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<div                                     onClick=\"return myMethod();\"  onblur=\"return myMethod2();\">\n<script >\ndojo.xhr(\"x\"); \n</script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("dojo.xhr() invocation moved but not properly mapped", structuredModel.getStructuredDocument().get().indexOf("dojo.xhr(\"x\");"), ((JsTranslation)translation).getWebPageOffset(translated.indexOf("dojo.xhr(\"x\");")));
		assertEquals("translated content differs", "                                      (function(){return myMethod();;})();(function(){return myMethod2();;})(); \n         \ndojo.xhr(\"x\"); \n", translated);
		StringBuffer problems = new StringBuffer();
		translation.reconcileCompilationUnit();
		List problemList = translation.getProblems();
		for (int i = 0; i < problemList.size(); i++) {
			problems.append(((IProblem)problemList.get(i)).getMessage());
			problems.append('\n');
		}
		assertEquals("problems were found", "", problems.toString());
		assertEquals("offsets didn't match", structuredModel.getStructuredDocument().get().indexOf("dojo. \n"), translated.indexOf("dojo. \n"));
		// release model
		structuredModel.releaseFromRead();
	}

	public void testScriptLocationsWereRecorded() {
		// get model
		String fileName = getName() + ".html";
		String dojoCall = "\ndojo.xhr(\"x\"); \n";
		IStructuredModel structuredModel = getSharedModel(fileName, "<div                                   onClick=\"return myMethod();\"  onblur=\"return myMethod2();\">\n<script >"+dojoCall+"</script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertEquals("dojo.xhr() invocation moved but not properly mapped", structuredModel.getStructuredDocument().get().indexOf("dojo.xhr(\"x\");"), ((JsTranslation)translation).getWebPageOffset(translated.indexOf("dojo.xhr(\"x\");")));
		assertEquals("translated content differs", "                                    (function(){return myMethod();;})();(function(){return myMethod2();;})(); \n         \ndojo.xhr(\"x\"); \n", translated);
		StringBuffer problems = new StringBuffer();
		translation.reconcileCompilationUnit();
		List problemList = translation.getProblems();
		for (int i = 0; i < problemList.size(); i++) {
			problems.append(((IProblem)problemList.get(i)).getMessage());
			problems.append('\n');
		}
		assertEquals("problems were found", "", problems.toString());
		assertEquals("offsets didn't match", structuredModel.getStructuredDocument().get().indexOf("dojo. \n"), translated.indexOf("dojo. \n"));
		assertNotSame("script locations not returned by translation", null, translation.getScriptPositions());
		int expectedOffset = structuredModel.getStructuredDocument().get().indexOf(dojoCall);
		assertEquals("script location not returned by translation", expectedOffset, translation.getScriptPositions()[0].getOffset());
		assertEquals("script location length incorrect in translation", dojoCall.length(), translation.getScriptPositions()[0].getLength());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testCDATAAroundJS1() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script> <![CDATA[ var text =  serverObject.getText() ]]> </script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("CDATA start found", translated.indexOf("CDATA") < 0);
		assertTrue("CDATA start found", translated.indexOf("[") < 0);
		assertTrue("CDATA end found", translated.indexOf("]") < 0);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testCDATAAroundJS2() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script><![CDATA[ var text =  serverObject.getText() ]]></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("CDATA start found", translated.indexOf("CDATA") < 0);
		assertTrue("CDATA start found", translated.indexOf("[") < 0);
		assertTrue("CDATA end found", translated.indexOf("]") < 0);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}
	public void testCDATAAroundJS3() {
		// get model
		String fileName = getName() + ".html";
		IStructuredModel structuredModel = getSharedModel(fileName, "<script>//<![CDATA[\n var text =  serverObject.getText(); ]]></script>");
		assertNotNull("missing test model", structuredModel);
		
		// do translation
		JsTranslationAdapterFactory.setupAdapterFactory(structuredModel);
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) ((IDOMModel) structuredModel).getDocument().getAdapterFor(IJsTranslation.class);
		IJsTranslation translation = translationAdapter.getJsTranslation(false);
		String translated = translation.getJsText();
		assertTrue("translation empty", translated.length() > 5);
		assertTrue("CDATA start found", translated.indexOf("CDATA") < 0);
		assertTrue("CDATA start found", translated.indexOf("[") < 0);
		assertTrue("CDATA end found", translated.indexOf("]") < 0);
		assertTrue("problems found in translation ", translation.getProblems().isEmpty());

		// release model
		structuredModel.releaseFromRead();
	}

}
