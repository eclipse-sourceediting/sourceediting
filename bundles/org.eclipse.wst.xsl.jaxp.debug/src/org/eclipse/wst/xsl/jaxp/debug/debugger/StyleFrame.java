/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalio) - cleanup findbug errors.
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.debugger;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>xsl:template</code> that forms part of the call stack.
 * 
 * @author Doug Satchwell
 */
public abstract class StyleFrame
{
	private static int nextId;
	private final StyleFrame parent;
	private final List<StyleFrame> children = new ArrayList<StyleFrame>();
	private final int id;

	/**
	 * Create a new instance of this using the given frame as its parent (may be null if this is the root frame).
	 * 
	 * @param parent the parent frame
	 */
	public StyleFrame(StyleFrame parent)
	{
		this.id = nextId++;
		this.parent = parent;
		if (parent != null)
			parent.addChild(this);
	}
	
	/**
	 * A unique id for this frame
	 * 
	 * @return a unique id for this frame
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Get the name of this frame.
	 * 
	 * @return the name of this
	 */
	public abstract String getName();

	/**
	 * Get the file in which this frame is found.
	 * 
	 * @return the filename for this
	 */
	public abstract String getFilename();

	/**
	 * Get the line number at which the frame is currently located.
	 * 
	 * @return the line number at which this is held
	 */
	public abstract int getCurrentLine();

	/**
	 * Get a list of <code>Variables</code>'s
	 * 
	 * @return the list of variables for this frame
	 */
	public abstract List<?> getVariableStack();

	/**
	 * Get the parent of this.
	 * 
	 * @return the parent frame
	 */
	public StyleFrame getParent()
	{
		return parent;
	}

	/**
	 * Add a child frame to this.
	 * 
	 * @param child the frame to add
	 */
	public void addChild(StyleFrame child)
	{
		children.add(child);
	}

	/**
	 * Remove a child frame from this.
	 * 
	 * @param child the frame to remove
	 */
	public void removeChild(StyleFrame child)
	{
		children.remove(child);
	}

	/**
	 * Get the children of this
	 * 
	 * @return a list of <code>StyleFrame</code>'s
	 */
	public List<StyleFrame> getChildren()
	{
		return children;
	}

	/**
	 * Creates a string in the format <i>file</i>|<i>frameId</i>|<i>lineNumber</i>|<i>name</i>.
	 * Since pipe is used as a separator, the name has any pipe (|) characters replaced with the literal '%@_PIPE_@%'
	 */
	public String toString()
	{
		String safename = getName().replaceAll("\\|", "%@_PIPE_@%");  //$NON-NLS-1$//$NON-NLS-2$
		return getFilename() + "|" + getId() + "|" + getCurrentLine() + "|" + safename;  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
	}
}
