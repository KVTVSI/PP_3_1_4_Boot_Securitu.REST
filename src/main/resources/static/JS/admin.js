$(async function () {
    getUserName();
    await getTableWithAllUsers();
    getDefaultModal();
    addNewUser();
})

const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    findAdmin: async () => await fetch('api/admin'),
    findAllUsers: async () => await fetch('api/admin/usersList'),
    findOneUser: async (id) => await fetch(`api/admin/usersList/${id}`),
    addNewUser: async (user) => await fetch('api/admin', {
        method: 'POST',
        headers: userFetchService.head,
        body: JSON.stringify(user)
    }),
    updateUser: async (user, id) => await fetch(`api/admin/usersList/${id}`, {
        method: 'PUT',
        headers: userFetchService.head,
        body: JSON.stringify(user)
    }),
    deleteUser: async (id) => await fetch(`api/admin/usersList/${id}`, {
        method: 'DELETE',
        headers: userFetchService.head
    })

}


async function getUserName() {
    let adminEmail = $('#adminEmail');
    adminEmail.empty()
    let adminRole = $('#adminRole')
    adminRole.empty()
    let adminTable = $('#adminTable tbody')
// Заполнение верхней панели и информации о юзере
    await userFetchService.findAdmin()
        .then(res => res.json())
        .then(user => {
            adminEmail.append(user.email)
            // adminRole.append(user.role)
            user.roles.forEach(role => {
                adminRole.append(role.role.substring(5) + ` `)
            })
            let tableFill = `
            <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>     
                            <td>${user.email}</td>  
                            <td>   
           `
            user.roles.forEach(role => {
                tableFill += `${role.role.substring(5) + " "}`
            })
            tableFill += `</td></tr>`
            adminTable.append(tableFill)
        })
}

async function getTableWithAllUsers() {
    let table = $('#usersTable tbody');
    table.empty()

    await userFetchService.findAllUsers()
        .then(res => res.json())
        .then(users => {
            users.forEach(user => {
                let tableFill = `
            <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>     
                            <td>${user.email}</td>     
                            <td>
            `
                user.roles.forEach(role => {
                    tableFill += `${role.role.substring(5) + " "}`
                })
                tableFill += `</td><td>
                                <button class="btn btn-primary" type="button" data-userid="${user.id}" data-action="edit"
                                data-toggle="modal" data-target="#someDefaultModal">Edit</button>
                            </td>
                            <td>
                                <button class="btn btn-danger" type="button" data-userid="${user.id}" data-action="delete"
                                data-toggle="modal" data-target="#someDefaultModal">Delete</button>
                            </td>
                        </tr>`;
                table.append(tableFill)
            })
        })


    $("#usersTable").find('button').on('click', (event) => {
        let defaultModal = $('#someDefaultModal');

        let targetButton = $(event.target);
        let buttonUserId = targetButton.attr('data-userid');
        let buttonAction = targetButton.attr('data-action');

        defaultModal.attr('data-userid', buttonUserId);
        defaultModal.attr('data-action', buttonAction);
        defaultModal.modal('show');
    })


}


async function getDefaultModal() {
    $('#someDefaultModal').modal({
        keyboard: true,
        backdrop: "static",
        show: false
    }).on("show.bs.modal", (event) => {
        let thisModal = $(event.target);
        let userid = thisModal.attr('data-userid');

        let action = thisModal.attr('data-action');
        switch (action) {
            case 'edit':
                editUser(thisModal, userid);
                break;
            case 'delete':
                deleteUser(thisModal, userid);
                break;
        }
    }).on("hidden.bs.modal", (e) => {
        let thisModal = $(e.target);
        thisModal.find('.modal-title').html('');
        thisModal.find('.modal-body').html('');
        thisModal.find('.modal-footer').html('');
    })
}

async function editUser(modal, id) {
    let preuser = await userFetchService.findOneUser(id);
    let user = preuser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button type="button"  class="btn btn-outline-success" id="editButton">Edit</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let bodyForm = `
            <form style="display: inline-block; width: 250px; text-align: center" class="form-group" id="editUser">
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="id" class="col-sm-2 col-form-label">ID</label>
                    <input style="text-align: center" type="text" class="form-control" id="id" name="id" value="${user.id}" disabled placeholder="ID"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="firstname">First Name</label>
                    <input style="text-align: center" class="form-control" type="text" id="firstname" value="${user.name}" placeholder="First Name"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="lastname">Last Name</label>
                    <input style="text-align: center" class="form-control" type="text" id="lastname" value="${user.lastName}" placeholder="Last Name"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" class="col-sm-2 col-form-label" for="age">Age</label>
                    <input style="text-align: center" class="form-control" id="age" type="number" value="${user.age}" placeholder="Age">
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="email">Email</label>
                    <input style="text-align: center" class="form-control" type="email" id="email" value="${user.email}" placeholder="Email"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="showPassword" class="col-sm-2 col-form-label">Password</label>
                    <input style="text-align: center" class="form-control" type="password" id="showPassword" placeholder="Password">
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="hiddenPassword" hidden class="col-sm-2 col-form-label">Password</label>
                    <input style="text-align: center" class="form-control" type="password" hidden disabled id="hiddenPassword" value="${user.password}" placeholder="Password">
                </div>
                
                <div>
                    <span style="display: inline;font-size: 20px; font-weight: bold">Give access to the admin panel?</span>
                </div>
                
                <DIV style="padding: 10px">
                    <select size="2" multiple required class="form-select mx-auto" style="width: 250px" id="selectOption">
                        <option id="optionAdmin" value="ADMIN" >ADMIN</option>
                        <option selected id="optionUser" value="USER">USER</option>
                    </select>
                    
             
                
                
                </DIV>
                
            </form>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#editButton").on('click', async () => {
        let id = modal.find("#id").val().trim();
        let firstName = modal.find("#firstname").val().trim();
        let lastName = modal.find("#lastname").val().trim();
        let age = modal.find("#age").val().trim();
        let email = modal.find("#email").val().trim();
        let showPassword = modal.find("#showPassword").val().trim();
        let hiddenPassword = modal.find("#hiddenPassword").val().trim();
        let selectOption = modal.find("#selectOption").val();

        let data = {
            id: id,
            name: firstName,
            lastName: lastName,
            age: age,
            email: email,
            password: hiddenPassword,
            roles: [{
                id: 1,
                role: "ROLE_USER"
            }]
        }
        if (selectOption == "ADMIN") {
            data.roles = [{
                id: 1,
                role: "ROLE_USER"
            },
                {
                    id: 2,
                    role: "ROLE_ADMIN"
                }]
        }

        if (showPassword === "") {
            data.password = hiddenPassword
        } else {
            data.password = showPassword
        }
        const response = await userFetchService.updateUser(data, id);
        if (response.ok) {
            getTableWithAllUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function deleteUser(modal, id) {
    let preuser = await userFetchService.findOneUser(id);
    let user = preuser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button type="button"  class="btn btn-outline-success" id="deleteButton">Delete</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let bodyForm = `
            <form style="display: inline-block; width: 250px; text-align: center" class="form-group" id="editUser">
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="id" class="col-sm-2 col-form-label">ID</label>
                    <input style="text-align: center" type="text" class="form-control" id="id" name="id" value="${user.id}" disabled placeholder="ID"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="firstname">First Name</label>
                    <input style="text-align: center" class="form-control" type="text" id="firstname" value="${user.name}" disabled placeholder="First Name"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="lastname">Last Name</label>
                    <input style="text-align: center" class="form-control" type="text" id="lastname" value="${user.lastName}" disabled placeholder="Last Name"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" class="col-sm-2 col-form-label" for="age">Age</label>
                    <input style="text-align: center" class="form-control" id="age" type="number" value="${user.age}" disabled placeholder="Age">
                </div>
                
                <div style="padding: 10px">
                    <label style=" ;display: inline;font-size: 20px; font-weight: bold" for="email">Email</label>
                    <input style="text-align: center" class="form-control" type="email" id="email" value="${user.email}" disabled placeholder="Email"><br>
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="showPassword" class="col-sm-2 col-form-label">Password</label>
                    <input style="text-align: center" class="form-control" type="password" id="showPassword" disabled placeholder="Password">
                </div>
                
                <div style="padding: 10px">
                    <label style="display: inline;font-size: 20px; font-weight: bold" for="hiddenPassword" hidden class="col-sm-2 col-form-label">Password</label>
                    <input style="text-align: center" class="form-control" type="password" hidden disabled id="hiddenPassword" value="${user.password}" placeholder="Password">
                </div>
                
                <div>
                    <span style="display: inline;font-size: 20px; font-weight: bold">Give access to the admin panel?</span>
                </div>
                
                <DIV style="padding: 10px">
                    <select size="2" multiple required class="form-select mx-auto" style="width: 250px" id="selectOption">
                        <option id="optionAdmin" value="ADMIN" disabled>ADMIN</option>
                        <option disabled id="optionUser" value="USER">USER</option>
                    </select>
                    
             
                
                
                </DIV>
                
            </form>
        `;
        modal.find('.modal-body').append(bodyForm);
    })
    $("#deleteButton").on('click', async () => {

        const response = await userFetchService.deleteUser(id);
        if (response.ok) {
            getTableWithAllUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function addNewUser() {
    $('#addNewUserButton').click(async () => {
        let addUserForm = $('#addUserForm')
        let firstName = addUserForm.find('#addNewUserFirstName').val().trim();
        let lastName = addUserForm.find('#addNewUserLastName').val().trim();
        let age = addUserForm.find('#addNewUserAge').val().trim();
        let email = addUserForm.find('#addNewUserEmail').val().trim();
        let password = addUserForm.find('#addNewUserPassword').val().trim();
        let addUserSelectOption = addUserForm.find("#addUserSelectOption").val();
        let data = {
            name: firstName,
            lastName: lastName,
            age: age,
            email: email,
            password: password,
            roles: [{
                id: 1,
                role: "ROLE_USER"
            }]
        }
        if (addUserSelectOption == "ADMIN") {
            data.roles = [{
                id: 1,
                role: "ROLE_USER"
            },
                {
                    id: 2,
                    role: "ROLE_ADMIN"
                }]
        }
        const response = await userFetchService.addNewUser(data);
        if (response.ok) {
            getTableWithAllUsers();
            addUserForm.find('#addNewUserFirstName').val('');
            addUserForm.find('#addNewUserLastName').val('');
            addUserForm.find('#addNewUserAge').val('');
            addUserForm.find('#addNewUserEmail').val('');
            addUserForm.find('#addNewUserPassword').val('');
        } else {
            let body = await response.json();
            alert("ojibka")
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert)
        }
    })
}


// <span style="font-weight: bold" th:each="roles : ${user.getRoles()}"
//       th:text="${roles.getRole().substring(5)}"/>