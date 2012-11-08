package objects;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;

public class Response {

	private int responseCode;
	private String responseBody;
	public Response(){
		responseCode = 0;
		responseBody = "";
	}
	
	protected Response decodeReponse(String string){
		Response response = new Response();
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(string));

	        Document doc = db.parse(is);
	        NodeList postDate = doc.getElementsByTagName("RESPONSECODE");
	        Element line = (Element) postDate.item(0);
	        response.setResponseCode(getCharacterDataFromElement(line));
	        
	        NodeList postContent = doc.getElementsByTagName("BODY");
	        line = (Element) postContent.item(0);
	        response.setResponseBody(getCharacterDataFromElement(line));	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		return response;
	}
	
	
	protected Post decodePostReponse(String response){
		Post post = new Post();
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(response));

	        Document doc = db.parse(is);
	        NodeList postDate = doc.getElementsByTagName("DATE");
	        Element line = (Element) postDate.item(0);
	        post.setDate(getCharacterDataFromElement(line));
	        
	        NodeList postContent = doc.getElementsByTagName("CONTENT");
	        line = (Element) postContent.item(0);
	        post.setContent(getCharacterDataFromElement(line));	        
	        
	        /*
	        // iterate the employees
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element element = (Element) nodes.item(i);

	           NodeList name = element.getElementsByTagName("name");
	           Element line = (Element) name.item(0);
	           System.out.println("Name: " + getCharacterDataFromElement(line));

	           NodeList title = element.getElementsByTagName("title");
	           line = (Element) title.item(0);
	           System.out.println("Title: " + getCharacterDataFromElement(line));
	        }*/
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		
		return post;
	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	 
	protected void setResponseCode(String code){
		responseCode = Integer.parseInt(code);
	}
		
	protected void setResponseCode(int code){
		responseCode = code;
	}
	
	protected void setResponseBody(String body){
		responseBody = body;
	}
	
	protected int getResponseCode(){
		return responseCode;
	}
	
	protected String getResponseBody(){
		return responseBody;
	}
}
