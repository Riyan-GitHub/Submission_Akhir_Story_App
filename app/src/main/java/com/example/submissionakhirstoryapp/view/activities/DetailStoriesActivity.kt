package com.example.submissionakhirstoryapp.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.submissionakhirstoryapp.R
import com.example.submissionakhirstoryapp.data.response.Story
import com.example.submissionakhirstoryapp.databinding.ActivityDetailStoriesBinding
import com.example.submissionakhirstoryapp.view.main.MainViewModel
import com.example.submissionakhirstoryapp.view.main.ViewModelFactory

class DetailStoriesActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityDetailStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        val storyId = intent.getStringExtra(EXTRA_ID) ?: return
        observeStoryDetail(storyId)
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun observeStoryDetail(storyId: String) {
        viewModel.getSession().observe(this) { session ->
            session.token?.let {
                showLoading(true)
                viewModel.getDetailStory(it, storyId)
            }
        }

        viewModel.detail.observe(this) { story ->
            displayStory(story)
        }
    }

    private fun displayStory(story: Story) {
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.previewImageView)

        binding.apply {
            name.text = story.name
            description.text = story.description
        }

        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
