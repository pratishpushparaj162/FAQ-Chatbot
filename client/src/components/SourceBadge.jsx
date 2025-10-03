import React from 'react'

export default function SourceBadge({ source }) {
  const label = {
    rule: 'Rule',
    faq: 'FAQ (TF-IDF)',
    smalltalk: 'Smalltalk',
    system: 'System',
    fallback: 'Fallback',
    error: 'Error'
  }[source] || source

  return <span className="badge">{label}</span> 
}
