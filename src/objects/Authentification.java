package objects;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Authentification {
	public int id;
	public String fullname;
	
	public Authentification(String body) {
		System.out.println("Authentification Authentification body "+ body);
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(body));

	        Document doc = db.parse(is);
	        NodeList id = doc.getElementsByTagName("ID");
	        Element line = (Element) id.item(0);
	        setID(getCharacterDataFromElement(line));
	        
	        NodeList fullname = doc.getElementsByTagName("FULLNAME");
	        line = (Element) fullname.item(0);
	        setFullname(getCharacterDataFromElement(line));	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void setID(String id2){
		if(id2 == null){
			id = 0;
		}else{
			id = Integer.parseInt(id2);
		}
	}
	public void setID(int id2){
		id = id2;
	}
	public int getID(){
		return id;
	}
	public void setFullname(String fullname2){
		fullname = fullname2;
	}
	public String getFullname(){
		return fullname;
	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
}
