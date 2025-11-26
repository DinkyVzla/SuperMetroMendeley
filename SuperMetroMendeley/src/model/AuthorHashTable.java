package model;

/*
 *@author AlejandroSimanca
 * hash table author -> list of summaries.
 */
public class AuthorHashTable {

    private static class Node {
        String author;        
        SummaryList list;     
        Node next;

        Node(String author) {
            this.author = author;
            this.list = new SummaryList();
        }
    }

    private Node[] buckets;
    private int capacity;

    public AuthorHashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new Node[capacity];
    }

    private int hash(String author) {
        int h = 0;
        String a = author.toLowerCase();
        for (int i = 0; i < a.length(); i++) {
            h = 31 * h + a.charAt(i);
        }
        if (h < 0) h = -h;
        return h % capacity;
    }

    private Node findNode(String author) {
        int index = hash(author);
        Node current = buckets[index];
        while (current != null) {
            if (current.author.equalsIgnoreCase(author)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }


    public void addSummary(String author, Summary summary) {
        int index = hash(author);
        Node node = findNode(author);

        if (node == null) {
            node = new Node(author);
            node.next = buckets[index];
            buckets[index] = node;
        }

        node.list.addIfNotExists(summary);
    }

  
    public SummaryList getList(String author) {
        Node node = findNode(author);
        if (node == null) return null;
        return node.list;
    }
}
