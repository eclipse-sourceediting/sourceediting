package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.util.StringUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;

/**
 * Checks for:
 * - duplicate taglib prefix values
 * - reserved taglib prefix values
 *
 */
public class JSPDirectiveValidator extends JSPValidator implements ISourceValidator {

	private HashMap fReservedPrefixes = new HashMap();
	private HashMap fDuplicatePrefixes = new HashMap();
	private IDocument fDocument;

	public JSPDirectiveValidator() {
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("javax", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("servlet", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sun", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sunw", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
	}
	/**
	 * batch validation call
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		
		// when validating an entire file
		// need to clear dupes or else you're comparing between files
		fDuplicatePrefixes.clear();

		// for batch validation
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForRead(f);
			if (sModel != null) {
				IStructuredDocument sDoc = sModel.getStructuredDocument();
				// need to set this for partial validate call to work
				fDocument = sDoc;
				// iterate all document regions
				IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(0, sDoc.getLength());
				for (int i = 0; i < regions.length; i++) 
					validateDocumentRegion(f, reporter, regions[i]);
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

	private void validateDocumentRegion(IFile f, IReporter reporter, IStructuredDocumentRegion sdRegion) {
		
		final int start = sdRegion.getStartOffset();
		final int length = sdRegion.getEndOffset() - start;
		IRegion r = new IRegion() {
			public int getLength() {
				return length;
			}
			public int getOffset() {
				return start;
			}
		};
		// call w/ batch validator reporter
		validate(r, null, reporter, f);
	}

	public void connect(IDocument document) {
		fDocument = document;
	}

	public void disconnect(IDocument document) {
		fDuplicatePrefixes.clear();
	}

	/**
	 * for as you type validation (partial document)
	 */
	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		validate(dirtyRegion, helper, reporter, null);
	}

	private void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter, IFile file) {

		// check for restricted and duplicate prefixes
		if (fDocument instanceof IStructuredDocument) {
			IStructuredDocument sDoc = (IStructuredDocument) fDocument;
			IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(dirtyRegion.getOffset(), dirtyRegion.getLength());
			for (int i = 0; i < regions.length; i++) {
				// only checking directives
				if (regions[i].getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
					validateDirective(reporter, file, sDoc, regions[i]);
				}
			}
		}
	}

	private void validateDirective(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion sdRegion) {
		
		// we only care about taglib directive
		if (getDirectiveName(sdRegion).equals("taglib")) { //$NON-NLS-1$

			ITextRegion valueRegion = getAttributeValueRegion(sdRegion, JSP11Namespace.ATTR_NAME_PREFIX);
			if(valueRegion == null)
				return;
			
			String taglibPrefix = sdRegion.getText(valueRegion);
			int start = sdRegion.getStartOffset(valueRegion);
			// length before stripquotes
			int length = taglibPrefix.length();
			taglibPrefix = StringUtils.stripQuotes(taglibPrefix);

			int sev = IMessage.HIGH_SEVERITY;
			LocalizedMessage message = null;

			// check for errors
			// use file if available (for markers)
			if (isReservedPrefix(taglibPrefix)) {

				String msgText = "Reserved prefix  '" + taglibPrefix + "'";
				message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
			}
			else if (isDuplicatePrefix(sdRegion, taglibPrefix)) {

				String msgText = "Duplicate prefix  '" + taglibPrefix + "'";
				message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
			}

			// if there's a message, there was an error found
			if (message != null) {
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(this, message);
			}
		}
	}

	private boolean isDuplicatePrefix(IStructuredDocumentRegion region, String taglibPrefix) {
		boolean dupe = false;
		Object o = fDuplicatePrefixes.get(taglibPrefix);
		if (o == null) {
			// prefix doesn't exist, not a dupe
			fDuplicatePrefixes.put(taglibPrefix, region);
		}
		else if (o instanceof IStructuredDocumentRegion) {
			if (((IStructuredDocumentRegion) o).isDeleted()) {
				// region was deleted, replace w/ new region
				// not a dupe
				fDuplicatePrefixes.put(taglibPrefix, region);
			}
			else if (region != o) {
				// region exists and it's not this one
				// it's a dupe
				dupe = true;
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
