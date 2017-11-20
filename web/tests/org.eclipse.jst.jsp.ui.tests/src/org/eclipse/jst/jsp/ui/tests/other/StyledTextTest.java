/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.other;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Insert the type's description here.
 * Creation date: (3/20/2001 6:19:58 PM)
 * @author: David Williams
 */
public class StyledTextTest {
	/**
	 * StyledTextTest constructor comment.
	 */
	public StyledTextTest() {
		super();
	}

	/**
	 * Don't forget, the *swt*.dll will have to be copied to program directory for this to work.
	 */
	public static void main(String[] args) {

		try {
			Shell shell = new Shell();
			GridLayout layout = new GridLayout();
			shell.setLayout(layout);

			Button button = new Button(shell, SWT.PUSH);
			button.setText("Action");
			button.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					System.out.println("button pushed");
				}
			});

			StyledText text = new StyledText(shell, SWT.BORDER + SWT.H_SCROLL + SWT.V_SCROLL);
			GridData data = new GridData(GridData.FILL_BOTH);
			text.setLayoutData(data);

			//
			//		String fileName = System.getProperty("user.dir") + "\\" + "japanese_utf-16.txt";
			//		String encoding = "UnicodeBig";

			String fileName = System.getProperty("user.dir") + "\\" + "japanese_cp1252.txt";
			String encoding = "Cp1252";
			//sun.io.ByteToCharConverter.getDefault().toString();

			System.out.println("Test file: " + fileName);
			System.out.println("   Encoding: " + encoding);

			InputStream in = new FileInputStream(fileName);
			InputStreamReader inStream = new InputStreamReader(in, encoding);
			//Reader inStream = new FileReader(fileName);
			StringBuffer sb = new StringBuffer();
			while (inStream.ready()) {
				sb.append((char) inStream.read());
			}
			String inputString = sb.toString();
			System.out.println("Length of input: " + inputString.length());
			//
			text.setText(inputString);

			shell.setSize(400, 200);
			shell.open();

			Display display = shell.getDisplay();
			while (shell != null && !shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
		catch (Exception t) {
			t.printStackTrace();
		}
	}
}