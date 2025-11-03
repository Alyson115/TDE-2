package com.example.demo3;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TreeVisualizer extends Application {

    // Classe Node
    static class Node {
        char letter;
        Node left;
        Node right;

        public Node(char letter) {
            this.letter = letter;
            this.left = null;
            this.right = null;
        }
    }

    // Classe da árvore binária
    static class MorseBST {
        private Node root;
        private Map<Character, String> morseMap = new HashMap<>();

        public MorseBST() {
            root = new Node(' ');
            buildMorseAlphabet();
        }

        // Constrói o alfabeto Morse completo
        private void buildMorseAlphabet() {
            // Letras
            insert('A', ".-");
            insert('B', "-...");
            insert('C', "-.-.");
            insert('D', "-..");
            insert('E', ".");
            insert('F', "..-.");
            insert('G', "--.");
            insert('H', "....");
            insert('I', "..");
            insert('J', ".---");
            insert('K', "-.-");
            insert('L', ".-..");
            insert('M', "--");
            insert('N', "-.");
            insert('O', "---");
            insert('P', ".--.");
            insert('Q', "--.-");
            insert('R', ".-.");
            insert('S', "...");
            insert('T', "-");
            insert('U', "..-");
            insert('V', "...-");
            insert('W', ".--");
            insert('X', "-..-");
            insert('Y', "-.--");
            insert('Z', "--..");

            // Números
            insert('1', ".----");
            insert('2', "..---");
            insert('3', "...--");
            insert('4', "....-");
            insert('5', ".....");
            insert('6', "-....");
            insert('7', "--...");
            insert('8', "---..");
            insert('9', "----.");
            insert('0', "-----");
        }

        // Insere letras e seus códigos na árvore
        public void insert(char letter, String morseCode) {
            Node current = root;
            for (int i = 0; i < morseCode.length(); i++) {
                char symbol = morseCode.charAt(i);

                if (symbol == '.') {
                    if (current.left == null)
                        current.left = new Node(' ');
                    current = current.left;
                } else if (symbol == '-') {
                    if (current.right == null)
                        current.right = new Node(' ');
                    current = current.right;
                }
            }
            current.letter = letter;
            morseMap.put(letter, morseCode);
        }

        // Decodifica uma mensagem Morse
        public String decode(String morseMessage) {
            StringBuilder result = new StringBuilder();
            String[] letras = morseMessage.trim().split(" ");
            for (String codigo : letras) {
                result.append(decodeLetter(codigo));
            }
            return result.toString();
        }

        private char decodeLetter(String morseCode) {
            Node current = root;
            for (char symbol : morseCode.toCharArray()) {
                if (symbol == '.') current = current.left;
                else if (symbol == '-') current = current.right;
                if (current == null) return '?';
            }
            return current.letter;
        }

        // Retorna o caminho Morse de uma letra
        public String getPath(char letter) {
            return morseMap.getOrDefault(Character.toUpperCase(letter), "?");
        }

        // Altura
        public int getHeight() {
            return getHeight(root);
        }

        private int getHeight(Node node) {
            if (node == null) {
                return 0;
            }
            return 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }

        // Desenha a árvore
        public void drawTree(Canvas canvas) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            drawNode(gc, root, canvas.getWidth() / 2, 40, canvas.getWidth() / 4);
        }

        private void drawNode(GraphicsContext gc, Node node, double x, double y, double xOffset) {
            if (node == null) return;

            gc.setStroke(Color.BLACK);
            gc.strokeOval(x - 15, y - 15, 30, 30);

            if (node.letter != ' ')
                gc.strokeText(String.valueOf(node.letter), x - 5, y + 5);

            if (node.left != null) {
                double newX = x - xOffset;
                double newY = y + 80;
                gc.strokeLine(x, y + 15, newX, newY - 15);
                drawNode(gc, node.left, newX, newY, xOffset / 2);
            }

            if (node.right != null) {
                double newX = x + xOffset;
                double newY = y + 80;
                gc.strokeLine(x, y + 15, newX, newY - 15);
                drawNode(gc, node.right, newX, newY, xOffset / 2);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Visualizador de Árvore Binária - Código Morse");

        MorseBST bst = new MorseBST();

        // Entrada do usuário
        TextInputDialog dialog = new TextInputDialog(".... . .-.. .-.. ---");
        dialog.setTitle("Entrada de Código Morse");
        dialog.setHeaderText("Digite o código Morse (separe as letras com espaço)");
        dialog.setContentText("Exemplo: .... . .-.. .-.. ---");
        Optional<String> input = dialog.showAndWait();

        if (input.isEmpty() || input.get().trim().isEmpty()) {
            System.out.println("Nenhum código morse fornecido.");
            return;
        }

        String morseMessage = input.get().trim();
        String decoded = bst.decode(morseMessage);

        // Mostra no console também
        System.out.println("Mensagem decodificada: " + decoded);

        // Desenho da árvore
        int height = bst.getHeight();
        int canvasHeight = 200 + height * 80;
        int canvasWidth = 1200;

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        bst.drawTree(canvas);

        // Escrever a palavra decodificada e os caminhos na tela
        gc.setFill(Color.DARKBLUE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Mensagem decodificada: " + decoded, 40, canvasHeight - 60);

        gc.setFill(Color.DARKGREEN);
        gc.setFont(new Font("Arial", 16));

        StringBuilder caminhos = new StringBuilder("Caminhos dos nós: ");
        for (char c : decoded.toCharArray()) {
            caminhos.append(c).append(" (").append(bst.getPath(c)).append(")  ");
        }

        gc.fillText(caminhos.toString(), 40, canvasHeight - 30);

        // Mostra na tela
        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
