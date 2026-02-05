@v1 @EPIC-01 @US-001
Feature: Product creation (V1)

  Scenario: Create a new product successfully
    Given the product catalog is empty
    When the client creates a product with:
      | sku  | name     | price  | stock |
      | TV-1 | Smart TV | 799.99 | 10    |
    Then the response status should be 201
    And the created product should contain:
      | sku  | name     |
      | TV-1 | Smart TV |

  Scenario: Reject creation when SKU already exists
    Given the product catalog is empty
    And a product already exists with SKU "TV-1"
    When the client creates a product with:
      | sku  | name       | price  | stock |
      | TV-1 | Another TV | 999.99 | 5     |
    Then the response status should be 409
    And the error message should contain "SKU already exists"
