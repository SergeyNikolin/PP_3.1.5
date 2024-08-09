document.addEventListener('DOMContentLoaded', async function () {
    await showUserNavbar()
    await fillTableOfAllUsers();
    await fillTableAboutCurrentUser();
    await addNewUserForm();
    await DeleteModalHandler();
    await EditModalHandler();
});

//------------<<ROLES>>------------//

const ROLE_USER = {id: 2, name: "ROLE_USER"};
const ROLE_ADMIN = {id: 1, name: "ROLE_ADMIN"};

//------------<<NAVBAR INFO>>------------//

async function showUserNavbar() {
    const currentUserNavbar = document.getElementById("currentUserNavbar")
    const currentUser = await dataAboutCurrentUser();
    currentUserNavbar.innerHTML =
        `<strong>${currentUser.username}</strong>
                 with roles: 
                 ${currentUser.roles.map(role => role.name).join(' ')}`;
}

//------------<<ALL USERS TABLE>>------------//

async function dataAboutAllUsers() {
    const response = await fetch("/api/admin");
    return await response.json();
}

async function fillTableOfAllUsers() {
    const usersTable = document.getElementById("usersTable");
    const users = await dataAboutAllUsers();

    let usersTableHTML = "";
    for (let user of users) {
        usersTableHTML +=
            `<tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.name}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.roles.map(role => role.name).join(' ')}</td>
                <td>
                    <button class="btn btn-primary btn-sm text-white"
                            data-bs-toggle="modal"
                            data-bs-target="#editModal"
                            data-user-id="${user.id}">
                        Edit</button>
                </td>
                <td>
                    <button class="btn btn-danger btn-sm btn-delete"
                            data-bs-toggle="modal"
                            data-bs-target="#deleteModal"
                            data-user-id="${user.id}">                     
                        Delete</button>
                </td>
            </tr>`;
    }
    usersTable.innerHTML = usersTableHTML;
}

//------------<<CURRENT USER TABLE>>------------//

async function dataAboutCurrentUser() {
    const response = await fetch("/api/user")
    return await response.json();
}

async function fillTableAboutCurrentUser() {
    const currentUserTable = document.getElementById("currentUserTable");
    const currentUser = await dataAboutCurrentUser();

    let currentUserTableHTML = "";
    currentUserTableHTML +=
        `<tr>
            <td>${currentUser.id}</td>
            <td>${currentUser.username}</td>
            <td>${currentUser.name}</td>
            <td>${currentUser.lastName}</td>
            <td>${currentUser.age}</td>
            <td>${currentUser.roles.map(role => role.name).join(' ')}</td>
        </tr>`
    currentUserTable.innerHTML = currentUserTableHTML;
}

//------------<<ADD NEW USER>>------------//

async function createNewUser(user) {
    try {
        await fetch("/api/admin",
            {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(user)})
    } catch (error) {
        console.error('Error creating new user:', error);
    }
}

async function addNewUserForm() {
    const newUserForm = document.getElementById("newUser");

    newUserForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const firstName = document.getElementById("nameAdd").value;
        const lastName = document.getElementById("lastNameAdd").value;
        const age = document.getElementById("ageAdd").value;
        const username = document.getElementById("usernameAdd").value;
        const password = document.getElementById("passwordAdd").value;

        if (!firstName || !lastName || !age || !username || !password) {
            console.error('One or more input elements not found');
            return;
        }

        const rolesSelected = document.getElementById("roles");

        let roles = [];
        for (let option of rolesSelected.selectedOptions) {
            if (option.value === ROLE_USER.name) {
                roles.push(ROLE_USER);
            } else if (option.value === ROLE_ADMIN.name) {
                roles.push(ROLE_ADMIN);
            }
        }




        const newUserData = {
            name: firstName,
            lastName: lastName,
            age: age,
            username: username,
            password: password,
            roles: roles
        };

        await createNewUser(newUserData);
        newUserForm.reset();

        document.querySelector('a#show-users-table').click();
        await fillTableOfAllUsers();
    });
}

//------------<<FILL MODAL WITH USER DATA>>------------//

async function getUserDataById(userId) {
    const response = await fetch(`/api/admin/${userId}`);
    return await response.json();
}

function getOpenModal() {
    const openModalContainer = document.querySelector(".modal.open");
    if (openModalContainer) {
        return openModalContainer.id;
    } else {
        return null;
    }
}

async function fillModal(modal) {

    if (modal === null) {
        console.error('Modal element is null. Cannot add event listener.');
        return;
    }

    modal.addEventListener("show.bs.modal", async function(event) {

        const userId = event.relatedTarget.dataset.userId;
        const user = await getUserDataById(userId);

        const modalBody = modal.querySelector(".modal-body");

        const idInput = modalBody.querySelector("input[data-user-id='id']");
        const usernameInput = modalBody.querySelector("input[data-user-id='username']");
        const passwordInput = modalBody.querySelector("input[data-user-id='password']");
        const firstNameInput = modalBody.querySelector("input[data-user-id='name']");
        const lastNameInput = modalBody.querySelector("input[data-user-id='lastName']");
        const ageInput = modalBody.querySelector("input[data-user-id='age']");


        idInput.value = user.id;
        usernameInput.value = user.username;
        //passwordInput.value = user.password;
        firstNameInput.value = user.name;
        lastNameInput.value = user.lastName;
        ageInput.value = user.age;

        let rolesSelect = HTMLSelectElement;

        let rolesSelectDelete = modalBody.querySelector("select[data-user-id='rolesDelete']");
        let rolesSelectEdit = modalBody.querySelector("select[data-user-id='rolesEdit']");
        let userRolesHTML = "";

        if (rolesSelectDelete !== null) {
            rolesSelect = rolesSelectDelete;
            for (let i = 0; i < user.roles.length; i++) {
                userRolesHTML +=
                    `<option value="${user.roles[i].name}">${user.roles[i].name}</option>`;
            }
        } else if (rolesSelectEdit !== null) {
            rolesSelect = rolesSelectEdit;
            userRolesHTML +=
                `<option value="ROLE_USER">USER</option>
                 <option value="ROLE_ADMIN">ADMIN</option>`
        }

        rolesSelect.innerHTML = userRolesHTML;

    })
}

async function sendDataEditUser(user, userId) {
    await fetch(`/api/admin/${userId}`,
        {method: "PUT", headers: {'Content-type': 'application/json'}, body: JSON.stringify(user)})
}

const modalEdit = document.getElementById("editModal");

async function EditModalHandler() {
    await fillModal(modalEdit);
}

modalEdit.addEventListener("submit", async function (event) {
    event.preventDefault();

    const userRoleCheckbox = document.getElementById("editUserRoleCheckbox");
    const adminRoleCheckbox = document.getElementById("editAdminRoleCheckbox");

    const rolesSelected = document.getElementById("rolesEdit");
    let roles = [];
    for (let option of rolesSelected.selectedOptions) {
        if (option.value === ROLE_ADMIN.name) {
            roles.push(ROLE_ADMIN);
        } else if (option.value === ROLE_USER.name) {
            roles.push(ROLE_USER);
        }
    }

    let user = {
        id: document.getElementById("idEdit").value,
        username: document.getElementById("usernameEdit").value,
        name: document.getElementById("nameEdit").value,
        lastName: document.getElementById("lastNameEdit").value,
        age: document.getElementById("ageEdit").value,
        password: document.getElementById("passwordEdit").value,
        roles: roles
    }

    const userId = user.id;

    await sendDataEditUser(user, userId);
    await fillTableOfAllUsers();

    const modalBootstrap = bootstrap.Modal.getInstance(modalEdit);
    modalBootstrap.hide();
})

//------------<<DELETE USER>>------------//

async function deleteUserData(userId){
    await fetch(`/api/admin/${userId}`, {method: 'DELETE'});
}

const modalDelete = document.getElementById("deleteModal");

async function DeleteModalHandler() {
    await fillModal(modalDelete);
}

const formDelete = document.getElementById("modalBodyDelete");
formDelete.addEventListener("submit", async function(event) {
        event.preventDefault();

        const userId = event.target.querySelector("#idDelete").value;
        await deleteUserData(userId);
        await fillTableOfAllUsers();

        const modalBootstrap = bootstrap.Modal.getInstance(modalDelete);
        modalBootstrap.hide();
    }
)