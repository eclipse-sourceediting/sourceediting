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

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.wst.xsd.ui.internal.search.SearchMessages;

public class ReferencesSearchGroup extends SearchGroup  {
    protected static final String MENU_TEXT= SearchMessages.group_references; 

    protected IEditorPart fEditor;
    private IWorkbenchSite fSite;
    private IActionBars fActionBars;
    
//    private String fGroupId;
    
    protected FindAction fFindReferencesAction;
    protected FindAction fFindReferencesInProjectAction;
    protected FindAction fFindReferencesInWorkingSetAction;


    /**
     * Note: This constructor is for internal use only. Clients should not call this constructor.
     * @param editor
     */
    public ReferencesSearchGroup(IEditorPart editor) {
        Assert.isNotNull(editor);
        fEditor= editor;
        fSite= fEditor.getSite();
        
        initialize();
    }
    
    protected void initialize() {
//      fGroupId= ITextEditorActionConstants.GROUP_FIND;

        fFindReferencesAction= new FindReferencesAction(fEditor);
        fFindReferencesAction.setText(SearchMessages.Search_FindDeclarationAction_label);
        fFindReferencesAction.setActionDefinitionId("SEARCH_REFERENCES_IN_WORKSPACE");
        //fEditor.setAction("SearchReferencesInWorkspace", fFindReferencesAction); //$NON-NLS-1$

        fFindReferencesInProjectAction= new FindReferencesInProjectAction(fEditor);
        fFindReferencesInProjectAction.setText(SearchMessages.Search_FindDeclarationsInProjectAction_label);        
        fFindReferencesInProjectAction.setActionDefinitionId("SEARCH_REFERENCES_IN_PROJECT");
        //fEditor.setAction("SearchReferencesInProject", fFindReferencesInProjectAction); //$NON-NLS-1$
    
        fFindReferencesInWorkingSetAction= new FindReferencesInWorkingSetAction(fEditor);
        fFindReferencesInWorkingSetAction.setText(SearchMessages.Search_FindDeclarationsInWorkingSetAction_label);         
        fFindReferencesInWorkingSetAction.setActionDefinitionId(".SEARCH_REFERENCES_IN_WORKING_SET");
        //fEditor.setAction("SearchReferencesInWorkingSet", fFindReferencesInWorkingSetAction); //$NON-NLS-1$    	
    }

    /*
    private void registerAction(SelectionDispatchAction action, ISelectionProvider provider, ISelection selection) {
        action.update(selection);
        provider.addSelectionChangedListener(action);
    }*/

    /**
     * Note: this method is for internal use only. Clients should not call this method.
     * 
     * @return the menu label
     */
    protected String getName() {
        return MENU_TEXT;
    }
    
    public void fillActions(List list)
    {
      list.add(fFindReferencesAction);
      //list.add(fFindReferencesInHierarchyAction);
      list.add(fFindReferencesInProjectAction);
      list.add(new Separator());
      list.add(fFindReferencesInWorkingSetAction);
    }
    
    /* (non-Javadoc)
     * Method declared in ActionGroup
     */
    public void fillActionBars(IActionBars actionBars) {
        Assert.isNotNull(actionBars);
        super.fillActionBars(actionBars);
        fActionBars= actionBars;
        updateGlobalActionHandlers();
    }

    
//    private void addAction(IAction action, IMenuManager manager) {
//        if (action.isEnabled()) {
//            manager.add(action);
//        }
//    }

    /*
    private void addWorkingSetAction(IWorkingSet[] workingSets, IMenuManager manager) {
        FindAction action;
        if (fEditor != null)
            action= new WorkingSetFindAction(fEditor, new FindReferencesInWorkingSetAction(fEditor, workingSets), SearchUtil.toString(workingSets));
        else
            action= new WorkingSetFindAction(fSite, new FindReferencesInWorkingSetAction(fSite, workingSets), SearchUtil.toString(workingSets));
        action.update(getContext().getSelection());
        addAction(action, manager);
    }
    */
    
    /* (non-Javadoc)
     * Method declared on ActionGroup.
     */
    public void fillContextMenu(IMenuManager manager) {
      /*
        MenuManager javaSearchMM= new MenuManager(getName(), IContextMenuConstants.GROUP_SEARCH);
        addAction(fFindReferencesAction, javaSearchMM);
        addAction(fFindReferencesInProjectAction, javaSearchMM);
        addAction(fFindReferencesInHierarchyAction, javaSearchMM);
        
        javaSearchMM.add(new Separator());
        
        Iterator iter= SearchUtil.getLRUWorkingSets().sortedIterator();
        while (iter.hasNext()) {
            addWorkingSetAction((IWorkingSet[]) iter.next(), javaSearchMM);
        }
        addAction(fFindReferencesInWorkingSetAction, javaSearchMM);

        if (!javaSearchMM.isEmpty())
            manager.appendToGroup(fGroupId, javaSearchMM);
        */    
    }
    
    /* 
     * Overrides method declared in ActionGroup
     */
    public void dispose() {
        ISelectionProvider provider= fSite.getSelectionProvider();
        if (provider != null) {
            disposeAction(fFindReferencesAction, provider);
            disposeAction(fFindReferencesInProjectAction, provider);
          //  disposeAction(fFindReferencesInHierarchyAction, provider);
            disposeAction(fFindReferencesInWorkingSetAction, provider);
        }
        fFindReferencesAction= null;
        fFindReferencesInProjectAction= null;
        //fFindReferencesInHierarchyAction= null;
        fFindReferencesInWorkingSetAction= null;
        updateGlobalActionHandlers();
        super.dispose();
    }

    private void updateGlobalActionHandlers() {
        if (fActionBars != null) {
//            fActionBars.setGlobalActionHandler(JdtActionConstants.FIND_REFERENCES_IN_WORKSPACE, fFindReferencesAction);
//            fActionBars.setGlobalActionHandler(JdtActionConstants.FIND_REFERENCES_IN_PROJECT, fFindReferencesInProjectAction);
//            fActionBars.setGlobalActionHandler(JdtActionConstants.FIND_REFERENCES_IN_HIERARCHY, fFindReferencesInHierarchyAction);
//            fActionBars.setGlobalActionHandler(JdtActionConstants.FIND_REFERENCES_IN_WORKING_SET, fFindReferencesInWorkingSetAction);
        }
    }

    private void disposeAction(ISelectionChangedListener action, ISelectionProvider provider) {
        if (action != null)
            provider.removeSelectionChangedListener(action);
    }
}