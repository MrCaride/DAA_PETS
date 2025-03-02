var PetDAO = (function() {
    var resourcePath = "rest/pets/";
    
    const MAX_LENGTH = {
        name: 30,
        type: 30
    };
    
    var requestByAjax = function(data, done, fail, always) {
        done = typeof done !== 'undefined' ? done : function() {};
        fail = typeof fail !== 'undefined' ? fail : function() {};
        always = typeof always !== 'undefined' ? always : function() {};

        let authToken = localStorage.getItem('authorization-token');
        if (authToken !== null) {
            data.beforeSend = function(xhr) {
                xhr.setRequestHeader('Authorization', 'Basic ' + authToken);
            };
        }

        $.ajax(data)
            .done(function(response) {
                console.log('Success response:', response);
                done(response);
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', {
                    status: jqXHR.status,
                    textStatus: textStatus,
                    error: errorThrown,
                    response: jqXHR.responseText
                });
                fail(jqXHR, textStatus, errorThrown);
            })
            .always(always);
    };

    function validateAndTrim(text, maxLength) {
        if (!text) return '';
        return text.trim().substring(0, maxLength);
    }

    function PetDAO() {
        this.listPetsByOwner = function(ownerId, done, fail, always) {
            requestByAjax({
                url: resourcePath + "owner/" + ownerId,
                type: 'GET'
            }, done, fail, always);
        };
        
        this.addPet = function(pet, done, fail, always) {
            const validatedPet = {
                name: validateAndTrim(pet.name, MAX_LENGTH.name),
                type: validateAndTrim(pet.type, MAX_LENGTH.type),
                owner_id: pet.owner_id
            };

            requestByAjax({
                url: resourcePath,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(validatedPet)
            }, done, fail, always);
        };
        
        this.modifyPet = function(pet, done, fail, always) {
            if (!pet.pet_id) {
                console.error('No pet_id provided for modification');
                fail({ responseText: 'No pet ID provided' });
                return;
            }

            const validatedData = {
                name: validateAndTrim(pet.name, MAX_LENGTH.name),
                type: validateAndTrim(pet.type, MAX_LENGTH.type),
                owner_id: pet.owner_id
            };

            var formData = $.param(validatedData);

            requestByAjax({
                url: resourcePath + pet.pet_id,
                type: 'PUT',
                contentType: 'application/x-www-form-urlencoded',
                data: formData
            }, done, fail, always);
        };
        
        this.deletePet = function(petId, done, fail, always) {
            requestByAjax({
                url: resourcePath + petId,
                type: 'DELETE'
            }, done, fail, always);
        };
    }
    
    return PetDAO;
})();