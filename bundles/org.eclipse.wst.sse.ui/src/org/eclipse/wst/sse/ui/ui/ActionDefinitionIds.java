/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.ui;



/**
 * Defines the definitions ids for editor actions.
 */
public interface ActionDefinitionIds {
	public final static String QUICK_FIX = "org.eclipse.wst.sse.ui.edit.edit.text.java.correction.assist.proposals";//$NON-NLS-1$
	public final static String COMMENT = "org.eclipse.wst.sse.ui.edit.comment";//$NON-NLS-1$
	public final static String UNCOMMENT = "org.eclipse.wst.sse.ui.edit.uncomment";//$NON-NLS-1$
	public final static String TOGGLE_COMMENT = "org.eclipse.wst.sse.ui.edit.toggle.comment";//$NON-NLS-1$
	public final static String ADD_BLOCK_COMMENT = "org.eclipse.wst.sse.ui.edit.add.block.comment";//$NON-NLS-1$
	public final static String REMOVE_BLOCK_COMMENT = "org.eclipse.wst.sse.ui.edit.remove.block.comment";//$NON-NLS-1$
	public final static String CLEANUP_DOCUMENT = "org.eclipse.wst.sse.ui.edit.cleanup.document";//$NON-NLS-1$
	public final static String FORMAT_DOCUMENT = "org.eclipse.wst.sse.ui.edit.format.document";//$NON-NLS-1$
	public final static String FORMAT_ACTIVE_ELEMENTS = "org.eclipse.wst.sse.ui.edit.format.active.elements";//$NON-NLS-1$
	public final static String OPEN_FILE = "org.eclipse.wst.sse.ui.edit.open.file.from.source";//$NON-NLS-1$
	public final static String STRUCTURE_SELECT_ENCLOSING = "org.eclipse.wst.sse.ui.edit.structure.select.enclosing";//$NON-NLS-1$
	public final static String STRUCTURE_SELECT_NEXT = "org.eclipse.wst.sse.ui.edit.structure.select.next";//$NON-NLS-1$
	public final static String STRUCTURE_SELECT_PREVIOUS = "org.eclipse.wst.sse.ui.edit.structure.select.previous";//$NON-NLS-1$
	public final static String STRUCTURE_SELECT_HISTORY = "org.eclipse.wst.sse.ui.edit.structure.select.last";//$NON-NLS-1$
	public final static String INFORMATION = "org.eclipse.wst.sse.ui.edit.show.javadoc";//$NON-NLS-1$

	public final static String ADD_BREAKPOINTS = "org.eclipse.wst.sse.ui.edit.add.breakpoints";//$NON-NLS-1$
	public final static String MANAGE_BREAKPOINTS = "org.eclipse.wst.sse.ui.edit.manage.breakpoints";//$NON-NLS-1$
	public final static String EDIT_BREAKPOINTS = "org.eclipse.wst.sse.ui.edit.breakpoints.edit";//$NON-NLS-1$
	public final static String FIND_OCCURRENCES = "org.eclipse.wst.sse.ui.edit.search.find.occurrences";//$NON-NLS-1$
}
