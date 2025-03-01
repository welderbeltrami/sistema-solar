package com.sistema.service;

import com.sistema.model.KitSolar;
import com.sistema.model.CalculoSolar.TipoTelhado;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class KitSolarService {
    private final RestTemplate restTemplate;
    private final String pythonServiceUrl = "http://localhost:5000";
    private final ObjectMapper objectMapper;

    public KitSolarService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<KitSolar> buscarKitsSolares(double potenciaKw, TipoTelhado tipoTelhado) {
        try {
            // Preparar a requisição para o serviço Python
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("power", potenciaKw);
            requestBody.put("roof_type", tipoTelhado.toString().toLowerCase());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Fazer a requisição inicial
            Map<String, Object> response = restTemplate.postForObject(
                pythonServiceUrl + "/search",
                request,
                Map.class
            );

            if (response != null && response.get("status").equals("searching")) {
                // Se a busca está em andamento, aguardar e verificar o status
                String searchId = potenciaKw + "_" + tipoTelhado.toString().toLowerCase();
                return waitForResults(searchId);
            } else if (response != null && response.get("results") != null) {
                // Se já temos resultados, converter para nossa lista de KitSolar
                return convertToKitList((List<Map<String, Object>>) response.get("results"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback para dados simulados em caso de erro
        return getFallbackResults(potenciaKw, tipoTelhado);
    }

    private List<KitSolar> waitForResults(String searchId) throws InterruptedException {
        int maxAttempts = 10;
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            Thread.sleep(1000); // Esperar 1 segundo entre tentativas
            
            Map<String, Object> status = restTemplate.getForObject(
                pythonServiceUrl + "/status/" + searchId,
                Map.class
            );
            
            if (status != null && status.get("status").equals("complete")) {
                return convertToKitList((List<Map<String, Object>>) status.get("results"));
            } else if (status != null && status.get("status").equals("error")) {
                break;
            }
            
            attempt++;
        }
        
        return new ArrayList<>();
    }

    private List<KitSolar> convertToKitList(List<Map<String, Object>> results) {
        List<KitSolar> kits = new ArrayList<>();
        
        for (Map<String, Object> result : results) {
            kits.add(KitSolar.builder()
                .nome((String) result.get("title"))
                .fornecedor((String) result.get("site"))
                .url((String) result.get("url"))
                .potenciaKw(((Number) result.get("power")).doubleValue())
                .preco(((Number) result.get("price")).doubleValue())
                .descricao((String) result.get("description"))
                .compativel(true)
                .tipoTelhado((String) result.get("roof_type"))
                .build());
        }
        
        return kits;
    }

    private List<KitSolar> getFallbackResults(double potenciaKw, TipoTelhado tipoTelhado) {
        List<KitSolar> kits = new ArrayList<>();
        
        kits.add(KitSolar.builder()
            .nome("Kit Solar " + String.format("%.2f", potenciaKw) + "kWp - Premium")
            .fornecedor("SolarBrasil")
            .url("https://exemplo.com")
            .potenciaKw(potenciaKw)
            .preco(potenciaKw * 3500)
            .descricao("Kit completo com painéis 550W e inversor")
            .compativel(true)
            .tipoTelhado(tipoTelhado.toString())
            .build());
            
        return kits;
    }
}
