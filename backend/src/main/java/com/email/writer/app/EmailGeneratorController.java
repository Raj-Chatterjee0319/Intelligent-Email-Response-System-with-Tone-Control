package com.email.writer.app;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins="*")
public class EmailGeneratorController {

        private final EmailGeneratorService emailGeneratorService;

    public EmailGeneratorController(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @PostMapping("/generate")
        public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
            log.info("hello");
            String response = emailGeneratorService.generateEmailReply(emailRequest);
            return ResponseEntity.ok(response);
        }
}
