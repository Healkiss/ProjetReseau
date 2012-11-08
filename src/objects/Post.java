package objects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;



public class Post implements Serializable {

		private int id;
		private int ownerId;
		private String owner;
		private String content;
		private String postDate;
		private Date date;
		
		
		public Post(){

		}
		
		public Post(String owner2, String content2, GregorianCalendar GCdate){			 
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH-mm-ss");
			Date dateDate = GCdate.getTime();
			postDate = dateFormat.format(dateDate);
			owner = owner2;
			content = content2;
		}
		
		public String getDate(){
			return postDate;
		}
		
		public void setDate(String date){
			this.postDate = date;
		}
		
		public void setDate(GregorianCalendar GCdate){
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH-mm-ss");
			Date dateDate = GCdate.getTime();
			this.postDate = dateFormat.format(dateDate);
		}
		
		public String getContent(){
			return content;
		}
		
		public void setContent(String content){
			this.content = content;
		}
		
		public int getPostId(){
			return id;
		}
		
		public void setOwner(String string){
			owner = string;
		}
		public String getOwner(){
			return owner;
		}
		
		public void setOwnerId(int id){
			ownerId = id;
		}
		
		public int getOwnerId(){
			return ownerId;
		}
}
