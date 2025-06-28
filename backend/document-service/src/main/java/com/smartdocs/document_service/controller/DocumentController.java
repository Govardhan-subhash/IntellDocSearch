// package com.smartdocs.document_service.controller;
// import com.smartdocs.document_service.dto.ChatHistoryRequest;
// import com.smartdocs.document_service.model.DocumentChunk;
// import com.smartdocs.document_service.model.ChatHistory;
// import com.smartdocs.document_service.service.DocumentService;
// import com.smartdocs.document_service.util.JwtUtil;
// import com.smartdocs.document_service.dto.UserFileDTO;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import java.util.*;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.List;

// @RestController
// @RequestMapping("/documents")
// public class DocumentController {

//     private final DocumentService documentService;
//     private final JwtUtil jwtUtil;

   

//     @GetMapping("/chat/history")
//     public ResponseEntity<List<ChatHistory>> getChatHistory(@RequestHeader("Authorization") String token) {
//         logger.debug("Received request to get chat history");
//         String userId = extractUserIdFromToken(token);
//         logger.debug("Extracted userId from token: {}", userId);

//         List<ChatHistory> history = documentService.getChatHistoryForUser(userId);
//         logger.debug("Returning chat history with {} entries", history.size());

//         return ResponseEntity.ok(history);
//     }
//     @PostMapping("/chat/history/save")
//     public ResponseEntity<String> saveChatHistory(
//         @RequestHeader("Authorization") String token,
//         @RequestBody ChatHistoryRequest request) {
//             System.out.println(String.format("Received question: '%s'", request.getQuestion()));
//             System.out.println(String.format("Received response: '%s'", request.getResponse()));
            
//         String userId = extractUserIdFromToken(token);
//         logger.debug("Extracted userId: {}", userId);
    
//         if (request.getQuestion() == null || request.getResponse() == null) {
//             return ResponseEntity.badRequest().body("Question or Response is null");
//         }
    
//         documentService.saveChatHistory(userId, request.getQuestion(), request.getResponse());
    
//         return ResponseEntity.ok("Chat history saved.");
//     }
    

//     // Placeholder: Implement your token extraction logic here


//     public DocumentController(DocumentService documentService, JwtUtil jwtUtil) {
//         this.documentService = documentService;
//         this.jwtUtil = jwtUtil;
//     }
//     private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

//     // Upload a file
//     @PostMapping("/upload")
//     public ResponseEntity<String> uploadFile(
//             @RequestParam("file") MultipartFile file,
//             @RequestHeader("Authorization") String token) {
//         try {
//             String userId = extractUserIdFromToken(token);
//             documentService.processAndStoreFile(file, userId);
//             return ResponseEntity.ok("File uploaded and processed successfully!");
//         } catch (Exception e) {
//             // Handle exception, maybe log and return error response
//             e.printStackTrace();
//             return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
//         }
//     }
    
//     // Get all files uploaded by user with their document IDs
//     @GetMapping("/user/files")
//     public ResponseEntity<List<UserFileDTO>> getUserFilesWithDocumentIds(
//             @RequestHeader("Authorization") String token) {
//         String userId = extractUserIdFromToken(token);
//         List<UserFileDTO> files = documentService.getUserFilesWithDocumentIds(userId);
//         return ResponseEntity.ok(files);
//     }

//     // Get all chunks of a specific file by filename for user
//     @GetMapping("/user/files/{fileName}/chunks")
//     public ResponseEntity<List<DocumentChunk>> getChunksByUserAndFile(
//             @RequestHeader("Authorization") String token,
//             @PathVariable String fileName) {
//         String userId = extractUserIdFromToken(token);
//         List<DocumentChunk> chunks = documentService.getChunksByUserAndFileName(userId, fileName);
//         return ResponseEntity.ok(chunks);
//     }

//     // Get full concatenated content of a document by filename
//     @GetMapping("/user/files/{fileName}/content")
//     public ResponseEntity<String> getDocumentContent(
//             @RequestHeader("Authorization") String token,
//             @PathVariable String fileName) {
//         String userId = extractUserIdFromToken(token);
//         String fullContent = documentService.getFullDocumentContent(userId, fileName);
//         return ResponseEntity.ok(fullContent);
//     }

//     // Delete all chunks of a file by documentId for user
//     @DeleteMapping("/user/files")
//     public ResponseEntity<String> deleteFileChunks(
//             @RequestHeader("Authorization") String token,
//             @RequestParam String documentId) {
//         String userId = extractUserIdFromToken(token);
//         documentService.deleteChunksByUserAndDocumentId(userId, documentId);
//         return ResponseEntity.ok("Deleted document with documentId: " + documentId);
//     }

// //     // Get chat history for the logged-in user
// //     @GetMapping("/chat/history")
// //     public ResponseEntity<List<ChatHistory>> getChatHistory(
// //             @RequestHeader("Authorization") String token) {
// //         logger.debug("Received request to get chat history");
// //         String userId = extractUserIdFromToken(token);
// //         logger.debug("Extracted userId from token: {}", userId);

// //         List<ChatHistory> history = documentService.getChatHistoryForUser(userId);
// //         logger.debug("Returning chat history with {} entries", history.size());

// //         return ResponseEntity.ok(history);
// //     }

// //     @PostMapping("/chat/history/save")
// // public ResponseEntity<String> saveChatHistory(
// //         @RequestHeader("Authorization") String token,
// //         @RequestBody ChatHistory request) {
// //     logger.debug("Received request to save chat history: question='{}'", request.getQuestion());

// //     String userId = extractUserIdFromToken(token);
// //     logger.debug("Extracted userId from token: {}", userId);

// //     documentService.saveChatHistory(userId, request.getQuestion(), request.getResponse());

// //     logger.debug("Chat history saved successfully for userId: {}", userId);
// //     return ResponseEntity.ok("Chat history saved successfully.");
// // }


    
//     // Helper method to extract userId from JWT token
//     private String extractUserIdFromToken(String token) {
//         if (token == null || !token.startsWith("Bearer ")) {
//             throw new IllegalArgumentException("Invalid or missing Authorization header");
//         }
//         String jwt = token.substring(7);
//         return jwtUtil.extractUsername(jwt);
//     }
// }


// package com.smartdocs.document_service.controller;

// import com.smartdocs.document_service.dto.ChatHistoryRequest;
// import com.smartdocs.document_service.model.DocumentChunk;
// import com.smartdocs.document_service.model.ChatHistory;
// import com.smartdocs.document_service.service.DocumentService;
// import com.smartdocs.document_service.dto.UserFileDTO;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.List;

// @RestController
// @RequestMapping("/documents")
// public class DocumentController {

//     private final DocumentService documentService;

//     public DocumentController(DocumentService documentService) {
//         this.documentService = documentService;
//     }

//     private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

//     // Get chat history for authenticated user
//     @GetMapping("/chat/history")
//     public ResponseEntity<List<ChatHistory>> getChatHistory() {
//         // Here you get the userId from your internal context (e.g. SecurityContextHolder)
//         // Or pass it via header forwarded from API Gateway if needed

//         String userId = getCurrentUserId();  // Implement this method based on your auth context or header

//         logger.debug("Returning chat history for userId: {}", userId);
//         List<ChatHistory> history = documentService.getChatHistoryForUser(userId);
//         return ResponseEntity.ok(history);
//     }

//     @PostMapping("/chat/history/save")
//     public ResponseEntity<String> saveChatHistory(@RequestBody ChatHistoryRequest request) {
//         String userId = getCurrentUserId();

//         logger.debug("Saving chat history for userId: {}", userId);
//         if (request.getQuestion() == null || request.getResponse() == null) {
//             return ResponseEntity.badRequest().body("Question or Response is null");
//         }

//         documentService.saveChatHistory(userId, request.getQuestion(), request.getResponse());
//         return ResponseEntity.ok("Chat history saved.");
//     }

//     // Upload a file
//     @PostMapping("/upload")
//     public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//         try {
//             String userId = getCurrentUserId();
//             documentService.processAndStoreFile(file, userId);
//             return ResponseEntity.ok("File uploaded and processed successfully!");
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
//         }
//     }

//     // Get all files uploaded by user with their document IDs
//     @GetMapping("/user/files")
//     public ResponseEntity<List<UserFileDTO>> getUserFilesWithDocumentIds() {
//         String userId = getCurrentUserId();
//         List<UserFileDTO> files = documentService.getUserFilesWithDocumentIds(userId);
//         return ResponseEntity.ok(files);
//     }

//     // Get all chunks of a specific file by filename for user
//     @GetMapping("/user/files/{fileName}/chunks")
//     public ResponseEntity<List<DocumentChunk>> getChunksByUserAndFile(@PathVariable String fileName) {
//         String userId = getCurrentUserId();
//         List<DocumentChunk> chunks = documentService.getChunksByUserAndFileName(userId, fileName);
//         return ResponseEntity.ok(chunks);
//     }

//     // Get full concatenated content of a document by filename
//     @GetMapping("/user/files/{fileName}/content")
//     public ResponseEntity<String> getDocumentContent(@PathVariable String fileName) {
//         String userId = getCurrentUserId();
//         String fullContent = documentService.getFullDocumentContent(userId, fileName);
//         return ResponseEntity.ok(fullContent);
//     }

//     // Delete all chunks of a file by documentId for user
//     @DeleteMapping("/user/files")
//     public ResponseEntity<String> deleteFileChunks(@RequestParam String documentId) {
//         String userId = getCurrentUserId();
//         documentService.deleteChunksByUserAndDocumentId(userId, documentId);
//         return ResponseEntity.ok("Deleted document with documentId: " + documentId);
//     }

//     // TODO: Implement this to get userId from context or forwarded header from API Gateway
//     private String getCurrentUserId() {
//         // Example: extract from SecurityContext or a custom header forwarded by API Gateway
//         // If you want, you can inject a custom header via API Gateway like X-User-Id and read it here:
//         // return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//         //     .getRequest().getHeader("X-User-Id");

//         throw new UnsupportedOperationException("Implement getCurrentUserId() based on your setup");
//     }
// }


package com.smartdocs.document_service.controller;

import com.smartdocs.document_service.dto.ChatHistoryRequest;
import com.smartdocs.document_service.dto.UserFileDTO;
import com.smartdocs.document_service.model.ChatHistory;
import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.service.DocumentService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    // Get chat history for authenticated user
    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatHistory>> getChatHistory() {
        String userId = getCurrentUserId();
        logger.debug("Returning chat history for userId: {}", userId);
        List<ChatHistory> history = documentService.getChatHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/chat/history/save")
    public ResponseEntity<String> saveChatHistory(@RequestBody ChatHistoryRequest request) {
        String userId = getCurrentUserId();

        logger.debug("Saving chat history for userId: {}", userId);

        if (request.getQuestion() == null || request.getResponse() == null) {
            return ResponseEntity.badRequest().body("Question or Response is null");
        }

        documentService.saveChatHistory(userId, request.getQuestion(), request.getResponse());
        return ResponseEntity.ok("Chat history saved.");
    }

    // Upload a file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    System.out.println(">>> DocumentService: /upload endpoint hit! File name: " + file.getOriginalFilename());

        try {
            String userId = getCurrentUserId();
            documentService.processAndStoreFile(file, userId);
            return ResponseEntity.ok("File uploaded and processed successfully!");
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    // Get all files uploaded by user with their document IDs
    @GetMapping("/user/files")
    public ResponseEntity<List<UserFileDTO>> getUserFilesWithDocumentIds() {
        String userId = getCurrentUserId();
        List<UserFileDTO> files = documentService.getUserFilesWithDocumentIds(userId);
        return ResponseEntity.ok(files);
    }

    // Get all chunks of a specific file by filename for user
    @GetMapping("/user/files/{fileName}/chunks")
    public ResponseEntity<List<DocumentChunk>> getChunksByUserAndFile(@PathVariable String fileName) {
        String userId = getCurrentUserId();
        List<DocumentChunk> chunks = documentService.getChunksByUserAndFileName(userId, fileName);
        return ResponseEntity.ok(chunks);
    }

    // Get full concatenated content of a document by filename
    @GetMapping("/user/files/{fileName}/content")
    public ResponseEntity<String> getDocumentContent(@PathVariable String fileName) {
        String userId = getCurrentUserId();
        String fullContent = documentService.getFullDocumentContent(userId, fileName);
        return ResponseEntity.ok(fullContent);
    }

    // Delete all chunks of a file by documentId for user
    @DeleteMapping("/user/files")
    public ResponseEntity<String> deleteFileChunks(@RequestParam String documentId) {
        String userId = getCurrentUserId();
        documentService.deleteChunksByUserAndDocumentId(userId, documentId);
        return ResponseEntity.ok("Deleted document with documentId: " + documentId);
    }

    /**
     * Reads the current user ID from the request header "X-User-Id".
     * This header should be forwarded by your API Gateway after JWT validation.
     */
    private String getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request attributes found");
        }
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new IllegalStateException("User ID header 'X-User-Id' is missing");
        }
        return userId;
    }
}
