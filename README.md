# Backend & Frontend

A Java Spring Boot backend with Vue.js frontend using MongoDB, Docker, and Docker Compose.

## Quick Start

1. Start all services:
   ```
   docker-compose up -d
   ```

2. Access the applications:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger Documentation: http://localhost:8080/swagger-ui/index.html
   - MongoDB: localhost:27017

## Services

### Backend (Port 8080)
- Spring Boot application with MongoDB
- Schema registration and reflection utilities
- REST API endpoints for data management

### Frontend (Port 3000)
- Vue.js 3 application with TypeScript
- Dynamic form generation with recursion
- Collection management interface

### MongoDB (Port 27017)
- Database for storing schemas and data

## Main API Endpoints

### Schema Management
- GET /api/schema - List all registered schemas
- GET /api/schema/{collection} - Get schema by collection name
- DELETE /api/schema/{collection} - Delete schema

### Schema Generation
- POST /api/schema/register?className={className} - Register schema for a specific class
- POST /api/schema/register/default - Register schemas for default classes (Skin, PurchaseProduct, Offer, Cascade)
- POST /api/schema/register/bulk - Register schemas for multiple classes

### Additional Endpoints
- GET /api/schema/enums - List all available enums and their values
- POST /api/schema/test-reflection?className={className} - Test reflection parser for a specific class

### Generic CRUD Operations
- GET /api/{className} - Get all records of a class
- GET /api/{className}/{id} - Get record by ID
- POST /api/{className} - Create new record
- PUT /api/{className}/{id} - Update record
- DELETE /api/{className}/{id} - Delete record

## Development

### Running Tests
```
# Backend tests
cd acegames-backend
./gradlew test

# Frontend tests
cd acegamesfrontend
npm test
```

### Building Locally
```
# Backend
cd acegames-backend
./gradlew build

# Frontend
cd acegamesfrontend
npm install
npm run build
```

## Docker Commands

```
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build -d

# Check container status
docker-compose ps
```

## Troubleshooting

### Container Issues
- Check container status: `docker-compose ps`
- View logs: `docker-compose logs [service-name]`
- Restart service: `docker-compose restart [service-name]`

### Port Conflicts
- Ensure ports 3000, 8080, and 27017 are available
- Stop conflicting services or change ports in docker-compose.yml

### Health Checks
- Backend health: http://localhost:8080/actuator/health
- Frontend: Check browser console for errors
- MongoDB: Check connection in backend logs

## Project Structure

```
JavaReflection/
├── acegames-backend/          # Spring Boot backend
├── acegamesfrontend/          # Vue.js frontend
├── docker-compose.yml         # Docker orchestration
└── README.md                 # This file
```

## Testing

### Unit Tests
- Backend services tested with Mockito and JUnit 5
- Frontend components tested with Vue Test Utils
- Follow AAA pattern (Arrange-Act-Assert)

### Integration Tests
- End-to-end API testing with @SpringBootTest
- Database integration testing
- MockMvc for HTTP request testing

## Default Schema Classes

The application automatically registers schemas for these classes:
- Skin
- PurchaseProduct  
- Offer
- Cascade
- Tile

These schemas enable dynamic form generation and data management in the frontend.
