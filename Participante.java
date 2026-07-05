import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;

public class Participante implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String apelido;

    public Participante(Socket socket) {
        this.socket = socket;
    }

    public void enviarMensagemDireta(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            this.apelido = in.readLine();
            Servidor.logger.info("Participante '" + apelido + "' acabou de entrar no chat.");

            String texto;
            while ((texto = in.readLine()) != null) {
                if ("##sair##".equals(texto)) {
                    break;
                }
                
                ServicoMensagem tarefa = new ServicoMensagem(this.apelido, texto);
                Servidor.fofoqueiro.execute(tarefa);
            }

        } catch (IOException e) {
            Servidor.logger.log(Level.SEVERE, "Erro na comunicação com o participante " + apelido, e);
        } finally {
            Servidor.participantes.remove(this);
            Servidor.logger.info("Participante '" + apelido + "' saiu do chat.");
            try {
                socket.close();
            } catch (IOException e) {
                Servidor.logger.log(Level.SEVERE, "Erro ao fechar socket do participante", e);
            }
        }
    }
}