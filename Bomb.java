import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import static java.lang.System.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.net.*;
//location in 32x32
public class Bomb implements Serializable{
	private int power;//how far the explosion can reach
	private int timeLeft;//time till explodes
	private int x,y;//location
	private Rectangle rect;
	private boolean canWalk;//whether player can walk on it or not


    public Bomb() {
    }

    public Bomb(int X,int Y){
    	x=X;
    	y=Y;
    	rect=new Rectangle(x/32*32,y/32*32,32,32);
		timeLeft=200;
		power=4;
		canWalk=true;
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

	//	new AePlayWave("bazooka.wav").start();//explosion sound
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