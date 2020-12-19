package ep.rest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), Callback<List<Product>> {
    private val tag = this::class.java.canonicalName

    private lateinit var adapter: SneakersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = SneakersAdapter(this)
        items.adapter = adapter
        items.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val book = adapter.getItem(i)
            if (book != null) {
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("ep.rest.id", book.id)
                startActivity(intent)
            }
        }

        container.setOnRefreshListener { SneakersService.instance.getAll().enqueue(this) }

        btnLogin.setOnClickListener {
            val intent = Intent(this, UserLoginFormActivity::class.java)
            startActivity(intent)
        }

        fabEditUser.setOnClickListener {
            val intent = Intent(this, UserEditFormActivity2::class.java)
            startActivity(intent)
        }

        fabLogout.setOnClickListener {
            val app = application as MyApplicationObject
            SneakersService.instance.logout(app.sessionToken.toString())
            app.sessionToken = null
            app.id = null
            app.email = null
            app.name = null
            app.surname = null
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        SneakersService.instance.getAll().enqueue(this)
    }

    override fun onResume() {
        super.onResume()
        val app = application as MyApplicationObject

        // if logged in, show edit profile button
        if (app.sessionToken == "" || app.sessionToken == null) {
            btnLogin.visibility = View.VISIBLE
            fabEditUser.visibility = View.GONE
            fabLogout.visibility = View.GONE
        }
        else {
            btnLogin.visibility = View.GONE
            fabLogout.visibility = View.VISIBLE
            fabEditUser.visibility = View.VISIBLE
        }

        Log.i("MainActivity", "Got result: ${app.sessionToken}")
    }

    override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
        if (response.isSuccessful) {
            val hits = response.body() ?: emptyList()
            Log.i(tag, "Got ${hits.size} hits")
            adapter.clear()
            adapter.addAll(hits)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(tag, errorMessage)
        }
        container.isRefreshing = false
    }

    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
        Log.w(tag, "Error: ${t.message}", t)
        container.isRefreshing = false
    }
}
