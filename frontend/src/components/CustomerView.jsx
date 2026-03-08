import { useState, useEffect } from 'react'
import { getAvailableProducts, customerPurchase } from '../api'
import ProductCard from './ProductCard'
import styles from './CustomerView.module.css'

export default function CustomerView({ onToast }) {
    const [products, setProducts] = useState([])
    const [results, setResults] = useState({})  // productId → purchase result
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        load()
    }, [])

    async function load() {
        setLoading(true)
        try {
            const data = await getAvailableProducts()
            setProducts(data)
        } catch {
            onToast('Failed to load products', 'error')
        } finally {
            setLoading(false)
        }
    }

    async function handlePurchase(id) {
        try {
            const result = await customerPurchase(id)
            setResults(prev => ({ ...prev, [id]: result }))
            // Remove the purchased product from the list after a short delay
            setTimeout(() => setProducts(prev => prev.filter(p => p.id !== id)), 2000)
        } catch (err) {
            onToast(err?.message || 'Purchase failed', 'error')
        }
    }

    if (loading) return <p className={styles.status}>Loading products…</p>
    if (!products.length) return <p className={styles.status}>No coupons available right now.</p>

    return (
        <div className={styles.grid}>
            {products.map(p => (
                <ProductCard
                    key={p.id}
                    product={p}
                    onPurchase={handlePurchase}
                    purchaseResult={results[p.id]}
                />
            ))}
        </div>
    )
}
