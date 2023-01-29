package fil.SRA1.connexion;

import org.junit.Test;

import fil.SRA1.Tree.connexion.Connexion;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Before;

/**
 * Classe de test principale pour les connexions
 */
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
    public void testFinish() throws ConnectException, UnknownHostException, IOException {
        this.connexion.initialiseConnexion();
        this.connexion.getResponse();
        this.connexion.finish();
    }

    @Test
    public void testResponseError() throws ConnectException, UnknownHostException, IOException {
        this.connexion.initialiseConnexion();
        this.connexion.finish();
        assertThrows(SocketException.class,() -> {this.connexion.getResponse();} );
    }



    
}
