/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.text.correction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.wst.html.core.internal.HTMLCoreMessages;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.validate.StringMatcher;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;

public class HTMLSyntaxValidationQuickFixProcessor implements IQuickAssistProcessor {

	private StringMatcher UNKNOWN_ELEMENT_NAME_MATCHER;
	private IPreferencesService fPreferenceService;

	public HTMLSyntaxValidationQuickFixProcessor() {
		UNKNOWN_ELEMENT_NAME_MATCHER = new StringMatcher(NLS.bind(HTMLCoreMessages.Unknown_tag___0____ERROR_, "*")); //$NON-NLS-1$
		fPreferenceService = Platform.getPreferencesService();
	}
	
	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistProcessor#canFix(org.eclipse.jface.text.source.Annotation)
	 */
	public boolean canFix(Annotation annotation) {
		boolean result = false;

		String text = null;
		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) annotation;
			int problemID = tempAnnotation.getProblemID();
			text = tempAnnotation.getText();

			if (problemID == 0 && text != null)
				result = true;
		} else if (annotation instanceof MarkerAnnotation) {
			MarkerAnnotation markerAnnotation = (MarkerAnnotation) annotation;
			text = markerAnnotation.getText();
			IMarker marker = markerAnnotation.getMarker();
			IResource resource = marker == null ? null : marker.getResource();
			if (resource != null && resource.exists() && resource.isAccessible() && text != null) {
				result = true;
			}
		}
		
		result = (result && UNKNOWN_ELEMENT_NAME_MATCHER.match(text));

		return result;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistProcessor#canAssist(org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext)
	 */
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return true;
	}

	/*
	 * @see org.eclipse.jface.text.quickassist.IQuickAssistProcessor#computeQuickAssistProposals(org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext)
	 */
	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext) {
		ISourceViewer viewer = invocationContext.getSourceViewer();
		int documentOffset = invocationContext.getOffset();
		int length = viewer != null ? viewer.getSelectedRange().y : 0;

		IAnnotationModel model = viewer.getAnnotationModel();
		if (model == null)
			return null;

		List proposals = new ArrayList();
		if (model instanceof IAnnotationModelExtension2) {
			Iterator iter = ((IAnnotationModelExtension2) model).getAnnotationIterator(documentOffset, length, true, true);
			while (iter.hasNext()) {
				Annotation anno = (Annotation) iter.next();
				if (canFix(anno)) {
					int offset = -1;
					
					if (anno instanceof TemporaryAnnotation) {
						offset = ((TemporaryAnnotation)anno).getPosition().getOffset();
					} else if (anno instanceof MarkerAnnotation) {
						offset = ((MarkerAnnotation)anno).getMarker().getAttribute(IMarker.CHAR_START, -1);
					}
					if (offset == -1)
						continue;

					IDOMNode node = (IDOMNode) ContentAssistUtils.getNodeAt(viewer, offset);
					if (!(node instanceof Element))
						continue;

					Object adapter = (node instanceof IAdaptable ? ((IAdaptable)node).getAdapter(IResource.class) : null);
					IProject project = (adapter instanceof IResource ? ((IResource)adapter).getProject() : null);

					IScopeContext[] fLookupOrder = new IScopeContext[] {new InstanceScope(), new DefaultScope()};
					if (project != null) {
						ProjectScope projectScope = new ProjectScope(project);
						if(projectScope.getNode(getPreferenceNodeQualifier())
								.getBoolean(getProjectSettingsKey(), false))
							fLookupOrder = new IScopeContext[] {projectScope, new InstanceScope(), new DefaultScope()};
					}
					
					boolean ignore = fPreferenceService.getBoolean(
							getPreferenceNodeQualifier(), HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES, 
							HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES_DEFAULT, fLookupOrder);

					String ignoreList = fPreferenceService.getString(
							getPreferenceNodeQualifier(), HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE, 
							HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE_DEFAULT, fLookupOrder);

					Set result = new HashSet();
					if (ignoreList.trim().length() > 0) {
						String[] names = ignoreList.split(","); //$NON-NLS-1$
						for (int i = 0; names != null && i < names.length; i++) {
							String name = names[i] == null ? null : names[i].trim();
							if (name != null && name.length() > 0) 
								result.add(name.toLowerCase());
						}
					}
					
					String name = getElementName(node, offset);
					if (name == null) continue;
					
					// If ignore == false. then show a quick fix anyway (due to allow to turn 'ignore' option on)
					if (!ignore || shouldShowQuickFix(result, name.toLowerCase())) {
						IgnoreElementNameCompletionProposal p = new IgnoreElementNameCompletionProposal(
								name.toLowerCase(), offset, NLS.bind(HTMLUIMessages.DoNotValidateElement, name), 
								HTMLUIMessages.DoNotValidateElementAddInfo, node);
						if (!proposals.contains(p))
							proposals.add(p);  
					}
					
					int dashIndex = name.indexOf('-');
					while (dashIndex != -1) {
						StringBuffer namePattern = new StringBuffer(name.substring(0, dashIndex + 1)).append('*');
						
						// Do not continue creating proposals for the rest of patterns if 
						// a more common pattern is already created
						if (ignore && result.contains(namePattern.toString().toLowerCase()))
							break;
						
						IgnoreElementNameCompletionProposal p = new IgnoreElementNameCompletionProposal(
								namePattern.toString().toLowerCase(), offset, NLS.bind(HTMLUIMessages.DoNotValidateAllElements, namePattern.toString()), 
								HTMLUIMessages.DoNotValidateAllElementsAddInfo, node); 
						if (!proposals.contains(p))
							proposals.add(p);  

						dashIndex = name.indexOf('-', dashIndex + 1);
					}
				}
			}
		}

		if (proposals.isEmpty())
			return null;

		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);

	}

	private String getElementName(IDOMNode node, int offset) {
		return node.getNodeName();
	}
	
	private boolean shouldShowQuickFix(Set lcIgnoredPatterns, String attrName) {
		// Check the attribute name absence in ignore list
		String [] lcPatterns = (String[])lcIgnoredPatterns.toArray(new String[0]);
		for (int i = 0; i < lcPatterns.length; i++) {
			StringMatcher strMatcher = new StringMatcher(lcPatterns[i]);
			if (strMatcher.match(attrName.toLowerCase())) {
				return false; // The attribute name is already ignored, no need to show a quickfix
			}
		}

		// The attribute name is not ignored yet, need to show a quickfix
		return true;
	}
	
	private String getPreferenceNodeQualifier() {
		return HTMLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	private String getProjectSettingsKey() {
		return HTMLCorePreferenceNames.USE_PROJECT_SETTINGS;
	}
}
