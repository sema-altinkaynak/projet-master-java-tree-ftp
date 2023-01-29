package fil.SRA1.connexion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import java.net.UnknownHostException;

import org.junit.Test;

import fil.SRA1.Tree.connexion.CommandConnexion;
import fil.SRA1.Tree.connexion.Connexion;

/**
 * Classe testant la classe CommandConnexion
 */
public class CommandConnexionTest extends ConnexionTest{

    @Override
    protected Connexion myConnexion() {
        return new CommandConnexion("ftp.ubuntu.com", 21);
    }

    @Test
    public void testWriteCommandOK() throws UnknownHostException, IOException {
        CommandConnexion commandConnexion = new CommandConnexion("ftp.ubuntu.com", 21);
        commandConnexion.initialiseConnexion();
        commandConnexion.getResponse();
        commandConnexion.writeCommand("AUTH TLS");
        assertEquals("530 Please login with USER and PASS.", commandConnexion.getResponse());
    }

}
