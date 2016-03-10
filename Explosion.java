import java.util.*;
import java.awt.*;
import java.io.*;


public class Explosion implements Serializable{
	private int power;
	private Rectangle rect;
	private int timer;
	private boolean checked;
	private ArrayList<Rectangle> spread;// where the fire for the explosion covers


    public Explosion(int p,int x, int y) {
    	rect=new Rectangle(x,y,32,32);
    	timer=66;
    	checked=false;
    	power=p;
    	spread=new ArrayList<Rectangle>();
    }
	public Explosion(Bomb b){
		power=b.getPower();
		rect=b.getRect();
		timer=66;
		checked=false;
		spread=new ArrayList<Rectangle>();
		spread.add(rect);
		}

	public ArrayList<Rectangle> getSpread(){
		return spread;
		}

	public void setSpread(ArrayList<Rectangle> e){
		spread=e;
		}

	public void addSpread(int x, int y){
		spread.add(new Rectangle(x,y,32,32));
		}
	public void addSpread(ArrayList<Rectangle> r){
		spread.addAll(r);
		}

	public boolean getChecked(){
		return checked;
		}
	public void check(){
		checked=true;
		}
	public int getTime(){
		return timer;
		}
	public void setTime(int t){
		timer=t;
		}
	public Rectangle getRect(){
    	return rect;
    	}
    public int getPower(){
    	return power;
    	}

	public boolean update(){//count down till explosion disappears
		timer--;
		if(timer<=0){
			return true;
			}
		return false;
		}




	public boolean equals(Object x){
    	Explosion e=(Explosion)(x);
    	if(e.getPower()==power&&e.getRect().equals(rect)&&timer==e.getTime()&&e.getSpread().equals(spread)){
    		return true;
    		}
    	return false;

	}

}