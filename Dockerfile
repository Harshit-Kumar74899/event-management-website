FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mkdir out
RUN javac -cp "lib/*" -d out src/**/*.java src/*.java

CMD ["sh", "-c", "java -cp out:lib/* MainServer"]
