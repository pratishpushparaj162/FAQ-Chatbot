import React, { useState, useRef, useEffect } from 'react'
import ChatMessage from './components/ChatMessage.jsx'
import SourceBadge from './components/SourceBadge.jsx'
import Suggestions from './components/Suggestions.jsx'

const API = 'http://localhost:8080/api/chat/message'

export default function App() {
  const [messages, setMessages] = useState([
    { role: 'bot', text: "Hi! I'm your hybrid chatbot. Ask me about shipping, refunds, store hours, or anything else.", source: 'system' }
  ])
  const [text, setText] = useState('')
  const [loading, setLoading] = useState(false)
  const listRef = useRef(null)
  const sessionId = useRef(Math.random().toString(36).slice(2))

  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: 'smooth' })
  }, [messages])

  async function send() {
    const msg = text.trim()
    if (!msg) return
    setLoading(true)
    setMessages(m => [...m, { role: 'user', text: msg }])
    setText('')
    try {
      const res = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: sessionId.current, message: msg })
      })
      const data = await res.json()
      setMessages(m => [...m, {
        role: 'bot',
        text: data.reply,
        source: data.source,
        intent: data.intent,
        confidence: data.confidence
      }])
    } catch (e) {
      setMessages(m => [...m, { role: 'bot', text: 'Server is unavailable. Did you start the backend?', source: 'error' }])
    } finally {
      setLoading(false)
    }
  }

  function quick(msg) {
    setText(msg)
  }

  return (
    <div className="container">
      <div className="card">
        <div className="header">
          <h1>Hybrid Chatbot</h1>
          <span className="badge">Java + React</span>
          <span className="badge">Rule + ML</span>
        </div>

        <div ref={listRef} className="messages">
          {messages.map((m, i) => (
            <div key={i}>
              <ChatMessage role={m.role} text={m.text} />
              {m.role === 'bot' && m.source && (
                <div className="meta">
                  <SourceBadge source={m.source} />
                  {typeof m.confidence === 'number' && <span>conf: {m.confidence.toFixed(2)}</span>}
                  {m.intent && <span>intent: {m.intent}</span>}
                </div>
              )}
            </div>
          ))}
        </div>

        <Suggestions items={[
          "What are your store hours?",
          "How long does shipping take?",
          "How do I reset my password?",
          "Do you offer student discounts?"
        ]} onPick={quick} />

        <div className="inputbar">
          <input
            type="text"
            placeholder="Type a message…"
            value={text}
            onChange={e => setText(e.target.value)}
            onKeyDown={e => e.key === 'Enter' ? send() : null}
          />
          <button onClick={send} disabled={loading}>{loading ? 'Sending…' : 'Send'}</button>
        </div>

        <div className="footer">
          Tip: try “refund”, “shipping”, “contact”, “hello”, or any FAQ-like question.
        </div>
      </div>
    </div>
  )
}
