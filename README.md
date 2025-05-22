# HealthPet

Um aplicativo Android para gerenciamento de pets e seus tutores, desenvolvido com foco em boas práticas de programação e princípios SOLID.

## Sobre o Projeto

HealthPet3 é um aplicativo que permite o gerenciamento de pets e seus tutores, oferecendo funcionalidades como:
- Cadastro e autenticação de tutores
- Gerenciamento de perfis de pets
- Sistema de onboarding
- Integração com autenticação social (Google e Facebook)

## Arquitetura e Padrões

### Estrutura do Projeto
```
app/
├── data/           # Camada de dados e serviços
├── models/         # Modelos de domínio
├── repositories/   # Repositórios para acesso a dados
├── ui/            # Interface do usuário
│   ├── home/      # Telas principais
│   ├── login/     # Autenticação
│   ├── pet/       # Gerenciamento de pets
│   └── tutor/     # Gerenciamento de tutores
│   └── signup/    # Cadastro do tutor e do pet
└── adapters/      # Adaptadores para RecyclerView
└── utils/        # Para utilidades padrões
```

## Princípios SOLID Implementados

### 1. Single Responsibility Principle (SRP)
- **Implementado:**
  - Separação clara entre camadas (UI, dados, repositórios)
  - Classes como `AuthManager` focadas em autenticação
  - Activities com responsabilidades específicas (ex: `LoginActivity`, `PetProfileActivity`)

### 2. Open/Closed Principle (OCP)
- **Implementado:**
  - Interface `AuthService` permite extensão sem modificação
  - Sistema de adapters extensível para diferentes tipos de visualização

### 3. Liskov Substitution Principle (LSP)
- **Parcialmente Implementado:**
  - Hierarquia de classes seguindo contratos de interfaces
  - Possibilidade de substituir implementações concretas por suas abstrações

### 4. Interface Segregation Principle (ISP)
- **Implementado:**
  - Interfaces específicas como `AuthService` e `StorageService`
  - Callbacks específicos para diferentes operações

### 5. Dependency Inversion Principle (DIP)
- **Parcialmente Implementado:**
  - Uso de interfaces para serviços (`AuthService`)
  - Injeção de dependências manual em algumas classes

## Padrões de Projeto Utilizados

### 1. Repository Pattern
- Implementado nos repositórios `PetRepository`, `TutorRepository`
- Abstrai a fonte de dados da lógica de negócios

### 2. Adapter Pattern
- Usado em `PetAdapter` para RecyclerView
- Separa a lógica de apresentação dos dados

### Observer Pattern
- Implementado através de callbacks e listeners
- Usado para atualizações de UI e eventos de autenticação

## Áreas para Melhoria

1. **Dependency Injection**
   - Implementar um framework de DI (como Hilt ou Koin)
   - Reduzir acoplamento entre componentes

2. **Clean Architecture**
   - Separar melhor as camadas de domínio
   - Implementar casos de uso (Use Cases)

3. **Unit Testing**
   - Aumentar cobertura de testes
   - Implementar testes de integração

4. **ViewModels**
   - Implementar MVVM para melhor separação de responsabilidades
   - Usar LiveData ou Flow para reatividade

4. **Terminar o Codigo**
   - Terminar de codar a home principal, o tutor profile e as paginas de edição

## Tecnologias Utilizadas

- Firebase Authentication
- Firebase Firestore
- Google Sign-In
- Facebook Login
- Glide para carregamento de imagens
- RecyclerView para listas
- Material Design Components

## Requisitos

- Android SDK 24+
- Java 11
- Gradle 8.8.2

## Como Executar

1. Clone o repositório
2. Configure o arquivo `google-services.json` com suas credenciais Firebase
3. Execute o projeto no Android Studio
