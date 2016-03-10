/**
 * @(#)gameState.java
 * This class is used for transferring game over network
 *
 * @author Ian Fennen
 * @version 1.00 2015/5/7
 */
import java.util.*;
import java.io.*;



public class gameState implements Serializable{
	ArrayList<Bomb> bombList;
	ArrayList<Explosion> explosionList;
	Player player;
	block field[][];


    public gameState(ArrayList<Bomb> bL,ArrayList<Explosion> eL,Player p,block f[][]) {
		bombList=bL;
		explosionList=eL;
		player=p;
		field=f;
    }
    public gameState() {

    }

    public gameState(gameState g) {
		bombList=g.getBomb();
		explosionList=g.getExplosions();
		player=g.getPlayer();
		field=g.getField();
    }

    /*public gameState merge(gameState gs){
    	bombList.addAll(gs.getBomb());
    	explosionList.addAll(gs.getExplosions());



    	}
*/
	public ArrayList<Bomb> getBomb(){
		return bombList;
		}

	public ArrayList<Explosion> getExplosions(){
		return explosionList;
		}

	public Player getPlayer(){
		return player;
		}

	public block[][] getField(){
		return field;
		}

	public String toString(){
		return "this is a gameState!";
		}


	public boolean equals(Object o){
		gameState x=(gameState)o;
		if(bombList.equals(x.getBomb())&&explosionList.equals(x.getExplosions())&&player.equals(x.getPlayer())&&Arrays.equals(field,x.getField())){
			return true;
			}
		return false;

		}


}