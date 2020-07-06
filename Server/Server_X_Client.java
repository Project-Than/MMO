package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import GameSys.ActionJoueur;
import GameSys.World;
import Entities.DynamicEntity.PJ;


public class Server_X_Client {
	
	public static int id = 0;
	
public static void main(String args[]){


    Socket s=null;
    ServerSocket ss2=null;
    System.out.println(" ---------------------------------------- Town of Demons ---------------------------------------- ");
    System.out.println(" ---------------------------------------- SERVEUR ----------------------------------------------- ");
    System.out.println("En attente de connexion....");
    try{
        ss2 = new ServerSocket(4445); 

    }
    catch(IOException e){
    e.printStackTrace();
    System.out.println("Erreur serveur");

    }

    while(true){
        try{
            s= ss2.accept();
            System.out.println("Connexion établie !");
            ServerThread st=new ServerThread(s);
            st.start();

        }

    catch(Exception e){
        e.printStackTrace();
        System.out.println("Erreur de connexion..");

    }
    }

}

}

class ServerThread extends Thread{  

    String line=null;
    boolean keeprunning = true;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;
    ObjectOutputStream objout = null;
    ObjectInputStream objin = null;
    DataOutputStream outstream = null;
    DataInputStream instream = null;
    World map = new World(20,20);
    int numplayer = -1;
    ArrayList<PJ> pjlist = new ArrayList<PJ>();
    DataOutputStream out = null;
    ActionJoueur aj = null;
    
    public ServerThread(Socket s){
        this.s=s;
        numplayer++;
        
    }
    

    public void run() {
    try{
        is= new BufferedReader(new InputStreamReader(s.getInputStream()));
        os=new PrintWriter(s.getOutputStream());
        objout = new ObjectOutputStream(s.getOutputStream());
        objin = new ObjectInputStream(s.getInputStream());

    }catch(IOException e){
        System.out.println("IO error in server thread");
    }

    try {

        while(keeprunning){
        	map.GenererMap();
            objout.writeObject(map.StockerMap());
            /*objout.flush();*/
            pjlist.add(numplayer,(PJ) objin.readObject());
            objout.writeObject(map);
            /*objout.flush();*/
            map = (World) objin.readObject();
            map.AfficherMap();
            /*pjlist.set(numplayer, (PJ) objin.readObject());*/
            /*pjlist.get(numplayer).AffichageStats();*/
            
            saveGame();
        	/*keeprunning = false;*/
        }  
    } catch (IOException e) {

        line=this.getName(); 
        System.out.println("IO Error/ Client "+line+" terminated abruptly");
    }
    catch(NullPointerException e){
        line=this.getName(); 
        System.out.println("Client "+line+" Closed");
    } catch (ClassNotFoundException e) {
		e.printStackTrace();
	}

    finally{    
    try{
        System.out.println("Connection Closing..");
        if (is!=null){
            is.close();
            System.out.println(" Socket Input Stream Closed");
        }

        if(os!=null){
            os.close();
            System.out.println("Socket Out Closed");
        }
        if (s!=null){
        s.close();
        System.out.println("Socket Closed");
        }

        }
    catch(IOException ie){
        System.out.println("Socket Close Error");
    }
    }
    }
    
    public void saveGame() {
    	File file = new File("data/SavePJ.bin");
    	File savemap = new File("data/SaveMap.bin");
    	if (!file.exists()) {
    		try {
    			file.createNewFile();
    			savemap.createNewFile();
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	try {
    		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
    		out.writeObject(pjlist);
    		out.flush();
    		out = new ObjectOutputStream(new FileOutputStream(savemap));
    		out.flush();
    		}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
     }
}
