import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestRunner {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printHeader();

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1":
                    runHomePageTest();
                    break;
                case "2":
                    runUniversitiesFilterTest();
                    break;
                case "3":
                    runProgramsFilterTest();
                    break;
                case "4":
                    runBlogsTest();
                    break;
                case "5":
                    runVisaSupportTest();
                    break;
                case "A":
                    runAllTests();
                    break;
                case "S":
                    runSelectedTests();
                    break;
                case "Q":
                    System.out.println("\n👋 Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("\n❌ Invalid choice. Please try again.");
            }

            System.out.println("\n" + "═".repeat(70));
            System.out.println("Press ENTER to continue...");
            scanner.nextLine();
        }
    }

    private static void printHeader() {
        System.out.println("\n" + "█".repeat(70));
        System.out.println("█" + " ".repeat(20) + "🚀 STUDYLEO TEST RUNNER" + " ".repeat(25) + "█");
        System.out.println("█" + " ".repeat(15) + "Automated Test Suite for StudyLeo.com" + " ".repeat(16) + "█");
        System.out.println("█".repeat(70));
    }

    private static void printMenu() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("📋 SELECT TEST(S) TO RUN:");
        System.out.println("═".repeat(70));
        System.out.println("  [1] 🏠 HomePageTest");
        System.out.println("  [2] 🎓 UniversitiesFilterTest");
        System.out.println("  [3] 📚 ProgramsFilterTest");
        System.out.println("  [4] 📝 BlogsTest");
        System.out.println("  [5] 🛂 VisaSupportTest");
        System.out.println("  " + "─".repeat(35));
        System.out.println("  [A] ▶️  Run ALL tests");
        System.out.println("  [S] ☑️  Select multiple tests");
        System.out.println("  [Q] 🚪 Quit");
        System.out.println("═".repeat(70));
        System.out.print("Enter your choice: ");
    }

    private static void runHomePageTest() {
        System.out.println("\n🏠 Starting HomePageTest...\n");
        try {
            HomePageTest test = new HomePageTest();
            test.run();
            test.close();
        } catch (Exception e) {
            System.err.println("❌ HomePageTest failed: " + e.getMessage());
        }
    }

    private static void runUniversitiesFilterTest() {
        System.out.println("\n🎓 Starting UniversitiesFilterTest...\n");
        try {
            UniversitiesTest test = new UniversitiesTest();
            test.run();
            test.close();
        } catch (Exception e) {
            System.err.println("❌ UniversitiesFilterTest failed: " + e.getMessage());
        }
    }

    private static void runProgramsFilterTest() {
        System.out.println("\n📚 Starting ProgramsFilterTest...\n");
        try {
            ProgramsFilterTest test = new ProgramsFilterTest();
            test.run();
            test.close();
        } catch (Exception e) {
            System.err.println("❌ ProgramsFilterTest failed: " + e.getMessage());
        }
    }

    private static void runBlogsTest() {
        System.out.println("\n📝 Starting BlogsTest...\n");
        try {
            BlogsTest test = new BlogsTest();
            test.run();
            test.close();
        } catch (Exception e) {
            System.err.println("❌ BlogsTest failed: " + e.getMessage());
        }
    }

    private static void runVisaSupportTest() {
        System.out.println("\n🛂 Starting VisaSupportTest...\n");
        try {
            VisaSupportTest test = new VisaSupportTest();
            test.run();
            test.close();
        } catch (Exception e) {
            System.err.println("❌ VisaSupportTest failed: " + e.getMessage());
        }
    }

    // Helper class for test stats
    static class TestStats {
        String className;
        int total, passed, failed;
        TestStats(String className, int total, int passed, int failed) {
            this.className = className;
            this.total = total;
            this.passed = passed;
            this.failed = failed;
        }
    }

    private static void runAllTests() {
        System.out.println("\n▶️ Running ALL tests...\n");
        System.out.println("═".repeat(70));

        long startTime = System.currentTimeMillis();
        List<TestStats> allStats = new ArrayList<>();

        // Create a single shared ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver sharedDriver = new ChromeDriver(options);

        try {
            HomePageTest homePageTest = new HomePageTest(sharedDriver, true);
            homePageTest.run();
            homePageTest.close();
            allStats.add(new TestStats(
                homePageTest.getTestClassName(),
                homePageTest.getTotalTests(),
                homePageTest.getPassedTests(),
                homePageTest.getFailedTests()
            ));

            UniversitiesTest universitiesTest = new UniversitiesTest(sharedDriver, true);
            universitiesTest.run();
            universitiesTest.close();
            allStats.add(new TestStats(
                universitiesTest.getTestClassName(),
                universitiesTest.getTotalTests(),
                universitiesTest.getPassedTests(),
                universitiesTest.getFailedTests()
            ));

            ProgramsFilterTest programsFilterTest = new ProgramsFilterTest(sharedDriver, true);
            programsFilterTest.run();
            programsFilterTest.close();
            allStats.add(new TestStats(
                programsFilterTest.getTestClassName(),
                programsFilterTest.getTotalTests(),
                programsFilterTest.getPassedTests(),
                programsFilterTest.getFailedTests()
            ));

            BlogsTest blogsTest = new BlogsTest(sharedDriver, true);
            blogsTest.run();
            blogsTest.close();
            allStats.add(new TestStats(
                blogsTest.getTestClassName(),
                blogsTest.getTotalTests(),
                blogsTest.getPassedTests(),
                blogsTest.getFailedTests()
            ));

            VisaSupportTest visaSupportTest = new VisaSupportTest(sharedDriver, true);
            visaSupportTest.run();
            visaSupportTest.close();
            allStats.add(new TestStats(
                visaSupportTest.getTestClassName(),
                visaSupportTest.getTotalTests(),
                visaSupportTest.getPassedTests(),
                visaSupportTest.getFailedTests()
            ));
        } catch (Exception e) {
            System.err.println("\u274c One or more tests failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n\uD83D\uDD1A Closing shared browser...");
            sharedDriver.quit();
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        printGlobalSummary(allStats);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("   ▶️ Total Duration: " + duration + " seconds");
    }

    private static void printGlobalSummary(List<TestStats> statsList) {
        int total = 0, passed = 0, failed = 0;
        System.out.println("\n================== TEST RESULTS SUMMARY ==================");
        System.out.printf("%-20s %7s | %7s | %7s\n", "TestClass", "Total", "Passed", "Failed");
        for (TestStats s : statsList) {
            System.out.printf("%-20s %7d | %7d | %7d\n", s.className, s.total, s.passed, s.failed);
            total += s.total; passed += s.passed; failed += s.failed;
        }
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-20s %7d | %7d | %7d\n", "TOTAL", total, passed, failed);
        System.out.println("==========================================================");
    }

    private static void runSelectedTests() {
        System.out.println("\n☑️ Select tests to run (comma-separated, e.g., 1,3,5):");
        System.out.print("Enter test numbers: ");
        String input = scanner.nextLine().trim();

        String[] selections = input.split(",");

        for (String selection : selections) {
            switch (selection.trim()) {
                case "1":
                    runHomePageTest();
                    break;
                case "2":
                    runUniversitiesFilterTest();
                    break;
                case "3":
                    runProgramsFilterTest();
                    break;
                case "4":
                    runBlogsTest();
                    break;
                case "5":
                    runVisaSupportTest();
                    break;
                default:
                    System.out.println("⚠️ Unknown test: " + selection);
            }
        }
    }
}
