.PHONY: help build test start stop clean docker-build docker-run docker-stop

# Variables
JAVA_MAIN_CLASS=main.java.Server
DOCKER_IMAGE=sa-deliver-backend
DOCKER_CONTAINER=sa-deliver-container
DOCKER_TAG=latest

# Help target to show all available commands
help:
	@echo "Available commands:"
	@echo "  make build       - Compile the project"
	@echo "  make test        - Run tests"
	@echo "  make start       - Start the server"
	@echo "  make clean       - Clean build artifacts"
	@echo "  make docker-build - Build Docker image"
	@echo "  make docker-run   - Run the application in Docker"
	@echo "  make docker-stop  - Stop and remove Docker container"

# Build the project
build:
	@echo "Building the project..."
	cd backend && mvn clean package

# Run tests
test:
	@echo "Running tests..."
	cd backend && mvn test

# Start the server
start:
	@echo "Starting the server..."
	cd backend && mvn spring-boot:run

# Clean build artifacts
clean:
	@echo "Cleaning..."
	cd backend && mvn clean

docker-build:
	@echo "Building Docker image..."
	docker build -t $(DOCKER_IMAGE):$(DOCKER_TAG) -f ./Dockerfile .

docker-run:
	@echo "Starting Docker container..."
	docker run -d --name $(DOCKER_CONTAINER) -p 8080:8080 $(DOCKER_IMAGE):$(DOCKER_TAG)


docker-stop:
	@echo "Stopping and removing Docker container..."
	-docker stop $(DOCKER_CONTAINER)
	-docker rm $(DOCKER_CONTAINER)

# Default target
default: help