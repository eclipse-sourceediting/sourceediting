/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.htmlcss;



import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 */
public class StyleAttrAdapter extends AbstractCSSModelAdapter implements IStructuredDocumentListener, IStyleDeclarationAdapter {

	private boolean ignoreNotification = false;
	private final static String STYLE = "style";//$NON-NLS-1$

	/**
	 */
	StyleAttrAdapter() {
		super();
	}

	/**
	 */
	public ICSSModel getModel() {
		ICSSModel model = getExistingModel();
		if (model == null && isModelNecessary()) {
			model = createModel();
			if (model == null)
				return null;
			
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			if (structuredDocument == null)
				return null;

			RegionParser parser = structuredDocument.getParser();
			if (parser instanceof CSSSourceParser) {
				((CSSSourceParser)parser).setParserMode(CSSSourceParser.MODE_DECLARATION);
			} else {
				return null;
			}
			
			structuredDocument.addDocumentChangedListener(this);

			setModel(model); // need to set before valueChanged()
			valueChanged();
		}
		if (model != null && !isModelNecessary()) {
			model = null;
			valueChanged();
		}
		return model;
	}

	/**
	 */
	public CSSStyleDeclaration getStyle() {
		ICSSModel model = getModel();
		if (model == null)
			return null;
		return (CSSStyleDeclaration) model.getDocument();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return (type == IStyleDeclarationAdapter.class);
	}

	/**
	 */
	public void newModel(NewDocumentEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == this)
			return;

		setValue();
	}

	/**
	 */
	public void noChange(NoChangeEvent structuredDocumentEvent) {
	}

	/**
	 */
	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == this)
			return;

		setValue();
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (this.ignoreNotification)
			return;

		if (eventType != INodeNotifier.CHANGE)
			return;
		Attr attr = (Attr) changedFeature;
		if (attr == null)
			return;
		String name = attr.getName();
		if (name.equalsIgnoreCase(STYLE)) {
			valueChanged();
		}
	}

	/**
	 */
	public void regionChanged(RegionChangedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == this)
			return;

		setValue();
	}

	/**
	 */
	public void regionsReplaced(RegionsReplacedEvent event) {
		if (event == null)
			return;
		if (event.getOriginalRequester() == this)
			return;

		setValue();
	}

	/**
	 */
	private void setValue() {
		Element element = getElement();
		if (element == null)
			return;
		ICSSModel model = getExistingModel();
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;

		String value = null;
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		if (flatNodes != null) {
			int count = flatNodes.getLength();
			if (count > 0) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < count; i++) {
					IStructuredDocumentRegion flatNode = flatNodes.item(i);
					if (flatNode == null)
						continue;
					buffer.append(flatNode.getText());
				}
				value = buffer.toString();
			}
		}

		this.ignoreNotification = true;
		if (value == null || value.length() == 0) {
			element.removeAttribute(STYLE);
		}
		else {
			Attr attr = element.getAttributeNode(STYLE);
			if (attr != null) {
				((IDOMNode) attr).setValueSource(value);
			}
			else {
				Document document = element.getOwnerDocument();
				attr = document.createAttribute(STYLE);
				((IDOMNode) attr).setValueSource(value);
				element.setAttributeNode(attr);
			}
		}
		this.ignoreNotification = false;

		notifyStyleChanged(element);
	}

	/**
	 */
	private void valueChanged() {
		Element element = getElement();
		if (element == null)
			return;
		if (!isModelNecessary()) { // removed
			setModel(null);

			notifyStyleChanged(element);
			return;
		}

		ICSSModel model = getExistingModel();
		if (model == null)
			return; // defer
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return; // error

		String value = null;
		Attr attr = element.getAttributeNode(org.eclipse.wst.html.core.internal.provisional.HTML40Namespace.ATTR_NAME_STYLE);
		if (attr != null)
			value = ((IDOMNode) attr).getValueSource();
		structuredDocument.setText(this, value);

		notifyStyleChanged(element);
	}

	/**
	 * @return boolean
	 */
	private boolean isModelNecessary() {
		return getElement() != null && getElement().getAttributeNode(org.eclipse.wst.html.core.internal.provisional.HTML40Namespace.ATTR_NAME_STYLE) != null;
	}
}
