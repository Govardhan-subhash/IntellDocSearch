// package com.smartdocs.document_service.service;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// import com.smartdocs.document_service.dto.UserFileDTO;
// import com.smartdocs.document_service.model.DocumentChunk;
// import com.smartdocs.document_service.model.User;
// import com.smartdocs.document_service.repository.DocumentChunkRepository;
// import com.smartdocs.document_service.repository.UserRepository;
// import com.smartdocs.document_service.model.ChatHistory;
// import com.smartdocs.document_service.repository.ChatHistoryRepository;

// import com.mongodb.client.result.DeleteResult;

// import org.apache.poi.xslf.usermodel.XMLSlideShow;
// import org.apache.poi.xslf.usermodel.XSLFShape;
// import org.apache.poi.xslf.usermodel.XSLFSlide;
// import org.apache.poi.xwpf.usermodel.XWPFDocument;
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.text.PDFTextStripper;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import javax.annotation.PostConstruct;
// import java.io.InputStream;
// import java.security.MessageDigest;
// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class DocumentService {

//     private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
//     private static final int DEFAULT_CHUNK_SIZE = 1000;
//     @Autowired
//     private ChatHistoryRepository chatHistoryRepository;
//     @Autowired
//     private DocumentChunkRepository documentChunkRepository;

//     @Autowired
//     private MongoTemplate mongoTemplate;

//     @Autowired
//     private UserRepository userRepository;

//     @PostConstruct
//     public void createIndexes() {
//         try {
//             var collection = mongoTemplate.getCollection("document_chunks");
//             var options = new com.mongodb.client.model.IndexOptions().unique(true);
//             collection.createIndex(
//                 com.mongodb.client.model.Indexes.compoundIndex(
//                     com.mongodb.client.model.Indexes.ascending("userId"),
//                     com.mongodb.client.model.Indexes.ascending("fileHash"),
//                     com.mongodb.client.model.Indexes.ascending("chunkIndex")
//                 ),
//                 options
//             );
//             logger.info("Created unique index on userId, fileHash, and chunkIndex");
//         } catch (Exception e) {
//             logger.warn("Index creation failed or index already exists: {}", e.getMessage());
//         }
//     }
    

//     public String computeFileHash(byte[] fileBytes) throws Exception {
//         MessageDigest digest = MessageDigest.getInstance("SHA-256");
//         byte[] hashBytes = digest.digest(fileBytes);
//         StringBuilder sb = new StringBuilder();
//         for (byte b : hashBytes) {
//             sb.append(String.format("%02x", b));
//         }
//         return sb.toString();
//     }

//     public boolean isDuplicateFile(String userId, String fileHash) {
//         Query query = new Query();
//         query.addCriteria(Criteria.where("userId").is(userId).and("fileHash").is(fileHash));
//         return mongoTemplate.exists(query, DocumentChunk.class);
//     }

//     public void processAndStoreFile(MultipartFile file, String userId) throws Exception {
//         byte[] fileBytes = file.getBytes();
//         String fileHash = computeFileHash(fileBytes);

//         if (isDuplicateFile(userId, fileHash)) {
//             throw new DuplicateFileException("Duplicate file upload detected.");
//         }

//         String documentId = UUID.randomUUID().toString();
//         String content = extractContent(file);

//         if (content == null || content.isBlank()) {
//             throw new InvalidFileContentException("Failed to extract content or file is empty.");
//         }

//         List<String> chunks = splitIntoChunks(content, DEFAULT_CHUNK_SIZE);

//         List<DocumentChunk> documentChunks = new ArrayList<>();
//         for (int i = 0; i < chunks.size(); i++) {
//             DocumentChunk chunk = new DocumentChunk();
//             chunk.setDocumentId(documentId);
//             chunk.setUserId(userId);
//             chunk.setChunkIndex(i);
//             chunk.setContent(chunks.get(i));
//             chunk.setFileName(file.getOriginalFilename());
//             chunk.setFileType(file.getContentType());
//             chunk.setFileHash(fileHash);
//             documentChunks.add(chunk);
//         }

//         documentChunkRepository.saveAll(documentChunks);
//         logger.info("Saved {} chunks for documentId {} userId {}", documentChunks.size(), documentId, userId);

//         // Save document ID to the user's list of documentIds
//         User user = userRepository.findById(userId).orElse(null);
//         if (user != null) {
//             List<String> documentIds = user.getDocumentIds();
//             if (documentIds == null) {
//                 documentIds = new ArrayList<>();
//             }
//             documentIds.add(documentId);
//             user.setDocumentIds(documentIds);
//             userRepository.save(user);
//             logger.info("Added documentId {} to userId {}", documentId, userId);
//         }
//     }

//     private String extractContent(MultipartFile file) throws Exception {
//         String contentType = file.getContentType();
//         String fileName = file.getOriginalFilename();
//         InputStream inputStream = file.getInputStream();

//         if (contentType == null || fileName == null) {
//             throw new InvalidFileException("Missing file metadata.");
//         }

//         String lowerFileName = fileName.toLowerCase();

//         if (contentType.equals("text/plain") || lowerFileName.endsWith(".txt")) {
//             return new String(file.getBytes());
//         } else if (contentType.equals("application/pdf") || lowerFileName.endsWith(".pdf")) {
//             try (PDDocument document = PDDocument.load(inputStream)) {
//                 PDFTextStripper stripper = new PDFTextStripper();
//                 return stripper.getText(document);
//             }
//         } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || lowerFileName.endsWith(".docx")) {
//             try (XWPFDocument doc = new XWPFDocument(inputStream)) {
//                 StringBuilder sb = new StringBuilder();
//                 doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
//                 return sb.toString();
//             }
//         } else if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || lowerFileName.endsWith(".pptx")) {
//             try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
//                 StringBuilder sb = new StringBuilder();
//                 for (XSLFSlide slide : ppt.getSlides()) {
//                     for (XSLFShape shape : slide.getShapes()) {
//                         if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
//                             sb.append(((org.apache.poi.xslf.usermodel.XSLFTextShape) shape).getText()).append("\n");
//                         }
//                     }
//                     sb.append("\n");
//                 }
//                 return sb.toString();
//             }
//         } else {
//             throw new UnsupportedFileTypeException("Unsupported file type: " + contentType);
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

//     /**
//      * Return distinct file names for the user
//      */
//     public List<String> getDistinctFileNamesByUser(String userId) {
//         return mongoTemplate.query(DocumentChunk.class)
//                 .distinct("fileName")
//                 .matching(Criteria.where("userId").is(userId))
//                 .as(String.class)
//                 .all();
//     }
    

//     /**
//      * Return list of chunks by user and fileName
//      */
//     public List<DocumentChunk> getChunksByUserAndFileName(String userId, String fileName) {
//         return documentChunkRepository.findByUserIdAndFileName(userId, fileName);
//     }

//     /**
//      * Returns a list of UserFileDTO containing fileName and documentId for all files uploaded by the user.
//      */
//     public List<UserFileDTO> getUserFilesWithDocumentIds(String userId) {
//         List<String> fileNames = getDistinctFileNamesByUser(userId);
//         List<UserFileDTO> userFiles = new ArrayList<>();
    
//         for (String fileName : fileNames) {
//             List<DocumentChunk> chunks = getChunksByUserAndFileName(userId, fileName);
//             Set<String> documentIds = chunks.stream()
//                     .map(DocumentChunk::getDocumentId)
//                     .collect(Collectors.toSet());
    
//             // If multiple documentIds for same fileName (rare), just take first one
//             String documentId = documentIds.isEmpty() ? null : documentIds.iterator().next();
    
//             // System.out.println("FileName: " + fileName + ", DocumentId: " + documentId);  // <-- Print here
    
//             UserFileDTO dto = new UserFileDTO();
//             dto.setFileName(fileName);
//             dto.setDocumentId(documentId);
//             userFiles.add(dto);
//         }
    
//         // System.out.println("Final list of UserFileDTO to return:");
//         // for (UserFileDTO f : userFiles) {
//         //     System.out.println("FileName: " + f.getFileName() + ", DocumentId: " + f.getDocumentId());
//         // }
    
//         return userFiles;
//     }
    

//     /**
//      * Delete all chunks for a given userId and fileName
//      */
//     @Transactional
//     public void deleteChunksByUserAndFileName(String userId, String fileName) {
//         Query query = new Query(Criteria.where("userId").is(userId).and("fileName").is(fileName));
//         List<DocumentChunk> matches = mongoTemplate.find(query, DocumentChunk.class, "document_chunks");

//         logger.info("Found {} chunks for deletion by userId: {} and fileName: {}", matches.size(), userId, fileName);

//         DeleteResult result = mongoTemplate.remove(query, "document_chunks");
//         logger.info("Deleted {} chunks", result.getDeletedCount());

//         if (!matches.isEmpty()) {
//             User user = userRepository.findById(userId).orElse(null);
//             if (user != null) {
//                 List<String> documentIds = user.getDocumentIds();
//                 if (documentIds != null) {
//                     boolean modified = false;
//                     for (DocumentChunk chunk : matches) {
//                         if (documentIds.remove(chunk.getDocumentId())) {
//                             modified = true;
//                         }
//                     }
//                     if (modified) {
//                         userRepository.save(user);
//                         logger.info("Updated user's documentIds after deletion");
//                     }
//                 }
//             }
//         }
//     }
// @Transactional
// public void deleteChunksByUserAndDocumentId(String username, String documentId) {
//     // Construct query with username (stored as userId in chunks)
//     Query query = new Query(Criteria.where("userId").is(username).and("documentId").is(documentId));
//     System.out.println(username);
//     logger.info("Using DB: {}", mongoTemplate.getDb().getName());
//     logger.info("Query to delete chunks: {}", query);

//     List<DocumentChunk> matches = mongoTemplate.find(query, DocumentChunk.class, "document_chunks");
//     logger.info("Found {} chunks to delete for user: {} and documentId: {}", matches.size(), username, documentId);

//     DeleteResult result = mongoTemplate.remove(query, "document_chunks");
//     logger.info("Deleted {} chunks", result.getDeletedCount());

//     // ✅ Correct lookup by username
//     Optional<User> optionalUser = userRepository.findByUsername(username);
//     if (optionalUser.isPresent()) {
//         User user = optionalUser.get();
//         List<String> documentIds = user.getDocumentIds();
//         if (documentIds != null && documentIds.remove(documentId)) {
//             userRepository.save(user);
//             logger.info("Removed documentId {} from user {}'s document list", documentId, username);
//         } else {
//             logger.warn("documentId {} was not in user's list", documentId);
//         }
//     } else {
//         logger.warn("User not found with username: {}", username);
//     }
// }

//     /**
//      * Return the full content of a document by userId and fileName
//      */
//     public String getFullDocumentContent(String userId, String fileName) {
//         List<DocumentChunk> chunks = getChunksByUserAndFileName(userId, fileName);
//         StringBuilder sb = new StringBuilder();
//         for (DocumentChunk chunk : chunks) {
//             sb.append(chunk.getContent()).append(" ");
//         }    
//         return sb.toString().trim();
//     }






//     public void saveChatHistory(String userId, String question, String response) {
//         logger.debug("Saving chat history with userId='{}', question='{}', response='{}'", userId, question, response);
    
//         ChatHistory history = new ChatHistory();
//         history.setUserId(userId);
//         history.setQuestion(question);
//         history.setResponse(response);
//         history.setTimestamp(LocalDateTime.now());
    
//         chatHistoryRepository.save(history);
    
//         logger.debug("Chat history saved: {}", history);
//     }
    

//     public List<ChatHistory> getChatHistoryForUser(String userId) {
//         return chatHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
//     }
    



//     // Custom exceptions for clarity
//     public static class DuplicateFileException extends RuntimeException {
//         public DuplicateFileException(String message) { super(message); }
//     }

//     public static class UnsupportedFileTypeException extends RuntimeException {
//         public UnsupportedFileTypeException(String message) { super(message); }
//     }

//     public static class InvalidFileException extends RuntimeException {
//         public InvalidFileException(String message) { super(message); }
//     }

//     public static class InvalidFileContentException extends RuntimeException {
//         public InvalidFileContentException(String message) { super(message); }
//     }
// }


package com.smartdocs.document_service.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.security.MessageDigest;

import com.smartdocs.document_service.dto.UserFileDTO;
import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.model.User;
import com.smartdocs.document_service.model.ChatHistory;
import com.smartdocs.document_service.repository.DocumentChunkRepository;
import com.smartdocs.document_service.repository.UserRepository;
import com.smartdocs.document_service.repository.ChatHistoryRepository;

import com.mongodb.client.result.DeleteResult;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private static final int DEFAULT_CHUNK_SIZE = 1000;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    // Create a unique compound index on userId, fileHash, and chunkIndex for fast lookup and duplication check
    @PostConstruct
    public void createIndexes() {
        try {
            var collection = mongoTemplate.getCollection("document_chunks");
            var options = new com.mongodb.client.model.IndexOptions().unique(true);
            collection.createIndex(
                com.mongodb.client.model.Indexes.compoundIndex(
                    com.mongodb.client.model.Indexes.ascending("userId"),
                    com.mongodb.client.model.Indexes.ascending("fileHash"),
                    com.mongodb.client.model.Indexes.ascending("chunkIndex")
                ),
                options
            );
            logger.info("Created unique index on userId, fileHash, and chunkIndex");
        } catch (Exception e) {
            logger.warn("Index creation failed or index already exists: {}", e.getMessage());
        }
    }

    /**
     * Compute SHA-256 hash of file bytes to identify duplicates.
     */
    public String computeFileHash(byte[] fileBytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Check if file is already uploaded by user using file hash.
     */
    public boolean isDuplicateFile(String userId, String fileHash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("fileHash").is(fileHash));
        return mongoTemplate.exists(query, DocumentChunk.class);
    }

    /**
     * Process the uploaded file: extract content, split into chunks, save chunks, and update user document list.
     */
    public void processAndStoreFile(MultipartFile file, String userId) throws Exception {
        byte[] fileBytes = file.getBytes();
        String fileHash = computeFileHash(fileBytes);

        if (isDuplicateFile(userId, fileHash)) {
            throw new DuplicateFileException("Duplicate file upload detected.");
        }

        String documentId = UUID.randomUUID().toString();
        String content = extractContent(file);

        if (content == null || content.isBlank()) {
            throw new InvalidFileContentException("Failed to extract content or file is empty.");
        }

        List<String> chunks = splitIntoChunks(content, DEFAULT_CHUNK_SIZE);

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
        logger.info("Saved {} chunks for documentId {} userId {}", documentChunks.size(), documentId, userId);

        // Update user's document list with new documentId
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<String> documentIds = user.getDocumentIds();
            if (documentIds == null) {
                documentIds = new ArrayList<>();
            }
            documentIds.add(documentId);
            user.setDocumentIds(documentIds);
            userRepository.save(user);
            logger.info("Added documentId {} to userId {}", documentId, userId);
        }
    }

    /**
     * Extract text content from uploaded file according to type.
     */
    private String extractContent(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        if (contentType == null || fileName == null) {
            throw new InvalidFileException("Missing file metadata.");
        }

        String lowerFileName = fileName.toLowerCase();

        if (contentType.equals("text/plain") || lowerFileName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else if (contentType.equals("application/pdf") || lowerFileName.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(inputStream)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || lowerFileName.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(inputStream)) {
                StringBuilder sb = new StringBuilder();
                doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
                return sb.toString();
            }
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || lowerFileName.endsWith(".pptx")) {
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
            throw new UnsupportedFileTypeException("Unsupported file type: " + contentType);
        }
    }

    /**
     * Split the content string into chunks of specified size.
     */
    private List<String> splitIntoChunks(String content, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(content.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }

    /**
     * Return distinct file names uploaded by the user.
     */
    public List<String> getDistinctFileNamesByUser(String userId) {
        return mongoTemplate.query(DocumentChunk.class)
                .distinct("fileName")
                .matching(Criteria.where("userId").is(userId))
                .as(String.class)
                .all();
    }


       public String getFullDocumentContent(String userId, String fileName) {
                List<DocumentChunk> chunks = getChunksByUserAndFileName(userId, fileName);
                StringBuilder sb = new StringBuilder();
                for (DocumentChunk chunk : chunks) {
                    sb.append(chunk.getContent()).append(" ");
                }    
                return sb.toString().trim();
            }
        

    /**
     * Return all chunks for a user and fileName.
     */
    public List<DocumentChunk> getChunksByUserAndFileName(String userId, String fileName) {
        return documentChunkRepository.findByUserIdAndFileName(userId, fileName);
    }

    /**
     * Return list of UserFileDTO with fileName and documentId for files uploaded by the user.
     */
    public List<UserFileDTO> getUserFilesWithDocumentIds(String userId) {
        List<String> fileNames = getDistinctFileNamesByUser(userId);
        List<UserFileDTO> userFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            List<DocumentChunk> chunks = getChunksByUserAndFileName(userId, fileName);
            Set<String> documentIds = chunks.stream()
                    .map(DocumentChunk::getDocumentId)
                    .collect(Collectors.toSet());

            // If multiple documentIds exist for a fileName, take first (rare case)
            String documentId = documentIds.isEmpty() ? null : documentIds.iterator().next();

            UserFileDTO dto = new UserFileDTO();
            dto.setFileName(fileName);
            dto.setDocumentId(documentId);
            userFiles.add(dto);
        }

        return userFiles;
    }

    /**
     * Delete all chunks for a user and a given fileName, and update user's document list.
     */
    @Transactional
    public void deleteChunksByUserAndFileName(String userId, String fileName) {
        Query query = new Query(Criteria.where("userId").is(userId).and("fileName").is(fileName));
        List<DocumentChunk> matches = mongoTemplate.find(query, DocumentChunk.class, "document_chunks");

        logger.info("Found {} chunks for deletion by userId: {} and fileName: {}", matches.size(), userId, fileName);

        DeleteResult result = mongoTemplate.remove(query, "document_chunks");
        logger.info("Deleted {} chunks", result.getDeletedCount());

        if (!matches.isEmpty()) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                List<String> documentIds = user.getDocumentIds();
                if (documentIds != null) {
                    boolean modified = false;
                    for (DocumentChunk chunk : matches) {
                        if (documentIds.remove(chunk.getDocumentId())) {
                            modified = true;
                        }
                    }
                    if (modified) {
                        userRepository.save(user);
                        logger.info("Updated user's documentIds after deletion");
                    }
                }
            }
        }
    }

    /**
     * Delete all chunks for a user by username and documentId, update user's document list.
     */
    @Transactional
    public void deleteChunksByUserAndDocumentId(String username, String documentId) {
        Query query = new Query(Criteria.where("userId").is(username).and("documentId").is(documentId));
        logger.info("Using DB: {}", mongoTemplate.getDb().getName());
        logger.info("Query to delete chunks: {}", query);

        List<DocumentChunk> matches = mongoTemplate.find(query, DocumentChunk.class, "document_chunks");
        logger.info("Found {} chunks to delete for user: {} and documentId: {}", matches.size(), username, documentId);

        DeleteResult result = mongoTemplate.remove(query, "document_chunks");
        logger.info("Deleted {} chunks", result.getDeletedCount());

        if (!matches.isEmpty()) {
            User user = userRepository.findById(username).orElse(null);
            if (user != null) {
                List<String> documentIds = user.getDocumentIds();
                if (documentIds != null && documentIds.remove(documentId)) {
                    userRepository.save(user);
                    logger.info("Removed documentId {} from user's documentIds", documentId);
                }
            }
        }
    }

    /**
     * Find chat history by userId.
     */
    public List<ChatHistory> getChatHistoryByUserId(String userId) {
        return chatHistoryRepository.findByUserId(userId);
    }
        public DocumentService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    /**
     * Save chat history entry.
     */
    public ChatHistory saveChatHistory(ChatHistory chatHistory) {
        chatHistory.setTimestamp(LocalDateTime.now());
        return chatHistoryRepository.save(chatHistory);
    }



    public void saveChatHistory(String userId, String question, String response) {
                logger.debug("Saving chat history with userId='{}', question='{}', response='{}'", userId, question, response);
            
                ChatHistory history = new ChatHistory();
                history.setUserId(userId);
                history.setQuestion(question);
                history.setResponse(response);
                history.setTimestamp(LocalDateTime.now());
            
                chatHistoryRepository.save(history);
            
                logger.debug("Chat history saved: {}", history);
            }
    /**
     * Save or update user.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // --- Custom exception classes for clarity ---

    public static class DuplicateFileException extends RuntimeException {
        public DuplicateFileException(String message) { super(message); }
    }

    public static class InvalidFileException extends RuntimeException {
        public InvalidFileException(String message) { super(message); }
    }

    public static class UnsupportedFileTypeException extends RuntimeException {
        public UnsupportedFileTypeException(String message) { super(message); }
    }

    public static class InvalidFileContentException extends RuntimeException {
        public InvalidFileContentException(String message) { super(message); }
    }
}

