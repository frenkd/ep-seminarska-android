package ep.rest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.content_book_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProductDetailActivity : AppCompatActivity() {
    private var product: Product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        setSupportActionBar(toolbar)

        fabEdit.setOnClickListener {
            val intent = Intent(this, UserLoginFormActivity::class.java)
            intent.putExtra("ep.rest.book", product)
            startActivity(intent)
        }

        fabDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirm deletion")
            dialog.setMessage("Are you sure?")
            dialog.setPositiveButton("Yes") { _, _ -> deleteBook() }
            dialog.setNegativeButton("Cancel", null)
            dialog.create().show()
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("ep.rest.id", 0)

        if (id > 0) {
            SneakersService.instance.get(id).enqueue(OnLoadCallbacks(this))
        }
    }

    private fun deleteBook() {
        val id = intent.getIntExtra("ep.rest.id", 0)
        if (id > 0) {
            SneakersService.instance.delete(id).enqueue(OnDeleteCallbacks(this))
        }
    }

    private class OnDeleteCallbacks(val activity: ProductDetailActivity) : Callback<Void> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.i(tag, "Uspesno pobrisana knjiga")
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }

    private class OnLoadCallbacks(val activity: ProductDetailActivity) : Callback<Product> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Product>, response: Response<Product>) {
            activity.product = response.body() ?: Product()

            Log.i(tag, "Got result: ${activity.product}")

            if (response.isSuccessful) {
                activity.tvBookDetail.text = activity.product.description
                activity.toolbarLayout.title = activity.product.title
            } else {
                val errorMessage = try {
                    "An error occurred: ${response.errorBody()?.string()}"
                } catch (e: IOException) {
                    "An error occurred: error while decoding the error message."
                }

                Log.e(tag, errorMessage)
                activity.tvBookDetail.text = errorMessage
            }
        }

        override fun onFailure(call: Call<Product>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }
}

