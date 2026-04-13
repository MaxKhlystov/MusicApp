package com.example.musicapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityMainContainerBinding
import com.example.musicapp.presentation.fragments.AnalyticsFragment
import com.example.musicapp.presentation.fragments.MainFragment
import com.example.musicapp.presentation.fragments.SearchFragment
import com.example.musicapp.presentation.fragments.SongsListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainContainerBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Музыкальное приложение"

        bottomNavigationView = binding.bottomNavigationView

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, MainFragment())
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_add -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, MainFragment())
                    }
                    true
                }
                R.id.navigation_songs -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, SongsListFragment())
                    }
                    true
                }
                R.id.navigation_search -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, SearchFragment())
                    }
                    true
                }
                R.id.navigation_analytics -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, AnalyticsFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }
}