package com.sistema.service;

import com.sistema.model.PrecosEquipamentos;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class PesquisaPrecoService {
    
    private static final List<String> MARCAS_INVERSORES = Arrays.asList(
        "Growatt", "Fronius", "Deye", "Sungrow", "Canadian Solar"
    );
    
    private static final List<String> MARCAS_PLACAS = Arrays.asList(
        "Canadian Solar", "Jinko Solar", "JA Solar", "Trina Solar", "LONGi Solar"
    );

    public List<PrecosEquipamentos> pesquisarInversores(double potenciaMinima) {
        List<PrecosEquipamentos> resultados = new ArrayList<>();
        
        // Aqui você pode integrar com APIs de e-commerces ou marketplaces
        // Por enquanto, vou retornar alguns dados de exemplo
        for (String marca : MARCAS_INVERSORES) {
            PrecosEquipamentos inversor = new PrecosEquipamentos();
            inversor.setMarca(marca);
            inversor.setTipo("INVERSOR");
            inversor.setPotencia(Math.ceil(potenciaMinima));
            // Preços fictícios baseados em média de mercado
            inversor.setPreco(potenciaMinima * 1200); // Média de R$ 1200 por kW
            inversor.setModelo(marca + " " + Math.ceil(potenciaMinima) + "kW");
            resultados.add(inversor);
        }
        
        // Ordenar por preço
        resultados.sort(Comparator.comparing(PrecosEquipamentos::getPreco));
        return resultados;
    }

    public List<PrecosEquipamentos> pesquisarPlacas(int quantidade, double potenciaPlaca) {
        List<PrecosEquipamentos> resultados = new ArrayList<>();
        
        for (String marca : MARCAS_PLACAS) {
            PrecosEquipamentos placa = new PrecosEquipamentos();
            placa.setMarca(marca);
            placa.setTipo("PLACA");
            placa.setPotencia(potenciaPlaca);
            // Preço médio por placa (baseado em média de mercado)
            placa.setPreco(potenciaPlaca * 3.5); // Média de R$ 3.50 por W
            placa.setModelo(marca + " " + (int)potenciaPlaca + "W");
            resultados.add(placa);
        }
        
        resultados.sort(Comparator.comparing(PrecosEquipamentos::getPreco));
        return resultados;
    }

    public PrecosEquipamentos pesquisarCabos(double potenciaSistema) {
        // Cálculo aproximado de metros de cabo necessários
        double metrosCabo = potenciaSistema * 20; // Estimativa de 20m por kW
        
        PrecosEquipamentos cabos = new PrecosEquipamentos();
        cabos.setTipo("CABO");
        cabos.setMarca("Cabo Solar");
        cabos.setModelo("6mm²");
        cabos.setPreco(metrosCabo * 15); // Média de R$ 15 por metro
        return cabos;
    }

    public PrecosEquipamentos pesquisarEstrutura(int numeroPlacas) {
        PrecosEquipamentos estrutura = new PrecosEquipamentos();
        estrutura.setTipo("ESTRUTURA");
        estrutura.setMarca("Estrutura Solar");
        estrutura.setModelo("Kit " + numeroPlacas + " placas");
        // Preço médio por placa na estrutura
        estrutura.setPreco(numeroPlacas * 200); // Média de R$ 200 por placa
        return estrutura;
    }
}
