package org.eclipse.wst.sse.core.text;

/**
 * This interface is not intended to be implemented.
 * It defines the partitioning for StructuredDocuments.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.0
 */
public interface IStructuredPartitionTypes {

	String DEFAULT_PARTITION = "org.eclipse.wst.sse.ST_DEFAULT"; //$NON-NLS-1$
	String UNKNOWN_PARTITION = "org.eclipse.wst.sse.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
}
