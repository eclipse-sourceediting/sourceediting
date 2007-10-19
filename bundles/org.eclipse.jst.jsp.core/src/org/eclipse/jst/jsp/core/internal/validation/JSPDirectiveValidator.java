/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
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
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

import com.ibm.icu.text.Collator;

/**
 * Checks for: - duplicate taglib prefix values - reserved taglib prefix
 * values
 * 
 */
public class JSPDirectiveValidator extends JSPValidator implements ISourceValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$

	private static Collator collator = Collator.getInstance(Locale.US);
	private HashMap fReservedPrefixes = new HashMap();
	private HashMap fDuplicatePrefixes = new HashMap();
	private IDocument fDocument;

	private IValidator fMessageOriginator;
	private IFile fFile;
	private boolean fEnableSourceValidation = true;

	public JSPDirectiveValidator(){
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("java", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("javax", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("servlet", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sun", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sunw", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		
		this.fMessageOriginator = this;
	}


	public JSPDirectiveValidator(IValidator validator){
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("java", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("javax", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("servlet", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sun", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sunw", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		this.fMessageOriginator = validator;
	}
	/**
	 * batch validation call
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		// for batch validation
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForRead(f);
			if (sModel != null) {
				/* remove old messages (compatibility requirement) */
				reporter.removeAllMessages(fMessageOriginator, f);
				performValidation(f, reporter, sModel.getStructuredDocument());
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


	protected void performValidation(IFile f, IReporter reporter, IStructuredDocument sDoc) {
		/*
		 * when validating an entire file need to clear dupes or else you're
		 * comparing between files
		 */
		fDuplicatePrefixes.clear();

		// need to set this for partial validate call to work
		fDocument = sDoc;
		// iterate all document regions
		IStructuredDocumentRegion region = sDoc
				.getFirstStructuredDocumentRegion();
		while (region != null && !reporter.isCancelled()) {
			// only checking directives
			if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				validateDirective(reporter, f, sDoc, region);
			}
			region = region.getNext();
		}
		fDuplicatePrefixes.clear();
		fDocument = null;
	}

	public void connect(IDocument document) {
		fDuplicatePrefixes.clear();
		fDocument = document;

		IStructuredModel model = null;
		IFile file = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (model != null) {
				String baseLocation = model.getBaseLocation();
				// The baseLocation may be a path on disk or relative to the
				// workspace root. Don't translate on-disk paths to
				// in-workspace resources.
				IPath basePath = new Path(baseLocation);
				if (basePath.segmentCount() > 1) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
					/*
					 * If the IFile doesn't  exist, make sure it's not
					 * returned
					 */
					if (!file.exists())
						file = null;
				}
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		fFile = file;
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=199175
		fEnableSourceValidation = (fFile != null && fDocument instanceof IStructuredDocument && JSPBatchValidator.isBatchValidatorPreferenceEnabled(fFile) && shouldReallyValidate(fFile));
		if(DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " enablement for source validation: " + fEnableSourceValidation); //$NON-NLS-1$
		}
	}

	public void disconnect(IDocument document) {
		fDuplicatePrefixes.clear();
		fDocument = null;
		fFile = null;
	}
	
	/**
	 * for as you type validation (partial document)
	 */
	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if(fEnableSourceValidation) {
			if(DEBUG) {
				Logger.log(Logger.INFO, getClass().getName() + " revalidating " + dirtyRegion); //$NON-NLS-1$
			}
			validate(dirtyRegion, helper, reporter, fFile);
		}
	}

	private void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter, IFile file) {
		// check for restricted and duplicate prefixes
		IStructuredDocument sDoc = (IStructuredDocument) fDocument;
		IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(dirtyRegion.getOffset(), dirtyRegion.getLength());
		for (int i = 0; i < regions.length; i++) {
			// only checking directives
			if (regions[i].getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				validateDirective(reporter, file, sDoc, regions[i]);
			}
		}
	}

	private void validateDirective(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion sdRegion) {
		// we only care about taglib directive
		if (getDirectiveName(sdRegion).equals("taglib")) { //$NON-NLS-1$
			ITextRegion valueRegion = getAttributeValueRegion(sdRegion, JSP11Namespace.ATTR_NAME_PREFIX);
			if (valueRegion == null)
				return;

			String taglibPrefix = sdRegion.getText(valueRegion);
			int start = sdRegion.getStartOffset(valueRegion);
			// length before stripquotes
			int length = valueRegion.getTextLength();
			taglibPrefix = StringUtils.stripQuotes(taglibPrefix);

			ITextRegion uriValueRegion = getAttributeValueRegion(sdRegion, "uri"); //$NON-NLS-1$
			String taglibURI = StringUtils.stripQuotes(sdRegion.getText(uriValueRegion));

			LocalizedMessage message = null;

			// check for errors
			// use file if available (for markers)
			if (isReservedPrefix(taglibPrefix)) {
				int sev = IMessage.HIGH_SEVERITY;
				String msgText = JSPCoreMessages.JSPDirectiveValidator_0 + taglibPrefix + "'"; //$NON-NLS-2$ //$NON-NLS-1$
				message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
			}
			else if (isDuplicatePrefix(sdRegion, taglibPrefix, taglibURI)) {
				int sev = IMessage.NORMAL_SEVERITY;
				String msgText = JSPCoreMessages.JSPDirectiveValidator_2 + taglibPrefix + "'"; //$NON-NLS-2$ //$NON-NLS-1$
				message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
			}

			// if there's a message, there was an error found
			if (message != null) {
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(fMessageOriginator, message);
			}
		}
	}

	private boolean isDuplicatePrefix(IStructuredDocumentRegion region, String taglibPrefix, String uri) {
		boolean dupe = false;
		IStructuredDocumentRegion existingTaglibDirective = (IStructuredDocumentRegion) fDuplicatePrefixes.get(taglibPrefix);
		if (existingTaglibDirective == null) {
			// prefix doesn't exist, not a dupe
			fDuplicatePrefixes.put(taglibPrefix, region);
		}
		else {
			if (existingTaglibDirective.isDeleted()) {
				// region was deleted, replace w/ new region
				// not a dupe
				fDuplicatePrefixes.put(taglibPrefix, region);
			}
			else if (region != existingTaglibDirective) {
				/*
				 * region exists and it's not this one it's a dupe
				 * 
				 * 203711 - taglib declarations in JSP fragments
				 */
				ITextRegion oldURIRegion = getAttributeValueRegion(existingTaglibDirective, "uri");
				String oldURI = StringUtils.stripQuotes(region.getText(oldURIRegion));
				if (collator.compare(uri, oldURI) != 0) {
					dupe = true;
				}
			}
		}
		return dupe;

	}

	public void cleanup(IReporter reporter) {
		super.cleanup(reporter);
		fDuplicatePrefixes.clear();
	}

	private boolean isReservedPrefix(String name) {
		return fReservedPrefixes.get(name) != null;
	}
}
