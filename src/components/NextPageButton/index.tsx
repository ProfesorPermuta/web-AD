import React, { JSX } from 'react';
import Link from '@docusaurus/Link';
import styles from './styles.module.css';

interface Props {
  /** Ruta interna a la siguiente página, p. ej. '/tema-0/b-proyecto-multagal' */
  to: string;
  /** Texto del botón; por defecto "Siguiente" */
  children?: React.ReactNode;
}

export default function NextPageButton({ to, children }: Props): JSX.Element {
  return (
    <div className={styles.wrapper}>
      <Link className={styles.button} to={to}>
        <span>
          <span className={styles.label}>Siguiente</span>
          <br />
          {children}
        </span>
        <span className={styles.icon}>→</span>
      </Link>
    </div>
  );
}
