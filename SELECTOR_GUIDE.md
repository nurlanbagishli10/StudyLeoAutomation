# 🎯 XPath və CSS Selector Bələdçisi

## 📋 İcmal

Bu sənəd StudyLeo saytında istifadə olunan element selector-ların (XPath və CSS) təkmilləşdirilmiş versiyasını təqdim edir.

---

## 🏠 Ana Səhifə (Home Page)

### Menu Navigation Buttons

| Element | CSS Selector | XPath | Relative XPath |
|---------|-------------|--------|----------------|
| Universities | `header nav ul li:nth-child(1) a` | `/html/body/header/div/nav/div/ul/li[1]/a` | `//header//nav//a[contains(text(), 'Universities')]` |
| Programs | `header nav ul li:nth-child(2) a` | `/html/body/header/div/nav/div/ul/li[2]/a` | `//header//nav//a[contains(text(), 'Programs')]` |
| Blogs | `header nav ul li:nth-child(3) a` | `/html/body/header/div/nav/div/ul/li[3]/a` | `//header//nav//a[contains(text(), 'Blogs')]` |
| Visa Support | `header nav ul li:nth-child(4) a` | `/html/body/header/div/nav/div/ul/li[4]/a` | `//header//nav//a[contains(text(), 'Visa')]` |
| About | `header nav ul li:nth-child(5) a` | `/html/body/header/div/nav/div/ul/li[5]/a` | `//header//nav//a[contains(text(), 'About')]` |
| Contact | `header nav ul li:nth-child(6) a` | `/html/body/header/div/nav/div/ul/li[6]/a` | `//header//nav//a[contains(text(), 'Contact')]` |

**Tövsiyə:** Link text istifadə edin:
```java
By.linkText("Universities")
By.partialLinkText("Univer")
```

---

## 🎓 Universitet List Səhifəsi

### URL-lər:
- Səhifə 1: `https://studyleo.com/en/universities`
- Səhifə 2: `https://studyleo.com/en/universities?page=2`
- Səhifə 3: `https://studyleo.com/en/universities?page=3`
- Səhifə 4: `https://studyleo.com/en/universities?page=4`

### Universitet Kartları

#### Səhifə 1 (Fərqli struktur):

| Element | CSS Selector | XPath | Relative XPath |
|---------|-------------|--------|----------------|
| Universitet 1 | `section div.university-card:nth-of-type(1) a` | `/html/body/div[3]/section/div/div/div[1]/div[1]/a/div[1]` | `//section//div[contains(@class, 'university')][1]//a` |
| Universitet 2 | `section div.university-card:nth-of-type(2) a` | `/html/body/div[3]/section/div/div/div[1]/div[2]/a/div[1]` | `//section//div[contains(@class, 'university')][2]//a` |
| Universitet N | `section div.university-card:nth-of-type(N) a` | `/html/body/div[3]/section/div/div/div[1]/div[N]/a/div[1]` | `//section//div[contains(@class, 'university')][N]//a` |

#### Səhifə 2-4:

| Element | CSS Selector | XPath | Relative XPath |
|---------|-------------|--------|----------------|
| Universitet 1 | `section div.university-card:nth-of-type(1) a` | `/html/body/div[3]/section/div/div/div[1]/div[1]/a` | `//section//div[contains(@class, 'university')][1]//a` |
| Universitet N | `section div.university-card:nth-of-type(N) a` | `/html/body/div[3]/section/div/div/div[1]/div[N]/a` | `//section//div[contains(@class, 'university')][N]//a` |

### Universitet Adları:

#### Səhifə 1:
```css
section div.university-card:nth-of-type(N) h3
```

```xpath
/html/body/div[3]/section/div/div/div[1]/div[N]/a/div[2]/h3
```

#### Səhifə 2-4:
```css
section div.university-card:nth-of-type(N) h3
```

```xpath
/html/body/div[3]/section/div/div/div[1]/div[N]/a//h3
```

---

## 🏛️ Universitet Detal Səhifəsi

### Əsas Buttonlar

#### 1. Admission Requirements

```css
section a[href*='admission']
section a[href*='requirements']
```

```xpath
/html/body/section[1]/div/div[2]/div[3]/a
```

```xpath (relative)
//section//a[contains(text(), 'Admission') or contains(text(), 'Requirements')]
```

---

#### 2. Apply Now (Modal Button)

```css
button[class*='apply']
button:contains('Apply')
```

```xpath
/html/body/section[1]/div/div[2]/div[3]/button
```

```xpath (relative)
//button[contains(text(), 'Apply')]
```

**Modal Close Button:**
```css
div[class*='modal'] button[class*='close']
button.close
```

```xpath
/html/body/div[7]/button
```

---

#### 3. Rankings - View More

```css
section[class*='ranking'] a
section a[href*='ranking']
```

```xpath
/html/body/section[2]/a
```

```xpath (relative)
//section[contains(@class, 'ranking')]//a[contains(text(), 'View More')]
```

---

#### 4. Programs - View More

```css
section[class*='program'] a
div a[href*='programs']
```

```xpath
/html/body/div[4]/section/div/div[3]/a
```

```xpath (relative)
//section[contains(@class, 'program')]//a[contains(text(), 'View More')]
```

---

#### 5. Admission Requirements - View More

```css
section[class*='admission'] a[href*='admission']
```

```xpath
/html/body/section[4]/a
```

```xpath (relative)
//section[contains(@class, 'admission')]//a[contains(text(), 'View More')]
```

---

#### 6. Galleries - View More

```css
section[class*='galler'] a
section a[href*='galler']
```

```xpath
/html/body/section[5]/div[2]/section/div[2]/a
```

```xpath (relative)
//section[contains(@class, 'galler')]//a[contains(text(), 'View More')]
```

---

#### 7. Dormitories - View More

```css
section[class*='dormitor'] a
section a[href*='dormitor']
```

```xpath
/html/body/section[6]/a
```

```xpath (relative)
//section[contains(@class, 'dormitor')]//a[contains(text(), 'View More')]
```

---

#### 8. International Students

```css
section a[href*='international']
```

```xpath
/html/body/section[7]/div[2]/div/a[1]
```

```xpath (relative)
//section//a[contains(text(), 'International')]
```

---

#### 9. Campuses - View More

**Əsas Button (2+ kampus varsa):**
```css
section[class*='campus'] a:not([class*='card'])
```

```xpath
/html/body/div[5]/section/a
```

```xpath (relative)
//section[contains(@class, 'campus')]//a[contains(text(), 'View More')]
```

**Kampus 1 - Individual:**
```css
section[class*='campus'] div:nth-of-type(1) a
```

```xpath
/html/body/div[5]/section/div[1]/div[1]/div[2]/a
```

**Kampus 2 - Individual:**
```css
section[class*='campus'] div:nth-of-type(2) a
```

```xpath
/html/body/div[5]/section/div[1]/div[2]/div[2]/a
```

---

#### 10. Transportation Options

```css
section a[href*='transport']
```

```xpath
/html/body/section[8]/div[1]/a
```

```xpath (relative)
//section//a[contains(text(), 'Transport')]
```

---

#### 11. Visa Support

```css
section a[href*='visa']
```

```xpath
/html/body/section[9]/div[2]/a
```

```xpath (relative)
//section//a[contains(text(), 'Visa')]
```

---

#### 12. FAQs - View More

```css
section[class*='faq'] a
section a[href*='faq']
```

```xpath
/html/body/section[10]/a
```

```xpath (relative)
//section[contains(@class, 'faq')]//a[contains(text(), 'View More')]
```

---

#### 13. Reviews - View More

```css
section[class*='review'] a
section a[href*='review']
```

```xpath
/html/body/section[11]/a
```

```xpath (relative)
//section[contains(@class, 'review')]//a[contains(text(), 'View More')]
```

---

## 🎨 CSS Selector Pattern-ları

### Attribute Selectors:

```css
/* Exact match */
a[href='https://studyleo.com']

/* Contains */
a[href*='university']
div[class*='modal']

/* Starts with */
a[href^='https://']
div[class^='btn-']

/* Ends with */
a[href$='.pdf']
img[src$='.jpg']

/* Multiple attributes */
input[type='text'][name='email']
```

### Pseudo-classes:

```css
/* First/Last child */
li:first-child
li:last-child

/* Nth child */
li:nth-child(2)
li:nth-child(odd)
li:nth-child(even)

/* Nth of type */
section:nth-of-type(3)
div:nth-of-type(2)

/* Not selector */
a:not(.disabled)
button:not([disabled])
```

### Combinators:

```css
/* Descendant */
header nav a

/* Child */
header > nav > ul > li

/* Adjacent sibling */
h1 + p

/* General sibling */
h1 ~ p
```

---

## 🔍 XPath Expressions

### Axes:

```xpath
/* Child */
//section/child::a

/* Descendant */
//section/descendant::a

/* Parent */
//a/parent::section

/* Following sibling */
//h1/following-sibling::p

/* Preceding sibling */
//p/preceding-sibling::h1

/* Ancestor */
//a/ancestor::section
```

### Functions:

```xpath
/* Text content */
//button[text()='Apply']
//a[contains(text(), 'University')]

/* Attribute */
//a[contains(@href, 'university')]
//div[starts-with(@class, 'btn-')]

/* Position */
//section[1]
//li[position()=2]
//div[last()]

/* Count */
//section[count(./a) > 0]
```

### Logical Operators:

```xpath
/* AND */
//a[contains(@href, 'university') and contains(@class, 'card')]

/* OR */
//button[text()='Apply' or text()='Submit']

/* NOT */
//a[not(contains(@class, 'disabled'))]
```

---

## 💡 Best Practices

### ✅ Tövsiyə edilir:

1. **CSS Selector-ları primary olaraq istifadə edin**
   ```java
   By.cssSelector("section a[href*='university']")
   ```

2. **ID və Class-ları prioritet verin**
   ```css
   #submit-button
   .university-card
   ```

3. **Relative XPath istifadə edin (Absolute yox)**
   ```xpath
   ✅ //section//a[contains(text(), 'University')]
   ❌ /html/body/div[3]/section/div/div/div[1]/div[1]/a
   ```

4. **Multiple locator strategiyası**
   ```java
   elementLocator.findElement(
       cssStrategy,
       xpathStrategy,
       relativeXPathStrategy
   );
   ```

### ❌ Tövsiyə edilmir:

1. **Absolute XPath**
   ```xpath
   ❌ /html/body/div[3]/section/div/div/div[1]/div[1]/a
   ```

2. **Index-based selector-lar (mümkün olduqca)**
   ```css
   ❌ div:nth-child(47) > div:nth-child(2) > a
   ```

3. **Hard-coded değerlər test kodda**
   ```java
   ❌ By.xpath("/html/body/section[1]/a")
   ✅ ButtonLocators.RANKINGS_VIEW_MORE
   ```

---

## 🔧 Debugging Tools

### Chrome DevTools:

1. **Elements tab açın** (F12)
2. **Ctrl+F ilə axtarış açın**
3. **CSS Selector və ya XPath yazın**

### Console-da test:

```javascript
// CSS Selector
$$("section a[href*='university']")

// XPath
$x("//section//a[contains(text(), 'University')]")

// Element sayı
$$("section a[href*='university']").length
```

---

## 📚 Əlavə Resurslar

- [CSS Selectors Reference](https://www.w3schools.com/cssref/css_selectors.asp)
- [XPath Tutorial](https://www.w3schools.com/xml/xpath_intro.asp)
- [Selenium Locators Guide](https://www.selenium.dev/documentation/webdriver/elements/locators/)
- [Chrome DevTools](https://developer.chrome.com/docs/devtools/)

---

**Son yeniləmə:** 19 Noyabr 2025
**Versiya:** 1.0.0
