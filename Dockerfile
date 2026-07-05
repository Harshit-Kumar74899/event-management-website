# ---- Stage 1: Compile the Java source ----
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /build

# Copy source code and the jar libraries (mysql, sendgrid, jackson, mail)
COPY src ./src
COPY lib ./lib

# Compile all .java files into /build/classes, using the jars in lib/ as classpath
RUN mkdir -p classes && \
    find src -name "*.java" > sources.txt && \
    javac -d classes -cp "lib/*" @sources.txt

# Some handlers (IndexHandler, AppointmentHandler) load pages via
# getResourceAsStream("/web/..."), which reads from the classpath root.
# So we also copy the static "web" folder next to the compiled classes.
RUN cp -r src/web classes/web

# ---- Stage 2: Run the app with a lightweight JRE ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/classes ./classes
COPY lib ./lib

# Other handlers (LoginHandler, SignupHandler, DashboardHandler, ImageHandler,
# StaticFileHandler) read files directly off disk using relative paths like
# "src/web/login.html", based on the process's working directory. So we also
# keep a real "src/web" folder here, matching those relative paths.
COPY src/web ./src/web

# Render sets $PORT automatically; MainServer.java reads it via
# System.getenv().getOrDefault("PORT", "8081")
EXPOSE 8081

CMD ["sh", "-c", "java -cp classes:lib/* MainServer"]
