@SupportTicket @SupportTicketValidation
Feature: Support Ticket — edge cases
  Positive edge-case inputs for the support ticket system.

  # Toast facts (from source code):
  #   SupportTicketModal.tsx:
  #     Create success     → title "Ticket Submitted"  desc "Your support ticket has been submitted successfully..."
  #   AdminSupport.tsx:
  #     Admin reply success → title "Response Sent"    desc "Your response has been sent to the user."

  Background:
    Given I navigate to the login page
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    When I navigate to the support ticket page
    And I click the Create Ticket button

  # ═══════════════════════════════════════════════════════════════════════
  # Edge Cases — Input Boundaries (Positive)
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
  # Admin Edge Cases (Positive)
  # ═══════════════════════════════════════════════════════════════════════

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
