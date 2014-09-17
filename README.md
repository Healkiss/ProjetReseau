ProjetReseau
============

Reseau social distribué en Java.


				Protocole XML

Ce protocole tres simple définit des régles simple qui fournissent un support à la communication inter-client du projet de reseau.

Tout ce qui suit est ouvert à la discussion, et reste à completer.

Le format XML a été choisi car il est facilement lisible avec des bibliotheques native de java.
Il permet une validation rapide du bon formatage d’un flux.
De plus il permet de rajouter des balises personnalisées pour correspondre à ses besoins personnels

Chaque communication doit donc être une ligne XML valide. (balises ouvertes, toujours fermés)

Syntaxe generale :
Chaque flux (réponses, requetes ..) :
Est une chaine en String terminé par \r\n.
Une balise generale portant son nom : 
<REQUEST>,<RESPONSE>..
Deux sous-elements :
<KEYWORD>, <CODE> ..
le corps :
<BODY>


1 Structure de données à respecter

User
firstName
String
lastName
String
birthday
SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
friends
User[]
posts


Post[]
ip
String
port
int

l’en-tete	




Post

Name
Type
content
String
date
SimpleDateFormat("dd/MM/yyyy HH-mm-ss")


ex1 : requètes : <REQUEST><KEYWORD>mot-clé </KEYWORD><BODY>...</BODY> </REQUEST>

ex2 : réponses : <RESPONSE><CODE>code réponse </CODE ><BODY>...</BODY> </RESPONSE>

Pour presenter le protocole, trois parties importantes :

Les REQUETES
Pour les requêtes le mot clé est entouré par la balise <KEYWORD>
S’authentifier
Mot clé : AUTHENTIFICATION 
Corps : la balise <ID> à 0 pour le moment <FULLNAME> pour donner son nom (firstname + lastname)
(plus tard il y aura notion de clé publique/prive)
Exemple d’utilisation : <REQUEST><KEYWORD>AUTHENTIFICATION </KEYWORD><BODY><ID>0</ID><FULLNAME>Jean Dubois</FULLNAME></BODY> </REQUEST>

Demander des infos à un contact
Mot clé : WHO
Corps : aucun
Exemple d’utilisation : <REQUEST><KEYWORD>WHO </KEYWORD><BODY></BODY> </REQUEST>


Demande d’amitié
Mot clé : FRIEND 
Corps : <PRESENTATION> pour une presentation qui viendra completer ldemande d’ami.
Exemple d’utilisation : <REQUEST><KEYWORD>FRIEND </KEYWORD><BODY><FULLNAME>Jack Pote<FULLNAME><PRESENTATION>Coucou !</PRESENTATION><PORT>1234</1234></BODY> </REQUEST>

Demande de tous ses posts à un contact
Mot clé : LISTPOST
Corps : aucun
Exemple d’utilisation : <REQUEST><KEYWORD>LISPOST </KEYWORD><BODY></BODY> </REQUEST>

Envoyer un post
Mot clé : SENDPOST
Corps : La balise englobante <POST> incluant les balises :
<OWNER> l’id du proprietaire
 <ID> l’id du post
<DATE> la date de creation du post
<CONTENT> le contenu textuel du post (attention aux caracteres <, > qu’ils ne soient pas interpretes par le parseur)

Exemple d’utilisation : <REQUEST><KEYWORD>SENDPOST </KEYWORD><BODY><OWNER>2</OWNER><POST><ID>3</ID><DATE>09/11/2012 13:37:00</DATE><CONTENT></CONTENT></POST></BODY> </REQUEST>

Les REPONSES
Nous avons repris les codes de Jonathan DRUET et Jean Baptiste REY disponible sur : http://stackbase.fr.cr/bdx1
Pour les requêtes le mot clé est entouré par la balise <KEYWORD>
Réponse à WHO :
	Code : 201 → Les informations suivent à la ligne ( quand vous êtes l'ami de 
l'utilisateur distant )
Corps :<FULLNAME>explicite
		<BIRTHDAY>explicite
Exemple :
<RESPONSE><CODE>201</CODE><BODY><FULLNAME>Jean Dubois</FULLNAME><BIRTHDAY>08/02/1994</BIRTHDAY></BODY></RESPONSE>

Code : 211 → Seulement le champ fullname ( quand vous n'êtes pas l'ami de 
l'utilisateur distant ) 
Corps : <FULLNAME>explicite


Code : 401 → Accès interdit (implémentation facultative)
Corps : aucun








Réponse a FRIEND:

UseCode: 204 → La reponse à la demande d’amitié suis.
Corps :  La balise < ACCEPT> enveloppe la reponse YES ou NO, Les balises de presentation <FULLNAME> et du <PORT> d’ecoute doivent etre rempli en cas d’acceptation.
Exemple : <RESPONSE><CODE>204</CODE><BODY><ACCEPT>YES<ACCEPT><FULLNAME>Jack Pote<FULLNAME><PORT>1234<PORT></BODY></RESPONSE>


Réponse a LISTPOST :
Code:202 → Les informations (posts, dates de création et IDs) suivent à la ligne
Corps : composé de autant de<POST> (et de ses balises obligatoire)qu’il le faut
Exemple : <RESPONSE><CODE>202</CODE><BODY><POST>...</POST><POST>...</POST></BODY></RESPONSE>

Code:402 → Accès interdit ( quand vous n'êtes pas l'ami de l'utilisateur distant )

DIVERS

Il faut s’accorder sur un format de date, nous utilisons actuellement une forme à revoir :
Un DateFormat  de la forme SimpleDateFormat("dd/MM/yyyy HH-mm-ss");


TRACKER

S’identifier
Utilité : se declarer aupres d’un tracker
Mot clé : IDENTFICATION
Corps : la balise <FULLNAME> pour donner son nom  et <PORT> pour le port d’ecoute
(plus tard il y aura notion de clé publique/prive)
Exemple d’utilisation : <REQUEST><KEYWORD>AUTHENTIFICATION </KEYWORD><BODY><FULLNAME>Jean Dubois</FULLNAME><PORT>5656</PORT></BODY> </REQUEST>

Liste d’utilisateur
Utilité : demander la liste des clients au tracker
Mot clé : LISTUSERS
Corps : aucun
Exemple d’utilisation : <REQUEST><KEYWORD>LISTUSERS </KEYWORD><BODY></BODY> </REQUEST>
