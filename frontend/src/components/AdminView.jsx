import { useState, useEffect } from 'react'
import { adminGetProducts, adminCreateCoupon, adminDeleteCoupon } from '../api'
import styles from './AdminView.module.css'

const EMPTY_FORM = {
    name: '', description: '', imageUrl: '',
    costPrice: '', marginPercentage: '',
    valueType: 'STRING', couponValue: '',
}

export default function AdminView({ onToast }) {
    const [token, setToken] = useState('')
    const [products, setProducts] = useState([])
    const [form, setForm] = useState(EMPTY_FORM)
    const [submitting, setSubmitting] = useState(false)

    useEffect(() => {
        if (token) load()
    }, [token])

    async function load() {
        try {
            const data = await adminGetProducts(token)
            setProducts(data)
        } catch (err) {
            if (err?.error_code === 'UNAUTHORIZED' || err?.error_code === 'FORBIDDEN') {
                onToast('Invalid admin token', 'error')
            }
        }
    }

    function handleChange(e) {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
    }

    async function handleCreate(e) {
        e.preventDefault()
        setSubmitting(true)
        try {
            await adminCreateCoupon(token, {
                name: form.name,
                description: form.description,
                image_url: form.imageUrl,
                cost_price: parseFloat(form.costPrice),
                margin_percentage: parseFloat(form.marginPercentage),
                value_type: form.valueType,
                coupon_value: form.couponValue,
            })
            onToast('Coupon created!', 'success')
            setForm(EMPTY_FORM)
            load()
        } catch (err) {
            onToast(err?.message || 'Failed to create coupon', 'error')
        } finally {
            setSubmitting(false)
        }
    }

    async function handleDelete(id) {
        try {
            await adminDeleteCoupon(token, id)
            onToast('Deleted', 'success')
            load()
        } catch {
            onToast('Failed to delete', 'error')
        }
    }

    return (
        <div className={styles.container}>

            {/* Token input */}
            <div className={styles.tokenBar}>
                <label>🔑 Admin Token</label>
                <input
                    type="password"
                    placeholder="Enter admin token to authenticate"
                    value={token}
                    onChange={e => setToken(e.target.value)}
                />
            </div>

            {/* Create form */}
            <div className={styles.formBox}>
                <h3>Create Coupon</h3>
                <form onSubmit={handleCreate}>
                    <div className={styles.row}>
                        <div className={styles.field}>
                            <label>Name *</label>
                            <input name="name" value={form.name} onChange={handleChange} required placeholder="Amazon $100 Coupon" />
                        </div>
                        <div className={styles.field}>
                            <label>Image URL *</label>
                            <input name="imageUrl" value={form.imageUrl} onChange={handleChange} required placeholder="https://…" />
                        </div>
                    </div>
                    <div className={styles.field}>
                        <label>Description</label>
                        <input name="description" value={form.description} onChange={handleChange} placeholder="Optional" />
                    </div>
                    <div className={styles.row}>
                        <div className={styles.field}>
                            <label>Cost Price *</label>
                            <input name="costPrice" type="number" step="0.01" min="0" value={form.costPrice} onChange={handleChange} required placeholder="80.00" />
                        </div>
                        <div className={styles.field}>
                            <label>Margin % *</label>
                            <input name="marginPercentage" type="number" step="0.01" min="0" value={form.marginPercentage} onChange={handleChange} required placeholder="25" />
                        </div>
                    </div>
                    <div className={styles.row}>
                        <div className={styles.field}>
                            <label>Value Type *</label>
                            <select name="valueType" value={form.valueType} onChange={handleChange}>
                                <option value="STRING">STRING (barcode / code)</option>
                                <option value="IMAGE">IMAGE (URL)</option>
                            </select>
                        </div>
                        <div className={styles.field}>
                            <label>Coupon Value *</label>
                            <input name="couponValue" value={form.couponValue} onChange={handleChange} required placeholder="ABCD-1234" />
                        </div>
                    </div>
                    <button type="submit" className={styles.btnCreate} disabled={submitting || !token}>
                        {submitting ? 'Creating…' : '+ Create Coupon'}
                    </button>
                </form>
            </div>

            {/* Products table */}
            <div className={styles.tableBox}>
                <div className={styles.tableHeader}>
                    <h3>All Coupons</h3>
                    <button className={styles.btnRefresh} onClick={load} disabled={!token}>↻ Refresh</button>
                </div>
                {!products.length
                    ? <p className={styles.empty}>No coupons yet — create one above.</p>
                    : (
                        <table className={styles.table}>
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Cost</th>
                                <th>Margin</th>
                                <th>Min Sell</th>
                                <th>Value</th>
                                <th>Status</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            {products.map(p => (
                                <tr key={p.id} className={p.sold ? styles.sold : ''}>
                                    <td>{p.name}</td>
                                    <td>${Number(p.cost_price).toFixed(2)}</td>
                                    <td>{p.margin_percentage}%</td>
                                    <td>${Number(p.minimum_sell_price).toFixed(2)}</td>
                                    <td className={styles.valueCell}>{p.coupon_value}</td>
                                    <td>
                      <span className={p.sold ? styles.badgeSold : styles.badgeAvail}>
                        {p.sold ? 'SOLD' : 'AVAILABLE'}
                      </span>
                                    </td>
                                    <td>
                                        <button className={styles.btnDelete} onClick={() => handleDelete(p.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )
                }
            </div>
        </div>
    )
}
