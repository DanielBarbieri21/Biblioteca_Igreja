@echo off
REM Script de inicialização para Biblioteca Igreja (Windows)
REM Uso: scripts\start.bat [dev|prod]

setlocal enabledelayedexpansion

REM Configurar cores (se suportado)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Função para log
:log
echo %BLUE%[%date% %time%]%NC% %~1
goto :eof

:error
echo %RED%[ERROR]%NC% %~1 >&2
goto :eof

:success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

REM Verificar se Java está instalado
:check_java
java -version >nul 2>&1
if errorlevel 1 (
    call :error "Java não está instalado. Por favor, instale Java 17 ou superior."
    exit /b 1
)
call :success "Java encontrado"
goto :eof

REM Verificar se Maven está instalado
:check_maven
mvn -version >nul 2>&1
if errorlevel 1 (
    call :error "Maven não está instalado. Por favor, instale Maven 3.6 ou superior."
    exit /b 1
)
call :success "Maven encontrado"
goto :eof

REM Verificar se PostgreSQL está rodando
:check_postgres
REM Verificação básica - pode ser melhorada
call :warning "Certifique-se de que o PostgreSQL está instalado e rodando na porta 5432"
goto :eof

REM Compilar o projeto
:build_project
call :log "Compilando o projeto..."
mvn clean compile
if errorlevel 1 (
    call :error "Falha na compilação do projeto"
    exit /b 1
)
call :success "Projeto compilado com sucesso"
goto :eof

REM Executar testes
:run_tests
call :log "Executando testes..."
mvn test
if errorlevel 1 (
    call :warning "Alguns testes falharam, mas continuando..."
) else (
    call :success "Todos os testes passaram"
)
goto :eof

REM Empacotar aplicação
:package_app
call :log "Empacotando aplicação..."
mvn package -DskipTests
if errorlevel 1 (
    call :error "Falha no empacotamento"
    exit /b 1
)
call :success "Aplicação empacotada com sucesso"
goto :eof

REM Iniciar aplicação
:start_app
set "profile=%~1"
if "%profile%"=="" set "profile=dev"

call :log "Iniciando aplicação com perfil: %profile%"

if "%profile%"=="prod" (
    java -jar target\biblioteca-igreja-1.0.0.jar --spring.profiles.active=prod
) else (
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
)
goto :eof

REM Função principal
:main
set "profile=%~1"
if "%profile%"=="" set "profile=dev"

call :log "Iniciando Biblioteca Igreja - Sistema de Gestão"
call :log "Perfil: %profile%"

REM Verificações
call :check_java
if errorlevel 1 exit /b 1

call :check_maven
if errorlevel 1 exit /b 1

call :check_postgres

REM Build
call :build_project
if errorlevel 1 exit /b 1

if "%profile%"=="prod" (
    call :run_tests
    call :package_app
    if errorlevel 1 exit /b 1
)

REM Iniciar aplicação
call :start_app "%profile%"
goto :eof

REM Verificar argumentos de ajuda
if "%1"=="--help" (
    echo Uso: %0 [dev^|prod]
    echo.
    echo Argumentos:
    echo   dev   - Inicia em modo desenvolvimento (padrão)
    echo   prod  - Inicia em modo produção
    echo.
    echo Exemplos:
    echo   %0        # Modo desenvolvimento
    echo   %0 dev    # Modo desenvolvimento
    echo   %0 prod   # Modo produção
    exit /b 0
)

if "%1"=="-h" (
    goto :--help
)

REM Executar função principal
call :main %*
