/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;


public class NodeList {
	protected DTDFile dtdFile;

	ArrayList listNodes = new ArrayList();

	protected String listType;

	public NodeList(DTDFile dtdFile, String tokenType) {
		listType = tokenType;
		this.dtdFile = dtdFile;
	}

	public String getImagePath() {
		if (listType == DTDRegionTypes.ELEMENT_TAG) {
			return DTDResource.FLDR_EL;
		}
		else if (listType == DTDRegionTypes.ENTITY_TAG) {
			return DTDResource.FLDR_ENT;
		}
		else if (listType == DTDRegionTypes.NOTATION_TAG) {
			return DTDResource.FLDR_NOT;
		}
		else if (listType == DTDRegionTypes.COMMENT_START) {
			return DTDResource.FLDR_COMM;
		}
		else if (listType == DTDRegionTypes.ATTLIST_TAG) {
			return DTDResource.FLDR_ATTLIST;
		}
		else if (listType == DTDRegionTypes.UNKNOWN_CONTENT) {
			return DTDResource.FLDR_UNREC;
		}
		else
			return null;
	}

	public String getListType() {
		return listType;
	}

	public String getName() {
		if (listType == DTDRegionTypes.ELEMENT_TAG) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_ELEMENTS; //$NON-NLS-1$
		}
		else if (listType == DTDRegionTypes.ENTITY_TAG) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_ENTITIES; //$NON-NLS-1$
		}
		else if (listType == DTDRegionTypes.NOTATION_TAG) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_NOTATIONS; //$NON-NLS-1$
		}
		else if (listType == DTDRegionTypes.COMMENT_START) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_COMMENTS; //$NON-NLS-1$
		}
		else if (listType == DTDRegionTypes.ATTLIST_TAG) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_ATTRIBUTES; //$NON-NLS-1$
		}
		else if (listType == DTDRegionTypes.UNKNOWN_CONTENT) {
			return DTDCoreMessages._UI_LABEL_NODE_LIST_OTHER; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public ArrayList getNodes() {
		listNodes.clear();
		Iterator iter = dtdFile.getNodes().iterator();
		while (iter.hasNext()) {
			DTDNode node = (DTDNode) iter.next();
			if (listType == DTDRegionTypes.ELEMENT_TAG && (node instanceof Element || node instanceof ParameterEntityReference)) {
				listNodes.add(node);
			}
			else if (listType == DTDRegionTypes.ATTLIST_TAG && node instanceof AttributeList) {
				listNodes.add(node);
			}
			else if (listType == DTDRegionTypes.ENTITY_TAG && node instanceof Entity) {
				listNodes.add(node);
			}
			else if (listType == DTDRegionTypes.NOTATION_TAG && node instanceof Notation) {
				listNodes.add(node);
			}
			else if (listType == DTDRegionTypes.COMMENT_START && node instanceof Comment) {
				listNodes.add(node);
			}
			else if (listType == DTDRegionTypes.UNKNOWN_CONTENT && node instanceof Unrecognized) {
				listNodes.add(node);
			}
		}
		return listNodes;
	}
}
