# Spring Boot AI Chat Summarizer

This project is a proof of concept that demonstrates how to use **Spring Boot** along with **Spring AI** to create a REST API that summarizes a group chat. It integrates with an AI model (e.g., GPT-3) to provide intelligent conversation summarization.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Project Setup](#project-setup)
    - [1. Initialize the Spring Boot Project](#1-initialize-the-spring-boot-project)
    - [2. Configure Spring AI](#2-configure-spring-ai)
    - [3. Define Chat Message Model](#3-define-chat-message-model)
    - [4. Create Chat Summarization Service](#4-create-chat-summarization-service)
    - [5. Expose REST API for Summarization](#5-expose-rest-api-for-summarization)
- [Testing the API](#testing-the-api)
- [Enhancements](#enhancements)
- [License](#license)

## Overview

This project is a simple **Spring Boot** application that accepts a group chat as input, sends it to an AI model via **Spring AI**, and returns a summary of the conversation. It is designed as a demonstration of using AI services within the Spring ecosystem, focusing on chat summarization.

## Requirements

- **Java 23**: Ensure you have JDK 23 installed.
- **Maven**: For building and managing dependencies.
- **Spring Boot**: To create a robust, production-ready API.
- **Spring AI**: To integrate AI models for conversation summarization (e.g., GPT-3, OpenAI models).
- **API Key for AI Service**: You will need an API key for the AI service (e.g., OpenAI) that you want to use.

## Project Setup

### 1. Initialize the Spring Boot Project

Start by creating a new Spring Boot project using Spring Initializer or manually through Maven. Make sure to include the following dependencies:

- **Spring Web**: For building REST APIs.
- **Spring AI**: To integrate AI model interaction.
- **Lombok**: (Optional) For reducing boilerplate code.
- **Spring Boot DevTools**: (Optional) For automatic application reload during development.

Your `pom.xml` should contain the following relevant dependencies:

```xml
<dependencies>
    <!-- Spring Boot Web for REST API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring AI for AI integration -->
    <dependency>
        <groupId>org.springframework.experimental</groupId>
        <artifactId>spring-ai</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>

    <!-- Optional: Lombok for reducing boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 2. Configure Spring AI

Configure **Spring AI** to use the external AI service, such as OpenAI, by setting your API key in `application.properties`:

```properties
spring.ai.openai.api-key=your-api-key-here
```

You will also need to configure the AI service as a bean in your `AiConfiguration` class:

```java
@Configuration
public class AiConfiguration {
    @Bean
    public AiService aiService() {
        return new OpenAiService();  // Example using OpenAI
    }
}
```

### 3. Define Chat Message Model

Create a `ChatMessage` model that represents individual messages within a conversation. This will include fields like the sender, message content, and timestamp.

```java
public class ChatMessage {
    private String sender;
    private String message;
    private LocalDateTime timestamp;
}
```

Additionally, define a `ChatSummaryRequest` class that bundles multiple `ChatMessage` instances into one request:

```java
public class ChatSummaryRequest {
    private List<ChatMessage> messages;
}
```

### 4. Create Chat Summarization Service

Implement a `ChatSummaryService` that sends the chat messages to the AI model via Spring AI to generate a summary.

```java
@Service
public class ChatSummaryService {

    @Autowired
    private AiService aiService;  // Inject the Spring AI service.

    public String summarize(List<ChatMessage> messages) {
        String chatContent = messages.stream()
            .map(ChatMessage::getMessage)
            .collect(Collectors.joining(" "));

        AiRequest request = AiRequest.builder()
            .model("gpt-3")  // Example: GPT-3 model
            .prompt("Summarize the following conversation: " + chatContent)
            .build();

        AiResponse response = aiService.generate(request);
        return response.getSummary();
    }
}
```

### 5. Expose REST API for Summarization

Expose an endpoint `/summarize-chat` that accepts POST requests with chat messages, processes them, and returns a summary.

```java
@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatSummaryService chatSummaryService;

    @Autowired
    public ChatController(ChatSummaryService chatSummaryService) {
        this.chatSummaryService = chatSummaryService;
    }

    @PostMapping("/summarize-chat")
    public ResponseEntity<String> summarizeChat(@RequestBody ChatSummaryRequest chatSummaryRequest) {
        String summary = chatSummaryService.summarize(chatSummaryRequest.getMessages());
        return ResponseEntity.ok(summary);
    }
}
```

## Testing the API

1. **Start the Spring Boot application**:
   Run the application by executing:
   ```bash
   mvn spring-boot:run
   ```

2. **Send a POST request to `/api/summarize-chat`**:
   You can use Postman or cURL to send a POST request to the API. The payload should contain chat messages like this:

   ```json
   {
     "messages": [
       {"sender": "Alice", "message": "Hey, how are you?", "timestamp": "2023-10-17T10:00:00"},
       {"sender": "Bob", "message": "I'm good, thanks! How about you?", "timestamp": "2023-10-17T10:01:00"}
     ]
   }
   ```

3. **Receive the response**:
   The API will respond with a summarized version of the chat using AI:

   ```json
   {
     "summary": "Alice asked how Bob was doing, and Bob replied that he was good."
   }
   ```

## Enhancements

- **NLP Models**: You can improve the summarization by fine-tuning prompts or selecting a more conversation-specific AI model.
- **Database Integration**: If needed, you can integrate a database (e.g., MySQL, PostgreSQL) using Spring Data JPA to store chat messages and summaries.
- **Authentication**: Add security to the API using Spring Security if this service is intended for production use.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---