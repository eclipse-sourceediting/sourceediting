/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.emf;

/**
 * This interface can be implemented by an IDomainModel to augment
 * getElementProperty for any DTD object.
 */
public interface DTDExtender {
	public Object getElementProperty(DTDObject dtdObject, Object object);
}
