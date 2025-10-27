@echo off
echo Starting SA-Deliver Server...
java -cp "target/classes;target/dependency/*" main.java.Server
pause
