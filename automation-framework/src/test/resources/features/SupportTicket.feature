@SupportTicket
Feature: Support Ticket System — alumni and admin lifecycle
  Alumni submit support tickets; admins view, reply, and manage status.
  All ticket subjects containing "UNIQUE" receive a UUID suffix at runtime.

  # Toast facts (from source):
  #   Create success  → title "Ticket Submitted"  (assert with "submitted")
  #   Reply success   → title "Response Sent"     (assert with "Response Sent")
  #   Status update   → title "Ticket Updated"    (assert with "updated")
  # After "Update Ticket" the dialog closes automatically (setSelectedTicket(null)).

  Background:
    Given I navigate to the login page
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully

  # ═══════════════════════════════════════════════════════════════════════
  # ALUMNI — Page Load
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Smoke @Positive
  Scenario: Alumni can open the support ticket page
    When I navigate to the support ticket page
    Then I should see the support ticket page

  # ═══════════════════════════════════════════════════════════════════════
  # ALUMNI — Create Ticket (Positive)
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Smoke @Positive @CreateTicket
  Scenario: Alumni creates a High-priority Technical ticket
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Login Issue"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I enter ticket description "I cannot log into the portal after my password reset. Please assist."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"
    And the ticket list should show my created ticket

  @SupportTicket @Regression @Positive @CreateTicket
  Scenario: Alumni creates a Medium-priority General ticket
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Portal Navigation"
    And I select ticket category "General"
    And I select ticket priority "Medium"
    And I enter ticket description "I need help navigating the alumni portal features."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"
    And the ticket list should show my created ticket

  @SupportTicket @Regression @Positive @CreateTicket
  Scenario: Alumni creates a Low-priority Academic ticket
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Transcript Request"
    And I select ticket category "Academic"
    And I select ticket priority "Low"
    And I enter ticket description "I would like to request an official transcript from the university."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"
    And the ticket list should show my created ticket

  # ═══════════════════════════════════════════════════════════════════════
  # ALUMNI — View Ticket Detail / Conversation
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Regression @Positive
  Scenario: Alumni opens a created ticket and sees the conversation section
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — View Conversation"
    And I select ticket category "Technical"
    And I select ticket priority "Medium"
    And I enter ticket description "Testing conversation thread visibility for alumni."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I open the ticket with my tracked subject
    Then I should see the conversation thread

  # ═══════════════════════════════════════════════════════════════════════
  # ADMIN — Dashboard
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Smoke @Admin
  Scenario: Admin views the support tickets management page
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    Then I should see the admin support tickets page
    And I should see the ticket list table

  # ═══════════════════════════════════════════════════════════════════════
  # ADMIN — Open Ticket Detail
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Regression @Admin
  Scenario: Admin opens a ticket detail modal and sees the conversation
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Admin Detail Check"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I enter ticket description "Verifying admin can open ticket detail modal and view conversation."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    And I should see the conversation in the modal

  # ═══════════════════════════════════════════════════════════════════════
  # ADMIN — Reply to Ticket
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Regression @Admin @Reply
  Scenario: Admin replies to an alumni support ticket
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Needs Admin Reply"
    And I select ticket category "Technical"
    And I select ticket priority "Medium"
    And I enter ticket description "Please advise on how to update my profile photo."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I enter admin reply "Please navigate to Profile Settings to update your photo."
    And I send the admin reply
    Then I should see a ticket toast containing "Response Sent"

  # ═══════════════════════════════════════════════════════════════════════
  # ADMIN — Change Status
  # Note: "Update Ticket" closes the dialog automatically (setSelectedTicket(null)).
  #       Assert status from the ticket list after the modal closes.
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Regression @Admin @StatusChange
  Scenario: Admin changes ticket status to In Progress
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Status InProgress"
    And I select ticket category "General"
    And I select ticket priority "Low"
    And I enter ticket description "Checking admin can change status to In Progress."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I change the ticket status to "In Progress"
    And I save the ticket status change
    Then I should see a ticket toast containing "updated"
    And the ticket status in admin list should be "In Progress"

  @SupportTicket @Regression @Admin @StatusChange @Cleanup
  Scenario: Admin closes a resolved ticket
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — Close Ticket"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I enter ticket description "Issue is resolved and ticket should be closed."
    And I submit the ticket form
    Then the ticket list should show my created ticket
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I change the ticket status to "Closed"
    And I save the ticket status change
    Then I should see a ticket toast containing "updated"
    And the ticket status in admin list should be "Closed"

  # ═══════════════════════════════════════════════════════════════════════
  # E2E — Full Flow: Create → Admin Reply + Status → Alumni Verifies
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @E2E @Regression
  Scenario: Full E2E flow — alumni creates ticket, admin replies and sets status, alumni verifies
    # ── Phase 1: Alumni creates the ticket ──────────────────────────────
    When I navigate to the support ticket page
    And I click the Create Ticket button
    And I enter ticket subject "AUTO_ST_UNIQUE — E2E Full Flow"
    And I select ticket category "Technical"
    And I select ticket priority "High"
    And I enter ticket description "End-to-end test: create, admin reply, status change, alumni verify."
    And I submit the ticket form
    Then I should see a ticket toast containing "submitted"
    And the ticket list should show my created ticket
    # ── Phase 2: Admin replies and sets status to In Progress ────────────
    When I logout and clear session
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    When I navigate to the admin support tickets page
    And I open the ticket detail for my tracked subject from admin
    Then the ticket detail modal should be open
    When I enter admin reply "Your issue has been reviewed and is now being processed by our team."
    And I send the admin reply
    Then I should see a ticket toast containing "Response Sent"
    When I open the ticket detail for my tracked subject from admin
    And I change the ticket status to "In Progress"
    And I save the ticket status change
    Then I should see a ticket toast containing "updated"
    # ── Phase 3: Alumni re-opens ticket to verify reply and status ───────
    When I logout and clear session
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    When I navigate to the support ticket page
    And I open the ticket with my tracked subject
    Then I should see the conversation thread
    And I should see admin reply "Your issue has been reviewed" in the conversation
    And the ticket status should be "In Progress"

  # ═══════════════════════════════════════════════════════════════════════
  # ACCESS CONTROL
  # ═══════════════════════════════════════════════════════════════════════

  @SupportTicket @Security @AccessControl
  Scenario: Logged-in alumni cannot access the admin support management URL
    When I navigate directly to the admin support URL as alumni
    Then I should not remain on the admin support page

  @SupportTicket @Security @AccessControl
  Scenario: Unauthenticated user is redirected away from admin support page
    When I logout and clear session
    When I open the admin support page without authentication
    Then I should be redirected away from admin support page
