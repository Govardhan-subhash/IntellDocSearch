package com.smartdocs.document_service.controller;

import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.service.DocumentService;
import com.smartdocs.document_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private JwtUtil jwtUtil;

    // Upload a file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtil.extractUsername(jwt);
            documentService.processAndStoreFile(file, userId);
            return ResponseEntity.ok("File uploaded and processed successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported file type or duplicate: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    // Get all filenames uploaded by the user
    @GetMapping("/user/files")
    public ResponseEntity<List<String>> getUserFileNames(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtil.extractUsername(jwt);
            List<String> fileNames = documentService.getDistinctFileNamesByUser(userId);
            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all chunks of a specific file
    @GetMapping("/user/files/{fileName}/chunks")
    public ResponseEntity<List<DocumentChunk>> getChunksByUserAndFile(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileName) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtil.extractUsername(jwt);
            List<DocumentChunk> chunks = documentService.getChunksByUserAndFileName(userId, fileName);
            return ResponseEntity.ok(chunks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get full document content (for summarization)
    @GetMapping("/user/files/{fileName}/content")
    public ResponseEntity<String> getDocumentContent(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileName) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtil.extractUsername(jwt);
            String fullContent = documentService.getFullDocumentContent(userId, fileName);
            return ResponseEntity.ok(fullContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve full document content: " + e.getMessage());
        }
    }

    // NEW: Delete all chunks for a file uploaded by the user
    @DeleteMapping("/user/files/{fileName}")
    public ResponseEntity<String> deleteFileChunks(
            @RequestHeader("Authorization") String token,
            @PathVariable String fileName) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtil.extractUsername(jwt);
            documentService.deleteChunksByUserAndFileName(userId, fileName);
            return ResponseEntity.ok("Deleted all chunks for file: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete chunks for file: " + fileName + " - " + e.getMessage());
        }
    }
}
    
