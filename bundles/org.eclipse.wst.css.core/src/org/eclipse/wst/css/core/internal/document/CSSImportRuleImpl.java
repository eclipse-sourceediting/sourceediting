/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.adapters.IModelProvideAdapter;
import org.eclipse.wst.css.core.document.ICSSImportRule;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.util.CSSLinkConverter;
import org.eclipse.wst.css.core.util.CSSUtil;
import org.eclipse.wst.css.core.util.URLModelProviderCSS;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
class CSSImportRuleImpl extends CSSRuleImpl implements ICSSImportRule {

	private boolean fDirty = true;
	private ICSSStyleSheet fStyleSheet;

	/**
	 * 
	 */
	CSSImportRuleImpl() {
		super();
	}

	/**
	 * @param that
	 *            com.ibm.sed.css.treemodel.CSSImportRuleImpl
	 */
	CSSImportRuleImpl(CSSImportRuleImpl that) {
		super(that);
	}

	/**
	 * @return com.ibm.sed.css.interfaces.CSSNode
	 * @param deep
	 *            boolean
	 */
	public ICSSNode cloneNode(boolean deep) {
		CSSImportRuleImpl cloned = new CSSImportRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * 
	 */
	void closeStyleSheet() {
		if (fStyleSheet != null) {
			ICSSStyleSheet sheet = fStyleSheet;
			fStyleSheet = null; // to prevent loop, we must reset fStyleSheet
								// before all closing action

			sheet.getModel().removeStyleListener(getOwnerDocument().getModel());
			// get ModelProvideAdapter
			IModelProvideAdapter adapter = (IModelProvideAdapter) getAdapterFor(IModelProvideAdapter.class);
			// if (getOwnerDocument().getModel().getStyleSheetType() ==
			// ICSSModel.EMBEDDED) { // case STYLE tag
			// adapter = (ModelProvideAdapter)
			// ((INodeNotifier)getOwnerDocument().getModel().getOwnerDOMNode()).getAdapterFor(ModelProvideAdapter.class);
			// }
			if (adapter != null)
				adapter.modelRemoved(sheet.getModel());

			sheet.getModel().releaseFromRead();
		}
	}

	/**
	 * The location of the style sheet to be imported. The attribute will not
	 * contain the <code>"url(...)"</code> specifier around the URI.
	 */
	public String getHref() {
		return CSSUtil.extractUriContents(getAttribute(HREF));
	}

	/**
	 * A list of media types for which this style sheet may be used.
	 */
	public org.w3c.dom.stylesheets.MediaList getMedia() {
		ICSSNode media = getFirstChild();
		if (media != null) {
			if (media instanceof MediaList) {
				return (MediaList) media;
			}
		}

		// THIS CASE IS ERROR CASE
		return null;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return IMPORTRULE_NODE;
	}

	/**
	 * The style sheet referred to by this rule, if it has been loaded. The
	 * value of this attribute is <code>null</code> if the style sheet has
	 * not yet been loaded or if it will not be loaded (e.g. if the style
	 * sheet is for a media type not supported by the user agent).
	 */
	public CSSStyleSheet getStyleSheet() {

		if (fDirty) {
			// NOTE: try/catch block is a TEMP fix to avoid compile errors:
			try {
				closeStyleSheet();

				if (getHref() == null || getHref().length() <= 0) {
					fDirty = false;
					return null;
				}

				IStructuredModel baseModel = getOwnerDocument().getModel();
				if (getOwnerDocument().getModel().getStyleSheetType() == ICSSModel.EMBEDDED) { // case
																								// STYLE
																								// tag
					baseModel = ((DOMNode) getOwnerDocument().getModel().getOwnerDOMNode()).getModel();
				}
				// get ModelProvideAdapter
				IModelProvideAdapter adapter = (IModelProvideAdapter) getAdapterFor(IModelProvideAdapter.class);
				// if (getOwnerDocument().getModel().getStyleSheetType() ==
				// ICSSModel.EMBEDDED) { // case STYLE tag
				// adapter = (ModelProvideAdapter)
				// ((INodeNotifier)getOwnerDocument().getModel().getOwnerDOMNode()).getAdapterFor(ModelProvideAdapter.class);
				// }

				// load model from IModelManager
				URLModelProviderCSS provider = new URLModelProviderCSS();
				IStructuredModel newModel = provider.getModelForRead(baseModel, getHref());
				fDirty = false;

				if (newModel == null)
					return null;
				if (!(newModel instanceof ICSSModel)) {
					newModel.releaseFromRead();
					return null;
				}

				// notify adapter
				if (adapter != null)
					adapter.modelProvided(newModel);

				fStyleSheet = (ICSSStyleSheet) ((ICSSModel) newModel).getDocument();
				if (fStyleSheet != null)
					fStyleSheet.getModel().addStyleListener(getOwnerDocument().getModel());
			}
			catch (java.io.IOException e) {
				Logger.logException(e);
			}
		}
		return fStyleSheet;
	}

	/**
	 * The type of the rule, as defined above. The expectation is that
	 * binding-specific casting methods can be used to cast down from an
	 * instance of the <code>CSSRule</code> interface to the specific
	 * derived interface implied by the <code>type</code>.
	 */
	public short getType() {
		return IMPORT_RULE;
	}

	/**
	 * 
	 */
	public void refreshStyleSheet() {
		if (!fDirty) {
			closeStyleSheet();
			fDirty = true;
			notify(INodeNotifier.CHANGE, getAttributeNode(HREF), null, null, getStartOffset());
		}
	}

	/**
	 */
	void releaseRule() {
		if (fStyleSheet != null) {
			ICSSStyleSheet sheet = fStyleSheet;
			fStyleSheet = null; // to prevent loop, we must reset fStyleSheet
								// before all closing action

			sheet.getModel().removeStyleListener(getOwnerDocument().getModel());
			// get ModelProvideAdapter
			IModelProvideAdapter adapter = (IModelProvideAdapter) getAdapterFor(IModelProvideAdapter.class);
			// if (getOwnerDocument().getModel().getStyleSheetType() ==
			// ICSSModel.EMBEDDED) { // case STYLE tag
			// adapter = (ModelProvideAdapter)
			// ((INodeNotifier)getOwnerDocument().getModel().getOwnerDOMNode()).getAdapterFor(ModelProvideAdapter.class);
			// }
			if (adapter != null)
				adapter.modelReleased(sheet.getModel());

			sheet.getModel().releaseFromRead();
		}
	}

	/**
	 * @param href
	 *            java.lang.String
	 */
	public void setHref(String href) throws DOMException {
		fDirty = true;
		setAttribute(HREF, CSSLinkConverter.addFunc(href));
	}
}