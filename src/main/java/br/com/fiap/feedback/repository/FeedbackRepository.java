package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.domain.Feedback;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository {

    void salvar(Feedback feedback);

    List<Feedback> listarTodos();

    List<Feedback> listarEntre(
            LocalDateTime inicio,
            LocalDateTime fim
    );

}