package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyAccessor extends DatabaseAccessor {
    public CompanyAccessor() throws SQLException {
        super();
    }

    public boolean checkCompanyByName(String name) throws SQLException {
        //Returns true if the given company exists in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM companies WHERE company_name=?");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            int total = 0;
            if (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
        }
    }

    public void addCompany(String name, String ownerUuid) throws SQLException {
        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO companies (company_name, owner_uuid) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setString(2, ownerUuid);
            statement.executeUpdate();
        }
    }
}
