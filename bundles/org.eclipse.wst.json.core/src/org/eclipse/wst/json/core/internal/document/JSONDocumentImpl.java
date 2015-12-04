/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONBooleanValue;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONNullValue;
import org.eclipse.wst.json.core.document.IJSONNumberValue;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONStringValue;
import org.eclipse.wst.json.core.document.JSONException;

public class JSONDocumentImpl extends JSONStructureImpl implements
		IJSONDocument {

	private JSONModelImpl fModel = null;

	JSONDocumentImpl() {
		super();
	}

	JSONDocumentImpl(JSONDocumentImpl that) {
		super(that);
	}

	public IJSONModel getModel() {
		return fModel;
	}

	void setModel(JSONModelImpl model) {
		this.fModel = model;
	}

	@Override
	public IJSONObject createJSONObject() {
		JSONObjectImpl object = new JSONObjectImpl();
		object.setOwnerDocument(this);
		return object;
	}

	@Override
	public IJSONArray createJSONArray() {
		JSONArrayImpl array = new JSONArrayImpl();
		array.setOwnerDocument(this);
		return array;
	}

	@Override
	public IJSONPair createJSONPair(String name) {
		JSONPairImpl pair = new JSONPairImpl();
		pair.setOwnerDocument(this);
		pair.setName(name);
		return pair;
	}

	@Override
	public IJSONBooleanValue createBooleanValue() {
		JSONBooleanValueImpl value = new JSONBooleanValueImpl();
		value.setOwnerDocument(this);
		return value;
	}

	@Override
	public IJSONNumberValue createNumberValue() {
		JSONNumberValueImpl value = new JSONNumberValueImpl();
		value.setOwnerDocument(this);
		return value;
	}

	@Override
	public IJSONNullValue createNullValue() {
		JSONNullValueImpl value = new JSONNullValueImpl();
		value.setOwnerDocument(this);
		return value;
	}

	@Override
	public IJSONStringValue createStringValue() {
		JSONStringValueImpl value = new JSONStringValueImpl();
		value.setOwnerDocument(this);
		return value;
	}

	@Override
	public short getNodeType() {
		return DOCUMENT_NODE;
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

}
