// Arquivo principal de JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('calculoForm');
    const resultado = document.getElementById('resultado');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const cep = document.getElementById('cep').value.replace(/\D/g, '');
        const consumo = document.getElementById('consumo').value;

        if (cep.length !== 8) {
            alert('Por favor, digite um CEP válido');
            return;
        }

        if (consumo <= 0) {
            alert('Por favor, digite um consumo válido');
            return;
        }

        try {
            const response = await fetch(`/api/calculo-solar/calcular?cep=${cep}&consumoMensalKwh=${consumo}`);
            const data = await response.json();

            // Dados básicos do sistema
            document.getElementById('potenciaSistema').textContent = `${data.potenciaSistemaKw.toFixed(2)} kW`;
            document.getElementById('potenciaInversor').textContent = `${data.potenciaInversorKw.toFixed(2)} kW`;
            document.getElementById('numeroPlacas').textContent = `${data.numeroPlacas} placas de ${data.potenciaPlacaW}W`;
            document.getElementById('horasSolPico').textContent = `${data.horasSolPico.toFixed(1)} horas/dia`;

            // Limpar tabelas existentes
            document.getElementById('tabelaInversores').innerHTML = '';
            document.getElementById('tabelaPlacas').innerHTML = '';
            document.getElementById('tabelaOutros').innerHTML = '';

            // Preencher tabela de inversores
            data.inversores.forEach(inversor => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${inversor.marca}</td>
                    <td>${inversor.modelo}</td>
                    <td>${inversor.potencia.toFixed(1)} kW</td>
                    <td>R$ ${inversor.preco.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
                `;
                document.getElementById('tabelaInversores').appendChild(row);
            });

            // Preencher tabela de placas
            data.placas.forEach(placa => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${placa.marca}</td>
                    <td>${placa.modelo}</td>
                    <td>${placa.potencia}W</td>
                    <td>R$ ${placa.preco.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
                `;
                document.getElementById('tabelaPlacas').appendChild(row);
            });

            // Preencher tabela de outros equipamentos
            const rowCabos = document.createElement('tr');
            rowCabos.innerHTML = `
                <td>Cabos</td>
                <td>${data.cabos.modelo}</td>
                <td>R$ ${data.cabos.preco.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
            `;
            document.getElementById('tabelaOutros').appendChild(rowCabos);

            const rowEstrutura = document.createElement('tr');
            rowEstrutura.innerHTML = `
                <td>Estrutura</td>
                <td>${data.estrutura.modelo}</td>
                <td>R$ ${data.estrutura.preco.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
            `;
            document.getElementById('tabelaOutros').appendChild(rowEstrutura);

            // Atualizar preço total
            document.getElementById('precoTotal').textContent = 
                `R$ ${data.precoTotal.toLocaleString('pt-BR', {minimumFractionDigits: 2})}`;

            resultado.style.display = 'block';
        } catch (error) {
            alert('Erro ao realizar o cálculo. Por favor, tente novamente.');
            console.error('Erro:', error);
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
