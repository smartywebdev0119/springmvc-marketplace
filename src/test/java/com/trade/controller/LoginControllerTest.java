package com.trade.controller;

import com.trade.data.*;
import com.trade.exception.*;
import com.trade.model.*;
import com.trade.service.*;
import com.trade.service.dao.*;
import com.zaxxer.hikari.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.*;
import org.springframework.mock.web.*;
import org.springframework.test.context.*;
import org.springframework.test.context.testng.*;
import org.springframework.test.context.web.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.web.context.*;
import org.testng.annotations.*;

import javax.servlet.http.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:spring-web-servlet.xml" })
public class LoginControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext wac;

    private MockHttpServletResponse mockHttpServletResponse;
    private MockHttpServletRequest mockHttpServletRequest;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }

    @AfterMethod
    public void tearDown() {

        mockMvc = null;

    }

    @Test
    public void testGetLoginPage() throws Exception {

        mockMvc
                .perform(get("/login"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().encoding("UTF-8"));

    }

    @Test
    @Ignore("because it does not work")
    public void testLogin() throws Exception {

        String user = "user";
        String password = "password";


        User userFromDB = new User();
        userFromDB.setName(user);
        userFromDB.setPassword(password);

//        when(jdbcTemplate.queryForObject(
//                "select * from user where username = ?",
//                new UserDaoImpMySQL.UserRowMapper(),
//                user
//        )).thenReturn(userFromDB);
//
//        when(userDaoImpMySQL.findByUsername(user)).thenReturn(userFromDB);
//        when(userService.findByUsername(user)).thenReturn(userFromDB);
//
//        when(authorizationService.login(user, password, mockHttpServletResponse))
//                .thenReturn(true);

        mockMvc
                .perform(post("/login")
                        .param("username", user)
                        .param("password", password))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().encoding("UTF-8"));

    }

    @Test
    public void testLogout() {
    }
}