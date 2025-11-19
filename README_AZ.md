# 🎓 StudyLeo Test Automation - Azərbaycan Dilində

## 📋 Layihə Haqqında

Bu layihə StudyLeo (https://studyleo.com) veb saytının avtomatlaşdırılmış testlərini ehtiva edir.

## 🚀 Son Yeniliklər (v2.0.0)

### ✅ Əsas Təkmilləşdirmələr:

1. **Page Object Model (POM) Pattern** - Kod strukturu təkmilləşdirildi
2. **Multiple Locator Strategiyası** - CSS, XPath və Relative XPath dəstəyi
3. **ElementLocator Utility** - Robust element tapma mexanizmi
4. **Bug Fix-lər** - Timestamp parsing və digər xətalar düzəldildi
5. **Better Error Reporting** - Daha ətraflı xəta hesabatları
6. **Kod Təmizliyi** - JavaDoc, better naming, kod təkrarının azaldılması

Ətraflı məlumat: [IMPROVEMENTS.md](IMPROVEMENTS.md)

## 📁 Layihə Strukturu

```
studyleo-automation/
├── src/
│   ├── main/java/
│   │   ├── config/          # Konfiqurasiya class-ları
│   │   ├── model/           # Model class-ları
│   │   └── org/example/     # Main class
│   └── test/java/
│       ├── pages/           # ✅ Page Object Model (YENİ)
│       │   ├── BasePage.java
│       │   ├── HomePage.java
│       │   ├── UniversityListPage.java
│       │   └── UniversityDetailPage.java
│       ├── tests/           # Test class-ları
│       │   ├── AllMainPages.java
│       │   ├── universities.java
│       │   ├── UniversityButtonsFullTest.java
│       │   └── ImprovedUniversityTest.java  # ✅ YENİ
│       └── utils/           # Utility class-lar
│           ├── ElementLocator.java          # ✅ YENİ
│           ├── ExtentReportManager.java
│           ├── WaitHelper.java
│           └── EmailSender.java
├── pom.xml                  # Maven konfiqurasiyası
├── testng.xml              # TestNG konfiqurasiyası
├── IMPROVEMENTS.md         # ✅ Təkmilləşdirmələr sənədi (YENİ)
├── SELECTOR_GUIDE.md       # ✅ XPath/CSS selector bələdçisi (YENİ)
└── README_AZ.md            # ✅ Bu fayl (YENİ)
```

## 🔧 Texnologiyalar

- **Java 17**
- **Selenium WebDriver 4.15.0**
- **TestNG 7.8.0**
- **ExtentReports 5.1.1**
- **Maven 3.x**
- **Chrome WebDriver** (WebDriverManager ilə avtomatik idarəetmə)

## 📦 Quraşdırma

### Tələblər:
- Java 17 və ya daha yüksək
- Maven 3.x
- Chrome Browser

### Addımlar:

1. **Repository-ni klonlayın:**
```bash
git clone https://github.com/nurlanbagishli10/studyleo-automation.git
cd studyleo-automation
```

2. **Dependencies yükləyin:**
```bash
mvn clean install
```

3. **Konfiqurasiya edin (optional):**
`src/test/resources/config.properties` faylında timeout və digər parametrləri dəyişə bilərsiniz.

## 🎮 Testləri İşə Salma

### Bütün testləri işə sal:
```bash
mvn test
```

### Spesifik test class-ını işə sal:

#### Köhnə testlər:
```bash
mvn test -Dtest=AllMainPages
mvn test -Dtest=universities
mvn test -Dtest=UniversityButtonsFullTest
```

#### ✅ Yeni təkmilləşdirilmiş test:
```bash
mvn test -Dtest=ImprovedUniversityTest
```

### Headless mode ilə:
```bash
mvn test -Dheadless=true
```

### Normal mode ilə (browser görünəcək):
```bash
mvn test -Dheadless=false
```

## 📊 Test Hesabatları

Testlər işə düşdükdən sonra HTML hesabat yaradılır:

```
test-reports/
└── UniversityTest_Report_YYYYMMDD_HHMMSS.html
```

Hesabatı brauzer ilə açın və nəticələrə baxın.

## 🎯 XPath və CSS Selector Bələdçisi

Ətraflı selector bələdçisi üçün baxın: [SELECTOR_GUIDE.md](SELECTOR_GUIDE.md)

### Sürətli nümunələr:

#### Ana Səhifə Menu:
```css
/* CSS */
header nav ul li:nth-child(1) a

/* XPath */
//header//nav//a[contains(text(), 'Universities')]
```

#### Universitet Kartı:
```css
/* CSS */
section div.university-card:nth-of-type(1) a

/* XPath */
//section//div[contains(@class, 'university')][1]//a
```

## 🏗️ Yeni Kod İstifadəsi

### Page Object Model nümunəsi:

```java
// HomePage istifadə
HomePage homePage = new HomePage(driver);
homePage.open();
homePage.clickMenuButtonByName("Universities");

// UniversityListPage istifadə
UniversityListPage listPage = new UniversityListPage(driver);
listPage.openPage(2);  // Səhifə 2-yə get
listPage.clickUniversityCard(2, 1);  // 1-ci universitet

// UniversityDetailPage istifadə
UniversityDetailPage detailPage = new UniversityDetailPage(driver);
detailPage.clickButton("Apply Now");
detailPage.verifyPageLoaded();
```

### ElementLocator istifadəsi:

```java
ElementLocator locator = new ElementLocator(driver);

// Multiple strategiya ilə element tap
WebElement element = locator.findElement(
    LocatorStrategy.byCss("header nav a", "Menu Link (CSS)"),
    LocatorStrategy.byXPath("//header//nav//a", "Menu Link (XPath)"),
    LocatorStrategy.byLinkText("Universities", "Menu Link (Text)")
);

// Element mövcuddurmu yoxla
boolean exists = locator.isElementPresent(
    LocatorStrategy.byCss("button.apply", "Apply Button")
);
```

## 🐛 Problem Həll Etmə

### Maven build xətası:
```bash
mvn clean install -U
```

### ChromeDriver xətası:
WebDriverManager avtomatik driver yükləyir, internet bağlantısını yoxlayın.

### Test uğursuzluğu:
1. Headless mode-u söndürün: `-Dheadless=false`
2. Timeout-ları artırın: `config.properties`-də
3. Selector-ları yoxlayın: [SELECTOR_GUIDE.md](SELECTOR_GUIDE.md)

## 📈 Test Coverage

- ✅ Ana səhifə menu buttonları (6 button)
- ✅ Universitet list səhifələri (4 səhifə)
- ✅ Universitet detal səhifələri (41 universitet)
- ✅ Universitet daxili buttonlar (13-14 button hər universitet)

## 🤝 Töhfə Vermə

1. Fork edin
2. Feature branch yaradın (`git checkout -b feature/AmazingFeature`)
3. Commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Push edin (`git push origin feature/AmazingFeature`)
5. Pull Request açın

## 📝 Changelog

### v2.0.0 (19.11.2025)
- ✅ Page Object Model pattern tətbiqi
- ✅ ElementLocator utility class
- ✅ Multiple locator strategiyası
- ✅ CSS selector dəstəyi
- ✅ Timestamp parsing bug fix
- ✅ Better error reporting
- ✅ Code documentation
- ✅ IMPROVEMENTS.md və SELECTOR_GUIDE.md

### v1.0.0
- ✅ Əsas test strukturu
- ✅ ExtentReport inteqrasiyası
- ✅ Email sender funksionallığı

## 📞 Əlaqə

- **GitHub:** [nurlanbagishli10](https://github.com/nurlanbagishli10)
- **Layihə:** [studyleo-automation](https://github.com/nurlanbagishli10/studyleo-automation)

## 📄 Lisenziya

MIT License

---

**Müəllif:** StudyLeo QA Team  
**Son Yeniləmə:** 19 Noyabr 2025  
**Versiya:** 2.0.0
