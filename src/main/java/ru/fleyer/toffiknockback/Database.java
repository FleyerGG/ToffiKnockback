package ru.fleyer.toffiknockback;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public static Database INSTANCE = new Database();
    FileConfiguration cfg = ToffiKnockback.getInstance().getConfig();
    public String ADD_FRIEND = "INSERT INTO " + cfg.getString("mysql.table") + "(kbowner, kbfriend) VALUES (?,?)";
    public String GET_FRIENDS = "SELECT * FROM `test`.`toffiknockback1` WHERE kbowner=?";
    public String DELETE_FRIEND = "DELETE FROM " + cfg.getString("mysql.table") + " WHERE kbowner =? AND kbfriend=?;";
    public String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + cfg.getString("mysql.table") + "(`id` INT NOT NULL AUTO_INCREMENT, `kbowner` VARCHAR(16) NOT NULL , `kbfriend` VARCHAR(16) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

    public void createTable() {
        try (Connection connection = ToffiKnockback.getInstance().getHikari().getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }
    public static PreparedStatement prepareStatement(Connection connection, String sql, PreparedStatementSetter setter) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        setter.setValues(ps);
        setter.setValues(ps);
        return ps;
    }

    public void addFriend(String kbowner, String kbfriend) {

        Bukkit.getScheduler().runTaskAsynchronously(ToffiKnockback.getInstance(), () -> {
            try (Connection connection = ToffiKnockback.getInstance().getHikari().getConnection();
                 PreparedStatement statement = connection.prepareStatement(ADD_FRIEND)) {

                statement.setString(1, kbowner);
                statement.setString(2, kbfriend);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public ArrayList<String> getURL(String kbownerr) {
        ArrayList<String> urllist = new ArrayList<>();
        try (Connection connection = ToffiKnockback.getInstance().getHikari().getConnection();
             PreparedStatement statement = prepareStatement(connection,GET_FRIENDS,ps -> ps.setString(1,kbownerr));
             ResultSet resultSet = statement.executeQuery()) {

           // statement.setString(1,kbownerr);


            while (resultSet.next()) {
                urllist.add(resultSet.getString("kbfriend"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return urllist;
    }

    public void removefriend(String kbowner, String kbfriend) {
        Bukkit.getScheduler().runTaskAsynchronously(ToffiKnockback.getInstance(), () -> {
            try (Connection connection = ToffiKnockback.getInstance().getHikari().getConnection();
                 PreparedStatement statement = connection.prepareStatement(DELETE_FRIEND)) {
                statement.setString(1, kbowner);
                statement.setString(2, kbfriend);
                statement.executeUpdate();

            }
             catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }


}
