const api = 'http://localhost:8080';
let authHeader = '';
let loggedInUser = '';
let loggedInId = null;

const info = document.getElementById('info');
const carsDiv = document.getElementById('cars');
const carList = document.getElementById('carList');
const adminPanel = document.getElementById('adminPanel');
const userList = document.getElementById('userList');

// Rejestracja użytkownika
document.getElementById('regForm').addEventListener('submit', async e => {
  e.preventDefault();
  info.textContent = '';
  const body = {
    username: regUser.value,
    email: regMail.value,
    password: regPass.value
  };
  try {
    const r = await fetch(api + '/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    info.textContent = r.ok ? 'Zarejestrowano – możesz się zalogować' : 'Błąd rejestracji';
  } catch (err) {
    info.textContent = 'Błąd sieci przy rejestracji';
  }
});

// Logowanie użytkownika
document.getElementById('logForm').addEventListener('submit', async e => {
  e.preventDefault();
  info.textContent = '';
  const u = logUser.value, p = logPass.value;

  try {
    const res = await fetch(`${api}/auth/login?username=${encodeURIComponent(u)}&password=${encodeURIComponent(p)}`, {
      method: 'POST',
      credentials: 'include'
    });

    if (!res.ok) {
      info.textContent = 'Błędne dane logowania';
      return;
    }

    authHeader = 'Basic ' + btoa(u + ':' + p);
    loggedInUser = u;
    info.textContent = 'Zalogowano jako ' + u;

    fetchMyUserData();
  } catch (err) {
    info.textContent = 'Błąd sieci przy logowaniu';
  }
});

// Pobieranie danych zalogowanego użytkownika
async function fetchMyUserData() {
  try {
    const res = await fetch(`${api}/users/me`, {
      method: 'GET',
      headers: { 'Authorization': authHeader }
    });
    if (!res.ok) return;

    const me = await res.json();
    loggedInId = me.id;

    if (me.role === 'MECHANIK') {
      document.getElementById('mechanicPanel').classList.remove('hidden');
    }

    if (me.role === 'ADMIN') {
      adminPanel.classList.remove('hidden');
      loadUsers();
      loadCars();
    }

    if (me.role === 'KLIENT') {
      loadCars();
    }
  } catch (err) {
    info.textContent = 'Błąd pobierania danych użytkownika';
  }
}

// Pobieranie samochodów użytkownika
async function loadCars() {
  try {
    const r = await fetch(api + '/cars/my', {
      method: 'GET',
      headers: { 'Authorization': authHeader },
      credentials: 'include'
    });
    if (!r.ok) return;

    const cars = await r.json();
    carList.innerHTML = '';
    cars.forEach(c => {
      const li = document.createElement('li');
      li.textContent = `${c.marka} ${c.model} (${c.rokProdukcji}) – ${c.numerRejestracyjny}`;
      carList.appendChild(li);
    });
    carsDiv.classList.remove('hidden');
  } catch (err) {
    info.textContent = 'Błąd ładowania samochodów';
  }
}

// Pobieranie użytkowników (admin)
async function loadUsers() {
  try {
    const r = await fetch(api + '/users', {
      method: 'GET',
      headers: { 'Authorization': authHeader },
      credentials: 'include'
    });
    if (!r.ok) return;

    const users = await r.json();
    userList.innerHTML = '';
    users.forEach(u => {
      const li = document.createElement('li');
      li.innerHTML = `
        <strong>${u.username}</strong> (${u.role})
        <button class="deleteUser">Usuń użytkownika</button>
      `;
      li.querySelector('.deleteUser').addEventListener('click', async () => {
        if (!confirm(`Na pewno usunąć ${u.username}?`)) return;
        const resp = await fetch(`${api}/users/${u.id}`, {
          method: 'DELETE',
          headers: { 'Authorization': authHeader }
        });
        info.textContent = resp.ok
          ? `Użytkownik ${u.username} został usunięty.`
          : `Błąd przy usuwaniu ${u.username}`;
        loadUsers();
      });
      userList.appendChild(li);
    });
  } catch (err) {
    info.textContent = 'Błąd ładowania użytkowników';
  }
}

// Pobieranie napraw (mechanik)
document.getElementById('loadRepairsBtn').addEventListener('click', async () => {
  if (!loggedInId) {
    info.textContent = 'Nieznany użytkownik.';
    return;
  }
  try {
    const r = await fetch(`${api}/repairs/mechanic/${loggedInId}`, {
      method: 'GET',
      headers: { 'Authorization': authHeader },
      credentials: 'include'
    });
    if (!r.ok) {
      info.textContent = 'Nie udało się pobrać napraw.';
      return;
    }

    const repairs = await r.json();
    const list = document.getElementById('repairList');
    list.innerHTML = '';
    repairs.forEach(rep => {
      const li = document.createElement('li');
      li.textContent = `#${rep.id}: ${rep.opis} [${rep.status}]`;
      list.appendChild(li);
    });
  } catch (err) {
    info.textContent = 'Błąd ładowania napraw';
  }
});
