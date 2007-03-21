/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;

public class TaglibIndexDelta implements ITaglibIndexDelta {
	private Collection fChildren;
	private int fExplicitKind = -1;
	private int fImplicitKind = -1;
	private IProject fProject;
	private ITaglibRecord fTaglibRecord = null;
	long time;
	Object trigger = null;

	TaglibIndexDelta(IProject project, ITaglibRecord record, int kind) {
		fProject = project;
		fTaglibRecord = record;
		fExplicitKind = kind;
		time = System.currentTimeMillis();
	}

	void addChildDelta(ITaglibIndexDelta delta) {
		if (fChildren == null)
			fChildren = new ArrayList();
		fChildren.add(delta);
		fImplicitKind = -1;
	}

	private int computeKind() {
		int added = 0;
		int removed = 0;

		ITaglibIndexDelta[] children = (ITaglibIndexDelta[]) fChildren.toArray(new ITaglibIndexDelta[fChildren.size()]);
		for (int i = 0; i < children.length; i++) {
			int kind = children[i].getKind();
			if (kind == ITaglibIndexDelta.ADDED)
				added++;
			if (kind == ITaglibIndexDelta.REMOVED)
				removed++;
			if (added > 0 && removed > 0)
				break;
		}
		if (added > 0 && removed > 0) {
			return ITaglibIndexDelta.CHANGED;
		}
		else if (added > 0) {
			return ITaglibIndexDelta.ADDED;
		}
		else if (removed > 0) {
			return ITaglibIndexDelta.REMOVED;
		}
		else {
			return ITaglibIndexDelta.CHANGED;
		}
	}

	public ITaglibIndexDelta[] getAffectedChildren() {
		if (fChildren == null) {
			return new ITaglibIndexDelta[0];
		}
		return (ITaglibIndexDelta[]) fChildren.toArray(new ITaglibIndexDelta[fChildren.size()]);
	}

	public int getKind() {
		if (fChildren == null) {
			return fExplicitKind;
		}
		if (fImplicitKind == -1) {
			fImplicitKind = computeKind();
		}
		return fImplicitKind;
	}

	public IProject getProject() {
		return fProject;
	}

	public ITaglibRecord getTaglibRecord() {
		return fTaglibRecord;
	}

	public long getTime() {
		return time;
	}

	public Object getTrigger() {
		return trigger;
	}

	public String toString() {
		if (fTaglibRecord != null) {
			String string = fTaglibRecord.toString();
			int kind = getKind();
			switch (kind) {
				case ITaglibIndexDelta.ADDED :
					string = " ADDED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibIndexDelta.CHANGED :
					string = " CHANGED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibIndexDelta.REMOVED :
					string = " REMOVED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				default :
					string = " other:" + kind + " (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
			}
			return string;
		}
		else {
			StringBuffer buffer = new StringBuffer();
			int kind = getKind();
			switch (kind) {
				case ITaglibIndexDelta.ADDED :
					buffer.append("TaglibIndexDelta(" + fProject + "):ADDED\n"); //$NON-NLS-1$
					break;
				case ITaglibIndexDelta.CHANGED :
					buffer.append("TaglibIndexDelta(" + fProject + "):CHANGED\n"); //$NON-NLS-1$
					break;
				case ITaglibIndexDelta.REMOVED :
					buffer.append("TaglibIndexDelta(" + fProject + "):REMOVED\n"); //$NON-NLS-1$
					break;
			}
			ITaglibIndexDelta[] children = getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				buffer.append('\t');
				buffer.append(children[i].toString());
				if (i < children.length - 1) {
					buffer.append('\n');
				}
			}
			return buffer.toString();
		}
	}
}