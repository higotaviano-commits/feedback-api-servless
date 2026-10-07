package br.com.fiap.feedback.mapper;

import br.com.fiap.feedback.domain.Feedback;
import br.com.fiap.feedback.dto.FeedbackRequest;

public class FeedbackMapper {

    public static Feedback toDomain(FeedbackRequest request) {
        return Feedback.criar(
                request.getDescricao(),
                request.getNota()
        );
    }
}