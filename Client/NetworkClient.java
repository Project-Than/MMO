package Client;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import Entities.DynamicEntity.PJ;
import GameSys.ActionJoueur;
import GameSys.GestionPartie;
import GameSys.World;

public class NetworkClient {

public static void main(String args[]) throws IOException{


    InetAddress address=InetAddress.getLocalHost();
    Socket s1=null;
    String line=null;
    BufferedReader br=null;
    BufferedReader is=null;
    PrintWriter os=null;
    ObjectOutputStream objout = null;
    ObjectInputStream objin = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    PJ player = null ;
    boolean keeprunning = true;
    int id = 0;
    String map = null;
    World tmp = null;
    ActionJoueur aj = null;

    try {
        s1=new Socket(address, 4445); // You can use static final constant PORT_NUM
        br= new BufferedReader(new InputStreamReader(System.in));
        is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
        os= new PrintWriter(s1.getOutputStream());
        objout = new ObjectOutputStream(s1.getOutputStream());
        objin = new ObjectInputStream(s1.getInputStream());
        out = new DataOutputStream(s1.getOutputStream());
        in = new DataInputStream(s1.getInputStream());
    }
    catch (IOException e){
        e.printStackTrace();
        System.err.print("IO Exception");
    }

    System.out.println("Joueur connecté !");
    System.out.println("Bienvenue sur Town of Demons !");
    try{
        int choice = Launcher();
        if (choice == 1) {
        	player = CreatePJ(player);
        	player.setId(id);
        }
     
        Maj(objin,objout,map,player);
        objout.flush();
        tmp = (World) objin.readObject();
        placerJoueur(player,tmp);
        aj = new ActionJoueur(0,tmp,"nom",player,player.getSacPJ());
        Jouer(objout, aj);
        objout.writeObject(player);
        objout.flush();
    } catch (ClassNotFoundException e) {

		e.printStackTrace();
	}
    finally{
    	is.close();os.close();br.close();s1.close();
        
                System.out.println("Déconnexion du serveur...");

    }

}

public static PJ CreatePJ(PJ player) {
	player = new PJ();
	player.CreationPJ();
	return player;
}

public static int Launcher() {
	
	System.out.println("Vous voilà enfin prêt, souhaitez vous désormais");
	System.out.println("-1 Créer une partie");
	System.out.println("-2 Reprendre la partie");
	System.out.println("-3 Quitter le serveur");
	System.out.print("Que choisissez vous ? : ");
	Scanner sc = new Scanner(System.in);
	int choice = sc.nextInt();
	return choice;	
}

public static void Maj(ObjectInputStream objin,ObjectOutputStream objout,String map,PJ player) {
	try {
		map = (String) objin.readObject();
		objout.writeObject(player);
		objout.flush();
		System.out.println(map);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public static void Jouer(ObjectOutputStream out,ActionJoueur aj) {
	aj.GainPA();
	try {
		out.writeObject(aj.DeplacerPJ());
		out.flush();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public static void placerJoueur(PJ player,World tmp) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Veuillez entrer les coordonnées pour placer votre joueur :");
    System.out.println("x :");
    int x = sc.nextInt();
    System.out.println("y :");
    int y = sc.nextInt();
    player.placer(x, y, tmp.getEntities());
    tmp.AfficherMap();
}

}