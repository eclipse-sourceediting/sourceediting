package org.eclipse.wst.css.core.text;

import org.eclipse.wst.sse.core.text.IStructuredPartitions;

/**
 * This interface is not intended to be implemented.
 * It defines the partitioning for CSS and all its partitions.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.0
 */
public interface ICSSPartitions extends IStructuredPartitions {

	String CSS_PARTITIONING = IStructuredPartitions.STRUCTURED_PARTITIONING;
	
	String STYLE = "org.eclipse.wst.css.STYLE"; //$NON-NLS-1$
}
