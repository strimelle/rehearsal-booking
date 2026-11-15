Feature: US-4. Grupės registravimas
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
