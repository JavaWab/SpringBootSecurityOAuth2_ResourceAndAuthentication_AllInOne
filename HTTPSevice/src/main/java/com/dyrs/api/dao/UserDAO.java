package com.dyrs.api.dao;

import com.dyrs.api.entity.IMUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserDAO {
    @Value("${im.domain}")
    public String domain;
    @Value("${im.user.defaulte.icon}")
    public String defualte_icon;
    private final static String GET_USER_SQL = "SELECT username,name,email from ofUser WHERE username = ?";
    private final static String GET_USER_PROPS = "SELECT name,propValue FROM ofUserProp WHERE username = ?";


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public IMUser getUserInfo(final String username) {
        IMUser result_user = jdbcTemplate.execute(GET_USER_SQL, new PreparedStatementCallback<IMUser>() {
            @Override
            public IMUser doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    IMUser user = new IMUser();
                    user.setJid(username + "@" + domain);
                    user.setNick(rs.getString("name"));
                    user.setUsername(username);
                    return user;
                } else {
                    return null;
                }
            }
        });
        result_user.setIcon(getUserProps(username).getOrDefault("user.icon", defualte_icon).toString());
        return result_user;
    }

    public Map<String, Object> getUserProps(String username) {
        return jdbcTemplate.execute(GET_USER_PROPS, new PreparedStatementCallback<Map<String, Object>>() {

            @Override
            public Map<String, Object> doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                Map<String, Object> result = new HashMap<>();
                while (rs.next()) {
                    result.put(rs.getString("name"), rs.getString("propValue"));
                }
                return result;
            }
        });
    }
}
