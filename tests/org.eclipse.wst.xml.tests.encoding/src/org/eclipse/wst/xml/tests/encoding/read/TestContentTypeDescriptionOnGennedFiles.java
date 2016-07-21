/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 498268
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.read;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.tests.encoding.GenerateFiles;



public class TestContentTypeDescriptionOnGennedFiles extends TestContentDescription {
	private static final boolean DEBUG = false;

	protected void doGenTest(String charsetName) throws CoreException, IOException {
		String filename = GenerateFiles.getMainDirectoryBasedOnVMName() + "/xml/test-" + charsetName + ".xml";
		doTest(charsetName, filename, null);
	}

	protected void doTest(String expectedJavaCharset, String filePath, Class expectedException) throws CoreException, IOException {
		if (expectedJavaCharset != null && expectedJavaCharset.indexOf("UTF-32") > -1) {
			return; //won't try 32 bit right now
		}
		IFile file = (IFile) fTestProject.findMember(filePath);

		// if there no file, just assume that its due to which VM is
		// bring used. (Could be improved in future to avoid counting as a
		// test) - this check in here for initial debugging only
		if (file == null) {
			if (DEBUG) {
				System.out.println();
				System.out.println("test not ran since charset not supported by VM: " + filePath);
			}
			return;
		}
		super.doTest(expectedJavaCharset, filePath, expectedException);
	}


	public void testFile0() throws CoreException, IOException {
		String charsetName = "Big5";
		doGenTest(charsetName);
	}

	public void testFile1() throws CoreException, IOException {
		String charsetName = "CESU-8";
		doGenTest(charsetName);
	}

	public void testFile2() throws CoreException, IOException {
		String charsetName = "COMPOUND_TEXT";
		doGenTest(charsetName);
	}

	public void testFile3() throws CoreException, IOException {
		String charsetName = "EUC-CN";
		doGenTest(charsetName);
	}

	public void testFile4() throws CoreException, IOException {
		String charsetName = "EUC-JP";
		doGenTest(charsetName);
	}

	public void testFile5() throws CoreException, IOException {
		String charsetName = "EUC-KR";
		doGenTest(charsetName);
	}

	public void testFile6() throws CoreException, IOException {
		String charsetName = "GB18030";
		doGenTest(charsetName);
	}

	public void testFile7() throws CoreException, IOException {
		String charsetName = "GB2312";
		doGenTest(charsetName);
	}

	public void testFile8() throws CoreException, IOException {
		String charsetName = "GBK";
		doGenTest(charsetName);
	}

	public void testFile9() throws CoreException, IOException {
		String charsetName = "hp-roman8";
		doGenTest(charsetName);
	}

	public void testFile10() throws CoreException, IOException {
		String charsetName = "IBM-1006";
		doGenTest(charsetName);
	}

	public void testFile11() throws CoreException, IOException {
		String charsetName = "IBM-1041";
		doGenTest(charsetName);
	}

	public void testFile12() throws CoreException, IOException {
		String charsetName = "IBM-1043";
		doGenTest(charsetName);
	}

	public void testFile13() throws CoreException, IOException {
		String charsetName = "IBM-1046";
		doGenTest(charsetName);
	}

	public void testFile14() throws CoreException, IOException {
		String charsetName = "IBM-1046S";
		doGenTest(charsetName);
	}

	public void testFile15() throws CoreException, IOException {
		String charsetName = "IBM-1088";
		doGenTest(charsetName);
	}

	public void testFile16() throws CoreException, IOException {
		String charsetName = "IBM-1098";
		doGenTest(charsetName);
	}

	public void testFile17() throws CoreException, IOException {
		String charsetName = "IBM-1114";
		doGenTest(charsetName);
	}

	public void testFile18() throws CoreException, IOException {
		String charsetName = "IBM-1115";
		doGenTest(charsetName);
	}

	public void testFile19() throws CoreException, IOException {
		String charsetName = "IBM-1124";
		doGenTest(charsetName);
	}

	public void testFile20() throws CoreException, IOException {
		String charsetName = "IBM-1363";
		doGenTest(charsetName);
	}

	public void testFile21() throws CoreException, IOException {
		String charsetName = "IBM-1363C";
		doGenTest(charsetName);
	}

	public void testFile22() throws CoreException, IOException {
		String charsetName = "IBM-1370";
		doGenTest(charsetName);
	}

	public void testFile23() throws CoreException, IOException {
		String charsetName = "IBM-1381";
		doGenTest(charsetName);
	}

	public void testFile24() throws CoreException, IOException {
		String charsetName = "IBM-1383";
		doGenTest(charsetName);
	}

	public void testFile25() throws CoreException, IOException {
		String charsetName = "IBM-1386";
		doGenTest(charsetName);
	}

	public void testFile26() throws CoreException, IOException {
		String charsetName = "IBM-33722C";
		doGenTest(charsetName);
	}

	public void testFile27() throws CoreException, IOException {
		String charsetName = "IBM-437";
		doGenTest(charsetName);
	}

	public void testFile28() throws CoreException, IOException {
		String charsetName = "IBM-737";
		doGenTest(charsetName);
	}

	public void testFile29() throws CoreException, IOException {
		String charsetName = "IBM-775";
		doGenTest(charsetName);
	}

	public void testFile30() throws CoreException, IOException {
		String charsetName = "IBM-808";
		doGenTest(charsetName);
	}

	public void testFile31() throws CoreException, IOException {
		String charsetName = "IBM-850";
		doGenTest(charsetName);
	}

	public void testFile32() throws CoreException, IOException {
		String charsetName = "IBM-852";
		doGenTest(charsetName);
	}

	public void testFile33() throws CoreException, IOException {
		String charsetName = "IBM-855";
		doGenTest(charsetName);
	}

	public void testFile34() throws CoreException, IOException {
		String charsetName = "IBM-856";
		doGenTest(charsetName);
	}

	public void testFile35() throws CoreException, IOException {
		String charsetName = "IBM-857";
		doGenTest(charsetName);
	}

	public void testFile36() throws CoreException, IOException {
		String charsetName = "IBM-858";
		doGenTest(charsetName);
	}

	public void testFile37() throws CoreException, IOException {
		String charsetName = "IBM-859";
		doGenTest(charsetName);
	}

	public void testFile38() throws CoreException, IOException {
		String charsetName = "IBM-860";
		doGenTest(charsetName);
	}

	public void testFile39() throws CoreException, IOException {
		String charsetName = "IBM-861";
		doGenTest(charsetName);
	}

	public void testFile40() throws CoreException, IOException {
		String charsetName = "IBM-862";
		doGenTest(charsetName);
	}

	public void testFile41() throws CoreException, IOException {
		String charsetName = "IBM-863";
		doGenTest(charsetName);
	}

	public void testFile42() throws CoreException, IOException {
		String charsetName = "IBM-864";
		doGenTest(charsetName);
	}

	public void testFile43() throws CoreException, IOException {
		String charsetName = "IBM-864S";
		doGenTest(charsetName);
	}

	public void testFile44() throws CoreException, IOException {
		String charsetName = "IBM-865";
		doGenTest(charsetName);
	}

	public void testFile45() throws CoreException, IOException {
		String charsetName = "IBM-866";
		doGenTest(charsetName);
	}

	public void testFile46() throws CoreException, IOException {
		String charsetName = "IBM-867";
		doGenTest(charsetName);
	}

	public void testFile47() throws CoreException, IOException {
		String charsetName = "IBM-868";
		doGenTest(charsetName);
	}

	public void testFile48() throws CoreException, IOException {
		String charsetName = "IBM-869";
		doGenTest(charsetName);
	}

	public void testFile49() throws CoreException, IOException {
		String charsetName = "IBM-874";
		doGenTest(charsetName);
	}

	public void testFile50() throws CoreException, IOException {
		String charsetName = "IBM-897";
		doGenTest(charsetName);
	}

	public void testFile51() throws CoreException, IOException {
		String charsetName = "IBM-921";
		doGenTest(charsetName);
	}

	public void testFile52() throws CoreException, IOException {
		String charsetName = "IBM-922";
		doGenTest(charsetName);
	}

	public void testFile53() throws CoreException, IOException {
		String charsetName = "IBM-932";
		doGenTest(charsetName);
	}

	public void testFile54() throws CoreException, IOException {
		String charsetName = "IBM-942";
		doGenTest(charsetName);
	}

	public void testFile55() throws CoreException, IOException {
		String charsetName = "IBM-942C";
		doGenTest(charsetName);
	}

	public void testFile56() throws CoreException, IOException {
		String charsetName = "IBM-943";
		doGenTest(charsetName);
	}

	public void testFile57() throws CoreException, IOException {
		String charsetName = "IBM-943C";
		doGenTest(charsetName);
	}

	public void testFile58() throws CoreException, IOException {
		String charsetName = "IBM-948";
		doGenTest(charsetName);
	}

	public void testFile59() throws CoreException, IOException {
		String charsetName = "IBM-949";
		doGenTest(charsetName);
	}

	public void testFile60() throws CoreException, IOException {
		String charsetName = "IBM-949C";
		doGenTest(charsetName);
	}

	public void testFile61() throws CoreException, IOException {
		String charsetName = "IBM-950";
		doGenTest(charsetName);
	}

	public void testFile62() throws CoreException, IOException {
		String charsetName = "IBM-954C";
		doGenTest(charsetName);
	}

	public void testFile63() throws CoreException, IOException {
		String charsetName = "ISO-2022-CN";
		doGenTest(charsetName);
	}

	public void testFile64() throws CoreException, IOException {
		String charsetName = "ISO-2022-CN-GB";
		doGenTest(charsetName);
	}

	public void testFile65() throws CoreException, IOException {
		String charsetName = "ISO-2022-JP";
		doGenTest(charsetName);
	}

	public void testFile66() throws CoreException, IOException {
		String charsetName = "ISO-2022-KR";
		doGenTest(charsetName);
	}

	public void testFile67() throws CoreException, IOException {
		String charsetName = "ISO-8859-1";
		doGenTest(charsetName);
	}

	public void testFile68() throws CoreException, IOException {
		String charsetName = "ISO-8859-10";
		doGenTest(charsetName);
	}

	public void testFile69() throws CoreException, IOException {
		String charsetName = "ISO-8859-13";
		doGenTest(charsetName);
	}

	public void testFile70() throws CoreException, IOException {
		String charsetName = "ISO-8859-14";
		doGenTest(charsetName);
	}

	public void testFile71() throws CoreException, IOException {
		String charsetName = "ISO-8859-15";
		doGenTest(charsetName);
	}

	public void testFile72() throws CoreException, IOException {
		String charsetName = "ISO-8859-16";
		doGenTest(charsetName);
	}

	public void testFile73() throws CoreException, IOException {
		String charsetName = "ISO-8859-2";
		doGenTest(charsetName);
	}

	public void testFile74() throws CoreException, IOException {
		String charsetName = "ISO-8859-3";
		doGenTest(charsetName);
	}

	public void testFile75() throws CoreException, IOException {
		String charsetName = "ISO-8859-4";
		doGenTest(charsetName);
	}

	public void testFile76() throws CoreException, IOException {
		String charsetName = "ISO-8859-5";
		doGenTest(charsetName);
	}

	public void testFile77() throws CoreException, IOException {
		String charsetName = "ISO-8859-6";
		doGenTest(charsetName);
	}

	public void testFile78() throws CoreException, IOException {
		String charsetName = "ISO-8859-6S";
		doGenTest(charsetName);
	}

	public void testFile79() throws CoreException, IOException {
		String charsetName = "ISO-8859-7";
		doGenTest(charsetName);
	}

	public void testFile80() throws CoreException, IOException {
		String charsetName = "ISO-8859-8";
		doGenTest(charsetName);
	}

	public void testFile81() throws CoreException, IOException {
		String charsetName = "ISO-8859-9";
		doGenTest(charsetName);
	}

	public void testFile82() throws CoreException, IOException {
		String charsetName = "JIS0201";
		doGenTest(charsetName);
	}

	public void testFile83() throws CoreException, IOException {
		String charsetName = "JIS0208";
		doGenTest(charsetName);
	}

	public void testFile84() throws CoreException, IOException {
		String charsetName = "JIS0212";
		doGenTest(charsetName);
	}

	public void testFile85() throws CoreException, IOException {
		String charsetName = "Johab";
		doGenTest(charsetName);
	}

	public void testFile86() throws CoreException, IOException {
		String charsetName = "KOI8-R";
		doGenTest(charsetName);
	}

	public void testFile87() throws CoreException, IOException {
		String charsetName = "KOI8-RU";
		doGenTest(charsetName);
	}

	public void testFile88() throws CoreException, IOException {
		String charsetName = "KOI8-U";
		doGenTest(charsetName);
	}

	public void testFile89() throws CoreException, IOException {
		String charsetName = "KSC5601";
		doGenTest(charsetName);
	}

	public void testFile90() throws CoreException, IOException {
		String charsetName = "MacArabic";
		doGenTest(charsetName);
	}

	public void testFile91() throws CoreException, IOException {
		String charsetName = "MacCentralEurope";
		doGenTest(charsetName);
	}

	public void testFile92() throws CoreException, IOException {
		String charsetName = "MacCroatian";
		doGenTest(charsetName);
	}

	public void testFile93() throws CoreException, IOException {
		String charsetName = "MacCyrillic";
		doGenTest(charsetName);
	}

	public void testFile94() throws CoreException, IOException {
		String charsetName = "MacGreek";
		doGenTest(charsetName);
	}

	public void testFile95() throws CoreException, IOException {
		String charsetName = "MacHebrew";
		doGenTest(charsetName);
	}

	public void testFile96() throws CoreException, IOException {
		String charsetName = "MacIceland";
		doGenTest(charsetName);
	}

	public void testFile97() throws CoreException, IOException {
		String charsetName = "MacRoman";
		doGenTest(charsetName);
	}

	public void testFile98() throws CoreException, IOException {
		String charsetName = "MacRomania";
		doGenTest(charsetName);
	}

	public void testFile99() throws CoreException, IOException {
		String charsetName = "MacThai";
		doGenTest(charsetName);
	}

	public void testFile100() throws CoreException, IOException {
		String charsetName = "MacTurkish";
		doGenTest(charsetName);
	}

	public void testFile101() throws CoreException, IOException {
		String charsetName = "MacUkraine";
		doGenTest(charsetName);
	}

	public void testFile102() throws CoreException, IOException {
		String charsetName = "PTCP154";
		doGenTest(charsetName);
	}

	public void testFile103() throws CoreException, IOException {
		String charsetName = "Shift_JIS";
		doGenTest(charsetName);
	}

	public void testFile104() throws CoreException, IOException {
		String charsetName = "TIS-620";
		doGenTest(charsetName);
	}

	public void testFile105() throws CoreException, IOException {
		String charsetName = "US-ASCII";
		doGenTest(charsetName);
	}

	public void testFile106() throws CoreException, IOException {
		String charsetName = "UTF-16";
		doGenTest(charsetName);
	}

//	public void testFile107() throws CoreException, IOException {
//		String charsetName = "UTF-16BE";
//		doGenTest(charsetName);
//	}

	public void testFile108() throws CoreException, IOException {
		String charsetName = "UTF-16LE";
		doGenTest(charsetName);
	}

	public void testFile109() throws CoreException, IOException {
		String charsetName = "UTF-32";
		doGenTest(charsetName);
	}

	public void testFile110() throws CoreException, IOException {
		String charsetName = "UTF-32BE";
		doGenTest(charsetName);
	}

	public void testFile111() throws CoreException, IOException {
		String charsetName = "UTF-32LE";
		doGenTest(charsetName);
	}

	public void testFile112() throws CoreException, IOException {
		String charsetName = "UTF-8";
		doGenTest(charsetName);
	}

	public void testFile113() throws CoreException, IOException {
		String charsetName = "UTF-8J";
		doGenTest(charsetName);
	}

	public void testFile114() throws CoreException, IOException {
		String charsetName = "windows-1250";
		doGenTest(charsetName);
	}

	public void testFile115() throws CoreException, IOException {
		String charsetName = "windows-1251";
		doGenTest(charsetName);
	}

	public void testFile116() throws CoreException, IOException {
		String charsetName = "windows-1252";
		doGenTest(charsetName);
	}

	public void testFile117() throws CoreException, IOException {
		String charsetName = "windows-1253";
		doGenTest(charsetName);
	}

	public void testFile118() throws CoreException, IOException {
		String charsetName = "windows-1254";
		doGenTest(charsetName);
	}

	public void testFile119() throws CoreException, IOException {
		String charsetName = "windows-1255";
		doGenTest(charsetName);
	}

	public void testFile120() throws CoreException, IOException {
		String charsetName = "windows-1256";
		doGenTest(charsetName);
	}

	public void testFile121() throws CoreException, IOException {
		String charsetName = "windows-1256S";
		doGenTest(charsetName);
	}

	public void testFile122() throws CoreException, IOException {
		String charsetName = "windows-1257";
		doGenTest(charsetName);
	}

	public void testFile123() throws CoreException, IOException {
		String charsetName = "windows-1258";
		doGenTest(charsetName);
	}

	public void testFile124() throws CoreException, IOException {
		String charsetName = "windows-874";
		doGenTest(charsetName);
	}

	public void testFile125() throws CoreException, IOException {
		String charsetName = "windows-932";
		doGenTest(charsetName);
	}

	public void testFile126() throws CoreException, IOException {
		String charsetName = "windows-936";
		doGenTest(charsetName);
	}

	public void testFile127() throws CoreException, IOException {
		String charsetName = "windows-949";
		doGenTest(charsetName);
	}

	public void testFile128() throws CoreException, IOException {
		String charsetName = "windows-950";
		doGenTest(charsetName);
	}
/* 
removed for PPC machine with IBM VM 
https://bugs.eclipse.org/bugs/show_bug.cgi?id=126503
*/ 
//	public void testFile129() throws CoreException, IOException {
//		String charsetName = "X-UnicodeBig";
//		doGenTest(charsetName);
//	}

	public void testFile130() throws CoreException, IOException {
		String charsetName = "X-UnicodeLittle";
		doGenTest(charsetName);
	}



}
