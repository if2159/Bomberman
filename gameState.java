/**
 * @(#)gameState.java
 * This class is used for transferring game over network
 *
 * @author Ian Fennen
 * @version 1.00 2015/5/7
 */
import java.util.*;
import java.io.*;


@SuppressWarnings("serial")
public class gameState implements Serializable{
	ArrayList<Bomb> bombList;
	ArrayList<Explosion> explosionList;
	Player player1, player2;
	block field[][];


	public gameState(ArrayList<Bomb> bL,ArrayList<Explosion> eL,Player p1,block f[][]) {
		bombList=bL;
		explosionList=eL;
		player1=p1;
		field=f;
		player2 = null;

    }
    public gameState(ArrayList<Bomb> bL,ArrayList<Explosion> eL,Player p1,block f[][], Player p2) {
		bombList=bL;
		explosionList=eL;
		player1=p1;
		field=f;
		player2 = p2;
    }
    public gameState() {

    }

    public gameState(gameState g) {
		bombList=g.getBomb();
		explosionList=g.getExplosions();
		player1=g.getPlayer1();
		field=g.getField();
		player2 = g.getPlayer2();
    }

	public ArrayList<Bomb> getBomb(){
		return bombList;
		}
	public void setBomb( ArrayList<Bomb> a){
		bombList=a;
	}
	public void addBomb( ArrayList<Bomb> a){
		bombList.addAll(a);
	}

	public ArrayList<Explosion> getExplosions(){
		return explosionList;
		}
	public void setExplosions(ArrayList<Explosion> a){
		explosionList=a;
	}

	public Player getPlayer1(){
		return player1;
		}
	public void setPlayer1(Player p){
		player1 = p;
	}
	
	public Player getPlayer2(){
		return player2;
		}
	public void setPlayer2(Player p){
		player2 = p;
	}
	
	public block[][] getField(){
		return field;
		}
	public void setField(block f[][]){
		field=f;
	}

	public String toString(){
		return "this is a gameState!";
		}
	


	public boolean equals(Object o){
		gameState x=(gameState)o;
		if(bombList.equals(x.getBomb())&&explosionList.equals(x.getExplosions())&&player1.equals(x.getPlayer1())&&Arrays.equals(field,x.getField())&&player2.equals(x.getPlayer2())){
			return true;
			}
		return false;

		}


}