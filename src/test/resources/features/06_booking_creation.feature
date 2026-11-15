Feature: US-6. Repeticijos rezervavimas
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
