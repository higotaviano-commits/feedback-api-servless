package br.com.fiap.feedback.dto;

public class NotificacaoUrgencia {

    private String descricao;
    private String urgencia;
    private String dataEnvio;

    public NotificacaoUrgencia() {
    }

    public NotificacaoUrgencia(
            String descricao,
            String urgencia,
            String dataEnvio) {

        this.descricao = descricao;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }
}