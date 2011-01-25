/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
	/**
	 * The message arguments to list before the target information
	 */
	private String[] fPreTargetMsgArgs;

	/**
	 * The message arguments to list after the target information
	 */
	private String[] fPostTargetMsgArgs;

	/**
	 * <p>Create error info without any extra message arguments
	 * before or after the <code>target</code> information</p>
	 * 
	 * @param state the error state
	 * @param errorSeg the <code>Segment</code> to report the error on
	 * @param target the <code>Node</code> that is causing the error
	 */
	public ErrorInfoImpl(int state, Segment errorSeg, Node target) {
		super(state, errorSeg);
		this.target = target;
		this.fPreTargetMsgArgs = new String[0];
		this.fPostTargetMsgArgs = new String[0];
	}

	/**
	 * <p>Create error info with extra message arguments before and/or
	 * after the <code>target</code> information</p>
	 * 
	 * @param state the error state
	 * @param errorSeg the <code>Segment</code> to report the error on
	 * @param target the <code>Node</code> that is causing the error
	 * @param preTargetMsgArgs message arguments to place before the <code>target</code> information
	 * @param postTargetMsgInjections message arguments to place after the <code>target</code> information
	 */
	public ErrorInfoImpl(int state, Segment errorSeg, Node target, String[] preTargetMsgArgs, String[] postTargetMsgInjections) {
		super(state, errorSeg);
		this.target = target;
		this.fPreTargetMsgArgs = preTargetMsgArgs;
		this.fPostTargetMsgArgs = postTargetMsgInjections;
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

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.internal.validate.AbstractErrorInfo#getMessageArguments()
	 */
	public String[] getMessageArguments() {
		String[] messageInjections = new String[this.fPreTargetMsgArgs.length + 1 + this.fPostTargetMsgArgs.length];
		//copy the pre target message injections in
		System.arraycopy(this.fPreTargetMsgArgs, 0, messageInjections, 0, this.fPreTargetMsgArgs.length);
		int nextIndex = fPreTargetMsgArgs.length;

		//put the target message injection in
		switch (target.getNodeType()) {
			case Node.ATTRIBUTE_NODE :
				switch (getState()) {
					case UNDEFINED_VALUE_ERROR :
					case MISMATCHED_VALUE_ERROR :
					case UNCLOSED_ATTR_VALUE :
					case RESOURCE_NOT_FOUND :
						//D210422
						messageInjections[nextIndex] = ((Attr) target).getValue();
						break;
					default :
						messageInjections[nextIndex] = target.getNodeName();
						break;
				}
				break;
			case Node.TEXT_NODE :
				messageInjections[nextIndex] = ((Text) target).getData();
				break;
			case Node.ELEMENT_NODE :
			default :
				messageInjections[nextIndex] = target.getNodeName();
				break;
		}
		//copy the post target message injections in
		System.arraycopy(this.fPostTargetMsgArgs, 0, messageInjections, nextIndex+1, this.fPostTargetMsgArgs.length);
		return messageInjections;
	}

	public short getTargetType() {
		return target.getNodeType();
	}
}
