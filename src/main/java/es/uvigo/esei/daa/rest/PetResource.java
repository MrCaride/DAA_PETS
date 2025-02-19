package es.uvigo.esei.daa.rest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PetsDAO;
import es.uvigo.esei.daa.entities.Pet;

/**
 * REST resource for managing pets.
 * 
 * @author DRM
 */
@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {
    private final static Logger LOG = Logger.getLogger(PetResource.class.getName());
    private final PetsDAO dao;

    /**
     * Constructs a new instance of {@link PetResource}.
     */
    public PetResource() {
        this(new PetsDAO());
    }

    // Needed for testing purposes
    PetResource(PetsDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns a pet with the provided identifier.
     * 
     * @param id the identifier of the pet to retrieve.
     * @return a 200 OK response with a pet that has the provided identifier.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        try {
            final Pet pet = this.dao.get(id);
            return Response.ok(pet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet id in get method", iae);
            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting a pet", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Returns the complete list of pets stored in the system.
     */
    @GET
    public Response list() {
        try {
            return Response.ok(this.dao.list()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Creates a new pet in the system.
     */
    @POST
    public Response add(@FormParam("petId") String petId, @FormParam("name") String name, 
                        @FormParam("type") String type, @FormParam("ownerId") int ownerId) {
        try {
            final Pet newPet = this.dao.add(petId, name, type, ownerId);
            return Response.ok(newPet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet data in add method", iae);
            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding a pet", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Modifies the data of a pet.
     */
    @PUT
    @Path("/{id}")
    public Response modify(@PathParam("id") String id, @FormParam("name") String name, 
                           @FormParam("type") String type, @FormParam("ownerId") int ownerId) {
        try {
            final Pet modifiedPet = new Pet(id, name, type, ownerId);
            this.dao.modify(modifiedPet);
            return Response.ok(modifiedPet).build();
        } catch (NullPointerException | IllegalArgumentException e) {
            LOG.log(Level.FINE, "Invalid pet data in modify method", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Deletes a pet from the system.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        try {
            this.dao.delete(id);
            return Response.ok(id).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet id in delete method", iae);
            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting a pet", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
