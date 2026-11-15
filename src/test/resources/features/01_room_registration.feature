Feature: US-1. Kambario registravimas
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
