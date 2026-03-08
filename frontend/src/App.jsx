import { useState } from 'react'
import CustomerView from './components/CustomerView'
import AdminView from './components/AdminView'
import ResellerView from './components/ResellerView'
import Toast from './components/Toast'
import { useToast } from './hooks/useToast'
import styles from './App.module.css'

const TABS = [
    { id: 'customer', label: 'Customer' },
    { id: 'reseller', label: 'Reseller' },
    { id: 'admin',    label: 'Admin'    },
]

export default function App() {
    const [activeTab, setActiveTab] = useState('customer')
    const { toast, show } = useToast()

    return (
        <div className={styles.app}>
            <header className={styles.header}>
                <div className={styles.headerInner}>
                    <span className={styles.logo}>Nexus Coupon Marketplace</span>
                    <nav className={styles.tabs}>
                        {TABS.map(tab => (
                            <button
                                key={tab.id}
                                className={`${styles.tab} ${activeTab === tab.id ? styles.active : ''}`}
                                onClick={() => setActiveTab(tab.id)}
                            >
                                {tab.label}
                            </button>
                        ))}
                    </nav>
                </div>
            </header>

            <main className={styles.main}>
                {activeTab === 'customer' && <CustomerView onToast={show} />}
                {activeTab === 'reseller' && <ResellerView onToast={show} />}
                {activeTab === 'admin'    && <AdminView    onToast={show} />}
            </main>

            <Toast toast={toast} />
        </div>
    )
}
