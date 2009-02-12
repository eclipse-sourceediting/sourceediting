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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.java.jspel.TokenMgrError;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class JSPELValidator extends JSPValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$		
	private static final String PREFERENCE_NODE_QUALIFIER = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
	private static final int MAX_REGIONS = 1000;
	
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

	private IPreferencesService fPreferencesService = null;
	private IScopeContext[] fScopes = null;

	private void loadPreferences(IFile file) {
		fScopes = new IScopeContext[]{new InstanceScope(), new DefaultScope()};

		fPreferencesService = Platform.getPreferencesService();
		if (file != null && file.isAccessible()) {
			ProjectScope projectScope = new ProjectScope(file.getProject());
			if (projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, false)) {
				fScopes = new IScopeContext[]{projectScope, new InstanceScope(), new DefaultScope()};
			}
		}
	}
	
	int getMessageSeverity(String key) {
		int sev = fPreferencesService.getInt(PREFERENCE_NODE_QUALIFIER, key, IMessage.NORMAL_SEVERITY, fScopes);
		switch (sev) {
			case ValidationMessage.ERROR :
				return IMessage.HIGH_SEVERITY;
			case ValidationMessage.WARNING :
				return IMessage.NORMAL_SEVERITY;
			case ValidationMessage.INFORMATION :
				return IMessage.LOW_SEVERITY;
			case ValidationMessage.IGNORE :
				return ValidationMessage.IGNORE;
		}
		return IMessage.NORMAL_SEVERITY;
	}
	
	private void unloadPreferences() {
		fPreferencesService = null;
		fScopes = null;
	}

	protected void performValidation(IFile file, IReporter reporter, IStructuredDocument structuredDoc) {
		loadPreferences(file);
		IStructuredDocumentRegion curNode = structuredDoc.getFirstStructuredDocumentRegion();
		while (null != curNode && !reporter.isCancelled()) {
			if (curNode.getType() != DOMRegionContext.XML_COMMENT_TEXT && curNode.getType() != DOMRegionContext.XML_CDATA_TEXT && curNode.getType() != DOMRegionContext.UNDEFINED) {
				validateRegionContainer(curNode, reporter, file);
			}
			curNode = curNode.getNext();
		}
		unloadPreferences();
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
					/* [136795] Validate everything in the EL container, not just JSP_EL_CONTENT */
					if (childRegion.getType() == DOMJSPRegionContexts.JSP_EL_OPEN)
						validateELContent(parentRegion, childRegion, childRegions, reporter, file);
				}
			}
		}
	}
	
	protected void validateELContent(ITextRegionCollection container, ITextRegion elOpenRegion, Iterator elRegions, IReporter reporter, IFile file) {
		int contentStart = elOpenRegion.getEnd();
		int contentDocStart = container.getEndOffset(elOpenRegion);
		int contentLength = container.getLength();
		int regionCount = 0;
		ITextRegion elRegion = null;
		/* Find the EL closing region, otherwise the last region will be used to calculate the EL content text */
		while (elRegions != null && elRegions.hasNext() && (regionCount++ < MAX_REGIONS)) {
			elRegion = (ITextRegion) elRegions.next();
			if (elRegion.getType() == DOMJSPRegionContexts.JSP_EL_CLOSE)
				break;
		}
		
		String elText = container.getFullText().substring(contentStart, (elRegion != null) ? elRegion.getStart() : (contentLength - 1));
		JSPELParser elParser = JSPELParser.createParser(elText);
		try {
			elParser.Expression();
		}
		catch (ParseException e) {
			int sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_EL_SYNTAX);
			if (sev != ValidationMessage.IGNORE) {
				Token curTok = e.currentToken;
				int problemStartOffset = contentDocStart + curTok.beginColumn;
				Message message = new LocalizedMessage(sev, JSPCoreMessages.JSPEL_Syntax);
				message.setOffset(problemStartOffset);
				message.setLength(curTok.endColumn - curTok.beginColumn + 1);
				message.setTargetObject(file);
				reporter.addMessage(fMessageOriginator, message);
			}
		}
		catch (TokenMgrError te) {
			int sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_EL_LEXER);
			if (sev != ValidationMessage.IGNORE) {
				Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Token);
				message.setOffset(contentDocStart);
				message.setLength(contentLength);
				message.setTargetObject(file);
				reporter.addMessage(fMessageOriginator, message);
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
			int sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_EL_SYNTAX);
			if (sev != ValidationMessage.IGNORE) {
				Token curTok = e.currentToken;
				int problemStartOffset = contentStart + curTok.beginColumn;
				Message message = new LocalizedMessage(sev, JSPCoreMessages.JSPEL_Syntax);
				message.setOffset(problemStartOffset);
				message.setLength(curTok.endColumn - curTok.beginColumn + 1);
				message.setTargetObject(file);
				reporter.addMessage(fMessageOriginator, message);
			}
		}
		catch (TokenMgrError te) {
			int sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_EL_LEXER);
			if (sev != ValidationMessage.IGNORE) {
				Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Token);
				message.setOffset(contentStart);
				message.setLength(contentLength);
				message.setTargetObject(file);
				reporter.addMessage(fMessageOriginator, message);
			}
		}
	}
}
