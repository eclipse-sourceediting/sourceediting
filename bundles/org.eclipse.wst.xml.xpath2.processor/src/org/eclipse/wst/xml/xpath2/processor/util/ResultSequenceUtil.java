/*******************************************************************************
 * Copyright (c) 2011, Jesper Steen Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moller - initial API and implementation
 *     Jesper Steen Moller  - bug 340933 - Migrate to new XPath2 API
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.util;

import org.eclipse.wst.xml.xpath2.api.ResultBuffer;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;

public class ResultSequenceUtil {

	public static org.eclipse.wst.xml.xpath2.api.ResultSequence oldToNew(
			ResultSequence result) {
		if (result instanceof org.eclipse.wst.xml.xpath2.api.ResultSequence) 
			return (org.eclipse.wst.xml.xpath2.api.ResultSequence)result;
		
		ResultBuffer buff = new ResultBuffer();
		for (int i = 0; i < result.size(); ++i) {
			buff.add(result.get(i));
		}
		return buff.getSequence();
	}

	public static ResultSequence newToOld(
			org.eclipse.wst.xml.xpath2.api.ResultSequence result) {
		if (result instanceof ResultSequence) 
			return (ResultSequence)result;
		
		ResultSequence rs = ResultSequenceFactory.create_new();
		for (int i = 0; i < result.size(); ++i) {
			rs.add((AnyType) result.item(i));
		}
		return rs;
	}

}
