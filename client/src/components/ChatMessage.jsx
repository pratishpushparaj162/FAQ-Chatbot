import React from 'react'

export default function ChatMessage({ role, text }) {
  return (
    <div className={'msg ' + (role === 'user' ? 'user' : 'bot')}>
      {text}
    </div>
  )
}
