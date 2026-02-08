package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<T : ViewModel>(
    private val pageId: String,
    private val creators: Map<String, () -> ViewModel>
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (ViewModel::class.java.isAssignableFrom(modelClass)) {
            val creator: () -> ViewModel = creators[pageId]
                ?: throw IllegalArgumentException("page id doesn't exist")

            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}