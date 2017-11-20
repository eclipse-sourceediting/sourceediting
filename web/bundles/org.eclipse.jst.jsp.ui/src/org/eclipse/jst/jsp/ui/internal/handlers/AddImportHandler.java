/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.java.views.TypeNameLabelProvider;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class AddImportHandler extends AbstractHandler {

	private static final TypeNameLabelProvider LABEL_PROVIDER = new TypeNameLabelProvider();

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorSite site = HandlerUtil.getActiveEditor(event).getEditorSite();
		final ISelectionProvider provider = site.getSelectionProvider();
		final ISelection selection = provider != null ? provider.getSelection() : null;
		if (selection instanceof IStructuredSelection && selection instanceof ITextSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			final int offset = ((ITextSelection) selection).getOffset();
			final Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IDOMNode) {
				final IDOMModel model = ((IDOMNode) firstElement).getModel();
				INodeAdapter adapter = model.getDocument().getAdapterFor(IJSPTranslation.class);
				if (adapter != null) {
					JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
					final JSPTranslationExtension translation = translationAdapter.getJSPTranslation();
					translation.reconcileCompilationUnit();
					final ICompilationUnit cu = translation.getCompilationUnit();
					CompilationUnit astRoot = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_YES, null);
					if (astRoot != null) {
						final ASTNode node = NodeFinder.perform(astRoot, translation.getJavaOffset(offset), 0);
						if (node != null) {
							SimpleName name = null;
							if (node.getNodeType() == ASTNode.SIMPLE_NAME) {
								name = (SimpleName) node;
							}
							else if (node.getNodeType() == ASTNode.QUALIFIED_NAME) {
								name = ((QualifiedName) node).getName();
							}
							if (name != null) {
								IBinding binding = name.resolveBinding();
								if (binding instanceof ITypeBinding && (binding.getJavaElement() == null || !binding.getJavaElement().exists()) ) {
									// Look it up!
									ITypeBinding typeBinding = (ITypeBinding) binding;
									final IImportContainer importContainer = cu.getImportContainer();
									if (!importContainer.getImport(typeBinding.getQualifiedName()).exists()) {
										final List typesFound = new ArrayList();
										final TypeNameMatchRequestor collector = new TypeNameMatcher(typesFound);
										IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { cu.getJavaProject() });
										final int mode = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
										try {
											new SearchEngine().searchAllTypeNames(null, mode, name.getIdentifier().toCharArray(), mode, IJavaSearchConstants.CLASS_AND_INTERFACE, scope, collector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
											final int length = typesFound.size();
											final List elements = new ArrayList();
											for (int i = 0; i < length; i++) {
												final TypeNameMatch match = (TypeNameMatch) typesFound.get(i);
												final int modifiers = match.getModifiers();
												if (!Flags.isPrivate(modifiers) && !Flags.isPackageDefault(modifiers)) {
													elements.add(match);
												}
											}
											TypeNameMatch match = null;
											// If there's only one match, insert it; otherwise, open the dialog to choose from the list
											if (elements.size() == 1){
												match = (TypeNameMatch) elements.get(0);
											}
											else if (elements.size() > 1) {
												ElementListSelectionDialog dialog= new ElementListSelectionDialog(site.getShell(), LABEL_PROVIDER);
												dialog.setElements(elements.toArray(new TypeNameMatch[elements.size()]));
												dialog.setTitle(JSPUIMessages.AddImportHandler_title);
												dialog.setMessage(JSPUIMessages.AddImportHandler_label);
												if (dialog.open() == Window.OK) {
													final Object result = dialog.getFirstResult();
													if (result instanceof TypeNameMatch) {
														match = (TypeNameMatch) result;
													}
												}
											}
											addImport(match, model.getStructuredDocument());
										} catch (JavaModelException e) {
											Logger.logException("Exception while determining import.", e); //$NON-NLS-1$
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void addImport(TypeNameMatch match, IDocument document) {
		if (match != null) {
			new PageImport(match.getFullyQualifiedName()).add(document);
		}
	}

	private class TypeNameMatcher extends TypeNameMatchRequestor {
		private Collection matches;

		public TypeNameMatcher(Collection matches) {
			this.matches = matches;
		}

		private boolean isFiltered(TypeNameMatch match) {
			// TODO Java Type Filter support
			int accessibility= match.getAccessibility();
			switch (accessibility) {
				case IAccessRule.K_NON_ACCESSIBLE:
					return JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.CODEASSIST_FORBIDDEN_REFERENCE_CHECK));
				case IAccessRule.K_DISCOURAGED:
					return JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.CODEASSIST_DISCOURAGED_REFERENCE_CHECK));
				default:
					return false;
			}
		}

		public void acceptTypeNameMatch(TypeNameMatch match) {
			if (!isFiltered(match)) {
				matches.add(match);
			}
		}
	}

}
