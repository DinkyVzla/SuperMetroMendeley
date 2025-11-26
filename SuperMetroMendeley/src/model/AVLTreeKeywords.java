package model;

/*
 *@author AlejandroSimanca
 * AVL tree solo para keywords claves.
 */
public class AVLTreeKeywords {

    public static class Node {
        String keyword;  
        Node left;       
        Node right;      
        int height;      

        Node(String keyword) {
            this.keyword = keyword;
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

    //  rotacion simple a la derecha
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);
        return x;
    }

    //rotacion izuqierda
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
    public void insert(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        root = insertRec(root, keyword.toLowerCase());
    }

    // Recursive insert
    private Node insertRec(Node node, String keyword) {
        if (node == null) {
            return new Node(keyword);
        }

        int cmp = keyword.compareTo(node.keyword);
        if (cmp < 0) {
            node.left = insertRec(node.left, keyword);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, keyword);
        } else {
            return node;
        }

        updateHeight(node);
        int bal = balance(node);


        if (bal > 1 && keyword.compareTo(node.left.keyword) < 0)
            return rotateRight(node);


        if (bal < -1 && keyword.compareTo(node.right.keyword) > 0)
            return rotateLeft(node);

        
        if (bal > 1 && keyword.compareTo(node.left.keyword) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

       
        if (bal < -1 && keyword.compareTo(node.right.keyword) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // contar nodos
    private int countNodes(Node n) {
        if (n == null) return 0;
        return 1 + countNodes(n.left) + countNodes(n.right);
    }

    //recorrido inorden para llenar array
    private void fillInOrder(Node n, String[] arr, int[] index) {
        if (n == null) return;
        fillInOrder(n.left, arr, index);
        arr[index[0]] = n.keyword;
        index[0]++;
        fillInOrder(n.right, arr, index);
    }

    // Obtener lista ordenada
    public String[] getKeywordsInOrder() {
        int n = countNodes(root);
        String[] arr = new String[n];
        int[] index = new int[]{0};
        fillInOrder(root, arr, index);
        return arr;
    }
}

