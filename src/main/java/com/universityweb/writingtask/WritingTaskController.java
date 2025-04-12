package com.universityweb.writingtask;

import com.fasterxml.jackson.databind.JsonNode;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.writingresult.WritingResult;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.service.WritingTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.universityweb.common.auth.mapper.UserMapper.objectMapper;

@RestController
@RequestMapping("/api/v1/writing-tasks")
@Tag(name = "Writing Tasks")
public class WritingTaskController
        extends BaseController<WritingTask, WritingTaskDTO, Long, WritingTaskService> {
    @Value("${gemini.api-key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.url}")
    private String GEMINI_URL;
    @Autowired
    public WritingTaskController(
            WritingTaskService service
    ) {
        super(service);
    }
    @PostMapping("/submit")
    public ResponseEntity<?> generate(@RequestBody WritingResult writingResult) {


        try {
            // Đọc file prompt.txt trong resources folder
            var resource = new ClassPathResource("prompt.txt");
            String prompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            var textFinal = prompt + writingResult.getSubmittedText();
            // Tạo payload
            String requestBody = String.format("""
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": %s }
                      ]
                    }
                  ]
                }
            """, objectMapper.writeValueAsString(textFinal));

            // Gửi yêu cầu đến Gemini API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            String urlWithKey = GEMINI_URL + "?key=" + GEMINI_API_KEY;
            ResponseEntity<String> response = restTemplate.exchange(urlWithKey, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String resultText = jsonNode.at("/candidates/0/content/parts/0/text").asText();

                // Tìm đoạn JSON trong code block
                Pattern pattern = Pattern.compile("```json\\s*([\\s\\S]*?)\\s*```");
                Matcher matcher = pattern.matcher(resultText);
                String jsonContent = matcher.find() ? matcher.group(1).trim() : resultText;

                // Parse JSON kết quả
                try {
                    JsonNode parsed = objectMapper.readTree(jsonContent);
                    if (parsed.has("resultWriting")) {
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(parsed.get("resultWriting"));
                    } else {
                        return ResponseEntity.ok(parsed);
                    }
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body(
                            objectMapper.createObjectNode()
                                    .put("error", "JSON parsing error")
                                    .put("message", e.getMessage())
                                    .put("content", jsonContent)
                                    .put("content_length", jsonContent.length())
                                    .put("first_100_chars", jsonContent.substring(0, Math.min(100, jsonContent.length())))
                    );
                }
            } else {
                throw new ResponseStatusException(response.getStatusCode(), "Gemini API error: " + response.getBody());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", e.getMessage()));
        }
    }
}
