import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
//CITATION: from www.java2s.com
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XML {
	
	static Map<String, String> results = new HashMap<String, String>();
	static String THEFUAK;
	
  public void parse(String filename) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    factory.setIgnoringElementContentWhitespace(true);
    factory.setExpandEntityReferences(false);

    Document doc = factory.newDocumentBuilder().parse(new File(filename));
    
    
    THEFUAK = "d";
    
    
//    XPathFactory xpf = XPathFactory.newInstance();
//    XPath xp = xpf.newXPath();
//    NodeList nodes = (NodeList)xp.evaluate("//@* | //*[not(*)]", doc, XPathConstants.NODESET);
//
//    System.out.println(nodes.getLength());
//
//    for (int i=0, len=nodes.getLength(); i<len; i++) {
//        Node item = nodes.item(i);
//        System.out.println(item.getNodeName() + " : " + item.getTextContent());
//    }
    
    visit(doc, 0, null, 1);
    
  }
  

public static void visit(Node node, int level, String p, int ctr) {
	String h, c = null;	
	String[] x = null;
	node.normalize();
    NodeList list = node.getChildNodes();
    boolean sentence = false;
    for (int i = 0; i < list.getLength(); i++) {
    	Node childNode = list.item(i);
    	//if(childNode.getNodeName().equals("#text")) continue;
    	h = childNode.getNodeName();
    	c = childNode.getNodeValue();
    	
    	 	
    	if(c == null) {
    		if(childNode.getFirstChild() != null) {
    			c = childNode.getFirstChild().getNodeValue();
    			
    			x = c.split(" ");
    			if (x.length > 1) {
    				sentence = true;
    			}
    			System.out.println(sentence + c);
    		}
    	}
    	
    	if(!h.contains("#")) {
//    		System.out.println("FOUND ME");
//    		if(childNode.getNextSibling() != null) {
//    			System.out.println("dont print");
//    		}
//    		if(childNode.getFirstChild() != null) {
//    			System.out.println("child dont print");
//    		}
    		
    		
    		if(Character.isWhitespace(c.charAt(0))) {
    			//System.out.println("gotteeem, " + h);
    			try{
    				c = childNode.getParentNode().getNodeName();
    			} catch (NullPointerException e) {
    				c = THEFUAK;
    			}
    			
    			if (c.contains("#")) {
    				c = THEFUAK;
    				if(!h.equals(c)) {
    					System.out.println(h + ", " + c);
    					Indexer.add(c.trim(), h.trim());
						Linker.add(c.trim(), h.trim());
						
    				}
    			}
    			if(level == 0) {
    				h = THEFUAK + "." + h;
    				if(!h.equals(c)) {
    					System.out.println(h + ", " + c);
    					Indexer.add(c.trim(), h.trim());
						Linker.add(c.trim(), h.trim());
						
    				}
    			}
    			else if (level == 1) {
    				if (!sentence) {
    					c = THEFUAK + "." + c;
    					h = THEFUAK + "." +ctr + "elt" + level + "." + h;
    					System.out.println(h + ", " + c);
    					Indexer.add(c.trim(), h.trim());
						Linker.add(c.trim(), h.trim());
						
    				}
    				else {
    					for(String gg : x) {
    						if(!(gg.equals("a")) && !(gg.equals("the")) && !(gg.equals("here")) && !(gg.equals("you")) && !(gg.equals("is"))) {
	    						gg = THEFUAK + "." + gg;
	    						h = THEFUAK + "." + ctr + "elt" + level + "." + h;
	    						System.out.println(h + ", " + c);
	    						Indexer.add(h.trim(), gg.trim());
	    						Linker.add(h.trim(), gg.trim());
    						}
    						
    					}
    				}
    				
    			}
    			else if (childNode.getFirstChild().hasChildNodes()){
    				if (!sentence) {
    					h = THEFUAK + "." + ctr + "elt" + level + "." + h;
        				c = THEFUAK + "." + ctr + "elt" + level + "." + c;
        				System.out.println(h + ", " + c);
        				Indexer.add(c.trim(), h.trim());
						Linker.add(c.trim(), h.trim());
    				}
    				else {
    					for(String gg : x) {
    						if(!(gg.equals("a")) && !(gg.equals("the")) && !(gg.equals("here")) && !(gg.equals("you")) && !(gg.equals("is"))) {
	    						h = THEFUAK + "." + ctr + "elt" + level + "." + h;
	    	    				gg = THEFUAK + "." + ctr + "elt" + level + "." + gg;
	    	    				System.out.println(h + ", " + gg);
	//    	    				Indexer.add(gg.trim(), h.trim());
	//    	    				Linker.add(gg.trim(), h.trim());
	    	    				Indexer.add(h.trim(), gg.trim());
	    						Linker.add(h.trim(), gg.trim());
    						}
    					}
    				}
    				
    				
    			}
//    			System.out.println(c + ", " + h);
    			p = h;
    		} else {
    			//if (childNode.getParentNode().getParentNode().getParentNode().getParentNode().getNodeName() == null) System.out.println("new");
//    			System.out.println(childNode.getParentNode().getParentNode().getParentNode().getNodeName());
//    			System.out.println(childNode.getParentNode().getParentNode().getNodeName());
    			
    			if(level == 0) {
    				h = THEFUAK + "." + h;
    				if(!h.equals(c)) {
    					System.out.println(h + ", " + c);
	    				Indexer.add(h.trim(), c.trim());
						Linker.add(h.trim(), c.trim());
    				}
    			}
    			else if (level == 1) {
    				if (!sentence) {
    					c = THEFUAK + "." + c;
    					h = THEFUAK + "." + ctr + "elt" + level + "." + h;
    					System.out.println(h + ", " + c);
    					Indexer.add(h.trim(),  c.trim());
						Linker.add(h.trim(), c.trim());
    				}
    				else {
    					for(String gg : x) {
    						if(!(gg.equals("a"))	 && !(gg.equals("the")) && !(gg.equals("here")) && !(gg.equals("you")) && !(gg.equals("is"))) {
	    						gg = THEFUAK + "." + gg;
	    						h = THEFUAK + "." + ctr + "elt" + level + "." + h;
	    						System.out.println(h + ", " + gg);
	    						Indexer.add(h.trim(), gg.trim());
	    						Linker.add(h.trim(), gg.trim());
    						}
    					}
    				}
    				
    			}
    			else if (childNode.hasChildNodes()){
    				h = THEFUAK + "." + ctr + "elt" + level + "." + h;
    				
    				if (!sentence) {
    					System.out.println(h + ", " + c);
    					Indexer.add(h.trim(), c.trim());
						Linker.add(h.trim(), c.trim());
    				}
    				else {
    					for(String gg : x) {
    						if(!(gg.equals("a")) && !(gg.equals("the")) && !(gg.equals("here")) && !(gg.equals("you")) && !(gg.equals("is"))) {
	    						System.out.println(h + ", " + gg);
	    						Indexer.add(h.trim(), gg.trim());
	    						Linker.add(h.trim(), gg.trim());
    						}
    					}
    				}
//    				c = THEFUAK + "." + ctr + "elt" + level + "." + c;
//    				System.out.println(childNode.getFirstChild().getNodeName());
//    				System.out.println(childNode.getFirstChild().getNodeValue());
    			} else {
    				System.out.println("no kids divorced sad life");
    				Indexer.add(h.trim(), c.trim());
    				Linker.add(h.trim(), c.trim());
    				continue;
    			}
//    			System.out.println(childNode.getFirstChild().hasChildNodes());
//    			System.out.println(c);
    			System.out.println(h + ", " + c);
//    			Indexer.add(c.trim(), h.trim());
//				Linker.add(c.trim(), h.trim());
    			if (p != null) {
    				Indexer.add(p.trim(), h.trim());
    				Linker.add(p.trim(), h.trim());
    				System.out.println(p + ", " + h);
    			}
    			//System.out.println(level);
    		}
    	}
    	
//    	System.out.println("Name:" + h);
//    	System.out.println("Value:" +childNode.getNodeValue());
//    	System.out.println(h + ", " + c);
    	
//    	if(childNode.getFirstChild() != null) System.out.println("child: " + childNode.getFirstChild().getNodeName());
    	
//    	Node sibling = childNode.getNextSibling();
//    	if(childNode.getNodeValue() == null) {
//    		
//    		while (!(sibling instanceof Element) && sibling != null) {
//    			  sibling = sibling.getNextSibling();
//    		}
//    		if(sibling != null && !sibling.getNodeName().equals("#text")) c = sibling.getNodeValue();
//    		
//    		else if (childNode.hasChildNodes()){
//	    		System.out.println("no sibling");
//	    		
//	    		sibling = childNode.getFirstChild();
//	    		if (sibling.getNodeValue().equals("#text")) {
//	    			sibling = sibling.getNextSibling();
//	    		}
//	    		c = sibling.getNodeName();
//	    	}
//    		else c = null;
//    	}
//    	System.out.println(h + ", " + c);
//    	
//    	System.out.println(childNode.hasAttributes());
//    	if(childNode.getNextSibling() != null){ 
//    		System.out.println(childNode.getNextSibling().getNodeName());
//    	}
    	
    	
    	  
      /* if(childNode.getNextSibling() != null){
    	  if(childNode.getNextSibling().getNodeName().equals("#text")) {
    		  if(childNode.getNextSibling().getNextSibling() != null) {
    			  if(childNode.getNextSibling().getNextSibling().getNodeName().equals("#text")) {
    				  c = "pls";
    			  }
    		  } else {
    	    	  c = childNode.getNextSibling().getNodeName();
    	    	  }
    	  } else {
    	  c = childNode.getNextSibling().getNodeName();
    	  }
      } */
	      //System.out.println(h + ", " + c);
    	ctr++;
	      visit(childNode, level + 1, p, ctr);
    }
      
  }
}