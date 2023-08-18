/*******************************************************************************
 * Copyright (c) 2008, 2023 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.correction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.wst.sse.ui.internal.Logger;

public class QuickFixRegistry {
	private static QuickFixRegistry instance;

	public synchronized static QuickFixRegistry getInstance() {
		if (instance == null) {
			instance = new QuickFixRegistry();
			new QuickFixRegistryReader().addHelp(instance);
		}
		return instance;
	}

	/**
	 * Table of queries for marker resolutions
	 */
	private Map<AnnotationQuery, Map<AnnotationQueryResult, Collection<IConfigurationElement>>> resolutionQueries = new HashMap<>();

	/**
	 * Resolution class attribute name in configuration element
	 */
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	/**
	 * Adds a resolution query to the registry.
	 * 
	 * @param query
	 *            a marker query
	 * @param result
	 *            a result for the given query
	 * @param element
	 *            the configuration element defining the result
	 */
	void addResolutionQuery(AnnotationQuery query, AnnotationQueryResult result, IConfigurationElement element) {
		addQuery(resolutionQueries, query, result, element);
	}

	/**
	 * Adds a query to the given table.
	 * 
	 * @param table
	 *            the table to which the query is added
	 * @param query
	 *            a marker query
	 * @param result
	 *            a result for the given query
	 * @param element
	 *            the configuration element defining the result
	 */
	private void addQuery(Map<AnnotationQuery, Map<AnnotationQueryResult, Collection<IConfigurationElement>>> table, AnnotationQuery query, AnnotationQueryResult result, IConfigurationElement element) {

		// See if the query is already in the table
		Map<AnnotationQueryResult, Collection<IConfigurationElement>> results = table.get(query);
		if (results == null) {
			// Create a new results table
			results = new HashMap<>();

			// Add the query to the table
			table.put(query, results);
		}

		if (results.containsKey(result)) {
			Collection<IConfigurationElement> currentElements = results.get(result);
			currentElements.add(element);
		}
		else {
			Collection<IConfigurationElement> configElements = new HashSet<>();
			configElements.add(element);

			// Add the new result
			results.put(result, configElements);
		}
	}

	public IQuickAssistProcessor[] getQuickFixProcessors(Annotation anno) {
		// Collect all matches
		List<IQuickAssistProcessor> processors = new ArrayList<>();
		for (Iterator<AnnotationQuery> iter = resolutionQueries.keySet().iterator(); iter.hasNext();) {
			AnnotationQuery query = iter.next();
			AnnotationQueryResult result = null;
			try {
				/* AnnotationQuery objects are contributed by extension point */
				result = query.performQuery(anno);
			}
			catch (Exception e) {
				Logger.logException(e);
			}
			if (result != null) {
				// See if a matching result is registered
				Map<AnnotationQueryResult, Collection<IConfigurationElement>> resultsTable = resolutionQueries.get(query);

				if (resultsTable.containsKey(result)) {

					Iterator<IConfigurationElement> elements = resultsTable.get(result).iterator();
					while (elements.hasNext()) {
						IConfigurationElement element = elements.next();

						IQuickAssistProcessor processor = null;
						try {
							processor = (IQuickAssistProcessor) element.createExecutableExtension(ATT_CLASS);
						}
						catch (CoreException e) {
						}
						if (processor != null) {
							processors.add(processor);
						}

					}
				}
			}
		}
		return processors.toArray(new IQuickAssistProcessor[processors.size()]);
	}
}
