
package org.eclipse.wst.xml.xpath2.processor.testsuite;

import java.net.URL;

import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSBoolean;
      
      
public class SequenceTypeSyntax extends AbstractPsychoPathTest {

   //Simple evaluation of sequence type matching involving instance of and a sequence of integers.
   public void sequence_type_1 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of doubles.
   public void sequence_type_2 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of decimal.
   public void sequence_type_3 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of strings.
   public void sequence_type_4 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of strings. (uses integer*).
   public void sequence_type_5 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of one integer (Uses integer?).
   public void sequence_type_6 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of integers. (Uses integer?).
   public void sequence_type_7 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of integers. (Uses integer+).
   public void sequence_type_8 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of one integer. (Uses integer?).
   public void sequence_type_9 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of two integers. (Uses integer?).
   public void sequence_type_10 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of one double. (Uses double?).
   public void sequence_type_11 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of two double. (Uses double?).
   public void sequence_type_12 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of one decimal. (Uses decimal?).
   public void sequence_type_13 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of two decimals. (Uses decimal?).
   public void sequence_type_14 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of one string. (Uses string?).
   public void sequence_type_15 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of two strings. (Uses string?).
   public void sequence_type_16 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of one string. (Uses integer?).
   public void sequence_type_17 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving castable as and a sequence of two strings. (Uses integer?).
   public void sequence_type_18 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of two booleans. (Uses xs:boolean*).
   public void sequence_type_19 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of two booleans. (Uses xs:boolean?).
   public void sequence_type_20 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

   //Simple evaluation of sequence type matching involving instance of and a sequence of two booleans. (Uses xs:boolean+).
   public void sequence_type_21 throws Exception {
      URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = "string-length(x) > 2";
      XPath path = compileXPath(dc, xpath);

      Evaluator eval = new DefaultEvaluator(dc, domDoc);
      ResultSequence rs = eval.evaluate(path);

      XSBoolean result = (XSBoolean) rs.first();

      String actual = result.string_value();

      assertEquals("true", actual);
        

   }

}
      