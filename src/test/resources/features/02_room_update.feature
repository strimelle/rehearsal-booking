Feature: US-2. Kambario duomenų keitimas
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
