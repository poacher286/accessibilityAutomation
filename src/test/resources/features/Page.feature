Feature: Accessibility

  Scenario Outline: Axe tool automation <site>

    Given I navigate to "<site>"
    When I run axe tool
    Then I should get report

    Examples:
      | site     |
      | google   |
      | tdBank   |
#      | cricbuzz |