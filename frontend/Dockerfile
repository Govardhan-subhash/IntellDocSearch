# Build React app
FROM node:18-alpine as builder
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Serve React app using nginx
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html

# Expose frontend port
EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
