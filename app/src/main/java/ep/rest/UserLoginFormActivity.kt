package ep.rest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_user_login_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserLoginFormActivity : AppCompatActivity() {
    private var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login_form)

        btnLogin.setOnClickListener {
            val password = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()
            Log.i("Enroll", "login button pressed!")
            SneakersService.instance.login(email, password).enqueue(OnLoginCallbacks(this))
        }
    }

    companion object {
        private val TAG = UserLoginFormActivity::class.java.canonicalName
    }

    private class OnLoginCallbacks(val activity: UserLoginFormActivity) : Callback<User> {
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
                Toast.makeText(activity, "Logged in sucessfuly! Welcome ${app.name}!", Toast.LENGTH_SHORT).show()
            } else {
                val errorMessage = try {
                    "An error occurred: ${response.errorBody()?.string()}"
                } catch (e: IOException) {
                    "An error occurred: error while decoding the error message."
                }
                Log.e(TAG, errorMessage)
                if (response.code() == 401) {
                    Toast.makeText(activity, "Wrong password", Toast.LENGTH_SHORT).show()
                }
                if (response.code() == 404) {
                    Toast.makeText(activity, "No such user", Toast.LENGTH_SHORT).show()
                }

            }
        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }

}