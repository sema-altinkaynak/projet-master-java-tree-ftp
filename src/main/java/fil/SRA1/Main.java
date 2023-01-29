package fil.SRA1;

import java.io.IOException;

import fil.SRA1.Tree.*;

public class Main {
    
    public static void main( String[] args ) throws IOException{
        Tree tree;
        if(args.length<2){
            tree = new Tree(args[0],100000);
            tree.afficheTree();
            tree.finish();
        }
        if(args.length==2){
            tree = new Tree(args[0],Integer.parseInt(args[1]));
            tree.afficheTree();
            tree.finish();
        }
        
    }

}
