const BASE = '/api'

// ── helpers ──────────────────────────────────────────────────────────────────

async function request(path, options = {}) {
    const { headers: optHeaders, ...restOptions } = options
    const res = await fetch(`${BASE}${path}`, {
        ...restOptions,
        headers: { 'Content-Type': 'application/json', ...optHeaders },
    })

    if (res.status === 204) return null

    const data = await res.json()
    if (!res.ok) throw data  // throws the { error_code, message } object
    return data
}

function authHeader(token) {
    return token ? { Authorization: `Bearer ${token}` } : {}
}

// ── Customer API ──────────────────────────────────────────────────────────────

export const getAvailableProducts = () =>
    request('/customer/products')

export const getProductById = (id) =>
    request(`/customer/products/${id}`)

export const customerPurchase = (id) =>
    request(`/customer/products/${id}/purchase`, { method: 'POST' })

// ── Admin API ─────────────────────────────────────────────────────────────────

export const adminGetProducts = (token) =>
    request('/admin/products', { headers: authHeader(token) })

export const adminCreateCoupon = (token, body) =>
    request('/admin/products', {
        method: 'POST',
        headers: authHeader(token),
        body: JSON.stringify(body),
    })

export const adminDeleteCoupon = (token, id) =>
    request(`/admin/products/${id}`, {
        method: 'DELETE',
        headers: authHeader(token),
    })

// ── Reseller API ──────────────────────────────────────────────────────────────

export const resellerGetProducts = (token) =>
    request('/v1/products', { headers: authHeader(token) })

export const resellerPurchase = (token, id, resellerPrice) =>
    request(`/v1/products/${id}/purchase`, {
        method: 'POST',
        headers: authHeader(token),
        body: JSON.stringify({ reseller_price: resellerPrice }),
    })
