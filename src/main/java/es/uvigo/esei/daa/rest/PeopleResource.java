package es.uvigo.esei.daa.rest;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PeopleDAO;
import es.uvigo.esei.daa.entities.Person;

/**
 * REST resource for managing people.
 * 
 */
@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {
	private final static Logger LOG = Logger.getLogger(PeopleResource.class.getName());
	
	private final PeopleDAO dao;
	
	/**
	 * Constructs a new instance of {@link PeopleResource}.
	 */
	public PeopleResource() {
		this(new PeopleDAO());
	}
	
	// Needed for testing purposes
	PeopleResource(PeopleDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * Returns a person with the provided identifier.
	 * 
	 * @param id the identifier of the person to retrieve.
	 * @return a 200 OK response with a person that has the provided identifier.
	 * If the identifier does not corresponds with any user, a 400 Bad Request
	 * response with an error message will be returned. If an error happens
	 * while retrieving the list, a 500 Internal Server Error response with an
	 * error message will be returned.
	 */
	@GET
	@Path("/{id}")
	@RolesAllowed({"ADMIN", "USER"})
	public Response get(
		@PathParam("id") int id
	) {
		try {
			final Person person = this.dao.get(id);
			
			return Response.ok(person).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in get method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error getting a person", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Returns the complete list of people stored in the system.
	 * 
	 * @return a 200 OK response with the complete list of people stored in the
	 * system. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	@GET
	@RolesAllowed({"ADMIN", "USER"})
	public Response list(@Context SecurityContext securityContext) {
		try {
			// Obtener el rol del usuario actual
			String role = getCurrentUserRole(securityContext);

			if ("ADMIN".equals(role)) {

				return Response.ok(this.dao.list()).build();

			} else if ("USER".equals(role)) {

				Principal principal = securityContext.getUserPrincipal();
				String username = principal.getName();
				return Response.ok(this.dao.listByCreator(username)).build();
				
			} else {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error listing people", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	private String getCurrentUserRole(SecurityContext securityContext) {
		if (securityContext.isUserInRole("ADMIN")) {
			return "ADMIN";
		} else if (securityContext.isUserInRole("USER")) {
			return "USER";
		} else {
			return null;
		}
	}

	/**
	 * Creates a new person in the system.
	 * 
	 * @param name the name of the new person.
	 * @param surname the surname of the new person.
	 * @param createdBy the creator of the new person.
	 * @return a 200 OK response with a person that has been created. If the
	 * name or the surname are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	@RolesAllowed({"ADMIN", "USER"})
	public Response add(
		@FormParam("name") String name, 
		@FormParam("surname") String surname,
		@Context SecurityContext securityContext
	) {
		try {
			Principal principal = securityContext.getUserPrincipal();
			String username = principal.getName();
			final Person newPerson = this.dao.add(name, surname, username);
			
			return Response.ok(newPerson).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in add method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error adding a person", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Modifies the data of a person.
	 * 
	 * @param id identifier of the person to modify.
	 * @param name the new name of the person.
	 * @param surname the new surname of the person.
	 * @param createdBy the creator of the person.
	 * @return a 200 OK response with a person that has been modified. If the
	 * identifier does not corresponds with any user or the name or surname are
	 * not provided, a 400 Bad Request response with an error message will be
	 * returned. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	@PUT
	@Path("/{id}")
	@RolesAllowed({"ADMIN", "USER"})
	public Response modify(
		@PathParam("id") int id, 
		@FormParam("name") String name, 
		@FormParam("surname") String surname,
		@FormParam("createdBy") String createdBy
	) {
		try {
			final Person modifiedPerson = new Person(id, name, surname, createdBy);
			this.dao.modify(modifiedPerson);
			
			return Response.ok(modifiedPerson).build();
		} catch (NullPointerException npe) {
			final String message = String.format("Invalid data for person (name: %s, surname: %s)", name, surname);
			
			LOG.log(Level.FINE, message);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(message)
			.build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in modify method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error modifying a person", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Deletes a person from the system.
	 * 
	 * @param id the identifier of the person to be deleted.
	 * @return a 200 OK response with the identifier of the person that has
	 * been deleted. If the identifier does not corresponds with any user, a 400
	 * Bad Request response with an error message will be returned. If an error
	 * happens while retrieving the list, a 500 Internal Server Error response
	 * with an error message will be returned.
	 */
	@DELETE
	@Path("/{id}")
	@RolesAllowed({"ADMIN", "USER"})
	public Response delete(
		@PathParam("id") int id
	) {
		try {
			this.dao.delete(id);
			
			return Response.ok(id).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in delete method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error deleting a person", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}
}
