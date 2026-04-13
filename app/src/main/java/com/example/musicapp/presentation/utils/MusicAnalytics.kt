package com.example.musicapp.presentation.utils

import android.util.Log
import com.example.musicapp.domain.models.AnalyticsReport
import com.example.musicapp.presentation.viewmodels.AnalyticsViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class MusicAnalytics(private val viewModel: AnalyticsViewModel) {
    private val threadPool = Executors.newFixedThreadPool(3)
    private var currentThreadTask: Future<*>? = null
    private val TAG1 = "MusicAnalytics"
    private val TAG2 = "Thread1"
    private val TAG3 = "Thread2"
    private val TAG4 = "Thread3"
    private val TAG5 = "Coroutine1"
    private val TAG6 = "Coroutine2"
    private val TAG7 = "Coroutine3"

    @Volatile
    private var crashRequested = false
    private var crashType = 0
    private var isThreadAnalysisActive = false
    private var isCoroutineAnalysisActive = false
    private var threadOnError: ((String) -> Unit)? = null
    private var coroutineOnError: ((String) -> Unit)? = null

    fun analyzeWithThreads(
        onProgress: (String) -> Unit,
        onResult: (AnalyticsReport) -> Unit,
        onError: (String) -> Unit
    ) {
        crashRequested = false
        crashType = 0
        isThreadAnalysisActive = true
        threadOnError = onError

        fun onProgressUpdate(message: String) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onProgress(message)
            }
        }

        Log.d(TAG1, "=== ЗАПУСК АНАЛИЗА В ПОТОКАХ ===")

        currentThreadTask = threadPool.submit {
            val threadName = Thread.currentThread().name
            Log.d(TAG1, "Задача выполняется в потоке: $threadName")

            try {
                Log.d(TAG2, "Работаю в $threadName")
                onProgressUpdate("Поток 1 - собираю статистику")
                val startTime = System.currentTimeMillis()

                checkThreadCrash()

                Log.d(TAG2, "Запрос данных из БД")

                val allSongs = runBlocking(Dispatchers.IO) {
                    viewModel.songs.value ?: emptyList()
                }
                val allArtists = runBlocking(Dispatchers.IO) {
                    viewModel.artists.value ?: emptyList()
                }

                Log.d(TAG2, "Получил ${allSongs.size} песен и ${allArtists.size} артистов")

                checkThreadCrash()

                Log.d(TAG2, "Вычисления")
                Thread.sleep(1500)

                val totalSongs = allSongs.size
                val totalArtists = allArtists.size

                if (Thread.currentThread().isInterrupted) {
                    Log.w(TAG2, "Задача прервана пользователем")
                    onProgressUpdate("Задача прервана")
                    return@submit
                }
                Log.d(TAG2, "Поток 1 завершен за ${System.currentTimeMillis() - startTime}")

                onProgressUpdate("Поток 2 - анализирую жанры")
                Log.d(TAG3, "Работаю в $threadName")
                val genreStartTime = System.currentTimeMillis()

                checkThreadCrash()

                val genreStats = allSongs.groupBy { it.genre }
                    .mapValues { (_, songs) -> songs.size }

                Log.d(TAG3, "Вычисляю")
                Thread.sleep(2000)

                if (Thread.currentThread().isInterrupted) {
                    Log.w(TAG3, "Задача прервана пользователем")
                    onProgressUpdate("Задача прервана")
                    return@submit
                }
                Log.d(TAG3, "Поток 2 завершен за ${System.currentTimeMillis() - genreStartTime}")

                onProgressUpdate("Поток 3 - формирую отчет")
                Log.d(TAG4, "Работаю в $threadName")
                val reportStartTime = System.currentTimeMillis()

                checkThreadCrash()

                val report = AnalyticsReport(
                    totalSongs = totalSongs,
                    totalArtists = totalArtists,
                    genreDistribution = genreStats,
                    mostPopularGenre = genreStats.maxByOrNull { it.value }?.key ?: "Нет",
                    timestamp = System.currentTimeMillis()
                )

                Log.d(TAG4, "Вычисляю")
                Thread.sleep(2000)

                if (Thread.currentThread().isInterrupted) {
                    Log.w(TAG4, "Задача прервана пользователем")
                    onProgressUpdate("Задача прервана")
                    return@submit
                }

                Log.d(TAG4, "Отправка результата в главный поток")
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    if (isThreadAnalysisActive) {
                        onResult(report)
                        isThreadAnalysisActive = false
                    }
                    Log.d(TAG4, "Отправили успешно")
                }

                Log.d(TAG4, "Поток 3 завершен за ${System.currentTimeMillis() - reportStartTime}")
                val totalTime = System.currentTimeMillis() - startTime
                Log.d(TAG1, "Все потоки завершены за ${totalTime}мс")

            } catch (e: CancellationException) {
                Log.e(TAG1, "Поток отменен: ${e.message}")
                handleThreadError("Анализ отменён: ${e.message}")
            } catch (e: NullPointerException) {
                Log.e(TAG1, "NullPointerException: ${e.message}")
                e.printStackTrace()
                handleThreadError("Ошибка: ${e.message}")
            } catch (e: IOException) {
                Log.e(TAG1, "IOException: ${e.message}")
                e.printStackTrace()
                handleThreadError("Ошибка: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG1, "Ошибка в потоке: ${e.message}")
                e.printStackTrace()
                handleThreadError("Ошибка: ${e.message}")
            }
        }
    }

    private fun handleThreadError(message: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            threadOnError?.invoke(message)
            isThreadAnalysisActive = false
        }
    }

    fun analyzeWithCoroutines(
        onProgress: (String) -> Unit,
        onResult: (AnalyticsReport) -> Unit,
        onError: (String) -> Unit
    ) {
        crashRequested = false
        crashType = 0
        isCoroutineAnalysisActive = true
        coroutineOnError = onError

        Log.d(TAG1, "=== ЗАПУСК АНАЛИЗА В КОРУТИНАХ ===")

        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            try {
                withContext(Dispatchers.Main) { onProgress("Корутина 1 - сбор статистики") }
                Log.d(TAG5, "Начало работы в ${Thread.currentThread().name}")

                checkCoroutineCrash()

                val allSongs = withContext(Dispatchers.IO) {
                    viewModel.songs.value ?: emptyList()
                }

                checkCoroutineCrash()

                val allArtists = withContext(Dispatchers.IO) {
                    viewModel.artists.value ?: emptyList()
                }

                checkCoroutineCrash()

                delay(1500)
                val totalSongs = allSongs.size
                val totalArtists = allArtists.size
                withContext(Dispatchers.Main) { onProgress("Корутина 1 завершена: $totalSongs песен") }
                Log.d(TAG5, "Корутина 1 завершена")

                withContext(Dispatchers.Main) { onProgress("Корутина 2 - анализирую жанры") }
                Log.d(TAG6, "Начало работы в ${Thread.currentThread().name}")

                checkCoroutineCrash()

                val genreStats = withContext(Dispatchers.Default) {
                    allSongs.groupBy { it.genre }
                        .mapValues { (_, songs) -> songs.size }
                }
                delay(2000)

                checkCoroutineCrash()

                withContext(Dispatchers.Main) { onProgress("Корутина 2 завершена: найдено ${genreStats.size} жанров") }
                Log.d(TAG6, "Корутина 2 завершена")

                withContext(Dispatchers.Main) { onProgress("Корутина 3 - формирует отчёт") }
                Log.d(TAG7, "Начало работы в ${Thread.currentThread().name}")

                checkCoroutineCrash()

                val mostPopular = genreStats.maxByOrNull { it.value }?.key ?: "Нет"
                val report = AnalyticsReport(
                    totalSongs = totalSongs,
                    totalArtists = totalArtists,
                    genreDistribution = genreStats,
                    mostPopularGenre = mostPopular,
                    timestamp = System.currentTimeMillis()
                )
                delay(1500)

                checkCoroutineCrash()

                withContext(Dispatchers.Main) {
                    if (isCoroutineAnalysisActive) {
                        onResult(report)
                        isCoroutineAnalysisActive = false
                    }
                    Log.d(TAG1, "Результат доставлен в UI")
                }
                Log.d(TAG7, "Корутина 3 завершена")

            } catch (e: CancellationException) {
                Log.e(TAG1, "Корутина отменена: ${e.message}")
                withContext(Dispatchers.Main) {
                    coroutineOnError?.invoke("Анализ отменён: ${e.message}")
                    isCoroutineAnalysisActive = false
                }
            } catch (e: NullPointerException) {
                Log.e(TAG1, "NullPointerException: ${e.message}")
                withContext(Dispatchers.Main) {
                    coroutineOnError?.invoke("Ошибка: ${e.message}")
                    isCoroutineAnalysisActive = false
                }
            } catch (e: IOException) {
                Log.e(TAG1, "IOException: ${e.message}")
                withContext(Dispatchers.Main) {
                    coroutineOnError?.invoke("Ошибка: ${e.message}")
                    isCoroutineAnalysisActive = false
                }
            } catch (e: Exception) {
                Log.e(TAG1, "Ошибка в корутине: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    coroutineOnError?.invoke("Ошибка: ${e.message}")
                    isCoroutineAnalysisActive = false
                }
            }
        }
    }

    private suspend fun checkCoroutineCrash() {
        if (crashRequested) {
            crashRequested = false
            val (type, message) = when (crashType) {
                1 -> Pair(1, "NullPointerException: Данные не получены из БД")
                2 -> Pair(2, "IOException: Ошибка соединения с базой данных")
                else -> Pair(3, "RuntimeException: Некорректное состояние приложения")
            }
            Log.e(TAG1, "Аварийная остановка: $message")
            throw when (type) {
                1 -> NullPointerException(message)
                2 -> IOException(message)
                else -> RuntimeException(message)
            }
        }
        yield()
    }

    private fun checkThreadCrash() {
        if (crashRequested) {
            crashRequested = false
            val (type, message) = when (crashType) {
                1 -> Pair(1, "NullPointerException: Данные не получены из БД")
                2 -> Pair(2, "IOException: Ошибка соединения с базой данных")
                else -> Pair(3, "RuntimeException: Некорректное состояние приложения")
            }
            Log.e(TAG1, "Аварийная остановка: $message")
            throw when (type) {
                1 -> NullPointerException(message)
                2 -> IOException(message)
                else -> RuntimeException(message)
            }
        }
    }

    private fun getRandomCrashMessage(): Pair<Int, String> {
        val random = (1..3).random()
        return when (random) {
            1 -> Pair(1, "NullPointerException: Данные не получены из БД")
            2 -> Pair(2, "IOException: Ошибка соединения с базой данных")
            else -> Pair(3, "RuntimeException: Некорректное состояние приложения")
        }
    }

    fun triggerCrash() {
        val (type, message) = getRandomCrashMessage()
        crashType = type
        crashRequested = true
        Log.d(TAG1, "Получен сигнал аварийной остановки: $message")

        if (isThreadAnalysisActive) {
            currentThreadTask?.cancel(true)
        }
    }

    fun cancelThreadTask() {
        Log.d(TAG1, "Штатная отмена задачи в потоке")
        isThreadAnalysisActive = false
    }

    fun cancelCoroutineTask() {
        Log.d(TAG1, "Штатная отмена задачи в корутине")
        isCoroutineAnalysisActive = false
    }

    fun cleanup() {
        Log.d(TAG1, "Очистка ресурсов")
        currentThreadTask?.cancel(true)
        currentThreadTask = null
        threadPool.shutdown()
        try {
            threadPool.awaitTermination(1, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            threadPool.shutdownNow()
        }

        isThreadAnalysisActive = false
        isCoroutineAnalysisActive = false
        crashRequested = false
        crashType = 0
    }
}