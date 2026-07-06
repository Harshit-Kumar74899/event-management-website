# ---- Stage 1: Compile the Java source ----
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /build

COPY src ./src
COPY lib ./lib

RUN mkdir -p classes && \
    find src -name "*.java" > sources.txt && \
    javac -d classes -cp "lib/*" @sources.txt

# StaticFileHandler / ImageHandler load pages via getResourceAsStream("/web/..."),
# which reads from the classpath root — so copy the "web" folder next to the
# compiled classes.
RUN cp -r src/web classes/web

# ---- Stage 2: Run with a lightweight JRE ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/classes ./classes
COPY lib ./lib

# Render sets $PORT automatically; MainServer.java reads it via System.getenv("PORT")
EXPOSE 8081

CMD ["sh", "-c", "java -cp classes:lib/* MainServer"]
