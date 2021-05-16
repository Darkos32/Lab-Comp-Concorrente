package Trabalho_2;

import java.util.HashMap;
import java.util.Map;

public class Buffer {
    private Leitura buffer[];
    private int length;
    private int proxPos;
    private Map<Integer,Integer> ultimaPosEscrita;
    public Buffer(int tam) {
        this.length = tam;
        ultimaPosEscrita = new HashMap<>();
        buffer = new Leitura[this.length];
        proxPos = 0;
    }

    public Leitura ler(int pos) {
        return this.buffer[pos];
    }

    public int getProxPos() {
        return proxPos;
    }

    public void escrever(Leitura medicao, int idSensor) {
        this.buffer[this.proxPos] = medicao;
        ultimaPosEscrita.put(idSensor, proxPos);
        proxPos++;
    }

    public int getLength() {
        return length;
    }
    public int getUltimaPosEscrita(int id) {
        return ultimaPosEscrita.get(id)!=null?ultimaPosEscrita.get(id):0;
    }
}
