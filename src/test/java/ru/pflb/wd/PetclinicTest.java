package ru.pflb.wd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.sun.xml.internal.ws.util.StringUtils.capitalize;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicTest {

    private WebDriver driver;

    @Before
    public void initDriver() throws URISyntaxException, IOException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        File firebug = new File(FirefoxDriver.class.getResource("/firebug-1.12.7-fx.xpi").toURI());
        File firepath = new File(FirefoxDriver.class.getResource("/firepath-0.9.7-fx.xpi").toURI());
        profile.addExtension(firebug);
        profile.addExtension(firepath);
        profile.setPreference("extensions.firebug.showFirstRunPage", false);
        capabilities.setCapability(PROFILE, profile);
        driver = new FirefoxDriver(capabilities);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3, SECONDS);
    }

    @After
    public void closeDriver() {
        driver.quit();
    }

    @Test
    public void shouldFindOwnerAndChangeHisName() {

        // открытие браузера на нужной странице
        driver.get("http://localhost:8080");

        // клик по меню Find Owners
        driver.findElement(By.xpath("//a[@href='/owners/find']")).click();

        // ввод в поле поиска фамилии хозяина животного
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("Black");

        // клик по кнопке Find Owner
        driver.findElement(By.xpath("//button[starts-with(text(), 'Find')]")).click();

        // клик по кнопке Edit Owner
        driver.findElement(By.xpath("//h2/following-sibling::a[starts-with(text(), 'Edit')]")).click();

        // ввод произвольного значения длиной в 6 символов в поле имя
        String randomName = capitalize(randomAlphabetic(6));
        WebElement firstNameEdit = driver.findElement(By.xpath("//input[@id='firstName']"));
        firstNameEdit.clear();
        firstNameEdit.sendKeys(randomName);

        // клик по кнопке Update Owner
        driver.findElement(By.xpath("//button[.='Update Owner']")).click();

        // клик по меню Find Owners
        driver.findElement(By.xpath("//a[@href='/owners/find']")).click();

        // ввод в поле поиска фамилии хозяина животного
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("Black");

        // клик по кнопке Find Owner
        driver.findElement(By.xpath("//button[starts-with(text(), 'Find')]")).click();

        // считывание имени и фамилии
        String fullName = driver.findElement(By.xpath("//th[.='Name']/following-sibling::td/b")).getText();

        assertThat(fullName).describedAs("Имя пользователя не было изменено").startsWith(randomName);
    }

    @Test
    public void shouldAddNewPet() {

        // открытие браузера на нужной странице
        driver.get("http://localhost:8080");

        // клик по меню Find Owners
        driver.findElement(By.xpath("//a[@href='/owners/find']")).click();

        // ввод в поле поиска фамилии хозяина животного
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("Black");

        // клик по кнопке Find Owner
        driver.findElement(By.xpath("//button[starts-with(text(), 'Find')]")).click();

        // клик по кнопке Add new pet
        driver.findElement(By.xpath("//a[contains(@href, 'pets/new')]")).click();

        // ввод произвольной клички от 4 до 8 символов
        String randomPetName = capitalize(randomAlphabetic(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='name']")).sendKeys(randomPetName);

        // ввод даты рождения - неделя назад
        String petBirthDate = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        driver.findElement(By.xpath("//input[@id='birthDate']")).sendKeys(petBirthDate);

        // выбор из выпадающего списка - lizard
        new Select(driver.findElement(By.xpath("//select[@id='type']"))).selectByVisibleText("lizard");

        // клик по кнопке Update pet
        driver.findElement(By.xpath("//button[.='Update Pet']")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//h2[.='Pets and Visits']/following-sibling::table/tbody/tr"));

        // выставляю нулевое ожидание, чтобы не ждать подолгу на неподходящих строках
        driver.manage().timeouts().implicitlyWait(0, SECONDS);
        // итерация по всем строкам
        for (WebElement row : rows) {
            if (!row.findElements(By.xpath("//dd[.='" + randomPetName + "']")).isEmpty()) {
                // формат даты немного другой
                String formattedBirthDate = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (!row.findElements(By.xpath("//dd[.='" + formattedBirthDate + "']")).isEmpty()) {
                    if (!row.findElements(By.xpath("//dd[.='lizard']")).isEmpty()) {
                        // нашли искомую
                        return;
                    }
                }
            }
        }
        // не нашли нужную строку
        throw new IllegalStateException("Не найдено ни одной строки с требуемыми значениями");
    }

    /**
     * Домашнее задание.
     * <p>
     * Сценарий:<ol>
     * <li>Открыть http://localhost:8080/</li>
     * <li>Перейти в меню Find Owners -> Add Owner</li>
     * <li>Ввести валидные случайные данные (новые для каждого запуска теста)</li>
     * <li>Нажать Add Owner, открылась страница Owner Information</li>
     * <li>Проверить, что добавилась новая запись, и все ее поля соответствуют введенным значениям, использую поиск в Find Owners</li>
     * </ul>
     */
    @Test
    public void shouldValidateAddedUser() {

        driver.get("http://localhost:8080");

        driver.findElement(By.xpath("//a[@href='/owners/find']")).click();

        driver.findElement(By.xpath("//a[@href='/owners/new']")).click();

        String randomFirstName = capitalize(randomAlphabetic(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='firstName']")).sendKeys(randomFirstName);

        String randomLastName = capitalize(randomAlphabetic(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys(randomLastName);

        String randomAddress = capitalize(randomAlphabetic(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='address']")).sendKeys(randomAddress);

        String randomCity = capitalize(randomAlphabetic(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='city']")).sendKeys(randomCity);

        String randomPhone = capitalize(randomNumeric(4 + new Random().nextInt(5)));
        driver.findElement(By.xpath("//input[@id='telephone']")).sendKeys(randomPhone);

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        driver.findElement(By.xpath("//a[@href='/owners/find']")).click();

        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys(randomLastName);

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        String Name = driver.findElement(By.xpath("//th[.='Name']/following-sibling::td/b")).getText();

        assertThat(Name).describedAs("Имя не было изменено").startsWith(randomFirstName+" "+randomLastName);

        String Address = driver.findElement(By.xpath("//th[.='Address']/following-sibling::td")).getText();

        assertThat(Address).describedAs("Адрес не был изменен").startsWith(randomAddress);

        String City = driver.findElement(By.xpath("//th[.='City']/following-sibling::td")).getText();

        assertThat(City).describedAs("Город не был изменен").startsWith(randomCity);

        String Phone = driver.findElement(By.xpath("//th[.='Telephone']/following-sibling::td")).getText();

        assertThat(Phone).describedAs("Телефон не был изменен").startsWith(randomPhone);
    }
}
