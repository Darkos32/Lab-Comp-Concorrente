package Trabalho_2;

import java.util.HashMap;
import java.util.Map;

public class Buffer {
    private Leitura buffer[];// vetor onde se guarda as medições
    private int length;// tamanho do vetor buffer
    private int proxPos;// posição onde deve ser inserida a próxima medição
    private Map<Integer, Integer> ultimaPosEscrita;// guarda a última posição escrita por cada sensor

    // construtor da classe Buffer
    public Buffer(int tam) {
        this.length = tam;
        ultimaPosEscrita = new HashMap<>();
        buffer = new Leitura[this.length];
        proxPos = 0;
    }

    // retorna a medição na posição informada
    public Leitura ler(int pos) {
        return this.buffer[pos];
    }

    // função get para o atributo proxPos
    public int getProxPos() {
        return proxPos%length;
    }

    // escreve uma medição na posição indicada
    public void escrever(Leitura medicao, int idSensor) {
        this.buffer[this.proxPos%length] = medicao;// escreve a medição no buffer
        ultimaPosEscrita.put(idSensor, proxPos);// marca a útima posição escrita pelo sensor que chamou a função
        proxPos++;// atualiza a próxima posição ao ser escrita
    }

    // função get para o atributo length
    public int getLength() {
        return length;
    }

    // função get para o atributo ultimaPosEscrita
    public int getUltimaPosEscrita(int id) {
        return ultimaPosEscrita.get(id) != null ? ultimaPosEscrita.get(id) : 0;
    }
}
