package model;

/*
 *hash table para guardar summaries por title.
 */
public class SummaryHashTable {

    private static class Node {
        String key;     
        Summary value;  
        Node next;       

        Node(String key, Summary value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] buckets; 
    private int size;       

    public SummaryHashTable(int capacity) {
        buckets = new Node[capacity];
        size = 0;
    }

    /*
     * Convierte el string en numero y lo mete en un bucket.
     */
    private int hash(String key) {
        int h = 0;
        String k = key.toLowerCase();
        for (int i = 0; i < k.length(); i++) {
            h = 31 * h + k.charAt(i);
        }
        if (h < 0) h = -h; 
        return h % buckets.length;
    }

    public boolean contains(String key) {
        return find(key) != null;
    }

    /*
     * insert un summary en la hash table.
     */
    public void insert(String key, Summary value) {
        int index = hash(key);
        Node current = buckets[index];

        // si ya esta repetido
        while (current != null) {
            if (current.key.equalsIgnoreCase(key)) {
                current.value = value; 
                return;
            }
            current = current.next;
        }


        Node newNode = new Node(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        size++;
    }

    /*
     * Busca un summary por su title.
     */
    public Summary find(String key) {
        int index = hash(key);
        Node current = buckets[index];

        while (current != null) {
            if (current.key.equalsIgnoreCase(key)) {
                return current.value; // encontrado papá
            }
            current = current.next;
        }
        return null; 
    }

    /**
     * Cuantos summaries hay guardados.
     */
    public int getSize() {
        return size;
    }

    /*
     * Saca todo lo que hay en la table y lo mete en array.
     */
    public Summary[] getAll() {
        Summary[] all = new Summary[size];
        int i = 0;

        for (int b = 0; b < buckets.length; b++) {
            Node current = buckets[b];
            while (current != null) {
                all[i] = current.value;
                i++;
                current = current.next;
            }
        }
        return all;
    }

    /**
     * return todos los summaries ordenados por title.
     */
    public Summary[] getAllSortedByTitle() {
        Summary[] all = getAll();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (int i = 0; i < all.length - 1; i++) {
                if (all[i].getTitle().compareToIgnoreCase(all[i + 1].getTitle()) > 0) {
                    Summary tmp = all[i];
                    all[i] = all[i + 1];
                    all[i + 1] = tmp;
                    changed = true; 
                }
            }
        }
        return all;
    }
}
