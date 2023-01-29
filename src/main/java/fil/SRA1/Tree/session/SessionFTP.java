package fil.SRA1.Tree.session;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import fil.SRA1.Tree.connexion.*;

/**
 * Démarre une connexion avec un serveur FTP
 */
public class SessionFTP {

    private CommandConnexion commande;
    private String adresse;

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

    /**
     * Demande d'authentification au serveur
     * @throws IOException
     */
    public void initialiseSession() throws IOException,NullPointerException{
        this.commande.writeCommand("AUTH TLS");
        this.commande.getResponse();
        this.commande.writeCommand("USER anonymous");
        this.commande.getResponse();
        this.commande.writeCommand("PASS ");
        this.commande.getResponse();
        this.commande.writeCommand("PWD");
        this.commande.getResponse();
    }

    /**
     * calcule le numéro de port à partir d'une chaine de caractère
     * @param String port
     * @return int numéro de port 
     */
    private int calculeNumeroDePort(String port){
        String mots[]= port.split(",");
        String k = mots[mots.length-1];
        int ind =k.indexOf(")");
        String a = mots[mots.length-2];
        int b = Integer.parseInt(a);
        int c = Integer.parseInt(k.substring(0, ind));
        return b*256+c;
    }

    /**
     * Précise le mode de connexion et renvoie un numéro de port
     * @param mode
     * @return int 
     * @throws IOException
     */
    public int mode(String mode) throws IOException,ArrayIndexOutOfBoundsException{
        this.commande.writeCommand(mode);
        String port =this.commande.getResponse();
        if(port==null){
            return 0;
        }
        return calculeNumeroDePort(port);
    }

    /**
     * Créér et initialise une socket pour la récuperation des données envoyés par un serveur
     * @param port
     * @return DataConnexion
     * @throws UnknownHostException
     * @throws IOException
     */
    public DataConnexion initialiseSocketDestinationData(int port) throws UnknownHostException, IOException,ConnectException{
        DataConnexion connexion = new DataConnexion(this.adresse,port);
        connexion.initialiseConnexion();
        return connexion;
    }

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

    /**
     * declenche la commande permettant d'aller à un sous répertoire d'un repertoire
     * @param enfant
     * @return Bool
     */
    public boolean repertoireEnfant(String enfant) {
        this.commande.writeCommand("CWD "+enfant);
        try {
            this.commande.getResponse();
        } catch (IOException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Renvoie True si le fichier est un dossier et false sinon
     * @param fichier
     * @return Bool
     */
    public Boolean is_directionnary(String fichier){
        return fichier.substring(0, 1).equals("d");
    }

    /**
     * Renvoie True si un fichier a le droit d'execution
     * @param fichier
     * @return Bool
     */
    public Boolean hasRight(String fichier){
        return fichier.substring(9,10).equals("x");

    }

    /**
     * Ferme la socket de connexion principale
     * @throws IOException
     */
    public void finishSession() throws IOException{
        this.commande.finish();
    }
}
