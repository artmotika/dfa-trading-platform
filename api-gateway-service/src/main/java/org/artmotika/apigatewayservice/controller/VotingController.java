package org.artmotika.apigatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.artmotika.common.dto.VoteCastRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VotingController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/{votingId}/cast")
    public ResponseEntity<String> castVote(@PathVariable String votingId, @RequestBody VoteCastRequestDto req) {
        log.info("Casting vote for votingId: {}, userId: {}", votingId, req.getUserId());
        req.setVotingId(votingId);
        kafkaTemplate.send("vote.cast", req);
        return ResponseEntity.accepted().body("Vote cast command sent");
    }
}
