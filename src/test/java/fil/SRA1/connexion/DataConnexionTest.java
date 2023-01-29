package fil.SRA1.connexion;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.junit.Test;

import fil.SRA1.Tree.connexion.Connexion;
import fil.SRA1.Tree.connexion.DataConnexion;

/**
 * Classe testant la classe DataConnexion
 */
public class DataConnexionTest extends ConnexionTest{

    @Override
    protected Connexion myConnexion() {
        return new DataConnexion("ftp.ubuntu.com", 21);
    }
    
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
}
