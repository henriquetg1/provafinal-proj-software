package br.insper.provapf.historico.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "collection-pf")
@Getter
@Setter
public class Feedback {
    @MongoId
    private String id;
    private String autor;
    private String comentario;
    private LocalDateTime data;

}
