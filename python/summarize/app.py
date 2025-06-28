from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
from pymongo import MongoClient
from langchain.schema import Document
import os
import requests
from dotenv import load_dotenv
from fastapi.middleware.cors import CORSMiddleware

# Load environment variables from .env
load_dotenv()

# Environment configs
MONGODB_URI = os.getenv("MONGODB_URI")
DATABASE_NAME = os.getenv("MONGODB_DB")
COLLECTION_NAME = os.getenv("MONGODB_COLLECTION")
HUGGINGFACE_API_URL = os.getenv("HUGGINGFACE_API_URL")
HUGGINGFACE_API_TOKEN = os.getenv("HUGGINGFACE_API_KEY")

# Validate configs
if not all([MONGODB_URI, DATABASE_NAME, COLLECTION_NAME, HUGGINGFACE_API_URL, HUGGINGFACE_API_TOKEN]):
    raise ValueError("Missing environment variables. Check your .env file.")

# MongoDB setup
client = MongoClient(MONGODB_URI)
db = client[DATABASE_NAME]
collection = db[COLLECTION_NAME]

# FastAPI app setup
app = FastAPI(
    title="Document Summarization API",
    description="Summarizes documents or MongoDB document chunks",
    version="1.1"
)

# CORS Middleware (allow your frontend origin here, * for testing)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Change in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Schemas
class SummarizeByIdRequest(BaseModel):
    document_id: str

class SummarizeByTextRequest(BaseModel):
    content: str

# GET documents uploaded by a user
@app.get("/documents/user/{user_id}")
def get_documents_by_user(user_id: str):
    docs_cursor = collection.find({"userId": user_id})
    documents_map = {}
    for doc in docs_cursor:
        doc_id = doc.get("documentId")
        if doc_id and doc_id not in documents_map:
            documents_map[doc_id] = {
                "documentId": doc_id,
                "fileName": doc.get("fileName", "unknown"),
            }
    return list(documents_map.values())

# Get chunks for a specific document ID
def get_chunks(document_id: str) -> List[Document]:
    query = {"documentId": document_id}
    records = collection.find(query)
    documents = []
    for record in records:
        content = record.get("content", "")
        metadata = {
            "source": record.get("fileName", "unknown"),
            "chunkIndex": record.get("chunkIndex"),
        }
        documents.append(Document(page_content=content, metadata=metadata))
    return documents

# HuggingFace summarization
def summarize_text(text: str) -> str:
    headers = {"Authorization": f"Bearer {HUGGINGFACE_API_TOKEN}"}
    payload = {"inputs": text}

    response = requests.post(HUGGINGFACE_API_URL, headers=headers, json=payload)
    if response.status_code == 200:
        try:
            return response.json()[0]["summary_text"]
        except Exception:
            return "Summarization format error."
    else:
        print(f"HuggingFace Error: {response.status_code} - {response.text}")
        return "Summarization failed."

# 🔹 Summarize from MongoDB chunks
@app.post("/summarize")
def summarize_document_by_id(request: SummarizeByIdRequest):
    chunks = get_chunks(request.document_id)
    if not chunks:
        raise HTTPException(status_code=404, detail="No chunks found for this document ID.")
    
    summaries = [summarize_text(chunk.page_content) for chunk in chunks]
    final_summary = "\n".join(summaries)
    return {"summary": final_summary}

# 🔹 Summarize from raw text
@app.post("/summarize/raw")
def summarize_raw_text(request: SummarizeByTextRequest):
    if not request.content.strip():
        raise HTTPException(status_code=400, detail="No content provided for summarization.")

    summary = summarize_text(request.content)
    return {"summary": summary}
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8084)

# Create a logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# Create a file handler and a stream handler
file_handler = logging.FileHandler('app.log')
stream_handler = logging.StreamHandler()

# Create a formatter and set it for the handlers
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)
stream_handler.setFormatter(formatter)

# Add the handlers to the logger
logger.addHandler(file_handler)
logger.addHandler(stream_handler)

# ...

# GET documents uploaded by a user
@app.get("/documents/user/{user_id}")
def get_documents_by_user(user_id: str):
    try:
        docs_cursor = collection.find({"userId": user_id})
        documents_map = {}
        for doc in docs_cursor:
            doc_id = doc.get("documentId")
            if doc_id and doc_id not in documents_map:
                documents_map[doc_id] = {
                    "documentId": doc_id,
                    "fileName": doc.get("fileName", "unknown"),
                }
        logger.info(f"Successfully retrieved documents for user {user_id}")
        return list(documents_map.values())
    except Exception as e:
        logger.error(f"Error retrieving documents for user {user_id}: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal Server Error")

# Get chunks for a specific document ID
def get_chunks(document_id: str) -> List[Document]:
    try:
        query = {"documentId": document_id}
        records = collection.find(query)
        documents = []
        for record in records:
            content = record.get("content", "")
            metadata = {
                "source": record.get("fileName", "unknown"),
                "chunkIndex": record.get("chunkIndex"),
            }
            documents.append(Document(page_content=content, metadata=metadata))
        logger.info(f"Successfully retrieved chunks for document {document_id}")
        return documents
    except Exception as e:
        logger.error(f"Error retrieving chunks for document {document_id}: {str(e)}")
        return []

# HuggingFace summarization
def summarize_text(text: str) -> str:
    try:
        headers = {"Authorization": f"Bearer {HUGGINGFACE_API_TOKEN}"}
        payload = {"inputs": text}

        response = requests.post(HUGGINGFACE_API_URL, headers=headers, json=payload)
        if response.status_code == 200:
            try:
                return response.json()[0]["summary_text"]
            except Exception:
                logger.error("Summarization format error.")
                return "Summarization format error."
        else:
            logger.error(f"HuggingFace Error: {response.status_code} - {response.text}")
            return "Summarization failed."
    except Exception as e:
        logger.error(f"Error summarizing text: {str(e)}")
        return "Summarization failed."

# Summarize from MongoDB chunks
@app.post("/summarize")
def summarize_document_by_id(request: SummarizeByIdRequest):
    try:
        chunks = get_chunks(request.document_id)
        if not chunks:
            logger.error(f"No chunks found for document {request.document_id}")
            raise HTTPException(status_code=404, detail="No chunks found for this document ID.")
        
        summaries = [summarize_text(chunk.page_content) for chunk in chunks]
        final_summary = "\n".join(summaries)
        logger.info(f"Successfully summarized document {request.document_id}")
        return {"summary": final_summary}
    except Exception as e:
        logger.error(f"Error summarizing document {request.document_id}: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal Server Error")

# Summarize from raw text
@app.post("/summarize/raw")
def summarize_raw_text(request: SummarizeByTextRequest):
    try:
        if not request.content.strip():
            logger.error("No content provided for summarization.")
            raise HTTPException(status_code=400, detail="No content provided for summarization.")

        summary = summarize_text(request.content)
        logger.info("Successfully summarized raw text")
        return {"summary": summary}
    except Exception as e:
        logger.error(f"Error summarizing raw text: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal Server Error")