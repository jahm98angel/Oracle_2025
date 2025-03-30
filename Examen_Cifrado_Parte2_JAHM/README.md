# Visualizador de Expansión de Claves y Cifrado AES-128

## Descripción General

Esta segunda parte del exámen implementada con Java proporciona una visualización interactiva del algoritmo de cifrado AES-128, centrándose tanto en la expansión de claves como en el proceso de cifrado. Desarrollada con componentes GUI Swing, permite a los usuarios observar cada paso de transformación del algoritmo según el estándar FIPS-197.

## Requisitos Técnicos

- Java 11 o superior
- Docker (para ejecución en contenedor)
- Servidor X11 (para visualización GUI)

## Inicio Rápido con Docker

### Construcción de la Imagen Docker

Clonar el repositorio y construye la imagen Docker:

```bash
# Clonar el repositorio (o descargar los archivos fuente)
git clone <https://github.com/JoseAngelHernandezMorales/Criptografia-y-Seguridad.git>

# Navegar al directorio del proyecto
cd <Examen_Cifrado_Parte2_JAHM>

# Construir la imagen Docker
docker build -t aes-java:cifrado .

# Verificar la imagen Docker
docker images

# Permitir que Docker acceda a tu servidor X
xhost +local:docker

# Ejecutar el contenedor con reenvío X11
docker run --rm --net=host -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix aes-java:cifrado
```

## Ejecución Sin Docker

Si prefieres ejecutar la aplicación directamente:

```bash
# Compilar el código fuente Java
javac AESKeyExpansionGUI.java

# Ejecutar la aplicación
java AESKeyExpansionGUI
```

## Características de la Aplicación

El visualizador incluye:

- Proceso interactivo de expansión de claves
- Visualización paso a paso del cifrado
- Matrices de estado con códigos de colores
- Vectores de prueba predeterminados de FIPS-197
- Generación de claves aleatorias


## Uso de la Aplicación

1. **Entrada**: Ingresa una clave de 128 bits y un texto plano en formato hexadecimal (32 caracteres cada uno)
2. **Expansión de Clave**: Haz clic en "Process Key Expansion" para visualizar la generación del calendario de claves
3. **Cifrado**: Haz clic en "Encrypt" para ver el proceso completo de cifrado
4. **Clave Aleatoria**: Genera una clave aleatoria de 128 bits con el botón "Random Key"


La aplicación viene precargada con los vectores de prueba FIPS-197:

- Clave: `2b7e151628aed2a6abf7158809cf4f3c`
- Texto plano: `3243f6a8885a308d313198a2e0370734`


## Detalles de Implementación

La aplicación implementa:

- Transformación SubBytes mediante búsqueda en S-box
- Operación ShiftRows para difusión
- MixColumns para mezclar columnas de estado
- AddRoundKey para combinar claves de ronda con el estado
- Algoritmo completo de expansión de claves


Cada transformación se representa visualmente con matrices codificadas por colores para mejorar la comprensión del funcionamiento interno del algoritmo.
