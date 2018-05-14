# Blockchain-simulator
Project: Distributed Systems' course

Link: https://github.com/GovinV/Blockchain-simulator

## 		Use
    To Run the Application: 
    	./destroy
    	./clean
		./compile
		./serveur [num-port] (as many as you want)
		java Client localhost [num-port] (4 max peer server)


##		Rapport

###		Implémentation:

J'ai utilisé pour ce projet Java et la librairie de programmation distribuée RMI.
RMI étant très simple d'utilisation, j'ai pu implémenté les fonctionnalités 
qui me semblait très importante.

###		Choix:
		
-Je n'ai pas crée de "Noeud Participant" comme spécifié dans le sujet, 
ici c'est ma classe client qui fait tous les différents tests, 
comme on peut le voir dans Jeu_Test/.


-Mon algorithme de consensus pour avoir la meilleure Blockchain fontionne 
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