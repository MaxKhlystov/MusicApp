package com.example.musicapp.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentActivity
import com.example.musicapp.R
import com.example.musicapp.domain.models.Song
import com.example.musicapp.presentation.viewmodels.SongsListViewModel
import kotlinx.coroutines.launch

class EditSongDialog(
    private val activity: FragmentActivity,
    private val viewModel: SongsListViewModel,
    private val song: Song,
    private val onSaved: () -> Unit
) {

    fun show() {
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_edit_song, null)

        val editTitle = view.findViewById<EditText>(R.id.editTitle)
        val editDuration = view.findViewById<EditText>(R.id.editDuration)
        val radioGroupGenre = view.findViewById<RadioGroup>(R.id.radioGroupEditGenre)

        editTitle.setText(song.title)
        editDuration.setText(song.duration)

        when (song.genre) {
            "Рок" -> radioGroupGenre.check(R.id.radioButtonEditRock)
            "Джаз" -> radioGroupGenre.check(R.id.radioButtonEditJazz)
            "Рэп" -> radioGroupGenre.check(R.id.radioButtonEditRap)
            else -> radioGroupGenre.check(R.id.radioButtonEditPop)
        }

        AlertDialog.Builder(activity)
            .setTitle("Редактирование песни")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = editTitle.text.toString().trim()
                val duration = editDuration.text.toString().trim()

                if (title.isEmpty() || duration.isEmpty()) {
                    Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!duration.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
                    Toast.makeText(activity, "Неверный формат длительности", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val genre = when (radioGroupGenre.checkedRadioButtonId) {
                    R.id.radioButtonEditRock -> "Рок"
                    R.id.radioButtonEditJazz -> "Джаз"
                    R.id.radioButtonEditRap -> "Рэп"
                    else -> "Поп"
                }

                val updatedSong = song.copy(
                    title = title,
                    duration = duration,
                    genre = genre
                )

                activity.lifecycleScope.launch {
                    viewModel.updateSong(updatedSong)
                    Toast.makeText(activity, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                    onSaved()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}