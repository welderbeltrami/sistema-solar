package com.sistema.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitSolar {
    private String nome;
    private String fornecedor;
    private String url;
    private double potenciaKw;
    private double preco;
    private String descricao;
    private boolean compativel;
    private String tipoTelhado;
}
