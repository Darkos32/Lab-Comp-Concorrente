package Trabalho_2;

public class Leitura {
    private final Integer idLeitura;
    private final Integer idSensor;
    private final Integer valor;

    public Leitura(int idLeitura, int idSensor, int valor) {
        this.idLeitura = idLeitura;
        this.idSensor = idSensor;
        this.valor = valor;
    }

    public Integer getIdLeitura() {
        return idLeitura;
    }

    public Integer getIdSensor() {
        return idSensor;
    }

    public Integer getValor() {
        return valor;
    }
}
