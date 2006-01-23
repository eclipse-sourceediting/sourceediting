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
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorActionGroup;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorGroupActionDelegate;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorGroupSubMenu;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

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
				IEditorInput editorInput = editor.getEditorInput();
				if(editorInput instanceof IFileEditorInput){
					IFileEditorInput fileInput = (IFileEditorInput)editorInput;
					XSDSchema schema = createXMLSchema(fileInput.getFile(), resourceSet);
					RefactorActionGroup refactorMenuGroup = new XSDRefactorActionGroup(fSelection, schema);
					RefactorGroupSubMenu subMenu = new RefactorGroupSubMenu(refactorMenuGroup);
					subMenu.fill(menu, -1);
				}
				
			}
		
		}
	
	}
	
	public static XSDSchema createXMLSchema(IFile file, ResourceSet set)  {
		
		URI uri = URI.createFileURI(file.getLocation().toString());
		XSDSchema schema = XSDFactory.eINSTANCE.createXSDSchema();
		// we need this model to be able to get locations
		try {
			IStructuredModel structuredModel = StructuredModelManager.getModelManager().getModelForEdit(file);
			IDOMModel domModel = (IDOMModel) structuredModel;
			Resource xsdResource = new XSDResourceImpl();
			xsdResource.setURI(uri);
			schema = XSDFactory.eINSTANCE.createXSDSchema();
			xsdResource.getContents().add(schema);
			schema.setElement(domModel.getDocument().getDocumentElement());
			if(set != null){
				set.getResources().add(xsdResource);
			}
		} catch (IOException e) {
			// do nothing
		} catch (CoreException e) {
			// do nothing
		}
		return schema;
	}


}
