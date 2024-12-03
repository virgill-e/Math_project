package tree;

import java.util.Arrays;

/**
 * class de Node representant le noeud d'un arbre lexicographique
 * @author virgi
 *
 */
public class Node implements Comparable<Node>{
	private final char letter;
	private boolean isFinal;
	private Node[] childs;
	
	/**
	 * constructeur du noeud recevant le caractere en parametre
	 * @param letter
	 */
	public Node(char letter) {
		this.letter=letter;
		childs=new Node[0];
	}
	
	public char getLetter() {
		return this.letter;
	}
	
	/**
	 * renvoit si le noeud est fianl
	 * @return
	 */
	public boolean isFinal() {
		return this.isFinal;
	}
	
	/**
	 * renvoie un tableau des enfants du noeud
	 * @return
	 */
	public Node[] getChilds(){
			return Arrays.copyOf(this.childs, this.childs.length);
	}
	
	/**
	 * set le noeud comme final
	 */
	public void setFinal() {
		this.isFinal=true;
	}

	/**
	 * renvoie le fils du noeud correspondant au char, si aucun enfant correspondant renvoie null
	 * @param character
	 * @return
	 */
	public Node getChild(char character) {
		if(childs.length==0)return null;
		for(Node node:childs) {
			if(node.getLetter()==character)return node;
		}
		return null;
	}
	
	
	
	/**
	 * cree un fils au noeud en recevant un noeud en parametre
	 * @param node
	 */
	public void addChild(Node node) {
		if(getChild(node.getLetter())!=null)return;
		childs=Arrays.copyOf(childs, childs.length+1);
		childs[childs.length-1]=node;
	}
	
	
	
	
	/**
	 * ajoute à partir du noeud actuel
	 * @param word
	 */
	public void addWord(String word) {
	    if (word == null || word.isEmpty()) {
	        return;
	    }
	    char firstChar = word.charAt(0);
	    Node child = getChild(firstChar);
	    if (child == null) {
	    	child=new Node(firstChar);
	        addChild(child);
	    }
	    if (word.length() == 1) {
	        child.setFinal();
	    } else {
	        child.addWord(word.substring(1));
	    }
	}
	
	/**
	 * verifie si a partir du noeud on peut trouver le mot fournit en parmetre
	 * @param word
	 * @return
	 */
	public boolean containsWord(String word) {
	    if (word == null) {
	        return true;
	    }
	    if(word.isEmpty())return false;
	    char firstChar = word.charAt(0);
	    Node child = getChild(firstChar);
	    if (child == null) {
	        return false;
	    }
	    if (word.length() == 1) {
	        return child.isFinal();
	    } else {
	        return child.containsWord(word.substring(1));
	    }
	}

	
	

	
	@Override
	public int compareTo(Node object) {
		return Character.valueOf(object.getLetter()).compareTo(this.getLetter());
	}

	
	
}

