/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.internal.correction;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.correction.IQuickAssistProcessor;
import org.eclipse.wst.sse.ui.internal.correction.IQuickFixProcessor;
import org.eclipse.wst.sse.ui.internal.correction.StructuredCorrectionProcessor;

public class CorrectionProcessorXML extends StructuredCorrectionProcessor {
	protected IQuickFixProcessor fQuickFixProcessor;
	protected IQuickAssistProcessor fQuickAssistProcessor;

	public CorrectionProcessorXML(ITextEditor editor) {
		super(editor);
	}

	protected IQuickFixProcessor getQuickFixProcessor() {
		if (fQuickFixProcessor == null)
			fQuickFixProcessor = new QuickFixProcessorXML();

		return fQuickFixProcessor;
	}

	protected IQuickAssistProcessor getQuickAssistProcessor() {
		if (fQuickAssistProcessor == null)
			fQuickAssistProcessor = new QuickAssistProcessorXML();

		return fQuickAssistProcessor;
	}
}
