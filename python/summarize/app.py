import os
import requests
from langchain_community.document_loaders import PyMuPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from dotenv import load_dotenv

# Load environment variables from .env
load_dotenv()

# Hugging Face API URL and Token
HUGGINGFACE_API_URL = os.getenv("HUGGINGFACE_API_URL")  
HUGGINGFACE_API_TOKEN = os.getenv("HUGGINGFACE_API_TOKEN")  # Load token from .env

# Validate the Hugging Face API token
if not HUGGINGFACE_API_TOKEN:
    raise ValueError("HUGGINGFACE_API_TOKEN is not set. Please check your .env file.")

# Function to load and extract text from a PDF
def load_pdf(file_path):
    loader = PyMuPDFLoader(file_path)
    documents = loader.load()
    return documents

# Function to split text into manageable chunks
def split_text(documents):
    splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
    chunks = splitter.split_documents(documents)
    return chunks

# Function to summarize text chunks using Hugging Face API
def summarize_chunks(chunks):
    headers = {"Authorization": f"Bearer {HUGGINGFACE_API_TOKEN}"}
    summaries = []
    for chunk in chunks:
        payload = {"inputs": chunk.page_content}
        response = requests.post(HUGGINGFACE_API_URL, headers=headers, json=payload)
        if response.status_code == 200:
            summary = response.json()[0]['summary_text']
            summaries.append(summary)
        else:
            print(f"Error: {response.status_code}, {response.text}")
            summaries.append("Error in summarization.")
    return summaries

# Main function to summarize a PDF
def summarize_pdf(file_path):
    print(f"Loading PDF: {file_path}")
    documents = load_pdf(file_path)
    print("Splitting text into chunks...")
    chunks = split_text(documents)
    print("Generating summaries...")
    summaries = summarize_chunks(chunks)
    print("\nFinal Summary:")
    print("\n".join(summaries))

if __name__ == "__main__":
    # Replace 'example.pdf' with the path to your PDF file
    pdf_path = "GovardhanaSubhash_Meda.pdf"
    summarize_pdf(pdf_path)