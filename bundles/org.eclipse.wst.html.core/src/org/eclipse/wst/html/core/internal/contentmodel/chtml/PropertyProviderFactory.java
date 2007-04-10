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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;

import java.util.Hashtable;

import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLPropertyDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.AnnotationMap;

/**
 * The factory object of PropertyProvider.
 */
final class PropertyProviderFactory {


	/**
	 * DefaultProvider is intended to be used for unknown properties.
	 * It always returns null value for any properties.
	 */
	class DefaultProvider implements PropertyProvider {
		public DefaultProvider() {
			super();
		}

		public boolean supports(HTMLElementDeclaration edecl) {
			return false;
		}

		public Object get(HTMLElementDeclaration edecl) {
			return null;
		}
	}

	abstract class AbstractElementPropertyProvider implements PropertyProvider {
		protected AbstractElementPropertyProvider() {
			super();
		}

		public boolean supports(HTMLElementDeclaration edecl) {
			return (edecl != null);
		}

		public Object get(HTMLElementDeclaration edecl) {
			if (!(edecl instanceof HTMLPropertyDeclaration))
				return null;
			return getElementProperty((HTMLPropertyDeclaration)edecl);
		}

		abstract protected Object getElementProperty(HTMLPropertyDeclaration decl);
	}

	/*
	 * "tagInfo"
	 * gets documentation for the element
	 */
	class PPTagInfo extends AbstractElementPropertyProvider {
		private final static String htmlAnnotationLoc = "data/htmref.xml"; //$NON-NLS-1$
		protected AnnotationMap fAnnotationMap = null;

		public PPTagInfo() {
			super();
		}

		/**
		 * Gets the annotationMap.
		 * @return Returns a AnnotationMap
		 */
		protected AnnotationMap getAnnotationMap() {
			if (fAnnotationMap == null) {
				fAnnotationMap = new AnnotationMap();
				try {
					fAnnotationMap.load(htmlAnnotationLoc, HTMLCorePlugin.getDefault().getBundle().getSymbolicName());
				}
				catch (Exception e) {
					// no annotation available
				}
			}
			return fAnnotationMap;
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			if (decl instanceof HTMLElementDeclaration) {
				return getAnnotationMap().getProperty(((HTMLElementDeclaration)decl).getElementName(), "tagInfo"); //$NON-NLS-1$
			} else {
				return null;
			}
		}
	}

	/*
	 * "shouldKeepSpace"
	 */
	class PPShouldKeepSpace extends AbstractElementPropertyProvider {
		public PPShouldKeepSpace() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return new Boolean(decl.shouldKeepSpaces());
		}
	}

	/*
	 * "shouldIndentChildSource"
	 */
	class PPShouldIndentChildSource extends AbstractElementPropertyProvider {
		public PPShouldIndentChildSource() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return new Boolean(decl.shouldIndentChildSource());
		}
	}

	/*
	 * "terminators"
	 */
	class PPTerminators extends AbstractElementPropertyProvider {
		public PPTerminators() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			if (decl == null)
				return null;
			if (! (decl instanceof HTMLElemDeclImpl)) return null;
			return ((HTMLElemDeclImpl)decl).getTerminators();
		}
	}

	/*
	 * "prohibitedAncestors"
	 */
	class PPProhibitedAncestors extends AbstractElementPropertyProvider {
		public PPProhibitedAncestors() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return decl.getProhibitedAncestors();
		}
	}

	/*
	 * "isJSP"
	 */
	class PPIsJSP extends AbstractElementPropertyProvider {
		public PPIsJSP() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return new Boolean(decl.isJSP());
		}
	}

	/*
	 * "isXHTML"
	 * HTMLElementDeclaration itself never represent any XHTML element declaration.
	 * So, this property must be always false.
	 */
	class PPIsXHTML extends AbstractElementPropertyProvider {
		public PPIsXHTML() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return new Boolean(false);
		}
	}

	/*
	 * "isSSI"
	 * Each declaration class for SSI elements must provide this property itself,
	 * and then return true.  Other declaration must always return false.
	 */
	class PPIsSSI extends AbstractElementPropertyProvider {
		public PPIsSSI() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return new Boolean(false);
		}
	}

	/*
	 * "lineBreakHint"
	 */
	class PPLineBreakHint extends AbstractElementPropertyProvider {
		public PPLineBreakHint() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			String hint = HTMLCMProperties.Values.BREAK_NONE;
			switch (decl.getLineBreakHint()) {
				case HTMLElementDeclaration.BREAK_AFTER_START :
					hint = HTMLCMProperties.Values.BREAK_AFTER_START;
					break;
				case HTMLElementDeclaration.BREAK_BEFORE_START_AND_AFTER_END :
					hint = HTMLCMProperties.Values.BREAK_BEFORE_START_AND_AFTER_END;
					break;
				case HTMLElementDeclaration.BREAK_NONE :
				// nothing to do
				default :
					break;
			}
			return hint;
		}
	}

	/*
	 * "layoutType"
	 */
	class PPLayoutType extends AbstractElementPropertyProvider {
		public PPLayoutType() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			String type = HTMLCMProperties.Values.LAYOUT_NONE;
			switch (decl.getLayoutType()) {
				case HTMLElementDeclaration.LAYOUT_BLOCK :
					type = HTMLCMProperties.Values.LAYOUT_BLOCK;
					break;
				case HTMLElementDeclaration.LAYOUT_BREAK :
					type = HTMLCMProperties.Values.LAYOUT_BREAK;
					break;
				case HTMLElementDeclaration.LAYOUT_HIDDEN :
					type = HTMLCMProperties.Values.LAYOUT_HIDDEN;
					break;
				case HTMLElementDeclaration.LAYOUT_OBJECT :
					type = HTMLCMProperties.Values.LAYOUT_OBJECT;
					break;
				case HTMLElementDeclaration.LAYOUT_WRAP :
					type = HTMLCMProperties.Values.LAYOUT_WRAP;
					break;
				case HTMLElementDeclaration.LAYOUT_NONE :
				// nothing to do.
				default :
					break;
			}
			return type;
		}
	}

	/*
	 * "omitType"
	 */
	class PPOmitType extends AbstractElementPropertyProvider {
		public PPOmitType() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			String type = HTMLCMProperties.Values.OMIT_NONE;
			switch (decl.getOmitType()) {
				case HTMLElementDeclaration.OMIT_BOTH :
					type = HTMLCMProperties.Values.OMIT_BOTH;
					break;
				case HTMLElementDeclaration.OMIT_END :
					type = HTMLCMProperties.Values.OMIT_END;
					break;
				case HTMLElementDeclaration.OMIT_END_DEFAULT :
					type = HTMLCMProperties.Values.OMIT_END_DEFAULT;
					break;
				case HTMLElementDeclaration.OMIT_END_MUST :
					type = HTMLCMProperties.Values.OMIT_END_MUST;
					break;
				case HTMLElementDeclaration.OMIT_NONE :
				// nothing to do.
				default :
					break;
			}
			return type;
		}
	}

	/*
	 * "inclusion"
	 */
	class PPInclusion extends AbstractElementPropertyProvider {
		public PPInclusion() {
			super();
		}

		protected Object getElementProperty(HTMLPropertyDeclaration decl) {
			return decl.getInclusion();
		}
	}

	public static PropertyProvider getProvider(String propName) {
		PropertyProviderFactory factory = getInstance();
		PropertyProvider pp = (PropertyProvider) factory.registry.get(propName);
		if (pp != null)
			return pp;

		pp = factory.create(propName);
		if (pp == null)
			return factory.defaultProvider;

		factory.registry.put(propName, pp);
		return pp;
	}

	private static PropertyProviderFactory instance = null;

	private synchronized static PropertyProviderFactory getInstance() {
		if (instance != null)
			return instance;
		instance = new PropertyProviderFactory();
		return instance;
	}

	private Hashtable registry = new Hashtable();
	private PropertyProvider defaultProvider = new DefaultProvider();

	private PropertyProviderFactory() {
		super();
	}

	private PropertyProvider create(String propName) {
		PropertyProvider pp = null;
		if (propName.equals(HTMLCMProperties.IS_JSP))
			pp = new PPIsJSP();
		else if (propName.equals(HTMLCMProperties.IS_XHTML))
			pp = new PPIsXHTML();
		else if (propName.equals(HTMLCMProperties.IS_SSI))
			pp = new PPIsSSI();
		else if (propName.equals(HTMLCMProperties.LAYOUT_TYPE))
			pp = new PPLayoutType();
		else if (propName.equals(HTMLCMProperties.LINE_BREAK_HINT))
			pp = new PPLineBreakHint();
		else if (propName.equals(HTMLCMProperties.PROHIBITED_ANCESTORS))
			pp = new PPProhibitedAncestors();
		else if (propName.equals(HTMLCMProperties.SHOULD_KEEP_SPACE))
			pp = new PPShouldKeepSpace();
		else if (propName.equals(HTMLCMProperties.SHOULD_INDENT_CHILD_SOURCE))
			pp = new PPShouldIndentChildSource();
		else if (propName.equals(HTMLCMProperties.TERMINATORS))
			pp = new PPTerminators();
		else if (propName.equals(HTMLCMProperties.TAGINFO))
			pp = new PPTagInfo();
		else if (propName.equals(HTMLCMProperties.OMIT_TYPE))
			pp = new PPOmitType();
		else if (propName.equals(HTMLCMProperties.INCLUSION))
			pp = new PPInclusion();

		return pp;
	}
}
