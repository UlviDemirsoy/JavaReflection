FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

COPY --from=build /app/dist ./dist
RUN npm install -g serve

EXPOSE 5173

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:5173 || exit 1

CMD ["serve", "-s", "dist", "-l", "5173"] 