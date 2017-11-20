/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * Image provider for {@link org.eclipse.wst.xsl.ui.internal.editor.jdt.internal.ui.javaeditor.OverrideIndicatorManager.OverrideIndicator} annotations.
 * 
 * @since 3.0
 */
public class OverrideIndicatorImageProvider implements IAnnotationImageProvider
{
	private static final String OVERRIDE_IMG_DESC_ID = "OverrideIndicatorImageProvider.OVERRIDE_IMG_DESC_ID"; //$NON-NLS-1$

	/**
	 * This does not support managing its own image.
	 * 
	 * @param annotation
	 * @return null
	 */
	public Image getManagedImage(Annotation annotation)
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation)
	{
		if (!isImageProviderFor(annotation))
			return null;
		return OVERRIDE_IMG_DESC_ID;
	}

	/**
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId)
	{
		if (OVERRIDE_IMG_DESC_ID.equals(imageDescritporId))
			return AbstractUIPlugin.imageDescriptorFromPlugin(XSLUIPlugin.PLUGIN_ID, "icons/full/over_co.gif"); //$NON-NLS-1$
		return null;
	}

	private boolean isImageProviderFor(Annotation annotation)
	{
		return annotation != null && OverrideIndicatorManager.ANNOTATION_TYPE.equals(annotation.getType());
	}
}
