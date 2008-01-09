/*******************************************************************************
 Copyright (c) 2005, 2008 BEA Systems and others.
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
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class JSPELValidator extends JSPValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$		

	private IValidator fMessageOriginator;

	public JSPELValidator() {
		this.fMessageOriginator = this;
	}

	public JSPELValidator(IValidator validator) {
		this.fMessageOriginator = validator;
	}

	protected void validateFile(IFile file, IReporter reporter) {
		if (DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + file); //$NON-NLS-1$
		}

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (!reporter.isCancelled() && model != null) {
				performValidation(file, reporter, model.getStructuredDocument());
			}
		}
		catch (Exception e) {
			Logger.logException(e);
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
