# Mise en place de l'application tree FTP
Sema Altinkaynak
24/01/2022

## Introduction

L'objectif de ce projet était de mettre en place une commande permettant d'afficher l'arborescence d'un répertoire distant via le protocole FTP.
Cette commande fait appel à une classe. 

La classe principale permettant de gèrer cette commande la classe Tree. Elle contient un algorithme permettant de communiquer avec le répertoire distant. Elle permet donc d'afficher le contenu du répertoire distant. 


## Architecture

### Polymorphisme et classe abstraite


Le projet ne possède pas de design pattern précis. Il a été difficile de savoir quel design pattern utilisé. Le design pattern m'étant venu à l'esprit était le **singleton**. Seulement, je ne peux dire si il est réellement mis en place de facon correcte. Nous verrons cela dans les paragraphes à venir. 

Le projet contient un paquetage connexion contenant : 
<ul>
<li> une classe abstraite Connexion</li>
<li> une classe CommandConnexion héritant de la classe connexion</li>
<li> une classe DataConnexion héritant de la classe connexion</li>

La classe abstraite contient toutes les méthodes qui permettent une connexion au serveur par le biais d'une socket. 
Elle contient donc en grande partie la fonctionnalité de reception des données et des messages, la fermeture de socket. 

Chaque classe fille utilise la plus part des méthode implémentées par la classe Connexion. Il y a seulement une seule méthode qui différencie réellement les 2 classes filles. Nous ne pouvons écrire des commandes avec la classe DataConnexion. Ainsi, ce paquetage contient la méthode polymorphique **initialiseConnexion()**. 
La méthode suivante est contenu dans la classe Connexion : 
```java
        /**
     * Initilalise la connexion au port et au serveur
     * @throws UnknownHostException
     * @throws IOException
     * @throws ConnectException
     */
    public void initialiseConnexion() throws UnknownHostException, IOException ,ConnectException  {
        this.socket = new Socket(adresse, port);
        inputStream = socket.getInputStream();
        buffer = new BufferedReader(new InputStreamReader(inputStream)) ;
    }
````
La méthode suivant est contenu dans la classe CommandConnexion :

 ```java
    /**
     * Démarre la connexion au port et au serveur et initialise le flux sortant
     */
    @Override
    public void initialiseConnexion() throws UnknownHostException, IOException{
        super.initialiseConnexion();
        outputStream = socket.getOutputStream();
        printer = new PrintWriter(outputStream,true);
    }
````

La particularité de la classe CommandConnexion est qu'elle n'est limité qu'a une seule instance, c'est pour cela que je faisais réference au design pattern singleton. De plus, cette contient la fonctionnalité de pouvoir envoyé des messages au serveur ftp. 

De plus, l'héritage est présent dans les classes de tests du paquetage connexion. 
Le paquetage contient une classe abstraite connexionTest permettant d'être utilisé par les classes filles. 
Les classes commandConnexionTest et DataConnexionTest heritent de la classe abstraite. 
Les classes filles contiennent donc la méthode suivante :

````
 @Override
    protected Connexion myConnexion() {
        return new CommandConnexion("ftp.ubuntu.com", 21);
    }
````
Cela permet de renvoyé une instance de la classe fille et passer les tests.

Nous avons aussi une classe SessionFTP et une classe Tree dont nous parlerons dans les parties à venir. 

### Gestion d'erreur

La plus part des méthodes contiennent soit un try/catch soit un throw. Cela est dû à la gestion des sockets qui peuvent renvoyés des erreurs à tout moment. Voici quelques méthodes de la classe abstraite Connexion :

```java
/**
     * Renvoie les réponses obtenus par le serveur
     * @return String 
     * @throws IOException
     */
    public String getResponse() throws IOException, NullPointerException,SocketException{
        String message = buffer.readLine(); 
        return message;
    }

    /**
     * Termine la connexion au serveur
     * @throws IOException
     */
    public void finish() throws IOException {
        socket.close();
    }
`````

La méthode getResponse contient un throw pour la gestion d'erreur des socketException et NullPointerException, cela est dû au faite que si une socket est appelée sans être initialisé alors elle renvoie une exception. 

```java
/**
     * Initilalise la connexion au port et au serveur
     * @throws UnknownHostException
     * @throws IOException
     * @throws ConnectException
     */
    public void initialiseConnexion() throws UnknownHostException, IOException ,ConnectException  {
        this.socket = new Socket(adresse, port);
        inputStream = socket.getInputStream();
        buffer = new BufferedReader(new InputStreamReader(inputStream)) ;
    }
`````
La méthode contient une détection d'erreur au niveau de la connexion car une socket ne peut pas se connecter à n'importe qu'elle port du serveur. 


La gestion d'erreur a été principalement faite dans la classe SessionFTP. Voici quelques exemples :

```java
/**
     * Envoi la commande d'affichage au serveur et intercepte la réponse. Si la réponse est nulle alors il y a une deconnexion du serveur
     * @return
     */
    public Boolean afficherListRepertoire() {
        this.commande.writeCommand("LIST");
        try {
            this.commande.getResponse();
            if(this.commande.getResponse()==null){
                return false;
            }
        } catch (IOException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * retourne au répertoire parent du répertoire actuel
     * @return Bool
     */
    public Boolean repertoireParent()  {
        this.commande.writeCommand("CDUP");
        try {
            this.commande.getResponse();
        } catch (IOException e) {
            return false;
        }catch (NullPointerException e) {
            return false;
        }
        return true;
        
    }
`````
La méthode **afficherListRepertoire()** renvoie false dans le cas des exceptions car la connexion au port 21 peut s'arreter à tout moment et donc une exception est renvoyés dans ce cas. Ainsi, retourner un booléen permet de faire part à des methodes dépendantes de cette classe que la connexion s'est mal passés. 

La méthode **repertoireParent()** est du même genre que la prècedante et permet donc de gèrer le cas d'un NullPointerException lors de la deconnexion de la socket. 


```java
/**
     * Constructeur de la classe qui prend en paramètre l'adresse cible du serveur
     * @param String adresse
     * @throws ConnectException
     * @throws UnknownHostException
     * @throws IOException
     */
    public SessionFTP(String adresse) throws ConnectException, UnknownHostException, IOException {
        this.commande = new CommandConnexion(adresse, 21);
        this.adresse= adresse;
        this.commande.initialiseConnexion();
        this.commande.getResponse();
    }
````

La méthode ci-dessous permet de gèrer le cas où le nom du serveur est mal écrit ou bien qu'il existe tout simplement pas. C'est pourquoi il y a la détection d'un UnknownHostException.

Globalement toutes les méthodes de chaque classe gère des exceptions en lien avec la deconnexion et reconnexion des sockets . 


## Code Samples

### Algorithme 

Voici l'algorithme principale du logiciel : 
```java

    private boolean parcoursTree(String name,String parent, String espace) throws IOException{
        if(!(session.is_directionnary(name))){
            // Si le dossier est un simple fichier alors on affiche son nom
            System.out.println(espace+"|— "+name.substring(56));
            return true;
        }
        else{
            int portDestination = session.mode("PASV");
            {...}
            boolean check = session.afficherListRepertoire();
            // Si la socket de commande est deconnecté alors un Booléen False est renvoyé et le parcours n'est pas executé 
            if(check ){
                String fichier ;
                // Si nous avons une réponse et que nous avons les droits alors nous parcourons les sous dossiers du dossier
                while((fichier = connexion.getResponse()) != null){
                    if(session.is_directionnary(fichier) && session.hasRight(fichier)){
                        {...}
                        boolean val =parcoursTree(fichier,parent+"/"+name.substring(56),espace+"|   ");
                        // Si il y a eu un problème de connexion avec la session alors on la réinitialise et on continue notre parcours
                        if(val==false){
                            this.session = new SessionFTP(this.adresse);
                            this.session.initialiseSession();
                            this.session.repertoireEnfant("/"+parent+"/"+name.substring(56));
                        }
                    }
                    else{
                        parcoursTree(fichier, parent, espace );
                    }
                }     
                // Renvoie d'un booléen False pour la gestion des erreurs et la reprise de session   
        {...}      
        }
        
        return true;
    }
```

C'est une méthode récursive permettant de génèrer toute l'arborescence d'un répertoire distant. Elle renvoi un booléen permettant de gèrer les cas de deconnexion et d'erreur. Elle fait appel à la classe SessionFTP et lui dèlegue certaines tâches.

Voici la méthode permettant de lancé la récuperation des fichiers : 
```java
/**
        * Initialise le parcours du serveur et l'affiche
        * @throws IOException
        */
        public void afficheTree() throws IOException{
            // Initialisation du type d'échange
            int port = this.session.mode("PASV");
            // Récuperation du numéro de port
            DataConnexion data = session.initialiseSocketDestinationData(port);
            // Affiche la liste des répertoires et on les récuperes dans la socket crée précedemment
            this.session.afficherListRepertoire();
            String fichier = data.getResponse();
            while (fichier != null){

                System.out.println("|—-"+fichier.substring(56));
                // Si nous avons les droits requises alors nous parcours les sous dossiers de chaque fichier
                if(this.session.hasRight(fichier)){
                    this.session.repertoireEnfant("/"+fichier.substring(56));
                    parcoursTree(fichier, "", "|   ");   
                }
                fichier = data.getResponse();
            }
            // Termine la connexion à la socket et la session
            data.finish();
            session.finishSession();
        }
```
Elle rappel n fois la fonction parcoursTree afin d'afficher les dossiers. Diviser cette fonctionnalité en 2 méthodes a été un choix personnelle. Cela permet de répartir les taches entre classe afin de mieux gèrer tous les possibles pouvant être rencontrés. 

Voici des méthodes permettant de tester les exceptions lors de la création d'une socket à un mauvais port ou une mauvaise adresse :

```java 
    @Test
    public void testConnexionAdresseFail() {
        DataConnexion data = new DataConnexion("ftp.ubuntu;com", 21);
        assertThrows(UnknownHostException.class, () -> {data.initialiseConnexion();});
    }

    @Test 
    public void testConnexionPortFail(){
        DataConnexion data = new DataConnexion("ftp.ubuntu.com", 23);
        assertThrows(ConnectException.class, () -> {data.initialiseConnexion();});
    }
```
Ces méthodes permettent de vérifier la gestion des erreurs. 

Une liste classe impliquées dans un design pattern serait :
<ul>
<li> SessionFTP </li>
<li> CommandConnexion </li>
<li> DataConnexion </li>

Le design pattern en question est le singleton comme cité précedemment. Un objet SessionFTP aurait une seule instance de CommandConnexion et plusieurs instance de DataConnexion. Cela respecte en grande partie le design pattern singleton. 

Un exemple d'optimisation serait la classe ConnexionTest. Voici son contenu ci-dessous: 
```java

public abstract class ConnexionTest {
    protected Connexion connexion;
    protected  abstract Connexion myConnexion();

    @Before
    public void createConnexion(){
        this.connexion = this.myConnexion();
    }

    @Test
    public void testInitialiseConnexionOk() throws ConnectException, UnknownHostException, IOException {
        this.connexion.initialiseConnexion();
        assertEquals("220 FTP server (vsftpd)", this.connexion.getResponse());
    }


    @Test
    public void testResponseError() throws ConnectException, UnknownHostException, IOException {
        this.connexion.initialiseConnexion();
        this.connexion.finish();
        assertThrows(SocketException.class,() -> {this.connexion.getResponse();} );
    }
```
Elle permet de factoriser du code au niveau des classes de tests et rend plus simple la gestion des tests. Ainsi chaque classe fille implémente la méthode **myConnexion()**
pour pouvoir passer les tests. 


## Conclusion

Globalement le projet est en partie fait.
Il est fonctionnel.

Il faudrait optimiser d'avantage les tests car, même si elle recouvre une bonne partie du code, elles ont besoin d'internet pour fonctionner. 
 
