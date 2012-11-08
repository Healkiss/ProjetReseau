package Interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import  java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import objects.ContactServer;
import objects.Me;
import objects.Post;
import objects.Request;
import helpers.CacheHelper;
import helpers.TCPClient;
import helpers.TCPServer;

import  java.io.Serializable;

public class Profil extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JPanel panel, myPosts, theirPosts, contacts, contact, profilPanel;
	static JTextField postText;
	static JTextField contactNameText;
	static JTextField contactIPText;
	static JTextField  contactPortText;
	static JTextField nameText;
	private JPanel contentPane;
	private static int port = 7750;
	private static String nameFile = "mesPosts.txt";
	private static TCPServer mTCPServer;
	private static TCPClient mTCPClient;
	public static Me moi;
	public static int lastIDContact ;
	public static FriendList friendlist ;
	
	//initalise notre profil avec nos infos, no anciens posts et contacts
	public static void initializeProfil() {
		
		JLabel nameLabel = new JLabel("Mon profil : " + moi.getFirstName() + " " + moi.getLastName());
		profilPanel.add(nameLabel);
		JLabel portLabel = new JLabel("Port : " + moi.getMePort());
		profilPanel.add(portLabel);	
		Set<Post> myPostsCache = CacheHelper.selectAllMyPosts();
		if(myPostsCache != null){
			Iterator<Post> it = myPostsCache.iterator();
			while(it.hasNext())
			{
				Post post = it.next();
				JLabel label = new JLabel("old status " + post.getContent());
				myPosts.add(label);
			}
		}
		
		Set<ContactServer> mesContacts = CacheHelper.selectAllContacts();
		friendlist = new FriendList(mTCPClient, moi,panel, contacts, mesContacts);
		friendlist.start();
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Profil() throws IOException {
		moi = CacheHelper.selectMe();
		//si premiere connexion il faut qu'il parametre son compte
		if(moi == null){
			moi = new Me();
			Hashtable<String,String> res = showInscriptionDialog();
			moi.setFirstName(res.get("prenom"));
			moi.setLastName(res.get("nom"));
			moi.setPort(Integer.parseInt(res.get("port")));
			CacheHelper.insertMe(moi);
		}
		
		//on demarre le serveur
		mTCPServer = new TCPServer(moi.getMePort());
		mTCPServer.start();
		
		//on demarre la machine cliente
		mTCPClient = new TCPClient();
		mTCPClient.connectWhithFriends(moi);
		
		
		JLabel label;
		panel = new JPanel();
		getContentPane().add(panel);
		/* Afficher d'abord la zone de post, puis les gens */
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		profilPanel = new JPanel();
		profilPanel.setLayout(new BoxLayout(profilPanel, BoxLayout.Y_AXIS));
		panel.add(profilPanel);
		
		/* Une zone de texte et un bouton pour poster */
		JLabel postLabel = new JLabel("A partager");
		panel.add(postLabel);
		postText = new JTextField(30);
		postText.setMaximumSize(new Dimension(Integer.MAX_VALUE, postText.getMinimumSize().height));
		panel.add(postText);
		JButton postButton = new JButton("Post");
		postButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				/* Ajoute le statut */
				JLabel label = new JLabel(postText.getText());
				//try to send new post at other process, in this case localhost
				Post monPost = new Post(moi.getFullName(), postText.getText(), new GregorianCalendar());
				postText.setText("");
				String target = contactIPText.getText();
				Request request = new Request();
				request.buildSendPostRequest(monPost);
				mTCPClient.sendRequestToAll(request);
				//on insere le post dans le fichiers mesposts pour les sauvegarder
				CacheHelper.insertMyPost(monPost);
				
				myPosts.add(label);
				/* Et redessine */
				panel.validate();
				
			}
		});

		postButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(postButton);
		
		JLabel targetLabel1 = new JLabel("Ajouter un contact");
		panel.add(targetLabel1);
		JLabel targetLabel3 = new JLabel("Adresse IP");
		panel.add(targetLabel3);
		contactIPText = new JTextField(30);
		contactIPText.setMaximumSize(new Dimension(Integer.MAX_VALUE, contactIPText.getMinimumSize().height));
		panel.add(contactIPText);
		JLabel targetLabel4 = new JLabel("Port");
		panel.add(targetLabel4);
		contactPortText = new JTextField(30);
		contactPortText.setMaximumSize(new Dimension(Integer.MAX_VALUE, contactPortText.getMinimumSize().height));
		panel.add(contactPortText);
		JButton addContactButton = new JButton("Ajouter");
		
		addContactButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String monContactIP = contactIPText.getText();
				int monContactPort = Integer.parseInt(contactPortText.getText());
				ContactServer mContact = new ContactServer(monContactIP, monContactPort);
				lastIDContact = moi.getLastIDContact();
				
				mContact.setContactID(lastIDContact+1);
				mContact.setContactPort(monContactPort);
				moi.incrementLastIdContact();
				//on insere le post dans le fichiers mescontacts pour les sauvegarder
				CacheHelper.insertContact(mContact);
				moi.addFriend(mContact);
				System.out.println("ajout d'un contact : " + monContactIP + ":"+monContactPort);
				/* Ajoute le contact */
				Request request = new Request();
				request.buildFriendRequest("Veux tu etre mon ami ? TU vois bien qui je suis !");
				TCPClient.sendRequest(mContact.getContactID(), request);
				TCPClient.connect(mContact, moi);
				mTCPClient.addContact(mContact);
				/* Et redessine */
				contactIPText.setText("");
				panel.validate();
				
			}
		});
		addContactButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(addContactButton);

		/* Un panel horizontal pour les gens */
		JPanel people = new JPanel();
		panel.add(people);
		people.setAlignmentX(Component.LEFT_ALIGNMENT);
		/* Les personnes sont affichees de gauche a droite */
		people.setLayout(new BoxLayout(people, BoxLayout.X_AXIS));

		/* Moi */
		myPosts = new JPanel();
		myPosts.setBorder(new LineBorder(Color.black));
		/* Mes commentaires sont affiches de haut en bas */
		myPosts.setLayout(new BoxLayout(myPosts, BoxLayout.Y_AXIS));
		myPosts.setAlignmentY(Component.TOP_ALIGNMENT);
		people.add(myPosts);

		label = new JLabel("vos statuts");
		myPosts.add(label);

		/* Une petite separation entre moi et lui */
		people.add(Box.createRigidArea(new Dimension(5,0)));

		/* Un ami */
		theirPosts = new JPanel();
		theirPosts.setBorder(new LineBorder(Color.black));
		theirPosts.setLayout(new BoxLayout(theirPosts, BoxLayout.Y_AXIS));
		theirPosts.setAlignmentY(Component.TOP_ALIGNMENT);
		people.add(theirPosts);

		label = new JLabel("Statuts de vos contacts : ");
		theirPosts.add(label);

		/* De la place pour les autres */
		people.add(Box.createHorizontalGlue());

		/* Un panel horizontal pour les contacts */
		contacts = new JPanel();
		contacts.setLayout(new BoxLayout(contacts, BoxLayout.Y_AXIS));
		panel.add(contacts);

		/* Mes contacts sont affichEs de haut en bas */
		contact = new JPanel();
		contact.setLayout(new BoxLayout(contact, BoxLayout.Y_AXIS));
		contact.setAlignmentY(Component.TOP_ALIGNMENT);
		contacts.add(contact);
		JLabel labelContact = new JLabel("contacts :");
		contacts.add(labelContact);
		
		/* Le reste de l'interface */
		setTitle("Social");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
	public static void displayNewPost(String line){
		JLabel label = new JLabel(line);
		theirPosts.add(label);
		panel.validate();
	}
	
	public static Me getMe(){
		return moi;
	}
	
	public static Hashtable<String,String> showInscriptionDialog(){
		//construction du panel d'inscription
	    JTextField textNom = new JTextField(20);
	    JTextField textPrenom = new JTextField(20);
	    JTextField textPort = new JTextField(4);
	    
	    JPanel myPanel = new JPanel();
	    myPanel.add(new JLabel("Prenom:"));
	    myPanel.add(textPrenom);
	    myPanel.add(Box.createHorizontalStrut(15));
	    myPanel.add(new JLabel("Nom :"));
	    myPanel.add(textNom);	      
	    myPanel.add(new JLabel("Port :"));
	    myPanel.add(textPort);	
	    
	    Hashtable<String,String> res = new Hashtable<String,String>();
		int result = JOptionPane.showConfirmDialog(null, myPanel, "Ceci est votre premiere connexion, veuillez completer ces quelques informations \r\n", JOptionPane.OK_CANCEL_OPTION);
		String prenom = textPrenom.getText();
		String nom = textNom.getText();
		String port = textPort.getText();
		if (result == JOptionPane.OK_OPTION){
			res.put("prenom",prenom);
			res.put("nom",nom);
			res.put("port",port);
		}
		else{
			JOptionPane.showMessageDialog(null, "Vous ne pouvez passez cette etape.","Inscription", 1);
			showInscriptionDialog();
		}
		return res;
	}

	public static void acceptNewFriend(ContactServer contact2) {
		Object[] options = {"accepter","Refuser !"};
		int n = JOptionPane.showOptionDialog(null, contact2.getName() + " souhaite devenir votre ami, que souhaitez vous faire ?"+ contact2.getIPName() + " " + contact2.getContactPort(),
		"Demande d'ami !",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,     //do not use a custom Icon
		options,  //the titles of buttons
		options[0]); //default button title
		
	}
}
