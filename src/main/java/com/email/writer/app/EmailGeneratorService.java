package com.email.writer.app;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
//@NoArgsConstructor(force = true)
//@AllArgsConstructor
@Slf4j
@Service
public class EmailGeneratorService {
    //@Autowired
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String generateEmailReply(EmailRequest emailRequest){
        //build prompt
        String prompt=buildPrompt(emailRequest);
        //craft a request as of api

        String requestBody = """
{
  "contents": [
    {
      "parts": [
        {
          "text": "%s"
        }
      ]
    }
  ]
}
""".formatted(prompt);
        log.info("REQUEST BODY: " + requestBody);

        String uri = geminiApiUrl + "?key=" + geminiApiKey;
        log.info(uri);
        String response;
        //do request  and get response

//        try{
            response = webClient.post()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("API ERROR: " + body))
                    )
                    .bodyToMono(String.class)
                    .block();
//        }
//        catch (Exception e){
//            return e.getMessage();
//        }

        log.info("RESPONSE: " + response);


        //extract response and return response
        return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        }catch(Exception e){
            return "Error!Processing request"+e.getMessage();
        }

    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt =new StringBuilder();
        prompt.append("Generate a email reply for the following email.");
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\n Original email: \n").append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}
