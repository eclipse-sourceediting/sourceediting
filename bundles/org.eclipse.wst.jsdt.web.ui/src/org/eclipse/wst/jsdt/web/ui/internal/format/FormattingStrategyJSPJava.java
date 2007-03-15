package org.eclipse.wst.jsdt.web.ui.internal.format;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.core.ToolFactory;
import org.eclipse.wst.jsdt.core.formatter.CodeFormatter;
import org.eclipse.wst.jsdt.internal.compiler.util.Util;
import org.eclipse.wst.jsdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.wst.jsdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.wst.jsdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

public class FormattingStrategyJSPJava extends ContextBasedFormattingStrategy{
	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments= new LinkedList();
	/** Partitions to be formatted by this strategy */
	private final LinkedList fPartitions= new LinkedList();
	
	private static final int regionStartIndentLevel=1;
	
	private int startIndentLevel;

	/**
	 * Creates a new java formatting strategy.
 	 */
	public FormattingStrategyJSPJava() {
		super();
	}
	
	private class partitionerAttachedChangeListener implements IDocumentPartitioningListener{
		
		private IDocument attachedDoc;
		
		partitionerAttachedChangeListener(IDocument attachedDoc){
			this.attachedDoc=attachedDoc;
		}
	
		public void documentPartitioningChanged(IDocument document) {
			if (document==attachedDoc && document instanceof IDocumentExtension3) {
				IDocumentExtension3 extension3= (IDocumentExtension3) document;
				String[] partitionings= extension3.getPartitionings();
				Vector partitioners = new Vector();
				for (int i= 0; i < partitionings.length; i++) {
					IDocumentPartitioner partitioner= extension3.getDocumentPartitioner(partitionings[i]);
					if (partitioner instanceof StructuredTextPartitioner) {
						IDOMModel xmlModel = null;
						try {
							xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(document);
							xmlModel.reinit();
						}catch(Exception e){
							System.out.println("Exception in adapter for model re-init"+e);
						} finally {
							if (xmlModel != null) {
								xmlModel.releaseFromRead();
							}
						}
						document.removeDocumentPartitioningListener(this);
					}
				}
			}
			
		}
	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#format()
	 */
	public void format() {
		super.format();
		final IStructuredDocument document= (IStructuredDocument)fDocuments.removeFirst();
		final TypedPosition partition= (TypedPosition)fPartitions.removeFirst();
	
		if (document != null) {
			try {
				JSPTranslationUtil translationUtil = new JSPTranslationUtil(document);
				ICompilationUnit cu = translationUtil.getCompilationUnit();
				if (cu != null) {
					String cuSource = cu.getSource();
					
					int javaStart = translationUtil.getTranslation().getJavaOffset(partition.getOffset());
					int javaLength =  partition.getLength();
					TextEdit edit= CodeFormatterUtil.format2(CodeFormatter.K_COMPILATION_UNIT, cuSource, javaStart, javaLength, startIndentLevel, TextUtilities.getDefaultLineDelimiter(document), getPreferences());
					IDocument doc = translationUtil.getTranslation().getJavaDocument();
					edit.apply(doc);
					String replaceText =  TextUtilities.getDefaultLineDelimiter(document) + getIndentationString(getPreferences(),startIndentLevel) + (doc.get(edit.getOffset(), edit.getLength())).trim() + TextUtilities.getDefaultLineDelimiter(document);
				
					/* this is necisary since SSE disconnects document partitioners..  Because of that, the 
					 * replaced regions are considered 'partitionless' when the linestyle provider trys to 
					 * color them. 
					 * 
					 */
					document.replaceText(document, partition.getOffset(), partition.getLength(), replaceText);
					document.addDocumentPartitioningListener(new partitionerAttachedChangeListener(document));			
				}
			}catch(BadLocationException e){
				
			}catch(JavaModelException e){
				
			}
		}
 	}

	/*
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
	 */
	public void formatterStarts(final IFormattingContext context) {
		fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
		
		startIndentLevel = regionStartIndentLevel + 0;
		
		Map projectOptions = (Map)context.getProperty(FormattingContextProperties.CONTEXT_PREFERENCES);
		if(projectOptions==null ){
			IDocument doc = (IDocument)context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM);
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
		startIndentLevel=0;
	}
	
	public String getIndentationString(Map options, int indentationLevel){
		DefaultCodeFormatter formatter = new DefaultCodeFormatter(options);
		return formatter.createIndentationString(indentationLevel);
	}
	
	private Map getProjectOptions(IDocument baseDocument){
		IJavaProject javaProject = null;
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

			if(project!=null)
				javaProject = JavaCore.create(project);
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		
		if(javaProject!=null){
			options = javaProject.getOptions(true);
		}
		return options;	
	}
	
	private Map getNodeFromRegion(IDocument baseDocument){
		IJavaProject javaProject = null;
		IDOMModel xmlModel = null;
		Map options = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(baseDocument);
			
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		
		if(javaProject!=null){
			options = javaProject.getOptions(true);
		}
		return options;	
	}
}
