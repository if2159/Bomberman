import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.*;
import static java.lang.System.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.JFrame;


public class BomberManMain extends JFrame implements ActionListener{//, ItemListener {

private BomberManInternal BomberPanel;
	private Container win, subWin;
	private JMenu helpMenu;
	private JMenuBar menubar;
	private JMenuItem helpItem;
	private boolean atStartMenu;
	protected String ip=null;
	private JButton begin,startServer;
	private JTextField inputText;
	private Thread j;

	public BomberManMain()
	{
		super ("Ian's Bomberman");

		win = getContentPane();
		win.setLayout(null);

		setSize(0,0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(false);




		menubar = new JMenuBar();

		helpMenu = new JMenu("Help");

		helpItem = new JMenuItem("Help");

		helpMenu.add(helpItem);
		menubar.add(helpMenu);

		setJMenuBar(menubar);
		ImageIcon img = new ImageIcon("./images/cat_icon.png");
		setIconImage(img.getImage());

		atStartMenu=true;
		startMenu();




		win.setVisible(false);

	}

	public void start1(){
		setSize(1000,1000);
		BomberPanel = new BomberManInternal();
		win.add(BomberPanel);
		BomberPanel.setLocation(5,5);
		win.setVisible(true);
		setVisible(true);
		}

	Thread ServerThread;
	Server server;
	public void actionPerformed(ActionEvent event){
		if (event.getSource() == begin)
		{
			f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
			ip=inputText.getText();

			out.println(ip);
			start1();


			}
		if (event.getSource() == startServer)
		{
			f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
			try{
			server=new Server(2662);
			//server.runServer();
			ServerThread=new Thread(server);
			ServerThread.start();
			ServerThread.setName("ServerThread");
			}
			catch(IOException e){
				e.printStackTrace();
				}
			ip="localhost";

			start1();


			}

	}

JFrame f;
	public void startMenu(){
		f = new JFrame("Server Connect");
		subWin = f.getContentPane();
        subWin.setLayout(null);
        subWin.setPreferredSize(new Dimension(400,200));

		JLabel info;
		info= new JLabel("Please enter the IP of server");
		info.setSize(500,20);
		info.setLocation(20, 20);
		subWin.add(info);


		begin = new JButton("Connect to Server");
		begin.addActionListener(this);

		startServer=new JButton("Start Server");
		startServer.addActionListener(this);

		inputText=new JTextField();
		subWin.add(begin);
		subWin.add(startServer);
		subWin.add(inputText);

		begin.setLocation(20,80);
		startServer.setLocation(170,80);
		inputText.setLocation(170-100,50-5);

		begin.setSize(140,30);
		startServer.setSize(140,30);
		inputText.setSize(200,30);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       	f.pack();
       	f.setVisible(true);
		}


public class BomberManInternal extends JPanel implements Runnable, KeyListener
{
	private JPanel frame = new JPanel();
	private BufferedImage buffer = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
	private Insets in = getInsets();
	private BufferedImage grass,column, brick,cat,bomb,explosion,i0,i1,i2,i3,i4,i5,i6,i7,image;
	private block field[][];//The play area that stores whether each block is grass, brick, or column
	private Player player,player2;
	private ArrayList<Explosion> explosionList,explosionList2;//list of all explosions in the game
	private ArrayList<Bomb> bombList,bombList2;//list of all bombs in the game
	private int test;
	private gameState CurrentState,gameState2;//used for sending to and receiving from server
	private Client client;//connection to the server
	private Thread clientThread;//client thread
	private int Score;

    public BomberManInternal(){


		try
		{
			grass=ImageIO.read(new File("./images/grass2.png"));
			column=ImageIO.read(new File("./images/column.png"));
			brick=ImageIO.read(new File("./images/bricks.png"));
			cat=ImageIO.read(new File("./images/cat.png"));
			bomb=ImageIO.read(new File("./images/bomb.png"));
			explosion=ImageIO.read(new File("./images/explosion.png"));
			image=ImageIO.read(new File("./images/test3.png"));

		}
		catch (IOException e)
		{
		  System.out.println("Images Not Found!"+e);
		  player=null;
		}
		setFocusable(true);						//YOU NEED THIS LINE AND THE FOLLOWING LINE!!!!!!!!!!!!  //I did not try it, but assume when game if over you setFocusable(false) and allow focus to return to the frame.
		requestFocusInWindow();

		setSize(800,800);

		//used to fill the field area with at least 30% grass
		//also places columns and bricks
		field=new block[25][25];
		for(int r=0;r<25;r++){
			for(int c=0; c<25;c++){

				if((r%2==0&&c%2==0)||(r==0||c==0)||(r==24||c==24)){
					field[r][c]=new block(r,c,3,false);

					}
				else{
					if((int)(Math.random()*100)>=70||(r==1&&c==1)){
						field[r][c]=new block(r,c,1,false);
						field[r][c].test=true;
						}
					else field[r][c]=new block(r,c,2,true);
					}
				}
			}
		clearStartPlaces();//clear the spawn place for the player
		Point x=randSpawnPoint();
		player=new Player((int)(x.getX())*32,(int)(x.getY())*32);//where the player spawns

	//	player2=new Player(0,0,cat);
		explosionList=new ArrayList<Explosion>();
		bombList=new ArrayList<Bomb>();

		explosionList2=new ArrayList<Explosion>();
		bombList2=new ArrayList<Bomb>();



		//no longer used was for "party mode"
		/*
		i0 = image.getSubimage(0,0,32,32);
		i1 = image.getSubimage(32,0,32,32);
		i2 = image.getSubimage(64,0,32,32);
		i3 = image.getSubimage(96,0,32,32);
		i4 = image.getSubimage(128,0,32,32);
		i5 = image.getSubimage(160,0,32,32);
		i6 = image.getSubimage(192,0,32,32);
		i7 = image.getSubimage(224,0,32,32);
		*/


		client=new Client(ip,2662);//establish connection to server
		client.start();

		CurrentState=new gameState(bombList,explosionList,player,field);
		clientThread=new Thread(client);
		clientThread.setName("Client");
		clientThread.start();

		synchronized(client){
			client.sendState(new gameState(CurrentState));//get the intial state of the clients
			gameState2=client.getState();}
		if(gameState2!=null)
			mergeFields(gameState2);//combine the fields in a way that grass is favoured
		Score=-100;
	}





	public void clearStartPlaces(){
		field[1][1]=new block(1,1,1,false);
		field[1][1].test=true;
		field[2][1]=new block(2,1,1,false);
		field[1][2]=new block(1,2,1,false);
		field[1][2].test=true;
		field[2][1].test=true;
		}

	public Point randSpawnPoint(){
		ArrayList <Point>points=new ArrayList<Point>();
		for(int r =0;r<25;r++){
			for(int c=0; c<25;c++){
				if(field[r][c].getImage()==1){
					points.add(new Point(r,c));
					}
				}
			}
		int x=(int)(Math.random()*points.size());
		return points.get(x);


		}


	public void reset(){

		}

	public void update(){//where most work is done
		if(player!=null){//used to get bombs and explosions that belonged to players
			for(Bomb b:player.getBombs()){
				if(!bombList.contains(b))
					bombList.add(b);
				//else out.println("ERROR**********************************************************");
				}
			for(Explosion e:player.getExplosions()){
				if(!explosionList.contains(e))
					explosionList.add(e);
			//	else out.println("ERROR**********************************************************");
				}

			}
		for(int i=0;i<bombList.size();i++){//update bombs and get the calculation for the spread of the explosion
			Bomb b=bombList.get(i);
			if(b.update()){
			//	out.println(b.getRect());
				b.explode();
				Explosion e=new Explosion(bombList.remove(0));
				e.setSpread(getSpreadDown(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadUp(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadLeft(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				e.addSpread(getSpreadRight(e.getPower(),(int)(e.getRect().getX())/32,(int)(e.getRect().getY())/32));
				explosionList.add(e);
				if(player!=null){
					player.decBomb();}
			}

		}

		for(int i=0;i<explosionList.size();i++){//update explosion removes if time==0
			Explosion e=explosionList.get(i);
			if(e.update()){
				explosionList.remove(0);
				//out.println("Removing");
				}
			}

		for(int i=0;i<bombList2.size();i++){//update bombs and get the calculation for the spread of the explosion
			Bomb b=bombList2.get(i);
			if(b.update()){
				bombList2.remove(i);
				i--;
				}
			}
		for(int i=0;i<explosionList2.size();i++){//update bombs and get the calculation for the spread of the explosion
			Explosion b=explosionList2.get(i);
			if(b.update()){
				explosionList2.remove(0);}
		}



		CurrentState=new gameState(bombList,explosionList,player,field);//prepare state to be sent to server


		synchronized(client){//must be synchronized to avoid errors
			client.sendState(new gameState(CurrentState));//sending to and receiving from server
			gameState2=client.getState();}
		if(gameState2!=null)
			player2=gameState2.getPlayer();//move the other clients player


	//	HashSet<Bomb> tempB=new HashSet<Bomb>();
	//	bombList.addAll(gameState2.getBomb());
	//	tempB.addAll(bombList);
	if(gameState2!=null){
		for(Bomb b:gameState2.getBomb()){
			if(!bombList2.contains(b)){
				b.walkOff();
				bombList2.add(b);
				}
			}
		for(Explosion e:gameState2.getExplosions()){
			if(!explosionList2.contains(e)){
				explosionList2.add(e);
				}
			}
	}
		//HashSet<Explosion> tempE=new HashSet<Explosion>();
	//	explosionList.addAll(gameState2.getExplosions());
	//	tempE.addAll(explosionList);

	//	explosionList=new ArrayList<Explosion>();
	//	bombList=new ArrayList<Bomb>();



	//	bombList.addAll(tempB);
	//	explosionList.addAll(tempE);
		explodeOthers();

		checkDeath();
		if(gameState2!=null)//combine two playing area; grass is favoured
			mergeFields(gameState2);

		repaint();

		}

	public void mergeFields(gameState gs){//Putting two fields together if there is grass on either it is placed in place of the bricks
		for(int r=0;r<25;r++){
			for(int c=0;c<25;c++){
				if(field[r][c].getImage()==1||gs.getField()[r][c].getImage()==1){
					field[r][c].setImage(1);
				}
			}
		}

	}


	private Thread t;
	public void addNotify(){
		super.addNotify();
		requestFocus();
		addKeyListener(this);
		t=new Thread(this);
		t.start();
		}

	public void run(){
			while(true){
			update();
			repaint();

			try{
				t.sleep(33);		//33 frames per second   1000/35
				}
			catch(Exception e){
//				System.out.println("Error sleeping in run method: " +e.printStackTrace());
				}

			}

		}

	public void paintComponent(Graphics g2){

		Graphics2D p = (Graphics2D)this.buffer.getGraphics();

		drawBlocks(p);
		drawBomb(p);
		drawPlayer(p);

	/*	for(int i =0;i<25;i++){
			p.drawLine(32*i,0,i*32,32*25);
			p.drawLine(0,32*i,32*25,i*32);
			}*/
		p.setColor(new Color(143,143,143));
		p.fillRect(640,768,800-640,825);
		Font arial =new Font("Arial",Font.BOLD,20);
		p.setColor(Color.BLUE);
		p.setFont(arial);
		p.drawString("Score: "+ Score,640,790);

		g2.drawImage(buffer, in.left, in.top, this);
		}

	public void drawBlocks(Graphics2D p){
		for(int r=0;r<25;r++){
				for(int c=0;c<25;c++){
					/*if(field[r][c].test){
						BufferedImage temp=grass;
						switch(test){
							case 0:
								temp=i0;
							break;
							case 1:
								temp=i1;
							break;
							case 2:
								temp=i2;
							break;
							case 3:
								temp=i3;
							break;
							case 4:
								temp=i4;
							case 5:
								temp=i5;
							break;
							case 6:
								temp=i6;
							break;
							case 7:
								temp=i7;
							break;

							}
							field[r][c].setImage(temp);
						}*/
					p.drawImage(((field[r][c].getImage()==1)?(grass):(field[r][c].getImage()==2)?(brick):(column)),(int)(field[r][c].getRect().getX()),(int)(field[r][c].getRect().getY()),this);

				}
			}
		//test++;
		//if(test==7){
		//	test=0;
		//	}

		}
	private boolean Player2Dead=false;
	private boolean PlayerDead=false;

	public void drawPlayer(Graphics2D p){
		if(player!=null){
			p.drawImage(cat,(int)(player.getX()),(int)(player.getY()),this);
		}
		//Composite x=p.getComposite();
		//p.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));

		//p.fillRect((int)(player.getRect().getX()),(int)(player.getRect().getY()),(int)(player.getRect().getWidth()),(int)(player.getRect().getHeight()));
		if(player2!=null){
			p.drawImage(cat,(int)(player2.getX()),(int)(player2.getY()),this);
			if(Player2Dead){
				Player2Dead=false;
				}
			}
		else if(!Player2Dead){
			Player2Dead=true;
			Score+=100;
			}
		//out.println("SCORE"+Score);
		//p.setComposite(x);
		//p.drawImage(player2.getImage(),(int)(player2.getX()),(int)(player2.getY()),this);


		}

	public void drawBomb(Graphics2D p){
		for(Bomb i:bombList){
			for(int r=0;r<25;r++){
				for(int c=0;c<25;c++){
					if(field[r][c].getRect().contains(i.getRect().getX(),i.getRect().getY())){
						p.drawImage(bomb,r*32,c*32,this);
					//	p.drawRect((int)(i.getRect().getX()),(int)(i.getRect().getY()),(int)(i.getRect().getWidth()),(int)(i.getRect().getHeight()));}
					}}
				}
			}
		for(Explosion e:explosionList){
			for(int r=0;r<25;r++){
				for(int c=0;c<25;c++){
					if(field[r][c].getRect().contains(e.getRect().getX(),e.getRect().getY())){
						p.drawImage(explosion,r*32,c*32,this);
						p.drawRect((int)(e.getRect().getX())*32,(int)(e.getRect().getY())*32,32,32);}

			}
			}
			for(Rectangle re:e.getSpread()){
				p.drawImage(explosion,(int)(re.getX())*32,(int)(re.getY())*32,this);
				field[(int)(re.getX())][(int)(re.getY())].setImage(1);
				p.drawRect((int)(re.getX())*32,(int)(re.getY())*32,32,32);
			}
		}
		if(gameState2!=null){
				for(Bomb i:bombList2){
					for(int r=0;r<25;r++){
						for(int c=0;c<25;c++){
							if(field[r][c].getRect().contains(i.getRect().getX(),i.getRect().getY())){
								p.drawImage(bomb,r*32,c*32,this);
								p.drawRect((int)(i.getRect().getX()),(int)(i.getRect().getY()),(int)(i.getRect().getWidth()),(int)(i.getRect().getHeight()));}
							}
						}
					}
		for(Explosion e:explosionList2){
			for(int r=0;r<25;r++){
				for(int c=0;c<25;c++){
					if(field[r][c].getRect().contains(e.getRect().getX(),e.getRect().getY())){
						p.drawImage(explosion,r*32,c*32,this);
						p.drawRect((int)(e.getRect().getX())*32,(int)(e.getRect().getY())*32,32,32);}

			}
			}
			for(Rectangle re:e.getSpread()){
				p.drawImage(explosion,(int)(re.getX())*32,(int)(re.getY())*32,this);
				field[(int)(re.getX())][(int)(re.getY())].setImage(1);
				p.drawRect((int)(re.getX())*32,(int)(re.getY())*32,32,32);
			}
		}}



			}


//these calculate the distance that an explosion reaches
//goes until it either it reaches a brick then consumes that brick or till it reaches a column
	public ArrayList<Rectangle> getSpreadDown(int p, int x, int y){
		if(p>=0)
			if(!(x<=0||y<=0||x>=25||y>=25)){
				if(field[x][y].getImage()==2){
					ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
					temp.add(new Rectangle(x,y,32,32));
					return temp;
				}
				else if(field[x][y].getImage()==3){
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
				if(field[x][y].getImage()==2){
					ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
					temp.add(new Rectangle(x,y,32,32));
					return temp;
				}
				else if(field[x][y].getImage()==3){
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
					if(field[x][y].getImage()==2){
						ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
						temp.add(new Rectangle(x,y,32,32));
						return temp;
					}
					else if(field[x][y].getImage()==3){
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
				if(field[x][y].getImage()==2){
					ArrayList <Rectangle>temp=new ArrayList<Rectangle>();
					temp.add(new Rectangle(x,y,32,32));
					return temp;
				}
				else if(field[x][y].getImage()==3){
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
		for(int i=0;i<explosionList.size();i++){
			for(Rectangle e:explosionList.get(i).getSpread()){
				//out.println(e);
				for(int j=0;j<bombList.size();j++){
					Bomb b=bombList.get(j);
					//out.println(b.getRect());
					if((new Rectangle((int)(e.getX())*32,(int)(e.getY()*32),32,32)).intersects(b.getRect())){//new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY())/32,32,32))){
					//	out.println("HERE"+e+"\n"+new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32));
						b.explode();
						Explosion ee=new Explosion(bombList.remove(j));
						ee.setSpread(getSpreadDown(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
						ee.addSpread(getSpreadUp(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
						ee.addSpread(getSpreadLeft(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
						ee.addSpread(getSpreadRight(ee.getPower(),(int)(ee.getRect().getX())/32,(int)(ee.getRect().getY())/32));
						explosionList.add(ee);
					//	ee.setTime(explosionList.get(i).getTime());
						if(player!=null){
							player.decBomb();}
						//j--;
					}
				}
				for(int j=0;j<bombList2.size();j++){
					Bomb b=bombList2.get(j);
					//out.println(b.getRect());
					if((new Rectangle((int)(e.getX())*32,(int)(e.getY()*32),32,32)).intersects(b.getRect())){
						bombList2.remove(j);

					}
				}

				}
			}


	}



	public void checkDeath(){//checks to see if fire covers a player
		try{


		if(player!=null){
		Rectangle rect=player.getRect();//new Rectangle((int)(Math.round(player.getRect().getX()/32)),(int)(Math.round(player.getRect().getY()/32)),32-4-5,29-3-4);

		for(Explosion e:explosionList){
			for(Rectangle x:e.getSpread()){
				Rectangle r=new Rectangle((int)(x.getX()*32),(int)(x.getY()*32),32,32);
				if(r.intersects(rect)){
					out.println("I die");
					player=null;

					}
			}}

		if(gameState2!=null){
		for(Explosion e:explosionList2){
			for(Rectangle x:e.getSpread()){
				Rectangle r=new Rectangle((int)(x.getX()*32),(int)(x.getY()*32),32,32);
				if(r.intersects(rect)){
					out.println("I die");
					player=null;

					}
			}}
		}
		}

		}
		catch(NullPointerException e){
		//	out.println("Null");
			}

		}
	public boolean canMove(int x, int y){//used to see if the player can move or if he walks off of a bomb
		int px=(int)player.getRect().getX();
		int py=(int)player.getRect().getY();
		for(int r=0;r<25;r++){
				for(int c=0;c<25;c++){
					if((field[r][c].getImage()==2||field[r][c].getImage()==3)
						&&(field[r][c].getRect().intersects(new Rectangle(px+x,py+y,23,22)))){
						//	out.println(false);
							return false;}
					for(Bomb b:bombList){
						if(!b.getWalk()){
						int xx=(int)(b.getRect().getX());
						int yy=(int)(b.getRect().getY());
						Rectangle BombTemp=new Rectangle(xx/32*32,yy/32*32,32,32);
						Rectangle newPlayerLoc=new Rectangle(px+x,py+y,22,22);
						if(BombTemp.intersects(newPlayerLoc)){
						//	out.println("false1 "+BombTemp+" \n\t"+newPlayerLoc);
						//	out.println(BombTemp.union(newPlayerLoc));
							return false;
							}
						}
						}
					for(Bomb b:bombList2){
						if(!b.getWalk()){
						int xx=(int)(b.getRect().getX());
						int yy=(int)(b.getRect().getY());
						Rectangle BombTemp=new Rectangle(xx/32*32,yy/32*32,32,32);
						Rectangle newPlayerLoc=new Rectangle(px+x,py+y,22,22);
						if(BombTemp.intersects(newPlayerLoc)&&!BombTemp.intersects(player.getRect())){
						//	out.println("false1 "+BombTemp+" \n\t"+newPlayerLoc);
						//	out.println(BombTemp.union(newPlayerLoc));
							return false;
							}
						}
						}

					}
				}
			return true;
		}

	private boolean first=true;//was used for a temporary hack to fix a thread synch problem
	private Rectangle screenMove;//not used


	public void keyPressed(KeyEvent e){
		char letter =  e.getKeyChar();
		if(player!=null){
			if(letter=='d'){
				if(canMove(2,0)){
					for(Bomb b:bombList){
						Rectangle Temp=new Rectangle(player.getRect());
						Rectangle bTemp=new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32);
						Temp.translate(2,0);
					if(b.getWalk()&&bTemp.intersects(player.getRect())&&!bTemp.intersects(Temp)){//checks if player is walking off of a bomb and therefore cannot walk back onto it
						b.walkOff();
					//	out.println("walkOFF");
						}}
					player.setX(player.getX()+2);

					}

				}
			if(letter=='a'){
				if(canMove(-2,0)){
						for(Bomb b:bombList){
							Rectangle Temp=new Rectangle(player.getRect());
							Rectangle bTemp=new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32);
							Temp.translate(-2,0);
						if(b.getWalk()&&((bTemp.intersects(player.getRect())&&!bTemp.intersects(Temp))||(!bTemp.intersects(player.getRect())))){ //&&!bTemp.intersects(Temp)))){
							b.walkOff();
						//	out.println("walkOFF");
						//	out.println(bTemp+"\n"+Temp);
							}
						else{
						//	out.println(bTemp.intersects(player.getRect())+" "+!bTemp.intersects(Temp));
						//	out.println(bTemp+"\n"+Temp);
							}
							}
						player.setX(player.getX()-2);

					}
				}

			if(letter=='w'){
				if(canMove(0,-2)){
					for(Bomb b:bombList){
						Rectangle Temp=new Rectangle(player.getRect());
						Rectangle bTemp=new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32);
						Temp.translate(0,-2);
					if(bTemp.intersects(player.getRect())&&!bTemp.intersects(Temp)){
						b.walkOff();
						//out.println("walkOFF");
						}}
					player.setY(player.getY()-2);
					/*Rectangle test=getBounds();
					test.translate(0,2);
					move((int)(test.getX()),(int)(test.getY()));*/
					}
				}
			if(letter=='s'){
				if(canMove(0,2)){
					for(Bomb b:bombList){
						Rectangle Temp=new Rectangle(player.getRect());
						Rectangle bTemp=new Rectangle((int)(b.getRect().getX()),(int)(b.getRect().getY()),32,32);
						Temp.translate(0,2);
					if(bTemp.intersects(player.getRect())&&!bTemp.intersects(Temp)){
						b.walkOff();
						//out.println("walkOFF");
						}}
					player.setY(player.getY()+2);
					/*Rectangle test=getBounds();
					test.translate(0,-2);
					move((int)(test.getX()),(int)(test.getY()));*/
					}
				}

			if(letter==' '){//player drops a bomb
				//synchronized(t){
					player.dropBomb();//}
				}
		/*	if(first&&letter==' '){
				first=false;
				t.suspend();
				player.dropBomb(bomb);
				t.resume();
				}*/
			}
		if(player==null){
			if(letter==' '){
				Point x=randSpawnPoint();
				player=new Player((int)(x.getX())*32,(int)(x.getY())*32);
				}
			}

		}

	public void keyTyped(KeyEvent e){}

	public void keyReleased(KeyEvent e){}

}

    public static void main(String[]arg)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new BomberManMain();

			}
		});

		}


}
