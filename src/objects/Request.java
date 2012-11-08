package objects;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Request {
	private String keyword;
	private String body;
	public Request(){
		keyword = "";
		body = "";
	}
	
	public Request(String line) {
	    try {
	    	System.out.println("Request line : "+line);
	    	System.out.println("Request line length : "+line.length());
	       /* DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(line));

	        Document doc = db.parse(is);	
	        */
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new InputSource(new StringReader(line)));
			
	        NodeList keywordNode = doc.getElementsByTagName("KEYWORD");
	        Element line2 = (Element) keywordNode.item(0);
	        //System.out.println("Request setKeyword : "+ getCharacterDataFromElement(line2));
	        String keyword = getCharacterDataFromElement(line2);
	        setKeyword(keyword);
	        if(keyword.equals("SENDPOST")){
	        	System.out.println("Request Request SENDPOST");
	        	parseSENDPOST(doc);
	        }else if(keyword.equals("AUTHENTIFICATION")){
	        	System.out.println("Request Request AUTHENTIFICATION");
	        	parseAuthentification(doc);
	        }

	    }
	    catch (Exception e) {
	    	System.out.println("Request Request error " + e );
	        e.printStackTrace();
	    }
	}
	
	private void parseSENDPOST(Document doc) {
        NodeList IDNode = doc.getElementsByTagName("ID");
        Element line = (Element) IDNode.item(0);
        String id = getCharacterDataFromElement(line);
        
        NodeList OWNERNode = doc.getElementsByTagName("OWNER");
        line = (Element) OWNERNode.item(0);
        String owner = getCharacterDataFromElement(line);       
        
        NodeList DATENode = doc.getElementsByTagName("DATE");
        line = (Element) DATENode.item(0);
        String date = getCharacterDataFromElement(line);      
        
        NodeList CONTENTode = doc.getElementsByTagName("CONTENT");
        line = (Element) CONTENTode.item(0);
        String content = getCharacterDataFromElement(line);      
        
		StringBuffer body = new StringBuffer();
		body.append("<POST>");
		body.append("<OWNER>"+owner+"</OWNER>");
		body.append("<ID>"+id+"</ID>");
		body.append("<DATE>"+date+"</DATE>");
		body.append("<CONTENT>"+content+"</CONTENT>");
		body.append("</POST>");
        setBody(body.toString());
	}

	public Post requestBodyTOPost(){
		String line = this.getBody();
        Post post = new Post();
		try{
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(line));
	
	        Document doc = db.parse(is);
	        NodeList IDNode = doc.getElementsByTagName("ID");
	        Element line1 = (Element) IDNode.item(0);
	        String id = getCharacterDataFromElement(line1);
	        
	        NodeList OWNERNode = doc.getElementsByTagName("OWNER");
	        line1 = (Element) OWNERNode.item(0);
	        String owner = getCharacterDataFromElement(line1);       
	        
	        NodeList DATENode = doc.getElementsByTagName("DATE");
	        line1 = (Element) DATENode.item(0);
	        String date = getCharacterDataFromElement(line1);      
	        
	        NodeList CONTENTode = doc.getElementsByTagName("CONTENT");
	        line1 = (Element) CONTENTode.item(0);
	        String content = getCharacterDataFromElement(line1);

	        post.setContent(content);
	        post.setOwner(owner);
	        post.setDate(date);
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
        return post;
	}
	
	public void parseAuthentification(Document doc){
	    NodeList IDNode = doc.getElementsByTagName("ID");
	    Element line1 = (Element) IDNode.item(0);
	    String id = getCharacterDataFromElement(line1);
	    
	    NodeList OWNERNode = doc.getElementsByTagName("FULLNAME");
	    line1 = (Element) OWNERNode.item(0);
	    String fullname = getCharacterDataFromElement(line1);
	    
		StringBuffer body = new StringBuffer();
		body.append("<POST>");
		body.append("<ID>"+id+"</ID>");
		body.append("<OWNER>"+fullname+"</OWNER>");
		body.append("</POST>");
		System.out.println("Request parseAuthentification() setBody: "+body.toString());
        setBody(body.toString());
	}
	//cette foncion permet de recuper tous les fils firect d'une balises
	/*
	public void parseBalise(Document doc){
	    Element el = (Element)(doc.getElementsByTagName("BODY").item(0));
	    NodeList children = el.getChildNodes();
	    String res = "<BODY>";
	    Element childrenElement = null;
	    for (int i=0; i<children.getLength(); i++) {
	    	childrenElement = (Element) children.item(i);
	    	String childrenFormat = "<"+ children.item(i).getNodeName()+">" + getCharacterDataFromElement(childrenElement)+ "</"+ children.item(i).getNodeName()+">";
	    	//System.out.println("Request children " + i + " :"+ childrenFormat);
	    	res = res + childrenFormat;
	    }
	    res = res +  "</BODY>";
	    setBody(res);	  
	}*/
	
	
	
	protected String encodePostRequest(Post post){
		StringBuffer body = new StringBuffer();
		body.append("<POST>");
		body.append("<OWNER>"+post.getOwner()+"</OWNER>");
		body.append("<ID>"+post.getPostId()+"</ID>");
		body.append("<DATE>"+post.getDate()+"</DATE>");
		body.append("<CONTENT>"+post.getContent()+"</CONTENT>");
		body.append("</POST>");
		return body.toString();
	}
	
	public void buildAuthentificationRequest(Me moi){
		setKeyword("AUTHENTIFICATION");
		setBody("<ID>"+moi.getMeId()+"</ID>" +"<FULLNAME>" + moi.getFullName() +"</FULLNAME>");
	}
	
	public void buildFriendRequest(String messagePerso){
		setKeyword("FRIEND");
		setBody("<PRESENTATION>"+messagePerso+"</PRESENTATION>");
	}
	
	public void buildWhoIsRequest(){
		buildSimpleRequest("WHO");
	}
	
	public void buildSendPostRequest(Post post){
		setKeyword("SENDPOST");
		setBody(encodePostRequest(post));
	}
	
	public void buildSimpleRequest(String command){
		setKeyword(command);
	}
	
	public void setKeyword(String keyword2){
		keyword = keyword2;
	}
	
	public void setBody(String body2){
		body = body2;
	}
	
	public String getKeyword(){
		return keyword;
	}
	
	public String getBody(){
		return body;
	}
	
	public String format(){
		return "<REQUEST><KEYWORD>"+keyword + "</KEYWORD><BODY>"+body+"</BODY></REQUEST>";
	}
	
	public void decodeRequest(String string){
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(string));

	        Document doc = db.parse(is);
	        NodeList keyword = doc.getElementsByTagName("KEYWORD");
	        Element line = (Element) keyword.item(0);
	        setKeyword(getCharacterDataFromElement(line));
	        
	        NodeList requestBody = doc.getElementsByTagName("BODY");
	        line = (Element) requestBody.item(0);
	        setBody(getCharacterDataFromElement(line));	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	public static String getCharacterDataFromElement2(List<Element> e) {
		Iterator<Element> itr = e.iterator();
		String res = null;
		while(itr.hasNext()) {
			Element element = itr.next(); 
			Node child = element.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				res = cd.getData();
			}
			res = res + "?";
		}
		return res;
	}
}
