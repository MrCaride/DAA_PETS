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
    
    
    Pet() {}
    
    public Pet(int pet_id, String name, String type, int owner_id) {
        this.pet_id = pet_id;
        this.setName(name != null ? name : "Unknown");
        this.setType(type != null ? type : "Unknown");
        this.owner_id = owner_id;  
    }
    
    
    public int getId() {
        return pet_id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = requireNonNull(name, "Name can't be null");
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = requireNonNull(type, "Type can't be null");
    }
    
    public int getOwner_id() {  
        return owner_id;
    }
    
    public void setOwner_id(int owner_id) {  
        this.owner_id = owner_id;
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
