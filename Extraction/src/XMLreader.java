import java.io.File;
import java.io.IOException;
import java.util.Map;

//from w  w  w  .  j a  v a  2s  .com
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLreader extends DefaultHandler {

  private static XMLreader handler = null;
  Map<String, String> results;

  private SAXParser parser = null;
  
  static String id;
  
  public static void newDoc(File f, String file_id){

    File xmlFile = f;
    id = file_id;
    handler = new XMLreader();
    handler.process(xmlFile);
    
    
  }

  private void process(File file) {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    spf.setValidating(true);
//    System.out.println("Parser will " + (spf.isNamespaceAware() ? "" : "not ")
//        + "be namespace aware");
//    System.out.println("Parser will " + (spf.isValidating() ? "" : "not ") + "validate XML");
    try {
      parser = spf.newSAXParser();
      System.out.println("Parser object is: " + parser);
    } catch (SAXException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    } catch (ParserConfigurationException e) {
      e.printStackTrace(System.err);
      System.exit(1);
	    }
	    System.out.println("\nStarting parsing of " + file + "\n");
	    try {
	      parser.parse(file, this);
	    } catch (IOException e) {
	      e.printStackTrace(System.err);
	    } catch (SAXException e) {
	      e.printStackTrace(System.err);
	    }
	  }

  public void startDocument() {
    System.out.println("Start document: ");
    //Indexer.add(id, )
  }

  public void endDocument() {
    System.out.println("End document: ");
  }

  public void startElement(String uri, String localName, String qname, Attributes attr) {
	 
    System.out.println("Start element: local name: " + localName + " qname: " + qname + " uri: "
        + uri);
  }

  public void endElement(String uri, String localName, String qname) {
    System.out.println("End element: local name: " + localName + " qname: " + qname + " uri: "
        + uri);
  }

  public void characters(char[] ch, int start, int length) {
    System.out.println("Characters: " + new String(ch, start, length));
  }

  public void ignorableWhitespace(char[] ch, int start, int length) {
    System.out.println("Ignorable whitespace: " + new String(ch, start, length));
  }

}