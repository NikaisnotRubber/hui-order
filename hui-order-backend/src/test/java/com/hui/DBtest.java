package com.hui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DBtest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test(){
        Long aLong = jdbcTemplate.queryForObject("select count(*) from users", Long.class);
        System.out.println("========================");
        System.out.println(aLong);
    }
}
