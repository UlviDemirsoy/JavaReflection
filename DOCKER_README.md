# Docker Compose Setup

This project uses Docker Compose to orchestrate three services: MongoDB, Backend, and Frontend with proper dependency management.

## 🏗️ Architecture

```
Frontend (5173) → Backend (8080) → MongoDB (27017)
```

## 🚀 Quick Start

### Prerequisites
- Docker
- Docker Compose

### Start All Services
```bash
docker-compose up -d
```

### Start with Build
```bash
docker-compose up -d --build
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mongodb
```

## 📋 Services

### 1. MongoDB
- **Port**: 27017
- **Container**: acegames-mongodb
- **Health Check**: MongoDB ping command
- **Dependencies**: None (starts first)

### 2. Backend (Spring Boot)
- **Port**: 8080
- **Container**: acegames-backend
- **Health Check**: Spring Boot Actuator health endpoint
- **Dependencies**: MongoDB (waits for MongoDB to be healthy)
- **Features**:
  - Automatic schema registration on startup
  - REST API endpoints
  - MongoDB integration

### 3. Frontend (Vue.js)
- **Port**: 5173
- **Container**: acegames-frontend
- **Health Check**: None (depends on backend)
- **Dependencies**: Backend (waits for backend to be healthy)
- **Features**:
  - Vue.js application
  - API integration with backend

## 🔧 Configuration

### Environment Variables

#### MongoDB
- `MONGO_INITDB_ROOT_USERNAME`: admin
- `MONGO_INITDB_ROOT_PASSWORD`: password
- `MONGO_INITDB_DATABASE`: acegames

#### Backend
- `SPRING_DATA_MONGODB_URI`: mongodb://admin:password@mongodb:27017/acegames?authSource=admin
- `APP_SCHEMA_REGISTRATION_ENABLED`: true
- `APP_SCHEMA_REGISTRATION_CLASSES`: Skin,PurchaseProduct,Offer,Cascade

#### Frontend
- `VITE_API_BASE_URL`: http://localhost:8080/api

## 📊 Health Checks

### MongoDB Health Check
```bash
docker-compose exec mongodb mongosh --eval "db.adminCommand('ping')"
```

### Backend Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Frontend Health Check
```bash
curl http://localhost:5173
```

## 🗄️ Database

### MongoDB Collections
- `modelSchemas` - Schema definitions
- `cascade` - Cascade data
- `offer` - Offer data
- `purchaseproduct` - Purchase product data
- `skin` - Skin data

### Access MongoDB
```bash
# Connect to MongoDB container
docker-compose exec mongodb mongosh -u admin -p password

# Use acegames database
use acegames

# List collections
show collections

# View schemas
db.modelSchemas.find()
```

## 🔍 Troubleshooting

### Check Service Status
```bash
docker-compose ps
```

### Check Service Dependencies
```bash
docker-compose config
```

### Restart Specific Service
```bash
docker-compose restart backend
```

### Rebuild and Restart
```bash
docker-compose down
docker-compose up -d --build
```

### View Service Logs
```bash
# Backend logs
docker-compose logs backend

# Frontend logs
docker-compose logs frontend

# MongoDB logs
docker-compose logs mongodb
```

### Common Issues

#### Backend Won't Start
- Check if MongoDB is running: `docker-compose ps`
- Check MongoDB logs: `docker-compose logs mongodb`
- Verify MongoDB connection in backend logs

#### Frontend Won't Start
- Check if backend is healthy: `curl http://localhost:8080/actuator/health`
- Check backend logs: `docker-compose logs backend`

#### Schema Registration Issues
- Check backend logs for reflection errors
- Verify class names in configuration
- Test schema generation manually: `curl -X POST "http://localhost:8080/api/schema/generate/default"`

## 🧹 Cleanup

### Stop All Services
```bash
docker-compose down
```

### Stop and Remove Volumes
```bash
docker-compose down -v
```

### Remove All Images
```bash
docker-compose down --rmi all
```

## 📝 Development

### Development Mode
For development, you can run services individually:

```bash
# Start only MongoDB
docker-compose up -d mongodb

# Run backend locally (requires MongoDB)
./acegames-backend/gradlew bootRun

# Run frontend locally
cd acegamesfrontend && npm run dev
```

### Hot Reload
The current setup doesn't include hot reload. For development with hot reload:

1. Run MongoDB with Docker Compose
2. Run backend and frontend locally with their respective dev commands
3. Use volume mounts for hot reload (requires additional Docker Compose configuration)

## 🔐 Security Notes

- MongoDB credentials are hardcoded for development
- In production, use environment variables or Docker secrets
- Consider using MongoDB authentication and network security
- Frontend API calls use localhost - adjust for production deployment 