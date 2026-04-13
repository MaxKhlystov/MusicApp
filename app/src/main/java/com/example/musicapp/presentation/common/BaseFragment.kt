package com.example.musicapp.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes private val layoutResId: Int) : Fragment() {

    private var _rootView: View? = null
    protected val rootView: View
        get() = _rootView ?: throw IllegalStateException("Root view not initialized")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _rootView = inflater.inflate(layoutResId, container, false)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _rootView = null
    }

    protected fun <T : View> findViewById(id: Int): T {
        return rootView.findViewById(id)
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}