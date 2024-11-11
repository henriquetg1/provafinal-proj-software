package br.insper.provapf.historico.service;

import br.insper.provapf.historico.dto.PlanoUsuarioDTO;

import br.insper.provapf.historico.model.Feedback;
import br.insper.provapf.historico.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Encontra o usuário a partir do token JWT
    private PlanoUsuarioDTO achaUsuario(String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "http://184.72.80.215/usuario/validate";

        try {
            ResponseEntity<PlanoUsuarioDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlanoUsuarioDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Usuário não encontrado. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar papel do usuário", e);
        }
    }

    // Verifica se o usuário é ADMIN
    private boolean isAdmin(PlanoUsuarioDTO usuario) {
        return "ADMIN".equals(usuario.getPapel());
    }

    // Verifica se o usuário é DEVELOPER
    private boolean isDeveloper(PlanoUsuarioDTO usuario) {
        return "DEVELOPER".equals(usuario.getPapel());
    }

    // Lista todos os feedbacks cadastrados (ADMIN, DEVELOPER)
    public List<Feedback> listarFeedbacks(String jwtToken) {
        PlanoUsuarioDTO usuario = achaUsuario(jwtToken);

        if (!isAdmin(usuario) && !isDeveloper(usuario)) {
            throw new SecurityException("Acesso negado. Usuário sem permissão para listar feedbacks.");
        }

        List<Feedback> feedbacks = feedbackRepository.findAll();

        if (feedbacks == null) {
            return Collections.emptyList(); // Retorna uma lista vazia se nenhum feedback for encontrado
        }

        return feedbacks;
    }

    // Lista um feedback específico a partir do id (ADMIN, DEVELOPER)
    public Feedback listarFeedbackPorId(String jwtToken, String id) {
        PlanoUsuarioDTO usuario = achaUsuario(jwtToken);

        if (!isAdmin(usuario) && !isDeveloper(usuario)) {
            throw new SecurityException("Acesso negado. Usuário sem permissão para listar feedbacks.");
        }

        Optional<Feedback> feedback = feedbackRepository.findById(id); // String MongoId

        // Verifica se o feedback foi encontrado
        if (feedback.isEmpty()) {
            throw new IllegalArgumentException("Feedback não encontrado.");
        }

        return feedback.get();
    }

    // Cria um novo feedback (ADMIN)
    public Feedback criarFeedback(String jwtToken, Feedback feedback) {
        PlanoUsuarioDTO usuario = achaUsuario(jwtToken);

        if (!isAdmin(usuario)) {
            throw new SecurityException("Acesso negado. Apenas ADMIN pode criar feedbacks.");
        }

        // Componentes de feedback
        feedback.setAutor(usuario.getNome());
        feedback.setComentario(feedback.getComentario());
        feedback.setData(LocalDateTime.now());

        feedbackRepository.save(feedback);

        return feedback;
    }

    // Exclui um feedback com o id (ADMIN)
    public void excluirFeedback(String jwtToken, String id) {
        PlanoUsuarioDTO usuario = achaUsuario(jwtToken);

        if (!isAdmin(usuario)) {
            throw new SecurityException("Acesso negado. Apenas ADMIN pode excluir feedbacks.");
        }

        Optional<Feedback> feedback = feedbackRepository.findById(id);

        if (feedback.isEmpty()) {
            throw new IllegalArgumentException("Feedback não encontrado.");
        }

        feedbackRepository.delete(feedback.get());
    }



}

