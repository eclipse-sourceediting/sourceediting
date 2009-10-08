/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - fix issue with xslElm being null on local variables.
 *     David Carver (STAR) - add XSL Functions element support and refactored
 *                           StyleSheet Parser
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.util.Debug;
import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.core.model.Function;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Parameter;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.core.model.XSLElement;
import org.eclipse.wst.xsl.core.model.XSLNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A builder that creates and maintains a cache of <code>Stylesheet</code>'s.
 * 
 * @author Doug Satchwell
 */
public class StylesheetBuilder {
	private static StylesheetBuilder instance;
	private final Map<IFile, Stylesheet> builtFiles = new HashMap<IFile, Stylesheet>();

	private StylesheetBuilder() {
	}

	/**
	 * Get the <code>Stylesheet</code> associated with the given file. If either
	 * the <code>Stylesheet</code> has not yet been created or
	 * <code>force</code> is specified then the <code>Stylesheet</code> is
	 * built.
	 * 
	 * @param file
	 *            the XSL file
	 * @param force
	 *            <code>true</code> to force a parse of the file
	 * @return the <code>Stylesheet</code>
	 */
	public Stylesheet getStylesheet(IFile file, boolean force) {
		Stylesheet stylesheet = builtFiles.get(file);
		if (stylesheet == null || force) {
			stylesheet = build(file);
			builtFiles.put(file, stylesheet);
		}
		return stylesheet;
	}

	private Stylesheet build(IFile file) {
		long start = System.currentTimeMillis();
		if (Debug.debugXSLModel) {
			System.out.println("Building " + file + "..."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		Stylesheet stylesheet = null;
		IStructuredModel smodel = null;
		try {
			smodel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (smodel == null) {
				smodel = StructuredModelManager.getModelManager()
						.getModelForRead(file);
				if (Debug.debugXSLModel) {
					long endParse = System.currentTimeMillis();
					System.out.println("PARSE " + file + " in " //$NON-NLS-1$ //$NON-NLS-2$
							+ (endParse - start) + "ms"); //$NON-NLS-1$
				}
			} else if (Debug.debugXSLModel) {
				long endParse = System.currentTimeMillis();
				System.out.println("NO-PARSE " + file + " in " //$NON-NLS-1$ //$NON-NLS-2$
						+ (endParse - start) + "ms"); //$NON-NLS-1$
			}
			// start = System.currentTimeMillis();
			if (smodel != null && smodel instanceof IDOMModel) {
				IDOMModel model = (IDOMModel) smodel;
				stylesheet = parseModel(model, file);
			}
		} catch (IOException e) {
			XSLCorePlugin.log(e);
		} catch (CoreException e) {
			XSLCorePlugin.log(e);
		} finally {
			if (smodel != null)
				smodel.releaseFromRead();
		}
		if (Debug.debugXSLModel) {
			long end = System.currentTimeMillis();
			System.out.println("BUILD " + file + " in " + (end - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return stylesheet;
	}

	private Stylesheet parseModel(IDOMModel model, IFile file) {
		IDOMDocument document = model.getDocument();
		Stylesheet sf = new Stylesheet(file);
		StylesheetParser walker = new StylesheetParser(sf);
		walker.walkDocument(document);
		return sf;
	}

	/**
	 * Get the singleton <code>StylesheetBuilder</code> instance.
	 * 
	 * @return the <code>StylesheetBuilder</code> instance
	 */
	public static synchronized StylesheetBuilder getInstance() {
		if (instance == null) {
			instance = new StylesheetBuilder();
		}
		return instance;
	}
	
	/**
	 * Releases all the cached stylesheets.
	 */
	public void release() {
		if (builtFiles.isEmpty()) {
			return;
		}
		Iterator it = builtFiles.keySet().iterator();
		while (it.hasNext()) {
			IFile key = (IFile) it.next();
			it.remove();
		}
	}
	
	public void release(IFile file) {
		builtFiles.remove(file);
	}
}
