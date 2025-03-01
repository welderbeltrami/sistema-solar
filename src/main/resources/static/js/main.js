// Arquivo principal de JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('calculoForm');
    const resultado = document.getElementById('resultado');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const cep = document.getElementById('cep').value;
        const consumoMensalKwh = document.getElementById('consumo').value;
        const tipoTelhado = document.getElementById('tipoTelhado').value;
        
        try {
            // Fazer o cálculo solar
            const response = await fetch(`https://api.solarbeltrami.com.br/api/calculo-solar/calcular?cep=${cep}&consumoMensalKwh=${consumoMensalKwh}&tipoTelhado=${tipoTelhado}`);
            const data = await response.json();
            
            if (data.error) {
                alert('Erro ao calcular: ' + data.error);
                return;
            }
            
            // Atualizar a interface com os resultados
            document.getElementById('potenciaSistema').textContent = data.potenciaSistemaKw.toFixed(2);
            document.getElementById('numeroPlacas').textContent = data.numeroPlacas;
            document.getElementById('potenciaInversor').textContent = data.potenciaInversorKw.toFixed(2);
            document.getElementById('producaoMensal').textContent = (data.potenciaSistemaKw * data.horasSolPico * 30 * (1 - data.perdaSistema)).toFixed(2);
            document.getElementById('economiaAnual').textContent = (data.potenciaSistemaKw * data.horasSolPico * 365 * (1 - data.perdaSistema) * 0.8).toFixed(2);
            
            // Buscar kits recomendados
            const kitsResponse = await fetch(`https://api.solarbeltrami.com.br/api/kits-solares/buscar?potenciaKw=${data.potenciaSistemaKw}&tipoTelhado=${tipoTelhado}`);
            const kitsData = await kitsResponse.json();
            
            // Mostrar kits recomendados
            const listaKits = document.getElementById('listaKits');
            listaKits.innerHTML = '';
            
            kitsData.forEach(kit => {
                const kitElement = document.createElement('div');
                kitElement.className = 'col-md-4 mb-3';
                kitElement.innerHTML = `
                    <div class="card h-100">
                        <div class="card-body">
                            <h5 class="card-title">${kit.title}</h5>
                            <p class="card-text">
                                <strong>Potência:</strong> ${kit.power} kW<br>
                                <strong>Preço:</strong> R$ ${kit.price.toFixed(2)}<br>
                                <strong>Score:</strong> ${(kit.score * 100).toFixed(1)}%
                            </p>
                            <a href="${kit.url}" target="_blank" class="btn btn-primary">Ver Detalhes</a>
                        </div>
                    </div>
                `;
                listaKits.appendChild(kitElement);
            });
            
            // Mostrar seção de resultados
            resultado.style.display = 'block';
            
        } catch (error) {
            console.error('Erro:', error);
            alert('Erro ao processar sua solicitação. Por favor, tente novamente.');
        }
    });

    // Máscara para o CEP
    const cepInput = document.getElementById('cep');
    cepInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 8) value = value.slice(0, 8);
        if (value.length > 5) {
            value = value.slice(0, 5) + '-' + value.slice(5);
        }
        e.target.value = value;
    });
});
