# SA-Deliver E-Commerce Application

A full-stack e-commerce application built with Java (Spark Framework) backend and vanilla JavaScript frontend.

## Features

- **User Authentication**: Register, login, logout with session management
- **Product Management**: Browse products, search, filter by categories
- **User Profiles**: View and update user profiles
- **Responsive Design**: Modern, mobile-friendly interface
- **RESTful API**: Clean API endpoints for all operations

## Prerequisites

- **Java 17 or higher**
- **Maven 3.6 or higher**
- **Python 3.x** (for frontend HTTP server)
- **Git** (for cloning the repository)

## Quick Start

### Option 1: Automated Scripts (Recommended)

#### Windows Users:
```bash
# Run the batch file
start-app.bat

# Or run the PowerShell script
powershell -ExecutionPolicy Bypass -File start-app.ps1
```

#### Linux/macOS Users:
```bash
# Make the script executable and run
chmod +x start-app.sh
./start-app.sh
```

### Option 2: Manual Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd SA-deliver
   ```

2. **Build the backend:**
   ```bash
   cd backend
   mvn clean compile
   ```

3. **Start the backend server:**
   ```bash
   mvn exec:java -Dexec.mainClass="main.java.Server"
   ```

4. **Start the frontend server:**
   ```bash
   cd ../frontend
   python -m http.server 3000
   ```

5. **Open your browser:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

## Application URLs

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/health

## Test Credentials

- **Username**: `admin`, **Password**: `admin123`
- **Username**: `test`, **Password**: `test123`

## API Endpoints

### Authentication
- `POST /api/register` - Register a new user
- `POST /api/login` - User login
- `POST /api/logout` - User logout

### Products
- `GET /api/products` - Get all products
- `GET /api/products/:id` - Get product by ID
- `GET /api/products/search/:query` - Search products
- `GET /api/products/category/:category` - Get products by category
- `POST /api/products` - Add new product (authenticated)
- `PUT /api/products/:id` - Update product (authenticated)
- `DELETE /api/products/:id` - Delete product (authenticated)

### Categories
- `GET /api/categories` - Get all categories

### User Profile
- `GET /api/profile` - Get user profile (authenticated)
- `PUT /api/profile` - Update user profile (authenticated)
- `DELETE /api/profile` - Delete user account (authenticated)

## Project Structure

```
SA-deliver/
├── backend/
│   ├── src/main/java/main/java/
│   │   ├── Server.java              # Main server class
│   │   ├── User.java                # User model
│   │   ├── Product.java             # Product model
│   │   ├── UserService.java         # User business logic
│   │   ├── ProductService.java      # Product business logic
│   │   └── Response.java            # API response wrapper
│   ├── src/main/java/main/java/entities/
│   │   ├── User.java                # JPA User entity
│   │   └── Product.java             # JPA Product entity
│   ├── src/main/resources/
│   │   └── hibernate.cfg.xml         # Hibernate configuration
│   └── pom.xml                      # Maven dependencies
├── frontend/
│   ├── index.html                   # Main HTML file
│   ├── app.js                       # JavaScript application
│   └── styles.css                   # CSS styles
├── start-app.sh                     # Linux/macOS startup script
├── start-app.bat                    # Windows batch startup script
├── start-app.ps1                    # PowerShell startup script
└── README.md                        # This file
```

## Technology Stack

### Backend
- **Java 17** - Programming language
- **Spark Framework** - Web framework
- **Maven** - Dependency management
- **Hibernate** - ORM (configured but using in-memory storage)
- **H2 Database** - In-memory database
- **JWT** - Authentication tokens
- **Gson** - JSON processing

### Frontend
- **HTML5** - Markup
- **CSS3** - Styling with modern features
- **Vanilla JavaScript** - Client-side logic
- **Font Awesome** - Icons
- **Responsive Design** - Mobile-friendly

## Development

### Running Tests
```bash
cd backend
mvn test
```

### Building for Production
```bash
cd backend
mvn clean package
```

### Docker Support
```bash
# Build Docker image
make docker-build

# Run in Docker
make docker-run

# Stop Docker container
make docker-stop
```

## Troubleshooting

### Common Issues

1. **Port already in use:**
   - The scripts will automatically kill existing processes
   - Or manually kill processes using ports 8080 and 3000

2. **Java version issues:**
   - Ensure Java 17+ is installed and in PATH
   - Check with `java -version`

3. **Maven not found:**
   - Install Maven and ensure it's in PATH
   - Check with `mvn -version`

4. **Frontend not loading:**
   - Ensure Python is installed for HTTP server
   - Or manually open `frontend/index.html` in browser

### Logs and Debugging

- Backend logs are displayed in the terminal
- Frontend errors are shown in browser console
- API responses include detailed error messages

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the API documentation
3. Check browser console for frontend errors
4. Review backend terminal output for server errors
