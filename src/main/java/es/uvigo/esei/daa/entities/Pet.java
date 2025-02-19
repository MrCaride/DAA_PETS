package es.uvigo.esei.daa.entities;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a pet.
 * 
 * @author DRM
 */
public class Pet {
    private String petId;
    private String name;
    private String type;
    private int ownerId;
    
    // Constructor needed for the JSON conversion
    Pet() {}
    
    /**
     * Constructs a new instance of {@link Pet}.
     *
     * @param petId identifier of the pet.
     * @param name name of the pet.
     * @param type type of the pet.
     * @param ownerId identifier of the owner (Person).
     */
    public Pet(String petId, String name, String type, int ownerId) {
        this.petId = requireNonNull(petId, "Pet ID can't be null");
        this.setName(name);
        this.setType(type);
        this.ownerId = ownerId;
    }
    
    /**
     * Returns the identifier of the pet.
     * 
     * @return the identifier of the pet.
     */
    public String getPetId() {
        return petId;
    }
    
    /**
     * Returns the name of the pet.
     * 
     * @return the name of the pet.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this pet.
     * 
     * @param name the new name of the pet.
     * @throws NullPointerException if the {@code name} is {@code null}.
     */
    public void setName(String name) {
        this.name = requireNonNull(name, "Name can't be null");
    }
    
    /**
     * Returns the type of the pet.
     * 
     * @return the type of the pet.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type of this pet.
     * 
     * @param type the new type of the pet.
     * @throws NullPointerException if the {@code type} is {@code null}.
     */
    public void setType(String type) {
        this.type = requireNonNull(type, "Type can't be null");
    }
    
    /**
     * Returns the owner ID of the pet.
     * 
     * @return the owner ID of the pet.
     */
    public int getOwnerId() {
        return ownerId;
    }
    
    /**
     * Sets the owner ID of this pet.
     * 
     * @param ownerId the new owner ID of the pet.
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    
    @Override
    public int hashCode() {
        return petId.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Pet))
            return false;
        Pet other = (Pet) obj;
        return petId.equals(other.petId);
    }
}
