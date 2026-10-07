package br.com.fiap.feedback.dto;

public class FeedbackRequest {

    private String descricao;
    private Integer nota;

    public FeedbackRequest() {
    }

    public FeedbackRequest(String descricao, Integer nota) {
        this.descricao = descricao;
        this.nota = nota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public boolean isValido() {
        return descricao != null
                && !descricao.isBlank()
                && nota != null
                && nota >= 0
                && nota <= 10;
    }
}