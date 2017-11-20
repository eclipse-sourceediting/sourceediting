/**
 *  Copyright (c) 2016 Angelo ZERR and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Alina Marin <alina@mx1.ibm.com> - fixed some stuff to improve the synch between the editor and the model.
 */
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONStructure;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.document.JSONException;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.core.util.JSONUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class JSONPairImpl extends JSONStructureImpl implements IJSONPair {

	private String name;
	private ITextRegion nameRegion = null;
	private ITextRegion equalRegion = null;
	private JSONObjectImpl ownerObject = null;
	private IJSONValue value;

	@Override
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		name = name.substring(1, name.length() - 1); // remove start/end quote
		String oldName = this.name;
		this.name = name;
		notify(CHANGE, this, oldName, name, getStartOffset());
	}

	@Override
	public String getNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeValue() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONNode cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getNodeType() {
		return PAIR_NODE;
	}

	public void setOwnerObject(JSONObjectImpl ownerObject) {
		this.ownerObject = ownerObject;
	}

	public JSONObjectImpl getOwnerObject() {
		return ownerObject;
	}

	@Override
	public IJSONValue getValue() {
		return value;
	}

	public void setNameRegion(ITextRegion nameRegion) {
		this.nameRegion = nameRegion;
	}

	@Override
	public ITextRegion getNameRegion() {
		return nameRegion;
	}

	@Override
	public int getStartOffset() {
		if (this.getStartStructuredDocumentRegion() == null)
			return 0;
		int offset = this.getStartStructuredDocumentRegion().getStartOffset();
		// if (this.value != null) {
		// if (value.getNodeType() == IJSONNode.ARRAY_NODE)
		// return (offset + this.value.getStartOffset());
		// //return (offset);
		// }
		// if (this.nameRegion != null) {
		// return (offset + this.nameRegion.getStart());
		// }
		// if (this.equalRegion != null) {
		// return (offset + this.equalRegion.getStart());
		// }
		// if (this.value != null) {
		// return (offset + this.value.getStartOffset());
		// }
		return offset;
	}

	@Override
	public int getEndOffset() {
		if (this.getStartStructuredDocumentRegion() == null)
			return 0;
		int offset = this.getStartStructuredDocumentRegion().getEndOffset();
		if (this.value != null) {
			// if (value.getNodeType() == IJSONNode.OBJECT_NODE ||
			// value.getNodeType() == IJSONNode.ARRAY_NODE )
			return (this.value.getEndOffset());
		}
		// if (this.equalRegion != null) {
		// return (offset + this.equalRegion.getEnd());
		// }
		// if (this.nameRegion != null) {
		// return (offset + this.nameRegion.getEnd());
		// }
		return offset;
	}

	public void setEqualRegion(ITextRegion equalRegion) {
		this.equalRegion = equalRegion;
	}

	@Override
	public short getNodeValueType() {
		if (value != null) {
			return value.getNodeType();
		}
		return -1;
	}

	@Override
	public String getSimpleValue() {
		if (value == null) {
			return null;
		}
		return value.getSimpleValue();
	}
	
	@Override
	public String getValueRegionType() {
		if (value != null) {
			if (value.getStartStructuredDocumentRegion() == null) {
				return null;
			}
			return value.getStartStructuredDocumentRegion().getType();
		}
		return null;
	}

	public void setValue(IJSONValue value) {
		((JSONValueImpl) value).setParentNode(ownerObject);
		((JSONValueImpl) value).setOwnerPairNode(this);
		this.value = value;
		notify(CHANGE, this, null, this.value, getStartOffset());
	}
	
	public void updateValue(IJSONValue value) {
		IJSONValue oldValue = this.value;
		((JSONValueImpl) value).setParentNode(ownerObject);
		((JSONValueImpl) value).setOwnerPairNode(this);
		this.value = value;
		notify(CHANGE, null, oldValue, this.value, getStartOffset());
	}

	@Override
	public IStructuredDocumentRegion getEndStructuredDocumentRegion() {
		if (value != null) {
			return ((IJSONStructure) value).getStartStructuredDocumentRegion();
		}
		return super.getEndStructuredDocumentRegion();
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		String tagName = getName();
		buffer.append(tagName);
		IStructuredDocumentRegion startStructuredDocumentRegion = getStartStructuredDocumentRegion();
		if (startStructuredDocumentRegion != null) {
			buffer.append('@');
			buffer.append(startStructuredDocumentRegion.toString());
		}
		IStructuredDocumentRegion endStructuredDocumentRegion = getEndStructuredDocumentRegion();
		if (endStructuredDocumentRegion != null) {
			buffer.append('@');
			buffer.append(endStructuredDocumentRegion.toString());
		}
		return buffer.toString();

	}
}
