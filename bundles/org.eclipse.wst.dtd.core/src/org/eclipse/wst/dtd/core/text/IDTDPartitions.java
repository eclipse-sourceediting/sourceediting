package org.eclipse.wst.dtd.core.text;

import org.eclipse.wst.sse.core.text.IStructuredPartitions;

/**
 * This interface is not intended to be implemented.
 * It defines the partitioning for DTD and all its partitions.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.0
 */
public interface IDTDPartitions {
	
	String DTD_PARTITIONING = IStructuredPartitions.STRUCTURED_PARTITIONING;
	
	String DTD_DEFAULT = "org.eclipse.wst.dtd.DEFAULT"; //$NON-NLS-1$
}
