package com.sistema.controller;

import com.sistema.model.CalculoSolar;
import com.sistema.model.CalculoSolar.TipoTelhado;
import com.sistema.service.CalculoSolarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculo-solar")
@CrossOrigin(origins = "*")
public class CalculoSolarController {

    @Autowired
    private CalculoSolarService calculoSolarService;

    @GetMapping("/calcular")
    public CalculoSolar calcular(
            @RequestParam String cep,
            @RequestParam double consumoMensalKwh,
            @RequestParam TipoTelhado tipoTelhado) {
        return calculoSolarService.calcularSistema(cep, consumoMensalKwh, tipoTelhado);
    }
}
