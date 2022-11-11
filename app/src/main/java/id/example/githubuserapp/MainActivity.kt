package id.example.githubuserapp

import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val STATE_SEARCH_VIEW = "state_search_view"
    }

    private lateinit var dataAvatar: TypedArray
    private lateinit var dataName: Array<String>
    private lateinit var dataUsername: Array<String>
    private lateinit var dataCompany: Array<String>
    private lateinit var dataLocation: Array<String>
    private lateinit var dataRepository: Array<String>
    private lateinit var dataFollowers: Array<String>
    private lateinit var dataFollowing: Array<String>
    private lateinit var searchQuery: String
    private lateinit var searchView: SearchView
    private var users: ArrayList<User> = arrayListOf()
    private var userAdapter = UserAdapter(users)
    private lateinit var rv_user : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.github, null)
        val bitmap = drawable?.toBitmap()
        val scaledDrawable = BitmapDrawable(resources,
            bitmap?.let { Bitmap.createScaledBitmap(it, 60, 60, true) })
        supportActionBar?.setHomeAsUpIndicator(scaledDrawable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_user = findViewById(R.id.rv_user)
        rv_user.setHasFixedSize(true)
        addItem()
        showRecyclerView()
        if (savedInstanceState != null) {
            val searchResults = savedInstanceState.getString(STATE_SEARCH_VIEW) as String
            searchQuery = searchResults
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchQuery = searchView.query.toString()
        outState.putString(STATE_SEARCH_VIEW, searchQuery)
    }

    private fun showRecyclerView() {
        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.adapter = userAdapter
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: User) = showSelectedUser(data)
            override fun onItemShared(data: User) = sharedUser(data)
        })
    }

    private fun sharedUser(data: User) {
        val shareUser = "Github User:\n" +
                "Name: ${data.name}\n" +
                "Username: ${data.username}\n" +
                "Company: ${data.company}\n" +
                "Location: ${data.location}\n" +
                "Repositories: ${data.repository}\n" +
                "Followers: ${data.followers}\n" +
                "Following: ${data.following}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUser)
        shareIntent.type = "text/html"
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

    private fun showSelectedUser(data: User) {
        val moveObjectWithIntent = Intent(this@MainActivity, UserDetailActivity::class.java)
        moveObjectWithIntent.putExtra(UserDetailActivity.EXTRA_USER, data)
        startActivity(moveObjectWithIntent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun prepare() {
        dataAvatar = resources.obtainTypedArray(R.array.avatar)
        dataName = resources.getStringArray(R.array.name)
        dataUsername = resources.getStringArray(R.array.username)
        dataCompany = resources.getStringArray(R.array.company)
        dataLocation = resources.getStringArray(R.array.location)
        dataRepository = resources.getStringArray(R.array.repository)
        dataFollowers = resources.getStringArray(R.array.followers)
        dataFollowing = resources.getStringArray(R.array.following)
    }

    private fun addItem(): ArrayList<User> {
        prepare()
        for (position in dataName.indices) {
            val user = User(
                dataAvatar.getResourceId(position, -1),
                dataName[position],
                dataUsername[position],
                dataCompany[position],
                dataLocation[position],
                dataRepository[position],
                dataFollowers[position],
                dataFollowing[position]
            )
            users.add(user)
        }
        dataAvatar.recycle()
        return users
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem : MenuItem? = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search User or Username"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Peringatan!")
            .setMessage("Apakah Kamu Ingin Keluar Dari Aplikasi Ini?")
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                super.onBackPressed()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

            }
            .show()
    }



    }