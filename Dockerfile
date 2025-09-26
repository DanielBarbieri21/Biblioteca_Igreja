# Dockerfile para Biblioteca Igreja
FROM openjdk:17-jdk-slim

# Metadados
LABEL maintainer="Biblioteca Igreja <suporte@bibliotecaigreja.com>"
LABEL description="Sistema de Gestão de Biblioteca Comunitária"
LABEL version="1.0.0"

# Configurar diretório de trabalho
WORKDIR /app

# Instalar dependências do sistema
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar arquivo JAR
COPY target/biblioteca-igreja-1.0.0.jar app.jar

# Criar usuário não-root para segurança
RUN groupadd -r biblioteca && useradd -r -g biblioteca biblioteca
RUN chown -R biblioteca:biblioteca /app
USER biblioteca

# Expor porta
EXPOSE 8080

# Configurar variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
