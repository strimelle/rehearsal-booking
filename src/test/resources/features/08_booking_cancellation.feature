Feature: US-8. Rezervacijos atšaukimas
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
