import React, { JSX, useEffect, useState } from 'react';
import styles from './styles.module.css';
import { versions } from './changelogs';

type ChangeType = 'nuevo' | 'mejora' | 'corrección';

interface Change {
  type: ChangeType;
  text: string;
}

const SECTION_TO_TYPE: Record<string, ChangeType> = {
  'nuevo': 'nuevo',
  'mejora': 'mejora',
  'corrección': 'corrección',
  'correccion': 'corrección',
};

function compareVersions(a: string, b: string): number {
  const pa = a.split('.').map(Number);
  const pb = b.split('.').map(Number);
  for (let i = 0; i < 3; i++) {
    if (pa[i] !== pb[i]) return pa[i] - pb[i];
  }
  return 0;
}

function getLatestVersion(): string | null {
  if (versions.length === 0) return null;
  return [...versions].sort((a, b) => compareVersions(b, a))[0];
}

function parseChanges(raw: string): Change[] {
  const changes: Change[] = [];
  let currentType: ChangeType = 'nuevo';

  for (const line of raw.split('\n')) {
    const sectionMatch = line.match(/^###\s+(.+)/);
    if (sectionMatch) {
      const key = sectionMatch[1].trim().toLowerCase();
      currentType = SECTION_TO_TYPE[key] ?? 'nuevo';
      continue;
    }
    const itemMatch = line.match(/^-\s+(.+)/);
    if (itemMatch) {
      changes.push({ type: currentType, text: itemMatch[1].trim() });
    }
  }

  return changes;
}

// Renders plain text with inline markdown links [label](url) as <a> elements.
function renderText(text: string): React.ReactNode {
  const parts = text.split(/\[([^\]]+)\]\(([^)]+)\)/);
  if (parts.length === 1) return text;
  return parts.map((part, i) => {
    if (i % 3 === 1) return null; // label — used in next iteration
    if (i % 3 === 2) {
      const label = parts[i - 1];
      return <a key={i} href={part} className={styles.changeLink}>{label}</a>;
    }
    return part;
  });
}

const TYPE_LABELS: Record<ChangeType, string> = {
  'nuevo': '✨ Nuevo',
  'mejora': '⚡ Mejora',
  'corrección': '🐛 Corrección',
};

const TYPE_CLASS: Record<ChangeType, string> = {
  'nuevo': styles.tagNew,
  'mejora': styles.tagImprove,
  'corrección': styles.tagFix,
};

const CURRENT_VERSION = getLatestVersion();

export default function WhatsNewModal(): JSX.Element | null {
  const [changes, setChanges] = useState<Change[] | null>(null);

  useEffect(() => {
    if (!CURRENT_VERSION) return;

    const storageKey = `whats-new-seen-v${CURRENT_VERSION}`;
    const seen = localStorage.getItem(storageKey);
    if (seen) return;

    fetch(`/changelogs/${CURRENT_VERSION}.md`)
      .then((res) => res.text())
      .then((raw) => setChanges(parseChanges(raw)))
      .catch(() => setChanges([]));
  }, []);

  function handleDismiss() {
    if (CURRENT_VERSION) {
      localStorage.setItem(`whats-new-seen-v${CURRENT_VERSION}`, '1');
    }
    setChanges(null);
  }

  if (!CURRENT_VERSION || !changes || changes.length === 0) return null;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <div className={styles.header}>
          <span className={styles.icon}>🆕</span>
          <h2 className={styles.title}>Novidades da versión {CURRENT_VERSION}</h2>
        </div>
        <ul className={styles.changeList}>
          {changes.map((change, index) => (
            <li key={index} className={styles.changeItem}>
              <span className={`${styles.tag} ${TYPE_CLASS[change.type]}`}>
                {TYPE_LABELS[change.type]}
              </span>
              <span className={styles.changeText}>{renderText(change.text)}</span>
            </li>
          ))}
        </ul>
        <button className={styles.button} onClick={handleDismiss}>
          Entendido!
        </button>
      </div>
    </div>
  );
}
