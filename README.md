# Java + React Chatbot (Rule-based + ML-enhanced)

A production-ready starter project for a hybrid chatbot:
- **Rule-based** patterns for common intents (hours, shipping, refunds, contact, greetings…)
- **ML-enhanced** FAQ matcher using **TF‑IDF + cosine similarity**
- Lightweight **Naive Bayes** intent classifier (from scratch, no heavy ML dependencies)
- **Java 17 + Spring Boot** backend API
- **React + Vite** frontend with a clean chat UI

## Quickstart

### Prereqs
- Java 17+
- Maven 3.9+
- Node 18+ / npm 9+

### Run the backend
```bash
cd server
mvn spring-boot:run
```
This starts the API at `http://localhost:8080`.

### Run the frontend
```bash
cd client
npm install
npm run dev
```
Open the URL that prints in the terminal (usually `http://localhost:5173`).

> The frontend is configured to call `http://localhost:8080` — CORS is enabled on the backend.

## Project Structure 
```
java-react-chatbot/
  server/        # Spring Boot API
  client/        # React UI (Vite)
```

## API 
**POST** `/api/chat/message`

Request :
```json
{ "sessionId": "abc123", "message": "What are your store hours?" }
```

Response:
```json
{
  "reply": "We’re open Mon–Fri, 9am–6pm (EST).",
  "source": "rule",
  "confidence": 1.0,
  "intent": "HOURS"
}
```

## Customize
- Edit FAQs in: `server/src/main/resources/data/faqs.json`
- Add training phrases / labels in: `server/src/main/resources/data/intents.json`
- Update rule replies/patterns in `ChatService` (or extend with your own `RuleEngine`).
- Tweak thresholds in `TfIdfFaqMatcher` and `ChatService`.

## License
MIT
