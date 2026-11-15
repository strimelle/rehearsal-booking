Feature: US-3. Kambario šalinimas
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
