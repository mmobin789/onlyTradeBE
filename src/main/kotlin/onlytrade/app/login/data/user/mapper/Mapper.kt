package onlytrade.app.login.data.user.mapper

import onlytrade.app.login.data.user.dao.UserDAO
import onlytrade.app.login.data.user.model.User

fun UserDAO.toModel(): User = User(
    id = id.value,
    name = usersName,
    phone = phone,
    email = email,
    verified = verified == "true",
    loggedIn = loggedIn == "true"
)