#!/bin/bash

# Script de inicialização para Biblioteca Igreja
# Uso: ./scripts/start.sh [dev|prod]

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Verificar se Java está instalado
check_java() {
    if ! command -v java &> /dev/null; then
        error "Java não está instalado. Por favor, instale Java 17 ou superior."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        error "Java 17 ou superior é necessário. Versão atual: $JAVA_VERSION"
        exit 1
    fi
    
    success "Java $JAVA_VERSION encontrado"
}

# Verificar se Maven está instalado
check_maven() {
    if ! command -v mvn &> /dev/null; then
        error "Maven não está instalado. Por favor, instale Maven 3.6 ou superior."
        exit 1
    fi
    
    success "Maven encontrado"
}

# Verificar se PostgreSQL está rodando
check_postgres() {
    if ! command -v psql &> /dev/null; then
        warning "PostgreSQL client não encontrado. Certifique-se de que o PostgreSQL está instalado e rodando."
        return
    fi
    
    if ! pg_isready -h localhost -p 5432 &> /dev/null; then
        warning "PostgreSQL não está rodando na porta 5432. Certifique-se de que o banco está iniciado."
    else
        success "PostgreSQL está rodando"
    fi
}

# Compilar o projeto
build_project() {
    log "Compilando o projeto..."
    mvn clean compile
    
    if [ $? -eq 0 ]; then
        success "Projeto compilado com sucesso"
    else
        error "Falha na compilação do projeto"
        exit 1
    fi
}

# Executar testes
run_tests() {
    log "Executando testes..."
    mvn test
    
    if [ $? -eq 0 ]; then
        success "Todos os testes passaram"
    else
        warning "Alguns testes falharam, mas continuando..."
    fi
}

# Empacotar aplicação
package_app() {
    log "Empacotando aplicação..."
    mvn package -DskipTests
    
    if [ $? -eq 0 ]; then
        success "Aplicação empacotada com sucesso"
    else
        error "Falha no empacotamento"
        exit 1
    fi
}

# Iniciar aplicação
start_app() {
    local profile=${1:-dev}
    
    log "Iniciando aplicação com perfil: $profile"
    
    if [ "$profile" = "prod" ]; then
        java -jar target/biblioteca-igreja-1.0.0.jar --spring.profiles.active=prod
    else
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
    fi
}

# Função principal
main() {
    local profile=${1:-dev}
    
    log "Iniciando Biblioteca Igreja - Sistema de Gestão"
    log "Perfil: $profile"
    
    # Verificações
    check_java
    check_maven
    check_postgres
    
    # Build
    build_project
    
    if [ "$profile" = "prod" ]; then
        run_tests
        package_app
    fi
    
    # Iniciar aplicação
    start_app "$profile"
}

# Verificar argumentos
if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
    echo "Uso: $0 [dev|prod]"
    echo ""
    echo "Argumentos:"
    echo "  dev   - Inicia em modo desenvolvimento (padrão)"
    echo "  prod  - Inicia em modo produção"
    echo ""
    echo "Exemplos:"
    echo "  $0        # Modo desenvolvimento"
    echo "  $0 dev    # Modo desenvolvimento"
    echo "  $0 prod   # Modo produção"
    exit 0
fi

# Executar função principal
main "$@"
