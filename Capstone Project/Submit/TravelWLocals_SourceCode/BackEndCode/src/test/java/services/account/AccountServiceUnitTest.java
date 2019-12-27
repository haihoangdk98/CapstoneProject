package services.account;

import entities.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import services.Mail.MailService;
import winter.findGuider.TestDataSourceConfig;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceUnitTest {
    @InjectMocks
    AccountRepository accountService;
    @Mock
    MailService mailService;
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    PasswordEncoder passwordEncoder;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        accountService = new AccountRepository(jdbcTemplate, mailService, passwordEncoder);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account (user_name, password, email ,role, email_verified) " +
                "values ('Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER',false)");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByName() throws Exception {
        Account acc = accountService.findAccountByName("Jacky");
        Assert.assertEquals("$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK", acc.getPassword());
        Assert.assertEquals("Jacky@gmail.com", acc.getEmail());
        Assert.assertEquals("GUIDER", acc.getRole());
    }

    @Test
    public void testAddAccount() throws Exception {
        Account account = new Account("test", "123", "test@test.com", "GUIDER");
        jdbcTemplate.update("delete from account where user_name = 'test'");
        int id = accountService.addAccount(account);
        Assert.assertEquals(accountService.findAccountByName("test").getId(), id);
    }

    @Test
    public void testGetEmail() throws Exception {
        jdbcTemplate.update("delete from account");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        Assert.assertEquals("Jacky@gmail.com", accountService.getEmail(1));
    }

    @Test
    public void findAllAccount() throws Exception {
        jdbcTemplate.update("delete from account");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (2,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (3,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        Assert.assertEquals(3, accountService.findAllAccount().size());
    }

    @Test
    public void findAccountNameByAccountId() throws Exception {
        jdbcTemplate.update("delete from account");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        Assert.assertEquals("Jacky", accountService.findAccountNameByAccountId(1));
    }

    @Test
    public void changePassword() throws Exception {
        accountService.changePassword("Jacky", "abc");
        Assert.assertEquals("abc", accountService.findAccountByName("Jacky").getPassword());
    }

    @Test
    public void isMailVerified() throws Exception {
        Assert.assertEquals(false, accountService.isMailVerified(accountService.findAccountByName("Jacky").getId()));
    }

    @Test
    public void insertEmailConfirmToken() throws Exception {
        Account jacky = accountService.findAccountByName("Jacky");
        String token = accountService.insertEmailConfirmToken(jacky.getId());
        String[] data = token.split("E", 2);
        Assert.assertEquals(String.valueOf(jacky.getId()), data[0]);
    }

    @Test
    public void confirmEmail() throws Exception {
        Account jacky = accountService.findAccountByName("Jacky");
        jdbcTemplate.update("update account set email_token = 'abcde' where account_id = ?", jacky.getId());
        String token = jacky.getId() + "Eabcde";
        accountService.confirmEmail(token);
        Assert.assertEquals(true, accountService.isMailVerified(jacky.getId()));
    }

    @Test
    public void confirmEmail2() throws Exception {
        Account jacky = accountService.findAccountByName("Jacky");
        jdbcTemplate.update("update account set email_token = 'abcde' where account_id = ?", jacky.getId());
        String token = jacky.getId() + "Eabcd";
        accountService.confirmEmail(token);
        Assert.assertEquals(false, accountService.isMailVerified(jacky.getId()));
    }

    @Test
    public void canGuiderLogin() {
        jdbcTemplate.update("delete from account");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        Assert.assertEquals(true, accountService.canGuiderLogin(1));
    }
}
