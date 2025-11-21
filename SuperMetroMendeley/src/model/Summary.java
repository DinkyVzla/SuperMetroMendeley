package model;

/*
 * save un summary de investigacion.
 * O sea, el "resumen" con title, authors, body y keywords
 */
public class Summary {

    private String title;

    private String[] authors;

    private String body;

    private String[] keywords;

    private int[] frequencies;

    /*
     * Constr crea un Summary con todo del txt.
     * y save de las frecuencias
     */
    public Summary(String title, String[] authors, String body, String[] keywords) {
        this.title = title;
        this.authors = authors;
        this.body = body;
        this.keywords = keywords;
        
        this.frequencies = new int[keywords.length];

        calculateFrequencies();
    }

    /*
     * hace un search keyword por keyword y cuenta cuantas veces sale en el body.
     */
    private void calculateFrequencies() {
        String text = body.toLowerCase();

        for (int i = 0; i < keywords.length; i++) {
            String kw = keywords[i].trim().toLowerCase();
            frequencies[i] = countOccurrences(text, kw);
        }
    }

    /*
     * contador para palabras dentro de un text.
     */
    private int countOccurrences(String text, String word) {
        if (word.isEmpty()) return 0; // si la palabra ta vacia no vale

        int count = 0;
        int pos = text.indexOf(word);

        // suma 1
        while (pos != -1) {
            count++;
            pos = text.indexOf(word, pos + word.length());
        }

        return count;
    }

    public String getTitle() {
        return title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getBody() {
        return body;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    /*
     * conteo de una keyword especifica.
     */
    public int getFrequencyOf(String keyword) {
        String target = keyword.toLowerCase();

        for (int i = 0; i < keywords.length; i++) {
            if (keywords[i].trim().toLowerCase().equals(target)) {
                return frequencies[i];
            }
        }

        return 0; 
    }
}
