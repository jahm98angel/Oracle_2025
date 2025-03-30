# Imagen base con Java 11
FROM openjdk:11-jdk-slim

# Actualizar repositorios e instalar dependencias necesarias para GUI
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libfreetype6 \
    libfontconfig1 \
    x11-apps \
    && rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Crear usuario no privilegiado para ejecutar la aplicación
RUN groupadd -r javauser && useradd -r -g javauser javauser
RUN mkdir -p /home/javauser && chown -R javauser:javauser /home/javauser
RUN chown -R javauser:javauser /app

# Copiar el archivo fuente Java
COPY AESKeyExpansionGUI.java /app/

# Compilar la aplicación
RUN javac AESKeyExpansionGUI.java

# Cambiar al usuario no privilegiado
USER javauser

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "AESKeyExpansionGUI"]
