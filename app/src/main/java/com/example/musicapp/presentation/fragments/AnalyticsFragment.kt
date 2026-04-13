package com.example.musicapp.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentAnalyticsBinding
import com.example.musicapp.presentation.common.BaseFragment
import com.example.musicapp.presentation.utils.MusicAnalytics
import com.example.musicapp.presentation.viewmodels.AnalyticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyticsFragment : BaseFragment(R.layout.fragment_analytics) {

    private val viewModel: AnalyticsViewModel by viewModels()
    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var analytics: MusicAnalytics
    private lateinit var textStatus: TextView
    private lateinit var textResult: TextView
    private lateinit var btnThreads: Button
    private lateinit var btnCoroutines: Button
    private lateinit var btnCancel: Button
    private lateinit var btnCrash: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAnalyticsBinding.bind(rootView)

        viewModel.loadSongs()
        viewModel.loadArtists()

        analytics = MusicAnalytics(viewModel)

        textStatus = binding.textStatus
        textResult = binding.textResult
        btnThreads = binding.btnStartThreads
        btnCoroutines = binding.btnStartCoroutines
        btnCancel = binding.btnCancel
        btnCrash = binding.btnCrash

        btnThreads.setOnClickListener { startThreadsAnalysis() }
        btnCoroutines.setOnClickListener { startCoroutinesAnalysis() }
        btnCancel.setOnClickListener { cancelAnalysis() }
        btnCrash.setOnClickListener { crashAnalysis() }

        btnCancel.isEnabled = false
        btnCrash.isEnabled = false
    }

    private fun startThreadsAnalysis() {
        textStatus.text = "Запуск анализа в потоках..."
        textResult.text = ""

        setButtonsEnabled(false)

        analytics.analyzeWithThreads(
            onProgress = { message ->
                requireActivity().runOnUiThread { textStatus.text = message }
            },
            onResult = { report ->
                requireActivity().runOnUiThread {
                    textStatus.text = "Анализ завершен!"
                    textResult.text = report.format()
                    setButtonsEnabled(true)
                }
            },
            onError = { errorMessage ->
                requireActivity().runOnUiThread {
                    textStatus.text = "Ошибка"
                    textResult.text = "Анализ прерван ошибкой:\n\n$errorMessage"
                    setButtonsEnabled(true)
                    showToast("Произошла ошибка")
                }
            }
        )
    }

    private fun startCoroutinesAnalysis() {
        textStatus.text = "Запуск анализа в корутинах..."
        textResult.text = ""

        setButtonsEnabled(false)

        analytics.analyzeWithCoroutines(
            onProgress = { message ->
                requireActivity().runOnUiThread { textStatus.text = message }
            },
            onResult = { report ->
                requireActivity().runOnUiThread {
                    textStatus.text = "Анализ завершен!"
                    textResult.text = report.format()
                    setButtonsEnabled(true)
                }
            },
            onError = { errorMessage ->
                requireActivity().runOnUiThread {
                    textStatus.text = "Ошибка"
                    textResult.text = "Анализ прерван ошибкой:\n\n$errorMessage"
                    setButtonsEnabled(true)
                    showToast("Произошла ошибка")
                }
            }
        )
    }

    private fun cancelAnalysis() {
        analytics.cancelThreadTask()
        analytics.cancelCoroutineTask()

        textStatus.text = "Анализ отменен"
        showToast("Задача отменена")
        setButtonsEnabled(true)
    }

    private fun crashAnalysis() {
        analytics.triggerCrash()
        btnCrash.isEnabled = false
        showToast("Аварийная остановка активирована")
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        btnThreads.isEnabled = enabled
        btnCoroutines.isEnabled = enabled
        btnCancel.isEnabled = !enabled
        btnCrash.isEnabled = !enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        analytics.cleanup()
    }
}