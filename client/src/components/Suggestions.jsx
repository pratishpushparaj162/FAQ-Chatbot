import React from 'react'

export default function Suggestions({ items, onPick }) {
  return (
    <div className="suggestions">
      {items.map((s, i) => (
        <span key={i} className="suggestion" onClick={() => onPick(s)}>{s}</span>
      ))}
    </div>
  )
}
