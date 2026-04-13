package com.example.musicapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.domain.models.Song

class SongAdapter(
    private var songs: List<Song>,
    private val onItemLongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val artist: TextView = view.findViewById(R.id.textArtist)
        val duration: TextView = view.findViewById(R.id.textDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.title.text = song.title
        holder.artist.text = song.artistName
        holder.duration.text = "${song.duration} • ${song.genre}"

        holder.itemView.setOnLongClickListener {
            onItemLongClick(song)
            true
        }
    }

    override fun getItemCount() = songs.size

    fun getSongAtPosition(position: Int): Song? {
        return if (position in songs.indices) songs[position] else null
    }

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}
