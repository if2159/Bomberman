/**
 * @(#)Server.java
 *
 *
 * @author
 * @version 1.00 2015/5/8
 */

import java.io.*;
import java.net.*;
import static java.lang.System.*;

public class Server implements Runnable{
	ServerSocket mySocket;
	Socket ClientSocket,Client2Socket;
	ObjectOutputStream output,output2;
	ObjectInputStream input,input2;

    public Server() throws IOException {
    	mySocket=new ServerSocket(2662);
    }

    public Server(int port) throws IOException {
    	mySocket=new ServerSocket(port);
    //	out.println("NEW SERVER");
    }

    public void runServer() throws IOException{
    	ClientSocket=mySocket.accept();
    //	out.println("Connect 1");

    	Client2Socket=mySocket.accept();
    //	out.println("Connect 2");

    	output=new ObjectOutputStream(ClientSocket.getOutputStream());
    	input=new ObjectInputStream(ClientSocket.getInputStream());

    	output2=new ObjectOutputStream(Client2Socket.getOutputStream());
    	input2=new ObjectInputStream(Client2Socket.getInputStream());

    	output.flush();//clears the output buffer
		output2.flush();

    }
	//private void mergeStates(gameState one,gameState two){

	public void run(){
	//	out.println("Run");
	boolean running=true;
		try{
		runServer();}
		catch(IOException e){
			e.printStackTrace();
			}
		while(running){
    		//recieving data
    		try{
    				out.println("Run1");
    		gameState g2=(gameState)input2.readObject();//recieving states of the games from clients
    		gameState g1=(gameState)input.readObject();

			out.println("IN");
			//sending data
			gameState x1=new gameState(g2);//makes a copy to avoid concurrent modification exception
			gameState x2=new gameState(g1);

			output.writeObject(x1);//sending states to the other clients
			output2.writeObject(x2);
			output.flush();
			output2.flush();

			output.reset();//must be done in order for the objects to be different so that the states will send
			output2.reset();

		//	out.println("OUT");
			try{
			Thread.sleep(20);
			}

			catch(InterruptedException e){

				e.printStackTrace();
				}

    		}
    		catch(SocketException e){
    			running=false;
				out.println("Client Disconnected");
				}


    		catch(IOException e){
				e.printStackTrace();
    			}
    		catch(ClassNotFoundException e){
				e.printStackTrace();
    			}


		}

		}



	//	}

    //	catch(Exception e){
    //		e.printStackTrace();
    //		}


   /* public static void main(String args[]) throws IOException, ClassNotFoundException{
//    	Thread t=new Thread(this);
    	Server s=new Server();
    	out.println("run");
    	s.runServer();
    	}*/


}