package com.universityweb.writingresult;

import com.fasterxml.jackson.databind.JsonNode;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.section.service.SectionService;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingresult.req.WritingResultFilterReq;
import com.universityweb.writingresult.service.WritingResultService;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.service.WritingTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.universityweb.common.auth.mapper.UserMapper.objectMapper;

@RestController
@RequestMapping("/api/v1/writing-results")
@Tag(name = "Writing Results")
public class WritingResultController
        extends BaseController<WritingResult, WritingResultDTO, Long, WritingResultService> {

    @Value("${gemini.api-key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.url}")
    private String GEMINI_URL;

    private final AuthService authService;
    private final SectionService sectionService;
    private final WritingTaskService writingTaskService;

    @Autowired
    public WritingResultController(
            WritingResultService service,
            AuthService authService,
            SectionService sectionService,
            WritingTaskService writingTaskService
    ) {
        super(service);
        this.authService = authService;
        this.sectionService = sectionService;
        this.writingTaskService = writingTaskService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/submit")
    @Override
    public ResponseEntity<WritingResultDTO> create(WritingResultDTO dto) {
        return super.create(dto);
    }

    @Override
    public void preCreate(WritingResultDTO dto) {
        String curUsername = authService.getCurrentUsername();
        dto.setOwnerUsername(curUsername);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<WritingResultDTO> update(Long id, WritingResultDTO dto) {
        return super.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }

    @PostMapping("/get-results")
    public ResponseEntity<Page<WritingResultDTO>> getMyWritingResults(
            @RequestBody WritingResultFilterReq filterReq
    ) {
        String username = authService.getCurrentUsername();
        Long writingTaskId = filterReq.getWritingTaskId();
        WritingTask task = writingTaskService.getEntityById(writingTaskId);
        boolean isAccessible = sectionService.isAccessible(username, task.getSectionId());
        if (!isAccessible) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<WritingResultDTO> results = service.getByFilters(filterReq);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/support-by-ai")
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

    @PostMapping("/image-to-text")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Convert ảnh sang base64
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 2. Tạo JSON payload gửi đến Gemini
            String jsonPayload = """
             {
               "contents": [
                 {
                   "parts": [
                     {
                       "text": "Extract all visible text from this image."
                     },
                     {
                       "inline_data": {
                         "mime_type": "%s",
                         "data": "%s"
                       }
                     }
                   ]
                 }
               ]
             }
             """.formatted(file.getContentType(), base64Image);

            // 3. Sử dụng RestTemplate như hàm generate
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            String urlWithKey = GEMINI_URL + "?key=" + GEMINI_API_KEY;
            ResponseEntity<String> response = restTemplate.exchange(urlWithKey, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode textNode = jsonNode
                        .path("candidates")
                        .path(0)
                        .path("content")
                        .path("parts")
                        .path(0)
                        .path("text");

                if (textNode.isMissingNode() || textNode.asText().isEmpty()) {
                    return ResponseEntity.ok("Không tìm thấy văn bản trong ảnh.");
                }

                return ResponseEntity.ok(textNode.asText());
            } else {
                throw new ResponseStatusException(response.getStatusCode(), "Gemini API error: " + response.getBody());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    @PostMapping("/chat-with-ai")
    public ResponseEntity<?> chatWithAI(@RequestBody WritingResult writingResult) {
        try {
            // 1. Tạo JSON payload gửi đến Gemini
            String jsonPayload = """
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
         """.formatted(writingResult.getSubmittedText());

            // 2. Gửi request tới Gemini API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            String urlWithKey = GEMINI_URL + "?key=" + GEMINI_API_KEY;
            ResponseEntity<String> response = restTemplate.exchange(urlWithKey, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode textNode = jsonNode
                        .path("candidates")
                        .path(0)
                        .path("content")
                        .path("parts")
                        .path(0)
                        .path("text");

                if (textNode.isMissingNode() || textNode.asText().isEmpty()) {
                    return ResponseEntity.ok("Không tìm thấy câu trả lời.");
                }

                return ResponseEntity.ok(textNode.asText());
            } else {
                throw new ResponseStatusException(response.getStatusCode(), "Gemini API error: " + response.getBody());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", e.getMessage()));
        }
    }

}
