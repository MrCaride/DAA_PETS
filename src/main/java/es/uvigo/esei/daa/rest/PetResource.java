package es.uvigo.esei.daa.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PetDAO;
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

    private final PetDAO dao;

    /**
     * Constructs a new instance of {@link PetResource}.
     */
    public PetResource() {
        this(new PetDAO());
    }

    // Needed for testing purposes
    PetResource(PetDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns a pet with the provided identifier.
     *
     * @param id the identifier of the pet to retrieve.
     * @return a 200 OK response with the pet details. If the pet does not exist,
     * a 400 Bad Request response is returned. If an error happens, a 500 Internal Server Error is returned.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") int id) {
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
     * Returns the complete list of pets.
     *
     * @return a 200 OK response with the list of pets. If an error occurs, a 500 Internal Server Error is returned.
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

    @GET
    @Path("/owner/{id}")
    public Response listByOwner(@PathParam("id") int ownerId) {
        try {
            return Response.ok(dao.listPetsByOwner(ownerId)).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing pets by owner", e);
            return Response.serverError().build();
        }
    }

    /**
     * Adds a new pet to the system.
     *
     * @param name the name of the pet.
     * @param type the type of the pet.
     * @param ownerId the identifier of the pet's owner.
     * @return a 200 OK response with the created pet. If invalid data is provided,
     * a 400 Bad Request response is returned. If an error happens, a 500 Internal Server Error is returned.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(Pet pet) {
        try {
            LOG.log(Level.INFO, "Received request to add pet: name=" + pet.getName() + 
                ", type=" + pet.getType() + ", ownerId=" + pet.getOwner_id());
            
            if (pet.getName() == null || pet.getType() == null) {
                LOG.log(Level.WARNING, "Invalid pet data: name or type is null");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name and type are required").build();
            }
    
            final Pet newPet = this.dao.add(pet.getName(), pet.getType(), pet.getOwner_id());
            LOG.log(Level.INFO, "Successfully added pet with ID: " + newPet.getId());
            return Response.ok(newPet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.WARNING, "Invalid pet data: " + iae.getMessage(), iae);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding pet: " + e.getMessage(), e);
            return Response.serverError()
                .entity("Error adding pet: " + e.getMessage()).build();
        }
    }

    /**
     * Modifies an existing pet.
     *
     * @param id the identifier of the pet to modify.
     * @param name the new name of the pet.
     * @param type the new type of the pet.
     * @param ownerId the new owner ID of the pet.
     * @return a 200 OK response with the modified pet. If the pet does not exist or invalid data is provided,
     * a 400 Bad Request response is returned. If an error happens, a 500 Internal Server Error is returned.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modify(@PathParam("id") int id, Pet pet) {
        try {
            if (pet.getId() != id) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Path id and pet id don't match").build();
            }
            
            this.dao.modify(pet);
            return Response.ok(pet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet data in modify method", iae);
            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Deletes a pet from the system.
     *
     * @param id the identifier of the pet to delete.
     * @return a 200 OK response with the deleted pet ID. If the pet does not exist,
     * a 400 Bad Request response is returned. If an error happens, a 500 Internal Server Error is returned.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
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
