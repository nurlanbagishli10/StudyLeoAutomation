# 🚀 StudyLeo Automation

StudyLeo.com veb-saytı üçün avtomatlaşdırılmış test paketi. Bu layihə Selenium WebDriver istifadə edərək StudyLeo platformasının əsas funksiyalarını test edir.

## 📋 Xüsusiyyətlər

Bu test paketi aşağıdakı səhifələri və funksiyaları test edir:

### 🏠 Ana Səhifə Testləri (HomePageTest)
- Cookie banner qəbulu
- "Apply Now" düyməsi
- Dialoq pəncərəsinin bağlanması
- Axtarış qutusu və düyməsi
- WhatsApp dəstək düyməsi
- Timer düyməsi
- Naviqasiya linklərinin yoxlanması (Universities, Programs, Blogs, Visa Support, About, Contact)

### 🎓 Universitetlər Testləri (UniversitiesTest)
- Axtarış qutusu funksionallığı
- QS Reytinqi filtri
- Yataqxana filtri
- Müraciət etmək filtri
- Şəhərlər, fakültələr, proqramlar, dərəcə növləri dropdown-ları
- Sıralama dropdown-u

### 📚 Proqramlar Testləri (ProgramsFilterTest)
- Proqram axtarışı
- Universitet, fakültə, dərəcə növü, təhsil dili, təqaüd filtrləri
- Sıralama funksiyası

### 📝 Bloqlar Testləri (BlogsTest)
- Bloq axtarışı
- Kateqoriya filtri
- Pagination və blog linklərinin yoxlanması

### 🛂 Viza Dəstəyi Testləri (VisaSupportTest)
- Ölkə axtarışı
- Viza məlumatlarının yoxlanması
- Pagination və per-page funksiyası

## 🛠️ Texnologiyalar

- **Java 21**
- **Selenium WebDriver 4.15.0**
- **WebDriverManager 5.6.0**
- **Maven**
- **Google Chrome**

## 📦 Quraşdırma

### Tələblər

- Java JDK 21 və ya daha yuxarı
- Maven 3.6+
- Google Chrome brauzeri

### Addımlar

1. **Reponu klonlayın:**
   ```bash
   git clone https://github.com/nurlanbagishli10/studyleo-automation.git
   cd studyleo-automation
   ```

2. **Asılılıqları yükləyin:**
   ```bash
   mvn clean install
   ```

## 🚀 İstifadə

### Test Runner ilə İşə Salmaq

Bütün testləri interaktiv menyudan idarə etmək üçün:

```bash
mvn compile exec:java -Dexec.mainClass="TestRunner"
```

Test Runner menyusu:
```
[1] 🏠 HomePageTest
[2] 🎓 UniversitiesFilterTest
[3] 📚 ProgramsFilterTest
[4] 📝 BlogsTest
[5] 🛂 VisaSupportTest
[A] ▶️  Run ALL tests (browser paylaşılır)
[S] ☑️  Select multiple tests
[Q] 🚪 Quit
```

### Fərdi Testləri İşə Salmaq

```bash
# Ana səhifə testləri
mvn compile exec:java -Dexec.mainClass="HomePageTest"

# Universitetlər testləri
mvn compile exec:java -Dexec.mainClass="UniversitiesTest"

# Proqramlar testləri
mvn compile exec:java -Dexec.mainClass="ProgramsFilterTest"

# Bloqlar testləri
mvn compile exec:java -Dexec.mainClass="BlogsTest"

# Viza dəstəyi testləri
mvn compile exec:java -Dexec.mainClass="VisaSupportTest"
```

## 📁 Layihə Strukturu

```
StudyleoPreTestAuto/
├── pom.xml
├── src/
│   └── test/
│       ├── TestRunner.java
│       ├── HomePageTest.java
│       ├── UniversitiesTest.java
│       ├── ProgramsFilterTest.java
│       ├── BlogsTest.java
│       └── VisaSupportTest.java
├── logs/
└── screenshots/
```

## 📊 Test Nəticələri və Global Summary

Hər test işə salındıqda:
- **Logs:** `logs/` qovluğunda saxlanılır (məsələn: `HomePageTest_2026-02-25_14-09-37.txt`)
- **Screenshots:** `screenshots/` qovluğunda xəta baş verdikdə ekran şəkilləri saxlanılır

Bütün testlər bitdikdən sonra aşağıdakı formatda global summary çıxır:
```
================== TEST RESULTS SUMMARY ==================
TestClass           Total  |  Passed |  Failed
HomePageTest        10     |   9     |   1
BlogsTest           4      |   3     |   1
ProgramsFilterTest  8      |   8     |   0
VisaSupportTest     6      |   5     |   1
----------------------------------------------------------
TOTAL               28     |  25     |   3
==========================================================
```

## 🔧 Konfiqurasiya

Testlər `https://studyleo.com/en` URL-i üzərində işləyir. URL-i dəyişmək üçün hər test faylındakı `SITE_URL` konstantını yeniləyin.

## 👤 Müəllif

**Nurlan Bağışlı**

## 📄 Lisenziya

Bu layihə açıq mənbəlidir və şəxsi istifadə üçün nəzərdə tutulub.
