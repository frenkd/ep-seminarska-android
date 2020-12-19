package ep.rest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_user_edit_form2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserEditFormActivity2 : AppCompatActivity() {
    private var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_form2)

        btnLogin.setOnClickListener {
            val password = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()
            Log.i("Enroll", "login button pressed!")
            SneakersService.instance.login(email, password).enqueue(OnLoginCallbacks(this))
        }
    }

    companion object {
        private val TAG = UserEditFormActivity2::class.java.canonicalName
    }

    private class OnLoginCallbacks(val activity: UserEditFormActivity2) : Callback<User> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<User>, response: Response<User>) {
            if (response.isSuccessful) {
                Log.i(TAG, "Login success!")
                activity.user = response.body() ?: User() as User

                Log.i(TAG, "Got result: {$activity.user.name}")
                val app = activity.application as MyApplicationObject
                app.sessionToken = activity.user.sessionToken
                app.id = activity.user.id
                app.email = activity.user.email
                app.name = activity.user.name
                app.surname = activity.user.surname

                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            } else {
                val errorMessage = try {
                    "An error occurred: ${response.errorBody()?.string()}"
                } catch (e: IOException) {
                    "An error occurred: error while decoding the error message."
                }
                Log.e(TAG, errorMessage)
            }
        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }

}