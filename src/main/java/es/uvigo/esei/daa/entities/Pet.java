package es.uvigo.esei.daa.entities;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a person.
 * 
 * @author DRM
 */
public class Pet {
	private int pet_id;
	private String name;
	private String type;
    private int owner_id;

	
	// Constructor needed for the JSON conversion
	Pet() {}
	
	/**
	 * Constructs a new instance of {@link Pet}.
	 *
	 * @param pet_id identifier of the pet.
	 * @param name name of the pet.
	 * @param type surname of the pet.
	 */
	public Pet(int pet_id, String name, String surname,int owner_id) {
		this.pet_id = pet_id;
		this.setName(name);
		this.setType(type);
        this.setOwnerId(owner_id);
	}
	
	/**
	 * Returns the identifier of the person.
	 * 
	 * @return the identifier of the person.
	 */
	public int getId() {
		return pet_id;
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
	 * Set the name of this pet.
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
	 * Set the surname of this person.
	 * 
	 * @param type the new surname of the pet.
	 * @throws NullPointerException if the {@code type} is {@code null}.
	 */
	public void setType(String type) {
		this.type = requireNonNull(type, "Type can't be null");
	}

	public int getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(int owner_id) {
		this.owner_id=owner_id;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pet_id;
		return result;
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
		if (pet_id != other.pet_id)
			return false;
		return true;
	}
}
