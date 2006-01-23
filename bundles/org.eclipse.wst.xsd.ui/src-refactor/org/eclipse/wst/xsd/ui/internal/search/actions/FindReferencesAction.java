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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.core.search.scope.WorkspaceSearchScope;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.wst.xsd.ui.internal.search.XSDSearchQuery;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class FindReferencesAction extends FindAction
{
  public FindReferencesAction(IEditorPart editor)
  {   
    super(editor);
  }
  
  public void setActionDefinitionId(String string)
  {
    
  }

  public void run()
  {
    String pattern = "";
    if (editor != null)
    {  
      IEditorInput input = editor.getEditorInput();
      if (input instanceof IFileEditorInput)
      {
        IFileEditorInput fileEditorInput = (IFileEditorInput)input;               
        IFile file = fileEditorInput.getFile();        
        ISelectionProvider provider = (ISelectionProvider)editor.getAdapter(ISelectionProvider.class);
        if (provider != null)
        {  
          ISelection selection = provider.getSelection();
          if (selection != null && selection instanceof IStructuredSelection)
          {
            IStructuredSelection s = (IStructuredSelection)selection;
            Object o = s.getFirstElement();
            if (o != null && o instanceof XSDNamedComponent)
            {  
              XSDNamedComponent c = (XSDNamedComponent)o;
              QualifiedName metaName = null;
              if (c instanceof XSDComplexTypeDefinition)
              {             
                metaName = IXSDSearchConstants.COMPLEX_TYPE_META_NAME;
              }
              else if (c instanceof XSDSimpleTypeDefinition)
              {
                metaName = IXSDSearchConstants.SIMPLE_TYPE_META_NAME;
              }  
              else if (c instanceof XSDElementDeclaration)
              {
               metaName = IXSDSearchConstants.ELEMENT_META_NAME; 
              }  
              QualifiedName elementQName = new QualifiedName(c.getTargetNamespace(), c.getName());
              System.out.println("name" + c.getTargetNamespace() + ":" + c.getName());
              SearchScope scope = new WorkspaceSearchScope();
              String scopeDescription = "Workspace";    
              XSDSearchQuery searchQuery= new XSDSearchQuery(pattern, file, elementQName, metaName, XSDSearchQuery.LIMIT_TO_REFERENCES, scope, scopeDescription);    
              NewSearchUI.activateSearchResultView();
              NewSearchUI.runQueryInBackground(searchQuery);
            }
          }
        }
      }
    }
  }     
}
