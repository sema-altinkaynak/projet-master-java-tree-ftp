package fil.SRA1.Tree.connexion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Classe permettant de faire des requêtes au serveur
 */
public class CommandConnexion extends Connexion{

    private OutputStream outputStream;
    private PrintWriter printer;
    
    /**
     * Constructeur de la classe qui prend en paramètre le port et l'adresse cible du serveur
     * @param String serveur
     * @param int typeConnexion
     */
    public CommandConnexion(String serveur, int typeConnexion) {
        super(serveur, typeConnexion);
    }

    /**
     * Démarre la connexion au port et au serveur et initialise le flux sortant
     */
    @Override
    public void initialiseConnexion() throws UnknownHostException, IOException{
        super.initialiseConnexion();
        outputStream = socket.getOutputStream();
        printer = new PrintWriter(outputStream,true);
    }
    
    /**
     * Renvoie les ordres recu au flux sortant de la connexion au serveur
     * @param String commande
     */
    public void writeCommand(String commande){
        printer.println(commande); 
    }
    
}
