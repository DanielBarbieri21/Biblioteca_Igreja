# 📚 Biblioteca Igreja - Sistema de Gestão Moderno

Sistema profissional de gestão de biblioteca comunitária desenvolvido com Spring Boot, oferecendo uma interface web moderna e funcionalidades completas para administração de acervo, usuários e empréstimos.

## 🚀 Características Principais

### ✨ **Interface Moderna**
- Design responsivo com Bootstrap 5
- Dashboard interativo com gráficos e estatísticas
- Navegação intuitiva e experiência de usuário otimizada
- Tema personalizado com gradientes e ícones

### 🔧 **Tecnologias Utilizadas**
- **Backend**: Spring Boot 3.2.0 + Spring Data JPA + Spring Security
- **Frontend**: Thymeleaf + Bootstrap 5 + JavaScript
- **Banco de Dados**: PostgreSQL + Flyway (migrações)
- **Build**: Maven
- **Java**: 17+

### 📋 **Funcionalidades Implementadas**

#### 👥 **Gestão de Usuários**
- ✅ CRUD completo de usuários
- ✅ Validação de dados e emails únicos
- ✅ Controle de empréstimos ativos por usuário
- ✅ Busca e filtros avançados
- ✅ Paginação e ordenação

#### 📖 **Catálogo de Livros**
- ✅ Gestão completa do acervo
- ✅ Controle de disponibilidade e quantidades
- ✅ Busca por título, autor, gênero e ano
- ✅ Categorização por gêneros
- ✅ Validação de ISBN único

#### 🔄 **Sistema de Empréstimos**
- ✅ Realização de empréstimos com validações
- ✅ Controle de datas e prazos
- ✅ Sistema de multas automático
- ✅ Renovação de empréstimos
- ✅ Relatórios de atrasos
- ✅ Status em tempo real

#### 📊 **Dashboard e Relatórios**
- ✅ Estatísticas em tempo real
- ✅ Gráficos interativos (Chart.js)
- ✅ Empréstimos vencendo em breve
- ✅ Livros mais emprestados
- ✅ Distribuição por gêneros
- ✅ Histórico mensal

#### 🔐 **Segurança**
- ✅ Autenticação com Spring Security
- ✅ Controle de acesso às funcionalidades
- ✅ Validação de dados em todas as camadas
- ✅ Tratamento global de exceções

## 🛠️ Instalação e Configuração

### **Pré-requisitos**
- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+
- Git

### **1. Clone o Repositório**
```bash
git clone https://github.com/seu-usuario/biblioteca-igreja.git
cd biblioteca-igreja
```

### **2. Configuração do Banco de Dados**

#### **Criar o banco de dados:**
```sql
CREATE DATABASE biblioteca_comunitaria;
CREATE USER postgres WITH PASSWORD '******'
GRANT ALL PRIVILEGES ON DATABASE biblioteca_comunitaria TO postgres;
```

#### **Configurar conexão no `application.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/biblioteca_comunitaria
    username: postgres
    password: *******
```

### **3. Executar o Projeto**

#### **Via Maven:**
```bash
mvn clean install
mvn spring-boot:run
```

#### **Via IDE:**
Execute a classe `BibliotecaIgrejaApplication.java`

### **4. Acessar o Sistema**
- **URL**: http://localhost:8080
- **Usuário**: admin
- **Senha**: admin123

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/biblioteca/
│   │   ├── config/          # Configurações (Security, etc.)
│   │   ├── controller/      # Controllers REST e Web
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Tratamento de exceções
│   │   ├── repository/      # Repositories Spring Data JPA
│   │   ├── service/         # Lógica de negócio
│   │   └── BibliotecaIgrejaApplication.java
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       ├── templates/       # Templates Thymeleaf
│       └── application.yml  # Configurações
└── test/                    # Testes unitários
```

## 🔧 Configurações Avançadas

### **Personalizar Configurações**

#### **application.yml - Configurações Principais:**
```yaml
spring:
  application:
    name: biblioteca-igreja
  
  # Configurações do banco
  datasource:
    url: jdbc:postgresql://localhost:5432/biblioteca_comunitaria
    username: postgres
    password: *******
  
  # Configurações JPA
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  # Configurações de segurança
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN

# Configurações do servidor
server:
  port: 8080

# Configurações de logging
logging:
  level:
    com.biblioteca: DEBUG
```

### **Configurações de Empréstimo**
No arquivo `EmprestimoService.java`, você pode personalizar:
- **Dias de empréstimo**: `DIAS_EMPRESTIMO = 15`
- **Valor da multa**: `VALOR_MULTA_DIA = R$ 2,00`
- **Limite de livros por usuário**: `3 livros`

## 📊 API REST

### **Endpoints Disponíveis**

#### **Usuários**
- `GET /usuarios/api` - Listar usuários
- `GET /usuarios/api/{id}` - Buscar usuário por ID
- `POST /usuarios/api` - Criar usuário
- `PUT /usuarios/api/{id}` - Atualizar usuário
- `DELETE /usuarios/api/{id}` - Excluir usuário

#### **Livros**
- `GET /livros/api` - Listar livros
- `GET /livros/api/{id}` - Buscar livro por ID
- `GET /livros/api/disponiveis` - Listar livros disponíveis
- `POST /livros/api` - Criar livro
- `PUT /livros/api/{id}` - Atualizar livro
- `DELETE /livros/api/{id}` - Excluir livro

#### **Empréstimos**
- `GET /emprestimos/api` - Listar empréstimos
- `GET /emprestimos/api/{id}` - Buscar empréstimo por ID
- `POST /emprestimos/api` - Realizar empréstimo
- `POST /emprestimos/api/{id}/devolver` - Devolver livro
- `POST /emprestimos/api/{id}/renovar` - Renovar empréstimo

## 🎯 Funcionalidades em Destaque

### **Dashboard Inteligente**
- Visão geral do sistema em tempo real
- Alertas para empréstimos atrasados
- Gráficos de estatísticas
- Ações rápidas para operações comuns

### **Sistema de Busca Avançada**
- Filtros múltiplos para livros
- Busca por nome de usuários
- Ordenação personalizável
- Paginação eficiente

### **Controle de Empréstimos**
- Validações automáticas
- Cálculo de multas
- Renovação inteligente
- Relatórios de atrasos

### **Interface Responsiva**
- Funciona em desktop, tablet e mobile
- Design moderno e intuitivo
- Feedback visual para todas as ações
- Navegação otimizada

## 🔒 Segurança

### **Autenticação**
- Login seguro com Spring Security
- Controle de sessão
- Proteção contra ataques comuns

### **Validações**
- Validação de dados em todas as camadas
- Prevenção de SQL Injection
- Sanitização de entradas
- Tratamento de exceções

## 📈 Monitoramento

### **Logs**
- Logs estruturados para debugging
- Níveis configuráveis
- Rastreamento de operações

### **Métricas**
- Endpoints de monitoramento
- Health checks
- Métricas de performance

## 🚀 Deploy

### **Build para Produção**
```bash
mvn clean package -Pprod
```

### **Docker (Opcional)**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/biblioteca-igreja-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contribuição

### **Como Contribuir**
1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### **Padrões de Código**
- Seguir convenções Java
- Documentar métodos públicos
- Escrever testes unitários
- Manter cobertura de código

## 📝 Changelog

### **v1.0.0** (2025-09-25)
- ✅ Sistema completo de gestão de biblioteca
- ✅ Interface web moderna e responsiva
- ✅ API REST completa
- ✅ Sistema de segurança
- ✅ Dashboard com estatísticas
- ✅ Controle de empréstimos e multas

## 📞 Suporte

### **Contato**
- **Email**: dibarbieri21@gmail.com
- **Telefone**: (32) 99118-6728
- **Endereço**: Rua Nair Furtado de Souza, 10 - Cascatinha  - Juiz de Fora/MG

### **Documentação Adicional**
- [Manual do Usuário](docs/manual-usuario.md)
- [Guia de Instalação](docs/instalacao.md)
- [API Documentation](docs/api.md)

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 🙏 Agradecimentos

- **Padre Tarcísio** - Diretor da Biblioteca
- **Padre David** - Gerente da Biblioteca  
- **Daniel e Jessica** - Voluntários
- **Comunidade Cascatinha** - Pelo apoio e feedback

---

## 👨‍💻 Desenvolvido por:
IronDev Software. Contato: dibarbieri21@gmail.com | (32) 99118-6728

