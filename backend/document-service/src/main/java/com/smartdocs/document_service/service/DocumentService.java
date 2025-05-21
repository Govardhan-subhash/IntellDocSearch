

// @Service
// public class DocumentService {

//     @Autowired
//     private DocumentChunkRepository documentChunkRepository;

//     @Autowired
//     private MongoTemplate mongoTemplate;

//     public void processAndStoreFile(MultipartFile file, String userId) throws Exception {
//         // Generate a unique document ID
//         String documentId = UUID.randomUUID().toString();

//         // Extract content based on file type
//         String content = extractContent(file);

//         if (content == null || content.isBlank()) {
//             throw new IllegalArgumentException("Failed to extract content or file is empty.");
//         }

//         // Chunk content
//         List<String> chunks = splitIntoChunks(content, 1000);

//         // Store in MongoDB
//         List<DocumentChunk> documentChunks = new ArrayList<>();
//         for (int i = 0; i < chunks.size(); i++) {
//             DocumentChunk chunk = new DocumentChunk();
//             chunk.setDocumentId(documentId);
//             chunk.setUserId(userId);
//             chunk.setChunkIndex(i);
//             chunk.setContent(chunks.get(i));
//             chunk.setFileName(file.getOriginalFilename());
//             chunk.setFileType(file.getContentType());
//             documentChunks.add(chunk);
//         }

//         documentChunkRepository.saveAll(documentChunks);
//     }

//     private String extractContent(MultipartFile file) throws Exception {
//         String contentType = file.getContentType();
//         String fileName = file.getOriginalFilename();
//         InputStream inputStream = file.getInputStream();

//         if (contentType == null || fileName == null) {
//             throw new IllegalArgumentException("Missing file metadata.");
//         }

//         if (contentType.equals("text/plain") || fileName.endsWith(".txt")) {
//             return new String(file.getBytes());
//         } else if (contentType.equals("application/pdf") || fileName.endsWith(".pdf")) {
//             try (PDDocument document = PDDocument.load(inputStream)) {
//                 PDFTextStripper stripper = new PDFTextStripper();
//                 return stripper.getText(document);
//             }
//         } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || fileName.endsWith(".docx")) {
//             try (XWPFDocument doc = new XWPFDocument(inputStream)) {
//                 StringBuilder sb = new StringBuilder();
//                 doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
//                 return sb.toString();
//             }
//         } else if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || fileName.endsWith(".pptx")) {
//             try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
//                 StringBuilder sb = new StringBuilder();
//                 for (XSLFSlide slide : ppt.getSlides()) {
//                     for (XSLFShape shape : slide.getShapes()) {
//                         if (shape.getShapeName() != null && shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
//                             sb.append(((org.apache.poi.xslf.usermodel.XSLFTextShape) shape).getText()).append("\n");
//                         }
//                     }
//                     sb.append("\n");
//                 }
//                 return sb.toString();
//             }
//         } else {
//             throw new IllegalArgumentException("Unsupported file type: " + contentType);
//         }
//     }

//     private List<String> splitIntoChunks(String content, int chunkSize) {
//         List<String> chunks = new ArrayList<>();
//         int length = content.length();
//         for (int i = 0; i < length; i += chunkSize) {
//             chunks.add(content.substring(i, Math.min(length, i + chunkSize)));
//         }
//         return chunks;
//     }

//     // New method: get distinct filenames for a user
//     public List<String> getDistinctFileNamesByUser(String userId) {
//         return mongoTemplate.query(DocumentChunk.class)
//                 .distinct("fileName")
//                 .matching(Criteria.where("userId").is(userId))
//                 .as(String.class)
//                 .all();
//     }

//     // New method: get chunks by userId and fileName
//     public List<DocumentChunk> getChunksByUserAndFileName(String userId, String fileName) {
//         return documentChunkRepository.findByUserIdAndFileName(userId, fileName);
//     }
// }
     
package com.smartdocs.document_service.service;

import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.repository.DocumentChunkRepository;
import com.smartdocs.document_service.model.User;
import com.smartdocs.document_service.repository.UserRepository;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void createIndexes() {
        var collection = mongoTemplate.getCollection("documentchunks");
        var options = new com.mongodb.client.model.IndexOptions().unique(true);
        collection.createIndex(
                com.mongodb.client.model.Indexes.compoundIndex(
                        com.mongodb.client.model.Indexes.ascending("userId"),
                        com.mongodb.client.model.Indexes.ascending("fileHash")),
                options);
    }

    public String computeFileHash(byte[] fileBytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public boolean isDuplicateFile(String userId, String fileHash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("fileHash").is(fileHash));
        return mongoTemplate.exists(query, DocumentChunk.class);
    }

    public void processAndStoreFile(MultipartFile file, String userId) throws Exception {
        byte[] fileBytes = file.getBytes();
        String fileHash = computeFileHash(fileBytes);

        if (isDuplicateFile(userId, fileHash)) {
            throw new IllegalArgumentException("Duplicate file upload detected.");
        }

        String documentId = UUID.randomUUID().toString();
        String content = extractContent(file);

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Failed to extract content or file is empty.");
        }

        List<String> chunks = splitIntoChunks(content, 1000);

        List<DocumentChunk> documentChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(documentId);
            chunk.setUserId(userId);
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            chunk.setFileName(file.getOriginalFilename());
            chunk.setFileType(file.getContentType());
            chunk.setFileHash(fileHash);
            documentChunks.add(chunk);
        }

        documentChunkRepository.saveAll(documentChunks);

        // 🔗 Save document ID to the user's list of documentIds
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<String> documentIds = user.getDocumentIds();
            if (documentIds == null) {
                documentIds = new ArrayList<>();
            }
            documentIds.add(documentId);
            user.setDocumentIds(documentIds);
            userRepository.save(user);
        }
    }

    private String extractContent(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        if (contentType == null || fileName == null) {
            throw new IllegalArgumentException("Missing file metadata.");
        }

        if (contentType.equals("text/plain") || fileName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else if (contentType.equals("application/pdf") || fileName.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(inputStream)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || fileName.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(inputStream)) {
                StringBuilder sb = new StringBuilder();
                doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
                return sb.toString();
            }
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || fileName.endsWith(".pptx")) {
            try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
                StringBuilder sb = new StringBuilder();
                for (XSLFSlide slide : ppt.getSlides()) {
                    for (XSLFShape shape : slide.getShapes()) {
                        if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
                            sb.append(((org.apache.poi.xslf.usermodel.XSLFTextShape) shape).getText()).append("\n");
                        }
                    }
                    sb.append("\n");
                }
                return sb.toString();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    private List<String> splitIntoChunks(String content, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(content.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }

    public List<String> getDistinctFileNamesByUser(String userId) {
        return mongoTemplate.query(DocumentChunk.class)
                .distinct("fileName")
                .matching(Criteria.where("userId").is(userId))
                .as(String.class)
                .all();
    }

    public List<DocumentChunk> getChunksByUserAndFileName(String userId, String fileName) {
        return documentChunkRepository.findByUserIdAndFileName(userId, fileName);
    }

@Transactional
public void deleteChunksByUserAndFileName(String userId, String fileName) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(userId).and("fileName").is(fileName));

    long countBefore = mongoTemplate.count(query, DocumentChunk.class);
    System.out.println("Chunks found before delete: " + countBefore);

    if (countBefore == 0) {
        System.out.println("No matching chunks found for deletion!");
        return;
    }

    DeleteResult result = mongoTemplate.remove(query, DocumentChunk.class);

    System.out.println("Deleted count: " + result.getDeletedCount());

    long countAfter = mongoTemplate.count(query, DocumentChunk.class);
    System.out.println("Chunks remaining after delete: " + countAfter);
}

       

    // 🔥 New: Get full document content for summarization
    public String getFullDocumentContent(String userId, String fileName) {
        List<DocumentChunk> chunks = getChunksByUserAndFileName(userId, fileName);
        StringBuilder sb = new StringBuilder();
        for (DocumentChunk chunk : chunks) {
            sb.append(chunk.getContent()).append(" ");
        }
        return sb.toString().trim();
    }
}
