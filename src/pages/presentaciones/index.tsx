import React, { JSX } from 'react';
import Layout from '@theme/Layout';
import useBaseUrl from '@docusaurus/useBaseUrl';

type Presentacion = {
  titulo: string;
  descripcion: string;
  href: string;
};

// Cada presentación generada con la skill `presentacion-clase` se guarda en
// static/presentaciones/<slug-tema>.html. Al añadir una nueva, registrarla
// aquí para que aparezca en el índice.
const presentaciones: Presentacion[] = [
  {
    titulo: 'Tema 0 — Arranque del proyecto',
    descripcion: 'Entorno de desarrollo, Spring Boot y estructura del proyecto base.',
    href: '/presentaciones/tema-0-arranque.html',
  },
  {
    titulo: 'Tema 1 — Gestión de ficheros y directorios',
    descripcion: 'Manejo de ficheros, directorios y flujos de entrada/salida en Java.',
    href: '/presentaciones/tema-1-ficheros.html',
  },
];

function PresentacionCard({ presentacion }: { presentacion: Presentacion }): JSX.Element {
  const href = useBaseUrl(presentacion.href);
  return (
    <a
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      style={{
        display: 'block',
        padding: '1.25rem 1.5rem',
        borderRadius: '8px',
        border: '1px solid var(--ifm-color-emphasis-300)',
        textDecoration: 'none',
        color: 'inherit',
        transition: 'border-color 0.2s, box-shadow 0.2s',
      }}
    >
      <h3 style={{ margin: '0 0 0.35rem' }}>{presentacion.titulo}</h3>
      <p style={{ margin: 0, color: 'var(--ifm-color-emphasis-700)' }}>
        {presentacion.descripcion}
      </p>
    </a>
  );
}

export default function PresentacionesPage(): JSX.Element {
  return (
    <Layout
      title="Presentaciones"
      description="Diapositivas de clase de Acceso a Datos"
    >
      <div style={{ maxWidth: '900px', margin: '0 auto', padding: '2rem 1rem' }}>
        <h1>Presentaciones</h1>
        <p>
          Diapositivas usadas en clase, navegables con flechas, barra
          espaciadora o swipe táctil. Se abren en una pestaña independiente.
        </p>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1.5rem' }}>
          {presentaciones.map((p) => (
            <PresentacionCard key={p.href} presentacion={p} />
          ))}
        </div>
      </div>
    </Layout>
  );
}
