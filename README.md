# SA-Deliver E-commerce Application

A full-stack e-commerce application built with Java backend and vanilla JavaScript frontend.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Git

### Running the Application

#### Option 1: Using Startup Scripts (Recommended)
```bash
# For Linux/macOS/WSL
bash start-app.sh

# For Windows PowerShell
.\start-app.ps1

# For Windows Command Prompt
start-app.bat
```

#### Option 2: Manual Setup
```bash
# Start backend server
cd backend
mvn clean compile exec:java

# In another terminal, start frontend server
cd frontend
python -m http.server 8000
```

## 🏗️ CI/CD Pipeline

This project includes GitHub Actions for automated testing and deployment:

### Workflow Features
- **Backend Testing**: Runs Maven tests on Java 21
- **Frontend Testing**: Validates HTML/CSS/JS
- **Integration Testing**: Tests API endpoints
- **Staging Deployment**: Auto-deploys from `develop` branch
- **Production Deployment**: Auto-deploys from `main` branch

### Pipeline Stages
1. **Test Backend** - Compiles and tests Java code
2. **Test Frontend** - Validates frontend code
3. **Integration Test** - Tests full application stack
4. **Deploy Staging** - Deploys to staging environment
5. **Deploy Production** - Deploys to production environment

## 🌐 Netlify Deployment

The project is configured for Netlify deployment with:

### Features
- **Automatic Builds**: Triggers on Git pushes
- **Branch Deploys**: Preview deployments for PRs
- **Environment Variables**: Different configs for staging/production
- **Security Headers**: CSP, XSS protection, etc.
- **Performance Optimization**: Asset caching and minification

### Netlify Configuration
- **Build Command**: Custom build process
- **Publish Directory**: `frontend/`
- **Redirects**: SPA routing support
- **Headers**: Security and performance headers

## 📁 Project Structure

```
SA-deliver/
├── .github/
│   └── workflows/
│       └── ci-cd.yml          # GitHub Actions pipeline
├── backend/
│   ├── src/
│   │   ├── main/java/
│   │   └── test/java/
│   ├── pom.xml                # Maven configuration
│   └── target/                # Compiled classes
├── frontend/
│   ├── index.html             # Main application
│   ├── styles.css             # Styling
│   └── app.js                 # JavaScript logic
├── netlify.toml               # Netlify configuration
├── start-app.sh               # Linux/macOS startup script
├── start-app.ps1              # PowerShell startup script
├── start-app.bat              # Windows startup script
└── README.md                  # This file
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
cd backend && mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **UserServiceTest**: 8 tests - User registration, login, profile management
- **ProductServiceTest**: 5 tests - Product CRUD operations
- **ServerTest**: 11 tests - API endpoint testing

## 🔧 Development

### Backend API Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/products` - Get all products
- `POST /api/products` - Create product
- `PUT /api/products/:id` - Update product
- `DELETE /api/products/:id` - Delete product

### Environment Variables
```bash
# Backend
PORT=8080
DB_URL=jdbc:h2:mem:testdb

# Frontend
API_URL=http://localhost:8080
```

## 🚀 Deployment

### GitHub Actions
The CI/CD pipeline automatically:
1. Tests code on every push/PR
2. Builds artifacts
3. Deploys to staging (develop branch)
4. Deploys to production (main branch)

### Netlify
1. Connect your GitHub repository to Netlify
2. Set build command: `echo 'Frontend build completed'`
3. Set publish directory: `frontend`
4. Configure environment variables in Netlify dashboard

### Manual Deployment
```bash
# Build backend
cd backend && mvn clean package

# Deploy frontend to any static hosting
# Copy frontend/ directory to your hosting provider
```

## 🔒 Security

### Implemented Security Features
- Password hashing with bcrypt
- JWT token authentication
- CORS configuration
- Input validation
- SQL injection prevention

### Security Headers (Netlify)
- Content Security Policy
- X-Frame-Options
- X-XSS-Protection
- X-Content-Type-Options

## 📊 Monitoring

### Health Checks
- Backend: `GET /health` - Server status
- Frontend: Built-in error handling

### Logging
- Backend: SLF4J with Logback
- Frontend: Console logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Workflow
1. Make changes on `develop` branch
2. Create PR to `main` branch
3. CI/CD pipeline runs automatically
4. Review and merge


## 🆘 Troubleshooting

### Common Issues
1. **Port conflicts**: Change port in `Server.java` or kill existing processes
2. **Java version**: Ensure Java 21+ is installed
3. **Maven issues**: Clear Maven cache with `mvn clean`
4. **CORS errors**: Check API URL configuration

### Getting Help
- Check the logs in `backend/logs/`
- Verify environment variables
- Ensure all dependencies are installed
- Check GitHub Actions logs for CI/CD issues
