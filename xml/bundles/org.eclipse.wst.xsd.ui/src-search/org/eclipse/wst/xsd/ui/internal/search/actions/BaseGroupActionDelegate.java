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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

//TODO (trung) should be in common.ui this one ??
public abstract class BaseGroupActionDelegate implements IObjectActionDelegate, IEditorActionDelegate, IMenuCreator
{
    protected ISelection fSelection;
    private IAction fDelegateAction;
    // whether to re-fill the menu (reset on selection change)
    private boolean fFillMenu = true;
    protected IWorkbenchPart workbenchPart; 
    

    public BaseGroupActionDelegate() 
    {

    }
    
    /*
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        workbenchPart = targetPart;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuCreator#dispose()
     */
    public void dispose() {
        // nothing to do
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu(Control parent) {
        // never called
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
     */
    public Menu getMenu(Menu parent) {
        //Create the new menu. The menu will get filled when it is about to be shown. see fillMenu(Menu).
        Menu menu = new Menu(parent);
        /**
         * Add listener to repopulate the menu each time
         * it is shown because MenuManager.update(boolean, boolean) 
         * doesn't dispose pulldown ActionContribution items for each popup menu.
         */
        menu.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                if (fFillMenu) {
                    Menu m = (Menu)e.widget;
                    MenuItem[] items = m.getItems();
                    for (int i=0; i < items.length; i++) {
                        items[i].dispose();
                    }
                    fillMenu(m);
                    fFillMenu = false;
                }
            }
        });
        return menu;
    }

    /*
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        // Never called because we become a menu.
    }
    
    /*
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        fDelegateAction = action;
        updateWith(selection);
        
    }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        workbenchPart = targetEditor;
        fDelegateAction = action;
        if (targetEditor != null && targetEditor.getEditorSite() != null && targetEditor.getEditorSite().getSelectionProvider() != null) {
            updateWith(targetEditor.getEditorSite().getSelectionProvider().getSelection());
        }
        
    }
  
    public void updateWith(ISelection selection) {
        fSelection = selection;
        if (fDelegateAction != null) {
            boolean enable = false;
            if (selection != null) {
                if (selection instanceof ITextSelection) {
                    //if (((ITextSelection) selection).getLength() > 0) {
                        enable = true;
                    //}
                }
                else if(selection instanceof IStructuredSelection ){
                    enable = !selection.isEmpty();
                }
            }
            // enable action
            fDelegateAction.setEnabled(enable);
            
            // fill submenu
            fFillMenu = true;
            fDelegateAction.setMenuCreator(this);
            
            
        }
        
    }
    
    
  protected abstract void fillMenu(Menu menu);
    
   
  
}
