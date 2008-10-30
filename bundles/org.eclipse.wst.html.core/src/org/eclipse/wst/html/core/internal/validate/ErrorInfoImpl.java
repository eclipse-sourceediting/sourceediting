/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;



import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

final class ErrorInfoImpl extends AbstractErrorInfo {

	private Node target = null;

	//	private Segment seg = null;
	/**
	 */
	public ErrorInfoImpl(int state, Segment errorSeg, Node target) {
		super(state, errorSeg);
		this.target = target;
	}

	/**
	 */
	public String getHint() {
		switch (target.getNodeType()) {
			case Node.ATTRIBUTE_NODE :
				switch (getState()) {
					case UNDEFINED_VALUE_ERROR :
					case MISMATCHED_VALUE_ERROR :
					case UNCLOSED_ATTR_VALUE :
					case RESOURCE_NOT_FOUND :
						//D210422
						return ((Attr) target).getValue();
					default :
						return target.getNodeName();
				}
			case Node.TEXT_NODE :
				return ((Text) target).getData();
			case Node.ELEMENT_NODE :
			default :
				return target.getNodeName();
		}
	}

	public short getTargetType() {
		return target.getNodeType();
	}
}
