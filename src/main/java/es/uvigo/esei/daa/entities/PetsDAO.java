package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.daa.entities.Pet;

/**
 * DAO class for the {@link Pet} entities.
 * 
 * @author DRM
 */
public class PetsDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(PetsDAO.class.getName());

    private boolean ownerExists(int ownerId) throws SQLException {
        try (Connection conn = this.getConnection()) {
            String query = "SELECT 1 FROM people WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, ownerId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        }
    }

    public Pet get(String petId) throws DAOException {
        try (Connection conn = this.getConnection()) {
            String query = "SELECT * FROM pets WHERE pet_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, petId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid pet ID");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting a pet", e);
            throw new DAOException(e);
        }
    }

    public List<Pet> list() throws DAOException {
        try (Connection conn = this.getConnection()) {
            String query = "SELECT * FROM pets";
            try (PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet result = statement.executeQuery()) {
                List<Pet> pets = new LinkedList<>();
                while (result.next()) {
                    pets.add(rowToEntity(result));
                }
                return pets;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            throw new DAOException(e);
        }
    }

    public Pet add(String petId, String name, String type, int ownerId) throws DAOException {
        if (petId == null || name == null || type == null) {
            throw new IllegalArgumentException("petId, name, and type can't be null");
        }

        try (Connection conn = this.getConnection()) {
            if (!ownerExists(ownerId)) {
                throw new IllegalArgumentException("Owner ID does not exist");
            }
            
            String query = "INSERT INTO pets (pet_id, name, type, owner_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, petId);
                statement.setString(2, name);
                statement.setString(3, type);
                statement.setInt(4, ownerId);
                
                if (statement.executeUpdate() == 1) {
                    return new Pet(petId, name, type, ownerId);
                } else {
                    throw new SQLException("Error inserting pet");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding a pet", e);
            throw new DAOException(e);
        }
    }

    public void modify(Pet pet) throws DAOException {
        if (pet == null) {
            throw new IllegalArgumentException("Pet can't be null");
        }

        try (Connection conn = this.getConnection()) {
            if (!ownerExists(pet.getOwnerId())) {
                throw new IllegalArgumentException("Owner ID does not exist");
            }

            String query = "UPDATE pets SET name = ?, type = ?, owner_id = ? WHERE pet_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, pet.getName());
                statement.setString(2, pet.getType());
                statement.setInt(3, pet.getOwnerId());
                statement.setString(4, pet.getPetId());
                
                if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid pet ID");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);
            throw new DAOException(e);
        }
    }

    public void delete(String petId) throws DAOException {
        try (Connection conn = this.getConnection()) {
            String query = "DELETE FROM pets WHERE pet_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, petId);
                
                if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid pet ID");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a pet", e);
            throw new DAOException(e);
        }
    }

    private Pet rowToEntity(ResultSet row) throws SQLException {
        return new Pet(
            row.getString("pet_id"),
            row.getString("name"),
            row.getString("type"),
            row.getInt("owner_id")
        );
    }
}
