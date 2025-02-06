# Oracle_2025
Curso de Oracle 2025# Aplicación de Sorteo de Amigo Secreto

## Descripción
Esta aplicación web permite organizar un sorteo de "Amigo Secreto" de manera sencilla y aleatoria. Los usuarios pueden agregar nombres de participantes a una lista y realizar un sorteo aleatorio para seleccionar un amigo secreto.

## Características
- Interfaz de usuario intuitiva y responsiva
- Validación de entrada de datos
- Visualización en tiempo real de la lista de participantes
- Selección aleatoria de amigo secreto
- Diseño moderno y amigable

## Tecnologías Utilizadas

### HTML
- Estructura semántica con elementos HTML5
- Integración de archivos CSS y JavaScript externos
- Formulario para entrada de datos
- Contenedores para visualización dinámica

### CSS
- Diseño responsive con flexbox
- Esquema de colores coherente
- Efectos de hover en botones
- Animaciones suaves de transición
- Estilos para mensajes de error y resultados
- Media queries para adaptación a diferentes dispositivos
- Sombras y bordes redondeados para mejor UX

### JavaScript
- Manipulación del DOM
- Arrays para almacenamiento de datos
- Funciones de validación
- Generación aleatoria para el sorteo
- Event handlers para interacciones de usuario
- Actualización dinámica de la interfaz

## Estructura del Proyecto
```
📁 proyecto-amigo-secreto/
├── 📄 index.html
├── 📄 styles.css
├── 📄 script.js
└── 📄 README.md
```

## Instalación y Ejecución

1. **Clonar o Descargar el Proyecto**
   - Descarga todos los archivos en una carpeta local

2. **Verificar la Estructura**
   - Asegúrate de que todos los archivos estén en la misma carpeta
   - Verifica que los nombres de los archivos coincidan exactamente

3. **Ejecutar la Aplicación**
   - Abre el archivo `index.html` en un navegador web moderno
   - No se requiere servidor web, funciona localmente

## Uso de la Aplicación

1. **Agregar Participantes**
   - Escribe el nombre en el campo de texto
   - Presiona el botón "Adicionar" o la tecla Enter
   - El nombre se agregará a la lista visible

2. **Validaciones**
   - No se permiten nombres vacíos
   - Se muestra mensaje de error si el campo está vacío

3. **Realizar el Sorteo**
   - Una vez agregados todos los participantes
   - Presiona el botón "Sortear Amigo"
   - Se mostrará el resultado del sorteo

## Funcionalidades JavaScript Detalladas

- `addFriend()`: Agrega nuevos participantes a la lista
- `updateFriendList()`: Actualiza la visualización de la lista
- `drawFriend()`: Realiza el sorteo aleatorio
- Validaciones de entrada de datos
- Manejo de errores y alertas
- Actualización dinámica del DOM

## Estilos CSS Implementados

- Contenedor principal: `.container`
- Grupo de entrada: `.input-group`
- Lista de amigos: `.friend-list`
- Elementos individuales: `.friend-item`
- Mensajes de resultado: `.result`
- Mensajes de error: `.error`
- Estilos de botones y efectos hover
- Diseño responsive

## Requisitos del Sistema
- Navegador web moderno (Chrome, Firefox, Safari, Edge)
- JavaScript habilitado
- No requiere conexión a internet

## Notas Adicionales
- La aplicación funciona completamente en el frontend
- Los datos se mantienen solo durante la sesión actual
- No se almacena información en el navegador
- Compatible con todos los dispositivos modernos

## Mejoras Futuras Posibles
1. Almacenamiento local para persistencia de datos
2. Opción para eliminar participantes
3. Historial de sorteos
4. Exportación de resultados
5. Temas visuales personalizables

## Soporte
Para reportar problemas o sugerir mejoras, por favor crear un issue en el repositorio del proyecto.
