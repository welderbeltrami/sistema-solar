# Sistema de Cálculo Solar com IA

Sistema para cálculo e recomendação de kits solares usando Java Spring Boot e Python com IA.

## Requisitos

- Java 17
- Python 3.13+
- Maven 3.9+

## Estrutura do Projeto

```
sistema/
├── src/                    # Código Java (Spring Boot)
│   └── main/
│       ├── java/
│       └── resources/
├── python_service/         # Serviço Python com IA
│   ├── app.py
│   ├── model.py
│   ├── scraper.py
│   └── requirements.txt
└── pom.xml
```

## Instalação

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/sistema-solar.git
cd sistema-solar
```

2. Instale as dependências Python:
```bash
cd python_service
pip install -r requirements.txt
cd ..
```

3. Compile o projeto Java:
```bash
mvn clean install
```

## Executando

1. Inicie o serviço Java (Spring Boot):
```bash
mvn spring-boot:run
```

2. Em outro terminal, inicie o serviço Python:
```bash
cd python_service
python app.py
```

3. Acesse a aplicação em:
```
http://localhost:8080
```

## Funcionalidades

- Cálculo de dimensionamento solar baseado em:
  - Consumo mensal em kWh
  - CEP
  - Tipo de telhado
- Busca inteligente de kits solares
- Classificação por IA considerando:
  - Preço por kW
  - Correspondência de potência
  - Qualidade dos componentes
- Interface web responsiva

## Tecnologias

- Backend: Java Spring Boot
- IA/Scraping: Python
- Frontend: HTML/CSS/JavaScript
- Banco de Dados: H2 (em memória)

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Crie um Pull Request
