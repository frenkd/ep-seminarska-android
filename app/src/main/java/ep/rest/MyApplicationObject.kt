package ep.rest

import android.app.Application

class MyApplicationObject : Application() {
    var sessionToken: String? = null
    var name: String? = null
    var surname: String? = null
    var id: Int? = null
    var email: String? = null
}