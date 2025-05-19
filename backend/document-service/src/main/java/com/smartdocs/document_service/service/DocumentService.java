package com.smartdocs.document_service.service;

import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.repository.DocumentChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    public void processAndStoreFile(MultipartFile file, String userId) throws Exception {
        // Generate a unique document ID
        String documentId = UUID.randomUUID().toString();

        // Split the file content into chunks
        String content = new String(file.getBytes());
        List<String> chunks = splitIntoChunks(content, 1000); // Split into chunks of 1000 characters

        // Save each chunk in MongoDB
        List<DocumentChunk> documentChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(documentId);
            chunk.setUserId(userId);
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            documentChunks.add(chunk);
        }

        documentChunkRepository.saveAll(documentChunks);
    }

    private List<String> splitIntoChunks(String content, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(content.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }
}
