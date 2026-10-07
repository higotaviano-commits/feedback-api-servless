package br.com.fiap.feedback.dto;

import br.com.fiap.feedback.domain.Feedback;

public class FeedbackResponse {

    private String id;
    private String descricao;
    private Integer nota;
    private String dataEnvio;
    private String urgencia;

    public FeedbackResponse() {
    }

    public FeedbackResponse(
            String id,
            String descricao,
            Integer nota,
            String dataEnvio,
            String urgencia) {

        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.dataEnvio = dataEnvio;
        this.urgencia = urgencia;
    }

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getDescricao(),
                feedback.getNota(),
                feedback.getDataEnvio().toString(),
                feedback.getUrgencia()
        );
    }

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public String getUrgencia() {
        return urgencia;
    }
}