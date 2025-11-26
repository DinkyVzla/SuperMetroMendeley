package ui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

//*@author AlejandroSimanca

public class MainWindow extends JFrame {

    // Main hash table by title / Hash principal by title
    private SummaryHashTable summaryTable;
    
    private SummaryList allSummaries;

    // Hash + AVL for keywords and authors
    private KeywordHashTable keywordTable;
    private AuthorHashTable authorTable;
    private AVLTreeKeywords keywordAVL;
    private AVLTreeAuthors authorAVL;

    public MainWindow() {
        super("SuperMetroMendeley");

        summaryTable = new SummaryHashTable(101);
        allSummaries = new SummaryList();

        keywordTable = new KeywordHashTable(101);
        authorTable = new AuthorHashTable(101);
        keywordAVL = new AVLTreeKeywords();
        authorAVL = new AVLTreeAuthors();

        preloadSummaries();   
        loadSavedData();      

        buildUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 400);
        setLocationRelativeTo(null);
    }

    // Build buttons / Construir botones
    private void buildUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton btnAdd = new JButton("Add summary / Agregar resumen");
        JButton btnAnalyze = new JButton("Analyze summary / Analizar resumen");
        JButton btnByKeyword = new JButton("Search by keyword / Buscar por palabra");
        JButton btnByAuthor = new JButton("Search by author / Buscar por autor");
        JButton btnKeywordsList = new JButton("Keyword list / Lista palabras");
        JButton btnSaveExit = new JButton("Save and exit / Guardar y salir");

        btnAdd.addActionListener(this::onAddSummary);
        btnAnalyze.addActionListener(this::onAnalyzeSummary);
        btnByKeyword.addActionListener(this::onSearchByKeyword);
        btnByAuthor.addActionListener(this::onSearchByAuthor);
        btnKeywordsList.addActionListener(this::onKeywordsList);
        btnSaveExit.addActionListener(e -> {
            saveData();
            System.exit(0);
        });

        panel.add(btnAdd);
        panel.add(btnAnalyze);
        panel.add(btnByKeyword);
        panel.add(btnByAuthor);
        panel.add(btnKeywordsList);
        panel.add(btnSaveExit);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    // BUTTON ACTIONS 

    private void onAddSummary(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try {
            Summary s = readSummaryFromFile(file);

            if (summaryTable.contains(s.getTitle())) {
                JOptionPane.showMessageDialog(this,
                        "This title already exists. / Título repetido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addSummaryToStructures(s);

            JOptionPane.showMessageDialog(this,
                    "Summary added correctly. / Resumen agregado.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "File error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAnalyzeSummary(ActionEvent e) {
        if (summaryTable.getSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No summaries loaded. / No hay resúmenes.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Summary[] arr = allSummaries.toArray();
        // Simple bubble sort by title / Orden burbuja por título
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < arr.length - 1; i++) {
                if (arr[i].getTitle().compareToIgnoreCase(arr[i + 1].getTitle()) > 0) {
                    Summary tmp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = tmp;
                    changed = true;
                }
            }
        }

        String[] titles = new String[arr.length];
        for (int i = 0; i < arr.length; i++) titles[i] = arr[i].getTitle();

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select a summary / Seleccione resumen:",
                "Analyze / Analizar",
                JOptionPane.PLAIN_MESSAGE,
                null,
                titles,
                titles[0]
        );

        if (chosen == null) return;

        Summary s = summaryTable.find(chosen);
        if (s == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Title / Título: ").append(s.getTitle()).append("\n\n");

        sb.append("Authors / Autores:\n");
        String[] authors = s.getAuthors();
        for (int i = 0; i < authors.length; i++) {
            sb.append("- ").append(authors[i]).append("\n");
        }

        sb.append("\nKeyword frequencies / Frecuencias:\n");
        String[] kws = s.getKeywords();
        int[] freqs = s.getFrequencies();
        for (int i = 0; i < kws.length; i++) {
            sb.append(kws[i]).append(": ").append(freqs[i]).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Summary analysis / Análisis", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onSearchByKeyword(ActionEvent e) {
        String[] keywords = keywordAVL.getKeywordsInOrder();
        if (keywords.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No keywords stored. / No hay palabras clave.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose a keyword / Elija una palabra:",
                "Search by keyword / Buscar por palabra",
                JOptionPane.PLAIN_MESSAGE,
                null,
                keywords,
                keywords[0]
        );

        if (chosen == null) return;

        SummaryList list = keywordTable.getList(chosen);
        if (list == null || list.getSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No summaries for that keyword. / No hay resúmenes.",
                    "Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Summary[] res = list.toArray();
        StringBuilder sb = new StringBuilder();
        sb.append("Keyword: ").append(chosen)
          .append(" / Palabra clave\n\n");

        // For each summary, show title and its frequency / título + frecuencia
        for (int i = 0; i < res.length; i++) {
            Summary s = res[i];
            int freq = s.getFrequencyOf(chosen);
            sb.append("- ").append(s.getTitle())
              .append("  (freq: ").append(freq).append(")\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Results / Resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onSearchByAuthor(ActionEvent e) {
        String[] authors = authorAVL.getAuthorsInOrder();
        if (authors.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No authors stored. / No hay autores.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose an author / Elija autor:",
                "Search by author / Buscar por autor",
                JOptionPane.PLAIN_MESSAGE,
                null,
                authors,
                authors[0]
        );

        if (chosen == null) return;

        SummaryList list = authorTable.getList(chosen);
        if (list == null || list.getSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No summaries for that author. / No hay resúmenes.",
                    "Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Summary[] res = list.toArray();
        StringBuilder sb = new StringBuilder();
        sb.append("Author: ").append(chosen)
          .append(" / Autor\n\n");

        for (int i = 0; i < res.length; i++) {
            sb.append("- ").append(res[i].getTitle()).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Results / Resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onKeywordsList(ActionEvent e) {
        String[] keywords = keywordAVL.getKeywordsInOrder();
        if (keywords.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No keywords stored. / No hay palabras clave.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Keywords / Palabras clave:\n\n");
        for (int i = 0; i < keywords.length; i++) {
            sb.append("- ").append(keywords[i]).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Keywords / Palabras", JOptionPane.INFORMATION_MESSAGE);
    }

    //CORE HELPERS

    // Insert summary en todas las estructuras
    private void addSummaryToStructures(Summary s) {
        summaryTable.insert(s.getTitle(), s);
        allSummaries.addIfNotExists(s);

        // Authors structures
        String[] authors = s.getAuthors();
        for (int i = 0; i < authors.length; i++) {
            String a = authors[i].trim();
            if (!a.isEmpty()) {
                authorTable.addSummary(a, s);
                authorAVL.insert(a);
            }
        }

        // Keywords structures
        String[] kws = s.getKeywords();
        for (int i = 0; i < kws.length; i++) {
            String k = kws[i].trim();
            if (!k.isEmpty()) {
                keywordTable.addSummary(k, s);
                keywordAVL.insert(k);
            }
        }
    }

    /*
     * Read summary from file
     */
    private Summary readSummaryFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        String title = null;
        String[] authors = new String[20];
        int authorCount = 0;

        StringBuilder body = new StringBuilder();

        String[] keywords = new String[20];
        int keywordCount = 0;

        int state = 0;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (title == null) {
                title = line;
                continue;
            }

            //Authors
            if (line.equalsIgnoreCase("Autores") ||
                line.equalsIgnoreCase("Authors")) {
                state = 1;
                continue;
            }

            //Summary
            if (line.equalsIgnoreCase("Resumen") ||
                line.equalsIgnoreCase("Summary")) {
                state = 2;
                continue;
            }

            if (state == 1) {
                if (authorCount < authors.length) {
                    authors[authorCount++] = line;
                }
                continue;
            }

            if (state == 2) {
                String lower = line.toLowerCase();
                // palabras claves
                if (lower.startsWith("palabras clave") ||
                    lower.startsWith("palabras claves") ||
                    lower.startsWith("keywords")) {

                    int idx = line.indexOf(":");
                    if (idx != -1) {
                        String rest = line.substring(idx + 1);
                        String[] parts = rest.split(",");
                        for (int i = 0; i < parts.length && keywordCount < keywords.length; i++) {
                            keywords[keywordCount++] = parts[i].trim();
                        }
                    }
                    break; // end of summary / fin del resumen
                } else {
                    body.append(line).append(" ");
                }
            }
        }
        br.close();

        // shrink arrays / achicar arreglos
        String[] finalAuthors = new String[authorCount];
        for (int i = 0; i < authorCount; i++) finalAuthors[i] = authors[i];

        String[] finalKeywords = new String[keywordCount];
        for (int i = 0; i < keywordCount; i++) finalKeywords[i] = keywords[i];

        return new Summary(title, finalAuthors, body.toString(), finalKeywords);
    }

    // ------------------ PRELOAD / PRECARGA ------------------ //

    // Very small preload / Precarga muy sencilla
    private void preloadSummaries() {
        Summary s1 = new Summary(
                "Interacción inalámbrica con dispositivos de bajo costo",
                new String[]{"Rhadamés Carmona", "Marcos Ramírez"},
                "Short demo text about interaction and virtual reality.",
                new String[]{"interacción humano-computador", "realidad virtual"}
        );

        Summary s2 = new Summary(
                "GraphQL vs REST",
                new String[]{"Christian Guillén Drija", "Reynaldo Quintero"},
                "Short demo text about REST and GraphQL performance.",
                new String[]{"REST", "GraphQL", "calidad de software"}
        );

        if (!summaryTable.contains(s1.getTitle())) addSummaryToStructures(s1);
        if (!summaryTable.contains(s2.getTitle())) addSummaryToStructures(s2);
    }

    //SAVE-LOAD

    // Save en txt file
    private void saveData() {
        File file = new File("summaries_data.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            Summary[] arr = allSummaries.toArray();
            for (int i = 0; i < arr.length; i++) {
                Summary s = arr[i];
                pw.println(s.getTitle());
                pw.println("Autores");
                String[] authors = s.getAuthors();
                for (int j = 0; j < authors.length; j++) {
                    pw.println(authors[j]);
                }
                pw.println("Resumen");
                pw.println(s.getBody());
                pw.print("Palabras claves: ");
                String[] kws = s.getKeywords();
                for (int j = 0; j < kws.length; j++) {
                    pw.print(kws[j]);
                    if (j < kws.length - 1) pw.print(", ");
                }
                pw.println();
                pw.println("---"); // separator / separador
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Save error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load from text file / Cargar desde archivo de texto
    private void loadSavedData() {
        File file = new File("summaries_data.txt");
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder block = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (line.trim().equals("---")) {
                    File temp = File.createTempFile("summary_tmp", ".txt");
                    try (PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
                        pw.print(block.toString());
                    }
                    Summary s = readSummaryFromFile(temp);
                    if (!summaryTable.contains(s.getTitle())) {
                        addSummaryToStructures(s);
                    }
                    block.setLength(0);
                    temp.delete();
                } else {
                    block.append(line).append("\n");
                }
            }

            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Load error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

