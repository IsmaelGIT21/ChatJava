package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static final int PORTA = 50123; 
    public static final Logger logger = Logger.getLogger(Servidor.class.getName());
    
    public static final List<Participante> participantes = new CopyOnWriteArrayList<>();
    
    public static final ExecutorService fofoqueiro = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        logger.setLevel(Level.INFO); 
        
        ExecutorService poolClientes = Executors.newCachedThreadPool();

        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            logger.info("Servidor de Chat iniciado na porta " + PORTA);

            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                logger.info("Nova conexão recebida de: " + clienteSocket.getRemoteSocketAddress());
                
                Participante participante = new Participante(clienteSocket);
                participantes.add(participante); 
                
                poolClientes.execute(participante);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro crítico no servidor", e); 
        } finally {
            poolClientes.shutdown();
            fofoqueiro.shutdown();
        }
    }
}