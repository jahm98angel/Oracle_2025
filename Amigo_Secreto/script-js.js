// Array para almacenar los amigos
const friends = [];

// Función para agregar un amigo a la lista
function addFriend() {
    const input = document.getElementById('friendName');
    const error = document.getElementById('error');
    const name = input.value.trim();

    // Validar que el campo no esté vacío
    if (name === '') {
        error.style.display = 'block';
        return;
    }

    // Ocultar mensaje de error si existe
    error.style.display = 'none';
    
    // Agregar el nombre al array
    friends.push(name);
    
    // Limpiar el campo de entrada
    input.value = '';
    
    // Actualizar la lista visual
    updateFriendList();
}

// Función para actualizar la lista visual de amigos
function updateFriendList() {
    const list = document.getElementById('friendList');
    list.innerHTML = '';
    
    // Crear un elemento para cada amigo
    friends.forEach(friend => {
        const div = document.createElement('div');
        div.className = 'friend-item';
        div.textContent = friend;
        list.appendChild(div);
    });
}

// Función para realizar el sorteo
function drawFriend() {
    // Validar que haya amigos en la lista
    if (friends.length === 0) {
        alert('Agrega al menos un amigo a la lista');
        return;
    }

    // Seleccionar un amigo aleatorio
    const result = document.getElementById('result');
    const randomIndex = Math.floor(Math.random() * friends.length);
    const selectedFriend = friends[randomIndex];
    
    // Mostrar el resultado
    result.textContent = `¡${selectedFriend} es el amigo secreto!`;
    result.style.display = 'block';
}
