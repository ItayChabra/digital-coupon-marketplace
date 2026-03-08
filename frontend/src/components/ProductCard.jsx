import { useState } from 'react'
import styles from './ProductCard.module.css'

export default function ProductCard({ product, onPurchase, purchaseResult }) {
    const [loading, setLoading] = useState(false)

    async function handlePurchase() {
        setLoading(true)
        await onPurchase(product.id)
        setLoading(false)
    }

    return (
        <div className={styles.card}>
            {product.image_url && (
                <img
                    className={styles.image}
                    src={product.image_url}
                    alt={product.name}
                    onError={e => (e.target.style.display = 'none')}
                />
            )}
            <div className={styles.body}>
                <h3 className={styles.name}>{product.name}</h3>
                {product.description && <p className={styles.desc}>{product.description}</p>}
                <p className={styles.price}>${Number(product.price).toFixed(2)}</p>

                {purchaseResult ? (
                    <div className={styles.result}>
                        <span>Purchased!</span>
                        <strong>{purchaseResult.value_type}: </strong>
                        <code>{purchaseResult.value}</code>
                    </div>
                ) : (
                    <button className={styles.btn} onClick={handlePurchase} disabled={loading}>
                        {loading ? 'Processing…' : 'Purchase'}
                    </button>
                )}
            </div>
        </div>
    )
}
