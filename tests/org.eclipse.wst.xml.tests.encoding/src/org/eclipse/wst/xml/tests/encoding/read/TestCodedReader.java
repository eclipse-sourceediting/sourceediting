/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.read;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.wst.sse.core.internal.encoding.CodedReaderCreator;
import org.eclipse.wst.sse.core.internal.encoding.IContentDescriptionExtended;
import org.eclipse.wst.sse.core.internal.encoding.NonContentBasedEncodingRules;
import org.eclipse.wst.xml.tests.encoding.TestsPlugin;
import org.eclipse.wst.xml.tests.encoding.util.ProjectUnzipUtility;


public class TestCodedReader extends TestCase {
	// private final String fileDir = "html/";
	// private final String fileRoot =
	// "/builds/Workspaces/HeadWorkspace/org.eclipse.wst.xml.tests.encoding/";
	// private final String fileLocation = fileRoot + fileDir;
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_TEST_DETAIL = false;
	// needs to be static, since JUnit creates difference instances for each
	// test
	private static boolean fTestProjectInitialized;
	private static final String TEST_PROJECT_NAME = "org.eclipse.encoding.resource.newtests";
	static IProject fTestProject;
	protected static int nSetups = 0;

	private String massageCharset(String foundCharset) {
		String result = foundCharset;
		if ("UTF-16LE".equals(foundCharset)) {
			result = "UTF-16";
		} else if ("X-UnicodeLittle".equals(foundCharset)) {
			result = "UTF-16";
		} else if ("X-UnicodeBig".equals(foundCharset)) {
			result = "UTF-16BE";
		}

		return result;
	}

	/**
	 * 
	 */
	public TestCodedReader() {
		super();
		// System.out.println(currentPlatformCharset);
	}

	public static void main(String[] args) {
		// try {
		// new TestCodedReader().doAllFiles();
		// } catch (CoreException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
				if (Platform.getLocation().equals(locationPath)) {
					locationPath = null;
				}
				desc.setLocation(locationPath);
				project.create(desc, monitor);
				monitor = null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor = null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	private static void getAndCreateProject() throws CoreException {
		// TestsPlugin testsPlugin = (TestsPlugin)
		// Platform.getPlugin("org.eclipse.wst.xml.tests.encoding");
		IWorkspace workspace = TestsPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		// this form creates project as "linked" back to 'fileRoot'
		// createProject(testProject, new Path(fileRoot), null);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
		// IContainer dotestFiles = testProject.getFolder("dotestFiles");
		// assertTrue(dotestFiles.exists());
		// IResource[] allFolders = dotestFiles.members();
		// assertNotNull(allFolders);
	}

	private StringBuffer readInputStream(Reader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int numRead = 0;
		char tBuff[] = new char[4000];
		while ((numRead = reader.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		return buffer;
	}

	protected void setUp() throws Exception {
		super.setUp();
		nSetups++;
		if (!fTestProjectInitialized) {
			getAndCreateProject();
			// unzip files to the root of workspace directory
			String destinationProjectString = fTestProject.getLocation().toOSString();
			String destinationFolder = destinationProjectString + "/";
			// this zip file is sitting in the "root" of test plugin
			File zipFile = TestsPlugin.getTestFile("testfiles.zip");
			ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
			projUtil.unzipAndImport(zipFile, destinationFolder);
			projUtil.initJavaProject(TEST_PROJECT_NAME);
			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		nSetups--;
		if (nSetups == 0) {
			if (!DEBUG) {
				// Display display = PlatformUI.getWorkbench().getDisplay();
				// display.asyncExec(new Runnable() {
				// public void run() {
				// ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
				// IProject proj = fTestProject;
				// fTestProject = null;
				// try {
				// projUtil.deleteProject(proj);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// });
			}
		}
	}

	protected void doTest(String expectedJavaCharset, String expectedDetectedCharset, String filePath, Class expectedException) throws CoreException, IOException {
		Reader reader = null;
		IFile file = (IFile) fTestProject.findMember(filePath);
		assertNotNull(file);
		expectedJavaCharset = massageCharset(expectedJavaCharset);
		expectedDetectedCharset = massageCharset(expectedDetectedCharset);

		// note: eventually, I should change calling methods so if detected
		// charset is expected to be same
		// as the 'used' java charset, then simply pass in null, but for now,
		// we'll "force" that here in this
		// method.
		if (expectedJavaCharset.equals(expectedDetectedCharset)) {
			expectedDetectedCharset = null;
		}

		try {
			reader = doCoreTest(expectedJavaCharset, expectedDetectedCharset, file);
		} catch (Exception e) {
			if (expectedException != null) {
				boolean result = expectedException.isInstance(e);
				assertTrue("Unexpected Exception: " + getGoodFullMsg(e), result);
			} else {
				// will alway assert if gets to here
				String msg = getGoodFullMsg(e);
				assertNull("Unexpected Exception: " + msg, e);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}

		}
	}

	protected Reader doCoreTest(String expectedJavaCharset, String expectedDetectedCharset, IFile file) throws CoreException, IOException {
		Reader reader;
		// create these first, to test exception being thrown correctly
		CodedReaderCreator codedReaderCreator = new CodedReaderCreator();
		codedReaderCreator.set(file);
		reader = codedReaderCreator.getCodedReader();

		String javaCharsetName = file.getCharset();
		IContentDescription description = file.getContentDescription();
		javaCharsetName = massageCharset(javaCharsetName);
		// codedReaderCreator.getEncodingMemento().getJavaCharsetName();
		if (expectedJavaCharset.equals("expectPlatformCharset")) {
			String platformDefault = NonContentBasedEncodingRules.useDefaultNameRules(null);
			assertTrue(javaCharsetName.equals(platformDefault));
		} else {
			boolean asExpected = javaCharsetName.equals(expectedJavaCharset);
			assertTrue(javaCharsetName + " did not equal the expected " + expectedJavaCharset + " (this is a VM dependent test)", asExpected);
		}
		String javaCharsetNameProperty = (String) description.getProperty(IContentDescription.CHARSET);
		String detectedCharsetNameProperty = getDetectedCharsetName(description);
		detectedCharsetNameProperty = massageCharset(detectedCharsetNameProperty);
		// if (detectedCharsetName == null) {
		// detectedCharsetName = javaCharsetNameProperty;
		// }
		if (!expectedJavaCharset.equals("expectPlatformCharset")) {
			boolean expecedResult = expectedJavaCharset.equals(javaCharsetNameProperty);
			assertTrue("java based charset name was not as expected", expecedResult);
		} else {
			String expectedDefault = NonContentBasedEncodingRules.useDefaultNameRules(null);
			boolean ExpectedResult = expectedDefault.equals(javaCharsetNameProperty);
			assertTrue("java based charset name not as expected when platform default expected", ExpectedResult);
		}

		if (expectedDetectedCharset != null) {
			boolean expectedResult = expectedDetectedCharset.equals(detectedCharsetNameProperty);
			assertTrue("detected charset name was not as expected", expectedResult);
		}
		// test if can read/write file
		StringBuffer buffer = readInputStream(reader);
		if (DEBUG_TEST_DETAIL) {
			System.out.print(buffer);
		}
		return reader;
	}

	/**
	 * This method is just to help handle transition from using
	 * EncodingMemtento to not using encodingMemento.
	 */
	private String getDetectedCharsetName(IContentDescription description) {
		String result = null;
		result = (String) description.getProperty(IContentDescriptionExtended.DETECTED_CHARSET);
		return result;
	}

	/**
	 * @param e
	 * @return
	 */
	private String getGoodFullMsg(Exception e) {
		String msg = e.getClass().getName() + ": " + e.getMessage();
		return msg;
	}

	public void testFile1() throws CoreException, IOException {
		doTest("expectPlatformCharset", null, "testfiles/css/emptyFile.css", null);
	}

	public void testFile2() throws CoreException, IOException {
		doTest("EUC-JP", "EUC-JP", "testfiles/css/encoding_test_eucjp.css", null);
	}

	public void testFile3() throws CoreException, IOException {
		doTest("ISO-2022-JP", "ISO-2022-JP", "testfiles/css/encoding_test_jis.css", null);
	}

	public void testFile4() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/css/encoding_test_sjis.css", null);
	}

	public void testFile5() throws CoreException, IOException {
		doTest("expectPlatformCharset", null, "testfiles/css/noEncoding.css", null);
	}

	public void testFile6() throws CoreException, IOException {
		doTest("ISO-8859-6", "ISO-8859-6", "testfiles/css/nonStandard.css", null);
	}

	public void testFile7() throws CoreException, IOException {
		doTest("ISO-8859-6", "ISO-8859-6", "testfiles/css/nonStandardIllFormed.css", null);
	}

	public void testFile8() throws CoreException, IOException {
		doTest("ISO-8859-6", "ISO-8859-6", "testfiles/css/nonStandardIllFormed2.css", null);
	}


	public void testFile57() throws CoreException, IOException {
		doTest("expectPlatformCharset", null, "testfiles/html/EmptyFile.html", null);
	}

	public void testFile58() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/html/IllformedNormalNonDefault.html", null);
	}

	public void testFile59() throws CoreException, IOException {
		doTest("expectPlatformCharset", null, "testfiles/html/LargeNoEncoding.html", null);
	}

	public void testFile60() throws CoreException, IOException {
		doTest("ISO-8859-1", "iso-8859-1", "testfiles/html/LargeNonDefault.html", null);
	}

	public void testFile61() throws CoreException, IOException {
		doTest("ISO-8859-6", "ISO-8859-6", "testfiles/html/MultiNonDefault.html", null);
	}

	public void testFile62() throws CoreException, IOException {
		doTest("expectPlatformCharset", null, "testfiles/html/NoEncoding.html", null);
	}

	public void testFile63() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/html/noquotes.html", null);
	}

	public void testFile64() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/html/NormalNonDefault.html", null);
	}

	public void testFile65() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/EmptyFile.jsp", null);
	}

	public void testFile66() throws CoreException, IOException {
		doTest("ISO-8859-8", null, "testfiles/jsp/IllformedNormalNonDefault.jsp", null);
	}

	public void testFile67() throws CoreException, IOException {
		doTest("null", "null", "testfiles/jsp/inValidEncodingValue.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile68() throws CoreException, IOException {
		doTest("windows-1252", "Cp1252", "testfiles/jsp/javaEncodingValue.jsp", null);
	}

	public void testFile69() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/MalformedNoEncoding.jsp", null);
	}

	public void testFile70() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/MalformedNoEncodingXSL.jsp", null);
	}

	public void testFile71() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/noEncoding.jsp", null);
	}

	public void testFile72() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/NoEncodinginXMLDecl.jsp", null);
	}

	public void testFile73() throws CoreException, IOException {
		doTest("ISO-8859-2", "ISO-8859-2", "testfiles/jsp/nomalDirectiveCase.jsp", null);
	}

	public void testFile74() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/nomalDirectiveCaseNoEncoding.jsp", null);
	}

	public void testFile75() throws CoreException, IOException {
		doTest("ISO-8859-3", "ISO-8859-3", "testfiles/jsp/nomalDirectiveCaseUsingCharset.jsp", null);
	}

	public void testFile76() throws CoreException, IOException {
		doTest("ISO-8859-2", "ISO-8859-2", "testfiles/jsp/nomalDirectiveCaseUsingXMLSyntax.jsp", null);
	}

	public void testFile77() throws CoreException, IOException {
		doTest("ISO-8859-8", null, "testfiles/jsp/NormalNonDefault.jsp", null);
	}

	public void testFile78() throws CoreException, IOException {
		doTest("ISO-8859-8", "ISO-8859-8", "testfiles/jsp/NormalNonDefaultWithXMLDecl.jsp", null);
	}

	public void testFile79() throws CoreException, IOException {
		doTest("ISO-8859-7", "ISO-8859-7", "testfiles/jsp/NormalPageCaseNonDefault.jsp", null);
	}

	public void testFile80() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/jsp/SelColBeanRow12ResultsForm.jsp", null);
	}

	public void testFile81() throws CoreException, IOException {
		doTest("ISO-8859-3", "ISO-8859-3", "testfiles/jsp/testBrokenLine.jsp", null);
	}

	public void testFile82() throws CoreException, IOException {
		doTest("null", "null", "testfiles/jsp/testDefaultEncoding.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile83() throws CoreException, IOException {
		doTest("null", "null", "testfiles/jsp/testDefaultEncodingWithJunk.jsp", org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail.class);
	}

	public void testFile84() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/testExtraJunk.jsp", null);
	}

	public void testFile85() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/jsp/testExtraValidStuff.jsp", null);
	}

	public void testFile86() throws CoreException, IOException {
		doTest("ISO-8859-1", null, "testfiles/jsp/testIllFormed.jsp", null);
	}

	public void testFile87() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/jsp/testIllFormed2.jsp", null);
	}

	public void testFile88() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/testNoEncodingValue.jsp", null);
	}

	public void testFile89() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/testNoPageDirective.jsp", null);
	}

	public void testFile90() throws CoreException, IOException {
		doTest("ISO-8859-2", "ISO-8859-2", "testfiles/jsp/testNoPageDirectiveAtFirst.jsp", null);
	}

	public void testFile91() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/jsp/testNoPageDirectiveInLargeFile.jsp", null);
	}

	public void testFile92() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/jsp/testNormalCase.jsp", null);
	}

	public void testFile93() throws CoreException, IOException {
		doTest("UTF-16", "UTF-16", "testfiles/jsp/testUTF16.jsp", null);
	}

	public void testFile94() throws CoreException, IOException {
		doTest("UTF-16LE", "UTF-16LE", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeader2.jsp", null);
	}

	public void testFile95() throws CoreException, IOException {
		doTest("UTF-16", "UTF-16", "testfiles/jsp/utf16UnicodeStreamWithNoEncodingInHeaderBE.jsp", null);
	}

	public void testFile96() throws CoreException, IOException {
		doTest("UTF-16", "UTF-16", "testfiles/jsp/utf16WithJapaneseChars.jsp", null);
	}

	public void testFile97() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/jsp/UTF8With3ByteBOM.jsp", null);
	}

	public void testFile98() throws CoreException, IOException {
		doTest("ISO-8859-8", "ISO-8859-8", "testfiles/jsp/WellFormedNormalNonDefault.jsp", null);
	}

	public void testFile99() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsForm.jsp", null);
	}

	public void testFile100() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/regressionTestFiles/defect223365/SelColBeanRow12ResultsFormB.jsp", null);
	}

	public void testFile101() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/regressionTestFiles/defect224293/testshiftjisXmlSyntax.jsp", null);
	}

	public void testFile102() throws CoreException, IOException {
		doTest("Shift_JIS", "Shift_JIS", "testfiles/regressionTestFiles/defect229667/audi.jsp", null);
	}

	public void testFile103() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/EmptyFile.xml", null);
	}

	public void testFile104() throws CoreException, IOException {
		doTest("EUC-JP", "EUC-JP", "testfiles/xml/eucjp.xml", null);
	}

	// public void testFile105() throws CoreException, IOException {
	// doTest("ISO-8859-1", "ISO-8859-1",
	// "testfiles/xml/IllformedNormalNonDefault.xml", null);
	// }

	// public void testFile106() throws CoreException, IOException {
	// doTest("UTF-8", "UTF-8", "testfiles/xml/MalformedNoEncoding.xml",
	// CharacterCodingException.class);
	// }

	// public void testFile107() throws CoreException, IOException {
	// doTest("UTF-8", "UTF-8", "testfiles/xml/MalformedNoEncoding.xsl",
	// CharacterCodingException.class);
	// }

	public void testFile108() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/NoEncoding.xml", null);
	}

	public void testFile109() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/xml/NormalNonDefault.xml", null);
	}

	public void testFile110() throws CoreException, IOException {
		doTest("Shift_JIS", "Shift_JIS", "testfiles/xml/shiftjis.xml", null);
	}

	public void testFile111() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/xml/testExtraJunk.xml", null);
	}

	public void testFile112() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testExtraValidStuff.xml", null);
	}

	// public void testFile113() throws CoreException, IOException {
	// doTest("UTF-8", "UTF-8", "testfiles/xml/testIllFormed.xml", null);
	// }

	public void testFile114() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testIllFormed2.xml", null);
	}

	// public void testFile115() throws CoreException, IOException {
	// doTest("null", "null", "testfiles/xml/testIllFormed3.xml",
	// UnsupportedCharsetExceptionWithDetail.class);
	// }

	public void testFile116() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testIllFormed4.xml", null);
	}

	public void testFile117() throws CoreException, IOException {
		doTest("ISO-8859-1", "ISO-8859-1", "testfiles/xml/testMultiLine.xml", null);
	}

	public void testFile118() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testNoEncodingValue.xml", null);
	}

	public void testFile119() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testNormalCase.xml", null);
	}

	public void testFile120() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testNoXMLDecl.xml", null);
	}

	public void testFile121() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testNoXMLDeclAtFirst.xml", null);
	}

	public void testFile122() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/testNoXMLDeclInLargeFile.xml", null);
	}

	// public void testFile123() throws CoreException, IOException {
	// doTest("UTF-16BE", "UTF-16BE", "testfiles/xml/testUTF16.xml",
	// CharacterCodingException.class);
	// }

	// public void testFile124() throws CoreException, IOException {
	// doTest("UTF-16LE", "UTF-16LE",
	// "testfiles/xml/UTF16LEAtStartOfLargeFile.xml", null);
	// }

	// public void testFile125() throws CoreException, IOException {
	// doTest("UTF-16LE", "UTF-16LE",
	// "testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeader2.xml", null);
	// }

	// public void testFile126() throws CoreException, IOException {
	// doTest("UTF-16BE", "UTF-16BE",
	// "testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeaderBE.xml",
	// CharacterCodingException.class);
	// }

	// public void testFile127() throws CoreException, IOException {
	// doTest("UTF-16BE", "UTF-16BE",
	// "testfiles/xml/utf16WithJapaneseChars.xml", null);
	// }

	public void testFile128() throws CoreException, IOException {
		doTest("UTF-8", "UTF-8", "testfiles/xml/UTF8With3ByteBOM.xml", null);
	}

	public void testDocumentStreamCaseHTML() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/DocumentStreamTestCase/kaeru5.htm", null);
	}

	public void testDocumentStreamCaseJSP() throws CoreException, IOException {
		doTest("Shift_JIS", "SHIFT_JIS", "testfiles/DocumentStreamTestCase/kaeru5.jsp", null);
	}
}
