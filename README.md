# Inter Enterprise Gateway

API interna para integrar sistemas externos com as APIs do Banco Inter: Cobranca, Banking, Pix e Pix Automatico. O servico e stateless, nao usa banco de dados e centraliza mTLS, OAuth, DTOs, tratamento de erros e webhooks.

## Arquitetura

Fluxo principal:

```text
Sistema externo -> Controller -> Service -> Gateway -> Banco Inter
```

Responsabilidades:

- `Controller`: expoe endpoints internos em `/api/inter/**` e valida DTOs.
- `Service`: organiza regras por modulo Inter.
- `Gateway`: adiciona token Bearer, executa chamadas HTTP e trata erros da API Inter.
- `InterTokenProvider`: busca e reutiliza token OAuth `client_credentials`.
- `InterSslContextFactory`: configura mTLS com certificado `.crt` e chave `.key`.

## Como Configurar Certificados

Crie a pasta local:

```bash
mkdir -p .certs
```

Coloque os arquivos fornecidos pelo Inter:

```text
.certs/interapicertificado.crt
.certs/interapi_chave.key
.certs/ca.crt
```

Para Docker, esses arquivos sao montados em `/app/.certs`, entao use:

```env
INTER_CERTIFICATE_PATH=/app/.certs/interapicertificado.crt
INTER_PRIVATE_KEY_PATH=/app/.certs/interapi_chave.key
```

Para execucao local sem Docker:

```env
INTER_CERTIFICATE_PATH=.certs/interapicertificado.crt
INTER_PRIVATE_KEY_PATH=.certs/interapi_chave.key
```

Nao use `.p12`. O projeto aceita diretamente `.crt` e `.key`.

## Como Obter o Token

O token OAuth e obtido automaticamente. O cliente externo nao precisa chamar endpoint de token.

Quando um endpoint interno e chamado, o backend:

```text
1. valida INTER_CLIENT_ID e INTER_CLIENT_SECRET
2. usa mTLS com INTER_CERTIFICATE_PATH e INTER_PRIVATE_KEY_PATH
3. solicita token em /oauth/v2/token
4. envia Authorization: Bearer <token> para o Inter
5. reutiliza o token em cache ate perto da expiracao
```

Configure `INTER_SCOPE` com os escopos liberados no portal Inter:

```env
INTER_SCOPE="extrato.read boleto-cobranca.read pix.read"
```

## Como Consumir Endpoints

Base local:

```text
http://localhost:8080
```

Principais grupos:

```text
/api/inter/banking
/api/inter/cobrancas
/api/inter/pix
/api/inter/pix-automatico
/api/inter/webhooks
```


## Variaveis de Ambiente

Exemplo `.env` local:

```env
SPRING_PROFILES_ACTIVE=sandbox
INTER_BASE_URL=https://cdpj-sandbox.partners.uatinter.co
INTER_CLIENT_ID=seu_client_id
INTER_CLIENT_SECRET=seu_client_secret
INTER_CERTIFICATE_PATH=.certs/interapicertificado.crt
INTER_PRIVATE_KEY_PATH=.certs/interapi_chave.key
INTER_SCOPE="extrato.read boleto-cobranca.read pix.read"
INTER_CONNECT_TIMEOUT_SECONDS=10
```

Producao:

```env
SPRING_PROFILES_ACTIVE=prod
INTER_BASE_URL=https://cdpj.partners.bancointer.com.br
INTER_CLIENT_ID=client_id_prod
INTER_CLIENT_SECRET=client_secret_prod
INTER_CERTIFICATE_PATH=/app/.certs/interapicertificado.crt
INTER_PRIVATE_KEY_PATH=/app/.certs/interapi_chave.key
INTER_SCOPE="extrato.read boleto-cobranca.read pix.read"
INTER_CONNECT_TIMEOUT_SECONDS=10
```

## Exemplo de Requisicao

Consultar saldo:

```bash
curl http://localhost:8080/api/inter/banking/saldo
```

Emitir cobranca:

```bash
curl -X POST http://localhost:8080/api/inter/cobrancas \
  -H "Content-Type: application/json" \
  -d '{
    "seuNumero": "123456",
    "valorNominal": 25.90,
    "dataVencimento": "2026-07-30",
    "numDiasAgenda": 10,
    "pagador": {
      "cpfCnpj": "12345678901",
      "nome": "Cliente Teste"
    }
  }'
```

Criar cobranca Pix:

```bash
curl -X PUT http://localhost:8080/api/inter/pix/cob/txid123 \
  -H "Content-Type: application/json" \
  -d '{
    "calendario": { "expiracao": 3600 },
    "valor": { "original": "10.00" },
    "chave": "sua-chave-pix"
  }'
```

## Estrutura do Projeto

```text
src/main/java/com/interenterprise/gateway
  configuration/       Configuracoes Inter, RestClient e mTLS
  controller/          Endpoints REST internos
  dto/                 DTOs por modulo e DTOs comuns
  exception/           Tratamento padronizado de erros
  gateway/             Cliente HTTP interno para API Inter
  service/             Interfaces e implementacoes por modulo
  utils/               Helpers de paths

src/main/resources
  application.properties
  application-sandbox.properties
  application-prod.properties

src/test/java
  controller/          Testes de todos os controllers
  service/             Testes de autenticacao/token
```

## Docker

Build e subida local:

```bash
docker compose up -d --build
docker compose logs -f api
```

O container publica a API apenas em `127.0.0.1:8080`. Em VPS, use Nginx como proxy HTTPS para essa porta.

## Testes

```bash
./mvnw test
```

A suite cobre os controllers principais, recebimento de webhooks e token provider.
