# RAG pipeline using LangChain, LLM, and MongoDB Atlas (Vector Search)

# Install required libraries:
# pip install langchain langchain-community openai pymongo[tls] pymupdf tiktoken sentence-transformers

from transformers import AutoTokenizer, AutoModel
import torch
import os
import fitz  # PyMuPDF
from langchain.document_loaders import PyMuPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from pymongo import MongoClient
from dotenv import load_dotenv
import requests  # For making API calls

# Load environment variables from .env
load_dotenv()

# MongoDB Atlas Config
mongo_uri = os.getenv("MONGODB_URI")  # MongoDB connection string
mongo_db_name = os.getenv("MONGODB_DB")  # Database name
mongo_collection_name = os.getenv("MONGODB_COLLECTION")  # Collection name

# Hugging Face API Config
huggingface_api_key = os.getenv("HUGGINGFACE_API_TOKEN")  # Your Hugging Face API key
huggingface_model = "sentence-transformers/multi-qa-mpnet-base-dot-v1"  # Model to use for embeddings
huggingface_api_endpoint = f"https://api-inference.huggingface.co/models/{huggingface_model}"

# Gemini API Config
gemini_api_endpoint = os.getenv("GEMINI_API_ENDPOINT")  # API for question-answering
gemini_api_key = os.getenv("GEMINI_API_KEY")  # API key for Gemini API

# Load PDFs from a folder using LangChain's PyMuPDFLoader
def load_pdfs_from_folder(folder_path):
    all_docs = []
    for filename in os.listdir(folder_path):
        if filename.endswith(".pdf"):
            loader = PyMuPDFLoader(os.path.join(folder_path, filename))
            documents = loader.load()
            all_docs.extend(documents)
    return all_docs

# Split documents into chunks using LangChain's RecursiveCharacterTextSplitter
def process_documents(docs, chunk_size=1000, chunk_overlap=200):
    splitter = RecursiveCharacterTextSplitter(chunk_size=chunk_size, chunk_overlap=chunk_overlap)
    chunks = splitter.split_documents(docs)
    return chunks

# Generate embeddings using Hugging Face Inference API
def generate_embedding(text):
    headers = {
        "Authorization": f"Bearer {huggingface_api_key}",
        "Content-Type": "application/json"
    }
    payload = {"inputs": text}  # Use "inputs" directly as a string
    response = requests.post(huggingface_api_endpoint, headers=headers, json=payload)

    if response.status_code == 200:
        # The API returns a dictionary with the embedding
        return response.json()["embedding"]
    else:
        print(f"Error generating embedding: {response.status_code}, {response.text}")
        return None

# Store document chunks and embeddings in MongoDB Atlas
def store_chunks_in_mongodb(chunks):
    client = MongoClient(mongo_uri)
    db = client[mongo_db_name]
    collection = db[mongo_collection_name]

    # Clear existing collection
    collection.delete_many({})

    # Insert chunks with embeddings into MongoDB
    for chunk in chunks:
        embedding = generate_embedding(chunk.page_content)
        if embedding:
            collection.insert_one({
                "content": chunk.page_content,
                "metadata": chunk.metadata,
                "embedding": embedding
            })
    print(f"Stored {len(chunks)} chunks in MongoDB Atlas.")

# Retrieve relevant documents from MongoDB Atlas using Vector Search
def retrieve_documents_from_mongodb(query):
    client = MongoClient(mongo_uri)
    db = client[mongo_db_name]
    collection = db[mongo_collection_name]

    # Generate embedding for the query
    query_embedding = generate_embedding(query)
    if not query_embedding:
        print("Failed to generate query embedding.")
        return []

    # Perform vector search in MongoDB
    results = collection.aggregate([
        {
            "$search": {
                "knnBeta": {
                    "vector": query_embedding,
                    "path": "embedding",
                    "k": 5  # Retrieve top 5 relevant documents
                }
            }
        }
    ])

    return [{"content": result["content"], "metadata": result["metadata"]} for result in results]

# Call Gemini API for question-answering
def call_gemini_api(question, context):
    headers = {
        "Authorization": f"Bearer {gemini_api_key}",
        "Content-Type": "application/json"
    }
    payload = {
        "question": question,
        "context": context
    }
    response = requests.post(gemini_api_endpoint, headers=headers, json=payload)

    if response.status_code == 200:
        return response.json().get("answer", "No answer provided by the API.")
    else:
        print(f"Error: {response.status_code}, {response.text}")
        return "Error in processing the request."

# Main execution
def main():
    folder_path = "."  # Your folder with multiple PDFs
    docs = load_pdfs_from_folder(folder_path)
    print(f"Loaded {len(docs)} documents from folder: {folder_path}")

    chunks = process_documents(docs)
    print(f"Created {len(chunks)} chunks.")
    print(f"First chunk: {chunks[0].page_content if chunks else 'No chunks created'}")

    # Store chunks and embeddings in MongoDB Atlas
    store_chunks_in_mongodb(chunks)

    while True:
        query = input("Ask a question (or type 'exit'): ")
        if query.lower() == 'exit':
            break

        # Retrieve relevant documents from MongoDB Atlas
        retrieved_docs = retrieve_documents_from_mongodb(query)

        if not retrieved_docs:
            print("No relevant documents found.")
            print("\nAnswer: No relevant documents found.")
            print("\nSources: []")
            continue

        # Construct context from retrieved documents
        context = " ".join([doc["content"] for doc in retrieved_docs])
        print(f"Constructed context: {context[:200]}...")

        # Call Gemini API for question-answering
        answer = call_gemini_api(question=query, context=context)
        print("\nAnswer:", answer)
        print("\nSources:", [doc["metadata"] for doc in retrieved_docs])

if __name__ == "__main__":
    main()
