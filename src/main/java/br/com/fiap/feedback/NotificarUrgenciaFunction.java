package br.com.fiap.feedback;

import br.com.fiap.feedback.dto.NotificacaoUrgencia;
import br.com.fiap.feedback.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

public class NotificarUrgenciaFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService = new EmailService();

    @FunctionName("NotificarUrgenciaFunction")
    public void run(
            @QueueTrigger(
                    name = "mensagem",
                    queueName = "notificacoes-urgencia",
                    connection = "AzureWebJobsStorage"
            )
            String mensagem,
            final ExecutionContext context) {

        try {
            context.getLogger().info(
                    "Mensagem de urgência recebida: " + mensagem
            );

            NotificacaoUrgencia notificacao =
                    objectMapper.readValue(
                            mensagem,
                            NotificacaoUrgencia.class
                    );

            emailService.enviarNotificacaoUrgencia(
                    notificacao.getDescricao(),
                    notificacao.getUrgencia(),
                    notificacao.getDataEnvio()
            );

            context.getLogger().info(
                    "E-mail de notificação enviado com sucesso."
            );

        } catch (Exception e) {

            context.getLogger().severe(
                    "Erro ao enviar notificação de urgência: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Falha ao processar notificação de urgência.",
                    e
            );
        }
    }
}