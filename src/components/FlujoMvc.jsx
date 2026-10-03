import React, {useEffect, useRef} from 'react';
import useBaseUrl from '@docusaurus/useBaseUrl';

export default function FlujoMvc({compact = false}) {
  const ref = useRef(null);
  const src = useBaseUrl('/diagramas/flujo-mvc.html') + (compact ? '?compact=1' : '');

  useEffect(() => {
    const onMsg = (e) => {
      if (e.data?.type === 'flujo-mvc-height' && ref.current) {
        ref.current.style.height = e.data.height + 'px';
      }
    };
    window.addEventListener('message', onMsg);
    return () => window.removeEventListener('message', onMsg);
  }, []);

  return (
    <iframe
      ref={ref}
      src={src}
      title="Flujo MVC de Spring Boot"
      style={{width: '100%', height: 1100, border: 0}}
      loading="lazy"
    />
  );
}
