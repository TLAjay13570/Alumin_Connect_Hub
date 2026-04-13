package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

/**
 * Page Object Model for the Alumni Directory page ({@code /directory}).
 *
 * <p>Wraps the directory view: search + tabs (My Connections, Suggestions,
 * Requests, Sent, Global Map) + member cards with Message / View Profile actions.
 *
 * <p>Locator strategy: prefer stable textual/role hooks (placeholders, ARIA roles,
 * button text) over brittle CSS class chains produced by Tailwind/shadcn.
 */
public class DirectoryPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DirectoryPage.class);

    // ─────────────────────────── Locators ────────────────────────────────────

    /** Page header — "Directory" heading inside &lt;main&gt;. */
    private static final By HEADER = By.xpath("//main//h1[normalize-space()='Directory']");

    /** Search input — placeholder observed on the deployed /directory page. */
    private static final By SEARCH_INPUT =
            By.xpath("//input[@placeholder='Search connections...']");

    /** Any tab trigger by visible label (Radix/shadcn renders role='tab'). */
    private static final String TAB_XPATH_FMT =
            "//button[@role='tab' and contains(normalize-space(),'%s')]";

    /** The currently visible tab panel (Radix sets data-state='active'). */
    private static final By ACTIVE_TAB_PANEL =
            By.xpath("//*[@role='tabpanel' and @data-state='active']");

    /**
     * A member card inside the active tab panel. Cards are the grid cells under
     * the active panel. We scope to role='tabpanel' so we never match cards
     * still mounted but hidden under another tab. A card is identified by the
     * presence of the "Message" action button (shared across My Connections /
     * Suggestions cards in the /directory view).
     */
    private static final By MEMBER_CARDS =
            By.xpath("//*[@role='tabpanel' and @data-state='active']"
                    + "//div[contains(@class,'grid')]/div[.//button[normalize-space()='Message']]");

    /**
     * Empty state shown when search has no matches. Kept loose to tolerate both
     * "No connections found" and "No members found" variants across builds.
     */
    private static final By NO_RESULTS =
            By.xpath("//*[@role='tabpanel' and @data-state='active']"
                    + "//h3[starts-with(normalize-space(),'No ') and "
                    + "(contains(normalize-space(),'found') or contains(normalize-space(),'results'))]");

    /** World-map container used on the Global Map tab. */
    private static final By GLOBAL_MAP =
            By.xpath("//*[@role='tabpanel' and @data-state='active']"
                    + "//*[contains(@class,'leaflet-container') "
                    + "or contains(@class,'mapbox') "
                    + "or self::canvas "
                    + "or self::svg[.//*[local-name()='g']]]");

    // ───────────────────────── Navigation ────────────────────────────────────

    /** Navigates to the directory page ({@code /directory}). */
    public void navigateToDirectoryPage() {
        String url = ConfigReader.getBaseUrl();
        url = url.endsWith("/") ? url + "directory" : url + "/directory";
        logger.info("Navigating to directory page: {}", url);
        navigateTo(url);
        WebDriverWaitUtil.waitForElementVisible(HEADER);
    }

    /** Page header text ("Directory"). */
    public String getHeaderText() {
        return WebDriverWaitUtil.waitForElementVisible(HEADER).getText().trim();
    }

    /** Returns true if the browser is currently on the directory URL. */
    public boolean isOnDirectoryUrl() {
        String current = driver.getCurrentUrl();
        return current != null && current.contains("/directory");
    }

    public boolean isSearchInputVisible() {
        try {
            return WebDriverWaitUtil.waitForElementVisible(SEARCH_INPUT).isDisplayed();
        } catch (Exception e) {
            logger.debug("Search input not visible: {}", e.getMessage());
            return false;
        }
    }

    // ───────────────────────── Tabs / Filters ────────────────────────────────

    /** Clicks a tab by its visible label ("My Connections", "Requests", "Sent", "Global Map"). */
    public void selectTab(String tabLabel) {
        logger.info("Selecting directory tab: {}", tabLabel);
        By tab = By.xpath(String.format(TAB_XPATH_FMT, tabLabel));
        WebElement el = WebDriverWaitUtil.waitForElementClickable(tab);
        click(el);
        // Wait for the matching panel to become active (Radix flips data-state).
        WebDriverWaitUtil.waitForElementVisible(ACTIVE_TAB_PANEL);
    }

    public boolean isTabVisible(String tabLabel) {
        By tab = By.xpath(String.format(TAB_XPATH_FMT, tabLabel));
        try {
            return WebDriverWaitUtil.waitForElementVisible(tab).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isActiveTabPanelVisible() {
        try {
            return WebDriverWaitUtil.waitForElementVisible(ACTIVE_TAB_PANEL).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ───────────────────────── Search ────────────────────────────────────────

    /**
     * Enters a query in the directory search field. Search is client-side so we
     * only need to wait for the card list (or the empty state) to settle.
     */
    public void searchFor(String query) {
        logger.info("Searching directory for: '{}'", query);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(SEARCH_INPUT);
        input.clear();
        input.sendKeys(query == null ? "" : query);
        // Let React re-render; wait for either results or the no-results state.
        WebDriverWaitUtil.waitForAnyElementVisible(List.of(MEMBER_CARDS, NO_RESULTS));
    }

    // ───────────────────────── Member Cards ──────────────────────────────────

    public List<WebElement> getMemberCards() {
        return driver.findElements(MEMBER_CARDS);
    }

    public int getMemberCardCount() {
        return getMemberCards().size();
    }

    /**
     * Returns true if every visible card has a non-blank name and at least two
     * additional lines of meta text (job title + company). Resilient across both
     * the /connections and /directory card variants: we simply require ≥3
     * non-blank short text lines inside the card rather than pinning on a
     * specific tag/class combination.
     */
    public boolean allCardsHaveRequiredFields() {
        List<WebElement> cards = getMemberCards();
        if (cards.isEmpty()) {
            return false;
        }
        for (WebElement card : cards) {
            String name = safeText(card, ".//h3");
            if (name.isEmpty()) {
                return false;
            }
            List<WebElement> metaLines = card.findElements(
                    By.xpath(".//p[string-length(normalize-space()) > 0]"));
            long nonBlank = metaLines.stream()
                    .map(e -> e.getText() == null ? "" : e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .count();
            if (nonBlank < 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Every visible card contains {@code query} in name / job title / company
     * (mirrors the client-side filter the /directory page applies).
     */
    public boolean allCardsContain(String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String needle = query.toLowerCase(Locale.ROOT);
        List<WebElement> cards = getMemberCards();
        if (cards.isEmpty()) {
            return false;
        }
        for (WebElement card : cards) {
            String haystack = card.getText().toLowerCase(Locale.ROOT);
            if (!haystack.contains(needle)) {
                return false;
            }
        }
        return true;
    }

    public boolean isNoResultsDisplayed() {
        try {
            return WebDriverWaitUtil.waitForElementVisible(NO_RESULTS).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ───────────────────────── Profile / Actions ─────────────────────────────

    /**
     * A small DTO that captures the visible details of a member card so step
     * definitions can assert on them without holding onto stale WebElements.
     */
    public static final class MemberCardDetails {
        public final String name;
        public final String jobTitle;
        public final String company;
        public final String university;
        public final String year;

        public MemberCardDetails(String name, String jobTitle, String company,
                                 String university, String year) {
            this.name = name;
            this.jobTitle = jobTitle;
            this.company = company;
            this.university = university;
            this.year = year;
        }

        public boolean isComplete() {
            return notBlank(name) && notBlank(jobTitle) && notBlank(company)
                    && notBlank(university) && notBlank(year);
        }

        private static boolean notBlank(String s) {
            return s != null && !s.isBlank();
        }
    }

    /**
     * Reads the first member card on the directory and returns its details.
     * This does NOT navigate away — the card itself is the visible profile
     * summary on the list view. For full-profile navigation use
     * {@link #clickViewProfileOnFirstMember()}.
     */
    public MemberCardDetails openFirstMember() {
        List<WebElement> cards = getMemberCards();
        if (cards.isEmpty()) {
            throw new IllegalStateException("No member cards are visible on the directory.");
        }
        WebElement card = cards.get(0);
        WebDriverWaitUtil.waitForElementVisible(card);

        String name = safeText(card, ".//h3");

        // Collect every non-blank short text row inside the card. Cards render
        // job title, company, and university/year as separate lines — order
        // matches the visual order of the rendered card.
        List<WebElement> lines = card.findElements(
                By.xpath(".//p[string-length(normalize-space()) > 0] | "
                        + ".//span[string-length(normalize-space()) > 0]"));
        List<String> texts = new java.util.ArrayList<>();
        for (WebElement e : lines) {
            String t = e.getText() == null ? "" : e.getText().trim();
            if (!t.isEmpty() && !t.equalsIgnoreCase(name) && !texts.contains(t)) {
                texts.add(t);
            }
        }

        String jobTitle = texts.size() > 0 ? texts.get(0) : "";
        String company = texts.size() > 1 ? texts.get(1) : "";

        // University + year — find the first line that looks like "University 'YYYY".
        String university = "";
        String year = "";
        for (String t : texts) {
            int apos = t.lastIndexOf('\'');
            if (apos > 0 && apos < t.length() - 1
                    && t.substring(apos + 1).trim().matches("\\d{2,4}")) {
                university = t.substring(0, apos).trim();
                year = t.substring(apos + 1).trim();
                break;
            }
        }

        MemberCardDetails details = new MemberCardDetails(name, jobTitle, company, university, year);
        logger.info("First member: name='{}' job='{}' company='{}' university='{}' year='{}'",
                name, jobTitle, company, university, year);
        return details;
    }

    /** Clicks the "Message" button on the first member card. Navigates to /chat. */
    public void clickMessageOnFirstMember() {
        clickCardActionOnFirstMember("Message");
        WebDriverWaitUtil.waitForUrlToContain("/chat");
    }

    /**
     * Clicks the "View Profile" button on the first member card. Exact target
     * URL (e.g. /profile/:id or modal) varies by build, so callers should use
     * {@link #isOnDirectoryUrl()} or a URL-change wait to verify navigation.
     */
    public void clickViewProfileOnFirstMember() {
        clickCardActionOnFirstMember("View Profile");
    }

    private void clickCardActionOnFirstMember(String buttonLabel) {
        List<WebElement> cards = getMemberCards();
        if (cards.isEmpty()) {
            throw new IllegalStateException("No member cards are visible on the directory.");
        }
        WebElement btn = cards.get(0)
                .findElement(By.xpath(".//button[normalize-space()='" + buttonLabel + "']"));
        WebDriverWaitUtil.waitForElementClickable(btn);
        click(btn);
    }

    // ───────────────────────── Global Map ────────────────────────────────────

    public boolean isGlobalMapVisible() {
        try {
            return WebDriverWaitUtil.waitForElementVisible(GLOBAL_MAP).isDisplayed();
        } catch (Exception e) {
            // Fallback: the WorldMapHeatmap component always renders a container
            // with a title on this tab even if the map library hasn't finished.
            try {
                By fallback = By.xpath(
                        "//*[@role='tabpanel' and @data-state='active']"
                                + "//*[contains(normalize-space(.),'Global Alumni Distribution') "
                                + "or contains(normalize-space(.),'University information required')]");
                return WebDriverWaitUtil.waitForElementVisible(fallback).isDisplayed();
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    // ───────────────────────── Helpers ───────────────────────────────────────

    private static String safeText(WebElement scope, String relativeXpath) {
        try {
            return scope.findElement(By.xpath(relativeXpath)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
