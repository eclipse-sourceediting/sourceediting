/*
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 */
package org.eclipse.wst.xsd.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.xsd.XSDConcreteComponent;

/**
 * WSDL Hyperlink that knows how to open links from wsdl files
 */
public class XSDHyperlink implements IHyperlink {
	private IRegion fRegion;
	private XSDConcreteComponent fComponent;

	public XSDHyperlink(IRegion region, XSDConcreteComponent component) {
		fRegion = region;
		fComponent = component;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		// if hyperlink points to schema already in editor, select the correct
		// node
		// if (fComponent.getRootContainer().equals(xsdSchema)) {
		// Node element = fComponent.getElement();
		// if (element instanceof IndexedRegion) {
		// IndexedRegion indexNode = (IndexedRegion) element;
		// textEditor.getTextViewer().setRangeIndication(indexNode.getStartOffset(),
		// indexNode.getEndOffset() - indexNode.getStartOffset(), true);
		// }
		// }
		// else {
		if (fComponent.getSchema() != null) {
			String schemaLocation = URIHelper.removePlatformResourceProtocol(fComponent.getSchema().getSchemaLocation());
			IPath schemaPath = new Path(schemaLocation);
			IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
			if (schemaFile != null && schemaFile.exists()) {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (workbenchWindow != null) {
					IWorkbenchPage page = workbenchWindow.getActivePage();
					try {
						IEditorPart editorPart = IDE.openEditor(page, schemaFile, true);
						if (editorPart instanceof XSDEditor) {
							((XSDEditor) editorPart).openOnGlobalReference(fComponent);
						}
					}
					catch (PartInitException pie) {
						Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
					}
				}
			}
		}
	}
}
