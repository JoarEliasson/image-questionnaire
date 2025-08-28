package org.example.imgq.repo;

import org.example.imgq.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByQuestionSetVersionOrderByIndexInSetAsc(Integer questionSetVersion);
}
