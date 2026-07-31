# ============================================================================
# TICKET-ADV146 — Spring Boot Dockerfile (multi-stage Maven -> JRE 25 slim)
#
# Stage 1: build the JAR with Temurin JDK 25 + Maven.
# Stage 2: copy ONLY the JAR onto a slim JRE image.
# ============================================================================

FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Install recon-audit-starter using the wrapper from backend
COPY recon-audit-starter/pom.xml recon-audit-starter/
COPY recon-audit-starter/src recon-audit-starter/src
COPY backend/.mvn backend/.mvn/
COPY backend/mvnw backend/
RUN chmod +x backend/mvnw
RUN cd backend && ./mvnw -f ../recon-audit-starter/pom.xml -q -B install

# Build backend
COPY backend/pom.xml backend/
RUN cd backend && ./mvnw -q -B dependency:go-offline || true
COPY backend/src backend/src
RUN cd backend && ./mvnw -q -B -DskipTests package && mv target/reconx-*.jar /workspace/app.jar

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S reconx && adduser -S reconx -G reconx
USER reconx
WORKDIR /app
COPY --from=build /workspace/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
