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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.ui.internal.Messages;

/**
 * Manages the override and overwrite indicators for the given Java element and annotation model.
 * 
 * @since 3.0
 */
public class OverrideIndicatorManager
{


	static final String ANNOTATION_TYPE = "org.eclipse.wst.xsl.ui.override"; //$NON-NLS-1$

	private IAnnotationModel fAnnotationModel;
	private Object fAnnotationModelLockObject;
	private Annotation[] fOverrideAnnotations;

	private IFile file;

	/**
	 * Constructor requires the editors annotation model and the file the editor is looking at.
	 * 
	 * @param annotationModel
	 * @param file
	 */
	public OverrideIndicatorManager(IAnnotationModel annotationModel, IFile file)
	{
		Assert.isNotNull(annotationModel);

		this.file = file;
		fAnnotationModel = annotationModel;
		fAnnotationModelLockObject = getLockObject(fAnnotationModel);

		updateAnnotations();
	}

	/**
	 * Returns the lock object for the given annotation model.
	 * 
	 * @param annotationModel
	 *            the annotation model
	 * @return the annotation model's lock object
	 * @since 1.0
	 */
	private Object getLockObject(IAnnotationModel annotationModel)
	{
		if (annotationModel instanceof ISynchronizable)
		{
			Object lock = ((ISynchronizable) annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}

	/**
	 * Updates the override and implements annotations based on the given AST.
	 * 
	 * @param ast
	 *            the compilation unit AST
	 * @param progressMonitor
	 *            the progress monitor
	 * @since 1.0
	 */
	public void updateAnnotations()
	{
		StylesheetModel stylesheetComposed = XSLCore.getInstance().getStylesheet(file);
		final Map<Annotation,Position> annotationMap= new HashMap<Annotation,Position>(50);
		List<Template> nestedTemplates = stylesheetComposed.findAllNestedTemplates();
		for (Template template : stylesheetComposed.getStylesheet().getTemplates())
		{
			// check for overridden stylesheets
			for (Template nestedTemplate : nestedTemplates)
			{
				IFile nestedFile = nestedTemplate.getStylesheet().getFile();
				if (nestedFile != null)
				{
					if(template.matchesByMatchOrName(nestedTemplate))
					{// the template overrides another templates as its name matches, or its match and mode matches
						if (template.getName() != null)
						{
							String text = NLS.bind(Messages.XSLEditorOverrideTemplate , template.getName(), nestedFile.getName());
							annotationMap.put(
								new OverrideIndicator(text, "binding.getKey()"),  //$NON-NLS-1$
								new Position(template.getOffset(), template.getLength())
							);
						}
						else
						{
							String[] overrideParms = { template.getMatch(),template.getMode(), nestedFile.getName() };
							
							String text = NLS.bind(Messages.XSLEditorOverrideTemplateMode, overrideParms);
							annotationMap.put(
								new OverrideIndicator(text, "binding.getKey()"),  //$NON-NLS-1$
								new Position(template.getOffset(), template.getLength())
							);
						}
					}
				}
			}
		}

		synchronized (fAnnotationModelLockObject)
		{
			if (fAnnotationModel instanceof IAnnotationModelExtension)
			{
				((IAnnotationModelExtension) fAnnotationModel).replaceAnnotations(fOverrideAnnotations, annotationMap);
			}
			else
			{
				removeAnnotations();
				Iterator iter = annotationMap.entrySet().iterator();
				while (iter.hasNext())
				{
					Map.Entry mapEntry = (Map.Entry) iter.next();
					fAnnotationModel.addAnnotation((Annotation) mapEntry.getKey(), (Position) mapEntry.getValue());
				}
			}
			fOverrideAnnotations = annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
		}
	}

	/**
	 * Removes all override indicators from this manager's annotation model.
	 */
	void removeAnnotations()
	{
		if (fOverrideAnnotations == null)
			return;

		synchronized (fAnnotationModelLockObject)
		{
			if (fAnnotationModel instanceof IAnnotationModelExtension)
			{
				((IAnnotationModelExtension) fAnnotationModel).replaceAnnotations(fOverrideAnnotations, null);
			}
			else
			{
				for (int i = 0, length = fOverrideAnnotations.length; i < length; i++)
					fAnnotationModel.removeAnnotation(fOverrideAnnotations[i]);
			}
			fOverrideAnnotations = null;
		}
	}
}
