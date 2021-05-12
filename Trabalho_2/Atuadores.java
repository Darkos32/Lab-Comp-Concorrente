package Trabalho_2;

public class Atuadores extends Thread {
    private final int id;
    private int ultimaPos;
    private int sinalVermelhoCount;
    private int sinalAmareloCount;
    private int medicoesCount;
    private int media;
    private Buffer compartilhado;
    private LeitorEscritor monitor;
    private Leitura[] ultimos;

    public Atuadores(int identificador, LeitorEscritor monitor, Buffer compartilhado) {
        this.id = identificador;
        this.monitor = monitor;
        this.compartilhado = compartilhado;
        this.sinalAmareloCount = 0;
        this.sinalVermelhoCount = 0;
        this.ultimos = new Leitura[15];
        this.medicoesCount = 0;
        this.media = 0;
    }

    private void setMedia() {
        media = media / medicoesCount;
    }

    private void setUltimasMedicoes() {
        int pos = 0;
        Leitura tempLeitura;
        media = 0;// reseta a média
        medicoesCount = 0;// reseta o contador
        ultimaPos = compartilhado.getUltimaPosEscrita(id);// garante que a medições lidas estarão ordenadas pelos seus
                                                          // ids
        for (int i = ultimaPos + 1; i != ultimaPos; i = (i + 1) % 60) {
            tempLeitura = compartilhado.ler(i);
            if (tempLeitura.getIdSensor() != this.id) {
                continue;
            }
            ultimos[pos] = tempLeitura;
            pos = (pos + 1) % 15;
            media += tempLeitura.getValor();
            medicoesCount++;
        }
        this.setMedia();// calcula a media
    }

    private boolean sinalizacao() {
        for (int i = 9; i < ultimos.length; i++) {
            if (controleDeSinais(ultimos[i].getValor())) {
                System.out.println("SINAL VERMELHO");
                return true;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (controleDeSinais(ultimos[i].getValor())) {
                System.out.println("SINAL AMARELO");
                return true;
            }
        }
        return false;
    }

    private boolean controleDeSinais(int temperatura) {
        if (temperatura > 35) {
            sinalVermelhoCount++;
            sinalAmareloCount++;
        } else {
            sinalVermelhoCount = 0;
        }
        if (sinalVermelhoCount == 5 || sinalAmareloCount == 5) {
            return true;
        }
        return false;

    }

    private void reset() {
        this.media = 0;
        this.medicoesCount = 0;
        this.sinalAmareloCount = 0;
        this.sinalVermelhoCount = 0;
        
    }

    private void avaliarMedicoes() {
        monitor.entrar_leitura();
        this.setUltimasMedicoes();
        monitor.sair_leitura();
    }

    @Override
    public void run() {

    }
}
