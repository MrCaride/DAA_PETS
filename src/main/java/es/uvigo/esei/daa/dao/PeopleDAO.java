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

import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.entities.Pet;

/**
 * DAO class for the {@link Person} entities.
 * 
 * @author DRM
 *
 */
public class PeopleDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(PeopleDAO.class.getName());
	
	/**
	 * Returns a person stored persisted in the system.
	 * 
	 * @param id identifier of the person.
	 * @return a person with the provided identifier.
	 * @throws DAOException if an error happens while retrieving the person.
	 * @throws IllegalArgumentException if the provided id does not corresponds
	 * with any persisted person.
	 */
	public Person get(int id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people WHERE id=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, id);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return rowToEntity(result);
					} else {
						throw new IllegalArgumentException("Invalid id");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error getting a person", e);
			throw new DAOException(e);
		}
	}
	
	/**
	 * Returns a list with all the people persisted in the system.
	 * 
	 * @return a list with all the people persisted in the system.
	 * @throws DAOException if an error happens while retrieving the people.
	 */
	public List<Person> list() throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				try (final ResultSet result = statement.executeQuery()) {
					final List<Person> people = new LinkedList<>();
					
					while (result.next()) {
						people.add(rowToEntity(result));
					}
					
					return people;
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error listing people", e);
			throw new DAOException(e);
		}
	}

	public List<Person> listByCreator(String createdBy) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people WHERE login_creator=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, createdBy);
				
				try (final ResultSet result = statement.executeQuery()) {
					final List<Person> people = new LinkedList<>();
					
					while (result.next()) {
						people.add(rowToEntity(result));
					}
					
					return people;
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error listing people by creator", e);
			throw new DAOException(e);
		}
	}
	
	/**
	 * Persists a new person in the system. An identifier will be assigned
	 * automatically to the new person.
	 * 
	 * @param name name of the new person. Can't be {@code null}.
	 * @param surname surname of the new person. Can't be {@code null}.
	 * @return a {@link Person} entity representing the persisted person.
	 * @throws DAOException if an error happens while persisting the new person.
	 * @throws IllegalArgumentException if the name or surname are {@code null}.
	 */
	public Person add(String name, String surname, String loginCreator) throws DAOException, IllegalArgumentException {
		if (name == null || surname == null || loginCreator == null) {
			throw new IllegalArgumentException("name, surname and loginCreator can't be null");
		}
		
		try (Connection conn = this.getConnection()) {
			final String query = "INSERT INTO people (name, surname, login_creator) VALUES (?, ?, ?)";
			
			try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, name);
				statement.setString(2, surname);
				statement.setString(3, loginCreator);
				
				if (statement.executeUpdate() == 1) {
					try (ResultSet resultKeys = statement.getGeneratedKeys()) {
						if (resultKeys.next()) {
							return new Person(resultKeys.getInt(1), name, surname, loginCreator);
						} else {
							LOG.log(Level.SEVERE, "Error retrieving inserted id");
							throw new SQLException("Error retrieving inserted id");
						}
					}
				} else {
					LOG.log(Level.SEVERE, "Error inserting value");
					throw new SQLException("Error inserting value");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error adding a person", e);
			throw new DAOException(e);
		}
	}


	public Pet addPet(int id, String name, String type) throws DAOException, IllegalArgumentException {
		if (name == null || type == null) {
			throw new IllegalArgumentException("name and type can't be null");
		}
	
		try (Connection conn = this.getConnection()) {
			conn.setAutoCommit(false);
			
			try {
				final String petQuery = "INSERT INTO pets VALUES(null, ?, ?, ?)";
				int petId;
				
				try (PreparedStatement petStmt = conn.prepareStatement(petQuery, Statement.RETURN_GENERATED_KEYS)) {
					petStmt.setString(1, name);
					petStmt.setString(2, type);
					petStmt.setInt(3, id); 
					
					if (petStmt.executeUpdate() != 1) {
						throw new SQLException("Error inserting pet");
					}
					
					try (ResultSet resultKeys = petStmt.getGeneratedKeys()) {
						if (resultKeys.next()) {
							petId = resultKeys.getInt(1);
						} else {
							throw new SQLException("Error retrieving inserted pet id");
						}
					}
				}
				conn.commit();
				return new Pet(petId, name, type, id);
	
			} catch (SQLException e) {
				conn.rollback();
				LOG.log(Level.SEVERE, "Transaction failed, rolling back", e);
				throw new DAOException(e);
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error adding a pet", e);
			throw new DAOException(e);
		}
	}
	
	/**
	 * Modifies a person previously persisted in the system. The person will be
	 * retrieved by the provided id and its current name and surname will be
	 * replaced with the provided.
	 * 
	 * @param person a {@link Person} entity with the new data.
	 * @throws DAOException if an error happens while modifying the new person.
	 * @throws IllegalArgumentException if the person is {@code null}.
	 */
	public void modify(Person person)
	throws DAOException, IllegalArgumentException {
		if (person == null) {
			throw new IllegalArgumentException("person can't be null");
		}
		
		try (Connection conn = this.getConnection()) {
			final String query = "UPDATE people SET name=?, surname=? WHERE id=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, person.getName());
				statement.setString(2, person.getSurname());
				statement.setInt(3, person.getId());
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("name and surname can't be null");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error modifying a person", e);
			throw new DAOException();
		}
	}
	
	/**
	 * Removes a persisted person from the system.
	 * 
	 * @param id identifier of the person to be deleted.
	 * @throws DAOException if an error happens while deleting the person.
	 * @throws IllegalArgumentException if the provided id does not corresponds
	 * with any persisted person.
	 */
	public void delete(int id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "DELETE FROM people WHERE id=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, id);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Invalid id");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error deleting a person", e);
			throw new DAOException(e);
		}
	}
	
	private Person rowToEntity(ResultSet row) throws SQLException {
		return new Person(
			row.getInt("id"),
			row.getString("name"),
			row.getString("surname")
		);
	}
}
