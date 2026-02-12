FROM openjdk:17

WORKDIR /app

COPY . .

RUN mkdir out
RUN javac -cp "lib/*" -d out src/**/*.java src/*.java

CMD ["sh", "-c", "java -cp out:lib/* MainServer"]
