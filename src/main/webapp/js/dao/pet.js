var PetDAO = (function() {
    var resourcePath = "rest/pets/";
    
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

        // Añadir headers específicos para JSON
        if (data.type === 'POST' || data.type === 'PUT') {
            data.headers = {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            };
        }

        // Log para depuración
        console.log('Making request:', {
            url: data.url,
            type: data.type,
            data: data.data,
            headers: data.headers
        });

        $.ajax(data)
            .done(function(response) {
                console.log('Success:', response);
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

    function PetDAO() {
        this.listPetsByOwner = function(ownerId, done, fail, always) {
            requestByAjax({
                url: resourcePath + "owner/" + ownerId,
                type: 'GET'
            }, done, fail, always);
        };
        
        this.addPet = function(pet, done, fail, always) {
            // Asegurarse de que los datos están en el formato correcto
            var petData = {
                name: pet.name,
                type: pet.type,
                owner_id: pet.owner_id
            };

            requestByAjax({
                url: resourcePath,
                type: 'POST',
                data: JSON.stringify(petData),
                processData: false
            }, done, fail, always);
        };
        
        this.modifyPet = function(pet, done, fail, always) {
            requestByAjax({
                url: resourcePath + pet.pet_id,
                type: 'PUT',
                data: JSON.stringify(pet),
                processData: false
            }, done, fail, always);
        };
        
        this.deletePet = function(pet_id, done, fail) {
            requestByAjax({
                url: resourcePath +  pet_id,  
                type: 'DELETE',
                contentType: 'application/json'
            }, done, fail);
        };
        
    }
    
    return PetDAO;
})();