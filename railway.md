# Railway Configuration for SA-Deliver Backend

# Railway will automatically detect this as a Java Maven project
# No additional configuration needed - just push to Railway

# Environment Variables to set in Railway:
# - PORT (Railway will set this automatically)
# - DB_TYPE=h2
# - DB_URL=jdbc:h2:mem:testdb
# - JWT_SECRET=your-production-secret-key
# - LOG_LEVEL=INFO

# Build command: mvn clean package
# Start command: java -jar target/*.jar
