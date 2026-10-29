# Etapa 1: compila o projeto com Maven (roda os testes tambem)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package

# Etapa 2: imagem leve so com o Java para rodar a API
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/netuni-0.1.0.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Xmx384m -Duser.timezone=America/Sao_Paulo -jar app.jar --server.port=${PORT}"]
