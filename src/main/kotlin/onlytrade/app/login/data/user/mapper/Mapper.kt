package onlytrade.app.login.data.user.mapper

import onlytrade.app.login.data.user.dao.UserDAO
import onlytrade.app.viewmodel.login.repository.data.db.User

fun UserDAO.toModel(): User = User(
    id = id.value.toLong(),
    name = usersName,
    password = password,
    phone = phone,
    email = email,
    verified = verified,
    loggedIn = loggedIn
)