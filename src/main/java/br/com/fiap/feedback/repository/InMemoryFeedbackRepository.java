package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.domain.Feedback;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFeedbackRepository implements FeedbackRepository {

    private final List<Feedback> feedbacks = new ArrayList<>();

    @Override
    public void salvar(Feedback feedback) {
        feedbacks.add(feedback);

        System.out.println(
                "Feedback salvo: " + feedback.getId()
                        + " - nota: " + feedback.getNota()
                        + " - urgencia: " + feedback.getUrgencia()
        );
    }

    public List<Feedback> listarTodos() {
        return List.copyOf(feedbacks);
    }
}