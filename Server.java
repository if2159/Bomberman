//version 2.0
import java.awt.Rectangle;
import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.*;

public class Server implements Runnable{
	ServerSocket mySocket;
	Socket ClientSocket,Client2Socket;
	ObjectOutputStream output,output2;
	ObjectInputStream input,input2;
	private gameState combined=new gameState();
	
	

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
    		//receiving data
    		try{
    				//out.println("Run1");
    		gameState g2=(gameState)input2.readObject();//receiving states of the games from clients
    		gameState g1=(gameState)input.readObject();

			//out.println("IN");
			//sending data
			gameState player1=new gameState(g2);//makes a copy to avoid concurrent modification exception
			gameState player2=new gameState(g1);
			
			
			combineStates(player1,player2);
			checkHits();
			update();
			
			output.writeObject(new gameState(combined));//sending states to the other clients
			output2.writeObject(new gameState(combined));
			output.flush();
			output2.flush();

			output.reset();//must be done in order for the objects to be different so that the states will send
			output2.reset();

		//	out.println("OUT");
			try{
			Thread.sleep(10); //100hz tick rate
			}

			catch(InterruptedException e){

				e.printStackTrace();
				}

    		}
    		catch(SocketException e){
    			running=false;
				out.println("Client Disconnected");
				return;
				}


    		catch(IOException e){
				e.printStackTrace();
    			}
    		catch(ClassNotFoundException e){
				System.out.println("Class not Found in Server");
				e.printStackTrace();
    			}


		}


	}
	
	//TODO need to change so that the bomb and explosion times work with the different tick rates
	public void update(){

		
		for(int i=0;i<combined.getBomb().size();i++){//update bombs and get the calculation for the spread of the explosion
			Bomb b=combined.getBomb().get(i);
			if(b.update()){
			//	out.println(b.getRect());
				b.explode();
				UUID id=b.getID();
				Explosion e=new Explosion(combined.getBomb().remove(0));
				e.setSpread(getSpreadDown(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadUp(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadLeft(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadRight(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				combined.getExplosions().add(e);
				if(combined.getPlayer1().getID()==id){
					combined.getPlayer1().decBomb();
				}
				else{
					combined.getPlayer2().decBomb();
				}
			}
			
			else out.println(b.getTime()+" "+b.getID());

		}
		for(int i=0;i<combined.getExplosions().size();i++){//update explosion removes if time==0
			Explosion e=combined.getExplosions().get(i);
			if(e.update()){
				combined.getExplosions().remove(0);
				//out.println("Removing");
				}
			}
		
		
	}
	//TODO redo the way to get spread
	//these calculate the distance that an explosion reaches
	//goes until it either it reaches a brick then consumes that brick or till it reaches a column
		public ArrayList<Rectangle> getSpreadDown(int p, int x, int y){
			if(p>=0)
				if(!(x<=0||y<=0||x>=25||y>=25)){
					if(combined.getField()[x][y].getImage()==2){
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						temp.add(new Rectangle(x,y,32,32));
						return temp;
					}
					else if(combined.getField()[x][y].getImage()==3){
						return new ArrayList<Rectangle>();
						}
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						ArrayList <Rectangle>tmp=getSpreadDown(p-1,x,y+1);

							temp.add(new Rectangle(x,y,32,32));
							temp.addAll(tmp);
							return temp;

				}
				return new ArrayList<Rectangle>();
			}
		public ArrayList<Rectangle> getSpreadUp(int p, int x, int y){//this works!
			if(p>=0)
				if(!(x<=0||y<=0||x>=25||y>=25)){
					if(combined.getField()[x][y].getImage()==2){
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						temp.add(new Rectangle(x,y,32,32));
						return temp;
					}
					else if(combined.getField()[x][y].getImage()==3){
						return new ArrayList<Rectangle>();
						}
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						ArrayList <Rectangle>tmp=getSpreadUp(p-1,x,y-1);

							temp.add(new Rectangle(x,y,32,32));
							temp.addAll(tmp);
							return temp;

				}
				return new ArrayList<Rectangle>();
			}
		public ArrayList<Rectangle> getSpreadLeft(int p, int x, int y){//this works!
				if(p>=0)
					if(!(x<=0||y<=0||x>=25||y>=25)){
						if(combined.getField()[x][y].getImage()==2){
							ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
							temp.add(new Rectangle(x,y,32,32));
							return temp;
						}
						else if(combined.getField()[x][y].getImage()==3){
							return new ArrayList<Rectangle>();
							}
							ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
							ArrayList <Rectangle>tmp=getSpreadLeft(p-1,x-1,y);

								temp.add(new Rectangle(x,y,32,32));
								temp.addAll(tmp);
								return temp;

					}
				return new ArrayList<Rectangle>();
			}
		public ArrayList<Rectangle> getSpreadRight(int p, int x, int y){//this works!
			if(p>=0)
				if(!(x<=0||y<=0||x>=25||y>=25)){
					if(combined.getField()[x][y].getImage()==2){
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						temp.add(new Rectangle(x,y,32,32));
						return temp;
					}
					else if(combined.getField()[x][y].getImage()==3){
						return new ArrayList<Rectangle>();
						}
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						ArrayList <Rectangle>tmp=getSpreadRight(p-1,x+1,y);

							temp.add(new Rectangle(x,y,32,32));
							temp.addAll(tmp);
							return temp;

				}
				return new ArrayList<Rectangle>();
			}


		public void explodeOthers(){//checks if the explosion covers a bomb then will cause it to explode
			for(int i=0;i<combined.getExplosions().size();i++){
				for(Rectangle e:combined.getExplosions().get(i).getSpread()){
					//out.println(e);
					for(int j=0;j<combined.getBomb().size();j++){
						Bomb b=combined.getBomb().get(j);
						//out.println(b.getRect());
						if((new Rectangle((int)(e.getX())*32,(int)(e.getY()*32),32,32)).intersects(b.getRect())){//new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY())/32,32,32))){
						//	out.println("HERE"+e+"\n"+new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32));
							b.explode();
							UUID id=b.getID();
							Explosion ee=new Explosion(combined.getBomb().remove(j));
							ee.setSpread(getSpreadDown(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
							ee.addSpread(getSpreadUp(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
							ee.addSpread(getSpreadLeft(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
							ee.addSpread(getSpreadRight(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
							combined.getExplosions().add(ee);
						//	ee.setTime(combined.getExplosions().get(i).getTime());
							if(combined.getPlayer1().getID()==id){
								combined.getPlayer1().decBomb();
							}
							else{
								combined.getPlayer2().decBomb();
							}
						}
					}

					}
				}


		}
	
	private void combineStates(gameState player1,gameState player2){
		
		ArrayList<Explosion> explosion=combined.getExplosions();
		if(explosion==null)
			explosion=new ArrayList<Explosion>();
		explosion.addAll(player1.getExplosions());
		explosion.addAll(player2.getExplosions());
		
		ArrayList<Bomb> bomb=combined.getBomb();
		if(bomb==null)
			bomb=new ArrayList<Bomb>();
		bomb.addAll(player1.getPlayer1().getBombs());
		bomb.addAll(player2.getPlayer1().getBombs());
		bomb.addAll(player1.getBomb());
		bomb.addAll(player2.getBomb());
		//combine play field
		block [][]field=player1.getField();
		for(int r=0;r<25;r++){
			for(int c=0;c<25;c++){
				if(player1.getField()[r][c].getImage()==1||player2.getField()[r][c].getImage()==1){
					field[r][c].setImage(1);
				}
			}
		}
		combined.setField(field);
		combined.setBomb(bomb);
		combined.setExplosions(explosion);
		combined.setPlayer1(player1.getPlayer1());
		combined.setPlayer2(player2.getPlayer1());
	}
	
	private void checkHits(){
		Player p1 = combined.getPlayer1();
		Player p2 = combined.getPlayer1();
		
		for(Explosion e: combined.getExplosions()){
			for(Rectangle x:e.getSpread()){
				Rectangle r=new Rectangle((int)(x.getX()*32),(int)(x.getY()*32),32,32);
				if(r.intersects(p1.getRect())){
					out.println("I die");
					combined.setPlayer1(null);//TODO Currently Uses null should move to using boolean playerDead
					}
				if(r.intersects(p2.getRect())){
					
					out.println("I die");
					combined.setPlayer2(null);//TODO Currently Uses null should move to using boolean playerDead
					}
			}
		}
		
	}

	
	
	
}















