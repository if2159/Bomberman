/**
 * @(#)Player.java
 *
 *
 * @author
 * @version 1.00 4/7/2015
 */
import java.util.*;
import java.awt.*;
import java.io.*;


@SuppressWarnings("serial")
public class Player implements Serializable{
	private int x,y;
	private int score;
	private Rectangle rect;
	private Color color;
//	private BufferedImage image;
	private int bombCount, bombMax;
	private ArrayList<Bomb> bombList;
	private ArrayList<Explosion> explodeList;
	private int power;
	private boolean dead;
	private int lives;
	private UUID id;


    public Player() {
    }

    public Player(int X, int Y,UUID i) {
    	x=X;
    	y=Y;
    	rect=new Rectangle(x+4,y+3,32-4-5,29-3-4);
    	bombCount=0;
    	bombMax=6;
    	bombList=new ArrayList<Bomb>();
    	explodeList=new ArrayList<Explosion>();
    	power=1;
    //	image=img;
    	dead=false;
    	lives=3;
    	bombList.add(new Bomb());// DO NOT REMOVE THESE TWO LINES
    	bombList.remove(0);//WILL BREAK BOMB DROPPING; SOMETHING TO DO WITH THREAD SYNCHRONIZATION
    	id=i;
    }

    public ArrayList<Bomb> getBombs(){
    	ArrayList<Bomb> temp=new ArrayList<Bomb>(bombList);
   		bombList=new ArrayList<Bomb>();
    	return temp;
    }
    public ArrayList<Explosion> getExplosions(){
    	ArrayList<Explosion> temp=explodeList;
    	explodeList=new ArrayList<Explosion>();
    	return temp;


    	}
    public Rectangle getRect(){
    	return rect;
    	}

    public void setX(int X){
    	x=X;
    	rect.setLocation(x+4,y+3);
    	}
    public void setY(int Y){
    	y=Y;
    	rect.setLocation(x+4,y+3);
    	}
    
    public UUID getID(){return id;}
    public void setID(UUID i){id=i;}
    
    public void decBomb(){
    	if(bombCount!=0)
    		bombCount--;
    	if(bombCount<=0){
    		bombCount=0;
    		}

    	}

	public void update(){
		for(int i=0;i<bombList.size();i++){
			if(bombList.get(i).update()){
				explodeList.add(new Explosion(bombList.remove(0)));
				bombCount--;
				}
			}
		for(int i=0;i<explodeList.size();i++){
			if(explodeList.get(i).update()){
				explodeList.remove(0);
				}
			}

		}

   /* public BufferedImage getImage(){
    	return image;
    	}
*/
    public int getX(){
    	return x;}

	public int getY(){
		return y;}

	public void dropBomb(){
			if(bombCount<bombMax){
				bombCount++;
				bombList.add(new Bomb(x+16,y+16,id));
				}
		}
	public void explode(){

		}

	@Deprecated
	public void die(){
		bombCount=bombMax=1;
		this.setX(-80);
		this.setY(-80);
		lives--;
		if(lives<=0){
			dead=true;
			}
		}

}