package org.eclipse.wst.xsl.ui.internal.validation;

import javax.xml.transform.*;
import java.util.logging.*;


public class XSLErrorListener implements ErrorListener {

  private Logger logger;
  
  public XSLErrorListener(Logger logger) {
    this.logger = logger;
  }
  
  public void warning(TransformerException exception) {
   
    logger.log(Level.WARNING, exception.getMessage(), exception);
   
    // Don't throw an exception and stop the processor
    // just for a warning; but do log the problem
  }
  
  public void error(TransformerException exception)
   throws TransformerException {
    
    logger.log(Level.SEVERE, exception.getMessage(), exception);
    // XSLT is not as draconian as XML. There are numerous errors
    // which the processor may but does not have to recover from; 
    // e.g. multiple templates that match a node with the same
    // priority. I do not want to allow that so I throw this 
    // exception here.
    throw exception;
    
  }
  
  public void fatalError(TransformerException exception)
   throws TransformerException {
    
    logger.log(Level.SEVERE, exception.getMessage(), exception);

    // This is an error which the processor cannot recover from; 
    // e.g. a malformed stylesheet or input document
    // so I must throw this exception here.
    throw exception;
    
  }
     
}
