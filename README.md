# AntiFraudEngine

Este repositório contém um motor de regras antifraude desenvolvido com Spring Boot e Kotlin para o processamento de transações financeiras em tempo real. O sistema analisa requisições de pagamento de forma síncrona, aplicando validações sequenciais para detectar comportamentos anômalos antes da persistência dos dados.

## Arquitetura do Sistema

O projeto foi estruturado seguindo os princípios de segregação de responsabilidades:

* **TransacaoController:** Entrypoint REST da API que expõe o endpoint `/transactions` para receber os payloads das requisições.
* **TransacaoRequestDTO:** Camada de transferência de dados que implementa validações contratuais nativas do Kotlin no bloco `init` através de cláusulas `require`.
* **MotorRegrasFraude:** Componente centralizador que executa uma lista polimórfica de componentes baseados na interface `RegraFraude`.
* **Regras de Negócio:** Conjunto de validadores isolados, incluindo `RegraLimiteValor`, `RegraRiscoNoturno` e a `RegraAltaFrequencia` (responsável pelo *Velocity Check* que analisa o histórico recente de compras em janelas temporais móveis de 5 minutos).
* **TransacaoRepository:** Camada de abstração de dados baseada em Spring Data JPA para comunicação com o banco de dados relacional H2 em memória.

---

## Pendências no Desenvolvimento

O fluxo principal de análise e persistência encontra-se totalmente funcional. Contudo, o sistema ainda carece de um barramento unificado para o tratamento de exceções. 

Atualmente, as falhas geradas pelas cláusulas de salvaguarda (`require`) no DTO estouram uma exceção do tipo `IllegalArgumentException` diretamente na stacktrace do servidor, retornando um erro genérico `500 Internal Server Error`.

As próximas etapas do cronograma preveem:
* Implementação de um **`ErroResponseDTO`** para padronizar o payload de retorno em cenários de falha.
* Criação de um **`GlobalExceptionHandler`** utilizando a anotação `@ControllerAdvice` para interceptar os erros de validação e convertê-los em respostas limpas com código de status HTTP **`400 Bad Request`**.
