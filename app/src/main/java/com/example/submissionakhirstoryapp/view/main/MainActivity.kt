package com.example.submissionakhirstoryapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionakhirstoryapp.R
import com.example.submissionakhirstoryapp.data.adapter.LoadingAdapter
import com.example.submissionakhirstoryapp.data.adapter.StoriesAdapter
import com.example.submissionakhirstoryapp.databinding.ActivityMainBinding
import com.example.submissionakhirstoryapp.view.activities.MapsActivity
import com.example.submissionakhirstoryapp.view.activities.UploadStoriesActivity
import com.example.submissionakhirstoryapp.view.activities.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        showLoading(true)
        viewModel.getSession().observe(this) { user ->
            val token = user.token
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            getData(token)
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadStoriesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getData(token: String) {
        val adapter = StoriesAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter {
                adapter.retry()
            }
        )
        viewModel.getStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
            showLoading(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                return true
            }
            R.id.maps -> {
                // Menangani klik menu Maps, membuka MapsActivity
                Intent(this, MapsActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Fungsi untuk menampilkan atau menyembunyikan progress bar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}