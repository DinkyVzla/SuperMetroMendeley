package model;

/*
 * hash table keyword -> lista d summaries.
 */
public class KeywordHashTable {

    //nodo simple para chaining
    private static class Node {
        String keyword;        
        SummaryList list;      
        Node next;             

        Node(String keyword) {
            this.keyword = keyword;
            this.list = new SummaryList();
        }
    }

    private Node[] buckets;    // arreglo de buckets
    private int capacity;      

    public KeywordHashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new Node[capacity];
    }

    //hash simple por caracteres
    private int hash(String keyword) {
        int h = 0;
        String k = keyword.toLowerCase();
        for (int i = 0; i < k.length(); i++) {
            h = 31 * h + k.charAt(i);
        }
        if (h < 0) h = -h;
        return h % capacity;
    }

    // find nodo por keyword
    private Node findNode(String keyword) {
        int index = hash(keyword);
        Node current = buckets[index];
        while (current != null) {
            if (current.keyword.equalsIgnoreCase(keyword)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // agregar summary a una keyword
    public void addSummary(String keyword, Summary summary) {
        int index = hash(keyword);
        Node node = findNode(keyword);

        if (node == null) {
            node = new Node(keyword);
            node.next = buckets[index];
            buckets[index] = node;
        }

        //no repite el resumen en la lista
        node.list.addIfNotExists(summary);
    }

    // Get list de summaries por keyword
    public SummaryList getList(String keyword) {
        Node node = findNode(keyword);
        if (node == null) return null;
        return node.list;
    }
}
