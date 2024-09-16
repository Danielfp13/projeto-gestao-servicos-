# Utiliza uma imagem base do OpenJDK 17 (Java 17)
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven para o diretório /app dentro do container
COPY target/clientes-1.0-SNAPSHOT.jar /app/clientes-1.0-SNAPSHOT.jar

# Expõe a porta padrão em que a aplicação Spring Boot roda
EXPOSE 8080

# Variáveis de ambiente do PostgreSQL
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/servicos
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=123

# Comando para rodar a aplicação Spring Boot no container
ENTRYPOINT ["java", "-jar", "/app/clientes-1.0-SNAPSHOT.jar"]

