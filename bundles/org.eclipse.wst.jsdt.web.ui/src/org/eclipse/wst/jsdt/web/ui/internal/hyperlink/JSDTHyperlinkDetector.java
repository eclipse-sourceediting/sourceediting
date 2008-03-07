package org.eclipse.wst.jsdt.web.ui.internal.hyperlink;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.jsdt.core.IClassFile;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IField;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.ILocalVariable;
import org.eclipse.wst.jsdt.core.IMethod;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.ISourceReference;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
//import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Detects hyperlinks in JSP Java content
 */
public class JSDTHyperlinkDetector extends AbstractHyperlinkDetector {
	private IHyperlink createHyperlink(IJavaElement element, IRegion region, IDocument document) {
		IHyperlink link = null;
		if (region != null) {
			// open local variable in the JSP file...
			if (element instanceof ISourceReference) {
				IFile file = null;
				IPath outsidePath = null;
				int jspOffset = 0;
				IStructuredModel sModel = null;
				// try to locate the file in the workspace
				try {
					sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
					if (sModel != null) {
						//URIResolver resolver = sModel.getResolver();
						//if (resolver != null) {
						//	String uriString = resolver.getFileBaseLocation();
						String uriString = sModel.getBaseLocation();
							file = getFile(uriString);
					//	}
					}
				} finally {
					if (sModel != null) {
						sModel.releaseFromRead();
					}
				}
				// get Java range, translate coordinate to JSP
				try {
					ISourceRange range = null;
					IJsTranslation jspTranslation = getJSPTranslation(document);
					if (jspTranslation != null) {
						// link to local variable definitions
						if (element instanceof ILocalVariable) {
							range = ((ILocalVariable) element).getNameRange();
							IJavaElement unit=((ILocalVariable) element).getParent();
							ICompilationUnit myUnit = jspTranslation.getCompilationUnit();
							
							while(!(unit instanceof ICompilationUnit || unit instanceof IClassFile || unit==null)) {
								unit = ((JavaElement) unit).getParent();
							}
							if(unit instanceof ICompilationUnit) {
								ICompilationUnit cu = (ICompilationUnit)unit;
								if(cu!=myUnit) {
									file = getFile(cu.getPath().toString());
									if(file==null) {
										outsidePath = cu.getPath();
									}
								}
							}else if(unit instanceof IClassFile) {
								IClassFile cu = (IClassFile)unit;
								if(cu!=myUnit) {
									file = getFile(cu.getPath().toString());
									if(file==null) {
										outsidePath = cu.getPath();
									}
								}
							}
							
						}
						// linking to fields of the same compilation unit
						else if (element.getElementType() == IJavaElement.FIELD) {
							Object cu = ((IField) element).getCompilationUnit();
							if (cu != null && cu.equals(jspTranslation.getCompilationUnit())) {
								range = ((ISourceReference) element).getSourceRange();
							}
						}
						// linking to methods of the same compilation unit
						else if (element.getElementType() == IJavaElement.METHOD) {
							Object cu = ((IMethod) element).getCompilationUnit();
							if (cu != null && cu.equals(jspTranslation.getCompilationUnit())) {
								range = ((ISourceReference) element).getSourceRange();
							}
						}
					}
					if (range != null && file != null) {
						jspOffset = range.getOffset();
						if (jspOffset >= 0) {
							link = new WorkspaceFileHyperlink(region, file, new Region(jspOffset, range.getLength()));
						}
					}else if (range!=null && outsidePath!=null) {
						jspOffset = range.getOffset();
						if (jspOffset >= 0) {
							link = new ExternalFileHyperlink(region,outsidePath.toFile());
						}
					}
				} catch (JavaModelException jme) {
					Logger.log(Logger.WARNING_DEBUG, jme.getMessage(), jme);
				}
			}
			if (link == null) {
				link = new JSDTHyperlink(region, element);
			}
		}
		return link;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion, boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		List hyperlinks = new ArrayList(0);
		if (region != null && textViewer != null) {
			IDocument document = textViewer.getDocument();
			// check and make sure this is a valid Java type
			IJsTranslation jspTranslation = getJSPTranslation(document);
			if (jspTranslation != null) {
				// check if we are in JSP Java content
				// check that we are not in indirect Java content (like
				// included files)
				// get Java elements
				IJavaElement[] elements = jspTranslation.getElementsFromJsRange(region.getOffset(), region.getOffset() + region.getLength());
				if (elements != null && elements.length > 0) {
					// create a JSPJavaHyperlink for each Java element
					for (int i = 0; i < elements.length; ++i) {
						IJavaElement element = elements[i];
						// find hyperlink range for Java element
						IRegion hyperlinkRegion = selectWord(document, region.getOffset());
						IHyperlink link = createHyperlink(element, hyperlinkRegion, document);
						if (link != null) {
							hyperlinks.add(link);
						}
					}
				}
			}
		}
		if (hyperlinks.size() == 0) {
			return null;
		}
		return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[0]);
	}
	
	/**
	 * Returns an IFile from the given uri if possible, null if cannot find file
	 * from uri.
	 * 
	 * @param fileString
	 *            file system path
	 * @return returns IFile if fileString exists in the workspace
	 */
	private IFile getFile(String fileString) {
		IFile file = null;
		if (fileString != null) {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(fileString));
			for (int i = 0; i < files.length && file == null; i++) {
				if (files[i].exists()) {
					file = files[i];
				}
			}
		}
		return file;
	}
	
	/**
	 * Get JSP translation object
	 * 
	 * @return JSPTranslation if one exists, null otherwise
	 */
	private IJsTranslation getJSPTranslation(IDocument document) {
		IJsTranslation translation = null;
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (xmlModel != null) {
				IDOMDocument xmlDoc = xmlModel.getDocument();
				JsTranslationAdapter adapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
				if (adapter != null) {
					translation = adapter.getJSPTranslation(true);
				}
			}
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return translation;
	}
	
	/**
	 * Java always selects word when defining region
	 * 
	 * @param document
	 * @param anchor
	 * @return IRegion
	 */
	private IRegion selectWord(IDocument document, int anchor) {
		try {
			int offset = anchor;
			char c;
			while (offset >= 0) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				--offset;
			}
			int start = offset;
			offset = anchor;
			int length = document.getLength();
			while (offset < length) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				++offset;
			}
			int end = offset;
			if (start == end) {
				return new Region(start, 0);
			}
			return new Region(start + 1, end - start - 1);
		} catch (BadLocationException x) {
			return null;
		}
	}
}
