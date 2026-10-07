package br.com.fiap.feedback;

import br.com.fiap.feedback.dto.FeedbackRequest;
import br.com.fiap.feedback.dto.FeedbackResponse;
import br.com.fiap.feedback.dto.NotificacaoUrgencia;
import br.com.fiap.feedback.repository.AzureTableFeedbackRepository;
import br.com.fiap.feedback.repository.FeedbackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import br.com.fiap.feedback.domain.Feedback;
import br.com.fiap.feedback.mapper.FeedbackMapper;

import java.util.Optional;

public class ReceberFeedbackFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FeedbackRepository repository =
            new AzureTableFeedbackRepository(
                    System.getenv("STORAGE_CONNECTION_STRING")
            );

    @FunctionName("ReceberFeedbackFunction")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "avaliacao"
            )
            HttpRequestMessage<Optional<String>> request,

            @QueueOutput(
                    name = "notificacaoUrgencia",
                    queueName = "notificacoes-urgencia",
                    connection = "AzureWebJobsStorage"
            )
            OutputBinding<String> notificacaoUrgencia,

            final ExecutionContext context) {

        context.getLogger().info("Recebendo feedback.");

        String body = request.getBody().orElse(null);

        if (body == null || body.isBlank()) {
            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Corpo da requisição não pode ser vazio.")
                    .build();
        }

        try {
            FeedbackRequest feedbackRequest =
                    objectMapper.readValue(body, FeedbackRequest.class);

            if (!feedbackRequest.isValido()) {
                return request
                        .createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Feedback inválido. A descrição é obrigatória e a nota deve estar entre 0 e 10.")
                        .build();
            }
            Feedback feedback = FeedbackMapper.toDomain(feedbackRequest);
            repository.salvar(feedback);
            if ("CRITICA".equals(feedback.getUrgencia())) {

                NotificacaoUrgencia notificacao =
                        new NotificacaoUrgencia(
                                feedback.getDescricao(),
                                feedback.getUrgencia(),
                                feedback.getDataEnvio().toString()
                        );

                String mensagem =
                        objectMapper.writeValueAsString(notificacao);

                notificacaoUrgencia.setValue(mensagem);
            }
            FeedbackResponse response = FeedbackResponse.from(feedback);

            return request
                    .createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(response)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Erro ao processar JSON: " + e.getMessage());

            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("JSON inválido: " + e.getMessage())
                    .build();
        }
    }
}