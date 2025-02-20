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

        $.ajax(data).done(done).fail(fail).always(always);
    };

    function PetDAO() {
        this.listPets = function(done, fail, always) {
            requestByAjax({
                url: resourcePath,
                type: 'GET'
            }, done, fail, always);
        };

        this.addPet = function(pet, done, fail, always) {
            requestByAjax({
                url: resourcePath,
                type: 'POST',
                data: pet
            }, done, fail, always);
        };

        this.modifyPet = function(pet, done, fail, always) {
            requestByAjax({
                url: resourcePath + pet.id,
                type: 'PUT',
                data: pet
            }, done, fail, always);
        };

        this.deletePet = function(id, done, fail, always) {
            requestByAjax({
                url: resourcePath + id,
                type: 'DELETE',
            }, done, fail, always);
        };
    }

    return PetDAO;
})();
