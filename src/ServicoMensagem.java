package src;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicoMensagem implements Runnable {
    private String apelido; 
    private String texto;   

    public ServicoMensagem(String apelido, String texto) {
        this.apelido = apelido;
        this.texto = texto;
    }

    @Override
    public void run() {
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        
        String mensagemFormatada = dataHora + " (" + apelido + ") - " + texto;
        Servidor.logger.fine(dataHora + " FINE (" + apelido + ")\n" + texto);
        for (Participante p : Servidor.participantes) {
            p.enviarMensagemDireta(mensagemFormatada);
        }
    }
}