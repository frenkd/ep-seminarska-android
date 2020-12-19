package ep.rest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_user_login_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserLoginFormActivity : AppCompatActivity(), Callback<Void> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login_form)

        btnLogin.setOnClickListener {
            val password = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()
            Log.i("Enroll", "login button pressed!")
            SneakersService.instance.login(email, password).enqueue(this)
        }
    }

    override fun onResponse(call: Call<Void>, response: Response<Void>) {
        val headers = response.headers()

        if (response.isSuccessful) {
            Log.i(TAG, "Login success!")
            val parts = headers.get("Location")?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()

            // spremenljivka id dobi vrednost, ki jo vrne zadnji izraz v bloku
            parts?.get(parts.size - 1)?.toInt()
            // Preusmerimo na prijavljeno stran
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(TAG, errorMessage)
        }
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = UserLoginFormActivity::class.java.canonicalName
    }
}
