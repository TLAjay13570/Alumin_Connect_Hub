@SupportTicket @SupportTicketValidation
Feature: Support Ticket — form validation and edge cases
  The UI blocks submission when required fields are missing and handles edge-case inputs gracefully.
  Validation is delivered via toast notifications (title "Error") rather than inline HTML errors.

  Background:
    Given I navigate to the login page
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    When I navigate to the support ticket page
    And I click the Create Ticket button

  # ═══════════════════════════════════════════════════════════════════════
  # Empty / Incomplete Submissions
  # Validation: SupportTicketModal.tsx checks subject.trim() && description.trim()
  # Toast on failure: title "Error", description "Please fill in all required fields."
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicketValidation @Negative @Regression
  Scenario: Submit with all fields empty shows validation error toast
    When I attempt to submit the ticket form without filling required fields
    Then I should see validation errors on the ticket form
    And the ticket form should remain open

  @SupportTicketValidation @Negative @Regression
  Scenario: Submit with only subject filled shows validation error toast
    When I enter ticket subject "Incomplete — subject only"
    And I attempt to submit the ticket form without filling required fields
    Then I should see validation errors on the ticket form
    And the ticket form should remain open

  @SupportTicketValidation @Negative @Regression
  Scenario: Submit with subject and category but no description shows validation error
    When I enter ticket subject "AUTO_ST_UNIQUE — No Description"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I attempt to submit the ticket form without filling required fields
    Then I should see validation errors on the ticket form
    And the ticket form should remain open

  # ═══════════════════════════════════════════════════════════════════════
  # Edge Cases — Input Boundaries
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicketValidation @EdgeCase @Regression
  Scenario: Alumni submits a ticket with a very long description
    When I enter ticket subject "AUTO_ST_UNIQUE — Long Description Edge"
    And I select ticket category "General"
    And I select ticket priority "Low"
    And I enter ticket description "This is an intentionally verbose description designed to verify that the support ticket system handles large text inputs gracefully without crashing, corrupting data, or silently dropping content. The system should either accept the full input, display a character-limit counter, or return a clear validation error. This text is well within typical database field limits (usually 65,535 chars for TEXT) but exceeds a short varchar. Systems that silently truncate here create data-integrity bugs in production. Automation tests like this guard against regressions in input handling and ensure user experience remains predictable at the boundaries."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"
    And the ticket list should show my created ticket

  @SupportTicketValidation @EdgeCase @Regression
  Scenario: Alumni submits a ticket with minimum valid inputs
    When I enter ticket subject "X"
    And I select ticket category "General"
    And I select ticket priority "Low"
    And I enter ticket description "Minimal."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"

  # ═══════════════════════════════════════════════════════════════════════
  # Admin Edge Cases
  # handleSendResponse shows toast "Error"/"Please enter a message." on empty reply
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicketValidation @Negative @Admin @Regression
  Scenario: Admin cannot send an empty reply
    When I enter ticket subject "AUTO_ST_UNIQUE — Admin Empty Reply"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I enter ticket description "Ticket for testing empty admin reply validation."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I enter admin reply ""
    And I send the admin reply
    Then I should see validation errors in the reply section

  @SupportTicketValidation @EdgeCase @Admin @Regression
  Scenario: Admin sends a reply with a very long message
    When I enter ticket subject "AUTO_ST_UNIQUE — Admin Long Reply"
    And I select ticket category "General"
    And I select ticket priority "Low"
    And I enter ticket description "Testing long admin reply message handling."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I enter admin reply "This is a deliberately long admin reply to verify that the system handles large response messages correctly. It checks that the reply textarea, the backend API, and the conversation rendering layer all handle text beyond typical short answers. A robust system should accept this without truncation, display it correctly in the thread, and not break the UI layout. Thank you for your patience while we process your request — our support team will follow up within 1-2 business days."
    And I send the admin reply
    Then I should see a ticket toast containing "Response Sent"
