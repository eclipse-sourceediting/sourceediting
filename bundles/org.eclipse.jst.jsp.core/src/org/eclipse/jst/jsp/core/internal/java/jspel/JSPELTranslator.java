/*******************************************************************************
 * Copyright (c) 2005, 2010 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     IBM Corporation - Bug 298304 User-configurable severities for EL problems
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.java.jspel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.jspel.ELProblem;
import org.eclipse.jst.jsp.core.jspel.IJSPELTranslator;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

	
public class JSPELTranslator implements IJSPELTranslator {

	private static final String PREFERENCE_NODE_QUALIFIER = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
	/**
	 * JSP Expression Language Parser.
	 */
	private JSPELParser elParser = null;
	
	public List translateEL(String elText, String delim,
			IStructuredDocumentRegion currentNode, int contentStart,
			int contentLength, StringBuffer fUserELExpressions,
			HashMap fUserELRanges, IStructuredDocument document) {
		
		ArrayList elProblems = new ArrayList();
		
		try {
			synchronized(this) {
				if(null == elParser) {
					elParser = JSPELParser.createParser(elText);
				} else {
					elParser.ReInit(elText);
				}
			
				ASTExpression expression = elParser.Expression();
				ELGenerator gen = new ELGenerator();
				List generatorELProblems = gen.generate(expression, currentNode, fUserELExpressions, fUserELRanges, document, currentNode, contentStart, contentLength);
				elProblems.addAll(generatorELProblems);
			}
		} catch (ParseException e) {
			final int sev = getProblemSeverity(JSPCorePreferenceNames.VALIDATION_EL_SYNTAX, getScopeContexts(document));
			if (sev != ValidationMessage.IGNORE) {
				Token curTok = e.currentToken;
				int problemStartOffset;
				int problemEndOffset;
				Position pos = null;
				problemStartOffset =  contentStart + curTok.beginColumn;
				problemEndOffset = contentStart + curTok.endColumn;
				
				pos = new Position(problemStartOffset, problemEndOffset - problemStartOffset + 1);
				elProblems.add(new ELProblem(sev, pos, e.getLocalizedMessage()));
			}
		} catch (TokenMgrError te) {
			int sev = getProblemSeverity(JSPCorePreferenceNames.VALIDATION_EL_LEXER, getScopeContexts(document));
			if (sev != ValidationMessage.IGNORE) {
				Position pos = new Position(contentStart, contentLength);
				elProblems.add(new ELProblem(sev, pos, JSPCoreMessages.JSPEL_Token));
			}
		}
		return elProblems;
	}
	
	/**
	 * @param key preference key used to get the severity for a problem
	 * @param contexts preference service contexts
	 * @return The severity of the problem represented by the given preference key
	 */
	private int getProblemSeverity(String key, IScopeContext[] contexts) {
		return Platform.getPreferencesService().getInt(PREFERENCE_NODE_QUALIFIER, key, IMessage.NORMAL_SEVERITY, contexts);
	}

	private IScopeContext[] getScopeContexts(IStructuredDocument document) {
		IScopeContext[] scopes = new IScopeContext[]{new InstanceScope(), new DefaultScope()};
		final IFile file = getFile(document);
		if (file != null && file.exists()) {
			final IProject project = file.getProject();
			if (project.exists()) {
				ProjectScope projectScope = new ProjectScope(project);
				if (projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, false)) {
					scopes = new IScopeContext[]{projectScope, new InstanceScope(), new DefaultScope()};
				}
			}
		}
		return scopes;
	}

	private IFile getFile(IStructuredDocument document) {
		IFile f = null;
		final ITextFileBuffer buffer = FileBufferModelManager.getInstance().getBuffer(document);
		if (buffer != null) {
			final IPath path = buffer.getLocation();
			if (path.segmentCount() > 1) {
				f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
			if (f != null && f.isAccessible()) {
				return f;
			}
		}
		return null;
	}
}
