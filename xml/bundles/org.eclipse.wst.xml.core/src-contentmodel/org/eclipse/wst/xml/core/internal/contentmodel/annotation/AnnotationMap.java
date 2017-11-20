/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.annotation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import com.ibm.icu.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation.AnnotationFileInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation.AnnotationFileParser;


/**
 * AnnotationMap
 */
public class AnnotationMap {
	protected List list = new Vector();
	protected Hashtable hashtable = new Hashtable();
	protected boolean isCaseSensitive = true;

	public AnnotationMap() {
	}

	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}

	public void addAnnotation(Annotation annotation) {
		String spec = annotation.getSpec();
		if (spec != null) {
			list.add(annotation);
			StringTokenizer st = new StringTokenizer(spec, "[]|\t\n\r\f "); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String cmNodeSpec = st.nextToken();
				addAnnotationForCMNodeSpec(cmNodeSpec, annotation);
			}
		}
	}

	protected void addAnnotationForCMNodeSpec(String cmNodeSpec, Annotation annotation) {
		String key = isCaseSensitive ? cmNodeSpec : cmNodeSpec.toLowerCase();
		List list = (List) hashtable.get(key);
		if (list == null) {
			list = new Vector();

			hashtable.put(key, list);
		}
		list.add(annotation);
	}

	public String getProperty(String cmNodeSpec, String propertyName) {
		String result = null;
		String key = isCaseSensitive ? cmNodeSpec : cmNodeSpec.toLowerCase();
		List annotationList = (List) hashtable.get(key);
		if (annotationList != null) {
			for (Iterator i = annotationList.iterator(); i.hasNext();) {
				Annotation annotation = (Annotation) i.next();
				result = annotation.getProperty(propertyName);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public String getProperty(CMNode cmNode, String propertyName) {
		String result = null;
		String cmNodeSpec = (String) cmNode.getProperty("spec"); //$NON-NLS-1$
		if (cmNodeSpec == null) {
			cmNodeSpec = cmNode.getNodeName();
		}
		if (cmNodeSpec != null) {
			result = getProperty(cmNodeSpec, propertyName);
		}
		return result;
	}

	public List getAnnotations() {
		return list;
	}

	public void load(String uri, String bundleId) throws Exception {
		AnnotationFileParser parser = new AnnotationFileParser();
		AnnotationFileInfo fileInfo = new AnnotationFileInfo(uri, bundleId);
		parser.parse(this, fileInfo);
	}
}
