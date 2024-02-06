package com.dnamaster10.tcgui.util.database;

import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;

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
    public boolean checkIfOwner(String companyName, String uuid) throws SQLException {
        //Checks if a player owns a company
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM companies WHERE company_name=? AND owner_uuid=?");
            statement.setString(1, companyName);
            statement.setString(2, uuid);
            ResultSet result = statement.executeQuery();
            int total = 0;
            if (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
        }
    }
    public boolean checkIfMember(int companyId, String uuid) throws SQLException {
        //Returns true if the given player is a member of the given company
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM companymembers WHERE company_id=? AND member_uuid=?");
            statement.setInt(1, companyId);
            statement.setString(2, uuid);
            ResultSet result = statement.executeQuery();
            int total = 0;
            if (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
        }
    }
    public Integer getCompanyIdByName(String companyName) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM companies WHERE company_name=?");
            statement.setString(1, companyName);
            ResultSet resultSet = statement.executeQuery();
            Integer companyId = null;
            if (resultSet.next()) {
                companyId = resultSet.getInt("id");
            }
            return companyId;
        }
    }
    public void addCompany(String name, String ownerUuid) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO companies (company_name, owner_uuid) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setString(2, ownerUuid);
            statement.executeUpdate();
        }
    }
    public void addMember(PlayerDatabaseObject player, String companyName) throws SQLException {
        //Adds a new member to the given company
        try (Connection connection = getConnection()) {
            statement = connection.prepareStatement("INSERT INTO companymembers (company_id, member_uuid) VALUES (?, ?)");
        }
    }
    public void deleteCompanyByName(String companyName) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM companies WHERE company_name=?");
            statement.setString(1, companyName);
            statement.executeUpdate();
        }
    }
    public void deleteCompanyById(int id) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM companies WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
