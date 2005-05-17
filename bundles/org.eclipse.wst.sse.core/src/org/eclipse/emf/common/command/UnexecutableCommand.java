/**
 * <copyright> 
 *
 * Copyright (c) 2002-2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: UnexecutableCommand.java,v 1.2 2005/05/17 18:23:07 nitind Exp $
 */
package org.eclipse.emf.common.command;



/**
 * A singleton {@link UnexecutableCommand#INSTANCE} that cannot execute.
 */
public class UnexecutableCommand extends AbstractCommand {
	/**
	 * The one instance of this object.
	 */
	public static final UnexecutableCommand INSTANCE = new UnexecutableCommand();

	/**
	 * Only one private instance is created.
	 */
	private UnexecutableCommand() {
		super(EMFCommonMessages._UI_UnexecutableCommand_label, EMFCommonMessages._UI_UnexecutableCommand_description);
	}

	/**
	 * Returns <code>false</code>.
	 * 
	 * @return <code>false</code>.
	 */
	public boolean canExecute() {
		return false;
	}

	/**
	 * Throws an exception if it should ever be called.
	 * 
	 * @exception UnsupportedOperationException
	 *                always.
	 */
	public void execute() {
		throw new RuntimeException("UnsupportedOperation"); //$NON-NLS-1$
	}

	/**
	 * Returns <code>false</code>.
	 * 
	 * @return <code>false</code>.
	 */
	public boolean canUndo() {
		return false;
	}

	/**
	 * Throws an exception if it should ever be called.
	 * 
	 * @exception UnsupportedOperationException
	 *                always.
	 */
	public void redo() {
		throw new RuntimeException("UnsupportedOperation"); //$NON-NLS-1$
	}
}
