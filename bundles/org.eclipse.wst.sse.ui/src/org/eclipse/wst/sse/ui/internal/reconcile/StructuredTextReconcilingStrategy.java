package org.eclipse.wst.sse.ui.internal.reconcile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.StructuredMarkerAnnotation;

/**
 * Uses IStructuredModel nodes to check "overlaps()" when getting annotations to remove.
 */
public abstract class StructuredTextReconcilingStrategy extends AbstractStructuredTextReconcilingStrategy {
	

	public StructuredTextReconcilingStrategy(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	protected TemporaryAnnotation[] getAnnotationsToRemove(DirtyRegion dr, List validTotalScopeSteps) {
		IDocument doc = getDocument();
		IStructuredDocument sDoc = null;
		
		if(doc instanceof IStructuredDocument)
			sDoc = (IStructuredDocument)doc;
		
		// if there's no structured document
		// do regular IDocument remove in super
		if(sDoc == null)
			return super.getAnnotationsToRemove(dr, validTotalScopeSteps);
			
		IStructuredDocumentRegion[] sdRegions = sDoc.getStructuredDocumentRegions(dr.getOffset(), dr.getLength());
		List remove = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		// can be null when closing the editor
		if (getAnnotationModel() != null) {
			
			// clear validator annotations
			getMarkerAnnotations().clear();
			
			Iterator i = annotationModel.getAnnotationIterator();
			while (i.hasNext()) {
				
				Object obj = i.next();
				
				// check if it's a validator marker annotation
				// if it is save it for comparison later (to "gray" icons)
				if(obj instanceof StructuredMarkerAnnotation) {
					StructuredMarkerAnnotation sma = (StructuredMarkerAnnotation)obj;
					if(sma.getAnnotationType() == TemporaryAnnotation.ANNOT_ERROR || sma.getAnnotationType() == TemporaryAnnotation.ANNOT_WARNING)
						getMarkerAnnotations().add(sma);
				}
				
				if (!(obj instanceof TemporaryAnnotation))
					continue;

				TemporaryAnnotation annotation = (TemporaryAnnotation) obj;
				ReconcileAnnotationKey key = (ReconcileAnnotationKey) annotation.getKey();
				
				// then if this strategy knows how to add/remove this
				// partition type
				if (canHandlePartition(key.getPartitionType()) && containsStep(key.getStep())) {
					if (key.getScope() == ReconcileAnnotationKey.PARTIAL && overlaps(annotation.getPosition(), sdRegions)) {
						remove.add(annotation);
					}
					else if (key.getScope() == ReconcileAnnotationKey.TOTAL && validTotalScopeSteps.contains(key.getStep())) {
						remove.add(annotation);
					}
				}
			}
		}
		return (TemporaryAnnotation[]) remove.toArray(new TemporaryAnnotation[remove.size()]);
	}

	/**
	 * Checks if this position overlaps any of the StructuredDocument regions'
	 * correstponding IndexedRegion.
	 * 
	 * @param pos
	 * @param dr
	 * @return true if the position overlaps any of the regions, otherwise
	 *         false.
	 */
	protected boolean overlaps(Position pos, IStructuredDocumentRegion[] sdRegions) {
		int start = -1;
		int end = -1;
		for (int i = 0; i < sdRegions.length; i++) {
		    if(!sdRegions[i].isDeleted()) {
    			IndexedRegion corresponding = getCorrespondingNode(sdRegions[i]);
                if(corresponding != null) {
        			if (start == -1 || start > corresponding.getStartOffset())
        				start = corresponding.getStartOffset();
        			if (end == -1 || end < corresponding.getEndOffset())
        				end = corresponding.getEndOffset();
                }
            }
		}
		return pos.overlapsWith(start, end - start);
	}

	/**
	 * Returns the corresponding node for the StructuredDocumentRegion.
	 * 
	 * @param sdRegion
	 * @return the corresponding node for sdRegion
	 */
	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
        IndexedRegion indexedRegion = null;
        try {
            if (sModel != null) 
                indexedRegion = sModel.getIndexedRegion(sdRegion.getStart());    
        } finally {
            if (sModel != null)
                sModel.releaseFromRead();
        }
        return indexedRegion;
    }

}
