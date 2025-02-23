var PetView = (() => {
    var dao;
    var ownerId;
    var self;

    var formId = "pets-form";
    var listId = "pets-list";
    var formQuery = "#" + formId;
    var listQuery = "#" + listId;

    function PetView(petDao, personId, formContainerId, listContainerId) {
        dao = petDao;
        ownerId = personId;
        self = this;

        // Limpiar el contenedor antes de insertar
        $("#" + formContainerId).empty();

        insertBackButton($("#" + formContainerId));
        insertPetForm($("#" + formContainerId));
        insertPetList($("#" + formContainerId));

        this.init = function () {
            // Cargar las mascotas del dueño específico
            dao.listPetsByOwner(
                ownerId,
                (pets) => {
                    $.each(pets, (key, pet) => {
                        appendToTable(pet);
                    });
                },
                () => {
                    alert("No ha sido posible acceder al listado de mascotas.");
                },
            );

            $(formQuery).submit((event) => {
                event.preventDefault(); // Evitar el comportamiento por defecto del formulario
                var pet = self.getPetInForm();

                if (self.isEditing()) {
                    dao.modifyPet(pet, function(pet) {
                        $("#pet-" + pet.id + " td.name").text(pet.name);
                        $("#pet-" + pet.id + " td.type").text(pet.type);
                        self.resetForm(); // Asegúrate de que el formulario se limpie después de modificar
                    },          
                    showErrorMessage, self.enableForm);
                } else {
                    pet.owner_id = ownerId;
                    dao.addPet(
                        pet,
                        (pet) => {
                            appendToTable(pet);
                            self.resetForm(); // Asegúrate de que el formulario se limpie después de añadir
                        },
                        showErrorMessage,
                    );
                }

                return false;
            });

            $('input#btnClear').click(() => {
                console.log("Clear button clicked");
                self.resetForm(); // Asegúrate de que el formulario se limpie cuando se haga clic en el botón de limpiar
            });

            $("#btnBack").click(() => {
                $("#pets-container").hide();
                $("#people-container").show();
            });
        };

        this.getPetInForm = () => {
            var form = $(formQuery);
            var petData = {
                pet_id: form.find('input[name="pet_id"]').val() || null,
                name: form.find('input[name="name"]').val(),
                type: form.find('input[name="type"]').val(),
                owner_id: Number.parseInt(ownerId),
            };
            console.log("Form data:", petData);
            return petData;
        };

        this.editPet = function(pet_id) {
            var row = $("#pet-" + pet_id);

            if (row.length > 0) {
                var form = $(formQuery);

                form.find('input[name="pet_id"]').val(pet_id);
                form.find('input[name="name"]').val(row.find("td.name").text());
                form.find('input[name="type"]').val(row.find("td.type").text());
                form.find('input[name="owner_id"]').val(row.find("td.owner_id").text());

                $("input#btnSubmit").val('Modificar');
                console.log("Editing pet with id:", pet_id);
            } else {
                console.error("Pet row not found for id:", pet_id);
            }
        };

        this.deletePet = (pet_id) => {
            if (pet_id && confirm("¿Está seguro de que desea eliminar esta mascota? (ID: " + pet_id + ")")) {
                dao.deletePet(
                    pet_id,
                    (response) => {
                        console.log("Pet deleted successfully:", pet_id, response);
                        $("tr#pet-" + pet_id).remove();
                    },
                    (jqXHR, textStatus, errorThrown) => {
                        console.error("Error deleting pet:", {
                            pet_id: pet_id,
                            status: jqXHR.status,
                            textStatus: textStatus,
                            error: errorThrown,
                            response: jqXHR.responseText,
                        });
                        alert("Error al eliminar la mascota: " + textStatus);
                    },
                );
            } else if (!pet_id) {
                console.error("Attempted to delete pet with undefined id");
                alert("Error: No se pudo identificar la mascota a eliminar");
            }
        };

        this.isEditing = function() {
            return $(formQuery + ' input[name="pet_id"]').val() != "";
        };

        this.resetForm = function() {
            console.log("Resetting form");
            $(formQuery)[0].reset();
            $(formQuery + ' input[name="pet_id"]').val('');
            $("input#btnSubmit").val('Crear'); // Cambia el texto del botón a "Crear"
        };
    }

    var insertBackButton = (parent) => {
        parent.append('<button id="btnBack" class="btn btn-secondary mb-3">Volver a Personas</button>');
    };

    var insertPetList = (parent) => {
        parent.append(
            '<table id="' +
            listId +
            '" class="table">\
                <thead>\
                    <tr class="row">\
                        <th class="col-sm-4">Nombre</th>\
                        <th class="col-sm-5">Tipo</th>\
                        <th class="col-sm-3">Acciones</th>\
                    </tr>\
                </thead>\
                <tbody>\
                </tbody>\
            </table>',
        );
    };

    var insertPetForm = (parent) => {
        parent.append(
            '<form id="' +
            formId +
            '" class="mb-5  mb-10">\
                <input name="pet_id" type="hidden" value=""/>\
                <div class="row">\
                    <div class="col-sm-4">\
                        <input name="name" type="text" value="" placeholder="Nombre" class="form-control" required/>\
                    </div>\
                    <div class="col-sm-5">\
                        <input name="type" type="text" value="" placeholder="Tipo" class="form-control" required maxlength="50"/>\
                    </div>\
                    <div class="col-sm-3">\
                        <input id="btnSubmit" type="submit" value="Crear" class="btn btn-primary" />\
                        <input id="btnClear" type="reset" value="Limpiar" class="btn" />\
                    </div>\
                </div>\
            </form>'
        );
    };

    var createPetRow = (pet) => {
        console.log("Creating row for pet:", pet); // Añade este log
        return (
            '<tr id="pet-' + pet.id +'" class="row">\
                <td class="name col-sm-4">' + pet.name +'</td>\
                <td class="type col-sm-5">' + pet.type +'</td>\
                <td class="col-sm-3">\
                    <a class="edit btn btn-primary me-2" href="#" data-pet-id="' +
                    pet.id +
            '">Editar</a>\
                    <a class="delete btn btn-warning" href="#" data-pet-id="' +
                    pet.id +
            '">Eliminar</a>\
                </td>\
            </tr>'
        );
    };

    var showErrorMessage = (jqXHR, textStatus, error) => {
        console.error("Error:", {
            status: jqXHR.status,
            textStatus: textStatus,
            error: error,
            response: jqXHR.responseText,
        });
        alert(textStatus + ": " + error + "\n" + jqXHR.responseText);
    };

    var addRowListeners = (pet) => {
        console.log("Adding listeners for pet:", pet);
        $("#pet-" + pet.id + " a.edit").click(function () {
            self.editPet(pet.id);
        });

        $("#pet-" + pet.id + " a.delete").click(function () {
            self.deletePet(pet.id);
        });
    };

    var appendToTable = (pet) => {
        $(listQuery + " > tbody:last").append(createPetRow(pet));
        addRowListeners(pet);
    };

    return PetView;
})();

