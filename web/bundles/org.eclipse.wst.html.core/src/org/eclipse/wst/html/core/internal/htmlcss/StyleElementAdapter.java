/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * 	    Masaki Saitoh (MSAITOH@jp.ibm.com)
 *		See Bug 153000  Style Adapters should be lazier
 *		https://bugs.eclipse.org/bugs/show_bug.cgi?id=153000
 *
 ********************************************************************************/
package org.eclipse.wst.html.core.internal.htmlcss;



import org.eclipse.wst.css.core.internal.provisional.adapters.IModelProvideAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 */
public class StyleElementAdapter extends AbstractStyleSheetAdapter implements IStructuredDocumentListener {

	private boolean replaceModel = true;
	private boolean ignoreNotification = false;

	/**
	 */
	protected StyleElementAdapter() {
		super();
	}

	/**
	 * Preparation of applying changes from CSS sub-model to HTML model
	 */
	private void changeStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return;
		Element element = getElement();
		if (element == null)
			return;
		ICSSModel model = getExistingModel();
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;

		// get old content length
		Node child = element.getFirstChild();
		if (child == null || child.getNodeType() != Node.TEXT_NODE)
			return;
		IDOMNode content = (IDOMNode) child;
		int oldLength = content.getEndOffset() - content.getStartOffset();

		// get new content length
		int newLength = 0;
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		if (flatNodes != null) {
			int count = flatNodes.getLength();
			if (count > 0) {
				IStructuredDocumentRegion last = flatNodes.item(count - 1);
				if (last != null)
					newLength = last.getEnd();
			}
		}

		int offset = flatNode.getStart();
		int end = flatNode.getEnd();
		int diff = oldLength - newLength;
		int length = end - offset + diff;
		String data = flatNode.getText();

		replaceData(offset, length, data);
	}

	/**
	 * Apply changes from HTML model to CSS sub-model
	 */
	private void contentChanged() {
		Element element = getElement();
		if (element == null)
			return;
		ICSSModel model = getExistingModel();
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;

		String data = null;
		Node child = element.getFirstChild();
		if (child != null && child.getNodeType() == Node.TEXT_NODE && child.getNextSibling() == null) {
			data = child.getNodeValue();
		}
		if (data == null)
			data = "";//$NON-NLS-1$

		// minimize replace range
		int start = 0, end = 0;
		String oldData = structuredDocument.get();
		if (oldData == null)
			oldData = "";//$NON-NLS-1$

		// search differenct character position from first
		for (; start < oldData.length() && start < data.length(); start++)
			if (oldData.charAt(start) != data.charAt(start))
				break;

		if (start == oldData.length() && start == data.length())
			return; // no change
		else if (start == oldData.length()) {
			structuredDocument.replaceText(getRequesterH2C(), start, 0, data.substring(start)); // append text to last
		}
		else if (start == data.length()) {
			structuredDocument.replaceText(getRequesterH2C(), start, oldData.length() - start, ""); // remove text of last //$NON-NLS-1$
		}
		else {
			// search differenct character position from last
			for (; start < oldData.length() - end && start < data.length() - end; end++) {
				if (oldData.charAt(oldData.length() - end - 1) != data.charAt(data.length() - end - 1))
					break;
			}
			structuredDocument.replaceText(getRequesterH2C(), start, oldData.length() - end - start, data.substring(start, data.length() - end));
		}

	}

	/**
	 */
	public ICSSModel getModel() {
		ICSSModel model = getExistingModel();
		if (this.replaceModel) {
			ICSSModel oldModel = model;
			model = createModel(false);

			setModel(model, false); // need to set before contentChanged()
			contentChanged();

			// from super.createModel()
			// get ModelProvideAdapter
			IModelProvideAdapter modelProvideAdapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
			// notify adapter
			if (modelProvideAdapter != null)
				modelProvideAdapter.modelProvided(model);

			// from createModel()
			IStructuredDocument structuredDocument = null;
			if (model != null)
				structuredDocument = model.getStructuredDocument();
			if (structuredDocument == null)
				return null;
			structuredDocument.addDocumentChangedListener(this);

			// from setModel()
			if (oldModel != null)
				oldModel.removeStyleListener(this);
			if (model != null)
				model.addStyleListener(this);

			if (oldModel != null) {
				// get ModelProvideAdapter
				IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
				if (adapter != null) {
					adapter.modelRemoved(oldModel);
				}
			}

			this.replaceModel = false;
		}
		return model;
	}

	/**
	 */
	protected boolean isValidAttribute() {
		Element element = getElement();
		if (element == null) {
			return false;
		}
		String type = element.getAttribute(HTML40Namespace.ATTR_NAME_TYPE);
		if (element.hasAttribute(HTML40Namespace.ATTR_NAME_TYPE) && type.length() > 0 && !type.equalsIgnoreCase("text/css")) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 */
	protected ICSSModel createModel() {
		return createModel(true);
	}

	/**
	 */
	protected ICSSModel createModel(boolean addListener) {
		if (!isValidAttribute()) {
			return null;
		}

		ICSSModel model = super.createModel(addListener);

		if (!addListener)
			return model;

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return null;
		structuredDocument.addDocumentChangedListener(this);

		return model;
	}

	/**
	 */
	//  public ICSSModel getModel() {
	//  	ICSSModel model = getExistingModel();
	//  	if (model == null) {
	//  		model = createModel();
	//  		if (model == null) return null;
	//  		IStructuredDocument structuredDocument = model.getStructuredDocument();
	//  		if (structuredDocument == null) return null;
	//  		structuredDocument.addModelChangedListener(this);
	//  		setModel(model); // need to set before contentChanged()
	//  		contentChanged();
	//  	}
	//  	return model;
	//  }
	/**
	 */
	private Object getRequesterH2C() {
		return (getElement() != null && ((IDOMNode) getElement()).getModel() != null) ? (Object) ((IDOMNode) getElement()).getModel() : this;
	}

	/**
	 */
	private Object getRequesterC2H() {
		return (getModel() != null) ? (Object) getModel() : this;
	}

	/**
	 * Implementing IStructuredDocumentListener's method
	 * Event from CSS Flat Model
	 */
	public void newModel(NewDocumentEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == getRequesterH2C())
			return;
		IStructuredDocument structuredDocument = event.getStructuredDocument();
		if (structuredDocument == null)
			return;
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		if (flatNodes == null)
			return;

		replaceStructuredDocumentRegions(flatNodes, null);
	}

	/**
	 * Implementing IStructuredDocumentListener's method
	 * Event from CSS Flat Model
	 */
	public void noChange(NoChangeEvent structuredDocumentEvent) {
	}

	/**
	 * Implementing IStructuredDocumentListener's method
	 * Event from CSS Flat Model
	 */
	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == getRequesterH2C())
			return;
		IStructuredDocumentRegionList oldStructuredDocumentRegions = event.getOldStructuredDocumentRegions();
		IStructuredDocumentRegionList newStructuredDocumentRegions = event.getNewStructuredDocumentRegions();
		if (oldStructuredDocumentRegions == null && newStructuredDocumentRegions == null)
			return;

		replaceStructuredDocumentRegions(newStructuredDocumentRegions, oldStructuredDocumentRegions);
	}

	/**
	 * Overriding INodeAdapter's method
	 * Event from <STYLE> element
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (this.ignoreNotification)
			return;

		if (eventType == INodeNotifier.ADD || eventType == INodeNotifier.REMOVE || eventType == INodeNotifier.CONTENT_CHANGED) {
			contentChanged();
		}
		else if (eventType == INodeNotifier.CHANGE) {
			Attr attr = (Attr) changedFeature;
			if (attr == null)
				return;
			String name = attr.getName();
			if (name.equalsIgnoreCase("type")) { //$NON-NLS-1$
				this.replaceModel = true;

				Element element = getElement();
				if (element == null) {
					return;
				}
				Document document = element.getOwnerDocument();
				if (document == null) {
					return;
				}
				HTMLDocumentAdapter adapter = (HTMLDocumentAdapter) ((INodeNotifier) document).getAdapterFor(IStyleSheetListAdapter.class);
				if (adapter != null) {
					adapter.childReplaced();
				}
			}
		}
	}

	/**
	 * Implementing IStructuredDocumentListener's method
	 * Event from CSS Flat Model
	 */
	public void regionChanged(RegionChangedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == getRequesterH2C())
			return;
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		if (flatNode == null)
			return;

		changeStructuredDocumentRegion(flatNode);
	}

	/**
	 * Implementing IStructuredDocumentListener's method
	 * Event from CSS Flat Model
	 */
	public void regionsReplaced(RegionsReplacedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == getRequesterH2C())
			return;
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		if (flatNode == null)
			return;

		changeStructuredDocumentRegion(flatNode);
	}

	/**
	 * Apply changes from CSS sub-model to HTML model
	 */
	private void replaceData(int offset, int length, String data) {
		IDOMNode element = (IDOMNode) getElement();
		if (element == null)
			return;
		IDOMModel ownerModel = element.getModel();
		if (ownerModel == null)
			return;
		IStructuredDocument structuredDocument = ownerModel.getStructuredDocument();
		if (structuredDocument == null)
			return;
		IStructuredDocumentRegion flatNode = element.getStartStructuredDocumentRegion();
		if (flatNode == null)
			return;

		int contentOffset = flatNode.getEndOffset();
		if (data == null)
			data = "";//$NON-NLS-1$

		this.ignoreNotification = true;
		structuredDocument.replaceText(getRequesterC2H(), contentOffset + offset, length, data);
		this.ignoreNotification = false;
	}

	/**
	 * Preparation of applying changes from CSS sub-model to HTML model
	 */
	private void replaceStructuredDocumentRegions(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		int offset = 0;
		int length = 0;
		if (oldStructuredDocumentRegions != null) {
			int count = oldStructuredDocumentRegions.getLength();
			if (count > 0) {
				IStructuredDocumentRegion first = oldStructuredDocumentRegions.item(0);
				if (first != null)
					offset = first.getStart();
				IStructuredDocumentRegion last = oldStructuredDocumentRegions.item(count - 1);
				if (last != null)
					length = last.getEnd() - offset;
			}
		}
		String data = null;
		if (newStructuredDocumentRegions != null) {
			int count = newStructuredDocumentRegions.getLength();
			if (count > 0) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < count; i++) {
					IStructuredDocumentRegion flatNode = newStructuredDocumentRegions.item(i);
					if (flatNode == null)
						continue;
					buffer.append(flatNode.getText());
					if (i == 0)
						offset = flatNode.getStart();
				}
				data = buffer.toString();
			}
		}

		replaceData(offset, length, data);
	}

	/**
	 */
	protected void setModel(ICSSModel model) {
		setModel(model, true);
	}

	/**
	 */
	protected void setModel(ICSSModel model, boolean setupListener) {
		ICSSModel oldModel = getExistingModel();
		if (model == oldModel)
			return;
		super.setModel(model);
		if (!setupListener)
			return;
		if (oldModel != null)
			oldModel.removeStyleListener(this);
		if (model != null)
			model.addStyleListener(this);
	}
}