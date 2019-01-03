/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.gef.commands.Command;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * @deprecated
 */
public class RemoveExtensionAttributerCommand extends Command 
{
	Element hostElement;
	Attr attr;
	
	public RemoveExtensionAttributerCommand(String label, Element hostElement, Attr attr)
	{
		super(label);
		this.hostElement = hostElement;
		this.attr = attr;
	}
	
	public void execute()
	{
		super.execute();
		hostElement.removeAttributeNode(attr);
	}
	
	public void undo()
	{
		super.undo();
		//TODO implement me
	}
}
