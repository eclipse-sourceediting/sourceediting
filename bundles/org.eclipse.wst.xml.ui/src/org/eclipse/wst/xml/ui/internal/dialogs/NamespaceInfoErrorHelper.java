/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.internal.dialogs;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceInfoManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.uriresolver.internal.util.IdResolver;

public class NamespaceInfoErrorHelper {

	protected List errorList;

	public NamespaceInfoErrorHelper() {
	}

	public String computeErrorMessage(List namespaceInfoList, IdResolver idResolver) {
		String result = null;
		Hashtable prefixTable = new Hashtable();
		Hashtable uriTable = new Hashtable();
		for (Iterator iterator = namespaceInfoList.iterator(); iterator.hasNext();) {
			NamespaceInfo nsInfo = (NamespaceInfo) iterator.next();
			nsInfo.normalize();

			String urikey = nsInfo.uri != null ? nsInfo.uri : ""; //$NON-NLS-1$
			NamespaceInfo nsInfo2 = null;
			if ((nsInfo2 = (NamespaceInfo) uriTable.get(urikey)) != null) {
				if (nsInfo.uri != null && nsInfo.uri.equals(nsInfo2.uri)) {
					result = XMLUIMessages._UI_WARNING_MORE_THAN_ONE_NS_WITH_NAME + "'" + nsInfo.uri + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					result = XMLUIMessages._UI_WARNING_MORE_THAN_ONE_NS_WITHOUT_NAME; //$NON-NLS-1$
				}
				break;
			} else {
				uriTable.put(urikey, nsInfo);
			}

			if (nsInfo.uri != null) {
				String key = nsInfo.prefix != null ? nsInfo.prefix : ""; //$NON-NLS-1$
				if (prefixTable.get(key) != null) {
					if (nsInfo.prefix != null) {
						result = XMLUIMessages._UI_WARNING_MORE_THAN_ONE_NS_WITH_PREFIX + "'" + nsInfo.prefix + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						break;
					} else {
						result = XMLUIMessages._UI_WARNING_MORE_THAN_ONE_NS_WITHOUT_PREFIX; //$NON-NLS-1$
						break;
					}
				} else {
					prefixTable.put(key, nsInfo);
				}

				if (nsInfo.locationHint != null && idResolver != null) {
					String grammarURI = idResolver.resolveId(nsInfo.locationHint, nsInfo.locationHint);
					if (!URIHelper.isReadableURI(grammarURI, false)) {
						result = XMLUIMessages._UI_WARNING_SCHEMA_CAN_NOT_BE_LOCATED + " '" + nsInfo.locationHint + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						break;
					}
				}
				if (idResolver != null && nsInfo.locationHint == null && !nsInfo.uri.equals(DOMNamespaceInfoManager.XSI_URI)) {
					result = XMLUIMessages._UI_WARNING_LOCATION_HINT_NOT_SPECIFIED + " '" + nsInfo.uri + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				}
			} else {
				if (nsInfo.prefix != null) {
					result = XMLUIMessages._UI_WARNING_NAMESPACE_NAME_NOT_SPECIFIED + " '" + nsInfo.prefix + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				}
			}
		}
		// additional tests
		if (result == null) {
			for (Iterator iterator = namespaceInfoList.iterator(); iterator.hasNext();) {
				NamespaceInfo nsInfo = (NamespaceInfo) iterator.next();
				nsInfo.normalize();
				if (nsInfo.uri != null && nsInfo.isPrefixRequired && nsInfo.prefix == null) {
					result = XMLUIMessages._UI_WARNING_PREFIX_NOT_SPECIFIED + " '" + nsInfo.uri + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				}
			}
		}

		return result;
	}
}
