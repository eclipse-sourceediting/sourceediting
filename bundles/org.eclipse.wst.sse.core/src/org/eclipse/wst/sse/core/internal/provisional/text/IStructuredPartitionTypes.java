package org.eclipse.wst.sse.core.internal.provisional.text;

/**
 * This interface is not intended to be implemented.
 * It defines the partitioning for StructuredDocuments.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @deprecated use org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitions
 */
public interface IStructuredPartitionTypes {

	String DEFAULT_PARTITION = "org.eclipse.wst.sse.ST_DEFAULT"; //$NON-NLS-1$
	String UNKNOWN_PARTITION = "org.eclipse.wst.sse.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
}
