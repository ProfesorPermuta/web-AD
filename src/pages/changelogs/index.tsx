import React, { JSX, useEffect, useRef, useState } from 'react';
import Layout from '@theme/Layout';
import { versions } from '../../components/WhatsNewModal/changelogs';

function compareVersions(a: string, b: string): number {
  const pa = a.split('.').map(Number);
  const pb = b.split('.').map(Number);
  for (let i = 0; i < 3; i++) {
    if (pa[i] !== pb[i]) return pa[i] - pb[i];
  }
  return 0;
}

const sortedVersions = [...versions].sort((a, b) => compareVersions(b, a));

function renderLine(text: string): React.ReactNode {
  const parts = text.split(/\[([^\]]+)\]\(([^)]+)\)/g);
  if (parts.length === 1) return text;
  return parts.map((part, i) => {
    if (i % 3 === 1) return null;
    if (i % 3 === 2) {
      return (
        <a key={i} href={part}>
          {parts[i - 1]}
        </a>
      );
    }
    return part;
  });
}

function ChangelogEntry({ version }: { version: string }): JSX.Element {
  const [content, setContent] = useState<string | null>(null);

  useEffect(() => {
    fetch(`/changelogs/${version}.md`)
      .then((res) => res.text())
      .then(setContent)
      .catch(() => setContent(''));
  }, [version]);

  const lines = content?.split('\n') ?? [];

  return (
    <section id={`v${version}`} style={{ marginBottom: '2.5rem', scrollMarginTop: '80px' }}>
      <h2>v{version}</h2>
      {content === null ? (
        <p>Cargando...</p>
      ) : (
        <ul>
          {lines
            .filter((line) => line.startsWith('- '))
            .map((line, i) => (
              <li key={i}>{renderLine(line.slice(2).trim())}</li>
            ))}
        </ul>
      )}
    </section>
  );
}

function VersionNav({ versions: vs }: { versions: string[] }): JSX.Element {
  const [activeVersion, setActiveVersion] = useState<string>(vs[0] ?? '');
  const observerRef = useRef<IntersectionObserver | null>(null);

  useEffect(() => {
    observerRef.current?.disconnect();
    const entries = new Map<string, number>();

    observerRef.current = new IntersectionObserver(
      (observed) => {
        observed.forEach((entry) => {
          entries.set(entry.target.id, entry.intersectionRatio);
        });
        let best = '';
        let bestRatio = -1;
        entries.forEach((ratio, id) => {
          if (ratio > bestRatio) {
            bestRatio = ratio;
            best = id;
          }
        });
        if (best) setActiveVersion(best.replace(/^v/, ''));
      },
      { threshold: [0, 0.25, 0.5, 0.75, 1] }
    );

    vs.forEach((v) => {
      const el = document.getElementById(`v${v}`);
      if (el) observerRef.current!.observe(el);
    });

    return () => observerRef.current?.disconnect();
  }, [vs]);

  return (
    <nav
      aria-label="Navegación de versións"
      style={{
        position: 'sticky',
        top: '80px',
        alignSelf: 'flex-start',
        width: '160px',
        flexShrink: 0,
        borderLeft: '2px solid var(--ifm-color-emphasis-200)',
        paddingLeft: '1rem',
      }}
    >
      <p style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem', color: 'var(--ifm-color-emphasis-600)' }}>
        Versións
      </p>
      <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
        {vs.map((v) => (
          <li key={v} style={{ marginBottom: '0.35rem' }}>
            <a
              href={`#v${v}`}
              style={{
                fontSize: '0.875rem',
                textDecoration: 'none',
                color: activeVersion === v ? 'var(--ifm-color-primary)' : 'var(--ifm-color-emphasis-700)',
                fontWeight: activeVersion === v ? 700 : 400,
                transition: 'color 0.2s',
              }}
            >
              v{v}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}

export default function ChangelogsPage(): JSX.Element {
  return (
    <Layout title="Changelogs" description="Historial de cambios da web">
      <div
        style={{
          display: 'flex',
          maxWidth: '1100px',
          margin: '0 auto',
          padding: '2rem 1rem',
          gap: '2rem',
          alignItems: 'flex-start',
        }}
      >
        <main style={{ flex: 1, minWidth: 0 }}>
          <h1>Changelogs</h1>
          <p>Historial de todas as versións e cambios publicados.</p>
          {sortedVersions.length === 0 ? (
            <p>Aínda non hai versións publicadas.</p>
          ) : (
            sortedVersions.map((v) => <ChangelogEntry key={v} version={v} />)
          )}
        </main>
        {sortedVersions.length > 0 && <VersionNav versions={sortedVersions} />}
      </div>
    </Layout>
  );
}
