/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 ********************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

public class AnnotationMsg {
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
