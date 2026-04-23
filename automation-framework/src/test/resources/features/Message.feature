@Message
Feature: Message Module — alumni send and view messages (happy path)
  Alumni open the Messages page, select a contact or group, send a message,
  and see it rendered in the conversation thread.

  # UI facts (from src/pages/Chat.tsx):
  #   Page heading      : <h1>Messages</h1>
  #   Search            : <Input placeholder="Search messages..." />
  #   Filter tabs       : "All (n)", "Connections (n)", "Groups (n)"
  #   Chat header       : <h2>{chat.name}</h2>
  #   Message composer  : <Input placeholder="Message {name}..." />  +  icon-only Send button
  #   Messages          : local React state only — receiver-side verification
  #                       requires a second WebDriver session (not covered here).
  #
  # Each message body containing "UNIQUE" receives a short UUID suffix at runtime
  # so repeated runs produce distinct content that can be reliably located in the thread.

  Background:
    Given I navigate to the login page
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully

  # ═══════════════════════════════════════════════════════════════════════
  # Page Load
  # ═══════════════════════════════════════════════════════════════════════

  @Message @Smoke @Positive
  Scenario: Alumni can open the Message module
    When I open the Message module
    Then I should see the Message page

  # ═══════════════════════════════════════════════════════════════════════
  # Select a chat
  # ═══════════════════════════════════════════════════════════════════════

  @Message @Smoke @Positive
  Scenario: Alumni opens a conversation from the Connections tab
    When I open the Message module
    And I switch the chat list filter to "connections"
    And I select the first available chat from the list
    Then the chat thread should be open for the selected chat

  # ═══════════════════════════════════════════════════════════════════════
  # Send a message (happy path)
  # ═══════════════════════════════════════════════════════════════════════

  @Message @Smoke @Positive @SendMessage
  Scenario: Alumni sends a text message in a 1:1 conversation
    When I open the Message module
    And I switch the chat list filter to "connections"
    And I select the first available chat from the list
    And I send the message "AUTO_MSG_UNIQUE Hello from automation"
    Then the message "AUTO_MSG_UNIQUE Hello from automation" should appear in the conversation
    And the last message in the thread should contain "AUTO_MSG_UNIQUE"

  @Message @Regression @Positive @SendMessage
  Scenario: Alumni sends a message in a group chat
    When I open the Message module
    And I switch the chat list filter to "groups"
    And I select the first available chat from the list
    And I send the message "AUTO_MSG_UNIQUE Group hello"
    Then the message "AUTO_MSG_UNIQUE Group hello" should appear in the conversation

  @Message @Regression @Positive @SendMessage
  Scenario: Alumni sends multiple messages in the same thread
    When I open the Message module
    And I switch the chat list filter to "connections"
    And I select the first available chat from the list
    And I send the message "AUTO_MSG_UNIQUE First message"
    And I send the message "AUTO_MSG_UNIQUE Second message"
    Then the message "AUTO_MSG_UNIQUE First message" should appear in the conversation
    And the message "AUTO_MSG_UNIQUE Second message" should appear in the conversation
    And the last message in the thread should contain "Second message"

  # ═══════════════════════════════════════════════════════════════════════
  # Search the chat list
  # ═══════════════════════════════════════════════════════════════════════

  @Message @Regression @Positive @Search
  Scenario: Alumni searches the chat list and opens a matching conversation
    When I open the Message module
    And I switch the chat list filter to "all"
    And I remember the first chat name from the list
    When I search the chat list for the remembered name
    Then the remembered chat should be visible in the list
    When I select the remembered chat from the list
    Then the chat thread should be open for the remembered chat

  # ═══════════════════════════════════════════════════════════════════════
  # End-to-end: select → send → render
  # ═══════════════════════════════════════════════════════════════════════

  @Message @E2E @Positive
  Scenario: Alumni end-to-end — open Messages, pick a contact, send, and verify render
    When I open the Message module
    Then I should see the Message page
    When I switch the chat list filter to "connections"
    And I select the first available chat from the list
    Then the chat thread should be open for the selected chat
    When I send the message "AUTO_MSG_UNIQUE E2E happy path"
    Then the message "AUTO_MSG_UNIQUE E2E happy path" should appear in the conversation
    And the last message in the thread should contain "E2E happy path"
