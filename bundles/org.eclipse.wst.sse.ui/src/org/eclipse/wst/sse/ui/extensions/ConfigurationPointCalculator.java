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
package org.eclipse.wst.sse.ui.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.MultiPageEditorSite;

public class ConfigurationPointCalculator {
	public static final String DESIGN = ".design"; //$NON-NLS-1$
	public static final String SOURCE = ".source"; //$NON-NLS-1$

	public static String[] getConfigurationPoints(IEditorPart part, String contentType, String subContext, Class rootClass) {
		ConfigurationPointCalculator calculator = new ConfigurationPointCalculator();
		calculator.setContentType(contentType);
		calculator.setPart(part);
		calculator.setRootClass(rootClass);
		calculator.setSubContext(subContext);
		return calculator.getConfigurationPoints();
	}

	protected String fContentType = null;
	protected IEditorPart fPart = null;

	protected Class fRootClass = null;
	protected String fSubContext = null;

	/**
	 * 
	 */
	public ConfigurationPointCalculator() {
		super();
	}

	public String[] getConfigurationPoints() {
		List points = new ArrayList(2);

		IEditorSite site = fPart.getEditorSite();
		String id = site.getId();
		if (id != null && id.length() > 0 && !id.equals(fRootClass.getName()))
			points.add(id);

		if (site instanceof MultiPageEditorSite) {
			String multipageID = ((MultiPageEditorSite) site).getMultiPageEditor().getSite().getId();
			if (!points.contains(multipageID))
				points.add(multipageID);
			String sourcePageID = ((MultiPageEditorSite) site).getMultiPageEditor().getSite().getId() + ".source"; //$NON-NLS-1$
			if (!points.contains(sourcePageID))
				points.add(sourcePageID);
		}
		if (site instanceof MultiPageEditorSite) {
			String multipageClassName = ((MultiPageEditorSite) site).getMultiPageEditor().getClass().getName();
			if (!points.contains(multipageClassName))
				points.add(multipageClassName);
		}
		Class editorClass = fPart.getClass();
		while (editorClass != null && fRootClass != null && !editorClass.equals(fRootClass)) {
			if (!points.contains(editorClass.getName()))
				points.add(editorClass.getName());
			editorClass = editorClass.getSuperclass();
		}
		if (fContentType != null && !points.contains(fContentType))
			points.add(fContentType);
		if (!points.contains(fRootClass.getName()))
			points.add(fRootClass.getName());
		return (String[]) points.toArray(new String[0]);
	}

	/**
	 * @return Returns the contentType.
	 */
	public String getContentType() {
		return fContentType;
	}

	/**
	 * @return Returns the part.
	 */
	public IEditorPart getPart() {
		return fPart;
	}

	/**
	 * @return Returns the rootClass.
	 */
	public Class getRootClass() {
		return fRootClass;
	}

	/**
	 * @return Returns the subContext.
	 */
	public String getSubContext() {
		return fSubContext;
	}

	/**
	 * @param contentType The contentType to set.
	 */
	public void setContentType(String contentType) {
		fContentType = contentType;
	}

	/**
	 * @param part The part to set.
	 */
	public void setPart(IEditorPart part) {
		fPart = part;
	}

	/**
	 * @param rootClass The rootClass to set.
	 */
	public void setRootClass(Class rootClass) {
		fRootClass = rootClass;
	}

	/**
	 * @param subContext The subContext to set.
	 */
	public void setSubContext(String subContext) {
		fSubContext = subContext;
	}

}
