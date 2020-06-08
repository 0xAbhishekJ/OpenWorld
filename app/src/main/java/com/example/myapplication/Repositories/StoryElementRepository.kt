package com.example.myapplication.Repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.myapplication.Daos.StoryElementDao
import com.example.myapplication.Databases.CollageDatabase
import com.example.myapplication.Models.StoryElement

class StoryElementRepository(application: Application?) {
    private val storyElementDao: StoryElementDao
    private val allStoryELements: LiveData<List<StoryElement>>? = null
    fun insert(storyElement: StoryElement?) {
        InsertStoryElementAsyncTask(storyElementDao).execute(storyElement)
    }

    fun update(storyElement: StoryElement?) {
        UpdateStoryElementAsyncTask(storyElementDao).execute(storyElement)
    }

    fun delete(storyElement: StoryElement?) {
        DeleteStoryElementAsyncTask(storyElementDao).execute(storyElement)
    }

    private class InsertStoryElementAsyncTask private constructor(private val storyElementDao: StoryElementDao) : AsyncTask<StoryElement?, Void?, Void?>() {
        protected override fun doInBackground(vararg storyElements: StoryElement): Void? {
            storyElementDao.insert(storyElements[0])
            return null
        }

    }

    private class UpdateStoryElementAsyncTask private constructor(private val storyElementDao: StoryElementDao) : AsyncTask<StoryElement?, Void?, Void?>() {
        protected override fun doInBackground(vararg storyElements: StoryElement): Void? {
            storyElementDao.update(storyElements[0])
            return null
        }

    }

    private class DeleteStoryElementAsyncTask private constructor(private val storyElementDao: StoryElementDao) : AsyncTask<StoryElement?, Void?, Void?>() {
        protected override fun doInBackground(vararg storyElements: StoryElement): Void? {
            storyElementDao.delete(storyElements[0])
            return null
        }

    }

    init {
        val database: CollageDatabase = CollageDatabase.getInstance(application)
        storyElementDao = database.storyElementDao()
    }
}