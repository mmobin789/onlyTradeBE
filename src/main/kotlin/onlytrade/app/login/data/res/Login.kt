package onlytrade.app.login.data.res

import io.ktor.resources.Resource

@Resource("/login")
class Login {
    @Resource("{mobile}")
    class Mobile(val parent: Login = Login(), val mobileNo: String)
}