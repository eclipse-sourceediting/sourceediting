package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.jsdt.core.IClassFile;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IParent;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JavaOutlinePage.ElementChangedListener;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JavaOutlinePage.NoClassElement;

public class JSContentProvider ITreeContentProvider {
	

		private Object[] NO_CLASS= new Object[] {new NoClassElement()};
		private ElementChangedListener fListener;

		protected boolean matches(IJavaElement element) {
			if (element.getElementType() == IJavaElement.METHOD) {
				String name= element.getElementName();
				return (name != null && name.indexOf('<') >= 0);
			}
			return false;
		}

		protected IJavaElement[] filter(IJavaElement[] children) {
			boolean initializers= false;
			for (int i= 0; i < children.length; i++) {
				if (matches(children[i])) {
					initializers= true;
					break;
				}
			}

			if (!initializers)
				return children;

			Vector v= new Vector();
			for (int i= 0; i < children.length; i++) {
				if (matches(children[i]))
					continue;
				v.addElement(children[i]);
			}

			IJavaElement[] result= new IJavaElement[v.size()];
			v.copyInto(result);
			return result;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c= (IParent) parent;
				try {
					return filter(c.getChildren());
				} catch (JavaModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (JavaPlugin.isDebug() || !x.isDoesNotExist())
						JavaPlugin.log(x);
				}
			}
			return NO_CHILDREN;
		}

		public Object[] getElements(Object parent) {
			if (fTopLevelTypeOnly) {
				if (parent instanceof ICompilationUnit) {
					try {
						IType type= getMainType((ICompilationUnit) parent);
						return type != null ? type.getChildren() : NO_CLASS;
					} catch (JavaModelException e) {
						JavaPlugin.log(e);
					}
				} else if (parent instanceof IClassFile) {
					try {
						IType type= getMainType((IClassFile) parent);
						return type != null ? type.getChildren() : NO_CLASS;
					} catch (JavaModelException e) {
						JavaPlugin.log(e);
					}
				}
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof IJavaElement) {
				IJavaElement e= (IJavaElement) child;
				return e.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c= (IParent) parent;
				try {
					IJavaElement[] children= filter(c.getChildren());
					return (children != null && children.length > 0);
				} catch (JavaModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (JavaPlugin.isDebug() || !x.isDoesNotExist())
						JavaPlugin.log(x);
				}
			}
			return false;
		}

		public boolean isDeleted(Object o) {
			return false;
		}

		public void dispose() {
			if (fListener != null) {
				JavaCore.removeElementChangedListener(fListener);
				fListener= null;
			}
		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			boolean isCU= (newInput instanceof ICompilationUnit);

			if (isCU && fListener == null) {
				fListener= new ElementChangedListener();
				JavaCore.addElementChangedListener(fListener);
			} else if (!isCU && fListener != null) {
				JavaCore.removeElementChangedListener(fListener);
				fListener= null;
			}
		}
	

}
