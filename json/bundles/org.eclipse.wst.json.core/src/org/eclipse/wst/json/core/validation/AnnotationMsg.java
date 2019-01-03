/*******************************************************************************
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.AnnotationMsg
 *                                           modified in order to process JSON Objects.     
 ********************************************************************************/
package org.eclipse.wst.json.core.validation;

public class AnnotationMsg {
	
	public static final String ID = AnnotationMsg.class.getName();
	
	public static String PROBMLEM_ID = "ProblemId"; //$NON-NLS-1$
	public static String LENGTH = "Length"; //$NON-NLS-1$
	public static String ATTRVALUETEXT = "AttributeValueText"; //$NON-NLS-1$
	public static String ATTRVALUENO = "AttributeValueNo"; //$NON-NLS-1$
	public static String ATTRNO = "AttrNo"; //$NON-NLS-1$
	private int problemId;
	private Object attributeValueText;
	private int length;
	
	public AnnotationMsg(int problemId, Object attributeValueText, int length) {
		super();
		this.problemId = problemId;
		this.attributeValueText = attributeValueText;
		this.length = length;
	}
	public int getProblemId() {
		return problemId;
	}

	public Object getAttributeValueText() {
		return attributeValueText;
	}

	public int getLength(){
		return length;
	}
}
