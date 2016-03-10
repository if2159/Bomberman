import java.util.*;
import java.awt.*;
import java.io.*;
//location in r,c max value is 25,25

public class block implements Serializable {
	private Rectangle rect;
	private int image;// 1==grass 2==bricks 3==Column
	private boolean checked;
	public boolean test;
	private boolean destroy;

    public block(int x, int y, int img,boolean d) {
	rect=new Rectangle(x*32,y*32,32,32);
	image=img;
	checked=false;//used to check explosion spread
	destroy=d;//not used

    }


    public boolean getDestroy(){
    	return destroy;
    	}
    public void setDestroy(boolean b){
			destroy=b;

    	}

    public Rectangle getRect(){
    	return rect;
    	}
    public int getImage(){
    	return image;
    	}
    public void setImage(int img){
    	image=img;
    	}
	public boolean getChecked(){
		return checked;
		}
	public void check(){
		checked=true;
		}
}