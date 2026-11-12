# Reikalavimai

## Funkciniai reikalavimai

| Kodas | Panaudojimo atvejis | Aprašymas | Naudotojo kategorija | Priklausomybė | Konfliktas | Prioritetas | Būsena | Versija |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| FR-1 | Kambario registravimas | Administratorius įveda kambario pavadinimą, talpą ir valandos kainą. Pavadinimas privalomas ir unikalus, talpa ne mažesnė nei 1, kaina didesnė nei 0. Sėkmės atveju grąžinamas sukurtas kambarys su ID. | Administratorius | Nėra | Nėra | 5 | Planuojama | 1.0 |
| FR-2 | Kambarių peržiūra | Galima gauti visų kambarių sąrašą arba vieno kambario duomenis pagal ID. Jei kambario nėra, grąžinamas pranešimas, kad jis nerastas. | Administratorius, Grupės atstovas | FR-1 | Nėra | 4 | Planuojama | 1.0 |
| FR-3 | Kambario duomenų keitimas | Administratorius gali pakeisti kambario pavadinimą, talpą ir kainą. Taikomos tos pačios taisyklės kaip FR-1. | Administratorius | FR-1 | FR-13: pakeitus kainą, jau sukurtų rezervacijų kaina nesikeičia, nes ji išsaugoma kuriant rezervaciją. | 3 | Planuojama | 1.0 |
| FR-4 | Kambario šalinimas | Administratorius gali pašalinti kambarį. Kartu pašalinamos ir visos to kambario rezervacijos (taisyklė 8). Jei kambario nėra, grąžinamas pranešimas, kad jis nerastas. | Administratorius | FR-1 | FR-1 | 2 | Planuojama | 1.0 |
| FR-5 | Grupės registravimas | Grupės atstovas įveda grupės pavadinimą, kontaktinį el. paštą ir narių skaičių. Pavadinimas privalomas, el. paštas turi būti teisingo formato, narių skaičius ne mažesnis nei 1. | Grupės atstovas | Nėra | Nėra | 5 | Planuojama | 1.0 |
| FR-6 | Grupių peržiūra | Galima gauti visų grupių sąrašą arba vienos grupės duomenis pagal ID. Jei grupės nėra, grąžinamas pranešimas, kad ji nerasta. | Administratorius, Grupės atstovas | FR-5 | Nėra | 4 | Planuojama | 1.0 |
| FR-7 | Grupės duomenų keitimas | Grupės atstovas gali pakeisti grupės pavadinimą, el. paštą ir narių skaičių. Taikomos tos pačios taisyklės kaip FR-5. | Grupės atstovas | FR-5 | Nėra | 3 | Planuojama | 1.0 |
| FR-8 | Grupės šalinimas | Grupė gali būti pašalinta. Kartu pašalinamos ir visos tos grupės rezervacijos (taisyklė 8). Jei grupės nėra, grąžinamas pranešimas, kad ji nerasta. | Grupės atstovas | FR-5 | Nėra | 2 | Planuojama | 1.0 |
| FR-9 | Rezervacijos sukūrimas | Grupės atstovas nurodo grupę, kambarį, pradžios ir pabaigos laiką. Sistema tikrina taisykles 1–5. Jei bent viena pažeista, rezervacija nesukuriama, o atsakyme nurodoma priežastis. Sukurta rezervacija gauna būseną ACTIVE ir apskaičiuotą kainą. | Grupės atstovas | FR-1, FR-5, FR-13, FR-14 | Nėra | 5 | Planuojama | 1.0 |
| FR-10 | Rezervacijų peržiūra | Galima gauti visų rezervacijų sąrašą arba vienos rezervacijos duomenis pagal ID, įskaitant būseną ir kainą. | Administratorius, Grupės atstovas | FR-9 | Nėra | 4 | Planuojama | 1.0 |
| FR-11 | Kambario užimtumo peržiūra | Grupės atstovas gali pamatyti pasirinkto kambario aktyvias rezervacijas, kad žinotų, kuris laikas jau užimtas. | Grupės atstovas | FR-1, FR-9 | Nėra | 4 | Planuojama | 1.0 |
| FR-12 | Rezervacijos atšaukimas | Grupės atstovas gali atšaukti rezervaciją ne vėliau kaip 24 val. iki jos pradžios (taisyklė 6). Atšaukta rezervacija gauna būseną CANCELLED, lieka sistemoje istorijai ir nebeužima kambario laiko. Pakartotinai atšaukti tos pačios rezervacijos negalima. | Grupės atstovas | FR-9 | Nėra | 5 | Planuojama | 1.0 |
| FR-13 | Kainos apskaičiavimas | Kuriant rezervaciją sistema apskaičiuoja kainą: valandų skaičius × kambario valandos kaina (taisyklė 7). Kaina išsaugoma rezervacijoje. | Sistema | FR-1 | Nėra | 4 | Planuojama | 1.0 |
| FR-14 | Laiko persidengimo tikrinimas | Prieš sukuriant rezervaciją sistema tikrina, ar tame kambaryje nėra aktyvios rezervacijos, kurios laikas persidengtų su nauja (taisyklė 3). Jei viena rezervacija baigiasi, o kita prasideda tą pačią minutę, jos nepersidengia. Atšauktos rezervacijos neįskaitomos. | Sistema | FR-1 | Nėra | 5 | Planuojama | 1.0 |

## Nefunkciniai reikalavimai

# Reikalavimai

## Funkciniai reikalavimai

| Nr. | Įvykis / Panaudojimo atvejis | Aprašymas | Šaltinis | Priklausomybė | Konfliktas | Papildoma medžiaga | Istorija |
| --- | --- | --- | --- | --- | --- | --- | --- |
| NFR-1 | REST architektūra (Richardson 3 lygis) | API sudaro resursai /rooms, /bands, /bookings. HTTP metodai naudojami pagal paskirtį (GET, POST, PUT, DELETE), atsakymai grąžina teisingus HTTP kodus. Kiekviename atsakyme yra HATEOAS nuorodos: bent self ir nuorodos į susijusius resursus. | Užduoties aprašas | Nėra | Nėra | Richardson Maturity Model | - |
| NFR-2 | Būsenos nesaugojimas (stateless) | Serveris nesaugo kliento sesijos. Kiekvienoje užklausoje yra visa jai įvykdyti reikalinga informacija, todėl užklausos gali būti vykdomos bet kuria tvarka. | Užduoties aprašas | Nėra | Nėra | Nėra | - |
| NFR-3 | Podėliavimas (cacheable) | GET atsakymai turi Cache-Control ir ETag antraštes. Jei klientas atsiunčia If-None-Match su nepasikeitusiu ETag, serveris grąžina 304 Not Modified be turinio. | Užduoties aprašas | NFR-2 | Nėra | RFC 9111 (HTTP Caching) | - |
| NFR-4 | Duomenų formatas | Užklausos ir atsakymai siunčiami JSON formatu (HATEOAS atsakymai – application/hal+json). Datos ir laikai rašomi ISO 8601 formatu, pvz., 2026-12-01T18:00:00, Europe/Vilnius laiku. | Sistemos kūrėjas | Nėra | Nėra | Nėra | - |
| NFR-5 | Vienodas klaidų formatas | Visos klaidos grąžinamos tuo pačiu JSON formatu: laikas, HTTP kodas, klaidos pavadinimas, pranešimas ir užklausos kelias. Kodai: 400 – neteisingi duomenys (taisyklės 1, 2, 4, 5), 404 – resursas nerastas, 409 – konfliktas su esama būsena (taisyklės 3, 6 ir pasikartojantis kambario pavadinimas). | Sistemos kūrėjas | NFR-4 | Nėra | Nėra | - |
| NFR-6 | Įvesties validacija | Visi į API ateinantys duomenys patikrinami prieš patenkant į service sluoksnį. Jei laukas netinkamas, atsakyme nurodoma, kuris laukas ir kodėl. | Sistemos kūrėjas | NFR-5 | Nėra | Jakarta Bean Validation | - |
| NFR-7 | DB schemos valdymas | Lentelės ir pradiniai duomenys kuriami tik per Liquibase XML changelog'us. Hibernate automatinis lentelių kūrimas išjungtas. Kiekvienas schemos pakeitimas yra naujas changeSet. | Sistemos kūrėjas | Nėra | Nėra | Liquibase dokumentacija | - |
| NFR-8 | API dokumentacija | API aprašytas OpenAPI 3 specifikacija, generuojama iš kodo. Swagger UI pasiekiamas adresu /swagger-ui.html, iš jo galima siųsti užklausas. | Užduoties aprašas | NFR-1 | Nėra | OpenAPI Specification | - |
| NFR-9 | Testuojamumas | Kiekviena klasė, turinti logiką, padengta unit testais (JUnit 5, Mockito). Kiekviena vartotojo istorija turi bent vieną automatinį Cucumber end-to-end scenarijų. Visi testai paleidžiami komanda mvn test. | Užduoties aprašas | Nėra | Nėra | Cucumber Java | - |
| NFR-10 | Kodo kokybė | Kodas vienodai suformatuotas, pavadinimai atitinka Java konvencijas, viešos klasės ir metodai turi JavaDoc komentarus, iš kurių generuojama JavaDoc dokumentacija. Kodas atitinka SOLID principus. | Užduoties aprašas | Nėra | Nėra | Nėra | - |
| NFR-11 | Našumas | Tipinės užklausos atsakymo laikas lokalioje aplinkoje neviršija 500 ms. Tikrinama Postman įrankiu. | Sistemos kūrėjas | Nėra | Nėra | Nėra | - |
| NFR-12 | Versijų kontrolė | Kodas saugomas GitHub. Kiekviena funkcija kuriama atskiroje šakoje ir sujungiama į master per Pull Request. | Užduoties aprašas | Nėra | Nėra | Nėra | - |