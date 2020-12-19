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

class BookDetailActivity : AppCompatActivity() {
    private var book: Book = Book()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        setSupportActionBar(toolbar)

        fabEdit.setOnClickListener {
            val intent = Intent(this, BookFormActivity::class.java)
            intent.putExtra("ep.rest.book", book)
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
            BookService.instance.get(id).enqueue(OnLoadCallbacks(this))
        }
    }

    private fun deleteBook() {
        val id = intent.getIntExtra("ep.rest.id", 0)
        if (id > 0) {
            BookService.instance.delete(id).enqueue(OnDeleteCallbacks(this))
        }
    }

    private class OnDeleteCallbacks(val activity: BookDetailActivity) : Callback<Void> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.i(tag, "Uspesno pobrisana knjiga")
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }

    private class OnLoadCallbacks(val activity: BookDetailActivity) : Callback<Book> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Book>, response: Response<Book>) {
            activity.book = response.body() ?: Book()

            Log.i(tag, "Got result: ${activity.book}")

            if (response.isSuccessful) {
                activity.tvBookDetail.text = activity.book.description
                activity.toolbarLayout.title = activity.book.title
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

        override fun onFailure(call: Call<Book>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }
}

