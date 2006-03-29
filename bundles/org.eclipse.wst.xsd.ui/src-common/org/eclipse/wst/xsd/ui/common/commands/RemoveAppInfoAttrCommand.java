/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.commands;

import org.eclipse.gef.commands.Command;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class RemoveAppInfoAttrCommand extends Command 
{
	Element hostElement;
	Attr attr;
	
	public RemoveAppInfoAttrCommand(String label, Element hostElement, Attr attr)
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
