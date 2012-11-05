/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.format;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.XMLFormattingStrategy;

public class StructuredFormattingStrategyJSP extends ContextBasedFormattingStrategy {

	private static final String XMLNS_ATTR = "xmlns:jsp"; //$NON-NLS-1$
	private static final String NAMESPACE = "http://java.sun.com/JSP/Page"; //$NON-NLS-1$

	private XMLFormattingStrategy xmlStrategy;
	private ContextBasedFormattingStrategy htmlStrategy;

	private IDocument fDocument;
	private ContextBasedFormattingStrategy fStrategy;

	public StructuredFormattingStrategyJSP() {
		xmlStrategy = new XMLFormattingStrategy();
		htmlStrategy = new StructuredFormattingStrategy(new HTMLFormatProcessorImpl());
	}
	public void formatterStarts(IFormattingContext context) {
		super.formatterStarts(context);
		fDocument = (IDocument) context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM);
		getFormattingStrategy().formatterStarts(context);
	}

	public void format() {
		super.format();
		getFormattingStrategy().format();
	}

	public void formatterStops() {
		super.formatterStops();
		getFormattingStrategy().formatterStops();
		fDocument = null;
		fStrategy = null;
	}

	protected ContextBasedFormattingStrategy getFormattingStrategy() {
		IDOMModel model = null;

		if(fStrategy != null)
			return fStrategy;

		if (fDocument == null || JSPUIPlugin.getInstance().getPreferenceStore().getBoolean(JSPUIPreferenceNames.USE_HTML_FORMATTER)) {
			fStrategy = htmlStrategy;
		}
		else {
			/* Always release the model */
			model = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			try {
				String attr = null;
				
				/* Check the xmlns:jsp attribute to determine if it is XML content */
				if(model != null && model.getDocument() != null)
					attr = model.getDocument().getDocumentElement().getAttribute(XMLNS_ATTR);
				
				if(NAMESPACE.equals(attr))
					fStrategy = xmlStrategy;
				else
					fStrategy = htmlStrategy;
			}
			finally {
				if (model != null)
					model.releaseFromRead();
			}
		}
		return fStrategy;
	}

}
