package fil.SRA1.Tree.connexion;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Classe permettant de recevoir le flux de données
 */
public class DataConnexion extends Connexion{

    /**
     * Constructeur de la classe qui prend en paramètre le nom du serveur et le port cible
     * @param serveur
     * @param typeConnexion
     */
    public DataConnexion(String serveur, int typeConnexion) {
        super(serveur, typeConnexion);
    }

     /**
     * Initilalise la connexion au port et au serveur
     * @throws UnknownHostException
     * @throws IOException
     * @throws ConnectException
     */
    @Override
    public void initialiseConnexion() throws UnknownHostException, IOException {
        super.initialiseConnexion();
        
    }


}
