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
package org.eclipse.wst.dtd.core;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;


public class ParameterEntityReference extends NamedTopLevelNode {

	public ParameterEntityReference(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode, DTDRegionTypes.COMMENT_START);
	}

	public Image getImage() {
		return DTDPlugin.getInstance().getImage(DTDResource.ENTITYREFERENCEICON);
	}

	public String getName() {
		return getStructuredDocumentRegion().getText();
	}

	public void setReferencedEntity(String name) {
		beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_PARM_ENTITY_REF_CHG_ENTITY_REF")); //$NON-NLS-1$
		setReferencedEntity(this, name);
		endRecording(this);
	}

	public void setReferencedEntity(Object requestor, String name) {
		replaceText(requestor, getStructuredDocumentRegion().getStartOffset(), getStructuredDocumentRegion().getLength(), "%" + name + ";"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getReferencedEntity() {
		String text = getName();
		return getName().substring(1, text.length() - 1);
	}

	private Entity cachedEntity = null;

	public Entity getEntityObject() {
		if (cachedEntity != null && !cachedEntity.getName().equals(getReferencedEntity())) {
			// if we have a cached entity, but the name doesnt match, 
			// null it now, so we perform a lookup
			cachedEntity = null;
		}

		if (cachedEntity == null) {
			List nodes = getDTDFile().getNodes();
			for (int i = 0; i < nodes.size(); i++) {
				DTDNode node = (DTDNode) nodes.get(i);
				if (node instanceof Entity) {
					Entity entity = (Entity) node;
					if (entity.isParameterEntity() && entity.getName().equals(getReferencedEntity())) {
						cachedEntity = entity;
					}
				}
			}
		}
		return cachedEntity;
	}



	public class StartEndPair {
		public int startOffset, endOffset;
	}

	private void getStartAndEndOffsetForText(StartEndPair pair) {
		RegionIterator iter = iterator();
		ITextRegion commentStartTag = getStartTag(iter);
		ITextRegion endCommentTag = getNextRegion(iter, DTDRegionTypes.COMMENT_END);
		pair.endOffset = getStructuredDocumentRegion().getEndOffset();
		if (commentStartTag != null) {
			pair.startOffset = getStructuredDocumentRegion().getEndOffset(commentStartTag);
		}
		if (endCommentTag != null) {
			pair.endOffset = getStructuredDocumentRegion().getEndOffset(endCommentTag);
		}
	}

	public String getText() {
		String text = getStructuredDocumentRegion().getText();
		int flatNodeStart = getStructuredDocumentRegion().getStartOffset();
		StartEndPair pair = new StartEndPair();
		getStartAndEndOffsetForText(pair);
		return text.substring(pair.startOffset - flatNodeStart, pair.endOffset - flatNodeStart);
	}

	public void setText(String newText) {
		beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_PARM_ENTITY_REF_COMMENT_CHG")); //$NON-NLS-1$
		int flatNodeStart = getStructuredDocumentRegion().getStartOffset();
		StartEndPair pair = new StartEndPair();
		getStartAndEndOffsetForText(pair);
		replaceText(this, pair.startOffset, pair.endOffset - pair.startOffset, newText);
		endRecording(this);
	}

}
