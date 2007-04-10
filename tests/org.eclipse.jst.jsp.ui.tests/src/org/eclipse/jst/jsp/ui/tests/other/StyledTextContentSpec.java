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
package org.eclipse.jst.jsp.ui.tests.other;

/**
 * Use this test class to validate an implementation of the StyledTextContent
 * interface. To perform the validation, copy this class to the package where
 * your StyledTextContent implementation lives. Then specify the fully
 * qualified name of your StyledTextContent class as an argument to the main
 * method of this class.
 * 
 * NOTE: This test class assumes that your StyledTextContent implementation
 * handles the following delimiters:
 *  /r /n /r/n
 *  
 */
// This class comes courtesy of Lynne Kues. Our StyledTextContent fails test
// cases 6x of
// "Special cases" since we (nor JFace) handle insertion between CR and LF. A
// minor modification
// was needed to getContentInstance for out version of StyleTextContent
// (StructuredDocumentToTextAdapter).
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.StructuredDocumentToTextAdapter;

public class StyledTextContentSpec implements TextChangeListener {
	static String contentClassName;
	static int failCount = 0;
	static int errorCount = 0;
	Class contentClass = null;
	StyledTextContent contentInstance = null;
	int verify = 0;
	Method currentMethod = null;
	boolean failed = false;
	StyledText widget = null;
	Shell shell = null;

	public StyledTextContentSpec() {
	}

	public void assertWithMessage(String message, boolean condition) {
		System.out.print("\t" + currentMethod.getName() + " " + message);
		if (!condition)
			fail(message);
		else
			System.out.println(" passed");
	}

	public void fail(String message) {
		failed = true;
		System.out.println(" FAILED");
		failCount++;
	}

	public StyledTextContent getContentInstance() {
		contentInstance.setText("");
		widget.setContent(contentInstance);
		// dw. Had to add for our special version of StyledTextContent
		// Needs to be removed for testing jface's DocumentAdapter
		((StructuredDocumentToTextAdapter) contentInstance).setWidget(widget);
		return contentInstance;
	}

	public static String getTestText() {
		return "This is the first line.\r\n" + "This is the second line.\r\n" + "This is the third line.\r\n" + "This is the fourth line.\r\n" + "This is the fifth line.\r\n" + "\r\n" + "This is the first line again.\r\n" + "This is the second line again.\r\n" + "This is the third line again.\r\n" + "This is the fourth line again.\r\n" + "This is the fifth line again.\r\n" + "\r\n" + "This is the first line once again.\r\n" + "This is the second line once again.\r\n" + "This is the third line once again.\r\n" + "This is the fourth line once again.\r\n" + "This is the fifth line once again.";
	}

	public static void main(String[] args) {
		StyledTextContentSpec spec = new StyledTextContentSpec();
		if (args.length > 0) {
			contentClassName = args[0];
			//String path = System.getProperty("java.library.path");
			//System.out.println("Java Library Path: " + path);
		}
		else {
			MessageBox box = new MessageBox(getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setMessage("Content class must be specified as an execution argument."); //$NON-NLS-1$
			box.open();
			return;
		}
		spec.run();
		System.out.println();
		System.out.println(failCount + " TEST FAILURES.");
		System.out.println(errorCount + " UNEXPECTED ERRORS.");
	}

	static private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	public void run() {
		if (contentClassName.equals("")) {
			MessageBox box = new MessageBox(getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setMessage("Content class must be specified as an execution argument."); //$NON-NLS-1$
			box.open();
			return;
		}
		if (contentClass == null) {
			try {
				contentClass = Class.forName(contentClassName);
			}
			catch (ClassNotFoundException e) {
				MessageBox box = new MessageBox(getDisplay().getActiveShell(), SWT.ICON_ERROR);
				box.setMessage("Content class:\n" + contentClassName + "\nnot found"); //$NON-NLS-1$
				box.open();
				return;
			}
		}
		try {
			contentInstance = (StyledTextContent) contentClass.newInstance();
		}
		catch (IllegalAccessException e) {
			MessageBox box = new MessageBox(getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setMessage("Unable to access content class:\n" + contentClassName); //$NON-NLS-1$
			box.open();
			return;
		}
		catch (InstantiationException e) {
			MessageBox box = new MessageBox(getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setMessage("Unable to instantiate content class:\n" + contentClassName); //$NON-NLS-1$
			box.open();
			return;
		}
		Class clazz;
		clazz = this.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			setUp();
			currentMethod = methods[i];
			failed = false;
			try {
				if (currentMethod.getName().startsWith("test_")) {
					System.out.println();
					System.out.println(currentMethod.getName() + "...");
					currentMethod.invoke(this, new Object[0]);
					if (!failed) {
						System.out.println("PASSED.");
					}
					else {
						System.out.println("FAILED");
					}
				}
			}
			catch (InvocationTargetException ex) {
				System.out.println("\t" + currentMethod.getName() + " ERROR ==> " + ex.getTargetException().toString());
				System.out.println("FAILED");
				errorCount++;
			}
			catch (Exception ex) {
				System.out.println("\t" + currentMethod.getName() + " ERROR ==> " + ex.toString());
				System.out.println("FAILED");
				errorCount++;
			}
			if (verify != 0) {
				verify = 0;
				contentInstance.removeTextChangeListener(this);
			}
			tearDown();
		}
	}

	protected void setUp() {
		// create shell
		shell = new Shell();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setSize(500, 300);
		shell.setLayout(layout);
		// create widget
		widget = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		widget.setLayoutData(spec);
		shell.open();
	}

	protected void tearDown() {
		if (shell != null && !shell.isDisposed())
			shell.dispose();
		shell = null;
	}

	public void test_Delete() {
		StyledTextContent content = getContentInstance();
		String newText;
		content.setText("This\nis a test\r");
		content.replaceTextRange(6, 2, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":1a:", newText.equals("This\nia test\r"));
		assertWithMessage(":1b:", content.getLine(0).equals("This"));
		assertWithMessage(":1c:", content.getLine(1).equals("ia test"));
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 9, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":2a:", newText.equals("This\n\r"));
		assertWithMessage(":2b:", content.getLineCount() == 3);
		assertWithMessage(":2c:", content.getLine(0).equals("This"));
		assertWithMessage(":2d:", content.getLine(1).equals(""));
		assertWithMessage(":2e:", content.getLine(2).equals(""));
		content.setText("This\nis a test\nline 3\nline 4");
		content.replaceTextRange(21, 7, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3a:", newText.equals("This\nis a test\nline 3"));
		assertWithMessage(":3b:", content.getLineCount() == 3);
		assertWithMessage(":3c:", content.getLine(0).equals("This"));
		assertWithMessage(":3d:", content.getLine(1).equals("is a test"));
		assertWithMessage(":3e:", content.getLine(2).equals("line 3"));
		content.setText("This\nis a test\nline 3\nline 4");
		content.replaceTextRange(0, 5, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4a:", newText.equals("is a test\nline 3\nline 4"));
		assertWithMessage(":4b:", content.getLineCount() == 3);
		assertWithMessage(":4c:", content.getLine(0).equals("is a test"));
		assertWithMessage(":4d:", content.getLine(1).equals("line 3"));
		assertWithMessage(":4e:", content.getLine(2).equals("line 4"));
		content.replaceTextRange(16, 7, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4f:", newText.equals("is a test\nline 3"));
		assertWithMessage(":4g:", content.getLine(0).equals("is a test"));
		assertWithMessage(":4h:", content.getLine(1).equals("line 3"));
		content.replaceTextRange(9, 7, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4i:", newText.equals("is a test"));
		assertWithMessage(":4j:", content.getLine(0).equals("is a test"));
		content.replaceTextRange(1, 8, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4k:", newText.equals("i"));
		assertWithMessage(":4l:", content.getLine(0).equals("i"));
		content.replaceTextRange(0, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4m:", newText.equals(""));
		assertWithMessage(":4n:", content.getLine(0).equals(""));
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 9, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5a:", newText.equals("This\n\r"));
		assertWithMessage(":5b:", content.getLineCount() == 3);
		assertWithMessage(":5c:", content.getLine(0).equals("This"));
		assertWithMessage(":5d:", content.getLine(1).equals(""));
		assertWithMessage(":5e:", content.getLine(2).equals(""));
		content.setText("L1\r\nL2\r\nL3\r\nL4\r\n");
		content.replaceTextRange(4, 8, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":6a:", newText.equals("L1\r\nL4\r\n"));
		assertWithMessage(":6b:", content.getLineCount() == 3);
		assertWithMessage(":6c:", content.getLine(0).equals("L1"));
		assertWithMessage(":6d:", content.getLine(1).equals("L4"));
		assertWithMessage(":6e:", content.getLine(2).equals(""));
		content.setText("\nL1\r\nL2");
		content.replaceTextRange(0, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7a:", newText.equals("L1\r\nL2"));
		assertWithMessage(":7b:", content.getLineCount() == 2);
		assertWithMessage(":7c:", content.getLine(0).equals("L1"));
		assertWithMessage(":7d:", content.getLine(1).equals("L2"));
		content.setText("\nL1\r\nL2\r\n");
		content.replaceTextRange(7, 2, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":8a:", newText.equals("\nL1\r\nL2"));
		assertWithMessage(":8b:", content.getLineCount() == 3);
		assertWithMessage(":8c:", content.getLine(0).equals(""));
		assertWithMessage(":8d:", content.getLine(1).equals("L1"));
		assertWithMessage(":8e:", content.getLine(2).equals("L2"));
		content.setText("\nLine 1\nLine 2\n");
		content.replaceTextRange(0, 7, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":9a:", newText.equals("\nLine 2\n"));
		assertWithMessage(":9b:", content.getLineCount() == 3);
		assertWithMessage(":9c:", content.getLine(0).equals(""));
		assertWithMessage(":9d:", content.getLine(1).equals("Line 2"));
		assertWithMessage(":9e:", content.getLine(2).equals(""));
		content.setText("Line 1\nLine 2\n");
		content.replaceTextRange(6, 8, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":10a:", newText.equals("Line 1"));
		assertWithMessage(":10b:", content.getLineCount() == 1);
		assertWithMessage(":10c:", content.getLine(0).equals("Line 1"));
		content.setText("Line one is short\r\nLine 2 is a longer line\r\nLine 3\n");
		content.replaceTextRange(12, 17, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":11a:", newText.equals("Line one is a longer line\r\nLine 3\n"));
		assertWithMessage(":11b:", content.getLineCount() == 3);
		assertWithMessage(":11c:", content.getLine(0).equals("Line one is a longer line"));
		assertWithMessage(":11d:", content.getLine(1).equals("Line 3"));
		assertWithMessage(":11e:", content.getLine(2).equals(""));
	}

	public void test_Empty() {
		StyledTextContent content = getContentInstance();
		assertWithMessage(":1a:", content.getLineCount() == 1);
		assertWithMessage(":1b:", content.getLine(0).equals(""));
		content.setText("test");
		content.replaceTextRange(0, 4, "");
		assertWithMessage(":2a:", content.getLineCount() == 1);
		assertWithMessage(":2b:", content.getLine(0).equals(""));
	}

	public void test_Insert() {
		StyledTextContent content = getContentInstance();
		String newText;
		content.setText("This\nis a test\r");
		content.replaceTextRange(0, 0, "test\n ");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":1a:", newText.equals("test\n This\nis a test\r"));
		assertWithMessage(":1b:", content.getLineCount() == 4);
		assertWithMessage(":1c:", content.getLine(0).equals("test"));
		assertWithMessage(":1d:", content.getLine(1).equals(" This"));
		assertWithMessage(":1e:", content.getLine(2).equals("is a test"));
		assertWithMessage(":1f:", content.getLine(3).equals(""));
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 0, "*** ");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":2a:", newText.equals("This\n*** is a test\r"));
		assertWithMessage(":2b:", content.getLineCount() == 3);
		assertWithMessage(":2c:", content.getLine(0).equals("This"));
		assertWithMessage(":2d:", content.getLine(1).equals("*** is a test"));
		assertWithMessage(":2e:", content.getLine(2).equals(""));
		content.setText("Line 1\r\nLine 2");
		content.replaceTextRange(0, 0, "\r");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3a:", newText.equals("\rLine 1\r\nLine 2"));
		assertWithMessage(":3b:", content.getLineCount() == 3);
		assertWithMessage(":3c:", content.getLine(0).equals(""));
		assertWithMessage(":3d:", content.getLine(1).equals("Line 1"));
		assertWithMessage(":3e:", content.getLine(2).equals("Line 2"));
		content.replaceTextRange(9, 0, "\r");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3f:", newText.equals("\rLine 1\r\n\rLine 2"));
		assertWithMessage(":3g:", content.getLineCount() == 4);
		assertWithMessage(":3h:", content.getLine(0).equals(""));
		assertWithMessage(":3i:", content.getLine(1).equals("Line 1"));
		assertWithMessage(":3j:", content.getLine(2).equals(""));
		assertWithMessage(":3k:", content.getLine(3).equals("Line 2"));
		content.setText("This\nis a test\r");
		content.replaceTextRange(0, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4a:", newText.equals("\nThis\nis a test\r"));
		assertWithMessage(":4b:", content.getLineCount() == 4);
		assertWithMessage(":4c:", content.getLine(0).equals(""));
		assertWithMessage(":4d:", content.getLine(1).equals("This"));
		assertWithMessage(":4e:", content.getLine(2).equals("is a test"));
		assertWithMessage(":4f:", content.getLine(3).equals(""));
		content.setText("This\nis a test\r");
		content.replaceTextRange(7, 0, "\r\nnewLine");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5a:", newText.equals("This\nis\r\nnewLine a test\r"));
		assertWithMessage(":5b:", content.getLineCount() == 4);
		assertWithMessage(":5c:", content.getLine(0).equals("This"));
		assertWithMessage(":5d:", content.getLine(1).equals("is"));
		assertWithMessage(":5e:", content.getLine(2).equals("newLine a test"));
		assertWithMessage(":5f:", content.getLine(3).equals(""));
		content.setText("");
		content.replaceTextRange(0, 0, "This\nis\r\nnewLine a test\r");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":6a:", newText.equals("This\nis\r\nnewLine a test\r"));
		assertWithMessage(":6b:", content.getLineCount() == 4);
		assertWithMessage(":6c:", content.getLine(0).equals("This"));
		assertWithMessage(":6d:", content.getLine(1).equals("is"));
		assertWithMessage(":6e:", content.getLine(2).equals("newLine a test"));
		assertWithMessage(":6f:", content.getLine(3).equals(""));
		// insert at end
		content.setText("This");
		content.replaceTextRange(4, 0, "\n ");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7a:", newText.equals("This\n "));
		assertWithMessage(":7b:", content.getLineCount() == 2);
		assertWithMessage(":7c:", content.getLine(0).equals("This"));
		assertWithMessage(":7d:", content.getLine(1).equals(" "));
		content.setText("This\n");
		content.replaceTextRange(5, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7e:", newText.equals("This\n\n"));
		assertWithMessage(":7f:", content.getLineCount() == 3);
		assertWithMessage(":7g:", content.getLine(0).equals("This"));
		assertWithMessage(":7h:", content.getLine(1).equals(""));
		assertWithMessage(":7i:", content.getLine(2).equals(""));
		// insert at beginning
		content.setText("This");
		content.replaceTextRange(0, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":8a:", newText.equals("\nThis"));
		assertWithMessage(":8b:", content.getLineCount() == 2);
		assertWithMessage(":8c:", content.getLine(0).equals(""));
		assertWithMessage(":8d:", content.getLine(1).equals("This"));
		// insert text
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 0, "*** ");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":9a:", newText.equals("This\n*** is a test\r"));
		assertWithMessage(":9b:", content.getLineCount() == 3);
		assertWithMessage(":9c:", content.getLine(0).equals("This"));
		assertWithMessage(":9d:", content.getLine(1).equals("*** is a test"));
		assertWithMessage(":9e:", content.getLine(2).equals(""));
		content.setText("This\n");
		content.replaceTextRange(5, 0, "line");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":10a:", newText.equals("This\nline"));
		assertWithMessage(":10b:", content.getLineCount() == 2);
		assertWithMessage(":10c:", content.getLine(0).equals("This"));
		assertWithMessage(":10d:", content.getLine(1).equals("line"));
		assertWithMessage(":10e:", content.getLineAtOffset(8) == 1);
		assertWithMessage(":10f:", content.getLineAtOffset(9) == 1);
		// insert at beginning
		content.setText("This\n");
		content.replaceTextRange(0, 0, "line\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":11a:", newText.equals("line\nThis\n"));
		assertWithMessage(":11b:", content.getLineCount() == 3);
		assertWithMessage(":11c:", content.getLine(0).equals("line"));
		assertWithMessage(":11d:", content.getLine(1).equals("This"));
		assertWithMessage(":11e:", content.getLineAtOffset(5) == 1);
		content.setText("Line 1\r\nLine 2\r\nLine 3");
		content.replaceTextRange(0, 0, "\r");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":12a:", newText.equals("\rLine 1\r\nLine 2\r\nLine 3"));
		assertWithMessage(":12b:", content.getLineCount() == 4);
		assertWithMessage(":12c:", content.getLine(0).equals(""));
		assertWithMessage(":12d:", content.getLine(1).equals("Line 1"));
		assertWithMessage(":12e:", content.getLine(2).equals("Line 2"));
		assertWithMessage(":12f:", content.getLine(3).equals("Line 3"));
		content.setText("Line 1\nLine 2\nLine 3");
		content.replaceTextRange(7, 0, "Line1a\nLine1b\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":13a:", newText.equals("Line 1\nLine1a\nLine1b\nLine 2\nLine 3"));
		assertWithMessage(":13b:", content.getLineCount() == 5);
		assertWithMessage(":13c:", content.getLine(0).equals("Line 1"));
		assertWithMessage(":13d:", content.getLine(1).equals("Line1a"));
		assertWithMessage(":13e:", content.getLine(2).equals("Line1b"));
		assertWithMessage(":13f:", content.getLine(3).equals("Line 2"));
		assertWithMessage(":13g:", content.getLine(4).equals("Line 3"));
		content.setText("Line 1\nLine 2\nLine 3");
		content.replaceTextRange(11, 0, "l1a");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":14a:", newText.equals("Line 1\nLinel1a 2\nLine 3"));
		assertWithMessage(":14b:", content.getLineCount() == 3);
		assertWithMessage(":14c:", content.getLine(0).equals("Line 1"));
		assertWithMessage(":14d:", content.getLine(1).equals("Linel1a 2"));
		assertWithMessage(":14e:", content.getLine(2).equals("Line 3"));
		content.setText("Line 1\nLine 2 is a very long line that spans many words\nLine 3");
		content.replaceTextRange(19, 0, "very, very, ");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":15a:", newText.equals("Line 1\nLine 2 is a very, very, very long line that spans many words\nLine 3"));
		assertWithMessage(":15b:", content.getLineCount() == 3);
		assertWithMessage(":15c:", content.getLine(0).equals("Line 1"));
		assertWithMessage(":15d:", content.getLine(1).equals("Line 2 is a very, very, very long line that spans many words"));
		assertWithMessage(":15e:", content.getLine(2).equals("Line 3"));
	}

	public void test_Line_Conversion() {
		StyledTextContent content = getContentInstance();
		content.setText("This\nis a test\rrepeat\nend\r");
		assertWithMessage(":1a:", content.getLineCount() == 5);
		assertWithMessage(":1b:", content.getLine(0).equals("This"));
		assertWithMessage(":1c:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":1d:", content.getLine(1).equals("is a test"));
		assertWithMessage(":1e:", content.getLineAtOffset(4) == 0);
		assertWithMessage(":1f:", content.getOffsetAtLine(1) == 5);
		assertWithMessage(":1g:", content.getLine(2).equals("repeat"));
		assertWithMessage(":1h:", content.getOffsetAtLine(2) == 15);
		assertWithMessage(":1i:", content.getLine(3).equals("end"));
		assertWithMessage(":1j:", content.getOffsetAtLine(3) == 22);
		assertWithMessage(":1k:", content.getLine(4).equals(""));
		assertWithMessage(":1l:", content.getOffsetAtLine(4) == 26);
		content.setText("This\r\nis a test");
		assertWithMessage(":2a:", content.getLineCount() == 2);
		assertWithMessage(":2b:", content.getLine(1).equals("is a test"));
		assertWithMessage(":2c:", content.getLineAtOffset(4) == 0);
		assertWithMessage(":2d:", content.getLineAtOffset(5) == 0);
		content.setText("This\r\nis a test\r");
		assertWithMessage(":3a:", content.getLineCount() == 3);
		assertWithMessage(":3b:", content.getLine(1).equals("is a test"));
		assertWithMessage(":3c:", content.getLineAtOffset(15) == 1);
		content.setText("\r\n");
		assertWithMessage(":4a:", content.getLineCount() == 2);
		assertWithMessage(":4b:", content.getLine(0).equals(""));
		assertWithMessage(":4c:", content.getLine(1).equals(""));
		assertWithMessage(":4d:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":4e:", content.getLineAtOffset(1) == 0);
		assertWithMessage(":4f:", content.getLineAtOffset(2) == 1);
		content.setText("\r\n\n\r\r\n");
		assertWithMessage(":5a:", content.getLineCount() == 5);
		assertWithMessage(":5b:", content.getLine(0).equals(""));
		assertWithMessage(":5c:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":5d:", content.getLine(1).equals(""));
		assertWithMessage(":5e:", content.getOffsetAtLine(1) == 2);
		assertWithMessage(":5f:", content.getLine(2).equals(""));
		assertWithMessage(":5g:", content.getOffsetAtLine(2) == 3);
		assertWithMessage(":5h:", content.getLine(3).equals(""));
		assertWithMessage(":5i:", content.getOffsetAtLine(3) == 4);
		assertWithMessage(":5j:", content.getLine(4).equals(""));
		assertWithMessage(":5k:", content.getOffsetAtLine(4) == 6);
		content.setText("test\r\rtest2\r\r");
		assertWithMessage(":6a:", content.getLineCount() == 5);
		assertWithMessage(":6b:", content.getLine(0).equals("test"));
		assertWithMessage(":6c:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":6d:", content.getLine(1).equals(""));
		assertWithMessage(":6e:", content.getOffsetAtLine(1) == 5);
		assertWithMessage(":6f:", content.getLine(2).equals("test2"));
		assertWithMessage(":6g:", content.getOffsetAtLine(2) == 6);
		assertWithMessage(":6h:", content.getLine(3).equals(""));
		assertWithMessage(":6i:", content.getOffsetAtLine(3) == 12);
		assertWithMessage(":6j:", content.getLine(4).equals(""));
		assertWithMessage(":6k:", content.getOffsetAtLine(4) == 13);
	}

	public void test_Line_To_Offset() {
		StyledTextContent content = getContentInstance();
		content.setText("This\nis a test\rrepeat\nend\r");
		assertWithMessage(":1a:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":1b:", content.getOffsetAtLine(1) == 5);
		assertWithMessage(":1c:", content.getOffsetAtLine(2) == 15);
		assertWithMessage(":1d:", content.getOffsetAtLine(3) == 22);
		assertWithMessage(":1e:", content.getOffsetAtLine(4) == 26);
		content.setText("This\r\nis a test");
		assertWithMessage(":2a:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":2b:", content.getOffsetAtLine(1) == 6);
		content.setText("\r\n");
		assertWithMessage(":3a:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":3b:", content.getOffsetAtLine(1) == 2);
		content.setText("\r\n\n\r\r\n");
		assertWithMessage(":4a:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":4b:", content.getOffsetAtLine(1) == 2);
		assertWithMessage(":4c:", content.getOffsetAtLine(2) == 3);
		assertWithMessage(":4d:", content.getOffsetAtLine(3) == 4);
		assertWithMessage(":4e:", content.getOffsetAtLine(4) == 6);
		content.setText("\r\ntest\r\n");
		assertWithMessage(":5a:", content.getOffsetAtLine(0) == 0);
		assertWithMessage(":5b:", content.getOffsetAtLine(1) == 2);
		assertWithMessage(":5c:", content.getOffsetAtLine(2) == 8);
	}

	public void test_Offset_To_Line() {
		StyledTextContent content = getContentInstance();
		content.setText("This\nis a test\rrepeat\nend\r");
		assertWithMessage(":1a:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":1b:", content.getLineAtOffset(3) == 0);
		assertWithMessage(":1c:", content.getLineAtOffset(4) == 0);
		assertWithMessage(":1d:", content.getLineAtOffset(25) == 3);
		assertWithMessage(":1e:", content.getLineAtOffset(26) == 4);
		content.setText("This\r\nis a test");
		assertWithMessage(":2a:", content.getLineAtOffset(5) == 0);
		assertWithMessage(":2b:", content.getLineAtOffset(6) == 1);
		assertWithMessage(":2c:", content.getLineAtOffset(10) == 1);
		content.setText("\r\n");
		assertWithMessage(":3a:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":3b:", content.getLineAtOffset(1) == 0);
		assertWithMessage(":3c:", content.getLineAtOffset(2) == 1);
		content.setText("\r\n\n\r\r\n");
		assertWithMessage(":4a:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":4b:", content.getLineAtOffset(1) == 0);
		assertWithMessage(":4c:", content.getLineAtOffset(2) == 1);
		assertWithMessage(":4d:", content.getLineAtOffset(3) == 2);
		assertWithMessage(":4e:", content.getLineAtOffset(4) == 3);
		assertWithMessage(":4f:", content.getLineAtOffset(5) == 3);
		assertWithMessage(":4g:", content.getLineAtOffset(6) == 4);
		content.setText("\r\n\r\n");
		assertWithMessage(":5a:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":5b:", content.getLineAtOffset(1) == 0);
		assertWithMessage(":5c:", content.getLineAtOffset(2) == 1);
		assertWithMessage(":5d:", content.getLineAtOffset(3) == 1);
		assertWithMessage(":5e:", content.getLineAtOffset(4) == 2);
		content.setText("\r\r\r\n\r\n");
		assertWithMessage(":6a:", content.getLineAtOffset(0) == 0);
		assertWithMessage(":6b:", content.getLineAtOffset(1) == 1);
		assertWithMessage(":6c:", content.getLineAtOffset(2) == 2);
		assertWithMessage(":6d:", content.getLineAtOffset(4) == 3);
		content.setText("");
		assertWithMessage(":7a:", content.getLineAtOffset(0) == 0);
		content = getContentInstance();
		assertWithMessage(":8a:", content.getLineAtOffset(0) == 0);
	}

	public void test_Replace() {
		StyledTextContent content = getContentInstance();
		String newText;
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 4, "a");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":1a:", newText.equals("This\na test\r"));
		assertWithMessage(":1b:", content.getLineCount() == 3);
		assertWithMessage(":1c:", content.getLine(0).equals("This"));
		assertWithMessage(":1d:", content.getLine(1).equals("a test"));
		assertWithMessage(":1e:", content.getLine(2).equals(""));
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 2, "was");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":2a:", newText.equals("This\nwas a test\r"));
		assertWithMessage(":2b:", content.getLineCount() == 3);
		assertWithMessage(":2c:", content.getLine(0).equals("This"));
		assertWithMessage(":2d:", content.getLine(1).equals("was a test"));
		assertWithMessage(":2e:", content.getLine(2).equals(""));
		content.setText("This is a test\r");
		content.replaceTextRange(5, 2, "was");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3a:", newText.equals("This was a test\r"));
		assertWithMessage(":3b:", content.getLineCount() == 2);
		assertWithMessage(":3c:", content.getLine(0).equals("This was a test"));
		assertWithMessage(":3d:", content.getLineAtOffset(15) == 0);
		content.setText("Line 1\nLine 2\nLine 3");
		content.replaceTextRange(0, 7, "La\nLb\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4a:", newText.equals("La\nLb\nLine 2\nLine 3"));
		assertWithMessage(":4b:", content.getLine(0).equals("La"));
		assertWithMessage(":4c:", content.getLine(1).equals("Lb"));
		assertWithMessage(":4d:", content.getLine(2).equals("Line 2"));
		assertWithMessage(":4e:", content.getLine(3).equals("Line 3"));
		content.setText(getTestText());
		newText = content.getTextRange(0, content.getCharCount());
		int start = content.getOffsetAtLine(6);
		int end = content.getOffsetAtLine(11);
		content.replaceTextRange(start, end - start, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5a:", content.getLineCount() == 12);
		assertWithMessage(":5a:", content.getLine(5).equals(""));
		assertWithMessage(":5a:", content.getLine(6).equals(""));
		start = content.getOffsetAtLine(7);
		content.replaceTextRange(start, content.getCharCount() - start, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5a:", content.getLineCount() == 8);
		assertWithMessage(":5a:", content.getLine(5).equals(""));
		assertWithMessage(":5a:", content.getLine(6).equals(""));
		assertWithMessage(":5a:", content.getLine(7).equals(""));
	}

	public void test_Special_Cases() {
		String newText;
		StyledTextContent content = getContentInstance();
		assertWithMessage(":0a:", content.getLineCount() == 1);
		assertWithMessage(":0b:", content.getOffsetAtLine(0) == 0);
		content.setText("This is the input/output text component.");
		content.replaceTextRange(0, 0, "\n");
		assertWithMessage(":1a:", content.getLine(0).equals(""));
		content.replaceTextRange(1, 0, "\n");
		assertWithMessage(":1b:", content.getLine(0).equals(""));
		content.replaceTextRange(2, 0, "\n");
		assertWithMessage(":1c:", content.getLine(0).equals(""));
		content.replaceTextRange(3, 0, "\n");
		assertWithMessage(":1d:", content.getLine(0).equals(""));
		content.replaceTextRange(4, 0, "\n");
		assertWithMessage(":1e:", content.getLine(0).equals(""));
		content.replaceTextRange(5, 0, "\n");
		assertWithMessage(":1f:", content.getLine(0).equals(""));
		content.replaceTextRange(6, 0, "\n");
		assertWithMessage(":1g:", content.getLine(0).equals(""));
		content.replaceTextRange(7, 0, "\n");
		assertWithMessage(":1h:", content.getLine(0).equals(""));
		content.setText("This is the input/output text component.");
		content.replaceTextRange(0, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":2a:", newText.equals("\nThis is the input/output text component."));
		assertWithMessage(":2b:", content.getLine(0).equals(""));
		assertWithMessage(":2c:", content.getLine(1).equals("This is the input/output text component."));
		content.replaceTextRange(1, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":2d:", newText.equals("\n\nThis is the input/output text component."));
		assertWithMessage(":2e:", content.getLine(0).equals(""));
		assertWithMessage(":2f:", content.getLine(1).equals(""));
		assertWithMessage(":2g:", content.getLine(2).equals("This is the input/output text component."));
		content.replaceTextRange(2, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3a:", newText.equals("\n\n\nThis is the input/output text component."));
		assertWithMessage(":3b:", content.getLine(0).equals(""));
		assertWithMessage(":3c:", content.getLine(1).equals(""));
		assertWithMessage(":3d:", content.getLine(2).equals(""));
		assertWithMessage(":3e:", content.getLine(3).equals("This is the input/output text component."));
		content.replaceTextRange(3, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":3f:", newText.equals("\n\n\n\nThis is the input/output text component."));
		assertWithMessage(":3g:", content.getLine(0).equals(""));
		assertWithMessage(":3h:", content.getLine(1).equals(""));
		assertWithMessage(":3i:", content.getLine(2).equals(""));
		assertWithMessage(":3j:", content.getLine(3).equals(""));
		assertWithMessage(":3k:", content.getLine(4).equals("This is the input/output text component."));
		content.replaceTextRange(3, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4a:", newText.equals("\n\n\nThis is the input/output text component."));
		assertWithMessage(":4b:", content.getLine(0).equals(""));
		assertWithMessage(":4c:", content.getLine(1).equals(""));
		assertWithMessage(":4d:", content.getLine(2).equals(""));
		assertWithMessage(":4e:", content.getLine(3).equals("This is the input/output text component."));
		content.replaceTextRange(2, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":4f:", newText.equals("\n\nThis is the input/output text component."));
		assertWithMessage(":4g:", content.getLine(0).equals(""));
		assertWithMessage(":4h:", content.getLine(1).equals(""));
		assertWithMessage(":4i:", content.getLine(2).equals("This is the input/output text component."));
		content.replaceTextRange(2, 0, "a");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5a:", newText.equals("\n\naThis is the input/output text component."));
		assertWithMessage(":5b:", content.getLine(0).equals(""));
		assertWithMessage(":5c:", content.getLine(1).equals(""));
		assertWithMessage(":5d:", content.getLine(2).equals("aThis is the input/output text component."));
		// delete only part of a delimiter
		content.setText("L1\r\nL2\r\nL3\r\nL4\r\n");
		content.replaceTextRange(0, 3, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":5e:", newText.equals("\nL2\r\nL3\r\nL4\r\n"));
		assertWithMessage(":5f:", content.getLineCount() == 5);
		assertWithMessage(":5g:", content.getLine(0).equals(""));
		assertWithMessage(":5h:", content.getLine(1).equals("L2"));
		assertWithMessage(":5i:", content.getLine(2).equals("L3"));
		assertWithMessage(":5j:", content.getLine(3).equals("L4"));
		assertWithMessage(":5k:", content.getLine(4).equals(""));
		// replace part of a delimiter
		content.setText("L1\r\nL2\r\nL3\r\nL4\r\n");
		content.replaceTextRange(3, 1, "test\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":6a:", newText.equals("L1\rtest\nL2\r\nL3\r\nL4\r\n"));
		assertWithMessage(":6b:", content.getLineCount() == 6);
		assertWithMessage(":6c:", content.getLine(0).equals("L1"));
		assertWithMessage(":6d:", content.getLine(1).equals("test"));
		assertWithMessage(":6e:", content.getLine(2).equals("L2"));
		assertWithMessage(":6f:", content.getLine(3).equals("L3"));
		assertWithMessage(":6g:", content.getLine(4).equals("L4"));
		assertWithMessage(":6h:", content.getLine(5).equals(""));
		content.setText("abc\r\ndef");
		content.replaceTextRange(1, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7a:", newText.equals("ac\r\ndef"));
		assertWithMessage(":7b:", content.getLineCount() == 2);
		assertWithMessage(":7c:", content.getLine(0).equals("ac"));
		assertWithMessage(":7d:", content.getLine(1).equals("def"));
		content.replaceTextRange(1, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7e:", newText.equals("a\r\ndef"));
		assertWithMessage(":7f:", content.getLineCount() == 2);
		assertWithMessage(":7g:", content.getLine(0).equals("a"));
		assertWithMessage(":7h:", content.getLine(1).equals("def"));
		content.replaceTextRange(1, 2, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7i:", newText.equals("adef"));
		assertWithMessage(":7j:", content.getLineCount() == 1);
		assertWithMessage(":7k:", content.getLine(0).equals("adef"));
		content.replaceTextRange(1, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7l:", newText.equals("aef"));
		assertWithMessage(":7m:", content.getLineCount() == 1);
		assertWithMessage(":7n:", content.getLine(0).equals("aef"));
		content.replaceTextRange(1, 1, "");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":7o:", newText.equals("af"));
		assertWithMessage(":7p:", content.getLineCount() == 1);
		assertWithMessage(":7q:", content.getLine(0).equals("af"));
		content.setText("abc");
		content.replaceTextRange(0, 1, "1");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":8a:", content.getLineCount() == 1);
		assertWithMessage(":8b:", newText.equals("1bc"));
		assertWithMessage(":8c:", content.getLine(0).equals("1bc"));
		content.replaceTextRange(0, 0, "\n");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":8d:", newText.equals("\n1bc"));
		assertWithMessage(":8e:", content.getLineCount() == 2);
		assertWithMessage(":8f:", content.getLine(0).equals(""));
		assertWithMessage(":8g:", content.getLine(1).equals("1bc"));
		content = getContentInstance();
		content.replaceTextRange(0, 0, "a");
		content.setText("package test;\n/* Line 1\n * Line 2\n */\npublic class SimpleClass {\n}");
		content.replaceTextRange(14, 23, "\t/*Line 1\n\t * Line 2\n\t */");
		newText = content.getTextRange(0, content.getCharCount());
		assertWithMessage(":9a:", newText.equals("package test;\n\t/*Line 1\n\t * Line 2\n\t */\npublic class SimpleClass {\n}"));
		assertWithMessage(":9b:", content.getLineCount() == 6);
		assertWithMessage(":9c:", content.getLine(0).equals("package test;"));
		assertWithMessage(":9d:", content.getLine(1).equals("\t/*Line 1"));
		assertWithMessage(":9e:", content.getLine(2).equals("\t * Line 2"));
		assertWithMessage(":9f:", content.getLine(3).equals("\t */"));
		assertWithMessage(":9g:", content.getLine(4).equals("public class SimpleClass {"));
		assertWithMessage(":9h:", content.getLine(5).equals("}"));
	}

	public void test_Text_Changed_Event() {
		StyledTextContent content = getContentInstance();
		content.addTextChangeListener(this);
		verify = 1;
		content.setText("testing");
		content.replaceTextRange(0, 0, "\n");
		verify = 2;
		content.setText("\n\n");
		content.replaceTextRange(0, 2, "a");
		verify = 3;
		content.setText("a");
		content.replaceTextRange(0, 1, "\n\n");
		verify = 5;
		content.setText("Line 1\r\nLine 2");
		content.replaceTextRange(0, 0, "\r");
		verify = 6;
		content.setText("This\nis a test\nline 3\nline 4");
		content.replaceTextRange(21, 7, "");
		verify = 7;
		content.setText("This\nis a test\r");
		content.replaceTextRange(5, 9, "");
		verify = 8;
		content.setText("\nL1\r\nL2\r\n");
		content.replaceTextRange(7, 2, "");
		verify = 9;
		content.setText("L1\r\n");
		content.replaceTextRange(2, 2, "test");
		verify = 0;
		content.removeTextChangeListener(this);
	}

	public void textChanged(TextChangedEvent event) {
	}

	public void textChanging(TextChangingEvent event) {
		switch (verify) {
			case 1 :
				{
					assertWithMessage(":1a:", event.replaceLineCount == 0);
					assertWithMessage(":1b:", event.newLineCount == 1);
					break;
				}
			case 2 :
				{
					assertWithMessage(":2a:", event.replaceLineCount == 2);
					assertWithMessage(":2b:", event.newLineCount == 0);
					break;
				}
			case 3 :
				{
					assertWithMessage(":3a:", event.replaceLineCount == 0);
					assertWithMessage(":3b:", event.newLineCount == 2);
					break;
				}
			case 4 :
				{
					assertWithMessage(":4a:", event.replaceLineCount == 0);
					assertWithMessage(":4b:", event.newLineCount == 1);
					break;
				}
			case 5 :
				{
					assertWithMessage(":5a:", event.replaceLineCount == 0);
					assertWithMessage(":5b:", event.newLineCount == 1);
					break;
				}
			case 6 :
				{
					assertWithMessage(":6a:", event.replaceLineCount == 1);
					assertWithMessage(":6b:", event.newLineCount == 0);
					break;
				}
			case 8 :
				{
					assertWithMessage(":8a:", event.replaceLineCount == 1);
					assertWithMessage(":8b:", event.newLineCount == 0);
					break;
				}
			case 9 :
				{
					assertWithMessage(":9a:", event.replaceLineCount == 1);
					assertWithMessage(":9b:", event.newLineCount == 0);
					break;
				}
			case 10 :
				{
					assertWithMessage(":10a:", event.replaceLineCount == 0);
					assertWithMessage(":10b:", event.newLineCount == 0);
					break;
				}
			case 11 :
				{
					assertWithMessage(":11a:", event.replaceLineCount == 0);
					assertWithMessage(":11b:", event.newLineCount == 0);
					break;
				}
			case 12 :
				{
					assertWithMessage(":12a:", event.replaceLineCount == 0);
					assertWithMessage(":12b:", event.newLineCount == 0);
					break;
				}
			case 13 :
				{
					assertWithMessage(":13a:", event.replaceLineCount == 0);
					assertWithMessage(":13b:", event.newLineCount == 0);
					break;
				}
			case 14 :
				{
					assertWithMessage(":14a:", event.replaceLineCount == 0);
					assertWithMessage(":14b:", event.newLineCount == 1);
					break;
				}
			case 15 :
				{
					assertWithMessage(":15a:", event.replaceLineCount == 1);
					assertWithMessage(":15b:", event.newLineCount == 2);
					break;
				}
			case 16 :
				{
					assertWithMessage(":16a:", event.replaceLineCount == 0);
					assertWithMessage(":16b:", event.newLineCount == 1);
					break;
				}
			case 17 :
				{
					assertWithMessage(":17a:", event.replaceLineCount == 0);
					assertWithMessage(":17b:", event.newLineCount == 2);
					break;
				}
			case 18 :
				{
					assertWithMessage(":18a:", event.replaceLineCount == 0);
					assertWithMessage(":18b:", event.newLineCount == 1);
					break;
				}
			case 19 :
				{
					assertWithMessage(":19a:", event.replaceLineCount == 0);
					assertWithMessage(":19b:", event.newLineCount == 4);
					break;
				}
			case 20 :
				{
					assertWithMessage(":20a:", event.replaceLineCount == 0);
					assertWithMessage(":20b:", event.newLineCount == 2);
					break;
				}
		}
	}

	public void textSet(TextChangedEvent event) {
	}
}
