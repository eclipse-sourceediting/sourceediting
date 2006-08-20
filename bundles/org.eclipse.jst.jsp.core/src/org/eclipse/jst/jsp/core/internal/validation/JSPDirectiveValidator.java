/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Checks for: - duplicate taglib prefix values and reserved taglib prefix
 * values in the same file
 * 
 */
public class JSPDirectiveValidator extends JSPValidator {

	private HashMap fReservedPrefixes = new HashMap();
	private HashMap fTaglibPrefixesInUse = new HashMap();
	private HashMap fTextRegionToDocumentRegionMap = new HashMap();

	public JSPDirectiveValidator() {
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("java", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("javax", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("servlet", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sun", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sunw", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	public void cleanup(IReporter reporter) {
		super.cleanup(reporter);
		fTaglibPrefixesInUse.clear();
		fTextRegionToDocumentRegionMap.clear();
	}

	private void collectPrefix(IStructuredDocumentRegion documentRegion, ITextRegion valueRegion, String taglibPrefix) {
		fTextRegionToDocumentRegionMap.put(valueRegion, documentRegion);

		Object o = fTaglibPrefixesInUse.get(taglibPrefix);
		if (o == null) {
			// prefix doesn't exist, remember it
			fTaglibPrefixesInUse.put(taglibPrefix, valueRegion);
		}
		else {
			List regionList = null;
			// already a List
			if (o instanceof List) {
				regionList = (List) o;
			}
			/*
			 * a single value region, create a new List and add previous
			 * valueRegion
			 */
			else {
				regionList = new ArrayList();
				regionList.add(o);
				fTaglibPrefixesInUse.put(taglibPrefix, regionList);
			}
			regionList.add(valueRegion);
		}
	}

	private boolean isReservedPrefix(String name) {
		return fReservedPrefixes.get(name) != null;
	}

	private void processDirective(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion documentRegion) {
		String directiveName = getDirectiveName(documentRegion);
		// we only care about taglib directive
		if (directiveName.equals("taglib")) { //$NON-NLS-1$

			ITextRegion valueRegion = getAttributeValueRegion(documentRegion, JSP11Namespace.ATTR_NAME_PREFIX);
			if (valueRegion == null)
				return;

			String taglibPrefix = documentRegion.getText(valueRegion);
			taglibPrefix = StringUtils.stripQuotes(taglibPrefix);

			collectPrefix(documentRegion, valueRegion, taglibPrefix);

			// check for the use of reserved prefixes
			if (isReservedPrefix(taglibPrefix)) {
				String msgText = JSPCoreMessages.JSPDirectiveValidator_0 + taglibPrefix + "'"; //$NON-NLS-2$ //$NON-NLS-1$
				int sev = IMessage.HIGH_SEVERITY;
				LocalizedMessage message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
				int start = documentRegion.getStartOffset(valueRegion);
				int length = documentRegion.getTextLength();
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(this, message);
			}
		}
	}

	private void reportDuplicatePrefixes(IFile file, IReporter reporter, IStructuredDocument document) {
		String[] prefixes = (String[]) fTaglibPrefixesInUse.keySet().toArray(new String[0]);
		for (int prefixNumber = 0; prefixNumber < prefixes.length; prefixNumber++) {
			Object o = fTaglibPrefixesInUse.get(prefixes[prefixNumber]);
			/*
			 * Only care if it's a List (because there was more than one
			 * directive with that prefix)
			 */
			if (o instanceof List) {
				List valueRegions = (List) o;

				int sev = IMessage.HIGH_SEVERITY;
				String msgText = JSPCoreMessages.JSPDirectiveValidator_2 + prefixes[prefixNumber] + "'"; //$NON-NLS-2$ //$NON-NLS-1$

				// Report an error in all directives using this prefix
				for (int regionNumber = 0; regionNumber < valueRegions.size(); regionNumber++) {
					ITextRegion valueRegion = (ITextRegion) valueRegions.get(regionNumber);
					IStructuredDocumentRegion documentRegion = (IStructuredDocumentRegion) fTextRegionToDocumentRegionMap.get(valueRegion);
					LocalizedMessage message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
					message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));

					// if there's a message, there was an error found
					if (message != null) {
						int start = documentRegion.getStartOffset(valueRegion);
						int length = valueRegion.getTextLength();
						int lineNo = document.getLineOfOffset(start);
						message.setLineNo(lineNo);
						message.setOffset(start);
						message.setLength(length);

						reporter.addMessage(this, message);
					}
				}
			}
		}
	}

	/**
	 * batch validation call
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		// when validating an entire file
		// need to clear dupes or else you're comparing between files
		fTaglibPrefixesInUse.clear();
		reporter.removeAllMessages(this);

		// for batch validation
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForRead(f);
			if (sModel != null) {
				IStructuredDocument structuredDocument = sModel.getStructuredDocument();
				// iterate all document regions
				IStructuredDocumentRegion region = structuredDocument.getFirstStructuredDocumentRegion();
				while (region != null && !reporter.isCancelled()) {
					// only checking directives
					if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
						processDirective(reporter, f, structuredDocument, region);
					}
					region = region.getNext();
				}

				reportDuplicatePrefixes(f, reporter, structuredDocument);
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}
}
