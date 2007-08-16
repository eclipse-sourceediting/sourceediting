/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.java.jspel.TokenMgrError;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class JSPELValidator extends JSPValidator implements ISourceValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$	

	private IValidator fMessageOriginator;
	private IDocument fDocument;
	private IFile fFile;
	private boolean fEnableSourceValidation = false;

	public JSPELValidator(){
		this.fMessageOriginator = this;
	}
	public JSPELValidator(IValidator validator){
		this.fMessageOriginator = validator;
	}
	

	public void connect(IDocument document) {
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
		fEnableSourceValidation = (fDocument instanceof IStructuredDocument && JSPBatchValidator.isBatchValidatorPreferenceEnabled(fFile) && shouldReallyValidate(fFile));
		if(DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " enablement for source validation: " + fEnableSourceValidation); //$NON-NLS-1$
		}
	}

	public void disconnect(IDocument document) {
		fDocument = null;
		fFile = null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if(fEnableSourceValidation) {
			if(DEBUG) {
				Logger.log(Logger.INFO, getClass().getName() + " revalidating"); //$NON-NLS-1$
			}
			performValidation(fFile, reporter, (IStructuredDocument) fDocument);
		}
	}

	protected void validateFile(IFile file, IReporter reporter) {		
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (!reporter.isCancelled()) {
				reporter.removeAllMessages(fMessageOriginator, file);
				performValidation(file, reporter, model.getStructuredDocument());
			}
		}
		catch (Exception e) {
		}
		finally {
			if (null != model)
				model.releaseFromRead();
		}
	}

	protected void performValidation(IFile file, IReporter reporter, IStructuredDocument structuredDoc) {
		IStructuredDocumentRegion curNode = structuredDoc.getFirstStructuredDocumentRegion();
		while (null != curNode && !reporter.isCancelled()) {
			if (curNode.getType() != DOMRegionContext.XML_COMMENT_TEXT && curNode.getType() != DOMRegionContext.XML_CDATA_TEXT && curNode.getType() != DOMRegionContext.UNDEFINED) {
				validateRegionContainer(curNode, reporter, file);
			}
			curNode = curNode.getNext();
		}
	}
	
	protected void validateRegionContainer(ITextRegionCollection container, IReporter reporter, IFile file) {
		ITextRegionCollection containerRegion = container;
		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		while (regions.hasNext() && !reporter.isCancelled()) {
			region = (ITextRegion) regions.next();
			String type = region.getType();
			if (type != null && region instanceof ITextRegionCollection) {
				ITextRegionCollection parentRegion = ((ITextRegionCollection) region);
				Iterator childRegions = parentRegion.getRegions().iterator();
				while (childRegions.hasNext() && !reporter.isCancelled()) {
					ITextRegion childRegion = (ITextRegion) childRegions.next();
					if (childRegion.getType() == DOMJSPRegionContexts.JSP_EL_CONTENT)
						validateXMLNode(parentRegion, childRegion, reporter, file);
				}
			}
		}
	}

	protected void validateXMLNode(ITextRegionCollection container, ITextRegion region, IReporter reporter, IFile file) {
		String elText = container.getText(region);
		JSPELParser elParser = JSPELParser.createParser(elText);
		int contentStart = container.getStartOffset(region);
		int contentLength = container.getLength();
		try {
			elParser.Expression();
		}
		catch (ParseException e) {
			Token curTok = e.currentToken;
			int problemStartOffset = contentStart + curTok.beginColumn;
			Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Syntax);
			message.setOffset(problemStartOffset);
			message.setLength(curTok.endColumn - curTok.beginColumn + 1);
			message.setTargetObject(file);
			reporter.addMessage(fMessageOriginator, message);
		}
		catch (TokenMgrError te) {
			Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Token);
			message.setOffset(contentStart);
			message.setLength(contentLength);
			message.setTargetObject(file);
			reporter.addMessage(fMessageOriginator, message);
		}
	}
}
