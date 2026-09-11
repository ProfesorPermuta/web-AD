import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

import remarkDirective from 'remark-directive';
// El transformador de admoniciones no se exporta desde la raíz del paquete, y
// al ser CommonJS hay que tomar su .default explícitamente.
import admonitionsModule from '@docusaurus/mdx-loader/lib/remark/admonitions/index.js';

const remarkAdmonitions = admonitionsModule.default;
const {DefaultAdmonitionOptions} = admonitionsModule;

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

// Los plugins de docs y blog están desactivados, así que las páginas sueltas de
// src/pages/ no reciben el transformador de admoniciones (:::note, :::tip…) que
// Docusaurus aplica de serie en docs/. Lo añadimos a mano para poder usarlas.
const remarkPlugins = [
  remarkDirective,
  [remarkAdmonitions, DefaultAdmonitionOptions],
];

const config: Config = {
  title: 'Acceso a Datos',
  tagline: 'Apuntes do módulo 0486 — CS DAM',
  favicon: 'img/favicon.ico',

  // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
  future: {
    v4: true, // Improve compatibility with the upcoming Docusaurus v4
  },

  // Set the production url of your site here
  url: 'https://permuta.github.io/',
  // Set the /<baseUrl>/ pathname under which your site is served
  baseUrl: '/',

  organizationName: 'permuta',
  projectName: 'web-acceso-datos',

  onBrokenLinks: 'warn',

  i18n: {
    defaultLocale: 'es',
    locales: ['es'],
  },

  markdown: {
    mermaid: true,
  },
  themes: ['@docusaurus/theme-mermaid'],

  presets: [
    [
      'classic',
      {
        // El contenido vive en src/pages/
        // Los plugins de docs y blog quedan desactivados.
        docs: false,
        blog: false,
        pages: {
          remarkPlugins,
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    image: 'img/docusaurus-social-card.jpg',
    colorMode: {
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'AD',
      logo: {
        alt: 'Logo Acceso a Datos',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'dropdown',
          label: 'Temas',
          position: 'left',
          items: [
            {
              label: 'Tema 0 — Arranque: entorno, Spring Boot y MULTAGAL',
              to: '/tema-0/a-presentacion-modulo',
            },
            {
              label: 'Tema 1 — Introducción a Acceso a Datos',
              to: '/tema-1/a-introduccion',
            },
          ],
        },
        {to: '/tema-1/f-plantilla-proyecto', label: 'Proyecto A', position: 'left'},
        {to: '/changelogs', label: 'Changelogs', position: 'left'},
      ],
    },
    footer: {
      style: 'dark',
      links: [],
      copyright: `Copyright © ${new Date().getFullYear()} — Acceso a Datos. Feito con Docusaurus.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['kotlin', 'java', 'csharp', 'json', 'bash'],
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
