package br.com.fiap.feedback;

import br.com.fiap.feedback.repository.AzureTableFeedbackRepository;
import br.com.fiap.feedback.repository.FeedbackRepository;
import br.com.fiap.feedback.service.EmailService;
import br.com.fiap.feedback.service.RelatorioService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

public class GerarRelatorioFunction {

    private final FeedbackRepository repository =
            new AzureTableFeedbackRepository(
                    System.getenv("STORAGE_CONNECTION_STRING")
            );

    private final EmailService emailService =
            new EmailService();

    private final RelatorioService relatorioService =
            new RelatorioService(repository);

    @FunctionName("GerarRelatorioFunction")
    public void run(
            @TimerTrigger(
                    name = "timerInfo",
                    schedule = "0 0 11 * * 1"
            )
            String timerInfo,
            final ExecutionContext context) {

        context.getLogger().info(
                "Iniciando geração do relatório."
        );

        String relatorio = relatorioService.gerarRelatorio();

        context.getLogger().info(relatorio);

        emailService.enviarRelatorioSemanal(relatorio);

        context.getLogger().info(
                "Relatório processado com sucesso."
        );
    }
}