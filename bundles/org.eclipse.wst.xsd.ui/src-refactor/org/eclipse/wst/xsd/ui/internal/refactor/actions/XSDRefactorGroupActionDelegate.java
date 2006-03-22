package org.eclipse.wst.xsd.ui.internal.refactor.actions;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.xsd.editor.ISelectionMapper;
import org.eclipse.wst.xsd.ui.internal.refactor.actions.XSDRefactorActionGroup;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorActionGroup;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorGroupActionDelegate;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorGroupSubMenu;
import org.eclipse.xsd.XSDSchema;

public class XSDRefactorGroupActionDelegate extends RefactorGroupActionDelegate {

	public XSDRefactorGroupActionDelegate() {
		super();
	}

	/**
	 * Fills the menu with applicable refactor sub-menues
	 * @param menu The menu to fill
	 */
	protected void fillMenu(Menu menu) {
		if (fSelection == null) {
			return;
		}
		if (workbenchPart != null) {
			IWorkbenchPartSite site = workbenchPart.getSite();
			if (site == null)
				return;
	
			IEditorPart editor = site.getPage().getActiveEditor();
			if (editor != null) {              
                XSDSchema schema = (XSDSchema)editor.getAdapter(XSDSchema.class);
                ISelectionMapper mapper = (ISelectionMapper)editor.getAdapter(ISelectionMapper.class);
			    if (schema != null)
                {                
                    ISelection selection = mapper != null ? mapper.mapSelection(fSelection) : fSelection;                    
					RefactorActionGroup refactorMenuGroup = new XSDRefactorActionGroup(selection, schema);
					RefactorGroupSubMenu subMenu = new RefactorGroupSubMenu(refactorMenuGroup);
					subMenu.fill(menu, -1);
				}				
			}
		
		}
	
	}
}
