/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.format;

import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.formatter.CodeFormatter;
import org.eclipse.wst.jsdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.wst.jsdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class FormattingStrategyJSDT extends ContextBasedFormattingStrategy {
	private static final String XML_COMMENT_START = "<!--"; //$NON-NLS-1$
	private static final String XML_COMMENT_END = "//-->"; //$NON-NLS-1$
	
	/** matches on <!-- at beginning of script region */
	private static final Pattern START_PATTERN = Pattern.compile("(\\A(\\s*<!--))");
	
	/** matches on //--> or --> at end of script region */
	private static final Pattern END_PATTERN = Pattern.compile("(((//\\s*)?-->\\s*)\\z)");
	
	private static final int regionStartIndentLevel = 1;
	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments = new LinkedList();
	/** Partitions to be formatted by this strategy */
	private final LinkedList fPartitions = new LinkedList();
	private int startIndentLevel;
	
	/**
	 * Creates a new java formatting strategy.
	 */
	public FormattingStrategyJSDT() {
		super();
	}
	
	class ModelIrritant implements IDocumentPartitioningListener {
		public ModelIrritant(IDocument attachedDoc) {}
		
		public void documentPartitioningChanged(IDocument document) {
			document.removeDocumentPartitioningListener(this);
			if (document instanceof BasicStructuredDocument) {
				try {
					((BasicStructuredDocument) document).replace(0, document.getLength(), document.get());
				} catch (BadLocationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#format()
	 */
	
	public void format() {
		super.format();
		final IStructuredDocument document = (IStructuredDocument) fDocuments.removeFirst();
		final TypedPosition partition = (TypedPosition) fPartitions.removeFirst();
		if (document != null) {
			String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
			try {
				//get the JS text from the document (not translated)
				String jsTextNotTranslated = document.get(partition.getOffset(), partition.getLength());
				
				//deal with getting the JS text and unwrapping it from any <!-- //--> statements
				String preText = "";
				String postText = lineDelim;

				//find start comment tag
				Matcher matcher = START_PATTERN.matcher(jsTextNotTranslated);
				if(matcher.find()) {
					jsTextNotTranslated = matcher.replaceAll("");
					preText = lineDelim + XML_COMMENT_START;
				}
				
				//find end tag
				matcher = END_PATTERN.matcher(jsTextNotTranslated);
				if(matcher.find()) {
					jsTextNotTranslated = matcher.replaceAll("");
					postText = lineDelim + XML_COMMENT_END + lineDelim;
				}
				
				//replace the text in the document with the none-translated JS text but without HTML leading and trailing comments
				TextEdit replaceEdit = new ReplaceEdit(partition.getOffset(), partition.getLength(), jsTextNotTranslated);
				replaceEdit.apply(document);
				int jsRegionLength = jsTextNotTranslated.length();
				
				//translate the updated document
				IJsTranslation translation = getTranslation(document);
				String jsTextTranslated = translation.getJsText();
				
				//format the text translated text
				TextEdit edit = CodeFormatterUtil.format2(CodeFormatter.K_JAVASCRIPT_UNIT, jsTextTranslated, partition.getOffset(), jsRegionLength, startIndentLevel, lineDelim, getPreferences());
				IDocument jsDoc = new Document(jsTextTranslated);
				
				//Undo the text replacements done by the translator so that it could build a CU for the JS region
				if(translation instanceof JsTranslation) {
					IJsTranslator translator = ((JsTranslation)translation).getTranslator();
					
					if(translator instanceof JsTranslator) {
						Region[] regions = ((JsTranslator)translator).getGeneratedRanges();
						//for each generated range, replace it with the original text
						for(int r = 0; r < regions.length; ++r) {
							jsDoc.replace(regions[r].getOffset(), regions[r].getLength(),
									document.get(regions[r].getOffset(), regions[r].getLength()));
						}
					}
				}
				
				/* error formating the code so abort */
				if(edit==null) return;
				edit.apply(jsDoc);
				String replaceText = lineDelim + getIndentationString(getPreferences(), startIndentLevel) + (jsDoc.get(edit.getOffset(), edit.getLength())).trim();
				
				//apply edit to html doc using the formated translated text and the possible leading and trailing html comments
				replaceText = preText + replaceText + postText;
				replaceEdit = new ReplaceEdit(partition.getOffset(), jsRegionLength, replaceText);
				replaceEdit.apply(document);
			} catch (BadLocationException e) {
			}
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
	 */
	
	public void formatterStarts(final IFormattingContext context) {
		fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
		startIndentLevel = FormattingStrategyJSDT.regionStartIndentLevel + 0;
		Map projectOptions = (Map) context.getProperty(FormattingContextProperties.CONTEXT_PREFERENCES);
		if (projectOptions == null) {
			IDocument doc = (IDocument) context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM);
			context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, getProjectOptions(doc));
		}
		super.formatterStarts(context);
	}
	
	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStops()
	 */
	
	public void formatterStops() {
		super.formatterStops();
		fPartitions.clear();
		fDocuments.clear();
		startIndentLevel = 0;
	}
	
	public String getIndentationString(Map options, int indentationLevel) {
		DefaultCodeFormatter formatter = new DefaultCodeFormatter(options);
		return formatter.createIndentationString(indentationLevel);
	}
	
	private Map getProjectOptions(IDocument baseDocument) {
		IJavaScriptProject javaProject = null;
		IDOMModel xmlModel = null;
		Map options = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(baseDocument);
			String baseLocation = xmlModel.getBaseLocation();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
			if (project != null) {
				javaProject = JavaScriptCore.create(project);
			}
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		if (javaProject != null) {
			options = javaProject.getOptions(true);
		}
		return options;
	}
	
	public IJsTranslation getTranslation(IStructuredDocument document) {
		IJsTranslation tran = null;
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(document);
			IDOMDocument xmlDoc = xmlModel.getDocument();
			JsTranslationAdapter translationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			if (translationAdapter != null) {
				tran = translationAdapter.getJsTranslation(true);
			}
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return tran;
	}
}
