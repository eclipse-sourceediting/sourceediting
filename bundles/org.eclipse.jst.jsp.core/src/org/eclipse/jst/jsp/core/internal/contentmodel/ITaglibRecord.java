/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;

public interface ITaglibRecord {
	short JAR = 1 << 2;
	short TAGDIR = 1 << 4;
	short TLD = 1 << 1;
	short URL = 1;
	short WEB_XML = 1 << 3;

	short getRecordType();
}
