package com.example.submissionakhirstoryapp.data

import com.example.submissionakhirstoryapp.data.response.ListStoryItem

object Dummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "author + $i",
                "story $i",
            )
            items.add(story)
        }
        return items
    }
}