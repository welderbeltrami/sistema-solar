package com.sistema.model;

import lombok.Data;

@Data
public class PrecosEquipamentos {
    private String marca;
    private String modelo;
    private double potencia;
    private double preco;
    private String loja;
    private String linkProduto;
    private String tipo; // INVERSOR, PLACA, CABO, ESTRUTURA
}
