# 🚀 StudyLeo Automation

StudyLeo.com veb-saytı üçün avtomatlaşdırılmış test paketi. Bu layihə Selenium WebDriver istifadə edərək StudyLeo platformasının əsas funksiyalarını test edir.

## 📋 Xüsusiyyətlər

Bu test paketi aşağıdakı səhifələri və funksiyaları test edir:

### 🏠 Ana Səhifə Testləri (HomePageTest)
- Cookie banner qəbulu
- "Apply Now" düyməsi
- Dialoq pəncərəsinin bağlanması
- Axtarış qutusu
- Axtarış düyməsi
- WhatsApp dəstək düyməsi
- Timer düyməsi
- Naviqasiya linklərinin yoxlanması (Universities, Programs, Blogs, Visa Support, About, Contact)

### 🎓 Universitetlər Testləri (UniversitiesTest)
- Axtarış qutusu funksionallığı
- QS Reytinqi filtri
- Yataqxana filtri
- Müraciət etmək filtri
- Şəhərlər dropdown-u
- Fakültələr dropdown-u
- Proqramlar dropdown-u
- Dərəcə növləri dropdown-u
- Sıralama dropdown-u

### 📚 Proqramlar Testləri (ProgramsFilterTest)
- Proqram axtarışı
- Universitet filtri
- Fakültə filtri
- Dərəcə növü filtri
- Təhsil dili filtri
- Təqaüd filtri
- Sıralama funksiyası

### 📝 Bloqlar Testləri (BlogsTest)
- Bloq axtarışı
- Kateqoriya filtri

### 🛂 Viza Dəstəyi Testləri (VisaSupportTest)
- Ölkə axtarışı
- Viza məlumatlarının yoxlanması

## 🛠️ Texnologiyalar

- **Java 21** - Proqramlaşdırma dili
- **Selenium WebDriver 4.15.0** - Veb avtomatlaşdırma
- **WebDriverManager 5.6.0** - Browser driver idarəetməsi
- **Maven** - Layihə idarəetməsi və asılılıqlar

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
[A] ▶️  Run ALL tests
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
studyleo-automation/
├── pom.xml                          # Maven konfiqurasiya faylı
├── src/
│   └── test/
│       └── java/
│           ├── TestRunner.java          # İnteraktiv test runner
│           ├── HomePageTest.java        # Ana səhifə testləri
│           ├── UniversitiesTest.java    # Universitetlər səhifəsi testləri
│           ├── ProgramsFilterTest.java  # Proqramlar səhifəsi testləri
│           ├── BlogsTest.java           # Bloqlar səhifəsi testləri
│           └── VisaSupportTest.java     # Viza dəstəyi səhifəsi testləri
├── logs/                            # Test log faylları
└── screenshots/                     # Test zamanı çəkilən ekran şəkilləri
```

## 📊 Test Nəticələri

Hər test işə salındıqda:
- **Logs:** `logs/` qovluğunda saxlanılır (məsələn: `HomePageTest_2026-02-03_14-30-45.txt`)
- **Screenshots:** `screenshots/` qovluğunda xəta baş verdikdə ekran şəkilləri saxlanılır

## 🔧 Konfiqurasiya

Testlər `https://studyleo.com/en` URL-i üzərində işləyir. URL-i dəyişmək üçün hər test faylındakı `SITE_URL` konstantını yeniləyin.

## 👤 Müəllif

**Nurlan Bağışlı**

## 📄 Lisenziya

Bu layihə açıq mənbəlidir və şəxsi istifadə üçün nəzərdə tutulub.
