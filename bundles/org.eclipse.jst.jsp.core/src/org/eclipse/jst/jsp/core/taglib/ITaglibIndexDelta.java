/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

import org.eclipse.core.resources.IProject;

/**
 * Describes changes to the known records within the TaglibIndex.
 * <p>
 * @noimplement This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.2
 */
public interface ITaglibIndexDelta {

	/**
	 * Status constant indicating that the record has been added. Note that an
	 * added taglib record delta has no children, as they are all implicitely
	 * added.
	 */
	public int ADDED = 1;
	/**
	 * Status constant indicating that the record has been changed, as
	 * described by the change flags.
	 */
	public int CHANGED = 4;
	/**
	 * Status constant indicating that the record has been removed. Note that
	 * a removed taglib element delta has no children, as they are all
	 * implicitely removed.
	 */
	public int REMOVED = 2;


	/**
	 * Returns deltas for the affected (added, removed, or changed) records.
	 * 
	 * @return
	 */
	ITaglibIndexDelta[] getAffectedChildren();

	/**
	 * @return the type of change, one of ADDED, CHANGED, or REMOVED
	 */
	int getKind();
	
	/**
	 * @return the IProject in which this delta originated
	 */
	IProject getProject();


	/**
	 * @return the record that was changed
	 */
	ITaglibRecord getTaglibRecord();

	/**
	 * Accepts the given visitor. The only kinds of index deltas visited are
	 * <code>ADDED</code>, <code>REMOVED</code>, and
	 * <code>CHANGED</code>. The visitor's <code>visit</code> method is
	 * called with this index delta if applicable. If the visitor returns
	 * <code>true</code>, the resource delta's children are also visited.
	 * 
	 * @param visitor
	 *            the visitor
	 * @exception CoreException
	 *                if the visitor failed with this exception.
	 * @see IResourceDeltaVisitor#visit(ITaglibIndexDeltaVisitor)
	 */
	// public void accept(ITaglibIndexDeltaVisitor visitor);
}
