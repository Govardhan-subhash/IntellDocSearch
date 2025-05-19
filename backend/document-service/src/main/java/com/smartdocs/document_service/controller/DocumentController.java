package com.smartdocs.document_service.controller;

import com.smartdocs.document_service.service.DocumentService;
import com.smartdocs.document_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
            documentService.processAndStoreFile(file, userId);
            return ResponseEntity.ok("File uploaded and processed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }
}
