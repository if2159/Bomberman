/**
 * @(#)Client.java
 *
 *
 * @author
 * @version 1.00 2015/5/8
 */
import java.net.*;
import java.io.*;
import static java.lang.System.*;

public class Client implements Runnable{
	Socket toServer;
	ObjectInputStream input;
	ObjectOutputStream output;
	gameState gs,gsOut,gsIn;

    public Client(String ip, int port){
    	try{
    	toServer=new Socket(ip,port);}//port should be 2662
    	catch(UnknownHostException e){
    		e.printStackTrace();
    		}
    	catch(IOException e){
    		e.printStackTrace();
    		}
    //	out.println("NEW CLIENT");
    	gs=gsOut=gsIn=null;
    }
	public void start(){
		try{

		input= new ObjectInputStream(toServer.getInputStream());
    	output= new ObjectOutputStream(toServer.getOutputStream());

		}
    	catch(IOException e){
    		e.printStackTrace();
    		}

		}
//Thread t=new Thread(this);
	public void run(){
		boolean running=true;
		while(running){
			try{

				gameState x=new gameState(gsOut);//used in attempt to avoid Concurrent Modification Exception
			synchronized(this){
	    		output.writeObject(x);//send to server

	    //	out.println("OUT");
	    	output.flush();
			output.reset();
			}
	    	gsIn = (gameState)(input.readObject());//receive from server
		}
		catch(SocketException e){
			running=false;
				out.println("Client Disconnected");
				}
    	catch(ClassNotFoundException e){
    		e.printStackTrace();
    		}
    	catch(IOException e){
    		e.printStackTrace();
    		}
    		try{
    		Thread.sleep(30);}
    		catch(InterruptedException e){

    			}
		}
		}

	public gameState getState(){
		return gsIn;
		}

	public synchronized void sendState(gameState g){
		gsOut=new gameState(g);
		}

    /*public gameState run(gameState gs){

    	//	out.println("run1");
		try{
	    	output.writeObject(gs);
	    //	out.println("OUT");
	    	output.flush();
	    	output.reset();

	    	return (gameState)(input.readObject());
		}
    	catch(ClassNotFoundException e){
    		e.printStackTrace();
    		}
    	catch(IOException e){
    		e.printStackTrace();
    		}
		return null;
    	}*/



/*	public static Client c;
	public static Thread t;
    public static void main(String args[])throws Exception{
//    		Scanner sc=new Scanner(System.in);
    		//out.println("input ip and port");
    		String ip="localhost";//sc.next();
    		int port=2662;
    		c=new Client(ip,port);
    		t=new Thread();
    		t.start();
    		//t.run();
    		c.start();
    		while(true){
    			c.run();
    			out.println(1);
    			Thread.sleep(30);
    			out.println(2);
    			}
    		//run2();
    		}


  // 	public void addNotify(){
	//	super.addNotify();
	//	t=new Thread(this);
	//	t.start();
		//}
			public void run(){}

	public static void run2(){
			while(true){
				out.println("run");
				try{
			update();}
			catch(Exception e){
				e.printStackTrace();
				}
			try{
				t.sleep(33);		//33 frames per second   1000/35
				}
			catch(Exception e){
				System.out.println("Error sleeping in run method: " +e);
				}

			}

		}

	public void reset(){

		}

	public static void update()throws Exception{
//		c.runClient();
	}*/

}