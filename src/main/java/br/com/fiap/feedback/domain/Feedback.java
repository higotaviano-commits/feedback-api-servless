package br.com.fiap.feedback.domain;

import java.time.LocalDateTime;

public class Feedback {

    private String id;
    private String descricao;
    private Integer nota;
    private LocalDateTime dataEnvio;
    private String urgencia;

    public Feedback() {
    }

    public Feedback(
            String id,
            String descricao,
            Integer nota,
            LocalDateTime dataEnvio,
            String urgencia) {

        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.dataEnvio = dataEnvio;
        this.urgencia = urgencia;
    }

    public static Feedback criar(String descricao, Integer nota) {

        String urgencia;

        if (nota <= 4) {
            urgencia = "CRITICA";
        } else if (nota <= 7) {
            urgencia = "MODERADA";
        } else {
            urgencia = "NORMAL";
        }

        return new Feedback(
                java.util.UUID.randomUUID().toString(),
                descricao,
                nota,
                java.time.LocalDateTime.now(),
                urgencia
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

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public String getUrgencia() {
        return urgencia;
    }
}
