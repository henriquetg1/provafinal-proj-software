package br.insper.provapf.historico.controller;

import br.insper.provapf.historico.model.Feedback;
import br.insper.provapf.historico.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

//    POST /feedbacks: Envia um novo feedback (Acessível por ADMIN).
//    GET /feedbacks: Lista todos os feedbacks enviados (Acessível por ADMIN e DEVELOPERS).
//    GET /feedbacks/{id}: Consulta detalhes de um feedback específico (Acessível por ADMIN e DEVELOPERS).
//    DELETE /feedbacks/{id}: Exclui um feedback (Apenas ADMIN)

    @PostMapping // (ADMIN)
    public ResponseEntity<?> criarFeedback(@RequestHeader("Authorization") String jwtToken, @RequestBody Feedback feedback) {
        var f = feedbackService.criarFeedback(jwtToken, feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(f);
    }

    @GetMapping // (ADMIN, DEVELOPER)
    public ResponseEntity<?> listarFeedbacks(@RequestHeader("Authorization") String jwtToken) {
        var feedbacks = feedbackService.listarFeedbacks(jwtToken);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}") // (ADMIN, DEVELOPER)
    public ResponseEntity<?> listarFeedbackPorId(@RequestHeader("Authorization") String jwtToken, @PathVariable String id) {
        var feedback = feedbackService.listarFeedbackPorId(jwtToken, id);
        return ResponseEntity.ok(feedback);
    }

    @DeleteMapping("/{id}") // (ADMIN)
    public ResponseEntity<?> deletarFeedback(@RequestHeader("Authorization") String jwtToken, @PathVariable String id) {
        feedbackService.excluirFeedback(jwtToken, id);
        return ResponseEntity.noContent().build();
    }

}
