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

package org.eclipse.wst.dtd.core.internal.saxparser;

import java.util.Enumeration;
import java.util.Hashtable;

public class EntityPool {
	private Hashtable m_entity = new Hashtable();
	private Hashtable m_para = new Hashtable(); // parameter references

	public EntityPool() {
	}

	public boolean add(EntityDecl entity) {
		String name = entity.getNodeName();
		if (entity.isParameter()) {
			if (m_para.containsKey(name))
				return false;
			m_para.put(name, entity);
		}
		else {
			if (m_entity.containsKey(name))
				return false;
			m_entity.put(name, entity);
		}
		return true;
	}

	public EntityDecl refer(String name) {
		return (EntityDecl) m_entity.get(name);
	}

	public EntityDecl referPara(String name) {
		return (EntityDecl) m_para.get(name);
	}

	/**
	 * Return an <CODE>Enumeration</CODE> of <CODE>EntityDecl</CODE>.
	 * 
	 * @see org.eclipcse.wst.dtd.parser.EntityDecl
	 */
	public Enumeration elements() {
		return m_entity.elements();
	}

	/**
	 * Return an <CODE>Enumeration</CODE> of <CODE>EntityDecl</CODE> for
	 * parameter entities.
	 * 
	 * @see org.eclipcse.wst.dtd.parser.EntityDecl
	 */
	public Enumeration parameterEntityElements() {
		return m_para.elements();
	}

	/**
	 * For DTD#getEntities();
	 */
	Hashtable getEntityHash() {
		return this.m_entity;
	}
}
