# Vartotojų istorijos

Priėmimo kriterijai parašyti Gherkin formatu: raktiniai žodžiai angliškai (Scenario, Background, Given, When, Then, And), žingsnių tekstas lietuviškai.

## US-1. Kambario registravimas

Susiję reikalavimai: FR-1, FR-2.

```gherkin
Kaip administratorius
Noriu užregistruoti repeticijų kambarį su talpa ir valandos kaina
Kad grupės galėtų jį rezervuoti

Scenario: Sėkmingas kambario registravimas
  Given sistemoje nėra kambario pavadinimu "Studija A"
  When administratorius registruoja kambarį "Studija A", kurio talpa 5 ir valandos kaina 15.00
  Then kambarys sukuriamas ir jam suteikiamas ID
  And kambarį galima rasti pagal šį ID

Scenario: Kambario pavadinimas jau naudojamas
  Given sistemoje yra kambarys "Studija A"
  When administratorius registruoja kambarį "Studija A", kurio talpa 8 ir valandos kaina 20.00
  Then kambarys nesukuriamas
  And gaunamas pranešimas, kad toks pavadinimas jau naudojamas

Scenario: Neteisinga kambario talpa
  When administratorius registruoja kambarį "Studija B", kurio talpa 0 ir valandos kaina 15.00
  Then kambarys nesukuriamas
  And gaunamas pranešimas, kad talpa turi būti ne mažesnė nei 1

Scenario: Kambarys nerastas
  When ieškomas kambarys, kurio ID neegzistuoja
  Then gaunamas pranešimas, kad kambarys nerastas
```

## US-2. Kambario duomenų keitimas

Susiję reikalavimai: FR-3, FR-13.

```gherkin
Kaip administratorius
Noriu pakeisti kambario kainą ir talpą
Kad sistemoje būtų teisinga informacija

Scenario: Kambario kainos pakeitimas
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  When administratorius pakeičia kambario "Studija A" valandos kainą į 20.00
  Then kambario "Studija A" valandos kaina yra 20.00

Scenario: Jau sukurtos rezervacijos kaina nesikeičia
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  And kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00, kurios kaina 30.00
  When administratorius pakeičia kambario "Studija A" valandos kainą į 20.00
  Then tos rezervacijos kaina lieka 30.00

Scenario: Neteisinga nauja kaina
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  When administratorius pakeičia kambario "Studija A" valandos kainą į 0
  Then kambario duomenys nepakeičiami
  And gaunamas pranešimas, kad kaina turi būti didesnė nei 0
```

## US-3. Kambario šalinimas

Susiję reikalavimai: FR-4.

```gherkin
Kaip administratorius
Noriu pašalinti nebenaudojamą kambarį
Kad grupės jo nebematytų ir negalėtų rezervuoti

Scenario: Kambario be rezervacijų šalinimas
  Given sistemoje yra kambarys "Studija B" be rezervacijų
  When administratorius pašalina kambarį "Studija B"
  Then kambario "Studija B" sistemoje nebėra

Scenario: Kartu su kambariu pašalinamos ir jo rezervacijos
  Given sistemoje yra kambarys "Studija A"
  And kambaryje "Studija A" yra 2 rezervacijos
  When administratorius pašalina kambarį "Studija A"
  Then kambario "Studija A" sistemoje nebėra
  And tų rezervacijų sistemoje nebėra

Scenario: Neegzistuojančio kambario šalinimas
  When administratorius šalina kambarį, kurio ID neegzistuoja
  Then gaunamas pranešimas, kad kambarys nerastas
```

## US-4. Grupės registravimas

Susiję reikalavimai: FR-5, FR-6.

```gherkin
Kaip grupės atstovas
Noriu užregistruoti savo grupę
Kad galėčiau rezervuoti kambarius jos vardu

Scenario: Sėkmingas grupės registravimas
  When grupės atstovas registruoja grupę "Vakaro Aidas" su el. paštu "aidas@example.com" ir 4 nariais
  Then grupė sukuriama ir jai suteikiamas ID
  And grupę galima rasti pagal šį ID

Scenario: Neteisingas el. pašto formatas
  When grupės atstovas registruoja grupę "Vakaro Aidas" su el. paštu "ne-el-pastas" ir 4 nariais
  Then grupė nesukuriama
  And gaunamas pranešimas, kad el. pašto formatas neteisingas

Scenario: Neteisingas narių skaičius
  When grupės atstovas registruoja grupę "Vakaro Aidas" su el. paštu "aidas@example.com" ir 0 narių
  Then grupė nesukuriama
  And gaunamas pranešimas, kad narių skaičius turi būti ne mažesnis nei 1

Scenario: Grupė nerasta
  When ieškoma grupė, kurios ID neegzistuoja
  Then gaunamas pranešimas, kad grupė nerasta
```

## US-5. Grupės duomenų keitimas ir šalinimas

Susiję reikalavimai: FR-7, FR-8.

```gherkin
Kaip grupės atstovas
Noriu atnaujinti grupės duomenis arba ją pašalinti
Kad sistemoje būtų tik aktuali informacija

Scenario: Narių skaičiaus pakeitimas
  Given sistemoje yra grupė "Vakaro Aidas" su 4 nariais
  When grupės atstovas pakeičia grupės "Vakaro Aidas" narių skaičių į 6
  Then grupės "Vakaro Aidas" narių skaičius yra 6

Scenario: Kartu su grupe pašalinamos ir jos rezervacijos
  Given sistemoje yra grupė "Vakaro Aidas"
  And grupė "Vakaro Aidas" turi 1 rezervaciją
  When grupės atstovas pašalina grupę "Vakaro Aidas"
  Then grupės "Vakaro Aidas" sistemoje nebėra
  And tos rezervacijos sistemoje nebėra
```

## US-6. Repeticijos rezervavimas

Susiję reikalavimai: FR-9, FR-13, FR-14.

```gherkin
Kaip grupės atstovas
Noriu rezervuoti repeticijų kambarį pasirinktam laikui
Kad grupė turėtų kur repetuoti

Background:
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  And sistemoje yra grupė "Vakaro Aidas" su 4 nariais

Scenario: Sėkminga rezervacija ir kainos apskaičiavimas
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 18:00 iki 20:00
  Then rezervacija sukuriama su būsena "ACTIVE"
  And rezervacijos kaina yra 30.00

Scenario: Kambarys tuo metu jau užimtas
  Given kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 19:00 iki 21:00
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad kambarys tuo metu užimtas

Scenario: Rezervacija iškart po kitos rezervacijos
  Given kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 20:00 iki 22:00
  Then rezervacija sukuriama su būsena "ACTIVE"

Scenario: Atšaukta rezervacija laiko neužima
  Given kambaryje "Studija A" yra atšaukta rezervacija rytoj nuo 18:00 iki 20:00
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 18:00 iki 20:00
  Then rezervacija sukuriama su būsena "ACTIVE"

Scenario: Grupė per didelė kambariui
  Given sistemoje yra grupė "Orkestras" su 12 narių
  When grupė "Orkestras" rezervuoja kambarį "Studija A" rytoj nuo 18:00 iki 20:00
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad grupės narių skaičius viršija kambario talpą

Scenario: Rezervacija praeityje
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" vakar nuo 18:00 iki 20:00
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad negalima rezervuoti praeityje

Scenario: Per ilga rezervacija
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 18:00 iki 22:00
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad rezervacija turi trukti nuo 1 iki 3 valandų

Scenario: Trukmė nėra sveikas valandų skaičius
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 18:00 iki 19:30
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad rezervacija turi trukti sveiką valandų skaičių

Scenario: Pabaiga ankstesnė už pradžią
  When grupė "Vakaro Aidas" rezervuoja kambarį "Studija A" rytoj nuo 20:00 iki 18:00
  Then rezervacija nesukuriama
  And gaunamas pranešimas, kad pabaigos laikas turi būti vėlesnis už pradžios laiką
```

## US-7. Kambario užimtumo peržiūra

Susiję reikalavimai: FR-10, FR-11.

```gherkin
Kaip grupės atstovas
Noriu matyti, kuriais laikais kambarys jau užimtas
Kad galėčiau pasirinkti laisvą laiką

Background:
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  And sistemoje yra grupė "Vakaro Aidas" su 4 nariais

Scenario: Rodomos tik aktyvios kambario rezervacijos
  Given kambaryje "Studija A" yra rezervacija rytoj nuo 10:00 iki 12:00
  And kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
  And kambaryje "Studija A" yra atšaukta rezervacija rytoj nuo 14:00 iki 16:00
  When grupės atstovas peržiūri kambario "Studija A" užimtumą
  Then matomos 2 rezervacijos
  And atšaukta rezervacija nerodoma

Scenario: Vienos rezervacijos informacija
  Given kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
  When grupės atstovas peržiūri tą rezervaciją pagal ID
  Then matoma grupė "Vakaro Aidas", kambarys "Studija A", būsena "ACTIVE" ir kaina 30.00
```

## US-8. Rezervacijos atšaukimas

Susiję reikalavimai: FR-12.

```gherkin
Kaip grupės atstovas
Noriu atšaukti rezervaciją, kai repeticija nebevyks
Kad kambarys atsilaisvintų kitoms grupėms

Background:
  Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
  And sistemoje yra grupė "Vakaro Aidas" su 4 nariais

Scenario: Atšaukimas likus daugiau nei 24 valandoms
  Given grupė "Vakaro Aidas" turi rezervaciją po 3 dienų nuo 18:00 iki 20:00
  When grupės atstovas atšaukia tą rezervaciją
  Then rezervacijos būsena yra "CANCELLED"

Scenario: Per vėlu atšaukti
  Given grupė "Vakaro Aidas" turi rezervaciją, kuri prasideda po 10 valandų ir trunka 2 valandas
  When grupės atstovas atšaukia tą rezervaciją
  Then rezervacija neatšaukiama
  And gaunamas pranešimas, kad atšaukti galima ne vėliau kaip prieš 24 valandas
  And rezervacijos būsena lieka "ACTIVE"

Scenario: Pakartotinis atšaukimas
  Given grupė "Vakaro Aidas" turi atšauktą rezervaciją po 3 dienų nuo 18:00 iki 20:00
  When grupės atstovas atšaukia tą rezervaciją
  Then gaunamas pranešimas, kad rezervacija jau atšaukta
```

## Reikalavimų ir istorijų sąsajos

| Reikalavimas | Istorija |
| --- | --- |
| FR-1, FR-2 | US-1 |
| FR-3 | US-2 |
| FR-4 | US-3 |
| FR-5, FR-6 | US-4 |
| FR-7, FR-8 | US-5 |
| FR-9, FR-14 | US-6 |
| FR-10, FR-11 | US-7 |
| FR-12 | US-8 |
| FR-13 | US-2, US-6 |
