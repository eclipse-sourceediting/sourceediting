/*
* Copyright (c) 2002 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.ui.nsedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.uriresolver.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.XMLCatalogPlugin;

public class CommonAddNamespacesDialog extends Dialog {
	public static final String copyright = "(c) Copyright IBM Corporation 2002."; //$NON-NLS-1$
	protected Button okButton;
	protected String title;
	protected CommonAddNamespacesControl addNamespacesControl;
	protected List existingNamespaces;
	protected List namespaceInfoList;
	protected HashMap preferredPrefixTable = new HashMap();
	protected IPath resourceLocation;

	public CommonAddNamespacesDialog(Shell parentShell, String title, IPath resourceLocation, List existingNamespaces) {
		super(parentShell);
		this.resourceLocation = resourceLocation;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.title = title;
		this.existingNamespaces = existingNamespaces;
		preferredPrefixTable.put("http://schemas.xmlsoap.org/wsdl/", "wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://schemas.xmlsoap.org/wsdl/soap/", "soap"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://schemas.xmlsoap.org/wsdl/http/", "http"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://schemas.xmlsoap.org/wsdl/mime/", "mime"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://schemas.xmlsoap.org/soap/encoding/", "soapenc"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://schemas.xmlsoap.org/soap/envelope/", "soapenv"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://www.w3.org/2001/XMLSchema-instance", "xsi"); //$NON-NLS-1$ //$NON-NLS-2$
		preferredPrefixTable.put("http://www.w3.org/2001/XMLSchema", "xsd"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public int createAndOpen() {
		create();
		getShell().setText(title);
		Rectangle r = getShell().getBounds();
		getShell().setBounds(r.x + 80, r.y + 80, r.width, r.height);
		setBlockOnOpen(true);
		return open();
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		return control;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}



	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		addNamespacesControl = new CommonAddNamespacesControl(dialogArea, SWT.NONE, resourceLocation);
		List list = new ArrayList();

		addBuiltInNamespaces(list);
		XMLCatalog catalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
		addCatalogMapToList(catalog.getChildCatalog(XMLCatalog.USER_CATALOG_ID), list);
		addCatalogMapToList(catalog.getChildCatalog(XMLCatalog.SYSTEM_CATALOG_ID), list);

		computeAddablePrefixes(list, existingNamespaces);

		addNamespacesControl.setNamespaceInfoList(list);
		return dialogArea;
	}

	protected String getPreferredPrefix(String namespaceURI) {
		return (String) preferredPrefixTable.get(namespaceURI);
	}

	protected void addBuiltInNamespaces(List list) {
		String xsiNamespace = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
		String xsdNamespace = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
		if (!isAlreadyDeclared(xsiNamespace)) {
			list.add(new NamespaceInfo("http://www.w3.org/2001/XMLSchema-instance", "xsi", null)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!isAlreadyDeclared(xsdNamespace)) {
			list.add(new NamespaceInfo("http://www.w3.org/2001/XMLSchema", "xsd", null)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected void addCatalogMapToList(XMLCatalog catalog, List list) {
		for (Iterator i = catalog.getEntries().iterator(); i.hasNext();) {
			XMLCatalogEntry entry = (XMLCatalogEntry) i.next();
			if (entry.getType() == XMLCatalogEntry.PUBLIC && entry.getURI().endsWith(".xsd")) { //$NON-NLS-1$
				if (!isAlreadyDeclared(entry.getKey())) {
					NamespaceInfo namespaceInfo = new NamespaceInfo(entry.getKey(), "xx", null); //$NON-NLS-1$
					list.add(namespaceInfo);
				}
			}
		}
	}

	protected boolean isAlreadyDeclared(String namespaceURI) {
		boolean result = false;
		for (Iterator i = existingNamespaces.iterator(); i.hasNext();) {
			NamespaceInfo namespaceInfo = (NamespaceInfo) i.next();
			if (namespaceURI.equals(namespaceInfo.uri)) {
				result = true;
				break;
			}
		}
		return result;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			namespaceInfoList = addNamespacesControl.getNamespaceInfoList();
		}
		super.buttonPressed(buttonId);
	}

	public List getNamespaceInfoList() {
		return namespaceInfoList;
	}

	public void computeAddablePrefixes(List addableList, List exisitingList) {
		HashMap map = new HashMap();
		for (Iterator i = exisitingList.iterator(); i.hasNext();) {
			NamespaceInfo info = (NamespaceInfo) i.next();
			if (info.prefix != null) {
				map.put(info.prefix, info);
			}
		}
		for (Iterator i = addableList.iterator(); i.hasNext();) {
			NamespaceInfo info = (NamespaceInfo) i.next();
			if (info.uri != null) {
				String prefix = (String) preferredPrefixTable.get(info.uri);
				info.prefix = getUniquePrefix(map, prefix, info.uri);
				map.put(info.prefix, info);
			}
		}
	}

	private String getUniquePrefix(HashMap prefixMap, String prefix, String uri) {
		if (prefix == null) {
			int lastIndex = uri.lastIndexOf('/');
			if (lastIndex == uri.length() - 1) {
				uri = uri.substring(0, lastIndex);
				lastIndex = uri.lastIndexOf('/');
			}
			prefix = uri.substring(lastIndex + 1);
			if (prefix.length() > 20 || prefix.indexOf(':') != -1) {
				prefix = null;
			}
		}
		if (prefix == null) {
			prefix = "p"; //$NON-NLS-1$
		}
		if (prefixMap.get(prefix) != null) {
			String base = prefix;
			for (int count = 0; prefixMap.get(prefix) != null; count++) {
				prefix = base + count;
			}
		}
		return prefix;
	}
}
