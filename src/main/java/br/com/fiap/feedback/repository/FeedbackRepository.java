package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.domain.Feedback;

public interface FeedbackRepository {

    void salvar(Feedback feedback);
}