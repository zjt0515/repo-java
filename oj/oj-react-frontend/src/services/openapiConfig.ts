import axios, { type AxiosResponseTransformer } from 'axios'
import { OpenAPI } from '../../generated/core/OpenAPI'

OpenAPI.BASE = import.meta.env.VITE_API_BASE_URL || ''
OpenAPI.WITH_CREDENTIALS = true
OpenAPI.CREDENTIALS = 'include'

const MAX_SAFE_INTEGER = BigInt(Number.MAX_SAFE_INTEGER)

const preserveLargeIntegerJsonResponse: AxiosResponseTransformer = (data) => {
  if (typeof data !== 'string') {
    return data
  }

  const text = data.trim()

  if (!text) {
    return data
  }

  try {
    return JSON.parse(quoteUnsafeIntegerTokens(text))
  } catch {
    return data
  }
}

function quoteUnsafeIntegerTokens(json: string) {
  let result = ''
  let index = 0
  let inString = false
  let escaping = false

  while (index < json.length) {
    const char = json[index]

    if (inString) {
      result += char

      if (escaping) {
        escaping = false
      } else if (char === '\\') {
        escaping = true
      } else if (char === '"') {
        inString = false
      }

      index += 1
      continue
    }

    if (char === '"') {
      inString = true
      result += char
      index += 1
      continue
    }

    if (char === '-' || isDigit(char)) {
      const start = index
      let cursor = char === '-' ? index + 1 : index

      if (json[cursor] === '0') {
        cursor += 1
      } else {
        while (isDigit(json[cursor])) {
          cursor += 1
        }
      }

      const isInteger =
        json[cursor] !== '.' &&
        json[cursor] !== 'e' &&
        json[cursor] !== 'E'
      const token = json.slice(start, cursor)

      result += isInteger && isUnsafeInteger(token) ? `"${token}"` : token
      index = cursor
      continue
    }

    result += char
    index += 1
  }

  return result
}

function isDigit(char: string | undefined) {
  return char !== undefined && char >= '0' && char <= '9'
}

function isUnsafeInteger(token: string) {
  const normalized = token.startsWith('-') ? token.slice(1) : token

  if (!normalized || /^0\d/.test(normalized)) {
    return false
  }

  return BigInt(normalized) > MAX_SAFE_INTEGER
}

axios.defaults.transformResponse = [preserveLargeIntegerJsonResponse]
