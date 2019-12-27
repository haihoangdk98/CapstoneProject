package winter.findGuider.web.api;


import entities.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import security.AuthenProvider;
import security.UserService;
import services.Mail.MailService;
import services.account.AccountRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AccountControllerUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    AccountController accountController;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserService userService;
    @Mock
    AuthenProvider authenProvider;
    @Mock
    MailService mailService;
    @Mock
    HttpServletResponse response;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUserAccount() throws Exception {
        Account account = Mockito.mock(Account.class);
        Account acc = new Account();
        acc.setToken("123");
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ReflectionTestUtils.setField(accountController, "URL_ROOT_CLIENT_DOMAIN", "localhost");
        when(userService.registerNewUserAccount(account)).thenReturn(acc);
        ResponseEntity result = accountController.registerUserAccount(account, httpServletResponse);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }


    @Test
    public void testRegisterUserAccountWithException() {
        Account account = Mockito.mock(Account.class);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity result = accountController.registerUserAccount(account, httpServletResponse);
        Assert.assertEquals(500, result.getStatusCodeValue());
    }

    @Test
    public void testLogout() {
        ReflectionTestUtils.setField(accountController, "URL_ROOT_CLIENT_DOMAIN", "localhost");
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        accountController.logout(httpServletResponse);
    }

    @Test
    public void testFindAllCategory() throws Exception {
        when(accountRepository.findAllAccount()).thenThrow(Exception.class);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity<List<Account>> result = accountController.findAll(httpServletResponse);
        Assert.assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testResendEmailConfirmation() {
        ResponseEntity<Boolean> result = accountController.resendEmailConfirmation(1, response);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testResendEmailConfirmationWithException() {
        ReflectionTestUtils.setField(accountController, "mailService", null);
        ResponseEntity<Boolean> result = accountController.resendEmailConfirmation(1, response);
        Assert.assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testConfirmEmail() {
        ReflectionTestUtils.setField(accountController, "URL_ROOT_CLIENT", "localhost");
        ResponseEntity<Object> result = accountController.confirmEmail("sdsd");
        Assert.assertEquals(303, result.getStatusCodeValue());
    }

    @Test
    public void testConfirmEmailWithException() {
        //ReflectionTestUtils.setField(accountController, "mailService", null);
        ResponseEntity<Object> result = accountController.confirmEmail("asas");
        Assert.assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testChangePassword() throws Exception {
        Account model = new Account();
        model.setPassword("123@123a");

        //when(accountRepository.findAccountByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())).thenReturn(model);
        ResponseEntity<String> result = accountController.changePassword(model);
        Assert.assertEquals(500, result.getStatusCodeValue());
    }
}
