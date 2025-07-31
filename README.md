# Tests Secure API

API REST securizat cu Spring Boot și MySQL pentru gestionarea utilizatorilor.

## Funcționalități

- ✅ Autentificare JWT
- ✅ Înregistrare utilizatori
- ✅ Gestionare profile
- ✅ Căutare și filtrare
- ✅ Validări și gestionare erori
- ✅ Statistici și dashboard

## Endpoints Principale

### Autentificare
- `POST /auth/signin` - Login
- `POST /auth/signup` - Înregistrare
- `POST /auth/signout` - Logout

### Utilizatori
- `GET /users/me` - Profilul curent
- `GET /users` - Listare utilizatori
- `GET /users/{id}` - Utilizator specific
- `GET /users/search?name={name}` - Căutare

### Statistici
- `GET /stats/dashboard` - Dashboard cu statistici

## Pornirea Aplicației

1. Configurați MySQL și creați baza `tests_app`
2. Actualizați `application.yml` cu credențialele MySQL
3. Rulați: `mvn spring-boot:run`
4. API disponibil la: `http://localhost:8080`