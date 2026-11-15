# Repeticijų kambarių rezervavimo REST API

Baigiamasis „Web services“ modulio projektas. REST API, per kurį muzikos grupės rezervuoja repeticijų kambarius, o administratorius tvarko kambarius.

Autorė: Ieva Mazolevska, VIKO EIF, programų sistemų studijų programa.

## Funkcijos

- Kambarių registravimas, peržiūra, keitimas ir šalinimas.
- Grupių registravimas, peržiūra, keitimas ir šalinimas.
- Kambario rezervavimas 1–3 valandoms, kaina apskaičiuojama automatiškai.
- Kambario užimtumo peržiūra: rodomos tik aktyvios rezervacijos.
- Rezervacijos atšaukimas ne vėliau kaip prieš 24 valandas.

## Verslo taisyklės

1. Pabaigos laikas turi būti vėlesnis už pradžios laiką.
2. Rezervacija trunka sveiką valandų skaičių, nuo 1 iki 3.
3. Kambario rezervacijos negali persidengti. Gretimos rezervacijos (18:00–20:00 ir 20:00–22:00) leidžiamos.
4. Grupės narių skaičius negali viršyti kambario talpos.
5. Negalima rezervuoti praeityje.
6. Atšaukti galima ne vėliau kaip prieš 24 valandas iki pradžios.
7. Kaina = valandų skaičius × kambario valandos kaina.
8. Pašalinus kambarį ar grupę, pašalinamos ir jų rezervacijos.

## Technologijos

| Sritis | Technologija |
| --- | --- |
| Kalba | Java 25 |
| Karkasas | Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Spring HATEOAS, Validation) |
| Duomenų bazė | H2 (atmintyje) |
| DB schemos valdymas | Liquibase (XML) |
| API dokumentacija | springdoc-openapi (Swagger UI) |
| Unit testai | JUnit, Mockito, MockMvc |
| End-to-end testai | Cucumber, RestAssured |
| Rankinis testavimas | Postman |
| Kūrimo įrankiai | Maven, IntelliJ IDEA, Git, GitHub |

## Paleidimas

Reikia Java 25. Maven atskirai diegti nereikia, nes projekte yra Maven Wrapper.

```bash
git clone https://github.com/strimelle/rehearsal-booking.git
cd rehearsal-booking
./mvnw spring-boot:run
```

Windows komandinėje eilutėje vietoj `./mvnw` rašyk `mvnw.cmd`.

Programa pasileidžia adresu `http://localhost:8080`. Paleidžiant Liquibase sukuria lenteles ir įrašo pradinius duomenis: 2 kambarius ir 2 grupes.

| Kas | Adresas |
| --- | --- |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI aprašas (JSON) | http://localhost:8080/v3/api-docs |
| H2 konsolė | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:rehearsaldb`, vartotojas `sa`, slaptažodis tuščias) |

## API

| Metodas | Adresas | Aprašymas | Sėkmės kodas |
| --- | --- | --- | --- |
| GET | `/rooms` | Visi kambariai | 200 |
| GET | `/rooms/{id}` | Vienas kambarys | 200 |
| GET | `/rooms/{id}/bookings` | Aktyvios kambario rezervacijos | 200 |
| POST | `/rooms` | Naujas kambarys | 201 |
| PUT | `/rooms/{id}` | Kambario keitimas | 200 |
| DELETE | `/rooms/{id}` | Kambario ir jo rezervacijų šalinimas | 204 |
| GET | `/bands` | Visos grupės | 200 |
| GET | `/bands/{id}` | Viena grupė | 200 |
| POST | `/bands` | Nauja grupė | 201 |
| PUT | `/bands/{id}` | Grupės keitimas | 200 |
| DELETE | `/bands/{id}` | Grupės ir jos rezervacijų šalinimas | 204 |
| GET | `/bookings` | Visos rezervacijos | 200 |
| GET | `/bookings/{id}` | Viena rezervacija | 200 |
| POST | `/bookings` | Nauja rezervacija | 201 |
| POST | `/bookings/{id}/cancel` | Rezervacijos atšaukimas | 200 |

Klaidos grąžinamos vienodu formatu:

```json
{
  "timestamp": "2026-12-01T18:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Room is already booked at this time",
  "path": "/bookings"
}
```

Kodai: 400 – neteisingi duomenys arba pažeista verslo taisyklė, 404 – resursas nerastas, 409 – konfliktas su esama būsena.

### Užklausos pavyzdys

```http
POST /bookings
Content-Type: application/json

{
  "bandId": 1,
  "roomId": 1,
  "startTime": "2026-12-01T18:00:00",
  "endTime": "2026-12-01T20:00:00"
}
```

Atsakymas `201 Created` su HATEOAS nuorodomis: `self`, `bookings`, `band`, `room` ir `cancel`. Nuoroda `cancel` rodoma tik aktyviai rezervacijai.

## REST principai

- **Richardson 3 lygis (HATEOAS):** kiekviename atsakyme yra nuorodos į susijusius resursus.
- **Stateless:** serveris nesaugo sesijų, kiekviena užklausa nepriklauso nuo kitų.
- **Cacheable:** GET atsakymai turi `ETag` ir `Cache-Control` antraštes. Pakartotinė užklausa su `If-None-Match` gauna `304 Not Modified`.

## Testai

Visi testai paleidžiami viena komanda:

```bash
./mvnw test
```

- **Unit testai** (`../../../Downloads/part13-files/src/test/java`): entity, service, mapper, controller, exception handler ir config klasės.
- **Cucumber end-to-end testai:** 8 vartotojų istorijos, 30 scenarijų (`src/test/resources/features`). Ataskaita: [docs/cucumber-report.html](docs/cucumber-report.html).
- **Postman kolekcija:** [docs/collection.json](docs/collection.json). Importuok į Postman ir paleisk visą kolekciją.

## JavaDoc

```bash
./mvnw javadoc:javadoc
```

Dokumentacija sugeneruojama į `target/reports/apidocs/index.html`.

## Dokumentacija

| Failas | Turinys |
| --- | --- |
| [docs/requirements.md](../../../Downloads/part13-files/docs/requirements.md) | Funkciniai ir nefunkciniai reikalavimai |
| [docs/user-stories.md](docs/user-stories.md) | Vartotojų istorijos su priėmimo kriterijais |
| [docs/openapi.yaml](docs/openapi.yaml) | OpenAPI specifikacija |
| [docs/collection.json](docs/collection.json) | Postman kolekcija |
| [docs/cucumber-report.html](docs/cucumber-report.html) | Cucumber testų ataskaita |

## Projekto struktūra

```
src/main/java/lt/viko/eif/imazolevska/rehearsalbooking
├── config       – ETag, Cache-Control, CORS ir Swagger nustatymai
├── controller   – REST endpoint'ai
├── dto          – užklausų ir atsakymų objektai
├── entity       – JPA esybės: Room, Band, Booking
├── exception    – klaidų klasės ir bendras klaidų apdorojimas
├── mapper       – esybių vertimas į atsakymus su HATEOAS nuorodomis
├── repository   – Spring Data JPA repository
└── service      – verslo logika ir taisyklės
```

## Darbo eiga

Kiekviena funkcija kurta atskiroje `feature/...` šakoje ir sujungta į `master` per Pull Request.
