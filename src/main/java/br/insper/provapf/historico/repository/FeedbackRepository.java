package br.insper.provapf.historico.repository;

import br.insper.provapf.historico.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

}

