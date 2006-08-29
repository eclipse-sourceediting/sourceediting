/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.search.actions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.scope.ProjectSearchScope;
import org.eclipse.wst.xsd.ui.internal.search.XSDSearchQuery;
import org.eclipse.xsd.XSDNamedComponent;
public class FindReferencesInProjectAction extends FindReferencesAction
{
  public FindReferencesInProjectAction(IEditorPart editor)
  {
    super(editor);
  }

  public void run()
  {
    String pattern = "";
    XSDNamedComponent component = getXSDNamedComponent();
    IFile file = getCurrentFile();
    if (file != null && component != null)
    {
      QualifiedName metaName = determineMetaName(component);
      QualifiedName elementQName = new QualifiedName(component.getTargetNamespace(), component.getName());
      IPath fullPath = file.getFullPath();
      ProjectSearchScope scope = new ProjectSearchScope(fullPath);
      String scopeDescription = "Project";
      XSDSearchQuery searchQuery = new XSDSearchQuery(pattern, file, elementQName, metaName, XSDSearchQuery.LIMIT_TO_REFERENCES, scope, scopeDescription);
      NewSearchUI.activateSearchResultView();
      NewSearchUI.runQueryInBackground(searchQuery);
    }
  }
}
