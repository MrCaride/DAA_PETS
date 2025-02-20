package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.daa.entities.Pet;

public class PetDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(PetDAO.class.getName());

    public Pet get(int id) throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM pets WHERE pet_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid pet id");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting a pet", e);
            throw new DAOException(e);
        }
    }

    public List<Pet> list() throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM pets";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<Pet> pets = new LinkedList<>();

                    while (result.next()) {
                        pets.add(rowToEntity(result));
                    }
                    return pets;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            throw new DAOException(e);
        }
    }

    public Pet add(String name, String type, int ownerId) throws DAOException, IllegalArgumentException {
        if (name == null || type == null) {
            throw new IllegalArgumentException("name and type can't be null");
        }

        try (Connection conn = this.getConnection()) {

            // final String ownerCheckQuery = "SELECT id FROM people WHERE id = ?";
            // try (PreparedStatement ownerCheckStmt = conn.prepareStatement(ownerCheckQuery)) {
            //     ownerCheckStmt.setInt(1, Pet.getOwnerId());
            //     try (ResultSet ownerResult = ownerCheckStmt.executeQuery()) {
            //         if (!ownerResult.next()) {
            //             throw new IllegalArgumentException("Owner ID does not exist");
            //         }
            //     }
            // }


            final String query = "INSERT INTO pets VALUES(null, ?, ?, ?)";

            try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(2, type);
                statement.setInt(3, ownerId);

                if (statement.executeUpdate() == 1) {
                    try (ResultSet resultKeys = statement.getGeneratedKeys()) {
                        if (resultKeys.next()) {
                            return new Pet(resultKeys.getInt(1), name, type, ownerId);
                        } else {
                            throw new SQLException("Error retrieving inserted pet id");
                        }
                    }
                } else {
                    throw new SQLException("Error inserting pet");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding a pet", e);
            throw new DAOException(e);
        }
    }

    public void modify(Pet pet) throws DAOException, IllegalArgumentException {
        if (pet == null) {
            throw new IllegalArgumentException("pet can't be null");
        }

        try (Connection conn = this.getConnection()) {
            final String query = "UPDATE pets SET name=?, type=?, owner_id=? WHERE pet_id=?";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, pet.getName());
                statement.setString(2, pet.getType());
                statement.setInt(3, pet.getOwnerId());
                statement.setInt(4, pet.getId());

                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Error updating pet");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);
            throw new DAOException(e);
        }
    }

    public void delete(int id) throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
            final String query = "DELETE FROM pets WHERE pet_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid pet id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a pet", e);
            throw new DAOException(e);
        }
    }

    private Pet rowToEntity(ResultSet row) throws SQLException {
        return new Pet(
            row.getInt("pet_id"),
            row.getString("name"),
            row.getString("type"),
            row.getInt("owner_id")
        );
    }
}
