# 💱 Câmbio API - Integração e Precificação

API REST desenvolvida em Java com Spring Boot para consulta, precificação e histórico de cotações de moedas estrangeiras em tempo real. O sistema integra-se com a AwesomeAPI e aplica regras de negócio locais (Spread e IOF) antes de persistir os dados.

## 🏗️ Arquitetura e Fluxo da Aplicação

O projeto segue os princípios da **Arquitetura em Camadas (Layered Architecture)**, complementado pelo padrão **Anti-Corruption Layer (ACL)** para isolamento de integrações externas.

1. **Presentation (`CotacaoController`)**: Recebe a requisição REST dinâmica com a moeda de origem e destino.
2. **Application (`CotacaoService`)**: Orquestra a regra de negócio. Multiplica o valor de mercado pelo fator de conversão (4% de Spread Comercial + 1,1% de IOF).
3. **Infrastructure / Integração (`AwesomeApiClient`)**: 
   - Comunica-se via `RestClient` com a AwesomeAPI externa.
   - Atua como **Anti-Corruption Layer**, extraindo e traduzindo apenas os dados necessários do JSON dinâmico para os DTOs internos.
   - Protegido por mecanismos de tolerância a falhas (**Resilience4j**), utilizando `@CircuitBreaker` e `@Retry` para evitar gargalos de rede e oferecer rotas de *fallback*.
4. **Infrastructure / Persistência (`CotacaoRepository`)**: Grava a cotação finalizada no **PostgreSQL** para histórico de auditoria.

---

## 🔌 Documentação dos Endpoints

A API é dinâmica e suporta qualquer par de moedas fornecido pela AwesomeAPI (ex: USD, EUR, GBP, BTC).

### Gerar Cotação Precificada
`GET /api/cotacoes/{moedaOrigem}/{moedaDestino}`

**Parâmetros de Rota:**
* `moedaOrigem` (String): Código da moeda a ser consultada (ex: `USD`, `EUR`, `BTC`).
* `moedaDestino` (String): Código da moeda de conversão (ex: `BRL`).

**Exemplo de Requisição (Euro para Real):**
```bash
curl -X GET http://localhost:8080/api/cotacoes/EUR/BRL
```

**Exemplo de Resposta (Sucesso - 200 OK):**
```json
{
  "id": 2,
  "moedaOrigem": "EUR",
  "moedaDestino": "BRL",
  "valorBruto": 5.9214,
  "valorFinalCalculado": 6.2260,
  "dataConsulta": "2026-11-03T10:45:00.123456"
}
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java 21+
* PostgreSQL 15+ em execução (com utilizador local sem senha ou configurado no `application.yml`).
* Maven (Embutido via `mvnw`).

### Passos
1. **Configurar o Banco de Dados:**
   No terminal, crie o banco de dados da aplicação:
   ```bash
   psql -U seu_usuario -c "CREATE DATABASE cambio_db;"
   ```

2. **Iniciar a Aplicação:**
   Na raiz do projeto, execute o comando Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   *O Hibernate encarrega-se de criar automaticamente a tabela `tb_cotacao_precificada` no PostgreSQL.*

---

## 🧪 Testes de Integração

Os testes foram desenvolvidos utilizando **WireMock** para simular o comportamento da API externa de forma isolada, garantindo alta velocidade e independência de rede, além do **MockMvc** para testar os endpoints.

```bash
./mvnw test
```