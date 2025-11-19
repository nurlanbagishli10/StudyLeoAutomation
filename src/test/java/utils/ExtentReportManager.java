package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static String reportPath;

    /**
     * ExtentReports-u başladır
     */
    public static ExtentReports createInstance() {
        if (extent == null) {
            // Report faylının adı və yolu
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportName = "UniversityTest_Report_" + timestamp + ".html";
            reportPath = System.getProperty("user.dir") + "/test-reports/" + reportName;

            // Reports qovluğunu yarat
            File reportsDir = new File(System.getProperty("user.dir") + "/test-reports/");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            // Spark Reporter konfiqurasiyası
            sparkReporter = new ExtentSparkReporter(reportPath);

            // Report görünüşü
            sparkReporter.config().setDocumentTitle("StudyLeo Universitet Test Hesabatı");
            sparkReporter.config().setReportName("Universitet Səhifələri Test Nəticələri");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("dd MMM yyyy, HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");

            // CSS customization - HƏR ŞEYİ AĞ ET
            sparkReporter.config().setCss(
                    // Ümumi mətn rəngləri
                    "body { color: #ffffff !important; }" +
                            ".test-content { font-family: 'Segoe UI', Arial, sans-serif; color: #ffffff !important; }" +

                            // Test adları (Səhifə 1, Səhifə 2, Universitet 1, və s.)
                            ".test-name { color: #ffffff !important; font-weight: 600 !important; }" +
                            ".node-name { color: #ffffff !important; font-weight: 600 !important; }" +

                            // Test başlıqları
                            ".test-name-node { color: #ffffff !important; }" +
                            ".collapsible-header { color: #ffffff !important; }" +

                            // Test məlumatları
                            ".test-detail { color: #ffffff !important; }" +
                            ".test-attributes { color: #ffffff !important; }" +

                            // Log mesajları
                            ".step-details { color: #ffffff !important; }" +
                            ".log { color: #ffffff !important; }" +

                            // Statistika mətnləri
                            ".panel-body { color: #ffffff !important; }" +
                            ".test-status { color: #ffffff !important; }" +

                            // Cədvəl mətnləri
                            "table { color: #ffffff !important; }" +
                            "td { color: #ffffff !important; }" +
                            "th { color: #ffffff !important; }" +

                            // Node və accordion başlıqları
                            ".accordion-header { color: #ffffff !important; }" +
                            ".card-header { color: #ffffff !important; }" +

                            // Digər elementlər
                            ".badge { border-radius: 4px; padding: 4px 8px; }" +
                            ".collapsible-header { cursor: pointer; background: #2c3e50; padding: 10px; border-radius: 5px; color: #ffffff !important; }" +

                            // Info və description mətnləri
                            ".test-desc { color: #ffffff !important; }" +
                            ".info { color: #ffffff !important; }" +

                            // Sistem məlumatları
                            ".system-info { color: #ffffff !important; }" +
                            ".card { color: #ffffff !important; }" +

                            // Timeline və digər bölmələr
                            ".timeline { color: #ffffff !important; }" +
                            ".timeline-content { color: #ffffff !important; }" +

                            // Span və p elementləri
                            "span { color: #ffffff !important; }" +
                            "p { color: #ffffff !important; }" +
                            "div { color: #ffffff !important; }" +
                            "label { color: #ffffff !important; }" +

                            // Link rəngləri (ağ amma hover-də göy)
                            "a { color: #3498db !important; text-decoration: none !important; }" +
                            "a:hover { color: #5dade2 !important; text-decoration: underline !important; }"
            );

            // JavaScript - Xəta loglarını collapse/expand
            sparkReporter.config().setJs(
                    "document.addEventListener('DOMContentLoaded', function() {" +
                            "  document.querySelectorAll('.error-details-btn').forEach(btn => {" +
                            "    btn.addEventListener('click', function() {" +
                            "      const details = this.nextElementSibling;" +
                            "      if (details.style.display === 'none') {" +
                            "        details.style.display = 'block';" +
                            "        this.textContent = '▼ Detayları Gizlə';" +
                            "      } else {" +
                            "        details.style.display = 'none';" +
                            "        this.textContent = '▶ Xəta Detaylarını Göstər';" +
                            "      }" +
                            "    });" +
                            "  });" +
                            "});"
            );

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Sistem məlumatları
            extent.setSystemInfo("Tester", "StudyLeo QA Team");
            extent.setSystemInfo("Test Mühiti", "Production");
            extent.setSystemInfo("Website", "https://studyleo.com");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", "Chrome (Headless dəstəkli)");
        }

        return extent;
    }

    /**
     * Report-u bitir və saxla
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n📊 ExtentReport yaradıldı:");
            System.out.println("   📁 Fayl: " + reportPath);
            System.out.println("   🔗 Faylı brauzer ilə açın\n");
        }
    }

    /**
     * Test uğurlu mesajı
     */
    public static void logPass(ExtentTest test, String message) {
        test.pass("<span style='color: #ffffff !important;'>✅ " + message + "</span>");
    }

    /**
     * Test uğursuz mesajı
     */
    public static void logFail(ExtentTest test, String message) {
        test.fail("<span style='color: #ffffff !important;'>❌ " + message + "</span>");
    }

    /**
     * Xəta detayları - Button ilə açılacaq
     */
    public static void logFailWithDetails(ExtentTest test, String mainMessage, String errorDetails) {
        String html = "<div style='margin: 10px 0;'>" +
                "<div class='error-details-btn' style='background: #e74c3c; color: #ffffff !important; " +
                "padding: 8px 15px; border-radius: 4px; cursor: pointer; display: inline-block; " +
                "user-select: none; font-weight: 600;'>▶ Xəta Detaylarını Göstər</div>" +
                "<div style='display: none; margin-top: 10px; padding: 15px; " +
                "background: #2c3e50; border-left: 4px solid #e74c3c; border-radius: 4px; " +
                "font-family: monospace; white-space: pre-wrap; color: #ffffff !important;'>" +
                errorDetails +
                "</div></div>";

        test.fail("<span style='color: #ffffff !important;'>❌ " + mainMessage + "</span>" + html);
    }

    /**
     * İnfo mesajı
     */
    public static void logInfo(ExtentTest test, String message) {
        test.info("<span style='color: #ffffff !important;'>ℹ️ " + message + "</span>");
    }

    /**
     * Warning mesajı
     */
    public static void logWarning(ExtentTest test, String message) {
        test.warning("<span style='color: #ffffff !important;'>⚠️ " + message + "</span>");
    }
}