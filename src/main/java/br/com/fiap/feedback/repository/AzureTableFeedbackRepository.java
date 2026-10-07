package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.domain.Feedback;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;

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
}