// import type {ReactNode} from 'react';
// import clsx from 'clsx';
// import Link from '@docusaurus/Link';
// import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
// import Layout from '@theme/Layout';
// import Heading from '@theme/Heading';

// import styles from './index.module.css';

// type Tema = {
//   title: string;
//   path: string;
//   items: {label: string; to: string}[];
// };

// // ─── ÍNDICE DE CONTENIDOS ─────────────────────────────────────────────────────
// // Añade aquí cada tema conforme se vaya creando el contenido en src/pages/tema-N/.
// // Recuerda replicar la entrada correspondiente en el dropdown "Temas"
// // de docusaurus.config.ts.
// //
// //   {
// //     title: 'Tema 0 — Título del tema',
// //     path: '/tema-0',
// //     items: [
// //       { label: 'Introducción', to: '/tema-0/a-introduccion' },
// //     ],
// //   },
// // ─────────────────────────────────────────────────────────────────────────────
// const TEMAS: Tema[] = [
//   {
//     title: 'Tema 0 — Arranque: entorno, Spring Boot y el proyecto MULTAGAL',
//     path: '/tema-0',
//     items: [
//       {label: 'Presentación del módulo', to: '/tema-0/a-presentacion-modulo'},
//       {label: 'El proyecto MULTAGAL', to: '/tema-0/b-proyecto-multagal'},
//       {label: 'Entorno de desarrollo', to: '/tema-0/c-entorno-desarrollo'},
//       {label: 'Docker y servicios de datos', to: '/tema-0/d-docker-servicios-datos'},
//       {label: 'Anatomía de un proyecto Spring Boot', to: '/tema-0/e-anatomia-spring-boot'},
//       {label: 'Inyección de dependencias', to: '/tema-0/f-inyeccion-dependencias'},
//       {label: 'Flujo de trabajo con Git', to: '/tema-0/g-flujo-git'},
//       {label: 'Actividades', to: '/tema-0/h-actividades'},
//     ],
//   }
// ];
import type {ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Heading from '@theme/Heading';

import styles from './index.module.css';

/* ─── Datos del curso ───────────────────────────────────────── */
type TemaPage = {label: string; href: string};
type Tema = {n: number; title: string; color: string; pages: TemaPage[]};

// ─── ÍNDICE DE CONTENIDOS ─────────────────────────────────────────────────────
// Añade aquí cada tema conforme se vaya creando el contenido en src/pages/tema-N/.
// Recuerda replicar la entrada correspondiente en el dropdown "Temas"
// de docusaurus.config.ts.
// ─────────────────────────────────────────────────────────────────────────────
const TEMAS: Tema[] = [
  {
    n: 0,
    title: 'Arranque: entorno, Spring Boot y el proyecto MULTAGAL',
    color: '#f59e0b',
    pages: [
     {label: 'Presentación del módulo', href: '/tema-0/a-presentacion-modulo'},
      {label: 'El proyecto MULTAGAL', href: '/tema-0/b-proyecto-multagal'},
      {label: 'Entorno de desarrollo', href: '/tema-0/c-entorno-desarrollo'},
      {label: 'Docker y servicios de datos', href: '/tema-0/d-docker-servicios-datos'},
      {label: 'Anatomía de un proyecto Spring Boot', href: '/tema-0/e-anatomia-spring-boot'},
      {label: 'Inyección de dependencias', href: '/tema-0/f-inyeccion-dependencias'},
      {label: 'Flujo de trabajo con Git', href: '/tema-0/g-flujo-git'},
      {label: 'Actividades', href: '/tema-0/h-actividades'}
    ],
  },
];

/* ─── Tarjeta de tema ───────────────────────────────────────── */
function TemaCard({tema}: {tema: Tema}) {
  return (
    <div className={styles.temaCard} style={{borderTopColor: tema.color}}>
      <div className={styles.temaCardHeader}>
        <span className={styles.temaNumber} style={{background: tema.color}}>
          T{tema.n}
        </span>
        <Heading as="h3" className={styles.temaTitle}>
          <Link to={tema.pages[0].href} style={{color: tema.color}}>
            {tema.title}
          </Link>
        </Heading>
      </div>
      <ul className={styles.temaPageList}>
        {tema.pages.map((p) => (
          <li key={p.href}>
            <Link to={p.href}>{p.label}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

/* ─── Hero ──────────────────────────────────────────────────── */
function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
      </div>
    </header>
  );
}

/* ─── Página principal ──────────────────────────────────────── */
export default function Home(): ReactNode {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout title={siteConfig.title} description={siteConfig.tagline}>
      <HomepageHeader />
      <main>
        <section className={styles.temasSection}>
          <div className="container">
            <Heading as="h2" className={styles.sectionTitle}>
              Índice de contenidos
            </Heading>
            {TEMAS.length === 0 ? (
              <p>Aún no hay contenido publicado.</p>
            ) : (
              <div className={styles.temasGrid}>
                {TEMAS.map((tema) => (
                  <TemaCard key={tema.n} tema={tema} />
                ))}
              </div>
            )}
          </div>
        </section>
      </main>
    </Layout>
  );
}
