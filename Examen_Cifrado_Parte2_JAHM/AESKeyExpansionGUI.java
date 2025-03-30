import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Implementación de cifrado y expansión de claves AES con interfaz gráfica de usuario
 * Basado en las especificaciones FIPS-197
 * @author José Angel Hernández Morales
 * @version 3.0
 */
public class AESKeyExpansionGUI extends JFrame {
    
    // Parámetros básicos de AES
    private static final int AES_128_NK = 4;    // Longitud de clave en palabras de 32 bits
    private static final int AES_128_NR = 10;   // Numbero de rondas
    private static final int AES_128_NB = 4;    // Tamaño de bloque en palabras de 32 bits
    
    // Cambio de colores para la GUI, para que se vea más estetico
    private static final Color HEADER_BG = new Color(25, 52, 70);       // Azul oscuro
    private static final Color HEADER_FG = new Color(240, 240, 240);    // Blanco hueso
    private static final Color ALTERNATE_ROW = new Color(45, 62, 80);   // Azul grisáceo
    private static final Color HIGHLIGHT_CELL = new Color(39, 55, 77);  // Azul marino
    private static final Color RCON_ROW_COLOR = new Color(32, 58, 67);  // Azul petróleo
    private static final Color BUTTON_BG = new Color(44, 57, 75);       // Azul oscuro
    private static final Color BUTTON_FG = new Color(220, 220, 220);    // Gris claro
    private static final Color BUTTON_BORDER = new Color(60, 76, 101);  // Azul acero
    private static final Color SUCCESS_COLOR = new Color(39, 78, 19);   // Verde militar
    
    // Implementación de S-box para la transformación de SubWord.
    private static final int[] SBOX = {
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };
    
    // Matriz de constantes redondeadas (Rcon) para el proceso de expansión de claves.
    private static final int[] RCON = {
        0x01000000, 0x02000000, 0x04000000, 0x08000000, 0x10000000, 
        0x20000000, 0x40000000, 0x80000000, 0x1b000000, 0x36000000
    };
    
    // componentes GUI 
    private JTable expansionTable;
    private DefaultTableModel tableModel;
    private JTextField keyInputField;
    private JTextField plaintextInputField;
    private JTextArea initialKeyArea;
    private JButton processButton;
    private JButton encryptButton;
    private JButton randomKeyButton;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel infoPanel;
    private JPanel encryptionPanel;
    private JPanel encryptionStepsPanel;
    private JScrollPane encryptionScrollPane;
    private JTextArea resultArea;
    
    // Almacenamiento de resultados
    private int[] w;                     // Expanded key words
    private String[] tempValues;         // Intermediate temp values
    private String[] afterRotWord;       // After RotWord operation
    private String[] afterSubWord;       // After SubWord operation
    private String[] rconValues;         // Rcon values used
    private String[] afterRcon;          // After XOR with Rcon
    private String[] wMinusNk;           // w[i-Nk] values
    private String[] wFinal;             // Final w[i] values
    private byte[][] roundKeys;          // Round keys for encryption
    
    /**
     * Constructor: inicialización y configuración los componentes de la GUI
     */
    public AESKeyExpansionGUI() {
        // Configure the main frame
        super("Advanced AES-128 Key Expansion and Encryption Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        initializeComponents();
        layoutComponents();
        registerListeners();
        
        // Establecer una clave predeterminada y texto sin formato para la demostración
        keyInputField.setText("2b7e151628aed2a6abf7158809cf4f3c");
        plaintextInputField.setText("3243f6a8885a308d313198a2e0370734");
    }
    
    /**
     * Inicializar todos los componentes de la interfaz
     */
    private void initializeComponents() {
        // Crear una tabla con encabezados de columna
        String[] columnNames = {
            "i (dec)", "temp", "After RotWord()", "After SubWord()", 
            "Rcon[i/Nk]", "After XOR with Rcon", "w[i-Nk]", "w[i]=w[i-Nk]⊕temp"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla sea de sólo lectura
            }
        };
        
        expansionTable = new JTable(tableModel);
        expansionTable.setFillsViewportHeight(true);
        expansionTable.setRowHeight(30);
        expansionTable.setFont(new Font("Monospaced", Font.PLAIN, 14));
        expansionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        expansionTable.getTableHeader().setBackground(HEADER_BG);
        expansionTable.getTableHeader().setForeground(HEADER_FG);
        
        // Alinear al centro todas las celdas con esquema de los nuevos colores
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Coloración personalizada para filas que usan Rcon
                if (row % AES_128_NK == 0) {
                    c.setBackground(RCON_ROW_COLOR);
                    c.setForeground(Color.WHITE);
                } else if (row % 2 == 0) {
                    c.setBackground(ALTERNATE_ROW);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(30, 45, 61)); // Azul oscuro alternativo
                    c.setForeground(Color.WHITE);
                }
                
                // Resaltar celdas no vacías en las columnas de transformación
                if (column >= 2 && column <= 5 && value != null && !value.toString().trim().isEmpty()) {
                    c.setBackground(HIGHLIGHT_CELL);
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < expansionTable.getColumnCount(); i++) {
            expansionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Componentes de entrada
        keyInputField = new JTextField(32);
        keyInputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        plaintextInputField = new JTextField(32);
        plaintextInputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        initialKeyArea = new JTextArea(6, 40);
        initialKeyArea.setEditable(false);
        initialKeyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        initialKeyArea.setBackground(new Color(30, 40, 50));
        initialKeyArea.setForeground(Color.WHITE);
        
        // Crea botones con un enfoque completamente nuevo
        processButton = createButton("Process Key Expansion");
        encryptButton = createButton("Encrypt");
        randomKeyButton = createButton("Random Key");
        
        // Panel con pestañas para información adicional
        tabbedPane = new JTabbedPane();
        infoPanel = createInfoPanel();
        encryptionPanel = new JPanel(new BorderLayout());
        
        // Área de resultados para la salida de cifrado
        resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(new Color(30, 40, 50));
        resultArea.setForeground(Color.WHITE);
        
        // Panel de pasos de cifrado con desplazamiento
        encryptionStepsPanel = new JPanel();
        encryptionStepsPanel.setLayout(new BoxLayout(encryptionStepsPanel, BoxLayout.Y_AXIS));
        encryptionStepsPanel.setBackground(new Color(25, 35, 45));
        
        encryptionScrollPane = new JScrollPane(encryptionStepsPanel);
        encryptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        encryptionScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    /**
     * Método para crear botones que se muestren correctamente
     */
    private JButton createButton(String text) {
        // Crear un botón básico
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar el fondo del botón
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_BG.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_BG.brighter());
                } else {
                    g2.setColor(BUTTON_BG);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Dibujar el borde
                g2.setColor(BUTTON_BORDER);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                // Dibujar el texto
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
                String buttonText = getText();
                g2.setColor(BUTTON_FG);
                int x = (getWidth() - fm.stringWidth(buttonText)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(buttonText, x, y);
                
                g2.dispose();
            }
        };
        
        // Configuración adicional
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); 
        button.setBorderPainted(false);     
        button.setPreferredSize(new Dimension(180, 40));
        button.setMargin(new Insets(10, 20, 10, 20));
        
        return button;
    }
    
    /**
     * Organizar los componentes en el diseño
     */
    private void layoutComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(20, 30, 40)); // Fondo general oscuro
        
        // Panel superior para controles de entrada
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 80), 2),
            "AES-128 Input",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            Color.WHITE
        ));
        inputPanel.setBackground(new Color(25, 35, 45));
        
        JPanel inputFieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputFieldsPanel.setBackground(new Color(25, 35, 45));
        
        // Key input row
        JLabel keyLabel = new JLabel("Enter Hex Key (32 chars, no spaces):");
        keyLabel.setForeground(Color.WHITE);
        inputFieldsPanel.add(keyLabel);
        inputFieldsPanel.add(keyInputField);
        
        // Plaintext input row
        JLabel plaintextLabel = new JLabel("Enter Hex Plaintext (32 chars, no spaces):");
        plaintextLabel.setForeground(Color.WHITE);
        inputFieldsPanel.add(plaintextLabel);
        inputFieldsPanel.add(plaintextInputField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(25, 35, 45));
        buttonPanel.add(processButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(randomKeyButton);
        
        JPanel initialKeyPanel = new JPanel(new BorderLayout(5, 5));
        initialKeyPanel.setBackground(new Color(25, 35, 45));
        JLabel initialKeyLabel = new JLabel("Initial Key Words:");
        initialKeyLabel.setForeground(Color.WHITE);
        initialKeyPanel.add(initialKeyLabel, BorderLayout.NORTH);
        initialKeyPanel.add(new JScrollPane(initialKeyArea), BorderLayout.CENTER);
        
        inputPanel.add(inputFieldsPanel, BorderLayout.NORTH);
        inputPanel.add(buttonPanel, BorderLayout.CENTER);
        inputPanel.add(initialKeyPanel, BorderLayout.SOUTH);
        
        // Panel central para contenido con pestañas
        tabbedPane.addTab("Key Expansion", new JScrollPane(expansionTable));
        
        //Configurar el panel de cifrado
        encryptionPanel.add(new JScrollPane(resultArea), BorderLayout.NORTH);
        encryptionPanel.add(encryptionScrollPane, BorderLayout.CENTER);
        encryptionPanel.setBackground(new Color(25, 35, 45));
        
        tabbedPane.addTab("Encryption Process", encryptionPanel);
        tabbedPane.addTab("Algorithm Information", infoPanel);
        tabbedPane.addTab("Visualization", createVisualizationPanel());
        
        // Estilo para el JTabbedPane
        tabbedPane.setBackground(new Color(30, 40, 50));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Agregar todo al panel principal
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Crear panel con información del algoritmo
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(25, 35, 45));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoArea.setBackground(new Color(30, 40, 50));
        infoArea.setForeground(Color.WHITE);
        
        infoArea.setText(
            "Información del algoritmo AES-128\n\n" +
            "Esta aplicación implementa el algoritmo de cifrado y expansión de clave AES-128 según se especifica en FIPS-197.\n\n" +
            "Key Parameters:\n" +
            "- Nk = 4 (Longitud de la clave en palabras de 32 bits)\n" +
            "- Nr = 10 (Numero de roundas)\n" +
            "- Nb = 4 (Tamaño de bloque en palabras de 32 bits)\n\n" +
            "El algoritmo de expansión de clave genera un total de Nb*(Nr+1) palabras de 32 bits. Para AES-128, " +
            "Esto da como resultado 44 palabras (176 bytes) de material clave expandido..\n\n" +
            "El proceso de cifrado consta de los siguientes pasos:\n" +
            "1. Inicial AddRoundKey\n" +
            "2. 9 rondas de: SubBytes, ShiftRows, MixColumns, AddRoundKey\n" +
            "3. Ronda final (sin MixColumns): SubBytes, ShiftRows, AddRoundKey\n\n" +
            "La función SubWord() aplica el S-box a cada byte de la palabra de entrada.\n" +
            "La función RotWord() realiza una permutación cíclica en la palabra de entrada.\n" +
            "La matriz Rcon[] contiene las constantes redondeadas utilizadas en la expansión de clave."
        );
        
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Crear un panel de visualización
     */
    private JPanel createVisualizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(25, 35, 45));
        
        JLabel placeholder = new JLabel("Visualization options will be available in future updates", JLabel.CENTER);
        placeholder.setFont(new Font("SansSerif", Font.ITALIC, 14));
        placeholder.setForeground(Color.WHITE);
        panel.add(placeholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Configurar detectores de eventos
     */
    private void registerListeners() {
        processButton.addActionListener(e -> processKeyExpansion());
        encryptButton.addActionListener(e -> encryptPlaintext());
        randomKeyButton.addActionListener(e -> generateRandomKey());
        
        // Agregar atajo de teclado (Enter) al proceso
        keyInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processKeyExpansion();
                }
            }
        });
        
        plaintextInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    encryptPlaintext();
                }
            }
        });
    }
    
    /**
     * Generar una clave aleatoria de 128 bits
     */
    private void generateRandomKey() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        keyInputField.setText(sb.toString());
        processKeyExpansion();
    }
    
    /**
     * Expansión de la clave de proceso al hacer clic en el botón
     */
    private void processKeyExpansion() {
        try {
            String keyString = keyInputField.getText().trim().replaceAll("\\s+", "");
            
            // Validar la longitud de la clave
            if (keyString.length() != 32) {
                JOptionPane.showMessageDialog(this,
                    "Invalid key length. Please enter a 128-bit key (32 hex characters).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar caracteres hexadecimales
            if (!keyString.matches("[0-9A-Fa-f]+")) {
                JOptionPane.showMessageDialog(this,
                    "Invalid characters in key. Please use only hexadecimal characters (0-9, A-F).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Convertir una cadena hexadecimal en una matriz de bytes
            byte[] key = new byte[16];
            for (int i = 0; i < 16; i++) {
                key[i] = (byte) Integer.parseInt(keyString.substring(2*i, 2*i+2), 16);
            }
            
            // Ejecutar expansión de clave y actualizar la interfaz de usuario
            runKeyExpansion(key);
            updateTableDisplay();
            updateInitialKeyDisplay(key);
            
            // Cambiar a la pestaña de expansión de teclas
            tabbedPane.setSelectedIndex(0);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error processing key: " + ex.getMessage(),
                "Processing Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Función SubWord: aplica S-box a cada byte de la palabra
     * Esta es una transformación crítica en el algoritmo AES.
     * @param word Palabra de entrada de 32 bits
     * @return Palabra transformada después de la sustitución de la caja S
     */
    private int SubWord(int word) {
        int result = 0;
        // Aplicar S-box a cada byte 
        result |= (SBOX[(word >>> 24) & 0xFF] & 0xFF) << 24;
        result |= (SBOX[(word >>> 16) & 0xFF] & 0xFF) << 16;
        result |= (SBOX[(word >>> 8) & 0xFF] & 0xFF) << 8;
        result |= (SBOX[word & 0xFF] & 0xFF);
        return result;
    }
    
    /**
     * Función RotWord: rota la palabra un byte
     * @param word Palabra de entrada de 32 bits
     * @return Palabra después de rotación cíclica a la izquierda
     */
    private int RotWord(int word) {
        return ((word << 8) | ((word >>> 24) & 0xFF));
    }
    
    /**
     * Ejecuta el algoritmo de expansión de clave y almacene todos los valores intermedios
     * @param key La clave de entrada de 16 bytes
     */
    private void runKeyExpansion(byte[] key) {
        // Inicializar matrices para almacenar valores calculados
        w = new int[AES_128_NB * (AES_128_NR + 1)];
        tempValues = new String[44];
        afterRotWord = new String[44];
        afterSubWord = new String[44];
        rconValues = new String[44];
        afterRcon = new String[44];
        wMinusNk = new String[44];
        wFinal = new String[44];
        
        // Las primeras palabras de Nk son la clave original
        IntStream.range(0, AES_128_NK).forEach(i -> {
            w[i] = ((key[4*i] & 0xFF) << 24) | 
                   ((key[4*i+1] & 0xFF) << 16) | 
                   ((key[4*i+2] & 0xFF) << 8) | 
                   (key[4*i+3] & 0xFF);
            wFinal[i] = String.format("%08x", w[i]);
        });
        
        // Generar el resto de la clave expandida
        IntStream.range(AES_128_NK, AES_128_NB * (AES_128_NR + 1)).forEach(i -> {
            int temp = w[i-1];
            tempValues[i] = String.format("%08x", temp);
            
            if (i % AES_128_NK == 0) {
                int rotated = RotWord(temp);
                afterRotWord[i] = String.format("%08x", rotated);
                
                int subbed = SubWord(rotated);
                afterSubWord[i] = String.format("%08x", subbed);
                
                int rconValue = RCON[i/AES_128_NK - 1];
                rconValues[i] = String.format("%08x", rconValue);
                
                temp = subbed ^ rconValue;
                afterRcon[i] = String.format("%08x", temp);
            } else if (AES_128_NK > 6 && i % AES_128_NK == 4) {
                // Esta rama no se ejecutará para AES-128, pero se incluye para completar.
                temp = SubWord(temp);
            }
            
            wMinusNk[i] = String.format("%08x", w[i-AES_128_NK]);
            w[i] = w[i-AES_128_NK] ^ temp;
            wFinal[i] = String.format("%08x", w[i]);
        });
        
        // Preparar claves redondas para el cifrado
        prepareRoundKeys();
    }
    
    /**
     * Prepara claves redondas en el formato necesario para el cifrado
     */
    /**
 */
private void prepareRoundKeys() {
    roundKeys = new byte[AES_128_NR + 1][16];
    
    for (int round = 0; round <= AES_128_NR; round++) {
        for (int i = 0; i < 4; i++) {
            int word = w[round * 4 + i];
            roundKeys[round][i * 4] = (byte) ((word >>> 24) & 0xFF);
            roundKeys[round][i * 4 + 1] = (byte) ((word >>> 16) & 0xFF);
            roundKeys[round][i * 4 + 2] = (byte) ((word >>> 8) & 0xFF);
            roundKeys[round][i * 4 + 3] = (byte) (word & 0xFF);
        }
    }
}
    
    /**
     * Actualizar la tabla con los resultados de la expansión
     */
    private void updateTableDisplay() {
        // Borrar filas existentes
        tableModel.setRowCount(0);
        
        // Agregar filas con valores calculados
        for (int i = AES_128_NK; i < AES_128_NB * (AES_128_NR + 1); i++) {
            Object[] rowData = new Object[8];
            rowData[0] = i;
            rowData[1] = tempValues[i];
            
            // Mostrar solo valores de transformación para filas donde i % Nk == 0
            if (i % AES_128_NK == 0) {
                rowData[2] = afterRotWord[i];
                rowData[3] = afterSubWord[i];
                rowData[4] = rconValues[i];
                rowData[5] = afterRcon[i];
            } else {
                rowData[2] = "";
                rowData[3] = "";
                rowData[4] = "";
                rowData[5] = "";
            }
            
            rowData[6] = wMinusNk[i];
            rowData[7] = wFinal[i];
            
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Actualizar la visualización de palabras clave iniciales
     * @param key La clave de entrada de 16 bytes
     */
    private void updateInitialKeyDisplay(byte[] key) {
        StringBuilder sb = new StringBuilder();
        
        // Formatear la clave tanto en representación hexadecimal como en bytes
        sb.append("Cipher Key = ");
        for (int i = 0; i < key.length; i++) {
            sb.append(String.format("%02x ", key[i] & 0xFF));
        }
        sb.append("\n\n");
        
        sb.append("For AES-128: Nk = 4, Nr = 10, Nb = 4\n");
        sb.append("Initial key words:\n\n");
        
        // Mostrar las primeras 4 palabras (w0 a w3)
        sb.append(String.format("w0 = %s    w1 = %s    w2 = %s    w3 = %s\n", 
                               wFinal[0], wFinal[1], wFinal[2], wFinal[3]));
        
        initialKeyArea.setText(sb.toString());
    }
    
    /**
     * Cifrar texto plano con AES-128
     */
    private void encryptPlaintext() {
        try {
            // Comprobar si se ha realizado la expansión de clave
            if (roundKeys == null) {
                JOptionPane.showMessageDialog(this,
                    "Please process key expansion first.",
                    "Encryption Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String plaintextString = plaintextInputField.getText().trim().replaceAll("\\s+", "");
            
            // Validar la longitud del texto sin formato
            if (plaintextString.length() != 32) {
                JOptionPane.showMessageDialog(this,
                    "Invalid plaintext length. Please enter a 128-bit plaintext (32 hex characters).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar caracteres hexadecimales
            if (!plaintextString.matches("[0-9A-Fa-f]+")) {
                JOptionPane.showMessageDialog(this,
                    "Invalid characters in plaintext. Please use only hexadecimal characters (0-9, A-F).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Convertir una cadena hexadecimal en una matriz de bytes
            byte[] plaintext = new byte[16];
            for (int i = 0; i < 16; i++) {
                plaintext[i] = (byte) Integer.parseInt(plaintextString.substring(2*i, 2*i+2), 16);
            }
            
            // Borrar la visualización del cifrado anterior
            encryptionStepsPanel.removeAll();
            
            // Ejecutar el cifrado y visualizar los pasos
            byte[] ciphertext = runEncryption(plaintext);
            
            // Mostrar resultado
            displayEncryptionResult(plaintext, ciphertext);
            
            // Cambiar a la pestaña de cifrado
            tabbedPane.setSelectedIndex(1);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error encrypting plaintext: " + ex.getMessage(),
                "Encryption Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Ejecutar el algoritmo de cifrado AES con visualización
     * @param plaintext El texto plano de 16 bytes para cifrar
     * @return El texto cifrado de 16 bytes
     */
    private byte[] runEncryption(byte[] plaintext) {
        // Convertir texto simple en una matriz de estados 
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = plaintext[i * 4 + j];
            }
        }
        
        // Agregar título para el proceso de cifrado
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(25, 35, 45));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("AES-128 Encryption Process Step by Step");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        encryptionStepsPanel.add(titlePanel);
        
        // Ronda inicial (solo AddRoundKey)
        addRoundPanel("Initial Round (AddRoundKey)", state, null, null, null, roundKeys[0]);
        
        // Aplicar clave de ronda inicial
        state = addRoundKey(state, roundKeys[0]);
        
        // Rondas principales
        for (int round = 1; round < AES_128_NR; round++) {
            byte[][] startState = copyState(state);
            
            // SubBytes
            byte[][] afterSubBytes = copyState(state);
            state = subBytes(state);
            
            // ShiftRows
            byte[][] afterShiftRows = copyState(state);
            state = shiftRows(state);
            
            // MixColumns
            byte[][] afterMixColumns = copyState(state);
            state = mixColumns(state);
            
            // AddRoundKey
            state = addRoundKey(state, roundKeys[round]);
            
            // Añadir visualización para esta ronda
            addRoundPanel("Round " + round, startState, afterSubBytes, afterShiftRows, afterMixColumns, roundKeys[round]);
        }
        
        // Final round (no MixColumns)
        byte[][] startState = copyState(state);
        
        // SubBytes
        byte[][] afterSubBytes = copyState(state);
        state = subBytes(state);
        
        // ShiftRows
        byte[][] afterShiftRows = copyState(state);
        state = shiftRows(state);
        
        // Almacenar el estado antes de AddRoundKey final para visualización
        byte[][] beforeFinalAddRoundKey = copyState(state);
        
        // AddRoundKey
        state = addRoundKey(state, roundKeys[AES_128_NR]);
        
        // Añadir visualización para la ronda final
        addFinalRoundPanel("Final Round (Round 10)", startState, afterSubBytes, afterShiftRows, roundKeys[AES_128_NR], state);
        
        // Convertir el estado nuevamente a la matriz de salida 
        byte[] output = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output[i * 4 + j] = state[j][i];
            }
        }
        
        return output;
    }
    
    /**
     * Añadir un panel de visualización para una ronda estándar
     */
    private void addRoundPanel(String title, byte[][] startState, byte[][] afterSubBytes, 
                              byte[][] afterShiftRows, byte[][] afterMixColumns, byte[] roundKey) {
        JPanel roundPanel = new JPanel();
        roundPanel.setLayout(new BoxLayout(roundPanel, BoxLayout.Y_AXIS));
        roundPanel.setBackground(new Color(25, 35, 45));
        roundPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 70, 80)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Round title
        JLabel roundTitle = new JLabel(title);
        roundTitle.setFont(new Font("Arial", Font.BOLD, 14));
        roundTitle.setForeground(Color.WHITE);
        roundTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        roundPanel.add(roundTitle);
        roundPanel.add(Box.createVerticalStrut(10));
        
        // States panel
        JPanel statesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statesPanel.setBackground(new Color(25, 35, 45));
        
        // Add start state
        if (startState != null) {
            statesPanel.add(createStatePanel("Start", startState));
        }
        
        // Add arrow
        statesPanel.add(createArrowLabel());
        
        // Add after SubBytes state
        if (afterSubBytes != null) {
            statesPanel.add(createStatePanel("SubBytes", afterSubBytes));
            statesPanel.add(createArrowLabel());
        }
        
        // Add after ShiftRows state
        if (afterShiftRows != null) {
            statesPanel.add(createStatePanel("ShiftRows", afterShiftRows));
            statesPanel.add(createArrowLabel());
        }
        
        // Add after MixColumns state
        if (afterMixColumns != null) {
            statesPanel.add(createStatePanel("MixColumns", afterMixColumns));
            statesPanel.add(createArrowLabel());
        }
        
        // Add round key
        if (roundKey != null) {
            byte[][] keyState = new byte[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    keyState[j][i] = roundKey[i * 4 + j];
                }
            }
            statesPanel.add(createStatePanel("Round Key", keyState));
        }
        
        roundPanel.add(statesPanel);
        encryptionStepsPanel.add(roundPanel);
    }
    
    /**
     * Añadir un panel de visualización para la ronda final (sin MixColumns)
     * Modificado para mostrar el estado final después de AddRoundKey
     */
    private void addFinalRoundPanel(String title, byte[][] startState, byte[][] afterSubBytes, 
                                   byte[][] afterShiftRows, byte[] roundKey, byte[][] finalState) {
        JPanel roundPanel = new JPanel();
        roundPanel.setLayout(new BoxLayout(roundPanel, BoxLayout.Y_AXIS));
        roundPanel.setBackground(new Color(25, 35, 45));
        roundPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 70, 80)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Round title
        JLabel roundTitle = new JLabel(title);
        roundTitle.setFont(new Font("Arial", Font.BOLD, 14));
        roundTitle.setForeground(Color.WHITE);
        roundTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        roundPanel.add(roundTitle);
        roundPanel.add(Box.createVerticalStrut(10));
        
        // States panel
        JPanel statesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statesPanel.setBackground(new Color(25, 35, 45));
        
        // Add start state
        statesPanel.add(createStatePanel("Start", startState));
        
        // Add arrow
        statesPanel.add(createArrowLabel());
        
        // Add after SubBytes state
        statesPanel.add(createStatePanel("SubBytes", afterSubBytes));
        statesPanel.add(createArrowLabel());
        
        // Add after ShiftRows state
        statesPanel.add(createStatePanel("ShiftRows", afterShiftRows));
        statesPanel.add(createArrowLabel());
        
        // Add round key
        byte[][] keyState = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                keyState[j][i] = roundKey[i * 4 + j];
            }
        }
        statesPanel.add(createStatePanel("Round Key", keyState));
        
        // Add arrow
        statesPanel.add(createArrowLabel());
        
        // Add final state (ciphertext)
        statesPanel.add(createStatePanel("Ciphertext", finalState));
        
        roundPanel.add(statesPanel);
        encryptionStepsPanel.add(roundPanel);
        
        // Añadir una sección adicional para mostrar el resultado final en formato de matriz
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.setBackground(new Color(25, 35, 45));
        outputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 70, 80)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel outputTitle = new JLabel("Output (Ciphertext)");
        outputTitle.setFont(new Font("Arial", Font.BOLD, 14));
        outputTitle.setForeground(Color.WHITE);
        outputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputPanel.add(outputTitle);
        outputPanel.add(Box.createVerticalStrut(10));
        
        JPanel outputStatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        outputStatePanel.setBackground(new Color(25, 35, 45));
        outputStatePanel.add(createStatePanel("Final State (Column-Major Order)", finalState));
        
        outputPanel.add(outputStatePanel);
        encryptionStepsPanel.add(outputPanel);
    }
    
    /**
     * Crear un panel para mostrar una matriz de estados
     */
    private JPanel createStatePanel(String title, byte[][] state) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 40, 50));
        panel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 80)));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        
        // State grid
        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 2, 2));
        gridPanel.setBackground(new Color(30, 40, 50));
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JLabel cell = new JLabel(String.format("%02x", state[i][j] & 0xFF));
                cell.setFont(new Font("Monospaced", Font.BOLD, 12));
                cell.setHorizontalAlignment(JLabel.CENTER);
                cell.setOpaque(true);
                
                // Colores nuevos
                int value = state[i][j] & 0xFF;
                Color cellColor;
                
                if (value < 64) {
                    // Tonos de azul oscuro
                    cellColor = new Color(20 + value, 30 + value/2, 50 + value);
                } else if (value < 128) {
                    // Tonos de verde militar
                    cellColor = new Color(30 + (value-64)/2, 50 + (value-64), 30);
                } else if (value < 192) {
                    // Tonos de gris acero
                    cellColor = new Color(60 + (value-128)/2, 70 + (value-128)/2, 80 + (value-128)/2);
                } else {
                    // Tonos de rojo oscuro
                    cellColor = new Color(100 + (value-192), 30 + (value-192)/3, 30);
                }
                
                cell.setBackground(cellColor);
                cell.setForeground(Color.WHITE);
                
                cell.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 80)));
                cell.setPreferredSize(new Dimension(30, 30));
                gridPanel.add(cell);
            }
        }
        
        panel.add(gridPanel);
        panel.add(Box.createVerticalStrut(5));
        
        return panel;
    }
    
    /**
     * Crear una etiqueta de flecha para la visualización
     */
    private JLabel createArrowLabel() {
        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Arial", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);
        return arrow;
    }
    
    /**
     * Mostrar el resultado del cifrado
     */
    private void displayEncryptionResult(byte[] plaintext, byte[] ciphertext) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("ENCRYPTION RESULT\n");
        sb.append("=================\n\n");
        
        // Format plaintext
        sb.append("Plaintext = ");
        for (int i = 0; i < plaintext.length; i++) {
            sb.append(String.format("%02x", plaintext[i] & 0xFF));
            if ((i + 1) % 4 == 0 && i < plaintext.length - 1) {
                sb.append(" ");
            }
        }
        sb.append("\n\n");
        
        // Format ciphertext
        sb.append("Ciphertext = ");
        for (int i = 0; i < ciphertext.length; i++) {
            sb.append(String.format("%02x", ciphertext[i] & 0xFF));
            if ((i + 1) % 4 == 0 && i < ciphertext.length - 1) {
                sb.append(" ");
            }
        }
        sb.append("\n\n");
        
        // Comprobar si esto coincide con el vector de prueba FIPS-197
        String expectedCiphertext = "3925841d02dc09fbdc118597196a0b32";
        String actualCiphertext = bytesToHex(ciphertext);
        
        if (keyInputField.getText().trim().equals("2b7e151628aed2a6abf7158809cf4f3c") && 
            plaintextInputField.getText().trim().equals("3243f6a8885a308d313198a2e0370734") &&
            actualCiphertext.equals(expectedCiphertext)) {
            sb.append("✓ The result matches the FIPS-197 Appendix B test vector.");
        }
        
        resultArea.setText(sb.toString());
    }
    
    /**
     * Convertir una matriz de bytes en una cadena hexadecimal
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
    
    /**
     * Crear una copia de una matriz de estados
     */
    private byte[][] copyState(byte[][] state) {
        byte[][] copy = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                copy[i][j] = state[i][j];
            }
        }
        return copy;
    }
    
    /**
     * SubBytes transformation
     */
    private byte[][] subBytes(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = (byte) SBOX[state[i][j] & 0xFF];
            }
        }
        return result;
    }
    
    /**
     * ShiftRows transformation
     */
    private byte[][] shiftRows(byte[][] state) {
        byte[][] result = new byte[4][4];
        
        // Row 0: no shift
        result[0][0] = state[0][0];
        result[0][1] = state[0][1];
        result[0][2] = state[0][2];
        result[0][3] = state[0][3];
        
        // Row 1: shift left by 1
        result[1][0] = state[1][1];
        result[1][1] = state[1][2];
        result[1][2] = state[1][3];
        result[1][3] = state[1][0];
        
        // Row 2: shift left by 2
        result[2][0] = state[2][2];
        result[2][1] = state[2][3];
        result[2][2] = state[2][0];
        result[2][3] = state[2][1];
        
        // Row 3: shift left by 3
        result[3][0] = state[3][3];
        result[3][1] = state[3][0];
        result[3][2] = state[3][1];
        result[3][3] = state[3][2];
        
        return result;
    }
    
    /**
     * MixColumns transformation
     */
    private byte[][] mixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];
        
        for (int j = 0; j < 4; j++) {
            result[0][j] = (byte) (gmul(0x02, state[0][j]) ^ gmul(0x03, state[1][j]) ^ state[2][j] ^ state[3][j]);
            result[1][j] = (byte) (state[0][j] ^ gmul(0x02, state[1][j]) ^ gmul(0x03, state[2][j]) ^ state[3][j]);
            result[2][j] = (byte) (state[0][j] ^ state[1][j] ^ gmul(0x02, state[2][j]) ^ gmul(0x03, state[3][j]));
            result[3][j] = (byte) (gmul(0x03, state[0][j]) ^ state[1][j] ^ state[2][j] ^ gmul(0x02, state[3][j]));
        }
        
        return result;
    }
    
    /**
     * Multiplicación del campo de Galois
     */
    private int gmul(int a, int b) {
        int p = 0;
        int high_bit;
        
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            
            high_bit = a & 0x80;
            a <<= 1;
            
            if (high_bit != 0) {
                a ^= 0x1b; // Irreducible polynomial x^8 + x^4 + x^3 + x + 1
            }
            
            b >>= 1;
        }
        
        return p & 0xFF;
    }
    
    /**
     * AddRoundKey transformation
     */
    private byte[][] addRoundKey(byte[][] state, byte[] roundKey) {
        byte[][] result = new byte[4][4];
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = (byte) (state[i][j] ^ roundKey[j * 4 + i]);
            }
        }
        
        return result;
    }
    
    /**
     * Punto de entrada de la aplicación
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Establecer la apariencia a los valores predeterminados del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            AESKeyExpansionGUI app = new AESKeyExpansionGUI();
            app.setVisible(true);
        });
    }
}