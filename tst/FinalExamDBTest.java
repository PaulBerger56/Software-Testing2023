import org.junit.Assert;
import org.junit.Test;

import java.sql.*;

public class FinalExamDBTest {

    @Test
    public void testNumOfVehicles() throws SQLException {
        int count = 0;
        Connection c = DriverManager.getConnection("jdbc:sqlite:test.db");

        String sql = "SELECT COUNT(*) FROM vehicles WHERE make = 'Ford';";
        PreparedStatement preparedStatement = c.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            count = resultSet.getInt(1);
        }

        Assert.assertEquals(2, count);
    }
}
