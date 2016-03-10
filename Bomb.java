import java.util.*;
import java.awt.*;
import java.io.*;

//location in 32x32; uses pixels rather than board location
@SuppressWarnings("serial")
public class Bomb implements Serializable{
	private int power;//how far the explosion can reach
	private int timeLeft;//time till explodes
	private int x,y;//location
	private Rectangle rect;
	private boolean canWalk;//whether player can walk on it or not
	private UUID uid;

    public Bomb() {
    }

    public Bomb(int X,int Y,UUID id){
    	x=X;
    	y=Y;
    	rect=new Rectangle(x/32*32,y/32*32,32,32);
		timeLeft=200;
		power=4;
		canWalk=true;
		uid=id;
    	}

    public void walkOff(){
    	canWalk=false;
    	}

    public boolean getWalk(){
    	return canWalk;
    	}

    public Rectangle getRect(){
    	return rect;
    	}
    public int getPower(){
    	return power;
    	}
    public UUID getID(){
    	return uid;
    }

    public boolean update(){
    	//out.println("UPDATING"+timeLeft);
			timeLeft--;
			if(timeLeft<=0){
				 explode();
				 return true;
				}
			return false;

    	}

    public int getTime(){
    	return timeLeft;
    	}

    public void explode(){

		new AePlayWave("../images/bazooka.wav").start();//explosion sound
		System.out.println("Bang");
		timeLeft=0;
    	}

    public boolean equals(Object x){
    	Bomb b=(Bomb)(x);
    	if(b.getPower()==power&&rect.equals(b.getRect())/*&&timeLeft==b.getTime()&&canWalk==b.getWalk()*/){
    		return true;
    		}
    	return false;
    	}


}