package fil.SRA1.Tree;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import fil.SRA1.Tree.connexion.*;
import fil.SRA1.Tree.session.*;

/**
 * Classe principale permettant l'affichage du contenu d'un serveur FTP
 */
public class Tree {
    
    private SessionFTP session;
    private String adresse;
    private int profondeur;
    /**
     * Initialisation de la session pour la charge des fichiers
     * @param adresse adresse du serveur
     * @throws IOException
     * @throws UnknownHostException
     * @throws ConnectException
     */
    public Tree(String adresse,int profondeur) throws ConnectException, UnknownHostException, IOException {
        this.session = new SessionFTP(adresse);
        this.session.initialiseSession();
        this.adresse = adresse;
        this.profondeur = profondeur;
 
    }

    /**
     * Parcours récursivement chaque sous dossier d'un dossier
     * @param name le nom du dossier actuel parcouru
     * @param parent le dossier parent du dossier actuel ( chemin absolu )
     * @param espace l'espace à afficher pour respecter l'arboresence 
     * @return Boolean 
     * @throws IOException
     */
    private boolean parcoursTree(String name,String parent, String espace,int parcours) throws IOException{
        if(!(session.is_directionnary(name))){
            System.out.println(espace+"|— "+name.substring(56));
            return true;
        }
        else if(parcours == (this.profondeur-1)){
            return true;
        }
        else{
            int portDestination = session.mode("PASV");
            if(portDestination==0){
                return false;
            }
            DataConnexion connexion = session.initialiseSocketDestinationData(portDestination);
            boolean check = session.afficherListRepertoire();
            if(check ){
                String fichier ;
                while((fichier = connexion.getResponse()) != null){
                    if(session.is_directionnary(fichier) && session.hasRight(fichier)){
                        System.out.println(espace+"|-"+fichier.substring(56));
                        session.repertoireEnfant(parent+"/"+name.substring(56)+"/"+fichier.substring(56));
                        boolean val =parcoursTree(fichier,parent+"/"+name.substring(56),espace+"|   ",parcours+1);
                        if(val==false){
                            this.session = new SessionFTP(this.adresse);
                            this.session.initialiseSession();
                            this.session.repertoireEnfant("/"+parent+"/"+name.substring(56));
                        }
                    }
                    else{

                        parcoursTree(fichier, parent, espace ,parcours);
                    }
                }
                //this.parcours++;
                session.repertoireParent();
            }else{
                connexion.finish();
                return false;

            }
            connexion.finish();
            
        }
        
        return true;
    }

    /**
     * Initialise le parcours du serveur et l'affiche
     * @throws IOException
     */
    public void afficheTree() throws IOException{
        int port = this.session.mode("PASV");
        DataConnexion data = session.initialiseSocketDestinationData(port);
        this.session.afficherListRepertoire();
        String fichier = data.getResponse();
        while (fichier != null){
            System.out.println("|—-"+fichier.substring(56));
            if(this.session.hasRight(fichier)){
                this.session.repertoireEnfant("/"+fichier.substring(56));
                parcoursTree(fichier, "", "|   ",0);   
            }
            fichier = data.getResponse();
        }
        data.finish();
        session.finishSession();
    }

    /**
     * Deconnecte la session et termine l'affichage 
     * @throws IOException
     */
    public void finish() throws IOException{
        this.session.finishSession();
    }
}

