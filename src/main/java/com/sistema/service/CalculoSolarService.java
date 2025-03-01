package com.sistema.service;

import com.sistema.model.CalculoSolar;
import com.sistema.model.CalculoSolar.TipoTelhado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculoSolarService {
    
    private static final double POTENCIA_PLACA_PADRAO = 550.0; // Watts
    private static final double PERDA_SISTEMA = 0.20; // 20% de perda
    
    @Autowired
    private KitSolarService kitSolarService;
    
    public CalculoSolar calcularSistema(String cep, double consumoMensalKwh, TipoTelhado tipoTelhado) {
        CalculoSolar calculo = new CalculoSolar();
        calculo.setCep(cep);
        calculo.setConsumoMensalKwh(consumoMensalKwh);
        calculo.setPotenciaPlacaW(POTENCIA_PLACA_PADRAO);
        calculo.setPerdaSistema(PERDA_SISTEMA);
        calculo.setTipoTelhado(tipoTelhado);
        
        // Buscar horas de sol pico baseado no CEP (valor médio para exemplo)
        double horasSolPico = buscarHorasSolPico(cep);
        calculo.setHorasSolPico(horasSolPico);
        
        // Cálculo da potência do sistema
        double consumoDiarioKwh = consumoMensalKwh / 30.0;
        double potenciaNecessariaKw = (consumoDiarioKwh / horasSolPico) * (1 + PERDA_SISTEMA);
        calculo.setPotenciaSistemaKw(Math.ceil(potenciaNecessariaKw * 10) / 10);
        
        // Cálculo do número de placas
        int numeroPlacas = (int) Math.ceil((potenciaNecessariaKw * 1000) / POTENCIA_PLACA_PADRAO);
        calculo.setNumeroPlacas(numeroPlacas);
        
        // Cálculo da potência do inversor (adiciona 20% de margem)
        double potenciaInversorKw = Math.ceil(potenciaNecessariaKw * 1.2 * 10) / 10;
        calculo.setPotenciaInversorKw(potenciaInversorKw);

        // Buscar kits solares compatíveis
        calculo.setKitsEncontrados(kitSolarService.buscarKitsSolares(calculo.getPotenciaSistemaKw(), tipoTelhado));

        // Define o preço total como o kit mais barato encontrado
        calculo.setPrecoTotal(calculo.getKitsEncontrados().stream()
                .mapToDouble(kit -> kit.getPreco())
                .min()
                .orElse(0.0));
        
        return calculo;
    }
    
    private double buscarHorasSolPico(String cep) {
        // TODO: Implementar integração com API de dados solares
        // Por enquanto, retorna um valor médio para o Brasil
        return 4.8;
    }
}
