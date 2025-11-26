package model;

/*
 *@author JuanFerreira
 * Lista enlazada para el save del summary
 */
public class SummaryList {


    private static class Node {
        Summary data;  // el summary save
        Node next;     

        Node(Summary s) {
            this.data = s; 
        }
    }

    private Node head; // primer nodo de la lista
    private int size;

    /*
     * agrega el summary 
     */
    public void addIfNotExists(Summary s) {
        if (contains(s)) return; 

        Node newNode = new Node(s);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /*
     * revisa si la lista ya tiene ese summary, compara referencia
     */
    public boolean contains(Summary s) {
        Node current = head;
        while (current != null) {
            if (current.data == s) return true; 
            current = current.next; 
        }
        return false;
    }

    /*
     * Devuelve el size
     */
    public int getSize() {
        return size;
    }

    /**
     * pasa la lista a un array para el GUI.
     */
    public Summary[] toArray() {
        Summary[] arr = new Summary[size];
        Node current = head;
        int i = 0;

        while (current != null) {
            arr[i] = current.data; //summary en el array
            i++;
            current = current.next;
        }
        return arr;
    }
}
