/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.htmlcss;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.css.core.internal.provisional.adapters.IModelProvideAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 */
public class LinkElementAdapter extends AbstractStyleSheetAdapter {

	private final static String CSS_ID = ContentTypeIdForCSS.ContentTypeID_CSS;
	private boolean replaceModel = true;
	// this variable to hold the class is just a VAJava trick.
	// it improves performance in VAJava by minimizing class loading.
	private final Class ModelProvideAdapterClass = IModelProvideAdapter.class;

	/**
	 */
	protected LinkElementAdapter() {
		super();
	}

	/**
	 */
	private void attrReplaced() {
		this.replaceModel = true;

		Element element = getElement();
		if (element == null)
			return; // error
		Document document = element.getOwnerDocument();
		if (document == null)
			return; // error
		INodeNotifier notifier = (INodeNotifier) document;
		HTMLDocumentAdapter adapter = (HTMLDocumentAdapter) notifier.getAdapterFor(IStyleSheetListAdapter.class);
		if (adapter == null)
			return;
		adapter.childReplaced();
	}

	protected ICSSModel createModel() {
		// create phantom(broken link) external CSS model
		if (getElement() == null)
			return null;
		IStructuredModel baseModel = ((IDOMNode) getElement()).getModel();
		ICSSModel newModel = (ICSSModel) baseModel.getModelManager().createUnManagedStructuredModelFor(CSS_ID);

		// calculate base location and set
		// get resolver in Model
		URIResolver resolver = baseModel.getResolver();

		// resolve to absolute url : this need not exact location of css file. It is important that absurl is not null. 
		String ref = getElement().getAttribute(HTML40Namespace.ATTR_NAME_HREF);
		String absurl = (resolver != null && ref != null && ref.length() > 0) ? resolver.getLocationByURI(ref, true) : null;
		if ((absurl == null) || (absurl.length() == 0)) {
			IPath basePath = new Path(baseModel.getBaseLocation());
			URLHelper helper = new URLHelper(basePath.removeLastSegments(1).toString());
			absurl = helper.toAbsolute(ref == null ? "" : ref);//$NON-NLS-1$
		}
		if ((absurl == null) || (absurl.length() == 0)) {
			absurl = ref;
		}
		if (absurl == null) {
			absurl = "";//$NON-NLS-1$
		}
		newModel.setBaseLocation(absurl);

		// set style listener
		newModel.addStyleListener(this);

		return newModel;
	}

	/**
	 */
	public ICSSModel getModel() {
		ICSSModel model = getExistingModel();
		if (this.replaceModel) {
			ICSSModel oldModel = model;
			try {
				model = retrieveModel();
				setModel(model);

				// release old model
				if (oldModel != null) {
					// get ModelProvideAdapter
					IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
					if (adapter != null)
						adapter.modelRemoved(oldModel);
				}
			}
			finally {
				if (oldModel != null)
					oldModel.releaseFromRead();
			}
			this.replaceModel = false;
		}
		return model;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (eventType != INodeNotifier.CHANGE)
			return;
		Attr attr = (Attr) changedFeature;
		if (attr == null)
			return;
		String name = attr.getName();
		if (name.equalsIgnoreCase("rel") || //$NON-NLS-1$
					name.equalsIgnoreCase("type") || //$NON-NLS-1$	
					name.equalsIgnoreCase("href")) {//$NON-NLS-1$
			attrReplaced();
		}
	}

	/**
	 */
	public void refreshSheet() {
		if (!replaceModel) {
			removed();
			replaceModel = true;

			IDOMNode node = (IDOMNode) getElement();
			if (node != null) {
				node.notify(INodeNotifier.CHANGE, getElement().getAttributeNode(org.eclipse.wst.html.core.internal.provisional.HTML40Namespace.ATTR_NAME_HREF), null, null, node.getStartOffset());
			}
		}
	}

	/**
	 */
	public void released() {
		ICSSModel model = getExistingModel();
		if (model != null) {
			try {
				// get ModelProvideAdapter
				IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);

				// set element to null first, so that no document wide updates
				setElement(null);
				setModel(null);

				if (adapter != null)
					adapter.modelReleased(model);
			}
			finally {
				model.releaseFromRead();
			}
		}
		this.replaceModel = false;
	}

	/**
	 */
	public void removed() {
		ICSSModel model = getExistingModel();
		if (model != null) {
			try {
				setModel(null);

				// get ModelProvideAdapter
				IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
				if (adapter != null)
					adapter.modelRemoved(model);
			}
			finally {
				model.releaseFromRead();
			}
		}
		this.replaceModel = false;
	}

	/**
	 */
	protected boolean isValidAttribute() {
		Element element = getElement();
		if (element == null)
			return false;
		if (!element.hasAttribute("rel") || !"stylesheet".equalsIgnoreCase(element.getAttribute("rel")))//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return false;
		if (element.hasAttribute("type") && !"text/css".equalsIgnoreCase(element.getAttribute("type")))//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return false;
		if (!element.hasAttribute("href") || element.getAttribute("href").length() == 0) //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		return true;
	}

	/**
	 */
	private ICSSModel retrieveModel() {
		if (!isValidAttribute()) {
			return null;
		}

		// null,attr check is done in isValidAttribute()
		Element element = getElement();
		String href = element.getAttribute("href");//$NON-NLS-1$

		IDOMModel baseModel = ((IDOMNode) element).getModel();
		if (baseModel == null)
			return null;
		Object id = baseModel.getId();
		if (!(id instanceof String))
			return null;
		//String base = (String)id;

		// get ModelProvideAdapter
		IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(ModelProvideAdapterClass);

		URLModelProvider provider = new URLModelProvider();
		try {
			IStructuredModel newModel = provider.getModelForRead(baseModel, href);
			if (newModel == null)
				return null;
			if (!(newModel instanceof ICSSModel)) {
				newModel.releaseFromRead();
				return null;
			}

			// notify adapter
			if (adapter != null)
				adapter.modelProvided(newModel);

			return (ICSSModel) newModel;
		}
		catch (UnsupportedEncodingException e) {
		}
		catch (IOException e) {
		}

		return null;
	}

	/**
	 */
	protected void setModel(ICSSModel model) {
		ICSSModel oldModel = getExistingModel();
		if (model == oldModel)
			return;
		super.setModel(model);
		if (this.replaceModel)
			this.replaceModel = false;
		if (oldModel != null)
			oldModel.removeStyleListener(this);
		if (model != null)
			model.addStyleListener(this);
	}
}
