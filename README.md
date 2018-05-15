# Blockchain-simulator
Project: Distributed Systems' course

Link: https://github.com/GovinV/Blockchain-simulator

## 		Use
    To Run the Application: 
    	./destroy
    	./clean
		./compile
		./serveur [num-port] (as many as you want)
		java Client localhost [num-port] (4 max peer server) (Client,ClientDump,ClientSignProblem)

	To add server:
		./serveur [num_port] [peer_port] [peer_port] ...

	To get the status of a server's BlockChain:
		java -classpath Jeu_Test ClientDump localhost [num_port]


##		Rapport

###		Implémentation:

J'ai utilisé pour ce projet Java et la librairie de programmation distribuée RMI.
RMI étant très simple d'utilisation, j'ai pu implémenté les fonctionnalités 
qui me semblait très importante.

###		Block:
	
Hormis, le bloc initial qui à besoin au minimum de 2 instructions pour être crée, 
un block est crée dès que le dernier block de la chaîne est validé et donc miné.
Je ne crée pas de block vide par soucis d'affichage, mais ce n'est qu'une condition
qui est rapidement modifiable.
La difficulté pour crée un block(donc le nombre de 0 du hash) augementent
en fonction de la taille de chaîne, tous les 5 blocks la difficulté augmente
de 1.

###		BlockChain:

Chaque serveur a sa propre chaîne de block et un nombre limité de participant(ici 4), 
ces participants lui sont liés mais il a aussi la liste des participants des autres serveurs.

Les clients peuvent ainsi s'incrire à un serveur, lui transmettre des transactions signées,
demander la liste de tout les clients, demander un travail à accomplir et vérifier son travail.
Il peut aussi avoir la blockchain d'un serveur.

###		Clients

Je n'ai pas crée de "Noeud Participant" comme spécifié dans le sujet, 
ici c'est ma classe client qui fait tous les différents tests, 
comme on peut le voir dans Jeu_Test/.
Chaque client a un mérite qui augmente selon le nombre d'instructions envoyées et de travail
fourni. Un montant qui augmente avec la création des blocks.
Chaque client génére une clé publique et privée qu'il utilise pour signer ses
transactions. Il envoie sa clé publique ainsi que sa clé public en String au serveur,
ce String sera l'identifiant de ce participant. Chaque participant à de base 10 de montant, 
qu'il pourra augmenter.

###		Consensus d'échanges de Block

Mon algorithme de consensus pour avoir la meilleure Blockchain fonctionne 
de la manière suivante:

Premièrement, toutes mes transactions sont transmises à tous 
les serveurs voisins du serveurs, 
si un serveur reçoit une transaction qui possède déjà, 
il ne la prend pas en compte et ne la broadcast pas.
On évite ainsi les boucles et les messages dupliqués.

Deuxièmement, tous les blocs créent sont transmis, au autre serveur, 
si le bloc reçu provient d'une blockchain plus courte que celle du serveur,
on ne la prend pas en compte,
si elle provient du chaîne plus longue d'une profondeur, 
on la prend et on met à jour les instructions en attente.
Si elle provient d'une chaine de profondeur égale à la profondeur du serveur + 2, 
on prend la chaîne d'où provient ce bloc.

###		Sécurité

Comme vu, plus tôt j'utilise les clés publiques et privées ainsi que les signatures,
les hashs des blocks sont générés en fonctions des informations du blocks,
le hash du blocks précédements et du nonce trouvé lors du minage.
Les algorithmes de chiffrements utilisés sont SHA-256 et RSA.


###		Amélioration

Dans mon algorithme de consensus, je récupère à chaque fois toute la chaîne 
d'un autre serveur, alors qu'on pourrait chercher le dernier previousHash égale,
et récuperer les blocks suivants.


### 	Jeu Test

Mon client est en fait un noeud participant mais aussi une interface de test, on peut modifié 
le client en fonction des tests qu'on veut faire.
J'ai deux exemples:
-ClientDump localhost [num_port] => avoir la blockchain du serveur [num_port] dans le terminal
et dans un fichier "blockchain_state_[num_port]_[timestamp].dump"
On peut l'afficher avec echo -e.

-ClientSignProblem localhost [num_port] => ici on signe nos transactions avec une clé différente
on peut voir que la transaction n'est pas accepté. Ce test est un mock pour tester la signature.



