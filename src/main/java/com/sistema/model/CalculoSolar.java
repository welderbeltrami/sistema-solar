package com.sistema.model;

import lombok.Data;
import java.util.List;

@Data
public class CalculoSolar {
    private Long id;
    private String cep;
    private double consumoMensalKwh;
    private double horasSolPico;
    private double potenciaSistemaKw;
    private double potenciaInversorKw;
    private int numeroPlacas;
    private double potenciaPlacaW;
    private double perdaSistema;
    private TipoTelhado tipoTelhado;
    private List<KitSolar> kitsEncontrados;
    private double precoTotal;

    public enum TipoTelhado {
        CERAMICA,
        FIBROCIMENTO,
        METALICO,
        LAJE
    }
}
