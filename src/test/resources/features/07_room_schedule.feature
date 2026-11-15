Feature: US-7. Kambario užimtumo peržiūra
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
