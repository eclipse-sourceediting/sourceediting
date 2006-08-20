package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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

/**
 * Checks for: - duplicate taglib prefix values - reserved taglib prefix
 * values
 * 
 */
public class JSPDirectiveValidator extends JSPValidator implements ISourceValidator {

	private HashMap fReservedPrefixes = new HashMap();
	private HashMap fDuplicatePrefixes = new HashMap();
	private IDocument fDocument;

	public JSPDirectiveValidator() {
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("java", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
				IStructuredDocumentRegion region = sDoc.getFirstStructuredDocumentRegion();
				while (region != null && !reporter.isCancelled()) {
					// only checking directives
					if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
						validateDirective(reporter, f, sDoc, region);
					}
					region = region.getNext();
				}
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

	public void connect(IDocument document) {
		fDuplicatePrefixes.clear();
		fDocument = document;
	}

	public void disconnect(IDocument document) {
		fDuplicatePrefixes.clear();
		fDocument = null;
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
			if (valueRegion == null)
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

				String msgText = JSPCoreMessages.JSPDirectiveValidator_0 + taglibPrefix + "'"; //$NON-NLS-2$ //$NON-NLS-1$
				message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
			}
			else if (isDuplicatePrefix(sdRegion, taglibPrefix)) {

				String msgText = JSPCoreMessages.JSPDirectiveValidator_2 + taglibPrefix + "'"; //$NON-NLS-2$ //$NON-NLS-1$
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
		fDocument = null;
	}

	private boolean isReservedPrefix(String name) {
		return fReservedPrefixes.get(name) != null;
	}
}
