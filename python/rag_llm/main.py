# RAG pipeline using LangChain, LLM, and MongoDB Atlas (Vector Search)

# Install required libraries:
# pip install langchain langchain-community openai pymongo[tls] pymupdf tiktoken sentence-transformers

import os
import fitz  # PyMuPDF
from pymongo import MongoClient
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.vectorstores import MongoDBAtlasVectorSearch
from langchain_community.llms import OpenAI
from langchain_community.document_loaders import PyMuPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.chains import RetrievalQA
from dotenv import load_dotenv

# Load env variables from .env
load_dotenv()

# Load OpenAI key
openai_api_key = os.getenv("OPENAI_API_KEY")

# MongoDB Atlas Vector Search config
mongo_uri = os.getenv("MONGODB_ATLAS_URI")  # Format: mongodb+srv://username:password@cluster.mongodb.net
mongo_db_name = "#"
mongo_collection_name = "#"

# Load PDFs from a folder
def load_pdfs_from_folder(folder_path):
    all_docs = []
    for filename in os.listdir(folder_path):
        if filename.endswith(".pdf"):
            loader = PyMuPDFLoader(os.path.join(folder_path, filename))
            documents = loader.load()
            all_docs.extend(documents)
    return all_docs

# Split and embed documents
def process_documents(docs):
    splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=100)
    chunks = splitter.split_documents(docs)
    return chunks

# Create or connect to MongoDB Atlas Vector Search
def setup_vector_db(chunks):
    # Initialize embeddings
    embeddings = HuggingFaceEmbeddings(model_name="model_name_here")  # Replace with your model name

    # Connect to MongoDB Atlas
    client = MongoClient(mongo_uri)
    db = client[mongo_db_name]
    collection = db[mongo_collection_name]

    # Ensure the collection exists
    if mongo_collection_name not in db.list_collection_names():
        db.create_collection(mongo_collection_name)

    # Create or connect to the vector store
    vector_dbs = MongoDBAtlasVectorSearch.from_documents(
        documents=chunks,
        embedding=embeddings,
        collection=collection  # Pass the MongoDB collection object directly
    )
    return vector_dbs

# Set up LLM + QA chain
def create_qa_chain(vector_dbs):
    llm = OpenAI(temperature=0, openai_api_key=openai_api_key)
    qa = RetrievalQA.from_chain_type(
        llm=llm,
        retriever=vector_dbs.as_retriever(search_type="similarity", search_kwargs={"k": 3}),
        return_source_documents=True
    )
    return qa

# Main execution
def main():
    folder_path = "."  # Your folder with multiple PDFs
    docs = load_pdfs_from_folder(folder_path)
    chunks = process_documents(docs)
    vector_dbs = setup_vector_db(chunks)
    qa_chain = create_qa_chain(vector_dbs)

    while True:
        query = input("Ask a question (or type 'exit'): ")
        if query.lower() == 'exit':
            break
        result = qa_chain({"query": query})
        print("\nAnswer:", result['result'])
        print("\nSources:", [doc.metadata for doc in result['source_documents']])

if __name__ == "__main__":
    main()
