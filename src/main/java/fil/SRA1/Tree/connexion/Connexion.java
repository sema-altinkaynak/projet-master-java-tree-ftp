package fil.SRA1.Tree.connexion;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * classe principale pour l'utilisation des sockets
 */
public abstract class Connexion {

    protected String adresse;
    protected int port;
    protected Socket socket;
    protected InputStream inputStream;
    protected BufferedReader buffer;

    /**
     * Constructeur de la classe qui prend en paramètre le nom du serveur et le port cible
     * @param serveur
     * @param typeConnexion
     */
    public Connexion(String serveur, int typeConnexion) {
        this.adresse = serveur;
        this.port = typeConnexion;
    }
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
}
