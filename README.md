# Feedback Platform --- Tech Challenge Fase 4

Plataforma serverless para receber avaliações de estudantes, armazenar
feedbacks, notificar administradores sobre avaliações críticas e enviar
relatórios periódicos.

> Projeto acadêmico da Fase 4 --- Cloud Computing, Serverless e Deploy.

## Objetivo

O sistema recebe feedbacks por uma API HTTP, valida as informações,
classifica a urgência da avaliação e persiste os dados na nuvem. Quando
um feedback é crítico, uma mensagem é encaminhada para uma fila, que
aciona uma função responsável por enviar um e-mail ao administrador. Uma
função agendada gera e envia um relatório periódico.

## Arquitetura

Principais componentes:

-   **Azure Functions**: execução serverless das funções da aplicação.
-   **Azure Table Storage**: persistência dos feedbacks na tabela
    `Feedbacks`.
-   **Azure Queue Storage**: fila `notificacoes-urgencia`, utilizada
    para desacoplar alertas críticos do recebimento HTTP.
-   **Azure Communication Services Email**: envio de e-mails.
-   **Application Insights**: telemetria e monitoramento.
-   **GitHub Actions**: automação de build e deploy.

### Functions

  -----------------------------------------------------------------------------
  Function                      Gatilho                 Responsabilidade
  ----------------------------- ----------------------- -----------------------
  `ReceberFeedbackFunction`     HTTP Trigger            Receber e validar
                                                        feedback, classificar
                                                        urgência, salvar os
                                                        dados e enfileirar
                                                        notificações críticas.

  `NotificarUrgenciaFunction`   Queue Trigger           Consumir mensagens da
                                                        fila e enviar e-mail
                                                        para o administrador.

  `GerarRelatorioFunction`      Timer Trigger           Consultar feedbacks
                                                        recentes, consolidar
                                                        indicadores e enviar
                                                        relatório por e-mail.
  -----------------------------------------------------------------------------

### Fluxo de um feedback

1.  O cliente envia uma requisição `POST` para a API.
2.  `ReceberFeedbackFunction` valida a descrição e a nota.
3.  A aplicação classifica a urgência e salva o feedback no Azure Table
    Storage.
4.  Se a urgência for `CRITICA`, a função publica uma mensagem na fila
    `notificacoes-urgencia`.
5.  `NotificarUrgenciaFunction` processa a mensagem e envia o e-mail ao
    administrador.

## API

### Endpoint

``` http
POST https://feedback-platform-functions-2026-g9hdefh7gre9hmae.brazilsouth-01.azurewebsites.net/api/avaliacao
```

A função utiliza autenticação `FUNCTION`. Envie uma chave válida usando
o parâmetro de consulta `code` ou o cabeçalho `x-functions-key`.

**Nunca publique a chave da Function, connection strings ou outras
credenciais neste repositório.**

### Corpo da requisição

``` json
{
  "descricao": "A aula foi excelente",
  "nota": 9
}
```

### Validações

-   `descricao` é obrigatória e não pode estar vazia.
-   `nota` deve ser um número inteiro entre `0` e `10`.

### Classificação de urgência

      Nota Urgência
  -------- ------------
     0 a 4 `CRITICA`
     5 a 7 `MODERADA`
    8 a 10 `NORMAL`

### Exemplos de teste

  ------------------------------------------------------------------------
  Cenário                                       Nota Resultado esperado
  --------------------- ---------------------------- ---------------------
  Feedback normal                                  9 Sucesso; urgência
                                                     `NORMAL`.

  Feedback crítico                                 2 Sucesso; urgência
                                                     `CRITICA` e envio de
                                                     e-mail de alerta.

  Nota inválida                                   15 Erro de validação.

  Descrição vazia                                  8 Erro de validação.
  ------------------------------------------------------------------------

O código HTTP exato retornado deve ser conferido na versão implantada da
Function.

## Persistência

Os feedbacks são armazenados na tabela `Feedbacks` do Azure Table
Storage. A implementação utiliza `PartitionKey` com o valor `FEEDBACK` e
`RowKey` com um identificador UUID. Os registros incluem descrição,
nota, data de envio e urgência.

## Notificações por e-mail

Para feedbacks classificados como `CRITICA`, a aplicação envia uma
mensagem à fila `notificacoes-urgencia`. A Function acionada pela fila
envia o alerta ao administrador. O e-mail de alerta deve conter a
descrição, a urgência e a data de envio.

O envio depende das configurações válidas do Azure Communication
Services Email e do endereço de e-mail do administrador configurado no
ambiente.

## Relatório periódico

`GerarRelatorioFunction` utiliza um Timer Trigger configurado para
`0 0 11 * * 1`, ou seja, segunda-feira às 11:00 UTC (08:00 no horário de
Maceió, UTC−3).

O relatório consolida feedbacks de uma janela recente de sete dias. Os
requisitos do desafio incluem:

-   média das avaliações;
-   quantidade de avaliações por dia;
-   quantidade de avaliações por urgência;
-   descrição, urgência e data de envio.

**Antes da entrega, confirme no e-mail gerado que todos os campos
solicitados aparecem na versão implantada.**

## Configuração local

### Pré-requisitos

-   JDK 25;
-   Maven;
-   Azure Functions Core Tools v4;
-   uma conta de armazenamento Azure configurada para a aplicação;
-   configurações de e-mail do Azure Communication Services, caso queira
    testar os envios.

### Configurações

Crie localmente o arquivo `local.settings.json` com as configurações
exigidas pela aplicação. Não adicione esse arquivo ao Git.

As configurações utilizadas pelo projeto incluem:

-   `AzureWebJobsStorage`
-   `STORAGE_CONNECTION_STRING`
-   `COMMUNICATION_SERVICES_CONNECTION_STRING`
-   `EMAIL_SENDER`
-   `ADMIN_EMAIL`

Use valores apropriados ao seu ambiente. **Não coloque valores reais de
credenciais neste README.**

### Compilar

Na raiz do projeto, onde está o `pom.xml`, execute:

``` bash
mvn clean package
```

### Executar localmente

Com o Azure Functions Core Tools instalado e o arquivo
`local.settings.json` configurado:

``` bash
mvn azure-functions:run
```

A URL local da função HTTP normalmente é:

``` text
http://localhost:7071/api/avaliacao
```

Como a função usa autenticação `FUNCTION` na configuração publicada,
confira a configuração local e a chave exigida pelo host ao executar os
testes.

## Deploy automatizado (CI/CD)

O workflow do GitHub Actions está em:

``` text
.github/workflows/master_feedback-platform-functions-2026.yml
```

O workflow é acionado por `push` para a branch `master` e também pode
ser executado manualmente por `workflow_dispatch`.

Etapas principais:

1.  checkout do repositório;
2.  configuração do Java 25;
3.  compilação com `mvn clean package`;
4.  autenticação na Azure usando GitHub Secrets;
5.  publicação na Azure Functions por `Azure/functions-action`.

Para publicar uma alteração:

1.  faça as alterações no código;
2.  crie um commit;
3.  envie o commit para a branch `master`;
4.  acompanhe a execução na aba **Actions** do GitHub;
5.  confirme que o workflow terminou com sucesso e teste a API
    publicada.

Um commit somente local não inicia o deploy. Se o workflow falhar, não
presuma que a nova versão foi publicada.

O workflow utiliza GitHub Secrets para os identificadores de
autenticação da Azure. Não substitua esses valores por credenciais
escritas diretamente no arquivo YAML.

## Monitoramento

A Function App está integrada ao Application Insights. Use o portal
Azure para acompanhar telemetria, requisições, duração e execuções das
funções.

Para a demonstração, mostre uma execução da API e onde consultar os
logs. Alertas personalizados precisam ser configurados separadamente
caso sejam exigidos como parte da estratégia de monitoramento.

## Segurança

-   A Function HTTP exige uma chave de função (`authLevel = FUNCTION`).
-   A opção **Somente HTTPS** está ativada na Function App.
-   O acesso anônimo ao Blob Storage está desativado.
-   A aplicação usa connection strings nas configurações da aplicação;
    por isso, o acesso por chave da conta de armazenamento está
    habilitado no ambiente atual.
-   O arquivo `local.settings.json` está listado no `.gitignore`, junto
    com diretórios gerados como `bin/` e `obj/`.
-   Não inclua chaves, connection strings, tokens, arquivos de
    publicação ou credenciais em commits, capturas de tela ou vídeos.
-   A autenticação por Managed Identity e permissões RBAC de menor
    privilégio é uma melhoria futura; não deve ser considerada
    implementada enquanto a migração não for concluída e testada.

## Postman

Uma coleção de testes pode ser importada no Postman para testar:

1.  feedback normal (nota 9);
2.  feedback crítico (nota 2);
3.  nota inválida (nota 15);
4.  descrição vazia.

Configure a URL base e a chave localmente no Postman. Não compartilhe
uma coleção exportada que contenha uma chave real. O cenário crítico
pode enviar um e-mail real ao administrador.



## Requisitos do Tech Challenge

A solução foi organizada para atender aos requisitos do desafio de
cloud, serverless, deploy automatizado, persistência, notificações
críticas, relatório periódico e monitoramento. Os testes finais, a
revisão dos campos do relatório e a gravação do vídeo devem ser
concluídos e verificados antes da entrega.
