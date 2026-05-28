# AntiFraudEngine

Este repositório contém um motor de regras antifraude desenvolvido com Spring Boot e Kotlin para o processamento síncrono de transações financeiras em tempo real. O sistema analisa as requisições de pagamento aplicando validações estruturais e de negócio sequenciais para detectar comportamentos anômalos antes da persistência dos estados.

## Arquitetura do Sistema

O projeto foi estruturado seguindo os princípios de segregação de responsabilidades e acoplamento fraco:

* **TransacaoController:** Entrypoint REST da API que expõe o endpoint `/transacoes` para receber os payloads das requisições via método HTTP POST.
* **TransacaoRequestDTO:** Camada de transferência de dados que implementa validações contratuais nativas do Kotlin no bloco `init` através de cláusulas de salvaguarda `require`, garantindo a integridade dos dados de entrada (bloqueio de valores negativos, datas futuras, strings vazias e submissões malformadas de cartões).
* **MotorRegrasService:** Componente da camada de serviço que centraliza a inteligência de negócio e a análise de risco, aplicando as regras de teto máximo global e restrições de categorias específicas (como Joalharia). Ele define deterministicamente o veredito da transação (`APROVADA` ou `NEGADA`).
* **GlobalExceptionHandler:** Mecanismo centralizado de tratamento de exceções baseado em `@ControllerAdvice`. Intercepta as falhas estruturais de desserialização do Spring (`HttpMessageNotReadableException`) e as violações do DTO, mapeando-as para respostas customizadas.
* **ErroResponseDTO:** Modelo de dados padronizado para encapsular os payloads de retorno em cenários de falha (HTTP 400 Bad Request).
* **TransacaoRepository:** Camada de abstração de dados baseada em Spring Data JPA para comunicação e persistência dos dados no banco relacional H2 em memória.

---

## Fluxo de Processamento da Requisição

1. **Validação Estrutural (Contrato):** O JSON da requisição é mapeado para o `TransacaoRequestDTO`. Se o bloco `init` disparar uma exceção por dados inválidos, o `GlobalExceptionHandler` intercepta a falha e retorna o status HTTP `400 Bad Request`.
2. **Avaliação Logística (Negócio):** Os dados válidos são encaminhados ao `MotorRegrasService`. O serviço executa as verificações de fraude com base em lógica condicional e converte o resultado para o estado final da transação.
3. **Persistência:** O registro é enviado ao `TransacaoRepository` para inserção no banco de dados H2 com o status resultante do processamento. O controlador retorna o status HTTP `201 Created` contendo a entidade persistida e o seu ID autogerado.

---

## Matriz de Cenários Homologados

| Caso de Teste | Input Provocado | Validador Atuante | Status HTTP |
| :--- | :--- | :--- | :--- |
| **Integridade de Valor** | `amount` negativo ou zerado | `TransacaoRequestDTO.init` | `400 Bad Request` |
| **Consistência Temporal** | `timestamp` com data futura | `TransacaoRequestDTO.init` | `400 Bad Request` |
| **Regex do Cartão** | Caracteres alfanuméricos ou tamanho inválido | `TransacaoRequestDTO.init` | `400 Bad Request` |
| **Teto Máximo Global** | `amount` superior a R$ 10.000,00 | `MotorRegrasService` | `201 Created` |
| **Setor de Risco** | Categoria `Joalharia` superior a R$ 5.000,00 | `MotorRegrasService` | `201 Created` |
| **Fluxo Nominal** | Dados consistentes e dentro dos limites | `MotorRegrasService` | `201 Created` |
