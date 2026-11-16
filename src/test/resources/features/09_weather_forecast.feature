Feature: US-9. Orų prognozė repeticijai
  Kaip grupės atstovas
  Noriu matyti orų prognozę repeticijos pradžiai
  Kad žinotume, kaip saugiai atvežti instrumentus

  Background:
    Given sistemoje yra kambarys "Studija A", kurio talpa 5 ir valandos kaina 15.00
    And sistemoje yra grupė "Vakaro Aidas" su 4 nariais

  Scenario: Prognozė rezervacijos laikui
    Given kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
    And orų tarnyba rytoj 18:00 prognozuoja -2.5 laipsnio ir "light-snow"
    When grupės atstovas peržiūri tos rezervacijos orų prognozę
    Then matoma prognozė: -2.5 laipsnio, "Nedidelis sniegas"

  Scenario: Prognozės dar nėra
    Given grupė "Vakaro Aidas" turi rezervaciją po 20 dienų nuo 18:00 iki 20:00
    And orų tarnyba rytoj 18:00 prognozuoja 3.0 laipsnio ir "cloudy"
    When grupės atstovas peržiūri tos rezervacijos orų prognozę
    Then prognozė negaunama
    And gaunamas pranešimas, kad orų prognozės šiam laikui dar nėra

  Scenario: Orų tarnyba nepasiekiama
    Given kambaryje "Studija A" yra rezervacija rytoj nuo 18:00 iki 20:00
    And orų tarnyba nepasiekiama
    When grupės atstovas peržiūri tos rezervacijos orų prognozę
    Then prognozė negaunama
    And gaunamas pranešimas, kad orų tarnyba nepasiekiama
