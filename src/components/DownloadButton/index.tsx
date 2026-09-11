import React, { JSX } from 'react';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

interface Props {
  /** Ruta del archivo dentro de static/, p. ej. 'plantillas/plantilla.md' */
  file: string;
  /** Nombre con el que se guarda el archivo descargado */
  filename?: string;
  children: React.ReactNode;
}

export default function DownloadButton({
  file,
  filename,
  children,
}: Props): JSX.Element {
  const url = useBaseUrl(file);

  return (
    <div className={styles.wrapper}>
      <a
        className={styles.button}
        href={url}
        download={filename ?? file.split('/').pop()}
      >
        <span className={styles.icon}>⬇</span>
        {children}
      </a>
    </div>
  );
}
