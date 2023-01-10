$(async function () {
    await getUserName();
})

const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    findUser: async () => await fetch('api/user')
}


async function getUserName() {
    let userEmail = $('#userEmail');
    userEmail.empty()
    let userRole = $('#userRole')
    userRole.empty()
    let userTable = $('#userTable tbody')
// Заполнение верхней панели и информации о юзере
    await userFetchService.findUser()
        .then(res => res.json())
        .then(user => {
            userEmail.append(user.email)
            // userRole.append(user.role)
            user.roles.forEach(role => {
                userRole.append(role.role.substring(5))
            })
            let tableFill = `$(
            <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>     
                            <td>${user.email}</td>     
                            
           
            )`
            user.roles.forEach(role=>{
                tableFill += `$(<td>${role.role.substring(5)}</td>)`
            })
            tableFill+= `</tr>`
            userTable.append(tableFill)

        })
}

// <span style="font-weight: bold" th:each="roles : ${user.getRoles()}"
//       th:text="${roles.getRole().substring(5)}"/>