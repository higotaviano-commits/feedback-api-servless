package br.com.fiap.feedback.service;

import br.com.fiap.feedback.domain.Feedback;
import br.com.fiap.feedback.repository.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {

    private final FeedbackRepository repository;

    public RelatorioService(FeedbackRepository repository) {
        this.repository = repository;
    }

    public String gerarRelatorio() {

        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(7);

        List<Feedback> feedbacks =
                repository.listarEntre(inicio, fim);

        double mediaNotas = feedbacks.stream()
                .mapToInt(Feedback::getNota)
                .average()
                .orElse(0.0);

        Map<String, Long> quantidadePorUrgencia =
                feedbacks.stream()
                        .collect(Collectors.groupingBy(
                                Feedback::getUrgencia,
                                Collectors.counting()
                        ));

        Map<String, Long> quantidadePorDia =
                feedbacks.stream()
                        .collect(Collectors.groupingBy(
                                feedback -> feedback.getDataEnvio()
                                        .toLocalDate()
                                        .toString(),
                                Collectors.counting()
                        ));

        StringBuilder relatorio = new StringBuilder();

        relatorio.append("===== RELATÓRIO SEMANAL =====\n\n");

        relatorio.append("Total de avaliações: ")
                .append(feedbacks.size())
                .append("\n");

        relatorio.append("Média das notas: ")
                .append(String.format("%.2f", mediaNotas))
                .append("\n\n");

        relatorio.append("Quantidade por urgência:\n");

        quantidadePorUrgencia.forEach(
                (urgencia, quantidade) ->
                        relatorio.append("- ")
                                .append(urgencia)
                                .append(": ")
                                .append(quantidade)
                                .append("\n")
        );

        relatorio.append("\nQuantidade por dia:\n");

        quantidadePorDia.forEach(
                (dia, quantidade) ->
                        relatorio.append("- ")
                                .append(dia)
                                .append(": ")
                                .append(quantidade)
                                .append("\n")
        );

        System.out.println(relatorio);

        return relatorio.toString();
    }
}