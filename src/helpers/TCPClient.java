package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import Interfaces.Profil;

import objects.ContactServer;
import objects.Me;
import objects.Request;

//cette classes envoi des requetes aux serveur et recupere les reponses
public class TCPClient {

	protected static Set<ContactServer> distants;
	protected static Set<ContactServer> distantsConnectes;
	protected static Set<ContactServer> distantsNonConnectes;
	public int m_receiveBufferSize;
	public Charset m_charset;
	public CharsetEncoder m_encoder;
	public CharsetDecoder m_decoder;
	static int port ;
		
	public TCPClient() {
		//utiliser une table d'association id -> objet a la place
		distants = new HashSet<ContactServer>();
		distantsConnectes = new HashSet<ContactServer>();
		distantsNonConnectes = new HashSet<ContactServer>();
		m_receiveBufferSize = 1000;
		m_charset = Charset.forName("UTF-8");
		m_encoder = m_charset.newEncoder();
		m_decoder = m_charset.newDecoder();
	}

	//create a socket to commmunicate with the contact and add it to distants list
	public static void connect(ContactServer contact, Me moi) {
		try {
			//System.out.println("TCPClient connect() Try to connect (&AUTHENTIFICATION) to " + contact.getIPName() + ":"+contact.getContactPort());
			Socket socket = new Socket(contact.getIPName(), contact.getContactPort());
			contact.setContactSocket(socket);
			Request request = new Request();
			request.buildAuthentificationRequest(moi);
			if (!sendRequest(contact.getContactID(), request)){
				distantsNonConnectes.add(contact);
				distantsConnectes.remove(contact);
			}else{
				distantsConnectes.add(contact);
				distantsNonConnectes.remove(contact);
			}
			
		}catch (ConnectException e) {
			//System.out.println("Le client n'est pas connecte (" + e + ")");
			distantsNonConnectes.add(contact);
			distantsConnectes.remove(contact);
		}
		catch (IOException e) {
			System.out.println("Erreur dans TCPClient connect() " + e);
			e.printStackTrace();
		}

	}
	
	//send the request and wait for response, select the contact in distants list by comparing the id
	public static boolean sendRequest(int contactID, Request request) {
		try{
			Set<ContactServer> clientsSet = distantsConnectes;
			Iterator<ContactServer> it = clientsSet.iterator();
			ContactServer contact;
			String requestComplete = request.format();
				while (it.hasNext()) {
					contact = it.next();
					if (contact.getContactID() == contactID){
						OutputStream os = contact.getContactSocket().getOutputStream();
						PrintStream ps = new PrintStream(os, false, "utf-8");
						System.out.println("TCPClient send() request : " + requestComplete);
						System.out.println("TCPClient send() request length : " + requestComplete.length());
						ps.print(requestComplete);
						
						//essai de lire directement la reponse
						//mauvaise idée, si on me repond pas, ou pas correctement, je peux rester bloqué des heures ici.
						/*
						InputStream is = contact.getContactSocket().getInputStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
						String line = br.readLine();
						System.out.println("TCPClient send() response : " + line);
						*/
					}
				}
				return true;
		} catch (IOException e) {
			System.out.println("Erreur d'envoi" + e);
			e.printStackTrace();
			return false;
		}
	}

	// envoyer la command voulu a tous les contacts
	public void sendRequestToAll(Request request) {
		try {
			String requestComplete = request.format();
			Me me = Profil.getMe();
			System.out.println("TCPClient send() requestComplete : " + requestComplete);
			Set<ContactServer> clientsSet = this.distantsConnectes;
			Iterator<ContactServer> it = clientsSet.iterator();
			ContactServer contact;
			while (it.hasNext()) {
				contact = it.next();
				OutputStream os = contact.getContactSocket().getOutputStream();
				PrintStream ps = new PrintStream(os, false, "utf-8");
				System.out.println("TCPClient sendRequestToAll() requestComplete : " + requestComplete);
				System.out.println("TCPClient sendRequestToAll() requestComplete length : " + requestComplete.length());
				ps.print(requestComplete);
			}

		} catch (IOException e) {
			System.out.println("Erreur d'envoi" + e);
			e.printStackTrace();
		}
	}

	//connect to the friend list
	public void connectWhithFriends(Me moi) {
		Set<ContactServer> friends = CacheHelper.selectAllContacts();
		System.out.println("Try to connect to all friends");
		if (friends != null) {
			Iterator<ContactServer> it = friends.iterator();
			while (it.hasNext()) {
				ContactServer friend = it.next();
				System.out.println("TCPClient connectWhithFriends() Connexion à contact " + friend.getContactID());
				connect(friend,moi);
			}
		}
	}
	
	public void connectWhithNonConnecte(Me moi) {
		//System.out.println("Try to connect to non connectes");
		
		Set<ContactServer> friends = distantsNonConnectes;
		if (friends != null) {
			Iterator<ContactServer> it = friends.iterator();
			while (it.hasNext()) {
				ContactServer friend = it.next();
				connect(friend,moi);
				//System.out.println("TCPClient connectWhithNonConnecte() Connexion à contact " + friend.getContactID());
			}
		}
	}

	public static void addFriend(ContactServer mContact, Me moi) {
		// TODO Auto-generated method stub
		
	}

	public void addContact(ContactServer mContact) {
		distantsNonConnectes.add(mContact);
	}
	
	public Set<ContactServer> getDistantsConnectes(){
		return distantsConnectes;
	}
	
	public Set<ContactServer> getDistantsNonConnectes(){
		return distantsNonConnectes;
	}
}
