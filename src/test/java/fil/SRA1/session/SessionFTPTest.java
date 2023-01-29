package fil.SRA1.session;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.junit.Test;

import fil.SRA1.Tree.connexion.DataConnexion;
import fil.SRA1.Tree.session.SessionFTP;

/**
 * Classe testant le 
 */
public class SessionFTPTest {

    @Test
    public void testSessionDemarrageFTP() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session = new SessionFTP("ftp.ubuntu.com");
        session.finishSession();
       
        
    }

    @Test
    public void testSessionFTPThrowUnknownHostExceptionErrror(){
        
        assertThrows(UnknownHostException.class, () -> {new SessionFTP("ftp.ubuntu;com");});  
        
    }

    
    @Test
    public void testinitialiseSessionThrowNullPointerExceptionError(){
        SessionFTP session=null;
        assertThrows(NullPointerException.class, () -> {session.initialiseSession();});
        
    }
    

    @Test
    public void testinitialiseSessionFTP() throws NullPointerException, IOException{
        SessionFTP session = new SessionFTP("ftp.ubuntu.com");
        session.initialiseSession();
        session.finishSession();
      
        
    }

    @Test 
    public void testModePassif() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        session.initialiseSession();
        int number = session.mode("PASV");
        assertTrue(number>1024);
        session.finishSession();
        
    }

    @Test
    public void testModePassifError() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {session.mode("PASV");});
        session.finishSession();
        
    }

    @Test 
    public void testCreationConnexionData() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session = new SessionFTP("ftp.ubuntu.com");
        session.initialiseSession();
        int port = session.mode("PASV");
        DataConnexion s = session.initialiseSocketDestinationData(port);
        session.afficherListRepertoire();  
        assertEquals ("d",s.getResponse().substring(0, 1));
        session.finishSession();
    }
    

    @Test
    public void testCreationConnexionDataThrowConnectExceptionError() throws ConnectException, UnknownHostException, IOException{

        SessionFTP session = new SessionFTP("ftp.ubuntu.com");
        assertThrows(ConnectException.class, () -> {session.initialiseSocketDestinationData(23);});

    }

    @Test 
    public void testVisiteSousRepertoireEtAfficheListe() throws ConnectException, UnknownHostException, IOException{
        // Récuperation d'un répertoire père contenat au moins un fichier
        SessionFTP session = new SessionFTP("ftp.ubuntu.com");
        session.initialiseSession();
        int port = session.mode("PASV");
        DataConnexion s = session.initialiseSocketDestinationData(port);
        session.afficherListRepertoire();  
        String reponse = s.getResponse();
        assertEquals ("d",reponse.substring(0, 1));
        s.finish();

        // Teste du passage à un sous repertoire
        session.repertoireEnfant("/"+reponse.substring(56));
        int portSuivant = session.mode("PASV");
        DataConnexion suivant = session.initialiseSocketDestinationData(portSuivant);
        session.afficherListRepertoire();  
        String reponseSuivante = suivant.getResponse();
        assertEquals ("d",reponseSuivante.substring(0, 1));
        suivant.finish();
        session.finishSession();

    }

    @Test
    public void testis_directionnary() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        assertTrue(session.is_directionnary("drwx"));

        
    }

    @Test
    public void testis_directionnaryFail() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        assertFalse(session.is_directionnary("-rwx"));

    }


    @Test 
    public void testHasRight() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        assertTrue(session.hasRight("-rwxrwxrwx"));

    }

    @Test
    public void testHasRightFail() throws ConnectException, UnknownHostException, IOException{
        SessionFTP session;
        session = new SessionFTP("ftp.ubuntu.com");
        assertFalse(session.hasRight("-rwxrwxrw-"));

    }
    
}

