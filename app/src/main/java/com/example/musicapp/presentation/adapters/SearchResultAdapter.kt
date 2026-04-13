package com.example.musicapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.musicapp.R
import com.example.musicapp.domain.models.SearchResult
import com.example.musicapp.domain.models.Track  // ← Track из Domain

class SearchResultAdapter(
    private var items: List<SearchResult> = emptyList(),
    private val onItemClick: (Track) -> Unit,
    private val onAddClick: (Track) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = view.findViewById(R.id.imageCover)
        val trackTitle: TextView = view.findViewById(R.id.textTrackTitle)
        val artistName: TextView = view.findViewById(R.id.textArtistName)
        val albumName: TextView = view.findViewById(R.id.textAlbumName)
        val duration: TextView = view.findViewById(R.id.textDuration)
        val addButton: ImageButton = view.findViewById(R.id.buttonAdd)
        val inCollectionBadge: ImageButton = view.findViewById(R.id.badgeInCollection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val track = item.track

        holder.trackTitle.text = track.title
        holder.artistName.text = track.artistName
        holder.albumName.text = track.albumName ?: "Неизвестный альбом"
        holder.duration.text = track.getDurationFormatted()

        holder.coverImage.load(track.artworkUrl) {
            placeholder(R.drawable.placeholder_album)
            error(R.drawable.error_album)
            transformations(RoundedCornersTransformation(8f))
            crossfade(true)
        }

        if (item.isAlreadyInCollection) {
            holder.addButton.visibility = View.GONE
            holder.inCollectionBadge.visibility = View.VISIBLE
            holder.inCollectionBadge.isEnabled = false
        } else {
            holder.addButton.visibility = View.VISIBLE
            holder.inCollectionBadge.visibility = View.GONE
            holder.addButton.setOnClickListener {
                onAddClick(track)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(track)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<SearchResult>) {
        items = newItems
        notifyDataSetChanged()
    }
}