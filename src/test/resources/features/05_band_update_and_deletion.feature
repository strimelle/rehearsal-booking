Feature: US-5. Grupės duomenų keitimas ir šalinimas
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
