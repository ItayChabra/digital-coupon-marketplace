import { useState, useEffect } from 'react'
import { resellerGetProducts, resellerPurchase } from '../api'
import styles from './ResellerView.module.css'

export default function ResellerView({ onToast }) {
    const [token, setToken] = useState('')
    const [products, setProducts] = useState([])
    const [prices, setPrices] = useState({})      // productId → input price
    const [results, setResults] = useState({})    // productId → purchase result
    const [loading, setLoading] = useState(false)

    async function load() {
        if (!token) return
        setLoading(true)
        try {
            const data = await resellerGetProducts(token)
            setProducts(data)
        } catch (err) {
            onToast(err?.message || 'Failed to load — check token', 'error')
        } finally {
            setLoading(false)
        }
    }

    async function handlePurchase(id) {
        const price = parseFloat(prices[id])
        if (!price || isNaN(price)) {
            onToast('Enter a valid reseller price', 'error')
            return
        }
        try {
            const result = await resellerPurchase(token, id, price)
            setResults(prev => ({ ...prev, [id]: result }))
            setTimeout(() => setProducts(prev => prev.filter(p => p.id !== id)), 2000)
            onToast('Purchase successful!', 'success')
        } catch (err) {
            onToast(err?.message || 'Purchase failed', 'error')
        }
    }

    return (
        <div className={styles.container}>
            <div className={styles.tokenBar}>
                <label>🔑 Reseller Token</label>
                <input
                    type="password"
                    placeholder="Enter reseller token"
                    value={token}
                    onChange={e => setToken(e.target.value)}
                />
                <button className={styles.btnLoad} onClick={load} disabled={!token}>Load Products</button>
            </div>

            {loading && <p className={styles.status}>Loading…</p>}

            {!loading && products.length === 0 && token && (
                <p className={styles.status}>No available products — press Load Products.</p>
            )}

            <div className={styles.grid}>
                {products.map(p => (
                    <div key={p.id} className={styles.card}>
                        <div className={styles.cardName}>{p.name}</div>
                        <div className={styles.cardDesc}>{p.description}</div>
                        <div className={styles.minPrice}>Min price: <strong>${Number(p.price).toFixed(2)}</strong></div>

                        {results[p.id] ? (
                            <div className={styles.result}>
                                ✅ Sold at ${Number(results[p.id].final_price).toFixed(2)}<br />
                                <strong>{results[p.id].value_type}:</strong> <code>{results[p.id].value}</code>
                            </div>
                        ) : (
                            <div className={styles.purchaseRow}>
                                <input
                                    type="number"
                                    step="0.01"
                                    min={p.price}
                                    placeholder={`≥ ${p.price}`}
                                    value={prices[p.id] || ''}
                                    onChange={e => setPrices(prev => ({ ...prev, [p.id]: e.target.value }))}
                                />
                                <button className={styles.btnBuy} onClick={() => handlePurchase(p.id)}>Buy</button>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    )
}
