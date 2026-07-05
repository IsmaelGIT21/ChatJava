import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends JFrame {
    private JTextArea areaTexto;
    private JTextField campoMensagem;
    private JButton botaoEnviar;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String apelido;

    public Cliente(String ip, String apelido) {
        this.apelido = apelido;
        
        setTitle("Chat Cliente - " + apelido);
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLayout(new BorderLayout());
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setBackground(new Color(130, 120, 240)); 
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setFont(new Font("Monospaced", Font.BOLD, 13));
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        JPanel painelInferior = new JPanel(new BorderLayout());
        campoMensagem = new JTextField();
        botaoEnviar = new JButton("Send"); 
        painelInferior.add(campoMensagem, BorderLayout.CENTER);
        painelInferior.add(botaoEnviar, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);
        botaoEnviar.addActionListener(e -> enviarMensagem());
        campoMensagem.addActionListener(e -> enviarMensagem());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharConexao();
            }
        });

        conectarAoServidor(ip);
    }

    private void conectarAoServidor(String ip) {
        try {
            this.socket = new Socket(ip, 50123); 
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(this.apelido);
            new Thread(() -> {
                try {
                    String linha;
                    while ((linha = in.readLine()) != null) {
                        areaTexto.append(linha + "\n");
                    }
                } catch (IOException e) {
                    areaTexto.append("Conexão encerrada.\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível conectar ao servidor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void enviarMensagem() {
        String texto = campoMensagem.getText().trim();
        if (!texto.isEmpty()) {
            out.println(texto); 
            campoMensagem.setText("");
        }
    }

    private void fecharConexao() {
        if (out != null) {
            out.println("##sair##"); 
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso incorreto! Execute passando: java Cliente <IP_SERVIDOR> <APELIDO>");
            System.exit(1);
        }

        String ip = args[0];
        String apelido = args[1];
        SwingUtilities.invokeLater(() -> {
            Cliente cliente = new Cliente(ip, apelido);
            cliente.setVisible(true);
        });
    }
}