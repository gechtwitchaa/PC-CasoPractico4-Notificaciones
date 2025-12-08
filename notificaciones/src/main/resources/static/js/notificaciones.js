function init(usuario) {
    const cuerpo = document.getElementById('cuerpo');
    const filtroSelect = document.getElementById('filtroTipo');

    // Función para agregar fila en la tabla
    function agregarFila(n) {
        // Si existe filtro y no coincide, no añadir
        const filtro = filtroSelect ? filtroSelect.value : "";
        if (filtro && n.tipo !== filtro) return;

        const tr = document.createElement('tr');
        if (!n.leido) tr.classList.add('no-leido');
        if (n.tipo === 'URGENTE') tr.classList.add('urgente');

        const fecha = n.fecha ? new Date(n.fecha).toLocaleString() : '';

        tr.innerHTML = `
      <td>${escapeHtml(n.mensaje)}</td>
      <td>${n.tipo}</td>
      <td>${fecha}</td>
      <td>${n.leido ? 'Sí' : 'No'}</td>
      <td>
        ${n.leido ? '' : `<button onclick="marcarLeido('${n.id}', this)">Marcar Leído</button>`}
        <button onclick="eliminar('${n.id}', this)">Eliminar</button>
      </td>
    `;
        // insertar arriba (más reciente arriba)
        cuerpo.insertBefore(tr, cuerpo.firstChild);
    }

    // Conectar al SSE
    const evtSource = new EventSource(`/api/notificaciones/stream/${usuario}`);
    evtSource.onmessage = function(e) {
        // e.data es JSON
        try {
            const n = JSON.parse(e.data);
            agregarFila(n);
        } catch (err) {
            console.error('Error al parsear SSE', err);
        }
    };
    evtSource.addEventListener('notificacion', function(e){
        // Si usas ServerSentEvent con event "notificacion", llega aquí
        try {
            const n = JSON.parse(e.data);
            agregarFila(n);
        } catch(e) {}
    });

    evtSource.onerror = function(err) {
        console.error('SSE error', err);
        // no recargamos ni hacemos nada; EventSource intentará reconectar.
    };

    // Exponer funciones globales para botones
    window.marcarLeido = function(id, btn) {
        fetch(`/api/notificaciones/leer/${id}`, { method: 'PUT' })
            .then(res => res.json())
            .then(updated => {
                // actualizar fila: encontrar el botón y la fila
                const row = btn.closest('tr');
                row.classList.remove('no-leido');
                row.querySelector('td:nth-child(4)').innerText = 'Sí';
                btn.remove(); // eliminar botón "Marcar Leído"
            });
    }

    window.eliminar = function(id, btn) {
        fetch(`/api/notificaciones/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    const row = btn.closest('tr');
                    row.remove();
                }
            });
    }

    window.aplicarFiltro = function() {
        // limpiar tabla y recargar desde endpoint de filtrado
        const tipo = filtroSelect.value;
        fetch(tipo ? `/api/notificaciones/filtrar/${usuario}/${tipo}` : `/api/notificaciones/usuario/${usuario}`)
            .then(r => r.json())
            .then(list => {
                cuerpo.innerHTML = '';
                list.forEach(n => agregarFila(n));
            });
    }

    // Cargar notificaciones existentes al cargar la página (simple fetch)
    fetch(`/api/notificaciones/usuario/${usuario}`)
        .then(r => r.json())
        .then(list => {
            cuerpo.innerHTML = '';
            list.forEach(n => agregarFila(n));
        });
}

// función simple para escapar HTML
function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function (m) {
        return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]);
    });
}
