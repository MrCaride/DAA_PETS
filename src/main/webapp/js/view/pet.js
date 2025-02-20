var PetView = (function() {
    var dao;
    var ownerId;
    var self;
    
    var formId = 'pets-form';
    var listId = 'pets-list';
    var formQuery = '#' + formId;
    var listQuery = '#' + listId;
    
    function PetView(petDao, personId, formContainerId, listContainerId) {
        dao = petDao;
        ownerId = personId;
        self = this;
        
        insertBackButton($('#' + formContainerId));
        insertPetForm($('#' + formContainerId));
        insertPetList($('#' + listContainerId));
        
        this.init = function() {
            dao.listPetsByOwner(ownerId, function(pets) {
                $.each(pets, function(key, pet) {
                    appendToTable(pet);
                });
            },
            function() {
                alert('No ha sido posible acceder al listado de mascotas.');
            });
            
            $(formQuery).submit(function(event) {
                var pet = self.getPetInForm();
                
                if (self.isEditing()) {
                    dao.modifyPet(pet,
                        function(pet) {
                            $('#pet-' + pet.pet_id + ' td.name').text(pet.name);
                            $('#pet-' + pet.pet_id + ' td.type').text(pet.type);
                            self.resetForm();
                        },
                        showErrorMessage,
                        self.enableForm
                    );
                } else {
                    pet.owner_id = ownerId;
                    dao.addPet(pet,
                        function(pet) {
                            appendToTable(pet);
                            self.resetForm();
                        },
                        showErrorMessage,
                        self.enableForm
                    );
                }
                
                return false;
            });
            
            $('#btnClear').click(this.resetForm);
            $('#btnBack').click(function() {
                $('#pets-container').hide();
                $('#people-container').show();
            });
        };

        this.getPetInForm = function() {
            var form = $(formQuery);
            return {
                'pet_id': form.find('input[name="pet_id"]').val(),
                'name': form.find('input[name="name"]').val(),
                'type': form.find('input[name="type"]').val(),
                'owner_id': ownerId
            };
        };

        this.getPetInRow = function(id) {
            var row = $('#pet-' + id);

            if (row !== undefined) {
                return {
                    'pet_id': id,
                    'name': row.find('td.name').text(),
                    'type': row.find('td.type').text(),
                    'owner_id': ownerId
                };
            } else {
                return undefined;
            }
        };
        
        this.editPet = function(id) {
            var row = $('#pet-' + id);

            if (row !== undefined) {
                var form = $(formQuery);
                
                form.find('input[name="pet_id"]').val(id);
                form.find('input[name="name"]').val(row.find('td.name').text());
                form.find('input[name="type"]').val(row.find('td.type').text());
                
                $('input#btnSubmit').val('Modificar');
            }
        };
        
        this.deletePet = function(id) {
            if (confirm('¿Está seguro de que desea eliminar esta mascota?')) {
                dao.deletePet(id,
                    function() {
                        $('tr#pet-' + id).remove();
                    },
                    showErrorMessage
                );
            }
        };

        this.isEditing = function() {
            return $(formQuery + ' input[name="pet_id"]').val() != "";
        };

        this.disableForm = function() {
            $(formQuery + ' input').prop('disabled', true);
        };

        this.enableForm = function() {
            $(formQuery + ' input').prop('disabled', false);
        };

        this.resetForm = function() {
            $(formQuery)[0].reset();
            $(formQuery + ' input[name="pet_id"]').val('');
            $('#btnSubmit').val('Crear');
        };
    };
    
    var insertBackButton = function(parent) {
        parent.prepend(
            '<div class="mb-3">\
                <button id="btnBack" class="btn btn-secondary">Volver a Personas</button>\
            </div>'
        );
    };
    
    var insertPetList = function(parent) {
        parent.append(
            '<table id="' + listId + '" class="table">\
                <thead>\
                    <tr class="row">\
                        <th class="col-sm-4">Nombre</th>\
                        <th class="col-sm-5">Tipo</th>\
                        <th class="col-sm-3">&nbsp;</th>\
                    </tr>\
                </thead>\
                <tbody>\
                </tbody>\
            </table>'
        );
    };

    var insertPetForm = function(parent) {
        parent.append(
            '<form id="' + formId + '" class="mb-5 mb-10">\
                <input name="pet_id" type="hidden" value=""/>\
                <div class="row">\
                    <div class="col-sm-4">\
                        <input name="name" type="text" value="" placeholder="Nombre" class="form-control" required/>\
                    </div>\
                    <div class="col-sm-5">\
                        <input name="type" type="text" value="" placeholder="Tipo" class="form-control" required/>\
                    </div>\
                    <div class="col-sm-3">\
                        <input id="btnSubmit" type="submit" value="Crear" class="btn btn-primary" />\
                        <input id="btnClear" type="reset" value="Limpiar" class="btn" />\
                    </div>\
                </div>\
            </form>'
        );
    };

    var createPetRow = function(pet) {
        return '<tr id="pet-'+ pet.pet_id +'" class="row">\
            <td class="name col-sm-4">' + pet.name + '</td>\
            <td class="type col-sm-5">' + pet.type + '</td>\
            <td class="col-sm-3">\
                <a class="edit btn btn-primary" href="#">Editar</a>\
                <a class="delete btn btn-warning" href="#">Eliminar</a>\
            </td>\
        </tr>';
    };

    var showErrorMessage = function(jqxhr, textStatus, error) {
        alert(textStatus + ": " + error);
    };

    var addRowListeners = function(pet) {
        $('#pet-' + pet.pet_id + ' a.edit').click(function() {
            self.editPet(pet.pet_id);
        });
        
        $('#pet-' + pet.pet_id + ' a.delete').click(function() {
            self.deletePet(pet.pet_id);
        });
    };

    var appendToTable = function(pet) {
        $(listQuery + ' > tbody:last')
            .append(createPetRow(pet));
        addRowListeners(pet);
    };
    
    return PetView;
})();