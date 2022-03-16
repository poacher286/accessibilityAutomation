Feature: Accessibility

  Scenario Outline: Axe tool automation <site>

    Given I navigate to "<site>"
    When I run axe tool
      | tag     |
      | wcag2aa |
      | wcag143 |
    Then I should get report

    Examples:
      | site   |
      | google |
      | tdBank   |
      | cricbuzz |