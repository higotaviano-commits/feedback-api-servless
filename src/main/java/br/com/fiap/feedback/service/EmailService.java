package br.com.fiap.feedback.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailMessage;

public class EmailService {

    private final EmailClient emailClient;
    private final String sender;
    private final String recipient;

    public EmailService() {
        String connectionString =
                System.getenv("COMMUNICATION_SERVICES_CONNECTION_STRING");

        this.sender =
                System.getenv("EMAIL_SENDER");

        this.recipient =
                System.getenv("ADMIN_EMAIL");

        this.emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public void enviarNotificacaoUrgencia(
            String descricao,
            String urgencia,
            String dataEnvio) {

        EmailMessage message = new EmailMessage()
                .setSenderAddress(sender)
                .setToRecipients(recipient)
                .setSubject("Feedback crítico recebido")
                .setBodyPlainText(
                        "Foi recebido um feedback crítico.\n\n" +
                                "Descrição: " + descricao + "\n" +
                                "Urgência: " + urgencia + "\n" +
                                "Data de envio: " + dataEnvio
                );

        emailClient.beginSend(message);
    }

    public void enviarRelatorioSemanal(String relatorio) {

        EmailMessage message = new EmailMessage()
                .setSenderAddress(sender)
                .setToRecipients(recipient)
                .setSubject("Relatório semanal de feedbacks")
                .setBodyPlainText(relatorio);

        emailClient.beginSend(message);
    }
}
