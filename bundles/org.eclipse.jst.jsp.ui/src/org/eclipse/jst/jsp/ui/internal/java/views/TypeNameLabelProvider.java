/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.views;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class TypeNameLabelProvider extends LabelProvider {

	private static final Point BIG_SIZE = new Point(22, 16);

	public String getText(Object element) {
		String text = null;
		if (!(element instanceof TypeNameMatch)) {
			text = super.getText(element);
		}
		else {
			text = ((TypeNameMatch) element).getFullyQualifiedName();
		}
		return text;
	}

	public Image getImage(Object element) {
		Image image = null;
		if (!(element instanceof TypeNameMatch)) {
			image = super.getImage(element);
		}
		else {
			image = ImageDescriptorRegistry.getImage(getImageDescriptor((TypeNameMatch) element));
		}
		return image;
	}

	private ImageDescriptor getImageDescriptor(TypeNameMatch match) {
		// TODO use isInner
		// final boolean isInner = match.getTypeContainerName().indexOf('.') != -1;
		final int modifiers = match.getModifiers();

		ImageDescriptor desc = getTypeImageDescriptor(modifiers);
		int adornmentFlags= 0;
		if (Flags.isFinal(modifiers)) {
			adornmentFlags |= JavaElementImageDescriptor.FINAL;
		}
		if (Flags.isAbstract(modifiers) && !Flags.isInterface(modifiers)) {
			adornmentFlags |= JavaElementImageDescriptor.ABSTRACT;
		}
		if (Flags.isStatic(modifiers)) {
			adornmentFlags |= JavaElementImageDescriptor.STATIC;
		}
		if (Flags.isDeprecated(modifiers)) {
			adornmentFlags |= JavaElementImageDescriptor.DEPRECATED;
		}

		return new JavaElementImageDescriptor(desc, adornmentFlags, BIG_SIZE);
	}

	private ImageDescriptor getTypeImageDescriptor(int modifiers) {
		String key = ISharedImages.IMG_OBJS_CLASS;
		if (Flags.isEnum(modifiers)) {
			key = ISharedImages.IMG_OBJS_ENUM;
		}
		else if (Flags.isAnnotation(modifiers)) {
			key = ISharedImages.IMG_OBJS_ANNOTATION;
		}
		else if (Flags.isInterface(modifiers)) {
			key = ISharedImages.IMG_OBJS_INTERFACE;
		}
		return JavaUI.getSharedImages().getImageDescriptor(key);
	}
}
