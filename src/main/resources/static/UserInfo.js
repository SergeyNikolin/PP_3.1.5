document.addEventListener('DOMContentLoaded', async function () {
    try {
        await showUsernameOnNavbar();
        await fillTableAboutUser();
    } catch (error) {
        console.error('Error:', error);
    }
});

async function dataAboutCurrentUser() {
    try {
        const response = await fetch("/api/user");
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching user data:', error);
        return null;
    }
}

async function fillTableAboutUser() {
    try {
        const currentUserTable1 = document.getElementById("currentUserTable");
        const currentUser = await dataAboutCurrentUser();

        if (!currentUser) {
            throw new Error('No user data received');
        }

        let currentUserTableHTML = "";
        currentUserTableHTML +=
            `<tr>
                <td>${currentUser.id}</td>
                <td>${currentUser.username}</td>
                <td>${currentUser.name}</td>
                <td>${currentUser.lastName}</td>
                <td>${currentUser.age}</td>
                <td>${currentUser.roles.map(role => role.name).join(' ')}</td>
            </tr>`;
        currentUserTable1.innerHTML = currentUserTableHTML;
    } catch (error) {
        console.error('Error filling table:', error);
    }
}

async function showUsernameOnNavbar() {
    try {
        const currentUserNavbar = document.getElementById("currentUsernameNavbar");
        const currentUser = await dataAboutCurrentUser();

        if (!currentUser) {
            throw new Error('No user data received');
        }

        currentUserNavbar.innerHTML =
            `<strong>${currentUser.username}</strong>
                 with roles: 
                 ${currentUser.roles.map(role => role.name).join(' ')}`;
    } catch (error) {
        console.error('Error showing user email on navbar:', error);
    }
}