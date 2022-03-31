Feature: Accessibility

  Scenario Outline: Axe tool automation for <site>

    Given I navigate to "<site>"
    When I run axe tool
      | tag     |
      | wcag2aa |
      | wcag143 |
      | wcag111 |
      | wcag412 |
      | wcag211 |
      | wcag131 |
      | wcag212 |
      | wcag243 |
      | wcag331 |
    Then I should get report

    Examples:
      | site     |
      | main           |
      | en             |
      | corporate      |
      | community      |
      | readytravel    |
      | departure      |
      | arrival        |
      | accessibility  |
      | whileyou       |
      | transportation |
      | whoweare       |
#      | google   |
#      | tdBank   |
#      | cricbuzz |