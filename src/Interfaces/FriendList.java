package Interfaces;

import helpers.TCPClient;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import objects.ContactServer;
import objects.Me;
import objects.Request;

public class FriendList extends Thread{
	
	JPanel panel, contacts,contactsEnligne,contactsHorsLigne;
	TCPClient mTCPClient; 
	Me moi;
	
	public FriendList(TCPClient mTCPClient, Me moi,JPanel panel, JPanel contacts, Set<ContactServer> mesContacts){
		this.mTCPClient = mTCPClient;
		this.moi = moi;
		this.contacts = contacts;
		this.panel =panel;
		createContactsList();
	}
	
	public void run(){
		while(true){
			synchronized(mTCPClient){
				mTCPClient.connectWhithNonConnecte(moi);
			}
			synchronized(this){
				displayContacts();
				try {
					sleep(100);
				} catch (InterruptedException e) {
					System.out.println("FriendList run() error : " + e);
					e.printStackTrace();
				}catch(IllegalMonitorStateException e){
					System.out.println("FriendList run() error : " + e);
					e.printStackTrace();
				}
			}
			//System.out.println("Fin boucle FriendList");
		}
		
	}
	
	private void displayContacts() {
		contactsEnligne.removeAll();
		contactsHorsLigne.removeAll();
		Set<ContactServer> mesContactEnLigne = mTCPClient.getDistantsConnectes();
		Iterator<ContactServer> it = mesContactEnLigne.iterator();
		while(it.hasNext())
		{
			final ContactServer contact = it.next();
			insertNewContactEnLigne(contact);
		}
		
		Set<ContactServer> mesContactHorsLigne = mTCPClient.getDistantsNonConnectes();
		it = mesContactHorsLigne.iterator();
		while(it.hasNext())
		{
			final ContactServer contact = it.next();
			insertContactHorsLigne(contact);
		}
		//System.out.println("Fin displayContacts FriendList");
	}

	public void createContactsList(){
		JLabel label = new JLabel("Contacts en ligne");
		contacts.add(label);
		
		contactsEnligne = new JPanel();
		contactsEnligne.setLayout(new BoxLayout(contactsEnligne, BoxLayout.Y_AXIS));
		contactsEnligne.setAlignmentY(Component.TOP_ALIGNMENT);
		contacts.add(contactsEnligne);

		
		label = new JLabel("Contacts hors ligne");	
		contacts.add(label);
		
		contactsHorsLigne = new JPanel();
		contactsHorsLigne.setLayout(new BoxLayout(contactsHorsLigne, BoxLayout.Y_AXIS));
		contactsHorsLigne.setAlignmentY(Component.TOP_ALIGNMENT);
		contacts.add(contactsHorsLigne);
	}
	
	public void insertNewContactEnLigne(final ContactServer contact){
		//System.out.println("FriendList insertNewContactEnLigne()");
		
		JLabel label = new JLabel(contact.getName() +" " + contact.getIPName() + ":"+ contact.getContactPort());
		contactsEnligne.add(label);
		
		JButton addContactButtonWho = new JButton("WHO");
		addContactButtonWho.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//au lieu de former la requete ici, il faudra une interface entre l'interface et le client
				Request request = new Request();
				request.buildWhoIsRequest();
				TCPClient.sendRequest(contact.getContactID(), request);
			}
		});
		addContactButtonWho.setAlignmentX(Component.LEFT_ALIGNMENT);
		contactsEnligne.add(addContactButtonWho);
		contactsEnligne.revalidate();
		contactsEnligne.repaint();
	}
	
	public void insertContactHorsLigne(final ContactServer contact){
		//System.out.println("FriendList insertContactHorsLigne()");
		
		JLabel label = new JLabel(contact.getName() +" " + contact.getIPName() + ":"+ contact.getContactPort());
		contactsHorsLigne.add(label);
		contactsHorsLigne.revalidate();
		contactsHorsLigne.repaint();
	}
}
