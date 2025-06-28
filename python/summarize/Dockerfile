FROM python:3.10-slim

WORKDIR /app

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of your code
COPY . .

# Expose the port your Python app runs on
EXPOSE 8084

# Run the Python app (adjust your command accordingly)
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8084"] 
