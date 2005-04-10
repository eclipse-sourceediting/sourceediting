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
package org.eclipse.wst.css.ui.views.contentoutline;



import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.ui.image.CSSImageHelper;
import org.eclipse.wst.css.ui.image.CSSImageType;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.MediaList;


/**
 * A class that uses a JFaceNodeAdapterFactory to provide adapters to provide
 * the labels and images for DOM nodes.
 */
class JFaceNodeLabelProviderCSS implements ILabelProvider {
	protected IAdapterFactory fAdapterFactory;

	/**
	 * JFaceNodeLabelProvider constructor comment.
	 */
	public JFaceNodeLabelProviderCSS(IAdapterFactory adapterFactory) {
		super();
		this.fAdapterFactory = adapterFactory;
	}

	/**
	 * Adds a listener to the label provider. A label provider should inform
	 * its listener about state changes that enforces rendering of the visual
	 * part that uses this label provider.
	 */
	public void addListener(ILabelProviderListener listener) {
		// The label provider state never changes so we do not have
		// to implement this method.
	}

	/**
	 * The visual part that is using this label provider is about to be
	 * disposed. Deallocate all allocated SWT resources.
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Returns the JFace adapter for the specified object.
	 * 
	 * @return com.ibm.sed.view.tree.DOMJFaceAdapter The JFace adapter
	 * @param adaptable
	 *            java.lang.Object The object to get the adapter for
	 */
	//protected IJFaceNodeAdapter getAdapter(Object adaptable) {
	//  return (IJFaceNodeAdapter) adapterFactory.adapt((INodeNotifier)
	// adaptable);
	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		return (IJFaceNodeAdapter) fAdapterFactory.adapt((INodeNotifier) adaptable);
	}

	/**
	 * Returns the image for the label of the given element, for use in the
	 * given viewer.
	 * 
	 * @param viewer
	 *            The viewer that displays the element.
	 * @param element
	 *            The element for which to provide the label image. Element
	 *            can be <code>null</code> indicating no input object is set
	 *            to the viewer.
	 */
	public Image getImage(Object element) {
		//  return getAdapter(element).getLabelImage((Node) element);


		if (element instanceof ICSSNode) {
			CSSImageHelper helper = CSSImageHelper.getInstance();
			return helper.getImage(CSSImageType.getImageType((ICSSNode) element));
			//		Image image = getCSSNodeImage(element);
			//		return image;
			//      return getAdapter(element).getLabelImage((ICSSNode) element);
		}
		return null;
	}

	/**
	 * Insert the method's description here.
	 */
	public String getLabelText(Viewer viewer, Object element) {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns the text for the label of the given element, for use in the
	 * given viewer.
	 * 
	 * @param viewer
	 *            The viewer that displays the element.
	 * @param element
	 *            The element for which to provide the label text. Element can
	 *            be <code>null</code> indicating no input object is set to
	 *            the viewer.
	 */
	public String getText(Object element) {
		// This was returning null, on occasion ... probably should not be,
		// but
		// took the quick and easy way out for now. (dmw 3/8/01)

		String result = "";//$NON-NLS-1$
		String mediaText;
		if (element instanceof ICSSNode) {
			switch (((ICSSNode) element).getNodeType()) {
				case ICSSNode.STYLERULE_NODE :
					result = ((ICSSStyleRule) element).getSelectors().getString();
					break;
				case ICSSNode.FONTFACERULE_NODE :
					result = "@font-face";//$NON-NLS-1$
					break;
				case ICSSNode.IMPORTRULE_NODE :
					result = ((CSSImportRule) element).getHref();
					mediaText = getMediaText((CSSImportRule) element);
					if (mediaText != null && 0 < mediaText.length()) {
						result += " (" + mediaText + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					break;
				case ICSSNode.PAGERULE_NODE :
					result = ((ICSSPageRule) element).getSelectors().getString();
					break;
				case ICSSNode.STYLEDECLARATION_NODE :
					result = "properties";//$NON-NLS-1$
					break;
				case ICSSNode.STYLEDECLITEM_NODE :
					result = ((ICSSStyleDeclItem) element).getPropertyName();
					break;
				case ICSSNode.PRIMITIVEVALUE_NODE :
					result = ((ICSSPrimitiveValue) element).getStringValue();
					break;
				case ICSSNode.MEDIARULE_NODE :
					result = "@media";//$NON-NLS-1$
					mediaText = getMediaText((ICSSMediaRule) element);
					if (mediaText != null && 0 < mediaText.length()) {
						result += " (" + mediaText + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					break;
				case ICSSNode.CHARSETRULE_NODE :
					result = "@charset";//$NON-NLS-1$
					break;
				case ICSSNode.MEDIALIST_NODE :
					result = ((MediaList) element).getMediaText();
					break;
				default :
					break;
			}
		}

		//  if (element instanceof ICSSNode) {
		//      ICSSNode node = ((ICSSNode)element);
		//      result = getAdapter(element).getLabelText((ICSSNode) element);
		//  }
		return result;
	}

	private String getMediaText(CSSRule rule) {
		String result = ""; //$NON-NLS-1$
		ICSSNode child = (rule != null) ? ((ICSSNode) rule).getFirstChild() : null;
		while (child != null) {
			if (child.getNodeType() == ICSSNode.MEDIALIST_NODE) {
				result = ((MediaList) child).getMediaText();
				break;
			}
			child = child.getNextSibling();
		}
		return result;
	}

	/**
	 * Checks whether this label provider is affected by the given domain
	 * event.
	 */
	public boolean isAffected(Object dummy) {//DomainEvent event) {
		//return event.isModifier(DomainEvent.NON_STRUCTURE_CHANGE);
		return true;

	}

	/**
	 * Returns whether the label would be affected by a change to the given
	 * property of the given element. This can be used to optimize a
	 * non-structural viewer update. If the property mentioned in the update
	 * does not affect the label, then the viewer need not update the label.
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return <code>true</code> if the label would be affected, and
	 *         <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes a listener from the label provider.
	 */
	public void removeListener(ILabelProviderListener listener) {
		// The label provider state never changes so we do not have
		// to implement this method.
	}
}