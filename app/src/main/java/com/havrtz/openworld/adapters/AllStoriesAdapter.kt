package com.havrtz.openworld.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.havrtz.openworld.R
import com.havrtz.openworld.fragments.ShareBottomSheetFragment
import com.havrtz.openworld.models.Story
import com.havrtz.openworld.viewmodels.StoryViewModel
import dagger.hilt.android.internal.managers.ViewComponentManager
import java.util.*


class AllStoriesAdapter(private val context: Context, val storyViewModel: StoryViewModel) : PagedListAdapter<Story, AllStoriesAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var stories: MutableList<Story> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_story_row, parent, false)
        return ViewHolder(view, storyViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val story = getItem(position)
        if (story != null) {
            holder.myStoryTitle.text = story.title
            Glide.with(context).load(BitmapFactory.decodeFile(story.image_location)).placeholder(R.drawable.image_unavailable).into(holder.myStoryImage)
        }
    }

    fun setStories(stories: MutableList<Story>) {
        this.stories = stories
        notifyDataSetChanged()
    }

    fun getStoryPosition(position: Int): Story {
        return stories.get(position)
    }

    inner class ViewHolder(itemView: View, storyViewModel: StoryViewModel) : RecyclerView.ViewHolder(itemView) {
        var delete: ImageButton = itemView.findViewById(R.id.mystorydelete)
        var myStoryImage: ImageView = itemView.findViewById(R.id.mystoryimage)
        var myStoryTitle: TextView = itemView.findViewById(R.id.mystorytitle)
        var share: ImageButton = itemView.findViewById(R.id.mystoryshare)

        init {
            /** Update feature removed temporarily  */

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UserTemplateItem items = listItems.get(getAdapterPosition());
                    Intent intent = new Intent(context, UpsertPageActivity.class);
                    intent.putExtra("user_template_id", items.getUser_template_id());
                    intent.putExtra("story_title", items.getStory_title());
                    intent.putExtra("template_id", items.getTemplate_id());

                    //context.startActivity(intent);
                }
            });*/
            /** Update feature removed temporarily  */
            /** Start shareBottomSheetFragment when share button is clicked  */
            share.setOnClickListener {
                val shareBottomSheetFragment = ShareBottomSheetFragment(getItem(adapterPosition)!!.image_location)
                shareBottomSheetFragment.show((activityContext() as AppCompatActivity).supportFragmentManager, shareBottomSheetFragment.tag)
            }
            /** Start shareBottomSheetFragment when share button is clicked  */
            /** Delete user story when delete is clicked  */
            delete.setOnClickListener {
                storyViewModel.delete(getItem(adapterPosition))
                notifyItemRemoved(adapterPosition)
            }
            /** Delete user story when delete is clicked  */
        }

        private fun activityContext(): Context? {
            val context = itemView.context
            return if (context is ViewComponentManager.FragmentContextWrapper) {
                context.baseContext
            } else context
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Story>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldStory: Story,
                                         newStory: Story) = oldStory.id == newStory.id


            override fun areContentsTheSame(oldStory: Story,
                                            newStory: Story) = oldStory == newStory
        }
    }
}