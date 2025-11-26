package model;

/*
 * arbol AVL solo para autores.
 */
public class AVLTreeAuthors {

    public static class Node {
        String author;  // nombre autor
        Node left;
        Node right;
        int height;

        Node(String author) {
            this.author = author;
            this.height = 1;
        }
    }

    private Node root;

    public Node getRoot() {
        return root;
    }

    private int height(Node n) {
        return (n == null) ? 0 : n.height;
    }

    private int balance(Node n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }

    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Public insert
    public void insert(String author) {
        if (author == null || author.trim().isEmpty()) return;
        root = insertRec(root, author.toLowerCase());
    }

    private Node insertRec(Node node, String author) {
        if (node == null) return new Node(author);

        int cmp = author.compareTo(node.author);
        if (cmp < 0) {
            node.left = insertRec(node.left, author);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, author);
        } else {
            // already exists / ya existe
            return node;
        }

        updateHeight(node);
        int bal = balance(node);

        if (bal > 1 && author.compareTo(node.left.author) < 0)
            return rotateRight(node);

        if (bal < -1 && author.compareTo(node.right.author) > 0)
            return rotateLeft(node);

        if (bal > 1 && author.compareTo(node.left.author) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (bal < -1 && author.compareTo(node.right.author) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private int countNodes(Node n) {
        if (n == null) return 0;
        return 1 + countNodes(n.left) + countNodes(n.right);
    }

    private void fillInOrder(Node n, String[] arr, int[] index) {
        if (n == null) return;
        fillInOrder(n.left, arr, index);
        arr[index[0]] = n.author;
        index[0]++;
        fillInOrder(n.right, arr, index);
    }

    //List de autores ordenada
    public String[] getAuthorsInOrder() {
        int n = countNodes(root);
        String[] arr = new String[n];
        int[] index = new int[]{0};
        fillInOrder(root, arr, index);
        return arr;
    }
}
