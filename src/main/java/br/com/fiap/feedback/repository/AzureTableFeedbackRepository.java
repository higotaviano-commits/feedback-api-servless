package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.domain.Feedback;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AzureTableFeedbackRepository implements FeedbackRepository {

    private static final String TABLE_NAME = "Feedbacks";

    private final TableClient tableClient;

    public AzureTableFeedbackRepository(String connectionString) {

        TableServiceClient serviceClient =
                new TableServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();

        this.tableClient = serviceClient.createTableIfNotExists(TABLE_NAME);
    }

    @Override
    public void salvar(Feedback feedback) {

        TableEntity entity = new TableEntity(
                "FEEDBACK",
                feedback.getId()
        );

        entity.addProperty("Descricao", feedback.getDescricao());
        entity.addProperty("Nota", feedback.getNota());
        entity.addProperty("DataEnvio", feedback.getDataEnvio().toString());
        entity.addProperty("Urgencia", feedback.getUrgencia());

        tableClient.createEntity(entity);
    }

    @Override
    public List<Feedback> listarTodos() {

        List<Feedback> feedbacks = new ArrayList<>();

        for (TableEntity entity : tableClient.listEntities()) {

            Feedback feedback = new Feedback(
                    entity.getRowKey(),
                    (String) entity.getProperty("Descricao"),
                    (Integer) entity.getProperty("Nota"),
                    LocalDateTime.parse(
                            (String) entity.getProperty("DataEnvio")
                    ),
                    (String) entity.getProperty("Urgencia")
            );

            feedbacks.add(feedback);
        }

        return feedbacks;
    }

    @Override
    public List<Feedback> listarEntre(
            LocalDateTime inicio,
            LocalDateTime fim) {

        return listarTodos().stream()
                .filter(feedback ->
                        !feedback.getDataEnvio().isBefore(inicio)
                                && feedback.getDataEnvio().isBefore(fim)
                )
                .toList();
    }
}