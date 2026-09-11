import React, { JSX, useEffect, useState } from 'react';
import styles from './styles.module.css';

const STORAGE_KEY = 'wip-banner-dismissed';

export default function WipBanner(): JSX.Element | null {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const dismissed = localStorage.getItem(STORAGE_KEY);
    if (!dismissed) {
      setVisible(true);
    }
  }, []);

  function handleDismiss() {
    localStorage.setItem(STORAGE_KEY, '1');
    setVisible(false);
  }

  if (!visible) return null;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <div className={styles.icon}>🚧</div>
        <h2 className={styles.title}>Proxecto en construción</h2>
        <p className={styles.message}>
          Esta web está en fase de desenvolvemento activo. Algúns contidos poden
          estar incompletos, conter erros ou cambiar sen aviso previo.
          Agradecemos a túa comprensión.
        </p>
        <button className={styles.button} onClick={handleDismiss}>
          Entendido
        </button>
      </div>
    </div>
  );
}
