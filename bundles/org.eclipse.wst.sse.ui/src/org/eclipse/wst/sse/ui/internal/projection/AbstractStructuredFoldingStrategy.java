/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.projection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;

/**
 * <p>This class has the default implementation for a structured editor folding strategy.
 * Each content type that the structured editor supports should create an extension point
 * specifying a child class of this class as its folding strategy, if that content type
 * should have folding.</p>
 * 
 * <p>EX:<br />
 * <code>&lt;extension point="org.eclipse.wst.sse.ui.editorConfiguration"&gt;<br />
 *  &lt;provisionalConfiguration<br />
 *			type="foldingstrategy"<br />
 *			class="org.eclipse.wst.xml.ui.internal.projection.XMLFoldingStrategy"<br />
 *			target="org.eclipse.core.runtime.xml, org.eclipse.wst.xml.core.xmlsource" /&gt;<br />
 *	&lt;/extension&gt;</code></p>
 * 
 * <p>Different content types can use the same folding strategy if it makes sense to do so,
 * such as with HTML/XML/JSP.</p>
 * 
 * <p>This strategy is based on the Reconciler paradigm and thus runs in the background,
 * this means that even for very large documents requiring the calculation of 1000s of
 * folding annotations the user will not be effected except for the annotations may take
 * some time to first appear.</p>
 */
public abstract class AbstractStructuredFoldingStrategy
	extends AbstractStructuredTextReconcilingStrategy implements IProjectionListener {
	
	/**
	 * The org.eclipse.wst.sse.ui.editorConfiguration provisionalConfiguration type
	 */
	public static final String ID = "foldingstrategy"; //$NON-NLS-1$ 
	
	/**
	 * A named preference that controls whether folding is enabled in the
	 * Structured Text editor.
	 */
	public final static String FOLDING_ENABLED = "foldingEnabled"; //$NON-NLS-1$ 
	
	/**
	 * The annotation model associated with this folding strategy
	 */
	protected ProjectionAnnotationModel fProjectionAnnotationModel;
	
	/**
	 * The structured text viewer this folding strategy is associated with
	 */
	private StructuredTextViewer fViewer;
	
	/**
	 * these are not used but needed to implement abstract methods
	 */
	private IReconcileStep fFoldingStep;
	
	/**
	 * Default constructor for the folding strategy, can not take any parameters
	 * because subclasses instances of this class are created using reflection
	 * based on plugin settings
	 */
	public AbstractStructuredFoldingStrategy() {
		super();
	}
	
	/**
	 * The folding strategy must be associated with a viewer for it to function
	 * 
	 * @param viewer the viewer to associate this folding strategy with
	 */
	public void setViewer(StructuredTextViewer viewer) {
		super.setViewer(viewer);
		
		if(fViewer != null) {
			fViewer.removeProjectionListener(this);
		}
		fViewer = viewer;
		fViewer.addProjectionListener(this);
		fProjectionAnnotationModel = fViewer.getProjectionAnnotationModel();
	}
	
	public void uninstall() {
		setDocument(null);
		
		if(fViewer != null) {
			fViewer.removeProjectionListener(this);
			fViewer = null;
		}
		
		fFoldingStep = null;
		
		projectionDisabled();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		super.setDocument(document);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionDisabled()
	 */
	public void projectionDisabled() {
		fProjectionAnnotationModel = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionEnabled()
	 */
	public void projectionEnabled() {
		if(fViewer != null) {
			fProjectionAnnotationModel = fViewer.getProjectionAnnotationModel();
		}
	}
	
	/**
	 * <p><b>NOTE 1:</b> This implementation of reconcile ignores the given {@link IRegion} and instead gets all of the
	 * structured document regions effected by the range of the given {@link DirtyRegion}.</p>
	 * 
	 * <p><b>NOTE 2:</b> In cases where multiple {@link IRegion} maybe dirty it is more efficient to pass one
	 * {@link DirtyRegion} contain all of the {@link IRegion}s then one {@link DirtyRegion} for each IRegion.
	 * Case in point, when processing the entire document it is <b>recommended</b> that this function be
	 * called only <b>once</b> with one {@link DirtyRegion} that spans the entire document.</p>
	 * 
	 * @param dirtyRegion the region that needs its folding annotations processed
	 * @param subRegion ignored
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		IStructuredModel model = null;
		if(fProjectionAnnotationModel != null) {
			try {
				model = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
				if(model != null) {
					//use the structured doc to get all of the regions effected by the given dirty region
					IStructuredDocument structDoc = model.getStructuredDocument();
					IStructuredDocumentRegion[] structRegions = structDoc.getStructuredDocumentRegions(dirtyRegion.getOffset(), dirtyRegion.getLength());
					
					//these are what are passed off to the annotation model to
					//actually create and maintain the annotations
					List modifications = new ArrayList();
					List deletions = new ArrayList();
					Map additions = new HashMap();
					
					//reconcile each effected region and be sure project is still enabled
					for(int i = 0; ((i < structRegions.length) && (fProjectionAnnotationModel != null)); ++i) {
						IStructuredDocumentRegion structRegion = structRegions[i];
						IndexedRegion indexedRegion = model.getIndexedRegion(structRegion.getStartOffset());
						
						//if the region is not just white space determine if a new annotation needs to be added,
						//	or if an update to an existing annotation needs to take place, or possibly delete an existing
						//	annotation
						//else the region is just white space so the only possibility is to update an existing
						//	annotation, which could include deleting it
						if(structRegion.getText().trim().length() > 0) {
							FoldingAnnotation annotation = new FoldingAnnotation(indexedRegion, false);
							
							// if INSERT calculate new addition position or modification
							// else if REMOVE add annotation to the deletion list
							if(dirtyRegion.getType().equals(DirtyRegion.INSERT)) {
								Iterator iter = getAnnotationIterator(indexedRegion, false);
								//if projection has been disabled the iter could be null
								//if annotation does not already exist for this region create a new one
								//else modify an old one, which could include deletion
								if(iter != null && !iter.hasNext()) {
									Position newPos = calcNewFoldPosition(indexedRegion);

									if(newPos != null) {
										additions.put(annotation, newPos);
									}
								} else {
									updateAnnotations(iter, indexedRegion, modifications, deletions);
								}
							} else if (dirtyRegion.getType().equals(DirtyRegion.REMOVE)) {
								deletions.add(annotation);
							}
						} else {
							/*in the case where the original annotation length is 0
							 *	the position of the annotation gets moved, if something
							 *	causes the length to increase, such that without checking
							 *	for tags that start previous the annotation will not
							 *  be updated correctly
							 */	
							Iterator iter = getAnnotationIterator(indexedRegion, false);
							//if projection has been disabled the iter could be null
							if(iter != null) {
								if(!iter.hasNext()) {
									iter = getAnnotationIterator(indexedRegion, true);
								}
								
								updateAnnotations(iter, indexedRegion, modifications, deletions);
							}
						}
					}
					
					//be sure projection has not been disabled
					if(fProjectionAnnotationModel != null) {
						//send the calculated updates to the annotations to the annotation model
						fProjectionAnnotationModel.modifyAnnotations((Annotation[])deletions.toArray(new Annotation[1]), additions, (Annotation[])modifications.toArray(new Annotation[0]));
					}
				}
			} finally {
				if(model != null) {
					model.releaseFromRead();
				}
			}
		}
	}
	
	/**
	 * <p>Every implementation of the folding strategy calculates the position for a given
	 * IndexedRegion differently.  Also this calculation often relies on casting to internal classes
	 * only available in the implementing classes plugin</p>
	 * 
	 * @param indexedRegion the IndexedRegion to calculate a new annotation position for
	 * @return the calculated annotation position or NULL if none can be calculated based on the given region
	 */
	abstract protected Position calcNewFoldPosition(IndexedRegion indexedRegion);
	
	/**
	 * This is the default behavior for updating a dirtied IndexedRegion.  This function
	 * can be overridden if slightly different functionality is required in a specific instance
	 * of this class.
	 * 
	 * @param existingAnnotationsIter the existing annotations that need to be updated
	 * based on the given dirtied IndexRegion
	 * @param dirtyRegion the IndexedRegion that caused the annotations need for updating
	 * @param modifications the list of annotations to be modified
	 * @param deletions the list of annotations to be deleted
	 */
	protected void updateAnnotations(Iterator existingAnnotationsIter,
			IndexedRegion dirtyRegion, List modifications, List deletions) {
		
		//for each effected existing annotation decide what to do
		while(existingAnnotationsIter.hasNext()) {
			Object obj = existingAnnotationsIter.next();
			if(obj instanceof FoldingAnnotation) {
				FoldingAnnotation annotation = (FoldingAnnotation)obj;
				Position newPos = calcNewFoldPosition(annotation.getRegion());
				
				//if a new position can be calculated then update the position of the annotation,
				//else the annotation needs to be deleted
				if(newPos != null) {
					Position oldPos = fProjectionAnnotationModel.getPosition(annotation);
					oldPos.setOffset(newPos.offset);
					oldPos.setLength(newPos.length);
					modifications.add(annotation);
				} else {
					deletions.add(annotation);
				}
			}
		}
	}
	
	/**
	 * Should return true if the given IndexedRegion is one that this strategy pays attention to
	 * when calculating new and updated annotations
	 * 
	 * @param indexedRegion the IndexedRegion to check the type of
	 * @return true if the IndexedRegion is of a valid type, false otherwise
	 */
	abstract protected boolean indexedRegionValidType(IndexedRegion indexedRegion);
	
	/**
	 *  Steps are not used in this strategy
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#containsStep(org.eclipse.jface.text.reconciler.IReconcileStep)
	 */
	protected boolean containsStep(IReconcileStep step) {
		return fFoldingStep.equals(step);
	}

	/** 
	 * Steps are not used in this strategy
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#createReconcileSteps()
	 */
	public void createReconcileSteps() {
		fFoldingStep = new StructuredReconcileStep() { };
	}
	
	/**
	 * A FoldingAnnotation is a ProjectionAnnotation in a structured document.
	 * Its extended functionality include storing the <code>IndexedRegion</code> it is folding
	 * and overriding the paint method (in a hacky type way) to prevent one line folding
	 * annotations to be drawn.
	 */
	protected class FoldingAnnotation extends ProjectionAnnotation {
		private boolean fIsVisible; /* workaround for BUG85874 */
		
		/**
		 * The IndexedRegion this annotation is folding
		 */
		private IndexedRegion fRegion;
		
		/**
		 * Creates a new FoldingAnnotation that is associated with the given IndexedRegion
		 * 
		 * @param region the IndexedRegion this annotation is associated with 
		 * @param isCollapsed true if this annotation should be collapsed, false otherwise
		 */
		public FoldingAnnotation(IndexedRegion region, boolean isCollapsed) {
			super(isCollapsed);
			
			fIsVisible = false;
			fRegion = region;
		}
		
		/**
		 * Returns the IndexedRegion this annotation is associated with
		 * 
		 * @return the IndexedRegion this annotation is associated with
		 */
		public IndexedRegion getRegion() {
			return fRegion;
		}

		/**
		 * Does not paint hidden annotations. Annotations are hidden when they
		 * only span one line.
		 * 
		 * @see ProjectionAnnotation#paint(org.eclipse.swt.graphics.GC,
		 *      org.eclipse.swt.widgets.Canvas,
		 *      org.eclipse.swt.graphics.Rectangle)
		 */
		public void paint(GC gc, Canvas canvas, Rectangle rectangle) {
		
			/* workaround for BUG85874 */
			/*
			 * only need to check annotations that are expanded because hidden
			 * annotations should never have been given the chance to
			 * collapse.
			 */
			if (!isCollapsed()) {
				// working with rectangle, so line height
				FontMetrics metrics = gc.getFontMetrics();
				if (metrics != null) {
					// do not draw annotations that only span one line and
					// mark them as not visible
					if ((rectangle.height / metrics.getHeight()) <= 1) {
						fIsVisible = false;
						return;
					}
				}
			}
			fIsVisible = true;
			super.paint(gc, canvas, rectangle);
		}

		/**
		 * @see org.eclipse.jface.text.source.projection.ProjectionAnnotation#markCollapsed()
		 */
		public void markCollapsed() {
			/* workaround for BUG85874 */
			// do not mark collapsed if annotation is not visible
			if (fIsVisible)
				super.markCollapsed();
		}
		
		/**
		 * Two FoldingAnnotations are equal if their IndexedRegions are equal
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			boolean equal = false;
			
			if(obj instanceof FoldingAnnotation) {
				equal = fRegion.equals(((FoldingAnnotation)obj).fRegion);
			}
			
			return equal;
		}
		
		/**
		 * Returns the hash code of the IndexedRegion this annotation is associated with
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return fRegion.hashCode();
		}
		
		/**
		 * Returns the toString of the aIndexedRegion this annotation is associated with
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return fRegion.toString();
		}
	}
	
	/**
	 * Given an IndexedRegion returns an Iterator of the already existing annotations in
	 * that region.
	 * 
	 * @param indexedRegion the IndexedRegion to check for existing annotations in
	 * @param canStartBefore true if the search can start with annotations that start before
	 * the given IndexedRegion, false if the search must stay only within the indexed region.
	 * 
	 * @return an Iterator over the annotations in the given IndexedRegion.
	 * The iterator could have no annotations in it. Or <code>null</code> if projection has
	 * been disabled.
	 */
	private Iterator getAnnotationIterator(IndexedRegion indexedRegion, boolean canStartBefore) {
		Iterator annoIter = null;
		//be sure project has not been disabled
		if(fProjectionAnnotationModel != null) {
			annoIter = fProjectionAnnotationModel.getAnnotationIterator(indexedRegion.getStartOffset(), indexedRegion.getEndOffset()-indexedRegion.getStartOffset(), canStartBefore, false);
		}
		return annoIter;
	}
}
