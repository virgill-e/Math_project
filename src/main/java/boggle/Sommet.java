package boggle;


/**
 * represente un sommet du graph
 * @author virgi
 *
 */
public class Sommet {
	private final char letter;
	private boolean isVisited;

	/**
	 * constructeur du sommet recevant le caractère en param
	 * @param letter
	 */
	public Sommet(char letter) {
		this.letter = letter;
		this.isVisited = false;
	}

	public char getLetter() {
		return letter;
	}

	/**
	 * set le boolean visited
	 * @param value
	 */
	public void setVisited(boolean value) {
		this.isVisited = value;
	}

	/**
	 * renvoie si la case est visité
	 * @return
	 */
	public boolean isVisited() {
		return this.isVisited;
	}
	
	

}
